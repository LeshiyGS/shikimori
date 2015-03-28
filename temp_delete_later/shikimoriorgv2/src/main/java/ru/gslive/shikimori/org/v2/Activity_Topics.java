package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_Topics extends ShikiSherlockActivity {
	int rotate;
	Boolean stop_update = true;
    Boolean clear = true;
	int page_no = 1;
	String section = "all";
	Boolean load_unable = true;
	
	MenuDrawer mDrawer;
	
	PullToRefreshListView lv_main;
	TextView tv_load, tv_unread;
	
	Functions.MenuAdapter adapter_menu;
	ActionBar actionbar;
	
	TopicAdapter adapter_list;
	
	ArrayList<SSDK_Topic> result = new ArrayList<SSDK_Topic>();
	ArrayList<SSDK_Topic> t_result = new ArrayList<SSDK_Topic>();

	AsyncTask<Void, Void, Void> al;
	
	//Адаптер списка новостей
	public class TopicAdapter extends BaseAdapter {
			private LayoutInflater mLayoutInflater;
						
			public TopicAdapter (Context ctx) {  
			      mLayoutInflater = LayoutInflater.from(ctx);
			} 
			
		    @Override
		    public int getCount() {
		        return result.size();
		    }
		
		    @Override
		    public Object getItem(int position) {
		        return result.get(position);
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
			    		convertView = mLayoutInflater.inflate(R.layout.w_item_topic, null); 
					}else{
						convertView = mLayoutInflater.inflate(R.layout.d_item_topic, null); 
					}
			    	     
		        		
		        	holder.textView = (TextView) convertView.findViewById(R.id.lbl_manga_name);
		        	holder.infoView = (TextView) convertView.findViewById(R.id.lbl_user);
		        	holder.dateView = (TextView) convertView.findViewById(R.id.lbl_date);
		        	holder.imageView = (ImageView) convertView.findViewById(R.id.img_news_prew);
		        	holder.userView = (ImageView) convertView.findViewById(R.id.img_item_preview);
		        	holder.layoutView = (LinearLayout) convertView.findViewById(R.id.ll_comment_add);
		        	holder.newView = (TextView) convertView.findViewById(R.id.lbl_new);
		        	convertView.setTag(holder);
			    }else {
		            holder = (Functions.ViewHolder) convertView.getTag();
		            holder.layoutView.removeAllViews();
		        }

                Log.d(Constants.LogTag, " count -> " + result.get(position).spoiler_names.size());
			    //Functions.comment_add(holder.layoutView, result.get(position).body_clean, topic_sname, getBaseContext());
                Functions.addComment(result.get(position).body_clean, result.get(position).spoiler_names, holder.layoutView, getBaseContext(), Activity_Topics.this, false);
			    				    
			    convertView.findViewById(R.id.img_news_prew).setOnClickListener(new OnClickListener() {
		        	
		        	@SuppressLint("DefaultLocale")
					@Override
		            public void onClick(View v) {
	    				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
	    				if (result.get(position).linked_type.equals("Anime") || result.get(position).linked_type.equals("Manga")){
		    				Intent intent_com = new Intent(getBaseContext(), Activity_AnimeManga.class);
		    	    		intent_com.putExtra("id", result.get(position).linked_id);
		    	    		intent_com.putExtra("type", result.get(position).linked_type.toLowerCase());
		    	    		startActivity(intent_com);
	    				}else if(result.get(position).linked_type.equals("Review")){
	    					Intent intent_com = new Intent(getBaseContext(), Activity_AnimeManga.class);
		    	    		intent_com.putExtra("id", result.get(position).linked_review_id);
		    	    		intent_com.putExtra("type", result.get(position).linked_review_type);
		    	    		startActivity(intent_com);
	    				}else if(result.get(position).linked_type.equals("Character")){
	    					Intent intent_com = new Intent(getBaseContext(), Activity_Character.class);
	    					intent_com.putExtra("id", result.get(position).linked_id);
	    					startActivity(intent_com);
	    				}
		        		
		            }
		        });
			    
	        	holder.textView.setText(result.get(position).title);
	        	holder.infoView.setText(result.get(position).user_name); 
	        	holder.dateView.setText(result.get(position).date); 
	        	Log.d(Constants.LogTag, "last => " + result.get(position).last_comment_viewed);
	        	if (result.get(position).last_comment_viewed){
			    	holder.newView.setVisibility(View.GONE);
			    }else{
			    	holder.newView.setVisibility(View.VISIBLE);
			    }
	        	
	        	
	        	ImageLoader.getInstance().displayImage(result.get(position).user_avatar, holder.userView);
	        	if (result.get(position).linked_type.equals("Group")){
	        		ImageLoader.getInstance().displayImage(result.get(position).linked_image, holder.userView);
	        		holder.infoView.setText(result.get(position).title);
	        		holder.textView.setVisibility(View.GONE);
	        		holder.dateView.setVisibility(View.GONE);
	        		holder.imageView.setVisibility(View.GONE);
	        	}else{
	        		holder.textView.setVisibility(View.VISIBLE);
	        		holder.dateView.setVisibility(View.VISIBLE);
	        		if (!result.get(position).linked_image.equals("")) {
	        			ImageLoader.getInstance().displayImage(result.get(position).linked_image, holder.imageView);
		        		holder.imageView.setVisibility(View.VISIBLE);
		        	} else {
		        		holder.imageView.setVisibility(View.GONE);
		        	}
	        	}        	
		        return convertView;
		    }
		}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		Functions.getPreference(getBaseContext());
				
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
			mDrawer = Functions.setMenuDrawer(actionbar, "", R.layout.w_activity_scroll_list, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, "", R.layout.d_activity_scroll_list, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}
		
		actionbar.setTitle("");
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(actionbar.getThemedContext(), R.array.topic_list_name, android.R.layout.simple_list_item_1);
        		
		OnNavigationListener mOnNavigationListener = new OnNavigationListener() {
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            	String[] list = getResources().getStringArray(R.array.topic_list);
            	if (!section.equals(list[itemPosition])){
	            	switch (itemPosition){
		            	case 0:
		            		section = "all";
		            		break;
		            	case 1:
		            		section = "news";
		            		break;
		            	case 2:
		            		section = "a";
		            		break;
		            	case 3:
		            		section = "m";
		            		break;
		            	case 4:
		            		section = "c";
		            		break;
		            	case 5:
		            		section = "s";
		            		break;
		            	case 6:
		            		section = "o";
		            		break;
		            	case 7:
		            		section = "g";
		            		break;
		            	case 8:
		            		section = "reviews";
		            		break;
		            	case 9:
		            		section = "v";
		            		break;
	            	}
	            	result.clear();
	            	t_result.clear();
	            	page_no = 1;
	            	adapter_list.notifyDataSetChanged();
	            	al = new AsyncLoading().execute();
            	}
                return true;
            }
        };
        
        actionbar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
		
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
        lv_main = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        adapter_list = new TopicAdapter(Activity_Topics.this);
	    lv_main.setAdapter(adapter_list);
	    
	    tv_load = (TextView) findViewById(R.id.tv_load);
        tv_load.setVisibility(View.GONE);
	    	    
	    lv_main.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_thread = new Intent(Activity_Topics.this, Activity_Thread.class);
	    		  intent_thread.putExtra("id", result.get(position-1).id);
	    		  startActivity(intent_thread);
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

				result.clear();
				t_result.clear();

                clear = true;
				page_no = 1;
				if (Functions.isNetwork(getBaseContext())) al = new AsyncLoading().execute();
			}
		});

	    al = (AsyncLoading) getLastNonConfigurationInstance();
	    if (al == null) {
	      al = new AsyncLoading();
	    }
	    
	    if ((savedInstanceState != null)) {
	    	result = (ArrayList<SSDK_Topic>)  savedInstanceState.getSerializable("result");
	    	t_result = (ArrayList<SSDK_Topic>)  savedInstanceState.getSerializable("t_result");

	    	section = savedInstanceState.getString("section");

			page_no = savedInstanceState.getInt("page_no");
			stop_update = savedInstanceState.getBoolean("stop_update");
            clear = savedInstanceState.getBoolean("clear");
		  	adapter_list.notifyDataSetChanged();
		  	
		  	Functions.tv_unread.setText(String.valueOf(Functions.count_unread));
		}else{
			page_no = 1;
			stop_update = true;
			
			result.clear();
			t_result.clear();

			adapter_list.notifyDataSetChanged();
			if (Functions.isNetwork(getBaseContext()))  al = new AsyncLoading().execute();
		}


	}
	
	//Асинхронная задача подгрузки новостей
	private class AsyncLoading extends AsyncTask<Void, Void, Void> {
		  
			@Override
			protected void onPreExecute() {
				setSupportProgressBarIndeterminateVisibility(true);
			}
		  
			protected void onPostExecute(Void result1) {
				result.addAll(t_result);

				Functions.tv_unread.setText(String.valueOf(Functions.count_unread));
				clear = false;
				adapter_list.notifyDataSetChanged();
				lv_main.onRefreshComplete();
				tv_load.setVisibility(View.GONE);
				setSupportProgressBarIndeterminateVisibility(false);
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					t_result = SSDK_API.getTopics(section, 20, page_no, Functions.preference.kawai, getBaseContext());
					SSDK_API.getUnread(Functions.preference.kawai);
					if (t_result.size()>0)stop_update = false; else stop_update = true;
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

	//Сохранение значения Асинхронной задачи
	public Object onRetainNonConfigurationInstance() {
		return al;
	}
	
	//События нажатий на меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
	
  	//Сохранение значений элементов
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page_no", page_no);
        outState.putBoolean("stop_update", stop_update);
        outState.putBoolean("clear", clear);
        outState.putSerializable("result", result);
        outState.putSerializable("t_result", t_result);
        outState.putString("section", section);

    }

}