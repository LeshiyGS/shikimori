package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;

import net.simonvt.menudrawer.MenuDrawer;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.Window;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_Inbox extends ShikiSherlockActivity {

	Boolean stop_update = true;
	String ids, id;
	int page_no = 1, iii = 0, rotate;
	int temp_del = 0;

	AsyncTask<Void, Void, Void> al;
	AsyncTask<Void, Void, Void> del;
	AsyncRead loader_read;
	
	ArrayList<SSDK_Inbox> mes = new ArrayList<SSDK_Inbox>();
	ArrayList<SSDK_Inbox> t_mes = new ArrayList<SSDK_Inbox>();
	
	ArrayList<String> mes_sname = new ArrayList<String>();
	ArrayList<String> t_mes_sname = new ArrayList<String>();

	Functions.MenuAdapter adapter_menu;
	TraningAdapter adapter_list;
	
	MenuDrawer mDrawer;
	TextView tv_unread, tv_load;
	ActionBar actionbar;
	ListView actualListView;
	PullToRefreshListView lv_main;
  
	//Адаптер ListView
	public class TraningAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		
		public TraningAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		} 
		
	    @Override
	    public int getCount() {
	        return mes.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return mes.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
	    	final Functions.ViewHolder holder;
		      		    
		    if (convertView == null) {
		    	holder = new Functions.ViewHolder();
		    	
		    	if (Functions.preference.theme.equals("ligth")){
		    		convertView = mLayoutInflater.inflate(R.layout.w_item_message, null);
				}else{
					convertView = mLayoutInflater.inflate(R.layout.d_item_message, null);  
				}
		        
		        
		        holder.textView = (TextView) convertView.findViewById(R.id.lbl_manga_name);
		        holder.newView = (TextView) convertView.findViewById(R.id.lbl_new);
		        holder.dateView = (TextView) convertView.findViewById(R.id.lbl_time);
		        holder.userView = (ImageView) convertView.findViewById(R.id.img_item_preview);
		        holder.clientView = (TextView) convertView.findViewById(R.id.lbl_client);
		        holder.layoutView = (LinearLayout) convertView.findViewById(R.id.ll_message_add);
		        
		        convertView.setTag(holder);
		    }else {
	            holder = (Functions.ViewHolder) convertView.getTag();
	            holder.layoutView.removeAllViews();
	        }
		    
		    convertView.findViewById(R.id.img_item_preview).setOnClickListener(new OnClickListener() {
	        	
	        	@Override
	            public void onClick(View v) {
	        		holder.userView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    				Intent intent_user = new Intent(Activity_Inbox.this, Activity_UserProfile.class);
    	    		intent_user.putExtra("id", mes.get(position).from_id);
    	    		startActivity(intent_user);

	            }
	        });
	               	
	        convertView.findViewById(R.id.lbl_new).setOnClickListener(new OnClickListener() {
	        	
	        	@Override
	            public void onClick(View v) {
	        		holder.newView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
	        		startRead(mes.get(position).id);
	        		mes.get(position).read = true;
	            }
	        });

            Functions.addComment(mes.get(position).body_clean, mes.get(position).spoiler_names, holder.layoutView, Activity_Inbox.this, Activity_Inbox.this, true);

	        holder.textView.setText(mes.get(position).from_nickname);  
	        if (mes.get(position).read){
        		holder.newView.setVisibility(View.GONE);
        	}else{
        		holder.newView.setVisibility(View.VISIBLE);
        	}
	        holder.dateView.setText(mes.get(position).created_at); 
	        
	        ImageLoader.getInstance().displayImage(mes.get(position).from_avatar, holder.userView);

	        if ((mes.get(position).body_clean.indexOf("[Отправлено с Android]") > mes.get(position).body_clean.length()-75) && 
	        		(mes.get(position).body_clean.indexOf("[Отправлено с Android]") != -1)){
	        	 holder.clientView.setVisibility(View.VISIBLE);
	        } else {
	        	 holder.clientView.setVisibility(View.GONE);
	        }
	        return convertView;
	    }
	}
	
	//Создание активити
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Чтение настроек
		Functions.getPreference(getBaseContext());

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		//Подключение и выбор варианта меню.
		int rotate = getWindowManager().getDefaultDisplay().getRotation();
		if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
			setTheme(R.style.AppTheme_ActionBarStyle_BackHome);
		}else{
			setTheme(R.style.AppTheme_ActionBarStyle);
		}
		
		actionbar = getSupportActionBar();

		super.onCreate(savedInstanceState);
		
		//Подключение и выбор варианта меню.
		
		if (Functions.preference.theme.equals("ligth")){
			mDrawer = Functions.setMenuDrawer(actionbar, "Почта", R.layout.w_activity_scroll_list, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, "Почта", R.layout.d_activity_scroll_list, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);   
		}
		
		tv_unread = (TextView) mDrawer.findViewById(R.id.tv_unread);
		tv_unread.addTextChangedListener(new TextWatcher() {
  	        @Override
  	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
  	        @Override
  	        public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void afterTextChanged(Editable arg0) {
				if (Functions.count_unread == 0) tv_unread.setVisibility(View.GONE);
					else tv_unread.setVisibility(View.VISIBLE);
			} 
  	    });
		tv_unread.setText(String.valueOf(Functions.count_unread));
		
        //Инициализация элементов
        
        tv_load = (TextView) findViewById(R.id.tv_load);
        tv_load.setVisibility(View.GONE);
        
        lv_main = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
	    adapter_list = new TraningAdapter(Activity_Inbox.this);
	    actualListView = lv_main.getRefreshableView();
        registerForContextMenu(actualListView);
        actualListView.setAdapter(adapter_list); 
	    
	    lv_main.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view,
		          int position, long id) {
	
		      }
		    });
        
        lv_main.setOnScrollListener(new OnScrollListener() {
	        public void onScrollStateChanged(AbsListView view, int scrollState) {

	        }

	        public void onScroll(AbsListView view, int firstVisibleItem,
	            int visibleItemCount, int totalItemCount) {
	        	boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

	    		if (loadMore && al.getStatus() == AsyncTask.Status.FINISHED && !stop_update) {
		        	page_no = page_no + 1;
		        	tv_load.setVisibility(View.VISIBLE);
		        	al = new AsyncLoading().execute();
	    		}
	        }
	    });

		lv_main.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				mes.clear();
				mes_sname.clear();
				t_mes.clear();
				t_mes_sname.clear();
				page_no = 1;
				iii = 0;
				if (Functions.isNetwork(getBaseContext())) al = new AsyncLoading().execute();
			}
		});
		
		al = (AsyncLoading) getLastNonConfigurationInstance();
	    if (al == null) {
	      al = new AsyncLoading();
	    }

		if ((savedInstanceState != null)) {
			mes = (ArrayList<SSDK_Inbox>) savedInstanceState.getSerializable("mes");
			mes_sname = savedInstanceState.getStringArrayList("mes_sname");
			t_mes = (ArrayList<SSDK_Inbox>) savedInstanceState.getSerializable("t_mes");
			t_mes_sname = savedInstanceState.getStringArrayList("t_mes_sname");
			page_no = savedInstanceState.getInt("page_no");
			iii = savedInstanceState.getInt("iii");
			stop_update = savedInstanceState.getBoolean("stop_update");
			tv_unread.setText(String.valueOf(Functions.count_unread));
		  	adapter_list.notifyDataSetChanged();
		}else{
			if (Functions.isNetwork(getBaseContext()))  al = new AsyncLoading().execute();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 10, 0, "")
			.setIcon(getBaseContext().getResources().getDrawable(R.drawable.ic_communication_clear_all))
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
        return true;
	}
	
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
	        case Constants.IDD_DEL_ITEM:
	           builder.setMessage("Вы действительно хотите удалить сообщение?")
	                    .setCancelable(false)
	                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int id) {
	                                	if (Functions.isNetwork(getBaseContext())) {
	                                		del = new messageDelete(mes.get(temp_del).id).execute();
	                                		mes.remove(temp_del);
	                                		adapter_list.notifyDataSetChanged();
	                                	}
	                                }
	                            })
	                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
	                                @SuppressWarnings("deprecation")
									public void onClick(DialogInterface dialog, int id) {
	                                    removeDialog(Constants.IDD_DEL_ITEM);
	                                }
	                            });
	            return builder.create();    
	         default:
	            return null;
        }
    }
	
	//
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Выберите:");
            menu.add(0, 1000, 0, "Ответить");
            menu.add(0, 1001, 0, "Цитировать");
            menu.add(0, 1002, 0, "Удалить");
            super.onCreateContextMenu(menu, v, menuInfo);
    }
	
	//
	@SuppressWarnings("deprecation")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    Intent intent_add = new Intent(Activity_Inbox.this, Activity_Add_Edit_Input.class);
	    switch (item.getItemId()) {
	    case 1000:
	    	//Личное сообщение
    		intent_add.putExtra("type", "message");
    		intent_add.putExtra("commentable_id", mes.get(info.position-1).from_id);
    		intent_add.putExtra("to_id", mes.get(info.position-1).from_id);
    		startActivity(intent_add);
	        return true;
	    case 1001:
	    	//Цитата
    		intent_add.putExtra("type", "message");
    		intent_add.putExtra("commentable_id", mes.get(info.position-1).from_id);
    		intent_add.putExtra("to_id", mes.get(info.position-1).from_id);
    		intent_add.putExtra("text","[quote="+mes.get(info.position-1).from_nickname+"]"+mes.get(info.position-1).body+"[/quote]");
    		startActivity(intent_add);
	        return true;
	    case 1002:
	    	//Dialog accept delete
	    	temp_del = info.position-1;
	    	showDialog(Constants.IDD_DEL_ITEM);
	    	return true;
	    default:
	        return super.onContextItemSelected(item);
	    }
	}

	//Сохранение значения Асинхронной задачи
	public Object onRetainNonConfigurationInstance() {

	    return al;
	}	
	
	//
  	@Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
  		switch (item.getItemId()) {
          	case android.R.id.home:
          		if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
        	    	finish();
        	    }else{
        	    	mDrawer.toggleMenu(true);
        	    }
          		break;
          	case 10:
          		new AllRead().execute();
          		break;
  			}
  		return super.onOptionsItemSelected(item);        
    }
   
  	//Обработка нажатий кнопок
  	@Override
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  	    if (keyCode == KeyEvent.KEYCODE_MENU) {
  	        event.startTracking();
      		mDrawer.toggleMenu(true);
  	        return true;
  	    }
  	    return super.onKeyDown(keyCode, event);
  	}
  	
  	//Асинхронная задача загрузки уведомлений
	private class AsyncLoading extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
			
		}
				  
		protected void onPostExecute(Void result1) {
			mes.addAll(t_mes);
			mes_sname.addAll(t_mes_sname);
			
			tv_unread.setText(String.valueOf(Functions.count_unread));
			
			setSupportProgressBarIndeterminateVisibility(false);
			adapter_list.notifyDataSetChanged();
			lv_main.onRefreshComplete();
			tv_load.setVisibility(View.GONE);
			
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				t_mes = SSDK_API.getInbox(20, page_no, Functions.preference.kawai);
				SSDK_API.getUnread(Functions.preference.kawai);
				if (t_mes.size()>0)stop_update = false; else stop_update = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			return null;
		}
	}
	
	//Асинхронная задача пометки прочтения
	private class AllRead extends AsyncTask<Void, Void, Void> {
			
			    @Override
			    protected void onPreExecute() {
			    	setSupportProgressBarIndeterminateVisibility(true);
			    }
				  
			    protected void onPostExecute(Void result1) {
			    	tv_unread.setText(String.valueOf(Functions.count_unread));
			    	setSupportProgressBarIndeterminateVisibility(false);
			    	adapter_list.notifyDataSetChanged();
			    	Functions.updateWidget(getBaseContext());
			    }

			    @Override
			    protected Void doInBackground(Void... arg0) {
			    	for (int i=0; i<mes.size();i++){
			    		if (mes.get(i).read == false){
			    			SSDK_API.readMessages(mes.get(i).id);
			    			mes.get(i).read = true;
			    		}
			    	}
			    	SSDK_API.getUnread(Functions.preference.kawai);
			    	return null;
			    }

					
			  }
	
	//Асинхронная задача пометки прочтения
	private class messageDelete extends AsyncTask<Void, Void, Void> {
		String id;
		
		public messageDelete (String id) {  
			this.id = id;
		}
		
		@Override
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
		}
					  
		protected void onPostExecute(Void result1) {
			tv_unread.setText(String.valueOf(Functions.count_unread));
			setSupportProgressBarIndeterminateVisibility(false);
			adapter_list.notifyDataSetChanged();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			SSDK_API.deleteMessages(id);
			SSDK_API.getUnread(Functions.preference.kawai);
			return null;
		}	
	}
	
	//Задача пометки прочтения
	public void startRead(String n_id) {
    	if (loader_read == null || loader_read.getStatus().equals(AsyncTask.Status.FINISHED)) {
    		loader_read = new AsyncRead(n_id);
    		loader_read.execute();
    	} else {
    		Toast.makeText(Activity_Inbox.this, "Ждите завершения загрузки", Toast.LENGTH_SHORT).show();
    	}
    }
		
	//Асинхроннай задача на пометку прочтения
	private class AsyncRead extends AsyncTask<Void, Void, Void> {
		private String n_id;


	    public AsyncRead(String n_id) {
	        this.n_id = n_id;
	    }
		@Override
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
		}
				  
		protected void onPostExecute(Void result1) {
			tv_unread.setText(String.valueOf(Functions.count_unread));
			adapter_list.notifyDataSetChanged();
			setSupportProgressBarIndeterminateVisibility(false);
			Functions.updateWidget(getBaseContext());
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			SSDK_API.readMessages(n_id);
			return null;
		}

		
	}
	
	//Сохранение значений при повороте экрана
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page_no", page_no);
        outState.putInt("iii", iii);
        outState.putBoolean("stop_update", stop_update);
        outState.putSerializable("mes", mes);
        outState.putStringArrayList("mes_sname", mes_sname);
        outState.putSerializable("t_mes", t_mes);
        outState.putStringArrayList("t_mes_sname", t_mes_sname);

    }
	
}