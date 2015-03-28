package ru.gslive.shikimori.org.v2;

import java.util.ArrayList;

import net.simonvt.menudrawer.MenuDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_Friends extends ShikiSherlockActivity {

	String user_id;
	int page_no = 1, rotate;
	Boolean stop_update = true;
	
	ActionBar actionbar;
	
	PullToRefreshGridView mPullToRefreshGridView;
	
	AsyncTask<Void, Void, Void> al;
	
	Functions.MenuAdapter adapter_menu;
	
	ImageAdapter adapter_list;
	
	public MenuDrawer mDrawer;
	public static TextView tv_unread;
	TextView tv_load;
	GridView lv_main;
	
	ArrayList<String> res = new ArrayList<String>();
	ArrayList<String> res_id = new ArrayList<String>();
	ArrayList<String> res_avatar = new ArrayList<String>();
		
	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		
		public ImageAdapter (Context ctx) {  
		      mLayoutInflater = LayoutInflater.from(ctx); 
		} 
		
	    @Override
	    public int getCount() {
	        return res.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return res.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
	    	final Functions.ViewHolder holder;
	       
	    	if (Functions.preference.theme.equals("ligth")){
	    		convertView = mLayoutInflater.inflate(R.layout.w_item_club, null);
			}else{
				convertView = mLayoutInflater.inflate(R.layout.d_item_club, null);  
			}
			
			
			holder = new Functions.ViewHolder();
    		holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
    		holder.infoView = (TextView) convertView.findViewById(R.id.lbl_title);
    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
    		convertView.setTag(holder);
			
    		ImageLoader.getInstance().displayImage(res_avatar.get(position), holder.imageView);

			holder.textView.setText(res.get(position));
			

			return convertView;
	    }
	}

	//
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
			mDrawer = Functions.setMenuDrawer(actionbar, "Друзья", R.layout.w_activity_scroll_grid_fit, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, "Друзья", R.layout.d_activity_scroll_grid_fit, getWindowManager().getDefaultDisplay().getRotation(),
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
		adapter_list = new ImageAdapter(Activity_Friends.this);
		lv_main.setAdapter(adapter_list);
			    
		lv_main.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent_user = new Intent(Activity_Friends.this, Activity_UserProfile.class);
				//intent_user.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
				intent_user.putExtra("id", res_id.get(position));
				startActivity(intent_user);
			}
		});
			    
		mPullToRefreshGridView.setOnRefreshListener(new OnRefreshListener<GridView>() {
			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {

				res.clear();
				res_id.clear();
				res_avatar.clear();
				if (Functions.isNetwork(getBaseContext())) al = new AsyncLoading().execute();
			}
		});
			    		           
		if ((savedInstanceState != null)) {
			res = savedInstanceState.getStringArrayList("res");
			res_id = savedInstanceState.getStringArrayList("res_id");
			res_avatar = savedInstanceState.getStringArrayList("res_avatar");
			adapter_list.notifyDataSetChanged();
		}else{
			if (Functions.isNetwork(getBaseContext()))  al = new AsyncLoading().execute();
		}
	}	

	//Асинхронная загрузка (Логинимся и получаем информацию о пользователе)
	private class AsyncLoading extends AsyncTask<Void, Void, Void> {
			  
			  	@Override
			  	protected void onPreExecute() {
			  		res.clear();
					res_id.clear();
					res_avatar.clear();
					tv_load.setVisibility(View.VISIBLE);
			  		setSupportProgressBarIndeterminateVisibility(true);
				
				}
				  
			  	protected void onPostExecute(Void result1) {
				  	setSupportProgressBarIndeterminateVisibility(false);
				  	tv_load.setVisibility(View.GONE);
	  		  		mPullToRefreshGridView.onRefreshComplete();
	  			  	adapter_list.notifyDataSetChanged();
				  	
				}

				@Override
				protected Void doInBackground(Void... arg0) {
					String result;
					result = SSDK_API.getFriends(user_id, Functions.preference.kawai);
					try {
						JSONArray jArray = new JSONArray(result);
						for (int i=0; i < jArray.length(); i++)
						{
						    try {
						        JSONObject oneObject = jArray.getJSONObject(i);
						        
								res.add(oneObject.getString("nickname"));
								res_id.add(oneObject.getString("id"));
								if (oneObject.getJSONObject("image").getString("x160").contains("http")){
									res_avatar.add(oneObject.getJSONObject("image").getString("x160"));
								}else{
									res_avatar.add(Constants.SERVER + oneObject.getJSONObject("image").getString("x160"));
									}
								
						    } catch (JSONException e) {
						        // Oops
						    }
						}
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
		outState.putStringArrayList("res", res);
		outState.putStringArrayList("res_id", res_id);
		outState.putStringArrayList("res_avatar", res_avatar);
	}
	
	    
	//Actions меню
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
	   
	//При нажатии кнопки меню открываем основное меню
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			event.startTracking();
			mDrawer.toggleMenu(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
