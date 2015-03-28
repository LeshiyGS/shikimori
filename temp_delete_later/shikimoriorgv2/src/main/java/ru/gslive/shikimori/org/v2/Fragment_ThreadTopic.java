package ru.gslive.shikimori.org.v2;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
 
public class Fragment_ThreadTopic extends SherlockFragment {
	
	int iii = 0;
	
	static AsyncTask<Void, Void, Void> al;
	View view;
		
	PullToRefreshScrollView mPullRefreshScrollView;
	
	TextView tv_title, tv_user, tv_date, tv_name, tv_status, tv_episodes, tv_comments_count;
	ImageView iv_user, iv_anime;
	LinearLayout ll_topic, ll_add_review, ll_review, ll_anime;
	RatingBar rb_storyline, rb_music, rb_overall, rb_characters, rb_animation;
	LinearLayout ll_load;
		
	SSDK_Topic result;

	//Создание
	@SuppressWarnings("deprecation")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		
    	Functions.getPreference(getActivity().getBaseContext());

    	if (Functions.preference.theme.equals("ligth")){
    		view = inflater.inflate(R.layout.w_fragment_threadtopic, container, false);
		}else{
			view = inflater.inflate(R.layout.d_fragment_threadtopic, container, false);
		}

        tv_title = (TextView) view.findViewById(R.id.lbl_title);
        tv_user = (TextView) view.findViewById(R.id.tv_user);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        ll_load = (LinearLayout) view.findViewById(R.id.ll_load);
        iv_user = (ImageView) view.findViewById(R.id.iv_user);
        ll_topic = (LinearLayout) view.findViewById(R.id.ll_comment_add);
        tv_comments_count = (TextView) view.findViewById(R.id.tv_comments_count);
        
        iv_anime = (ImageView) view.findViewById(R.id.iv_anime);
        iv_anime.setOnClickListener(new OnClickListener() {	
			@SuppressLint("DefaultLocale")
			@Override
            public void onClick(View v) {
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        		Intent intent_com = new Intent(getActivity().getBaseContext(), Activity_AnimeManga.class);
        		if (result.linked_type.equals("Anime") || result.linked_type.equals("Manga")){
        			intent_com.putExtra("id", result.linked_id);
        			intent_com.putExtra("type", result.linked_type.toLowerCase());
        		}else if (result.linked_type.equals("Review")){
        			intent_com.putExtra("id", result.linked_review_id);
        			intent_com.putExtra("type", result.linked_review_type.toLowerCase());
        		}
    	    	startActivity(intent_com);
	  	    }
	    });
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_status = (TextView) view.findViewById(R.id.tv_status);
        tv_episodes = (TextView) view.findViewById(R.id.tv_episodes);
        
        ll_add_review = (LinearLayout) view.findViewById(R.id.ll_add_review);
        ll_review = (LinearLayout) view.findViewById(R.id.ll_review);
        ll_anime = (LinearLayout) view.findViewById(R.id.ll_anime);
        
        rb_storyline = (RatingBar) view.findViewById(R.id.rb_storyline);
        rb_music = (RatingBar) view.findViewById(R.id.rb_music);
        rb_overall = (RatingBar) view.findViewById(R.id.rb_overall);
        rb_characters = (RatingBar) view.findViewById(R.id.rb_characters);
        rb_animation = (RatingBar) view.findViewById(R.id.rb_animation);
		
		mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if (Functions.isNetwork(getActivity().getBaseContext()))  al.execute();
			}
		});
		
		al = (AsyncLoading) getActivity().getLastNonConfigurationInstance();
	    if (al == null) {
	      al = new AsyncLoading();
	    }
		
		if ((savedInstanceState != null)) {
			result = (SSDK_Topic) savedInstanceState.getSerializable("result");
	    	iii = savedInstanceState.getInt("iii");

	    	Activity_Thread.actionbar.setSubtitle(result.title);
			tv_title.setText(result.title);
			tv_user.setText(result.user_name);
			tv_date.setText(result.date);

			ImageLoader.getInstance().displayImage(result.user_avatar, iv_user);
			
			if (result.linked_id.equals("")){
				//Functions.comment_add_with_link(ll_topic, result.body_clean, topic_sname, getActivity());
                Functions.addComment(result.body_clean, result.spoiler_names, ll_topic, getActivity(),getActivity(), true);

				ll_review.setVisibility(View.GONE);
				ll_anime.setVisibility(View.GONE);
			}else{
				if (result.linked_type.equals("Anime") || result.linked_type.equals("Manga")){
					ImageLoader.getInstance().displayImage(result.linked_image, iv_anime);
					
					if (result.linked_russian.equals("null"))
						tv_name.setText(result.linked_name);
					else
						tv_name.setText(result.linked_name +" / " + result.linked_russian);
					
					tv_status.setText(result.linked_status);
					tv_episodes.setText(result.linked_episodes);
					
					ll_review.setVisibility(View.GONE);
					ll_anime.setVisibility(View.VISIBLE);
				}
				
				if (result.linked_type.equals("Review")){
                    Functions.addComment(result.linked_body_clean, result.spoiler_names_linked, ll_add_review, getActivity(),getActivity(), true);
					//Functions.comment_add_with_link(ll_add_review, result.linked_body_clean, topic_sname, getActivity());
					ImageLoader.getInstance().displayImage(result.linked_image, iv_anime);
					
					if (result.linked_russian.equals("null"))
						tv_name.setText(result.linked_name);
					else
						tv_name.setText(result.linked_name +" / " + result.linked_russian);
					
					tv_status.setText(result.linked_status);
					tv_episodes.setText(result.linked_episodes);
					
					rb_storyline.setRating(((float)result.linked_storyline/2));
					rb_animation.setRating(((float)result.linked_animation/2));
					rb_characters.setRating(((float)result.linked_characters/2));
					rb_music.setRating(((float)result.linked_music/2));
					rb_overall.setRating(((float)result.linked_overall/2));
					ll_review.setVisibility(View.VISIBLE);
					ll_anime.setVisibility(View.VISIBLE);
				}					
			}
			ll_load.setVisibility(View.GONE);
	    	
		}else{
			if (Functions.isNetwork(getActivity().getBaseContext())) al.execute();
		}
	
        return view;
        
    }
          	  	 
	//Асинхронная задача подгрузки новостей
	private class AsyncLoading extends AsyncTask<Void, Void, Void> {
			  
				@Override
				protected void onPreExecute() {
					ll_load.setVisibility(View.VISIBLE);
					iii = 0;
					ll_topic.removeAllViews();
					ll_add_review.removeAllViews();
				}
			  
				protected void onPostExecute(Void result1) {
					Activity_Thread.actionbar.setSubtitle(result.title);
					tv_title.setText(result.title);
					tv_user.setText(result.user_name);
					tv_date.setText(result.date);
					
					ImageLoader.getInstance().displayImage(result.user_avatar, iv_user);
					
					tv_comments_count.setText("Всего комментариев: "+result.comments_count);

					//Functions.comment_add_with_link(ll_topic, result.body_clean, topic_sname, getActivity());
                    Functions.addComment(result.body_clean, result.spoiler_names, ll_topic, getActivity(),getActivity(), true);
					
					if (result.linked_id.equals("")){
						
						ll_review.setVisibility(View.GONE);
						ll_anime.setVisibility(View.GONE);
					}else{
						if (result.linked_type.equals("Anime") || result.linked_type.equals("Manga")){
							ImageLoader.getInstance().displayImage(result.linked_image, iv_anime);
							
							if (result.linked_russian.equals("null"))
								tv_name.setText(result.linked_name);
							else
								tv_name.setText(result.linked_name +" / " + result.linked_russian);
							
							tv_status.setText(result.linked_status);
							tv_episodes.setText(result.linked_episodes);
							
							ll_review.setVisibility(View.GONE);
							ll_anime.setVisibility(View.VISIBLE);
						}
						
						if (result.linked_type.equals("Review")){
                            Functions.addComment(result.linked_body_clean, result.spoiler_names_linked, ll_add_review, getActivity(),getActivity(), true);
							//Functions.comment_add_with_link(ll_add_review, result.linked_body_clean, topic_sname, getActivity());
							ImageLoader.getInstance().displayImage(result.linked_image, iv_anime);

							if (result.linked_russian.equals("null"))
								tv_name.setText(result.linked_name);
							else
								tv_name.setText(result.linked_name +" / " + result.linked_russian);
							
							tv_status.setText(result.linked_status);
							tv_episodes.setText(result.linked_episodes);
							
							rb_storyline.setRating(((float)result.linked_storyline/2));
							rb_animation.setRating(((float)result.linked_animation/2));
							rb_characters.setRating(((float)result.linked_characters/2));
							rb_music.setRating(((float)result.linked_music/2));
							rb_overall.setRating(((float)result.linked_overall/2));
							ll_review.setVisibility(View.VISIBLE);
							ll_anime.setVisibility(View.VISIBLE);
						}					
					}
					mPullRefreshScrollView.onRefreshComplete();
					al = new AsyncLoading();
					ll_load.setVisibility(View.GONE);
				}

				@Override
				protected Void doInBackground(Void... arg0) {
					result = SSDK_API.getTopic(Activity_Thread.target_id, Functions.preference.kawai, getActivity());
					SSDK_API.getUnread(Functions.preference.kawai);
    				return null;
				}

				
		  }
	
    //Сохранение значений элементов
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
        outState.putSerializable("result", result);
        outState.putInt("iii", iii);

    }
 
}