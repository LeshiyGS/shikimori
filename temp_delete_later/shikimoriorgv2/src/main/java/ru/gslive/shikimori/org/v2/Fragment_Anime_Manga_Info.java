package ru.gslive.shikimori.org.v2;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.koushikdutta.ion.Ion;

public class Fragment_Anime_Manga_Info extends SherlockFragment {
 	
	//http://translate.google.ru/translate_a/t?client=x&text=eng text&hl=en&sl=en&tl=ru
	
	ImageView iv_preview;
	ImageButton ib_translate;
	TextView tv_title;
	TextView tv_tom;
	TextView tv_info;
	TextView tv_info_translate;
	TextView tv_rating;
	RatingBar rb_shiki;
	RatingBar rb_my_shikimori;
	LinearLayout ll_load;
	View view;
		
	PullToRefreshScrollView mPullRefreshScrollView;
	
	public static AsyncTask<Void, Void, Void> al;
	public static AsyncTask<Void, Void, Void> tr;
	
	SSDK_Anime result = new SSDK_Anime(); 
	SSDK_Manga result_m = new SSDK_Manga();
	
	static String thread_id;
	String t_thread_id;

	//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//Чтение настроек
    	Functions.getPreference(getActivity().getBaseContext());
    	if (Functions.preference.theme.equals("ligth")){
    		view = inflater.inflate(R.layout.w_fragment_anime_manga_info, container, false);
		}else{
			view = inflater.inflate(R.layout.d_fragment_anime_manga_info, container, false);
		}
        
        tv_title = (TextView) view.findViewById(R.id.lbl_title);
        ll_load = (LinearLayout) view.findViewById(R.id.ll_load);
		iv_preview = (ImageView) view.findViewById(R.id.img_manga);
		ib_translate = (ImageButton) view.findViewById(R.id.ib_translate);
		tv_tom = (TextView) view.findViewById(R.id.lbl_tom_value);
		tv_info = (TextView) view.findViewById(R.id.txt_info);
		tv_info_translate = (TextView) view.findViewById(R.id.txt_info_translate);
		tv_info_translate.setVisibility(View.GONE);
		tv_info.setMovementMethod(LinkMovementMethod.getInstance());
		tv_rating = (TextView) view.findViewById(R.id.lbl_rating);
		rb_shiki = (RatingBar) view.findViewById(R.id.rb_shikimori);
		rb_shiki.isIndicator();
		
		mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if (Functions.isNetwork(getActivity().getBaseContext()))  al = new AsyncLoading(false).execute();
			}
		});
		
		ib_translate.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		//Log.d("descriptions","-> " + result.description);
        		//Log.d("descriptions","-> " + result_m.description);
        		if (Activity_AnimeManga.target_type.equals("anime")){
        			tr = new AsyncTranslate(result.description).execute();
      			}else{
      				tr = new AsyncTranslate(result_m.description).execute();
      			}
        		
        	}
        });
		
		iv_preview.setOnClickListener(new OnClickListener() {
        	@SuppressLint("InlinedApi")
			@Override
        	public void onClick(View view) {
        		final Dialog mSplashDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar);
        		//mSplashDialog.requestWindowFeature((int) window.FEATURE_NO_TITLE);
        		mSplashDialog.setContentView(R.layout.fullscreen_image);
        		ImageView iv = (ImageView) mSplashDialog.findViewById(R.id.iv_fullScreen);
        		iv.setOnClickListener(new OnClickListener() {
                	@Override
                	public void onClick(View view) {
                		mSplashDialog.cancel();
                	}
                });
 		        if (Activity_AnimeManga.target_type.equals("anime")){
                    Ion.with(iv)
                            .animateLoad(R.anim.spin_animation)
                            .error(R.drawable.missing_preview)
                            .load(result.image_original);
                    //aq.id(iv).progress(R.id.progress).image(result.image_original, false, true);
 		        }else{
                    Ion.with(iv)
                            .animateLoad(R.anim.spin_animation)
                            .error(R.drawable.missing_preview)
                            .load(result_m.image_original);
                    //aq.id(iv).progress(R.id.progress).image(result_m.image_original, false, true);
 		        }
        		//mSplashDialog.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        		mSplashDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        		mSplashDialog.setCancelable(true);
        		mSplashDialog.show();


        	}
        });
		
		al = (AsyncLoading) getActivity().onRetainNonConfigurationInstance();
        if (al == null) {
          al = new AsyncLoading(false);
        }
			        
		if ((savedInstanceState != null)) {
			result = (SSDK_Anime) savedInstanceState.getSerializable("result");
			result_m = (SSDK_Manga) savedInstanceState.getSerializable("result_m");
			t_thread_id = savedInstanceState.getString("t_thread_id");
			thread_id = t_thread_id;
			String status;
	    	if (Activity_AnimeManga.target_type.equals("anime")){
	    		if (result.description.contains(".*\\<[^>]+>.*")){
		    		ib_translate.setVisibility(View.GONE);
		    	}
	    		if (!result.ongoing){
	    			result.episodes_aired = result.episodes;
	    			status = "Вышло";
	    		}else{
	    			status = "Выходит";
	    		}
	    		if(result.anons){
	    			status = "Анонс";
	    			result.episodes_aired = "0";
	    		}
	    		
	    		Functions.setFirstTitle(tv_title, result.russian, result.name);
		    	if (!result.russian.equals("null")){
		        	Activity_AnimeManga.actionbar.setSubtitle(result.name + " / " + result.russian);
		        }else{
		        	Activity_AnimeManga.actionbar.setSubtitle(result.name);
		        }
		        
		        tv_info.setText(Html.fromHtml(result.description_html));

                Ion.with(iv_preview)
                        .animateLoad(R.anim.spin_animation)
                        .error(R.drawable.missing_preview)
                        .load(result.image_original);
		        //aq.id(iv_preview).progress(R.id.progress).image(result.image_original, false, true);
		        tv_rating.setText("Оценка: " + result.score);
		        rb_shiki.setRating(Float.valueOf(result.score)/2);
				tv_tom.setText(Html.fromHtml("<b>Тип:</b>&nbsp;" + result.kind + "<br /><b>Эпизоды:</b>&nbsp;" + result.episodes_aired + " / " + result.episodes +
									"<br /><b>Длительность эпизода:</b>&nbsp;" + result.duration+ "&nbsp;мин." + "<br /><b>Статус:</b>&nbsp;" + status + 
									"<br /><b>Рейтинг:</b>&nbsp;" + result.rating + "<br /><b>Жанры:</b>&nbsp;" + result.genres + "<br /><b>Студии:</b>&nbsp;" + result.studios));
				if (result.description.contains("а")||result.description.contains("о")
						||result.description.contains("е")||result.description.contains("и")
						||result.description.contains("у")||result.description.contains("я")){
					ib_translate.setVisibility(View.GONE);
				}
	    	}else{
	        	if (result_m.description.contains(".*\\<[^>]+>.*")){
		    		ib_translate.setVisibility(View.GONE);
		    	}
	        	if (!result_m.ongoing){
					status = "Вышло";
				}else{
					status = "Выходит";
				}
				if(result_m.anons){
					status = "Анонс";
					result_m.chapters = "0";
				}
				
				Functions.setFirstTitle(tv_title, result_m.russian, result_m.name);
	        	if (!result_m.russian.equals("null")){
		        	Activity_AnimeManga.actionbar.setSubtitle(result_m.name + " / " + result_m.russian);
		        }else{
		        	Activity_AnimeManga.actionbar.setSubtitle(result_m.name);
		        }
		        
		        tv_info.setText(Html.fromHtml(result_m.description_html));

                Ion.with(iv_preview)
                        .animateLoad(R.anim.spin_animation)
                        .error(R.drawable.missing_preview)
                        .load(result_m.image_original);
		        //aq.id(iv_preview).progress(R.id.progress).image(result_m.image_original, false, true);
		        tv_rating.setText("Оценка: " + result_m.score);
		        rb_shiki.setRating(Float.valueOf(result_m.score)/2);
				tv_tom.setText(Html.fromHtml("<b>Тип:</b>&nbsp;" + result_m.kind + "<br /><b>Тома:</b>&nbsp;" + result_m.volumes +
									"<br /><b>Главы:</b>&nbsp;" + result_m.chapters +"<br /><b>Статус:</b>&nbsp;" + status + 
									"<br/><b>Жанры:</b>&nbsp;" + result_m.genres + "<br /><b>Издатель:</b>&nbsp;" + result_m.publishers));
				if (result_m.description.contains("а")||result_m.description.contains("о")
						||result_m.description.contains("е")||result_m.description.contains("и")
						||result_m.description.contains("у")||result_m.description.contains("я")){
					ib_translate.setVisibility(View.GONE);
				}
	    	}
	    	ll_load.setVisibility(View.GONE);
		}else{
			if (Functions.isNetwork(getActivity().getBaseContext()))  al = new AsyncLoading(true).execute();
		}
		
        return view;
    }
 
    private class AsyncLoading extends AsyncTask<Void, Void, Void> {
  		Boolean update = true;
  		
  		AsyncLoading (Boolean update) {  
			this.update = update;
		}
  		
  		@Override
  		protected void onPreExecute() {
  			ll_load.setVisibility(View.VISIBLE);
  		}
  	  
  		protected void onPostExecute(Void result1) {
  			Activity_AnimeManga.actionbar.setSubtitle(result.name);
  			//Отображаем инфу
  			String status;
	    	if (Activity_AnimeManga.target_type.equals("anime")){
	    		if (result.description.contains(".*\\<[^>]+>.*")){
		    		ib_translate.setVisibility(View.GONE);
		    	}
	    		if (!result.ongoing){
	    			result.episodes_aired = result.episodes;
	    			status = "Вышло";
	    		}else{
	    			status = "Выходит";
	    		}
	    		if(result.anons){
	    			status = "Анонс";
	    			result.episodes_aired = "0";
	    		}
	    		
	    		Functions.setFirstTitle(tv_title, result.russian, result.name);
		    	if (!result.russian.equals("null")){
		        	Activity_AnimeManga.actionbar.setSubtitle(result.name + " / " + result.russian);
		        }else{
		        	Activity_AnimeManga.actionbar.setSubtitle(result.name);
		        }
		        
		        tv_info.setText(Html.fromHtml(result.description_html));

                Ion.with(iv_preview)
                        .animateLoad(R.anim.spin_animation)
                        .error(R.drawable.missing_preview)
                        .load(result.image_original);
		        //aq.id(iv_preview).progress(R.id.progress).image(result.image_original, false, true);
		        tv_rating.setText("Оценка: " + result.score);
		        rb_shiki.setRating(Float.valueOf(result.score)/2);
				tv_tom.setText(Html.fromHtml("<b>Тип:</b>&nbsp;" + result.kind + "<br /><b>Эпизоды:</b>&nbsp;" + result.episodes_aired + " / " + result.episodes +
									"<br /><b>Длительность эпизода:</b>&nbsp;" + result.duration+ "&nbsp;мин." + "<br /><b>Статус:</b>&nbsp;" + status + 
									"<br /><b>Рейтинг:</b>&nbsp;" + result.rating + "<br /><b>Жанры:</b>&nbsp;" + result.genres + "<br /><b>Студии:</b>&nbsp;" + result.studios));
				if (result.description.contains("а")||result.description.contains("о")
						||result.description.contains("е")||result.description.contains("и")
						||result.description.contains("у")||result.description.contains("я")){
					ib_translate.setVisibility(View.GONE);
				}
	    	}else{
	    		Log.d(Constants.LogTag, "-> "+ result_m.description);
	        	if (result_m.description.contains(".*\\<[^>]+>.*")){
		    		ib_translate.setVisibility(View.GONE);
		    	}
	        	if (!result_m.ongoing){
					status = "Вышло";
				}else{
					status = "Выходит";
				}
				if(result_m.anons){
					status = "Анонс";
					result_m.chapters = "0";
				}
				
				Functions.setFirstTitle(tv_title, result_m.russian, result_m.name);
	        	if (!result_m.russian.equals("null")){
		        	Activity_AnimeManga.actionbar.setSubtitle(result_m.name + " / " + result_m.russian);
		        }else{
		        	Activity_AnimeManga.actionbar.setSubtitle(result_m.name);
		        }
		        
		        tv_info.setText(Html.fromHtml(result_m.description_html));


                Ion.with(iv_preview)
                        .animateLoad(R.anim.spin_animation)
                        .error(R.drawable.missing_preview)
                        .load(result_m.image_original);
		        //aq.id(iv_preview).progress(R.id.progress).image(result_m.image_original, false, true);
		        tv_rating.setText("Оценка: " + result_m.score);
		        rb_shiki.setRating(Float.valueOf(result_m.score)/2);
				tv_tom.setText(Html.fromHtml("<b>Тип:</b>&nbsp;" + result_m.kind + "<br /><b>Тома:</b>&nbsp;" + result_m.volumes +
									"<br /><b>Главы:</b>&nbsp;" + result_m.chapters +"<br /><b>Статус:</b>&nbsp;" + status + 
									"<br/><b>Жанры:</b>&nbsp;" + result_m.genres + "<br /><b>Издатель:</b>&nbsp;" + result_m.publishers));
				if (result_m.description.contains("а")||result_m.description.contains("о")
						||result_m.description.contains("е")||result_m.description.contains("и")
						||result_m.description.contains("у")||result_m.description.contains("я")){
					ib_translate.setVisibility(View.GONE);
				}
	    	}
	    	
	    	
  			mPullRefreshScrollView.onRefreshComplete();
  			if (this.update){
  				Fragment_Anime_Manga_Comments.al.execute();
  			}
  			ll_load.setVisibility(View.GONE);
 		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			if (Activity_AnimeManga.target_type.equals("anime")){
  				result = SSDK_API.getAnime(Activity_AnimeManga.target_id, Functions.preference.kawai);
  				thread_id = result.thread_id;
  	  			t_thread_id = result.thread_id;
  			}else{
  				result_m = SSDK_API.getManga(Activity_AnimeManga.target_id, Functions.preference.kawai);
  				thread_id = result_m.thread_id;
  	  			t_thread_id = result_m.thread_id;
  			}
  			return null;
  		}

  		
    }
     
    private class AsyncTranslate extends AsyncTask<Void, Void, Void> {
  		String text = "";
  		String tr_text = "";
  		
  		AsyncTranslate (String text) {  
			this.text = text;
		}
  		
  		@Override
  		protected void onPreExecute() {

  		}
  	  
  		protected void onPostExecute(Void result1) {
  			tv_info_translate.setText(Html.fromHtml("<b>Перевод: </b><br>" + tr_text));
  			tv_info_translate.setVisibility(View.VISIBLE);
  			ib_translate.setVisibility(View.GONE);
 		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			ArrayList<String> temp = SSDK_API.GoogleTranslate(text);
  			for(int i=0; i< temp.size(); i++){
  				tr_text = tr_text + temp.get(i) + " ";
  			}
  			//tr_text.replaceAll("\r", "<br>");
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
  		thread_id = t_thread_id;
        super.onResume();
    }
    
  	//
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
        outState.putSerializable("result_m", result_m);
        outState.putSerializable("result", result);
        outState.putString("thread_id", t_thread_id);
        outState.putString("t_thread_id", t_thread_id);
    }
}