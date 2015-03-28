package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;

import net.simonvt.menudrawer.MenuDrawer;

import org.json.JSONException;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Notify extends ShikiSherlockActivity {


	Boolean stop_update = true;
	String ids;
	int page_no = 1, click_item = 0;

	PullToRefreshListView lv_main;

	AsyncTask<Void, Void, Void> al;
	AsyncTask<Void, Void, Void> alAdd;
	
	ArrayList<SSDK_Notify> notify = new ArrayList<SSDK_Notify>();
	ArrayList<SSDK_Notify> t_notify = new ArrayList<SSDK_Notify>();

	
	Functions.MenuAdapter adapter_menu;
	TraningAdapter adapter_list;
	
	MenuDrawer mDrawer;
	ActionBar actionbar;
	TextView tv_load, tv_unread;
  
	//Адаптер ListView
	public class TraningAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		
		public TraningAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		} 
		
	    @Override
	    public int getCount() {
	        return notify.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return notify.get(position);
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
		    		convertView = mLayoutInflater.inflate(R.layout.w_item_notification, null);
				}else{
					convertView = mLayoutInflater.inflate(R.layout.d_item_notification, null);
				}
		        

		        holder.textView = (TextView) convertView.findViewById(R.id.lbl_manga_name);
		        holder.infoView = (TextView) convertView.findViewById(R.id.lbl_manga_anime_info);
		        holder.titleView = (TextView) convertView.findViewById(R.id.lbl_time);
		        holder.dateView = (TextView) convertView.findViewById(R.id.tv_date);
		        holder.userView = (ImageView) convertView.findViewById(R.id.img_item_preview);
		        holder.newView = (TextView) convertView.findViewById(R.id.lbl_new);
		        convertView.setTag(holder);

		    } else {
	            holder = (Functions.ViewHolder) convertView.getTag();
	        }
		    
		    convertView.findViewById(R.id.img_item_preview).setOnClickListener(new OnClickListener() {
	        	
	        	@Override
	            public void onClick(View v) {
	        		holder.userView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
	        		v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    				Intent intent_user = new Intent(Activity_Notify.this, Activity_UserProfile.class);
    	    		intent_user.putExtra("id", notify.get(position).from_id);
    	    		startActivity(intent_user);
	        	}
	        });
	        
	        convertView.findViewById(R.id.lbl_new).setOnClickListener(new OnClickListener() {
	        	
	        	@Override
	            public void onClick(View v) {
	        		holder.newView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
	        		ids = notify.get(position).id;
	        		new AsyncRead().execute();
	        		notify.get(position).read = true;
	            }
	        });
		    
		    holder.textView.setText(notify.get(position).from_nickname);  
		    holder.dateView.setText(notify.get(position).created_at);  
	        holder.infoView.setText(Html.fromHtml(notify.get(position).html_body));
	        if (notify.get(position).read){
        		holder.newView.setVisibility(View.GONE);
        	}else{
        		holder.newView.setVisibility(View.VISIBLE);
        	}
	        
	        ImageLoader.getInstance().displayImage(notify.get(position).from_avatar, holder.userView);

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
			mDrawer = Functions.setMenuDrawer(actionbar, "Уведомления", R.layout.w_activity_scroll_list, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, "Уведомления", R.layout.d_activity_scroll_list, getWindowManager().getDefaultDisplay().getRotation(),
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
	    adapter_list = new TraningAdapter(Activity_Notify.this);
	    lv_main.setAdapter(adapter_list);
	    	    
	    lv_main.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    		if (notify.get(position-1).kind.equals("QuotedByUser") && notify.get(position-1).linked_type.equals("topic")){
		    		Intent intent_user = new Intent(Activity_Notify.this, Activity_Thread.class);
		    		intent_user.putExtra("id", notify.get(position-1).linked_topic_url);
		    		startActivity(intent_user);
	    		}else if (notify.get(position-1).kind.equals("FriendRequest")){
	    			click_item = position-1;
	    			showDialog(Constants.IDD_ADD_FRIEND);
	    		}
	    		if (notify.get(position-1).read == false){
	    			ids = notify.get(position-1).id;
	    			new AsyncRead().execute();
	    			notify.get(position-1).read = true;
		    	}
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
				notify.clear();
				t_notify.clear();

				page_no = 1;
				if (Functions.isNetwork(getBaseContext())) al = new AsyncLoading().execute();
			}
		});
		
		al = (AsyncLoading) getLastNonConfigurationInstance();
	    if (al == null) {
	      al = new AsyncLoading();
	    }

        
		if ((savedInstanceState != null)) {
			notify = (ArrayList<SSDK_Notify>) savedInstanceState.getSerializable("notify");
			t_notify = (ArrayList<SSDK_Notify>) savedInstanceState.getSerializable("t_notify");

			page_no = savedInstanceState.getInt("page_no");
			stop_update = savedInstanceState.getBoolean("stop_update");
			
			tv_unread.setText(String.valueOf(Functions.count_unread));
		  	adapter_list.notifyDataSetChanged();
		}else{
			if (Functions.isNetwork(getBaseContext())) { 
				al = new AsyncLoading().execute();
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case Constants.IDD_ADD_FRIEND:
			builder.setMessage("Добавить "+ notify.get(click_item).from_nickname +" в список друзей?")
				.setCancelable(false)
				.setPositiveButton("Да", new DialogInterface.OnClickListener() {
					@SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int id) {
						//Добавляем в друзья
						removeDialog(Constants.IDD_ADD_FRIEND);
						alAdd = new AsyncAddFriend(notify.get(click_item).from_id).execute();
					}
				})
				.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
					@SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int id) {
						removeDialog(Constants.IDD_ADD_FRIEND);
					}
				});
	 
			return builder.create();
		default:
			return null;
		}
	}
	
	//Сохранение значения Асинхронной задачи
	public Object onRetainNonConfigurationInstance() {

	    return al;
	}	
	
	//События нажатий на меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		int rotate = getWindowManager().getDefaultDisplay().getRotation();
  		switch (item.getItemId()) {
          	case android.R.id.home:
          		if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
        	    	finish();
        	    }else{
        	    	mDrawer.toggleMenu(true);
        	    }
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
			notify.addAll(t_notify);
			
			tv_unread.setText(String.valueOf(Functions.count_unread));

			adapter_list.notifyDataSetChanged();
			lv_main.onRefreshComplete();
			tv_load.setVisibility(View.GONE);
			setSupportProgressBarIndeterminateVisibility(false);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				t_notify = SSDK_API.getNotify(20, page_no, Functions.preference.kawai);
				SSDK_API.getUnread(Functions.preference.kawai);
				if (t_notify.size()>0)stop_update = false; else stop_update = true;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	//Асинхроннай задача на пометку прочтения
	private class AsyncRead extends AsyncTask<Void, Void, Void> {
			
		
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
						SSDK_API.readMessages("message-" + ids);
						SSDK_API.getUnread(Functions.preference.kawai);				
						return null;
					}

					
			  }
	
	private class AsyncAddFriend extends AsyncTask<Void, Void, Void> {
		private String id; 
		
		public AsyncAddFriend(String id) {
			 this.id = id;
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
			Toast.makeText(getBaseContext(), "Добавлен(а) в друзья.", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			SSDK_API.addFriend(id);
			SSDK_API.getUnread(Functions.preference.kawai);				
			return null;
		}
		
	}

	//Сохранение значений при повороте экрана
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page_no", page_no);
        outState.putBoolean("stop_update", stop_update);
        outState.putSerializable("notify", notify);
        outState.putSerializable("t_notify", t_notify);
 
    }
	
}
