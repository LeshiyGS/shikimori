package ru.gslive.shikimori.org.v2;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
 
public class Fragment_UserStatistic_Anime extends SherlockFragment {
 		
		
	PullToRefreshScrollView mPullRefreshScrollView;
	
	public static AsyncTask<Void, Void, Void> al;
	View view;
		
	static LinearLayout ll_score;
	static LinearLayout ll_types;
	static LinearLayout ll_ratings;
	static TextView tv_genres;
	static TextView tv_studios;
	static LinearLayout sv_activity;
	static ScrollView sv_main;
	static LinearLayout ll_load;
	
	static String thread_id;
	String t_thread_id;

	//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//Чтение настроек
    	Functions.getPreference(getActivity().getBaseContext());

    	if (Functions.preference.theme.equals("ligth")){
    		view = inflater.inflate(R.layout.w_fragment_user_statistic_anime, container, false);
  	  	}else{
  	  	 	view = inflater.inflate(R.layout.d_fragment_user_statistic_anime, container, false);
  	  	}	
        
        
        ll_load = (LinearLayout) view.findViewById(R.id.ll_load);
        sv_main = (ScrollView) view.findViewById(R.id.sv_main);
        
        ll_score = (LinearLayout) view.findViewById(R.id.ll_score);
		ll_types = (LinearLayout) view.findViewById(R.id.ll_types);
		ll_ratings = (LinearLayout) view.findViewById(R.id.ll_ratings);
		tv_genres = (TextView) view.findViewById(R.id.tv_genres);
		tv_studios = (TextView) view.findViewById(R.id.tv_studios);
		sv_activity = (LinearLayout) view.findViewById(R.id.sv_activity);
		/*mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if (Functions.isNetwork(getActivity().getBaseContext()))  al = new AsyncLoading(false).execute();
			}
		});*/
		if (savedInstanceState != null) {
			setStatistic(getActivity().getBaseContext());
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
    
  	@SuppressWarnings("deprecation")
	static void setStatistic(Context context){
  		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
  		//
  		if (Activity_UserStat.result.anime_scores.size() != 0) {
	  		int smax = 0;
	  		LinearLayout sll1 = new LinearLayout(context);
	  		LinearLayout sll2 = new LinearLayout(context);
	  		for (int i = 0; i < Activity_UserStat.result.anime_scores.size(); i++){
	  			if (smax < Activity_UserStat.result.anime_scores.get(i)){
	  				smax = Activity_UserStat.result.anime_scores.get(i);
	  			}
	  		}
	  		Log.d("size", "->"+Activity_UserStat.result.anime_scores.size());
	  		
	  		for (int i = 0; i < Activity_UserStat.result.anime_scores.size(); i++){
				sll1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
				sll1.setOrientation(LinearLayout.VERTICAL);
	
				sll2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				sll2.setOrientation(LinearLayout.VERTICAL);
								
				TextView tv = new TextView(context);
				tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(metrics.density*20)));
				tv.setText(Activity_UserStat.result.anime_scores_name.get(i));
				if (Functions.preference.theme.equals("ligth")){
					tv.setTextColor(context.getResources().getColor(R.color.black));
		  	  	}else{
		  	  		tv.setTextColor(context.getResources().getColor(R.color.gray_ligth));
		  	  	}

				tv.setTypeface(null, Typeface.BOLD);
				tv.setPadding(5, 0, 0, 0);
				tv.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
				
				ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
				if (Functions.preference.theme.equals("ligth")){
					pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.w_statistic_progressbar));
		  	  	}else{
		  	  		pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.d_statistic_progressbar));
		  	  	}
				pb.setMax(smax);
				pb.setProgress(Activity_UserStat.result.anime_scores.get(i));
				pb.setPadding(0, 2, 0, 2);
				pb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(metrics.density*20)));
								
				sll1.addView(pb);
				sll2.addView(tv);
			}
	  		ll_score.addView(sll1);
	  		ll_score.addView(sll2);
  		}else{
  			TextView tv = new TextView(context);
			tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			tv.setText("Недостаточно данных.");
			if (Functions.preference.theme.equals("ligth")){
				tv.setTextColor(context.getResources().getColor(R.color.black));
	  	  	}else{
	  	  		tv.setTextColor(context.getResources().getColor(R.color.gray_ligth));
	  	  	}
			ll_score.addView(tv);
  		}
  		
  		
  		if (Activity_UserStat.result.anime_scores.size() != 0) {
	  		int tmax = 0;
	  		LinearLayout tll1 = new LinearLayout(context);
	  		LinearLayout tll2 = new LinearLayout(context);
	  		for (int i = 0; i < Activity_UserStat.result.anime_types.size(); i++){
	  			if (tmax < Activity_UserStat.result.anime_types.get(i)){
	  				tmax = Activity_UserStat.result.anime_types.get(i);
	  			}
	  		}
	  		
			for (int i = 0; i < Activity_UserStat.result.anime_types.size(); i++){
				tll1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
				tll1.setOrientation(LinearLayout.VERTICAL);
	
				tll2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				tll2.setOrientation(LinearLayout.VERTICAL);
								
				TextView tv = new TextView(context);
				tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(metrics.density*20)));
				tv.setText(Activity_UserStat.result.anime_types_name.get(i));
				if (Functions.preference.theme.equals("ligth")){
					tv.setTextColor(context.getResources().getColor(R.color.black));
		  	  	}else{
		  	  		tv.setTextColor(context.getResources().getColor(R.color.gray_ligth));
		  	  	}
				tv.setTypeface(null, Typeface.BOLD);
				tv.setPadding(5, 0, 0, 0);
				tv.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
				
				ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
				if (Functions.preference.theme.equals("ligth")){
					pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.w_statistic_progressbar));
		  	  	}else{
		  	  		pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.d_statistic_progressbar));
		  	  	}
				pb.setMax(tmax);
				pb.setProgress(Activity_UserStat.result.anime_types.get(i));
				pb.setPadding(0, 2, 0, 2);
				pb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(metrics.density*20)));
								
				tll1.addView(pb);
				tll2.addView(tv);
			}
			ll_types.addView(tll1);
	  		ll_types.addView(tll2);
  		}else{
  			TextView tv = new TextView(context);
			tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			tv.setText("Недостаточно данных.");
			if (Functions.preference.theme.equals("ligth")){
				tv.setTextColor(context.getResources().getColor(R.color.black));
	  	  	}else{
	  	  		tv.setTextColor(context.getResources().getColor(R.color.gray_ligth));
	  	  	}
			ll_types.addView(tv);
  		}
  		
  		if (Activity_UserStat.result.anime_scores.size() != 0) {
	  		int rmax = 0;
	  		LinearLayout rll1 = new LinearLayout(context);
	  		LinearLayout rll2 = new LinearLayout(context);
	  		for (int i = 0; i < Activity_UserStat.result.anime_ratings.size(); i++){
	  			if (rmax < Activity_UserStat.result.anime_ratings.get(i)){
	  				rmax = Activity_UserStat.result.anime_ratings.get(i);
	  			}
	  		}
	  		
			for (int i = 0; i < Activity_UserStat.result.anime_ratings.size(); i++){
				rll1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
				rll1.setOrientation(LinearLayout.VERTICAL);
	
				rll2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				rll2.setOrientation(LinearLayout.VERTICAL);
								
				TextView tv = new TextView(context);
				tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(metrics.density*20)));
				tv.setText(Activity_UserStat.result.anime_ratings_name.get(i));
				if (Functions.preference.theme.equals("ligth")){
					tv.setTextColor(context.getResources().getColor(R.color.black));
		  	  	}else{
		  	  		tv.setTextColor(context.getResources().getColor(R.color.gray_ligth));
		  	  	}
				tv.setTypeface(null, Typeface.BOLD);
				tv.setPadding(5, 0, 0, 0);
				tv.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
				
				ProgressBar pb = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
				if (Functions.preference.theme.equals("ligth")){
					pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.w_statistic_progressbar));
		  	  	}else{
		  	  		pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.d_statistic_progressbar));
		  	  	}
				pb.setMax(rmax);
				pb.setProgress(Activity_UserStat.result.anime_ratings.get(i));
				pb.setPadding(0, 2, 0, 2);
				pb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)(metrics.density*20)));
								
				rll1.addView(pb);
				rll2.addView(tv);
			}
			ll_ratings.addView(rll1);
	  		ll_ratings.addView(rll2);
  		}else{
  			TextView tv = new TextView(context);
			tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			tv.setText("Недостаточно данных.");
			if (Functions.preference.theme.equals("ligth")){
				tv.setTextColor(context.getResources().getColor(R.color.black));
	  	  	}else{
	  	  		tv.setTextColor(context.getResources().getColor(R.color.gray_ligth));
	  	  	}
			ll_ratings.addView(tv);
  		}
  		
  		tv_genres.setText(Html.fromHtml(Activity_UserStat.result.agenres));
  		tv_studios.setText(Html.fromHtml(Activity_UserStat.result.studios));

  		if (Activity_UserStat.result.activity.size() != 0) {
	  		for (int i = 0; i < Activity_UserStat.result.activity.size(); i++){
								
				ImageView iv = new ImageView(context);
				iv.setLayoutParams(new LinearLayout.LayoutParams((int)(metrics.density*17), Activity_UserStat.result.activity.get(i) ));
				iv.setBackgroundColor(context.getResources().getColor(R.color.blue_dark_shiki));
				
				ImageView iv2 = new ImageView(context);
				iv2.setLayoutParams(new LinearLayout.LayoutParams(4,4));
				
				sv_activity.addView(iv);
				sv_activity.addView(iv2);
			}
  		}else{
  			TextView tv = new TextView(context);
			tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			tv.setText("Недостаточно данных для формирования статистики.");
			if (Functions.preference.theme.equals("ligth")){
				tv.setTextColor(context.getResources().getColor(R.color.black));
	  	  	}else{
	  	  		tv.setTextColor(context.getResources().getColor(R.color.gray_ligth));
	  	  	}
			sv_activity.addView(tv);
  		}
  		
  		ll_load.setVisibility(View.GONE);
  		sv_main.setVisibility(View.VISIBLE);
  	}
  	
  	
  	//
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);

    }
}