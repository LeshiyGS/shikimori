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

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;


public class Fragment_FCharacters extends SherlockFragment {

	String user_id = "";
	Context context;
	
	ImageAdapter adapter_list;
	GridView lv_main;
	View view;
	
	AsyncTask<Void, Void, Void> al;
	
	ArrayList<String> res = new ArrayList<String>();
	ArrayList<String> res_id = new ArrayList<String>();
	ArrayList<String> res_date = new ArrayList<String>();
	ArrayList<String> res_title = new ArrayList<String>();
	ArrayList<String> res_prew = new ArrayList<String>();
	ArrayList<String> res_link = new ArrayList<String>();
	
	
	PullToRefreshGridView mPullToRefreshGridView;

	TextView tv_load;
		
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
				convertView = mLayoutInflater.inflate(R.layout.w_item_faivourits, null);
			}else{
				convertView = mLayoutInflater.inflate(R.layout.d_item_faivourits, null);
			}
			holder = new Functions.ViewHolder();
    		holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
    		holder.infoView = (TextView) convertView.findViewById(R.id.lbl_title);
    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
    		convertView.setTag(holder);
			
    		ImageLoader.getInstance().displayImage(res_prew.get(position), holder.imageView);

    		holder.infoView.setText(res_title.get(position));
			holder.textView.setText(res.get(position));
				
			
			
			return convertView;
		}
	}
	
	
	//Создание
    @SuppressWarnings("deprecation")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {       	

        user_id = Activity_Favourites.user_id;
		
        if (Functions.preference.theme.equals("ligth")){
			view = inflater.inflate(R.layout.w_activity_scroll_grid_fit, container, false);  
		}else{
			view = inflater.inflate(R.layout.d_activity_scroll_grid_fit, container, false);
		}
        context = getActivity().getBaseContext();
        //Подключаем view

        tv_load = (TextView) view.findViewById(R.id.tv_load);
        tv_load.setVisibility(View.GONE);
        
      //Основное окно
        mPullToRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.lv_main);
        lv_main = mPullToRefreshGridView.getRefreshableView(); 
	    adapter_list = new ImageAdapter(getActivity().getBaseContext());
	    lv_main.setAdapter(adapter_list);
	    
	    al = (AsyncLoading) getActivity().getLastNonConfigurationInstance();
	    if (al == null) {
	      al = new AsyncLoading();
	    }
	    
	    
	    lv_main.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_com = new Intent(getActivity().getBaseContext(), Activity_Character.class);
		    	  intent_com.putExtra("id", res_id.get(position));
		    	  startActivity(intent_com);
		      }
	    });
      

	    mPullToRefreshGridView.setOnRefreshListener(new OnRefreshListener<GridView>() {
			@Override
			public void onRefresh(PullToRefreshBase<GridView> refreshView) {

				res.clear();
				res_id.clear();
				res_date.clear();
				res_title.clear();
				res_prew.clear();
				res_link.clear();
				if (Functions.isNetwork(getActivity().getBaseContext())) al = new AsyncLoading().execute();
			}
		});
	    
	    
	           
		if ((savedInstanceState != null)) {
			res = savedInstanceState.getStringArrayList("res");
			res_id = savedInstanceState.getStringArrayList("res_id");
			res_date = savedInstanceState.getStringArrayList("res_date");
			res_title = savedInstanceState.getStringArrayList("res_title");
			res_prew = savedInstanceState.getStringArrayList("res_prew");
			res_link = savedInstanceState.getStringArrayList("res_link");
		  	adapter_list.notifyDataSetChanged();
		}else{
			if (Functions.isNetwork(context))  al = new AsyncLoading().execute();
		}
        
        return view;
        
    }
     
  //Асинхронная загрузка (Логинимся и получаем информацию о пользователе)
  		private class AsyncLoading extends AsyncTask<Void, Void, Void> {
  		  
  		  	@Override
  		  	protected void onPreExecute() {
	  		  	res.clear();
				res_id.clear();
				res_date.clear();
				res_title.clear();
				res_prew.clear();
				res_link.clear();
				tv_load.setVisibility(View.VISIBLE);
  		  		//setSupportProgressBarIndeterminateVisibility(true);
  			
  			}
  			  
  		  	protected void onPostExecute(Void result1) {
  			  	//setSupportProgressBarIndeterminateVisibility(false);
  		  		tv_load.setVisibility(View.GONE);
  		  		mPullToRefreshGridView.onRefreshComplete();
  			  	adapter_list.notifyDataSetChanged();
  			  	
  			}

  			@Override
  			protected Void doInBackground(Void... arg0) {
  				String result;
  				result = SSDK_API.getUserFavourites(user_id, Functions.preference.kawai);
  				try {
  					JSONObject jObject = new JSONObject(result);
  					JSONArray jArray;
 					jArray = jObject.getJSONArray("characters");
  					for (int i=0; i < jArray.length(); i++)
  					{
  					    try {
  					        JSONObject oneObject = jArray.getJSONObject(i);
  					        String id = oneObject.getString("id");
  					        String name = oneObject.getString("name");
  					        String logo = oneObject.getString("image");
  					        String url = oneObject.getString("url");
  					        
  					        res.add(name);
  					        res_id.add(id);
  							res_link.add("http://shikimori.org"+url);
  							if (logo.contains("http")){
  								res_prew.add(logo.replace("x48", "original"));
  							}else{
  								res_prew.add("http://shikimori.org" + logo.replace("x64", "original"));
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

  		
  	//Сохранение значений элементов
  	    @Override
  	    public void onSaveInstanceState(Bundle outState) {
  	        super.onSaveInstanceState(outState);
  	        setUserVisibleHint(true);
  	        outState.putStringArrayList("res", res);
  	        outState.putStringArrayList("res_id", res_id);
  	        outState.putStringArrayList("res_date", res_date);
  	        outState.putStringArrayList("res_title", res_title);
  	        outState.putStringArrayList("res_prew", res_prew);
  	        outState.putStringArrayList("res_link", res_link);
  	    }
  	 
}
