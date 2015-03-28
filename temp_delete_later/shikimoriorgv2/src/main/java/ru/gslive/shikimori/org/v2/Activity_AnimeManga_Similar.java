package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
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
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_AnimeManga_Similar extends ShikiSherlockActivity {
	
	MenuDrawer mDrawer;
	TextView tv_unread;
	GridView lv_main;
	TextView tv_load;
	
	Functions.MenuAdapter adapter_menu;
	ActionBar actionbar;
	
	ArrayList<SSDK_Animes> a_result = new ArrayList<SSDK_Animes>();
	ArrayList<SSDK_Mangas> m_result = new ArrayList<SSDK_Mangas>();
	
	PullToRefreshGridView mPullToRefreshGridView;
	
	aListAdapter adapter_alist;
	mListAdapter adapter_mlist;
	
	AsyncTask<Void, Void, Void> al;
	
	String id;
	String type;
	
	//Адаптер aListView
	public class aListAdapter extends BaseAdapter {
			private LayoutInflater mLayoutInflater;
			
			public aListAdapter (Context ctx) {  
				mLayoutInflater = LayoutInflater.from(ctx);  
			} 
			
		    @Override
		    public int getCount() {
		    	 
		        return a_result.size();
		    }

		    @Override
		    public Object getItem(int position) {
		        return a_result.get(position);
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
						convertView = mLayoutInflater.inflate(R.layout.w_item_history, null);
					}else{
						convertView = mLayoutInflater.inflate(R.layout.d_item_history, null);
					}
					holder.textView = (TextView) convertView.findViewById(R.id.lbl_h_name);
		    		holder.infoView = (TextView) convertView.findViewById(R.id.lbl_h_info);
					holder.dateView = (TextView) convertView.findViewById(R.id.lbl_h_date);
		    		holder.imageView = (ImageView) convertView.findViewById(R.id.img_h_image);
					convertView.setTag(holder);
				} else {
					holder = (Functions.ViewHolder) convertView.getTag();
				}
			    
			    if (a_result.get(position).russian.equals("null")){
			    	holder.textView.setText(a_result.get(position).name);  
			    }else{
			    	holder.textView.setText(a_result.get(position).name + " / " + a_result.get(position).russian);  
			    }

			    ImageLoader.getInstance().displayImage(a_result.get(position).image_original, holder.imageView);
			    holder.dateView.setVisibility(View.GONE);
			    if (a_result.get(position).episodes.equals("0")){
			    	holder.infoView.setText("Эпизодов: ?");
			    }else{
			    	holder.infoView.setText("Эпизодов: " + a_result.get(position).episodes);
			    }
		        return convertView;
		    }
		}
	
	//Адаптер mListView
	public class mListAdapter extends BaseAdapter {
				private LayoutInflater mLayoutInflater;
				
				public mListAdapter (Context ctx) {  
					mLayoutInflater = LayoutInflater.from(ctx);  
				} 
				
			    @Override
			    public int getCount() {
			    	 
			        return m_result.size();
			    }

			    @Override
			    public Object getItem(int position) {
			        return m_result.get(position);
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
							convertView = mLayoutInflater.inflate(R.layout.w_item_history, null);
						}else{
							convertView = mLayoutInflater.inflate(R.layout.d_item_history, null);
						}
						holder.textView = (TextView) convertView.findViewById(R.id.lbl_h_name);
			    		holder.infoView = (TextView) convertView.findViewById(R.id.lbl_h_info);
						holder.dateView = (TextView) convertView.findViewById(R.id.lbl_h_date);
			    		holder.imageView = (ImageView) convertView.findViewById(R.id.img_h_image);
						convertView.setTag(holder);
					} else {
						holder = (Functions.ViewHolder) convertView.getTag();
					}
				    
				    if (m_result.get(position).russian.equals("null")){
				    	holder.textView.setText(m_result.get(position).name);  
				    }else{
				    	holder.textView.setText(m_result.get(position).name + " / " + m_result.get(position).russian);  
				    }

				    ImageLoader.getInstance().displayImage(m_result.get(position).image_original, holder.imageView);
				    holder.dateView.setVisibility(View.GONE);
				    if (m_result.get(position).chapters.equals("0")){
				    	holder.infoView.setText("Глав: ?");
				    }else{
				    	holder.infoView.setText("Глав: " + m_result.get(position).chapters);
				    }
			        return convertView;
			    }
			}
	
	//
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

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

		id = getIntent().getExtras().getString("id");
		type = getIntent().getExtras().getString("type");

        String subTitle;
		if (type.equals("animes")){
            subTitle = "Похожее аниме";
        }else{
            subTitle = "Похожая манга";
        }
		
		if (Functions.preference.theme.equals("ligth")){
			mDrawer = Functions.setMenuDrawer(actionbar, subTitle, R.layout.w_activity_scroll_grid, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, subTitle, R.layout.d_activity_scroll_grid, getWindowManager().getDefaultDisplay().getRotation(),
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
        
        //Основное окно
        mPullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.lv_main);
        lv_main = mPullToRefreshGridView.getRefreshableView(); 
        if (type.equals("animes")){
		    adapter_alist = new aListAdapter(getBaseContext());
		    lv_main.setAdapter(adapter_alist);
        }else if(type.equals("mangas")){
        	adapter_mlist = new mListAdapter(getBaseContext());
		    lv_main.setAdapter(adapter_mlist);
        }
		
	    lv_main.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    		if (type.equals("animes")){
	    			Intent intent_com = new Intent(Activity_AnimeManga_Similar.this, Activity_AnimeManga.class);
		    		intent_com.putExtra("id", a_result.get(position).id);
		    		intent_com.putExtra("type", "anime");
		    		startActivity(intent_com);
	            }else if(type.equals("mangas")){
	            	Intent intent_com = new Intent(Activity_AnimeManga_Similar.this, Activity_AnimeManga.class);
		    		intent_com.putExtra("id", m_result.get(position).id);
		    		intent_com.putExtra("type", "manga");
		    		startActivity(intent_com);
	            }
	    	}
	    });
	    
	    mPullToRefreshGridView.setOnRefreshListener(new OnRefreshListener<GridView>() {
			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {
				a_result.clear();
				m_result.clear();
				if (Functions.isNetwork(getBaseContext())) al = new AsyncListLoading().execute();
			}
		});
	    
	    if ((savedInstanceState != null)) {
	    	a_result = (ArrayList<SSDK_Animes>) savedInstanceState.getSerializable("a_result");
		   	m_result = (ArrayList<SSDK_Mangas>) savedInstanceState.getSerializable("m_result");
			tv_unread.setText(String.valueOf(Functions.count_unread));
			if (type.equals("animes")){
				adapter_alist.notifyDataSetChanged();
			}else if(type.equals("mangas")){	
				adapter_mlist.notifyDataSetChanged();
			}
		}else{
			if (Functions.isNetwork(getBaseContext())) { 
				al = new AsyncListLoading().execute();
			}
		}
	}


	@Override
    protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
        case Constants.IDD_NO_SEARCH:
        	builder.setMessage("Ничего не найдено.")
	            .setCancelable(false)
	            .setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
	                        @SuppressWarnings("deprecation")
							public void onClick(DialogInterface dialog, int id) {
	                            removeDialog(Constants.IDD_NO_SEARCH);
	                            finish();
	                        }
	                    });
			}
			return builder.create();
    }
	
	
	//
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
        return true;
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
          	case 10:
          		/*final Random rand = new Random();
          		//Выбираем рандомное значение
            	int diceRoll = rand.nextInt(result.size());
            	
            	for (int i=0; i < result.size(); i++){
            		//Проверяем есть ли непросмотренный эпизод и если просмотрено все то предлагаем для повторного просмотра..
            		if (Integer.parseInt(result.get(diceRoll).episodes) < Integer.parseInt(result.get(diceRoll).anime_episodes_aired) ||
            				(Integer.parseInt(result.get(diceRoll).episodes) == Integer.parseInt(result.get(diceRoll).anime_episodes))){
    	            	//Открываем рандомное аниме
    	            	Intent intent_com = new Intent(ClubList_Activity.this, Anime_Manga_Activity.class);
    		    		intent_com.putExtra("id", result.get(diceRoll).anime_id);
    		    		intent_com.putExtra("type", "anime");
    		    		startActivity(intent_com);
    		    		break;
                	}
            		diceRoll = rand.nextInt(result.size());
            	}*/
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
	
  	//Асинхронная задача загрузки списка
	private class AsyncListLoading extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
			
		}
				  
		protected void onPostExecute(Void result1) {

			tv_unread.setText(String.valueOf(Functions.count_unread));

			if (type.equals("animes")){
				adapter_alist.notifyDataSetChanged();
	        }else if(type.equals("mangas")){
	        	adapter_mlist.notifyDataSetChanged();
	        }

			mPullToRefreshGridView.onRefreshComplete();
			tv_load.setVisibility(View.GONE);
			setSupportProgressBarIndeterminateVisibility(false);
			if ((a_result.size() == 0) && (m_result.size() == 0)) showDialog(Constants.IDD_NO_SEARCH);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				if (type.equals("animes")){
					a_result = SSDK_API.getSimilarAnimes(id,Functions.preference.kawai);
		        }else if(type.equals("mangas")){
		        	m_result = SSDK_API.getSimilarMangas(id,Functions.preference.kawai);
		        }
				
				SSDK_API.getUnread(Functions.preference.kawai);
				//if (t_result.size()>0) stop_update = false; else stop_update = true;
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

	//Сохранение значений элементов
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("a_result", a_result);
        outState.putSerializable("m_result", m_result);
    }
	
}
