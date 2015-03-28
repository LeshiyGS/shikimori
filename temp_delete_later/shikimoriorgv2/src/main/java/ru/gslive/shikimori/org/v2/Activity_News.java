package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_News extends ShikiSherlockActivity {

	Boolean stop_update = true;
	String ids;
	int page_no = 1, iii = 0, rotate;
	
	ActionBar actionbar;
	
	AsyncTask<Void, Void, Void> al;
	
	ArrayList<SSDK_News> result = new ArrayList<SSDK_News>();
	ArrayList<SSDK_News> t_result = new ArrayList<SSDK_News>();

	Functions.MenuAdapter adapter_menu;
	TraningAdapter adapter_list;
	
	MenuDrawer mDrawer;
	TextView tv_unread, tv_load;
	PullToRefreshListView lv_main;
	
	private AsyncRead loader_read;
	
	//Адаптер списка новостей
	public class TraningAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		
		public TraningAdapter (Context ctx) {  
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
		    		convertView = mLayoutInflater.inflate(R.layout.w_item_news, null);      
				}else{
					convertView = mLayoutInflater.inflate(R.layout.d_item_news, null);      
				}
	        		
	        	holder.textView = (TextView) convertView.findViewById(R.id.lbl_manga_name);
	        	holder.newView = (TextView) convertView.findViewById(R.id.lbl_new);
	        	holder.infoView = (TextView) convertView.findViewById(R.id.lbl_manga_anime_info);
	        	holder.imageView = (ImageView) convertView.findViewById(R.id.img_news_prew);
	        	holder.userView = (ImageView) convertView.findViewById(R.id.img_item_preview);
	        	holder.layoutView = (LinearLayout) convertView.findViewById(R.id.ll_comment_add);
	        	convertView.setTag(holder);
		    }else {
	            holder = (Functions.ViewHolder) convertView.getTag();
	            holder.layoutView.removeAllViews();
	        }
		    
		    //Functions.comment_add(holder.layoutView, result.get(position).body_clean, news_sname, getBaseContext());
            Functions.addComment(result.get(position).body_clean, result.get(position).spoiler_names,holder.layoutView, getBaseContext(), Activity_News.this, stop_update);
		    	
		    convertView.findViewById(R.id.lbl_new).setOnClickListener(new OnClickListener() {
	        	
        		@Override
	            public void onClick(View v) {
	        		holder.newView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
	        		startRead(result.get(position).id);
	        		holder.newView.setVisibility(View.GONE);
	        		result.get(position).read = true;
        		}
	        });
		    
		    convertView.findViewById(R.id.img_item_preview).setOnClickListener(new OnClickListener() {
	        	
	        	@Override
	            public void onClick(View v) {
	        		holder.userView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    				Intent intent_user = new Intent(Activity_News.this, Activity_UserProfile.class);
    	    		intent_user.putExtra("id", result.get(position).from_id);
    	    		startActivity(intent_user);
	        		
	            }
	        });
		    
        	holder.textView.setText(result.get(position).from_nickname);
        	if (result.get(position).read){
        		holder.newView.setVisibility(View.GONE);
        	}else{
        		holder.newView.setVisibility(View.VISIBLE);
        	}
        	
        	holder.infoView.setText(result.get(position).created_at); 
        	
        	ImageLoader.getInstance().displayImage(result.get(position).from_avatar, holder.userView);
        	if (!result.get(position).l_image.equals("")) {
        		ImageLoader.getInstance().displayImage(result.get(position).l_image, holder.imageView);
        		holder.imageView.setVisibility(View.VISIBLE);
        	} else {
        		holder.imageView.setVisibility(View.GONE);
        	}

	        return convertView;
	    }
	}

	//Создание активити
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		//Чтение настроек
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
		
		if (Functions.preference.theme.equals("ligth")){
			mDrawer = Functions.setMenuDrawer(actionbar, "Новости", R.layout.w_activity_scroll_list, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, "Новости", R.layout.d_activity_scroll_list, getWindowManager().getDefaultDisplay().getRotation(),
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
		        
        tv_load = (TextView) findViewById(R.id.tv_load);
        tv_load.setVisibility(View.GONE);
        
        //Основное окно
        lv_main = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        adapter_list = new TraningAdapter(Activity_News.this);
	    lv_main.setAdapter(adapter_list);
	    
	    lv_main.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  if (result.get(position-1).l_type.equals("anime")){
			    	  Intent intent_anime = new Intent(Activity_News.this, Activity_AnimeManga.class);
			    	  intent_anime.putExtra("id", result.get(position-1).l_id);
			    	  intent_anime.putExtra("type", result.get(position-1).l_type);
			    	  startActivity(intent_anime);    
		    	  }else{
		    		  Intent intent_user = new Intent(Activity_News.this, Activity_Thread.class);
		    		  intent_user.putExtra("id", result.get(position-1).linked_topic_url);
		    		  startActivity(intent_user);
		    	  }
		    	  if (result.get(position-1).read == false){
		    		  startRead(result.get(position-1).id);
		    		  result.get(position-1).read = true;
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

				result.clear();
				t_result.clear();
							
				page_no = 1;
				if (Functions.isNetwork(getBaseContext())) al = new AsyncLoading().execute();
			}
		});

	    al = (AsyncLoading) getLastNonConfigurationInstance();
	    if (al == null) {
	      al = new AsyncLoading();
	    }
	    
	    if ((savedInstanceState != null)) {

	    	result = (ArrayList<SSDK_News>)  savedInstanceState.getSerializable("result");
	    	t_result = (ArrayList<SSDK_News>)  savedInstanceState.getSerializable("t_result");
	    	
	    	tv_unread.setText(String.valueOf(Functions.count_unread));
	    	
	    	iii = savedInstanceState.getInt("iii");
			page_no = savedInstanceState.getInt("page_no");
			stop_update = savedInstanceState.getBoolean("stop_update");
		  	adapter_list.notifyDataSetChanged();
		}else{
			iii = 0;
			page_no = 1;
			stop_update = true;

			result.clear();
			t_result.clear();
			
			adapter_list.notifyDataSetChanged();
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
	
	@Override
    protected void onDestroy() {
		setResult(1, new Intent());
        super.onDestroy();
    }

	//Задача пометки прочтения
	public void startRead(String n_id) {
    	if (loader_read == null || loader_read.getStatus().equals(AsyncTask.Status.FINISHED)) {
    		loader_read = new AsyncRead(n_id);
    		loader_read.execute();
    	} else {
    		Toast.makeText(Activity_News.this, "Ждите завершения загрузки", Toast.LENGTH_SHORT).show();
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

			tv_unread.setText(String.valueOf(Functions.count_unread));

			adapter_list.notifyDataSetChanged();
			lv_main.onRefreshComplete();
			tv_load.setVisibility(View.GONE);
			setSupportProgressBarIndeterminateVisibility(false);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				t_result = SSDK_API.getNews(20, page_no, Functions.preference.kawai);
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
	
	//Асинхронная задача пометки прочтения
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
	    	setSupportProgressBarIndeterminateVisibility(false);
	    	adapter_list.notifyDataSetChanged();
	    	Functions.updateWidget(getBaseContext());
	    }

	    @Override
	    protected Void doInBackground(Void... arg0) {
	    	SSDK_API.readMessages("message-" + this.n_id);
    		SSDK_API.getUnread(Functions.preference.kawai);
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
		    	for (int i=0; i<result.size();i++){
		    		if (result.get(i).read == false){
			    		SSDK_API.readMessages("message-" + result.get(i).id);
			    		result.get(i).read = true;
		    		}
		    	}
		    	SSDK_API.getUnread(Functions.preference.kawai);
				return null;
		    }
				
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
	
  	//Сохранение значения Асинхронной задачи
  	public Object onRetainNonConfigurationInstance() {
  		return al;
  	}
  	
	//Сохранение значений элементов
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page_no", page_no);
        outState.putBoolean("stop_update", stop_update);
        outState.putSerializable("result", result);
        outState.putSerializable("t_result", t_result);
        outState.putInt("iii", iii);

    }
}
