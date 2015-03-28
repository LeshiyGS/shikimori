package ru.gslive.shikimori.org.v2;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import net.simonvt.menudrawer.MenuDrawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
 
public class Fragment_User_Forum_Topics extends SherlockFragment {
	int rotate;
	int iii = 0;
	
	MenuDrawer mDrawer;
	
	PullToRefreshListView lv_main;
	View view;
	
	Functions.MenuAdapter adapter_menu;
	ActionBar actionbar;
	
	TopicAdapter adapter_list;
	
	ArrayList<SSDK_Topic> result = new ArrayList<SSDK_Topic>();
	
	ArrayList<String> topic_sname = new ArrayList<String>();
	
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
			
			    @SuppressLint("InflateParams")
				@Override
			    public View getView(final int position, View convertView, ViewGroup parent) {
			    	final Functions.ViewHolder holder;
				    
				    if (convertView == null) {
				    	holder = new Functions.ViewHolder();
				    	if (Functions.preference.theme.equals("ligth")){
				    		convertView = mLayoutInflater.inflate(R.layout.w_item_topic_lite, null);      
						}else{
							convertView = mLayoutInflater.inflate(R.layout.d_item_topic_lite, null);      
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
			           // holder.layoutView.removeAllViews();
			        }
				    
				    //Functions.comment_add(holder.layoutView, result.get(position).body_clean, topic_sname, getActivity().getBaseContext());
				    //holder.layoutView.setVisibility(View.GONE);
				    
				    convertView.findViewById(R.id.img_news_prew).setOnClickListener(new OnClickListener() {
			        	
			        	@SuppressLint("DefaultLocale")
						@Override
			            public void onClick(View v) {
		    				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		    				if (result.get(position).linked_type.equals("Anime") || result.get(position).linked_type.equals("Manga")){
			    				Intent intent_com = new Intent(getActivity().getBaseContext(), Activity_AnimeManga.class);
			    	    		intent_com.putExtra("id", result.get(position).linked_id);
			    	    		intent_com.putExtra("type", result.get(position).linked_type.toLowerCase());
			    	    		startActivity(intent_com);
		    				}else if(result.get(position).linked_type.equals("Review")){
		    					Intent intent_com = new Intent(getActivity().getBaseContext(), Activity_AnimeManga.class);
			    	    		intent_com.putExtra("id", result.get(position).linked_review_id);
			    	    		intent_com.putExtra("type", result.get(position).linked_review_type);
			    	    		startActivity(intent_com);
		    				}else if(result.get(position).linked_type.equals("Character")){
		    					Intent intent_com = new Intent(getActivity().getBaseContext(), Activity_Character.class);
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
		
	
	//
    @SuppressWarnings("unchecked")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//Чтение настроек
    	Functions.getPreference(getActivity().getBaseContext());

        if (Functions.preference.theme.equals("ligth")){
    		view = inflater.inflate(R.layout.w_activity_scroll_list, container, false);
		}else{
			view = inflater.inflate(R.layout.d_activity_scroll_list, container, false);
		}
        
      //Инициализация элементов    
        lv_main = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        adapter_list = new TopicAdapter(getActivity().getBaseContext());
	    lv_main.setAdapter(adapter_list);
	    	    	    
	    lv_main.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_thread = new Intent(getActivity().getBaseContext(), Activity_Thread.class);
	    		  intent_thread.putExtra("id", result.get(position-1).id);
	    		  startActivity(intent_thread);
		      }
		});
      

		lv_main.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				result.clear();
				topic_sname.clear();

				iii = 0;
				if (Functions.isNetwork(getActivity().getBaseContext())) al = new AsyncLoading().execute();
			}
		});

		TextView tv_load = (TextView) view.findViewById(R.id.tv_load);
        tv_load.setVisibility(View.GONE);
	    
	    if ((savedInstanceState != null)) {
	    	result = (ArrayList<SSDK_Topic>)  savedInstanceState.getSerializable("result");
	    		    	
	    	topic_sname = savedInstanceState.getStringArrayList("topic_sname");
	    	    	
	    	iii = savedInstanceState.getInt("iii");

		  	adapter_list.notifyDataSetChanged();

		}else{

			if (Functions.isNetwork(getActivity().getBaseContext()))  al = new AsyncLoading().execute();
		}		        		
        return view;
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
   
  //Асинхронная задача подгрузки новостей
  	private class AsyncLoading extends AsyncTask<Void, Void, Void> {
  		  
  			@Override
  			protected void onPreExecute() {
  				Functions.getPreference(getActivity().getBaseContext());
  			}
  		  
  			protected void onPostExecute(Void result1) {

  				Functions.tv_unread.setText(String.valueOf(Functions.count_unread));
  				
  				adapter_list.notifyDataSetChanged();
  				lv_main.onRefreshComplete();
  			}

  			@SuppressWarnings("static-access")
			@Override
  			protected Void doInBackground(Void... arg0) {
  				for (int i=0; i < Functions.preference.topic_fav.length; i++){
  					SSDK_Topic temp_topic = SSDK_API.getTopic(String.valueOf(Functions.preference.topic_fav[i]), Functions.preference.kawai, getActivity());
  					
  					String temp = temp_topic.body_html;
				    
					Document json = Jsoup.parse(temp);

			        topic_sname.addAll(temp_topic.spoiler_names);
					
					for(@SuppressWarnings("unused") Element element : json.select("div[class=inner]")){
					    iii++;
					}
					
					if (temp_topic.linked_type.equals("Review")){
				        topic_sname.addAll(temp_topic.spoiler_names);
						for(@SuppressWarnings("unused") Element element : json.select("div[class=inner]")){
						    iii++;
						}
					}
					
					result.add(temp_topic);
  				
  				}
  				return null;
  			}

  			
  	  }
  	
  	//
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
        setUserVisibleHint(true);
        outState.putSerializable("result", result);
        outState.putStringArrayList("topic_sname", topic_sname);
        outState.putInt("iii", iii);
    }
}