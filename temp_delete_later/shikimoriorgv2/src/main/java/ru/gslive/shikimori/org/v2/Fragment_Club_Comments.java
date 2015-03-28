package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;


import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
 
public class Fragment_Club_Comments extends SherlockFragment {
 
	int page_no = 1, iii=0;
	Boolean stop_update = true;
	
	static CommentsAdapter adapter_list;
	PullToRefreshListView lv_main;
	TextView tv_load;
	ListView actualListView;
	View view;
	
	static AsyncTask<Void, Void, Void> al;
	AsyncTask<Void, Void, Void> al_read;
	
	ArrayList<SSDK_Comments> result = new ArrayList<SSDK_Comments>();
	static ArrayList<SSDK_Comments> share_result = new ArrayList<SSDK_Comments>();
	ArrayList<SSDK_Comments> t_result = new ArrayList<SSDK_Comments>();//Комментарии последней загруженной страницы
	ArrayList<String> comments_sname = new ArrayList<String>();//Все имена спойлеров
	ArrayList<String> t_comments_sname = new ArrayList<String>();//Имена спойлеров последней загруженной страници
	
	//Адаптер для комментариев
	public class CommentsAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		
		public CommentsAdapter (Context ctx) {  
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
			    		convertView = mLayoutInflater.inflate(R.layout.w_item_comment, null);
					}else{
						convertView = mLayoutInflater.inflate(R.layout.d_item_comment, null);
					}
		           	
			        holder.titleView = (TextView) convertView.findViewById(R.id.lbl_user_title);
			        holder.infoView = (TextView) convertView.findViewById(R.id.lbl_comment);
			        holder.clientView = (TextView) convertView.findViewById(R.id.lbl_client);
			        holder.dateView = (TextView) convertView.findViewById(R.id.tv_time);
			        holder.userView = (ImageView) convertView.findViewById(R.id.img_user_ava);
				    holder.layoutView = (LinearLayout) convertView.findViewById(R.id.ll_comment_add);
			        holder.titleView.setText(result.get(position).user_name);
			        holder.newView = (TextView) convertView.findViewById(R.id.lbl_new);
			        holder.offtopicView = (TextView) convertView.findViewById(R.id.lbl_offtop);
			        convertView.setTag(holder);
			        
			    } else {
		            holder = (Functions.ViewHolder) convertView.getTag();
		            holder.layoutView.removeAllViews();
		        }
			    
			    convertView.findViewById(R.id.img_user_ava).setOnClickListener(new OnClickListener() {
	        		
		        	@Override
		            public void onClick(View v) {
		        		
		        		holder.userView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
	    				Intent intent_user = new Intent(getActivity(), Activity_UserProfile.class);
	    	    		intent_user.putExtra("id", result.get(position).user_id);
	    	    		startActivity(intent_user);
		        		
			  	    }
		        });
			    
			    convertView.findViewById(R.id.lbl_new).setOnClickListener(new OnClickListener() {
		        	
		        	@Override
		            public void onClick(View v) {
		        		holder.newView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		        		al_read = new AsyncRead(result.get(position).id).execute();
		        		result.get(position).viewed = true;
		            }
		        });
			    
			    Functions.addComment(result.get(position).body_clean, comments_sname, holder.layoutView, getActivity(), getActivity(), true);

                holder.titleView.setText(result.get(position).user_name);
			    holder.dateView.setText(result.get(position).date);
			    if (result.get(position).viewed){
			    	holder.newView.setVisibility(View.GONE);
			    }else{
			    	holder.newView.setVisibility(View.VISIBLE);
			    }
			    if (!result.get(position).offtopic){
			    	holder.offtopicView.setVisibility(View.GONE);
			    }else{
			    	holder.offtopicView.setVisibility(View.VISIBLE);
			    }
			    
			    
			    ImageLoader.getInstance().displayImage(result.get(position).user_avatar, holder.userView);

			    if (result.get(position).body_clean.contains("[Отправлено с Android]")){
			    	holder.clientView.setText("[Отправлено с Android]");
			    	holder.clientView.setVisibility(View.VISIBLE);
			    } else {
			    	holder.clientView.setText("");
			    	holder.clientView.setVisibility(View.GONE);
			    }
		        return convertView;
		    }
		}
			
	//Инициализируем фрагмент
    @SuppressWarnings({ "unchecked", "deprecation" })
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Functions.getPreference(getActivity().getBaseContext());

    	if (Functions.preference.theme.equals("ligth")){
    		view = inflater.inflate(R.layout.w_activity_scroll_list, container, false);
		}else{
			view = inflater.inflate(R.layout.d_activity_scroll_list, container, false);
		}
    	                
        tv_load = (TextView) view.findViewById(R.id.tv_load);
        tv_load.setVisibility(View.GONE);
                
        //Основное окно
        lv_main = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
	    adapter_list = new CommentsAdapter(getActivity().getBaseContext());
	    actualListView = lv_main.getRefreshableView();
        registerForContextMenu(actualListView);
        actualListView.setAdapter(adapter_list);

	    lv_main.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view,
			          int position, long id) {

				      
		
			      }
			    });
	    
        
	    al = (AsyncLoading) getActivity().getLastNonConfigurationInstance();
	    if (al == null) {
	      al = new AsyncLoading();
	    }
	    
	    lv_main.setOnScrollListener(new OnScrollListener() {
	        public void onScrollStateChanged(AbsListView view, int scrollState) {

	        }

	        public void onScroll(AbsListView view, int firstVisibleItem,
	            int visibleItemCount, int totalItemCount) {
	        	boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
	        	if (loadMore && al.getStatus() == AsyncTask.Status.PENDING && !stop_update) {
		        	page_no = page_no + 1;
		        	tv_load.setVisibility(View.VISIBLE);
		        	al = new AsyncLoading(false).execute();
	    		}
	        }
	    });
	    
	    lv_main.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				page_no = 1;
				iii=0;
				result.clear();
				comments_sname.clear();
				adapter_list.notifyDataSetChanged();

				if (Functions.isNetwork(getActivity().getBaseContext())) al = new AsyncLoading().execute();
			}
		});
	    
	    if ((savedInstanceState != null)) {
			comments_sname = savedInstanceState.getStringArrayList("comments_sname");
			t_comments_sname = savedInstanceState.getStringArrayList("t_comments_sname");
            //result = (ArrayList<SSDK_Comments>) savedInstanceState.getSerializable("result");
            //t_result = (ArrayList<SSDK_Comments>) savedInstanceState.getSerializable("t_result");
            result = (ArrayList<SSDK_Comments>) savedInstanceState.getSerializable("result");
            t_result = (ArrayList<SSDK_Comments>) savedInstanceState.getSerializable("t_result");
			share_result = result;

			iii = savedInstanceState.getInt("iii");
			page_no = savedInstanceState.getInt("page_no");
			stop_update = savedInstanceState.getBoolean("stop_update");
			adapter_list.notifyDataSetChanged();
		}else{
			iii = 0;
			page_no = 1;
			stop_update = true;
			
			comments_sname.clear();
			t_comments_sname.clear();
			result.clear();
			t_result.clear();
			adapter_list.notifyDataSetChanged();
		}
        return view;
    }  
    
    //Создание контекстного меню
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		Functions.createContextMenu(menu, result.get(info.position-1));
		super.onCreateContextMenu(menu, v, menuInfo);
    }
	
	//Обработка выбора контекстного меню
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    Intent intent = Functions.choiseContextItem(item, result.get(info.position-1), "comment",
	    		Fragment_Club_Info.thread_id, getActivity().getBaseContext(), lv_main);
	    if (intent != null){
	    	startActivityForResult(intent, Constants.UPDATE_TOPIC);
	    }
	    return true;
	}
	
	//Обработка ответов от других активити
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
   		if (requestCode == Constants.UPDATE_TOPIC) {
  			if (resultCode == 1) {
  				lv_main.setRefreshing(true);
  			}
  		}
  	}
	
	//Асинхронное удаление комментария
  	class CommentDelete extends AsyncTask<Void, Void, Void> {
  		String kawai;
  		String comment_id;
  	
  		public CommentDelete (String comment_id, String kawai) {  
  			this.kawai = kawai;
  			this.comment_id = comment_id;

  		}
  		
  		@Override
  		protected void onPreExecute() {
  			
  		}
  						  
  		protected void onPostExecute(Void result1) {
  			lv_main.setRefreshing(true);
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			String[] token = SSDK_API.getToken(kawai);
  			try {
  				Response res = Jsoup
  						.connect("http://shikimori.org/comments/" + comment_id)
  						.ignoreContentType(true)
  						.header("X-CSRF-Token", token[0])
  						.cookie("_kawai_session", token[1])
  						.data("_method","delete")
  						.method(Method.POST)
  						.execute();
  				res.statusCode();
  					
  			} catch (IOException e) {
  					// TODO Auto-generated catch block
  				e.printStackTrace();
  			}	
  			return null;
  		}				
  	}
  	
    //Асинхронная загрузка комментариев
  	private class AsyncLoading extends AsyncTask<Void, Void, Void> {
  		private Boolean update;
  		
  		public AsyncLoading(Boolean update) {
	        this.update = update;
	    }
  		
  		public AsyncLoading() {
	        this.update = true;
	    }
  		
  		@Override
  		protected void onPreExecute() {
			tv_load.setVisibility(View.VISIBLE);
			if (this.update){
				iii = 0;
				page_no = 1;
				stop_update = true;
				
				comments_sname.clear();
				t_comments_sname.clear();
				result.clear();
				t_result.clear();
				adapter_list.notifyDataSetChanged();
			}
  		}
  	  
  		protected void onPostExecute(Void result1) {
  			
  			result.addAll(t_result);
  			share_result = result;
  			comments_sname.addAll(t_comments_sname);
  			tv_load.setVisibility(View.GONE);
  			al = new AsyncLoading();
  			adapter_list.notifyDataSetChanged();
  			lv_main.onRefreshComplete();
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			try {
  				t_comments_sname.clear();
				//t_result = SSDK_API.getComments(Fragment_Club_Info.thread_id, "Entry", 20, page_no, Functions.preference.kawai);
                t_result = SSDK_API.getComments_new(Fragment_Club_Info.thread_id, "Entry", 20, page_no, Functions.preference.kawai);

                for (int i=0; i<t_result.size();i++){
					String temp = t_result.get(i).body_html;
			        
			        Document json = Jsoup.parse(temp);

					t_comments_sname.addAll(t_result.get(i).spoiler_names);
				
					for(@SuppressWarnings("unused") Element element : json.select("div[class=inner]")){
					    iii++;
					}
					
				}
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
  	
  //Асинхроннай задача на пометку прочтения
  	private class AsyncRead extends AsyncTask<Void, Void, Void> {
  		private String n_id;


  	    public AsyncRead(String n_id) {
  	        this.n_id = n_id;
  	    }
  		@Override
  		protected void onPreExecute() {
  			getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
  		}
  				  
  		protected void onPostExecute(Void result1) {
  			adapter_list.notifyDataSetChanged();
  			getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			SSDK_API.readComment(n_id);
  			SSDK_API.getUnread(Functions.preference.kawai);
  			return null;
  		}

  		
  	}
  	
  	@Override
	public void onResume() {
  		share_result = result;
        super.onResume();
    }
  	
	//Сохранение значений для восстановления после различных событий
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
        outState.putStringArrayList("comments_sname", comments_sname);
        outState.putStringArrayList("t_comments_sname", t_comments_sname);
        outState.putSerializable("result", result);
        outState.putSerializable("t_result", t_result);

		
        outState.putInt("iii", iii);
        outState.putInt("page_no", page_no);
        outState.putBoolean("stop_update", stop_update);
        
    }
 
}