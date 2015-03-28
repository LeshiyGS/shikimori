package ru.gslive.shikimori.org.v2;


import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.koushikdutta.ion.Ion;

public class Fragment_Club_Info extends SherlockFragment {
 	
	ImageView iv_logo;
	TextView tv_title;
	LinearLayout ll_info, ll_load;
	
	Context context;
	View view;
	
	PullToRefreshScrollView mPullRefreshScrollView;
	static AsyncTask<Void, Void, Void> al;
	
	int iii=0;
	
	SSDK_Club result = new SSDK_Club();
	// Переменные хранящие ИД для комментарие
	static String thread_id = "";
	String t_thread_id = "";
	static Boolean in_club = false;
	Boolean t_in_club = false;
	static String club_id = "";
	String t_club_id = "";
	
	//Названия спойлеров
	ArrayList<String> club_sname = new ArrayList<String>();
	
	//Создаем УИ фрагмента
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	context = getActivity();
    	//Чтение настроек
    	Functions.getPreference(getActivity().getBaseContext());

    	if (Functions.preference.theme.equals("ligth")){
    		view = inflater.inflate(R.layout.w_fragment_club_info, container, false);
		}else{
			view = inflater.inflate(R.layout.d_fragment_club_info, container, false);
		}
    
        iv_logo = (ImageView) view.findViewById(R.id.iv_club_logo);
        ll_load = (LinearLayout) view.findViewById(R.id.ll_load);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        ll_info = (LinearLayout) view.findViewById(R.id.ll_info);
               
        mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
        mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if (Functions.isNetwork(getActivity().getBaseContext()))  al = new AsyncLoading(false).execute();
			}
		});
             
		al = (AsyncLoading) getActivity().onRetainNonConfigurationInstance();
        if (al == null) {
          al = new AsyncLoading(false);
        }
        
        if ((savedInstanceState != null)) {
			club_sname = savedInstanceState.getStringArrayList("club_sname");
			result = (SSDK_Club) savedInstanceState.getSerializable("result");
			t_thread_id = savedInstanceState.getString("thread_id");
			thread_id = t_thread_id;
			t_club_id = savedInstanceState.getString("club_id");
			club_id = t_club_id;
			t_in_club = savedInstanceState.getBoolean("in_club");
			in_club = t_in_club;
			
			iii = savedInstanceState.getInt("iii");
			
			if (result.id != null){
				tv_title.setText(result.name);

                Ion.with(iv_logo)
                        //.placeholder(R.drawable.ic_launcher)
                        .animateLoad(R.anim.spin_animation)
                        .error(R.drawable.missing_preview)
                        .load(result.image_original);
	  			//AQuery aq = new AQuery(getActivity().getBaseContext());
	  			//aq.id(iv_logo).image(result.image_original, false, false);

                Functions.addComment(result.description_clean, club_sname, ll_info, getActivity(), getActivity(), true);
                //Functions.comment_add_with_link(ll_info, result.description_clean, club_sname, context);
	  			
	  			Activity_Club.actionbar.setSubtitle(result.name);
				ll_load.setVisibility(View.GONE);
				
			}else{
				if (Functions.isNetwork(getActivity().getBaseContext()))  al = new AsyncLoading(true).execute();
			}
		}else{
			if (Functions.isNetwork(getActivity().getBaseContext()))  al = new AsyncLoading(true).execute();
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
  		thread_id = t_thread_id;
  		club_id = t_club_id;
  		in_club = t_in_club;
        super.onResume();
    }

    //Асинхронная задача Информации о клубе
  	private class AsyncLoading extends AsyncTask<Void, Void, Void> { 		
 		private Boolean update;
 		
 		public AsyncLoading(Boolean update) {
	        this.update = update;
	    }
 		
  		@Override
  		protected void onPreExecute() {
  			//((SherlockFragmentActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
  			ll_load.setVisibility(View.VISIBLE);
  			ll_info.removeAllViews();
  		}
  	  
  		protected void onPostExecute(Void result1) {
  			tv_title.setText(result.name);
            Ion.with(iv_logo)
                    //.placeholder(R.drawable.ic_launcher)
                    .animateLoad(R.anim.spin_animation)
                    .error(R.drawable.missing_preview)
                    .load(result.image_original);

            Functions.addComment(result.description_clean, club_sname, ll_info, getActivity(), getActivity(), true);
  			
  			Activity_Club.actionbar.setSubtitle(result.name);

  			mPullRefreshScrollView.onRefreshComplete();

  			if (this.update && Fragment_Club_Comments.al.getStatus() != AsyncTask.Status.RUNNING){
  				Fragment_Club_Comments.al.execute();
  			}
  			al = new AsyncLoading(false);
  			Fragment_Club_Menu.in_list_adapter.notifyDataSetChanged();
  			//((SherlockFragmentActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
  			ll_load.setVisibility(View.GONE);
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			result = SSDK_API.getClub(Activity_Club.target_id, Functions.preference.kawai);
  			String temp = result.description_html;
	        
	        Document json = Jsoup.parse(temp);
			//Разбор спойлеров

	        club_sname.addAll(result.spoiler_names);
			
			for(@SuppressWarnings("unused") Element element : json.select("div[class=inner]")){
			    iii++;
			}
			
			thread_id = result.thread_id;
			t_thread_id = result.thread_id;
			
			in_club = result.inGroup;
			t_in_club = result.inGroup;
			Log.d("In Club", "-> " + result.inGroup);
			
			club_id = result.id;
			t_club_id = result.id;
			
  			return null;
  		}

  		
    }
    
  	//Сохряняем значения для их восстановления после разных событий
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
        outState.putStringArrayList("club_sname", club_sname);
        outState.putSerializable("result", result);
        outState.putInt("iii", iii);  
        outState.putString("thread_id", t_thread_id);
        outState.putBoolean("in_club", t_in_club);
        outState.putString("club_id", t_club_id);
    }
        	
}