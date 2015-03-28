package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;


public class Fragment_Mangas extends SherlockFragment {

	Context context;
	
	
	static AnimeAdapter adapter_list;
	GridView lv_main;
	int page_no = 1;
	Boolean stop_update = true;
	
	public static AsyncTask<Void, Void, Void> al;
	
	ArrayList<SSDK_Mangas> result = new ArrayList<SSDK_Mangas>();
	ArrayList<SSDK_Mangas> t_result = new ArrayList<SSDK_Mangas>();
	
	static PullToRefreshGridView mPullToRefreshGridView;

	TextView tv_load;
	
	public class AnimeAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		
		public AnimeAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return result.size(); // длина массива
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return result.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;
	        
			if (convertView == null) {
				holder = new Functions.ViewHolder();
				if (Functions.preference.theme.equals("ligth")){
					convertView = mLayoutInflater.inflate(R.layout.w_item_faivourits, null); 
				}else{
					convertView = mLayoutInflater.inflate(R.layout.d_item_faivourits, null);
				}
				
				holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
	    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
				convertView.setTag(holder);
			} else {
				holder = (Functions.ViewHolder) convertView.getTag();
			}

			ImageLoader.getInstance().displayImage(result.get(position).image_original, holder.imageView);

			holder.textView.setText(result.get(position).name);
				
			
			
			return convertView;
		}
	}
	
	//Создание
    @SuppressWarnings("unchecked")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         
    	context = getActivity().getBaseContext();
    	
    	Functions.getPreference(context);
    	View view;
    	if (Functions.preference.theme.equals("ligth")){
    		view = inflater.inflate(R.layout.w_activity_scroll_grid_fit, container, false);
		}else{
			view = inflater.inflate(R.layout.d_activity_scroll_grid_fit, container, false);
		}
        

        //Подключаем view

        tv_load = (TextView) view.findViewById(R.id.tv_load);
        tv_load.setVisibility(View.GONE);
        
        //Основное окно
        mPullToRefreshGridView = (PullToRefreshGridView) view.findViewById(R.id.lv_main);
        lv_main = mPullToRefreshGridView.getRefreshableView(); 
	    adapter_list = new AnimeAdapter(getActivity().getBaseContext());
	    lv_main.setAdapter(adapter_list);
	    	
	    al = (AsyncLoading) onRetainNonConfigurationInstance();
	    if (al == null) {
	      al = new AsyncLoading();
	    }
	    
	    lv_main.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    		
	    		Intent intent_com = new Intent(getActivity().getBaseContext(), Activity_AnimeManga.class);
	    		intent_com.putExtra("id", result.get(position).id);
	    		intent_com.putExtra("type", "manga");
	    		startActivity(intent_com);
	
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
				result.clear();
				t_result.clear();
				page_no = 1;
				if (Functions.isNetwork(getActivity().getBaseContext())) al = new AsyncLoading().execute();
			}
		});
	           
		if ((savedInstanceState != null)) {
			result = (ArrayList<SSDK_Mangas>) savedInstanceState.getSerializable("result");
			t_result =  (ArrayList<SSDK_Mangas>) savedInstanceState.getSerializable("t_result");
			page_no = savedInstanceState.getInt("page_no");
			stop_update = savedInstanceState.getBoolean("stop_update");
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
				tv_load.setVisibility(View.VISIBLE);
  			}
  			  
  		  	protected void onPostExecute(Void result1) {
  		  		result.addAll(t_result);
  		  		tv_load.setVisibility(View.GONE);
  		  		mPullToRefreshGridView.onRefreshComplete();
  			  	adapter_list.notifyDataSetChanged();
  			}

  			@Override
  			protected Void doInBackground(Void... arg0) {
  				try {
					t_result = SSDK_API.getFiltredMangas(Activity_Mangas.SearchText, Activity_Mangas.fstatus, Activity_Mangas.ftype, 
							Activity_Mangas.fseason, Activity_Mangas.forder_by, Activity_Mangas.fmylist, Activity_Mangas.fgenre, 
							Functions.preference.kawai, 30, page_no);
					SSDK_API.getUnread(Functions.preference.kawai);
					if (t_result.size()>0)stop_update = false; else stop_update = true;
					if (t_result.size()>30) t_result.remove(t_result.size()-1);
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
  		setUserVisibleHint(true);
  		outState.putSerializable("result", result);
  		outState.putSerializable("t_result", t_result);
  		outState.putInt("page_no", page_no);
        outState.putBoolean("stop_update", stop_update);
  	}
  	 
}
