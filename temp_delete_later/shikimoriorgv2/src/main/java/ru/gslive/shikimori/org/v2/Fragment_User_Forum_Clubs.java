package ru.gslive.shikimori.org.v2;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
 
public class Fragment_User_Forum_Clubs extends SherlockFragment {
 		
	int page_no = 1, rotate;
	Boolean stop_update = true;

	PullToRefreshGridView mPullToRefreshGridView;
	
	AsyncTask<Void, Void, Void> al;
	
	ActionBar actionbar;
	View view;
	
	Functions.MenuAdapter adapter_menu;
	
	ImageAdapter adapter_list;
	
	TextView tv_load;
	GridView lv_main;
	
	
	ArrayList<SSDK_Club> result = new ArrayList<SSDK_Club>();
	
	ArrayList<String> res = new ArrayList<String>();
	ArrayList<String> res_id = new ArrayList<String>();
	ArrayList<String> res_title = new ArrayList<String>();
	ArrayList<String> res_prew = new ArrayList<String>();
	ArrayList<String> res_link = new ArrayList<String>();

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		
		public ImageAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return res.size(); // длина массива
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return res.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;

			if (Functions.preference.theme.equals("ligth")){
				convertView = mLayoutInflater.inflate(R.layout.w_item_club, null);
			}else{
				convertView = mLayoutInflater.inflate(R.layout.d_item_club, null);
			}

			holder = new Functions.ViewHolder();
    		holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
    		holder.infoView = (TextView) convertView.findViewById(R.id.lbl_title);
    		holder.newimageView = (View_ImageView) convertView.findViewById(R.id.iv_item_preview);
    		convertView.setTag(holder);

            Ion.with(holder.newimageView)
                    //.placeholder(R.drawable.ic_launcher)
                    .animateLoad(R.anim.spin_animation)
                    .error(R.drawable.missing_preview)
                    .load(res_prew.get(position));
    		//ImageLoader.getInstance().displayImage(res_prew.get(position), holder.newimageView);
    		holder.infoView.setText(res_title.get(position));
			holder.textView.setText(res.get(position));
			
			
				
			return convertView;
		}
	}
	
	//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//Чтение настроек
    	Functions.getPreference(getActivity().getBaseContext());

    	if (Functions.preference.theme.equals("ligth")){
    		view = inflater.inflate(R.layout.w_activity_scroll_grid_fit, container, false);
		}else{
			view = inflater.inflate(R.layout.d_activity_scroll_grid_fit, container, false);
		}
        
        
        tv_load = (TextView) view.findViewById(R.id.tv_load);
		tv_load.setVisibility(View.GONE);
		
		mPullToRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.lv_main);
		lv_main = mPullToRefreshGridView.getRefreshableView(); 
		adapter_list = new ImageAdapter(getActivity().getBaseContext());
		lv_main.setAdapter(adapter_list);
      		
		lv_main.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent_club = new Intent(getActivity().getBaseContext(), Activity_Club.class);
				intent_club.putExtra("id", res_id.get(position));
				intent_club.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent_club);
			}
		});
      		
      		
		mPullToRefreshGridView.setOnRefreshListener(new OnRefreshListener<GridView>() {
			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {
				
				res.clear();
				res_id.clear();
				res_title.clear();
				res_prew.clear();
				res_link.clear();
				if (Functions.isNetwork(getActivity().getBaseContext())) al = new AsyncLoading().execute();
			}
		});
      		
		if ((savedInstanceState != null)) {
			res = savedInstanceState.getStringArrayList("res");
			res_id = savedInstanceState.getStringArrayList("res_id");
			res_title = savedInstanceState.getStringArrayList("res_title");
			res_prew = savedInstanceState.getStringArrayList("res_prew");
			res_link = savedInstanceState.getStringArrayList("res_link");
			
	
			adapter_list.notifyDataSetChanged();
		}else{
			if (Functions.isNetwork(getActivity().getBaseContext()))  al = new AsyncLoading().execute();
		}
        
        return view;
    }
  
  //Асинхронная загрузка (Логинимся и получаем информацию о пользователе)
  	private class AsyncLoading extends AsyncTask<Void, Void, Void> {
  			  
  		@Override
  		protected void onPreExecute() {
  			res.clear();
  			res_id.clear();
  			res_title.clear();
  			res_prew.clear();
  			res_link.clear();
  			tv_load.setVisibility(View.VISIBLE);
	
  		}
  				  
  		protected void onPostExecute(Void result1) {

  			tv_load.setVisibility(View.GONE);
  			mPullToRefreshGridView.onRefreshComplete();
  			adapter_list.notifyDataSetChanged();
  			//Activity_User_Forums.tv_unread.setText(String.valueOf(Functions.count_unread));
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			String result;
  			result = SSDK_API.getUserClubs(Functions.preference.user_id, Functions.preference.kawai);
  			//SSDK_API.getUnread(Functions.preference.kawai);
  			try {
  				JSONArray jArray = new JSONArray(result);
  				for (int i=0; i < jArray.length(); i++){
  					try {
  						JSONObject oneObject = jArray.getJSONObject(i);
  						String id = oneObject.getString("id");
  						String name = oneObject.getString("name");
  						String logo = oneObject.getJSONObject("logo").getString("main");
  						res.add(name);
  						res_id.add(id);
  						res_link.add("http://shikimori.org/clubs/"+id);
  						if (logo.contains("http")){
  							res_prew.add(logo);
  						}else{
  							res_prew.add(Constants.SERVER + logo);
  						}
  						res_title.add("");
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
  	
    
    //Сохранение значения Асинхронной задачи
  	public Object onRetainNonConfigurationInstance() {
  		return al;
  	}
    
    //Восстановление значений при возврате в активити
  	@Override
	public void onResume() {

        super.onResume();
    }
    
  	
  	//
  //Сохранение значений элементов
  	@Override
  	public void onSaveInstanceState(Bundle outState) {
  	        super.onSaveInstanceState(outState);
  	        //setUserVisibleHint(true);
  	        outState.putStringArrayList("res", res);
  	        outState.putStringArrayList("res_id", res_id);
  	        outState.putStringArrayList("res_title", res_title);
  	        outState.putStringArrayList("res_prew", res_prew);
  	        outState.putStringArrayList("res_link", res_link);
  	}
}