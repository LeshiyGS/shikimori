package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;


public class Activity_History extends ShikiSherlockActivity {

	int page_no = 1, rotate;
	Boolean stop_update = true;
	String user_id;
	String id;

	ArrayList<SSDK_History> history = new ArrayList<SSDK_History>();
	ArrayList<SSDK_History> t_history = new ArrayList<SSDK_History>();
	
	GridView lv_main;
	
	AsyncTask<Void, Void, Void> al;
		
	Functions.MenuAdapter adapter_menu;
	myAdapter adapter_list;
	PullToRefreshGridView mPullToRefreshGridView;
	
	MenuDrawer mDrawer;
	TextView tv_unread;
	TextView tv_load;
	
	ActionBar actionbar;
	  
	public class myAdapter extends BaseAdapter {  
	    private LayoutInflater mLayoutInflater;  
	          
	    public myAdapter (Context ctx) {  
	    	mLayoutInflater = LayoutInflater.from(ctx);  
	    }  
	          
	    public int getCount () {  
	      return history.size();  
	    }  
	          
	    public Object getItem (int position) {  
	      return position;  
	    }  
	          
	    public long getItemId (int position) {  
	      return position;  
	    }  
	          
	    public String getString (int position) {  
	      return history.get(position).img + " (" + history.get(position).name + ")";  
	    }  
	    
	    public View getView(int position, View convertView, ViewGroup parent) {   
	      final Functions.ViewHolder holder;
	      
	      if (convertView == null) {
	    	  holder = new Functions.ViewHolder();
	    	  if (Functions.preference.theme.equals("ligth")){
	    		  convertView = mLayoutInflater.inflate(R.layout.w_item_history_grid, null);  
	    	  }else{
	    		  convertView = mLayoutInflater.inflate(R.layout.d_item_history_grid, null);  
	    	  }
		      holder.textView = (TextView) convertView.findViewById(R.id.lbl_h_name);
		      holder.infoView = (TextView) convertView.findViewById(R.id.lbl_h_info);
		      holder.dateView = (TextView) convertView.findViewById(R.id.lbl_h_date);
		      holder.imageView = (ImageView) convertView.findViewById(R.id.img_h_image);
		      convertView.setTag(holder);
	      } else {
              holder = (Functions.ViewHolder) convertView.getTag();
          }
	      
	      Functions.setFirstTitle(holder.textView, history.get(position).name_russian, history.get(position).name);

          holder.infoView.setText(Html.fromHtml(history.get(position).info)); 
          holder.dateView.setText(history.get(position).date); 
          
          ImageLoader.getInstance().displayImage(history.get(position).img, holder.imageView);

	      return convertView;  
	    }

	  }
	
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
		
		user_id = getIntent().getExtras().getString("id");
		
		//Подключение и выбор варианта меню.
		if (Functions.preference.theme.equals("ligth")){
			mDrawer = Functions.setMenuDrawer(actionbar, "История", R.layout.w_activity_scroll_grid, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
  	  	}else{
  	  		mDrawer = Functions.setMenuDrawer(actionbar, "История", R.layout.d_activity_scroll_grid, getWindowManager().getDefaultDisplay().getRotation(),
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
        mPullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.lv_main);
        lv_main = mPullToRefreshGridView.getRefreshableView(); 
	    adapter_list = new myAdapter(Activity_History.this);
	    lv_main.setAdapter(adapter_list);
	    
	    al = (AsyncLoading) getLastNonConfigurationInstance();
	    if (al == null) {
	      al = new AsyncLoading();
	    }
	    	    
	    lv_main.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view,
		          int position, long id) {

		    	  if (!history.get(position).id.equals("") && !history.get(position).type.equals("")){
		    	  	Intent intent_com = new Intent(Activity_History.this, Activity_AnimeManga.class);
		        	intent_com.putExtra("id", history.get(position).id);
		        	intent_com.putExtra("type", history.get(position).type);
		  			startActivity(intent_com);
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

	    mPullToRefreshGridView.setOnRefreshListener(new OnRefreshListener<GridView>() {
			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {
				history.clear();
				t_history.clear();
				page_no = 1;
				if (Functions.isNetwork(getBaseContext())) al = new AsyncLoading().execute();
			}
		});
	    
	    
	           
		if ((savedInstanceState != null)) {
			history = (ArrayList<SSDK_History>) savedInstanceState.getSerializable("history");
			t_history = (ArrayList<SSDK_History>) savedInstanceState.getSerializable("t_history");

			page_no = savedInstanceState.getInt("page_no");
			stop_update = savedInstanceState.getBoolean("stop_update");
			
			tv_unread.setText(String.valueOf(Functions.count_unread));
		  	adapter_list.notifyDataSetChanged();
		}else{
			if (Functions.isNetwork(getBaseContext()))  al = new AsyncLoading().execute();
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
  	
  	//
  	private class AsyncLoading extends AsyncTask<Void, Void, Void> {
  	  
	  	@Override
	  	protected void onPreExecute() {
	  		setSupportProgressBarIndeterminateVisibility(true);
		}
		  
	  	protected void onPostExecute(Void result1) {
	  		history.addAll(t_history);
	  		adapter_list.notifyDataSetChanged();
	  		mPullToRefreshGridView.onRefreshComplete();
			tv_load.setVisibility(View.GONE);
			tv_unread.setText(String.valueOf(Functions.count_unread));
			
			setSupportProgressBarIndeterminateVisibility(false);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				t_history = SSDK_API.getHistory(user_id, Functions.preference.kawai, 20, page_no);
				SSDK_API.getUnread(Functions.preference.kawai);
				if (t_history.size()>0)stop_update = false; else stop_update = true;
				if (t_history.size()>20) t_history.remove(t_history.size()-1);
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
	
	//Сохранение значений элементов
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page_no", page_no);
        outState.putBoolean("stop_update", stop_update);
        outState.putSerializable("history", history);
        outState.putSerializable("t_history", t_history);  
    }

}
