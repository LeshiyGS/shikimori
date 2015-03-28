package ru.gslive.shikimori.org.v2;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
 
public class Fragment_Anime_Manga_Menu extends SherlockFragment {
 
	static float temp_rating;
	static String url;
	
	Boolean in_list = false;
	int what_list = -1;
	Boolean isFull = false;
	Boolean in_favourites = false;
	String all_episods = "0";
	String see_episods = "0";
	static float my_score = 0;
	Boolean first = true;
	
	SSDK_Anime anime = new SSDK_Anime();
	SSDK_Manga manga = new SSDK_Manga();
		
	TextView tv_all_episods, tv_my_score, tv_rewatch;
	EditText et_see_episods, et_rewatch, et_text;
	static RatingBar rb_my_score;
	Spinner sp_my_status;
	LinearLayout ll_in_list, ll_text, ll_rewatch;
	ScrollView ll_anime_manga_menu;
	View_GridView gv_in_list, gv_addon_menu;
	Button btn_plus, btn_minus, btn_re_plus, btn_re_minus, btn_save_text;
	View view;
	
	//Массивы списков
	String[] status;
	
	String[] in_list_no = new String[]{"ДОБАВИТЬ В СПИСОК","ДОБАВИТЬ В ИЗБРАННОЕ"};
	String[] in_list_yes = new String[]{"УДАЛИТЬ ИЗ СПИСКА","УДАЛИТЬ ИЗ ИЗБРАННОГО"};
	
	String[] addon = new String[]{"СВЯЗАННОЕ", "АДАПТАЦИИ", "ПОХОЖЕЕ", "ПЕРСОНАЖИ", "СОЗДАТЕЛИ", "СМОТРЕТЬ НА VK", "ПОИСК OST"};
	String[] addon_m = new String[]{"СВЯЗАННОЕ", "АДАПТАЦИИ", "ПОХОЖЕЕ", "ПЕРСОНАЖИ", "СОЗДАТЕЛИ", "ЧИТАТЬ", "СКАЧАТЬ"};
	
	String[] addon_lite = new String[]{"СВЯЗАННОЕ", "АДАПТАЦИИ", "ПОХОЖЕЕ", "ПЕРСОНАЖИ", "СОЗДАТЕЛИ"};
	String[] addon_m_lite = new String[]{"СВЯЗАННОЕ", "АДАПТАЦИИ", "ПОХОЖЕЕ", "ПЕРСОНАЖИ", "СОЗДАТЕЛИ"};
	
	@SuppressWarnings("rawtypes")
	Class[] activity_anime = { Activity_AnimeManga_Relation.class, Activity_AnimeManga_Relation.class, Activity_AnimeManga_Similar.class, Activity_AnimeManga_Characters.class, Activity_AnimeManga_Characters.class, Activity_Anime_VKSearch.class, Activity_OST_List.class};
	@SuppressWarnings("rawtypes")
	Class[] activity_manga = { Activity_AnimeManga_Relation.class, Activity_AnimeManga_Relation.class, Activity_AnimeManga_Similar.class, Activity_AnimeManga_Characters.class, Activity_AnimeManga_Characters.class, Activity_MangaCharacter.class, Activity_MangaSave.class};
	
	
	AMInListAdapter in_list_adapter;
	AMAddonAdapter addon_adapter;
	StatusListAdapter my_status_adapter;
	
	static AsyncTask<Void, Void, Void> al;
	AsyncTask<Void, Void, Void> change_list;
	AsyncTask<Void, Void, Void> change_ep;
	AsyncTask<Void, Void, Void> change_rewatch;
	AsyncTask<Void, Void, Void> change_text;
	AsyncTask<Void, Void, Void> add;
	AsyncTask<Void, Void, Void> add_favour;
		

	public class StatusListAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
	
		public StatusListAdapter (Context ctx) {  
		      mLayoutInflater = LayoutInflater.from(ctx);  
		}
	
		@Override
		public int getCount() {
			return status.length; // длина массива
		}
	
		@Override
		public Object getItem(int position) {
			return status[position];
		}
	
		@Override
		public long getItemId(int position) {
			return 0;
		}
	
		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;
			if (Functions.preference.theme.equals("ligth")){
				convertView = mLayoutInflater.inflate(R.layout.w_item_list, null);
			}else{
				convertView = mLayoutInflater.inflate(R.layout.d_item_list, null);
			}
			holder = new Functions.ViewHolder();
			holder.itemView = (TextView) convertView.findViewById(R.id.tv_list_item);
			convertView.setTag(holder);
			
			
			holder.itemView.setText(status[position]);
	
			return convertView;
		}
	}	
	
	//Адаптер меню
	public class AMInListAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public AMInListAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}

		@Override
		public int getCount() {
			return in_list_no.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;
			
			if (Functions.preference.theme.equals("ligth")){
				convertView = mLayoutInflater.inflate(R.layout.w_item_profile_menu, null);
			}else{
				convertView = mLayoutInflater.inflate(R.layout.d_item_profile_menu, null);
			}
			
					
			holder = new Functions.ViewHolder();
			holder.itemView = (TextView) convertView.findViewById(R.id.tv_profile_menu_item);
			holder.newView = (TextView) convertView.findViewById(R.id.tv_item_new);
			convertView.setTag(holder);
								
			if (position == 0){
				if (!in_list) holder.itemView.setText(in_list_no[position]); else holder.itemView.setText(in_list_yes[position]);
				holder.newView.setText("");
			}else{
				if (!in_favourites) holder.itemView.setText(in_list_no[position]); else holder.itemView.setText(in_list_yes[position]);
				holder.newView.setText("");
			}
			return convertView;
		}
	}
	
	//Адаптер меню
	public class AMAddonAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public AMAddonAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}

		@Override
		public int getCount() {
			if (isFull){
				if (Activity_AnimeManga.target_type.equals("anime")){
					return addon.length;
				}else{
					return addon_m.length;	
				}
			}else{
				if (Activity_AnimeManga.target_type.equals("anime")){
					return addon_lite.length;
				}else{
					return addon_m_lite.length;
				}
			}
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint({ "ViewHolder", "InflateParams" })
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;
			
			if (Functions.preference.theme.equals("ligth")){
				convertView = mLayoutInflater.inflate(R.layout.w_item_profile_menu, null);
			}else{
				convertView = mLayoutInflater.inflate(R.layout.d_item_profile_menu, null);
			}
			
			holder = new Functions.ViewHolder();
			holder.itemView = (TextView) convertView.findViewById(R.id.tv_profile_menu_item);
			holder.newView = (TextView) convertView.findViewById(R.id.tv_item_new);
			convertView.setTag(holder);
			
			if (Activity_AnimeManga.target_type.equals("anime")){
				holder.itemView.setText(addon[position]);
			}else{
				holder.itemView.setText(addon_m[position]);
			}
			
			holder.newView.setText("");
			
			return convertView;
		}
	}
		
	//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//Чтение настроек
    	Functions.getPreference(getActivity().getBaseContext());

    	PackageManager pm = getActivity().getPackageManager();
    	PackageInfo pi;
		try {
			pi = pm.getPackageInfo("ru.gslive.shikimori.org.v2.full", 0);
			if (pi != null) {
	    	    isFull = true;
	    	}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (Functions.preference.theme.equals("ligth")){
			view = inflater.inflate(R.layout.w_fragment_anime_manga_menu, container, false);
		}else{
			view = inflater.inflate(R.layout.d_fragment_anime_manga_menu, container, false);
		}
        
        ll_in_list = (LinearLayout) view.findViewById(R.id.ll_in_list);
        ll_text = (LinearLayout) view.findViewById(R.id.ll_text);
        ll_rewatch = (LinearLayout) view.findViewById(R.id.ll_rewatch);
        ll_anime_manga_menu = (ScrollView) view.findViewById(R.id.ll_anime_manga_menu);
                
        tv_all_episods = (TextView) view.findViewById(R.id.tv_all_episods);
        tv_rewatch = (TextView) view.findViewById(R.id.tv_rewatch);
        et_see_episods = (EditText) view.findViewById(R.id.et_see_episods);
        et_rewatch = (EditText) view.findViewById(R.id.et_rewatch);
        et_text = (EditText) view.findViewById(R.id.et_text);
        rb_my_score = (RatingBar) view.findViewById(R.id.rb_my_score);
        
        tv_my_score = (TextView) view.findViewById(R.id.tv_my_score);
        
        btn_save_text = (Button) view.findViewById(R.id.btn_save_text);
        btn_plus = (Button) view.findViewById(R.id.btn_plus);
        btn_minus = (Button) view.findViewById(R.id.btn_minus);
        btn_re_plus = (Button) view.findViewById(R.id.btn_re_plus);
        btn_re_minus = (Button) view.findViewById(R.id.btn_re_minus);
        
        if (Activity_AnimeManga.target_type.endsWith("anime")){
        	tv_rewatch.setText("Кол-во повторных просмотров:");
        }else{
        	tv_rewatch.setText("Кол-во повторных прочтений:");
        }
        
        gv_in_list = (View_GridView) view.findViewById(R.id.gv_in_list);
        in_list_adapter = new AMInListAdapter(getActivity().getBaseContext());
        gv_in_list.setAdapter(in_list_adapter);
        gv_in_list.setExpanded(true);
        
        gv_addon_menu = (View_GridView) view.findViewById(R.id.gv_addon_menu);
        addon_adapter = new AMAddonAdapter(getActivity().getBaseContext());
        gv_addon_menu.setAdapter(addon_adapter);
        gv_addon_menu.setExpanded(true);
        
        if (Activity_AnimeManga.target_type.equals("anime")){
        	status = getActivity().getBaseContext().getResources().getStringArray(R.array.anime_list);
        }else{
        	status = getActivity().getBaseContext().getResources().getStringArray(R.array.manga_list);
        }
        my_status_adapter = new StatusListAdapter(getActivity().getBaseContext());
        sp_my_status = (Spinner) view.findViewById(R.id.sp_my_status);
        sp_my_status.setAdapter(my_status_adapter);
        
        btn_save_text.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (Functions.isNetwork(getActivity().getBaseContext())){
	        		if (Activity_AnimeManga.target_type.equals("anime")){
	           			change_text = new AsyncChangeText(et_text.getText().toString()).execute();
	            	}else{
	           			change_text = new AsyncChangeText(et_text.getText().toString()).execute();
	            	}
        		}
        	}
        });
        
        btn_plus.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (Functions.isNetwork(getActivity().getBaseContext())){
	        		if (Activity_AnimeManga.target_type.equals("anime")){
	            		if (!anime.user_episodes.equals(anime.episodes)){
            				change_ep = new AsyncChangeEp(String.valueOf(Integer.parseInt(et_see_episods.getText().toString())+1)).execute();
	            		}
	            	}else{
	            		if (!manga.user_chapters.equals(manga.chapters)){
	           				change_ep = new AsyncChangeEp(String.valueOf(Integer.parseInt(et_see_episods.getText().toString())+1)).execute();
	            		}
	            	}
	        		view.setEnabled(false);
        		}
        	}
        });
        
        btn_minus.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (Functions.isNetwork(getActivity().getBaseContext())){
	        		if (Activity_AnimeManga.target_type.equals("anime")){
	        			if (!anime.user_episodes.equals("0")){
        					change_ep = new AsyncChangeEp(String.valueOf(Integer.parseInt(et_see_episods.getText().toString())-1)).execute();
	        			}
	        		}else{
	        			if (!manga.user_chapters.equals("0")){
        					change_ep = new AsyncChangeEp(String.valueOf(Integer.parseInt(et_see_episods.getText().toString())-1)).execute();
	        			}
	        		}
	        		view.setEnabled(false);
        		}
        	}
        });
        
        btn_re_plus.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (Functions.isNetwork(getActivity().getBaseContext())){
	        		if (Activity_AnimeManga.target_type.equals("anime")){
	        			//Пересмотел аниме
            			change_rewatch = new AsyncChangeRewatch(String.valueOf(Integer.parseInt(et_rewatch.getText().toString())+1)).execute();
	            	}else{
	            		//Перечитал мангу
	            		change_rewatch = new AsyncChangeRewatch(String.valueOf(Integer.parseInt(et_rewatch.getText().toString())+1)).execute();
	            	}
        		}
        		view.setEnabled(false);
        	}
        });
        
        btn_re_minus.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (Functions.isNetwork(getActivity().getBaseContext()))
        			if (Activity_AnimeManga.target_type.equals("anime")){
        			//Пересмотел аниме
        				if (!anime.rewatches.equals("0"))
        					change_rewatch = new AsyncChangeRewatch(String.valueOf(Integer.parseInt(et_rewatch.getText().toString())-1)).execute();
        			}else{
        			//Перечитал мангу
        				if (!manga.rewatches.equals("0")){
        					change_rewatch = new AsyncChangeRewatch(String.valueOf(Integer.parseInt(et_rewatch.getText().toString())-1)).execute();
        			}
        			view.setEnabled(false);
        		}
        	}
        });
       
        al = new AsyncLoading();
        
        et_see_episods.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            	if (actionId == EditorInfo.IME_ACTION_SEND) {
            		if (Functions.isNetwork(getActivity().getBaseContext()))
            			if (Activity_AnimeManga.target_type.equals("anime")){
            				change_ep = new AsyncChangeEp(String.valueOf(Integer.parseInt(v.getText().toString()))).execute();
            			}else{
            				change_ep = new AsyncChangeEp(String.valueOf(Integer.parseInt(v.getText().toString()))).execute();
            			}
        			
        			Activity_AnimeManga.imm.hideSoftInputFromWindow( et_see_episods.getWindowToken(), 0);
                }
                return false;
            }
        });
        
        rb_my_score.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){

        	@SuppressWarnings("deprecation")
			@Override
        	public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        		if (fromUser){
        			temp_rating = rating;
        			if (Activity_AnimeManga.target_type.equals("anime")){
        				url = anime.url;
        			}else{
        				url = manga.url;
        			}
        			getActivity().showDialog(Constants.IDD_CHANCHE_SCORE);
        			
        	    }
        	}});
        
        sp_my_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?> parent,
        			View itemSelected, int selectedItemPosition, long selectedId) {
        		int spos = 0;
        		if (!first){
        			if (Functions.isNetwork(getActivity().getBaseContext()))
        				if (selectedItemPosition == 5){
        					spos = 9;
        				}else{
        					spos = selectedItemPosition;
        				}
        				if (Activity_AnimeManga.target_type.equals("anime")){
        					change_list = new AsyncChangeList(spos).execute();
        				}else{
        					change_list = new AsyncChangeList(spos).execute();
        				}
        		}
        		first = false;
        	}
        	public void onNothingSelected(AdapterView<?> parent) {
        	}
        });
        
        gv_in_list.setOnItemClickListener(new OnItemClickListener() {
		      @SuppressWarnings("deprecation")
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  if (Activity_AnimeManga.target_type.equals("anime")){
		    		  url = anime.url;
		    	  }else{
		    		  url = manga.url;
		    	  }
		    	  switch (position) {
		    	  	case 0: 
		    	  		if (in_list){
		    	  			getActivity().showDialog(Constants.IDD_DEL_ITEM);
		    	  		}else{
		    	  			if (Activity_AnimeManga.target_type.equals("anime")){
		    	  				if (Functions.isNetwork(getActivity().getBaseContext()))
		    	  					add = new AsyncAdd(anime.id,"Anime").execute();
		    	  			}else{
		    	  				if (Functions.isNetwork(getActivity().getBaseContext()))
		    	  					add = new AsyncAdd(manga.id,"Manga").execute();
		    	  			}
				    	}
		    	  		break;
		    	  	case 1:
		    	  		if (in_favourites){
		    	  			getActivity().showDialog(Constants.IDD_DEL_FAV);
		    	  		}else{
		    	  			if (Activity_AnimeManga.target_type.equals("anime")){
		    	  				if (Functions.isNetwork(getActivity().getBaseContext()))
		    	  					add_favour = new AsyncAddFavour("http://shikimori.org" + anime.url).execute();
		    	  			}else{
		    	  				if (Functions.isNetwork(getActivity().getBaseContext()))
		    	  					add_favour = new AsyncAddFavour("http://shikimori.org" + manga.url).execute();
		    	  			}
				    	}
		    	  		break;
		    	  	default:
		    	  		break;
		    	  }

		      }
		    });
        
        gv_addon_menu.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  if (Activity_AnimeManga.target_type.equals("anime")){
			    	  Intent intent_addon = new Intent(getActivity().getBaseContext(), activity_anime[position]);
			    	  intent_addon.putExtra("title", anime.name);
			    	  intent_addon.putExtra("type", "animes");
			    	  intent_addon.putExtra("id", anime.id);
			    	  if (position == 1){
			    		  intent_addon.putExtra("adaptation", true);
			    	  }else{
			    		  intent_addon.putExtra("adaptation", false);
			    	  }
			    	  if (position == 4){
			    		  intent_addon.putExtra("character", false);
			    	  }else{
			    		  intent_addon.putExtra("character", true);
			    	  }
			    	  if (anime.user_episodes.equals(anime.episodes)){
			    		  intent_addon.putExtra("episodes", "0");
			    	  }else{
			    		  intent_addon.putExtra("episodes", anime.user_episodes);
			    	  }
			    	  startActivity(intent_addon);		      	  
		    	  }else{
		    		  Intent intent_m_addon = new Intent(getActivity().getBaseContext(), activity_manga[position]);
		    		  intent_m_addon.putExtra("title", manga.name);
		    		  intent_m_addon.putExtra("type", "mangas");
			    	  intent_m_addon.putExtra("id", manga.id);
			    	  intent_m_addon.putExtra("read_manga", manga.read_manga);
			    	  if (position == 1){
			    		  intent_m_addon.putExtra("adaptation", true);
			    	  }else{
			    		  intent_m_addon.putExtra("adaptation", false);
			    	  }
			    	  if (position == 4){
			    		  intent_m_addon.putExtra("character", false);
			    	  }else{
			    		  intent_m_addon.putExtra("character", true);
			    	  }
			      	  startActivity(intent_m_addon); 
		    	  }
		      }
		    });
        
        if ((savedInstanceState != null)) {
        	anime = (SSDK_Anime) savedInstanceState.getSerializable("anime");
        	manga = (SSDK_Manga) savedInstanceState.getSerializable("manga");
        	in_list = savedInstanceState.getBoolean("in_list");
        	what_list = savedInstanceState.getInt("what_list");
        	in_favourites = savedInstanceState.getBoolean("in_favourites");
        	all_episods = savedInstanceState.getString("all_episods");
        	see_episods = savedInstanceState.getString("see_episods");
        	my_score = savedInstanceState.getFloat("my_score");
        	
        	if (Activity_AnimeManga.target_type.equals("anime")){
	  			if (anime.episodes.equals("0") ) tv_all_episods.setText("/ ?"); else tv_all_episods.setText("/ " + anime.episodes);	
	  		    et_see_episods.setText(anime.user_episodes);
	  		    et_rewatch.setText(anime.rewatches);
	  		    et_text.setText(anime.text);
	   		    
	  		    if (anime.user_list == -1) in_list = false; else in_list = true;
	  		    what_list = anime.user_list;
	  		    sp_my_status.setSelection(what_list);
	  		  
	  		    in_favourites = anime.favoured;
	  		    if (!anime.user_score.equals("null")) my_score = Float.parseFloat(anime.user_score); else my_score = 0;
	  		    rb_my_score.setRating(my_score/2);
	  		    if (my_score == 0)tv_my_score.setText("Моя оценка: -"); else tv_my_score.setText("Моя оценка: " + my_score);
	  		      		    
	  		    if (anime.user_list >= 0){
	  		    	sp_my_status.setSelection(anime.user_list);
	  		    	ll_in_list.setVisibility(View.VISIBLE);
	  		    	ll_text.setVisibility(View.VISIBLE);
	  		    }else{
	  		    	ll_in_list.setVisibility(View.GONE);
	  		    	ll_text.setVisibility(View.GONE);
	  		    }
	  		    if (anime.user_list == 2){
	  		    	ll_rewatch.setVisibility(View.VISIBLE);
	  		    }else{
	  		    	ll_rewatch.setVisibility(View.GONE);
	  		    }
  			}else{
  				if (manga.chapters.equals("0") ) tv_all_episods.setText("/ ?"); else tv_all_episods.setText("/ " + manga.chapters);	
	  		    et_see_episods.setText(manga.user_chapters);
	  		    et_rewatch.setText(manga.rewatches);
	  		    et_text.setText(manga.text);
	   		    
	  		    if (manga.user_list == -1) in_list = false; else in_list = true;
	  		    what_list = manga.user_list;
	  		    sp_my_status.setSelection(what_list);
	  		  
	  		    in_favourites = manga.favoured;
	  		    if (!manga.user_score.equals("null")) my_score = Float.parseFloat(manga.user_score); else my_score = 0;
	  		    rb_my_score.setRating(my_score/2);
	  		    if (my_score == 0)tv_my_score.setText("Моя оценка: -"); else tv_my_score.setText("Моя оценка: " + my_score);
	  		      		    
	  		    if (manga.user_list >= 0){
	  		    	sp_my_status.setSelection(manga.user_list);
	  		    	ll_in_list.setVisibility(View.VISIBLE);
	  		    	ll_text.setVisibility(View.VISIBLE);
	  		    }else{
	  		    	ll_in_list.setVisibility(View.GONE);
	  		    	ll_text.setVisibility(View.GONE);
	  		    }
	  		  if (manga.user_list == 2){
	  		    	ll_rewatch.setVisibility(View.VISIBLE);
	  		    }else{
	  		    	ll_rewatch.setVisibility(View.GONE);
	  		    }
  			}

  		    my_status_adapter.notifyDataSetChanged();
  		    in_list_adapter.notifyDataSetChanged(); 
  		    ll_anime_manga_menu.setVisibility(View.VISIBLE); 
		}else{
			ll_anime_manga_menu.setVisibility(View.GONE);
			if (Functions.isNetwork(getActivity().getBaseContext())) al = new AsyncLoading().execute();
		}
        
        
        
        return view;
        
        
    }
        
    @Override
	public void onResume() {
  		al = new AsyncLoading();
        super.onResume();
    }
    
    //Сохранение значения Асинхронной задачи
  	public Object onRetainNonConfigurationInstance() {
  		return al;
  	}
    
    //Загрузка Информации
  	private class AsyncLoading extends AsyncTask<Void, Void, Void> {

  		@Override
  		protected void onPreExecute() {
  			anime = new SSDK_Anime();
  			manga = new SSDK_Manga();
  			ll_anime_manga_menu.setVisibility(View.GONE);
  		}
  	  
  		protected void onPostExecute(Void result1) {
  			try {
	  			if (Activity_AnimeManga.target_type.equals("anime")){
	  				
	  				Activity_AnimeManga.target_rate = anime.user_rate_id;
	  				
		  			if (anime.episodes.equals("0") ) tv_all_episods.setText("/ ?"); else tv_all_episods.setText("/ " + anime.episodes);	
		  		    et_see_episods.setText(anime.user_episodes);
		  		    et_rewatch.setText(anime.rewatches);
		  		    et_text.setText(anime.text);
		   		    
		  		    if (anime.user_list == -1) in_list = false; else in_list = true;
		  		    what_list = anime.user_list;
		  		    sp_my_status.setSelection(what_list);
		  		  
		  		    in_favourites = anime.favoured;
		  		    if (!anime.user_score.equals("null")) my_score = Float.parseFloat(anime.user_score); else my_score = 0;
		  		    rb_my_score.setRating(my_score/2);
		  		    if (my_score == 0)tv_my_score.setText("Моя оценка: -"); else tv_my_score.setText("Моя оценка: " + my_score);
		  		      		    
		  		    if (anime.user_list >= 0){
		  		    	sp_my_status.setSelection(anime.user_list);
		  		    	ll_in_list.setVisibility(View.VISIBLE);
		  		    	ll_text.setVisibility(View.VISIBLE);
		  		    }else{
		  		    	ll_in_list.setVisibility(View.GONE);
		  		    	ll_text.setVisibility(View.GONE);
		  		    }
		  		    if (anime.user_list == 2 || anime.user_list == 9){
		  		    	ll_rewatch.setVisibility(View.VISIBLE);
		  		    }else{
		  		    	ll_rewatch.setVisibility(View.GONE);
		  		    }
	  			}else{
	  				Activity_AnimeManga.target_rate = manga.user_rate_id;
	  				
	  				if (manga.chapters.equals("0") ) tv_all_episods.setText("/ ?"); else tv_all_episods.setText("/ " + manga.chapters);	
		  		    et_see_episods.setText(manga.user_chapters);
		  		    et_rewatch.setText(manga.rewatches);
		  		    et_text.setText(manga.text);
		   		    
		  		    if (manga.user_list == -1) in_list = false; else in_list = true;
		  		    what_list = manga.user_list;
		  		    sp_my_status.setSelection(what_list);
		  		  
		  		    in_favourites = manga.favoured;
		  		    if (!manga.user_score.equals("null")) my_score = Float.parseFloat(manga.user_score); else my_score = 0;
		  		    rb_my_score.setRating(my_score/2);
		  		    if (my_score == 0)tv_my_score.setText("Моя оценка: -"); else tv_my_score.setText("Моя оценка: " + my_score);
		  		      		    
		  		    if (manga.user_list >= 0){
		  		    	sp_my_status.setSelection(manga.user_list);
		  		    	ll_in_list.setVisibility(View.VISIBLE);
		  		    	ll_text.setVisibility(View.VISIBLE);
		  		    }else{
		  		    	ll_in_list.setVisibility(View.GONE);
		  		    	ll_text.setVisibility(View.GONE);
		  		    }
		  		    if (manga.user_list == 2){
		  		    	ll_rewatch.setVisibility(View.VISIBLE);
		  		    }else{
		  		    	ll_rewatch.setVisibility(View.GONE);
		  		    }
		  		    
		  		    if (manga.read_manga.equals("null")){
		  		    	addon_m = addon_m_lite;
		  		    }
	  			}
	
	  		    my_status_adapter.notifyDataSetChanged();
	  		    in_list_adapter.notifyDataSetChanged();  		    
	  		    
	  		    al = new AsyncLoading();
	  		    
	  		    btn_plus.setEnabled(true);
	  		    btn_minus.setEnabled(true);
	  		    btn_re_plus.setEnabled(true);
			    btn_re_minus.setEnabled(true);
			    
			    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
		        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
		  		}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  			ll_anime_manga_menu.setVisibility(View.VISIBLE); 
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			if (Activity_AnimeManga.target_type.equals("anime")){
  				anime = SSDK_API.getAnime(Activity_AnimeManga.target_id, Functions.preference.kawai);
  			}else{
  				manga = SSDK_API.getManga(Activity_AnimeManga.target_id, Functions.preference.kawai);
  			}
  			return null;
  		}
    }
 	
    //Изменения списка
  	private class AsyncChangeList extends AsyncTask<Void, Void, Void> {
  		int list;
  		
  		public AsyncChangeList(int list) {
  			this.list = list;
  		} 
  			  
  		@Override
  		protected void onPreExecute() {
  			
  		}
  			  
  		protected void onPostExecute(Void result1) {
  			try {
  				if (Functions.isNetwork(getActivity().getBaseContext())) al = new AsyncLoading().execute();
  			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
  			}
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			if (Activity_AnimeManga.target_type.equals("anime")){
  				SSDK_API.setAnimeMangaRate("Anime", anime.user_rate_id, list, anime.user_episodes, anime.user_score, anime.rewatches, anime.text);
  			} else {
  				SSDK_API.setAnimeMangaRate("Manga", manga.user_rate_id, list, manga.user_chapters, manga.user_score, manga.rewatches, manga.text);
  			}
  			return null;
  		}
  	}
      
  	//Добавление в список
  	private class AsyncAdd extends AsyncTask<Void, Void, Void> {
		String target_id;
		String target_type;
		
		
		public AsyncAdd(String target_id, String target_type) {
	        this.target_id = target_id;
	        this.target_type = target_type;
	    }
		
		@Override
		protected void onPreExecute() {

		}
		  
		protected void onPostExecute(Void result1) {
			if (Functions.isNetwork(getActivity().getBaseContext())) al = new AsyncLoading().execute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			SSDK_API.setAnimeMangaAdd(target_id, Functions.preference.user_id, target_type, 0);
			return null;
		}
	}
			
	//Добавление в избранное (Надо перевести на API)
  	private class AsyncAddFavour extends AsyncTask<Void, Void, Void> {
		String link;
		
		public AsyncAddFavour(String link) {
	        this.link = link;

	    }  
		
		
		@Override
		protected void onPreExecute() {

		}
		  
		protected void onPostExecute(Void result1) {
			if (Functions.isNetwork(getActivity().getBaseContext())) al = new AsyncLoading().execute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			String fav_link;
			if (Activity_AnimeManga.target_type.equals("anime")){
				fav_link = "http://shikimori.org/favourites/Anime" + link.substring(link.lastIndexOf("/"));
			}else{
				fav_link = "http://shikimori.org/favourites/Manga" + link.substring(link.lastIndexOf("/"));
			}
			try {
				String[] token = SSDK_API.getToken(Functions.preference.kawai);
				Response res = Jsoup
					.connect(fav_link)
					.header("X-CSRF-Token", token[0])
					.ignoreContentType(true)
					.cookie("_kawai_session", token[1])
					.method(Method.POST)
					.execute();
				if (res.statusCode() == 200){
  					//Log.d("ListChange", "OK");
  				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

			
	 }
		
	//Изменение количества эпизодов
	private class AsyncChangeEp extends AsyncTask<Void, Void, Void> {
  		String ep;
  		
  		public AsyncChangeEp(String ep) {
  			this.ep = ep;
  		}
  		  
  		@Override
  		protected void onPreExecute() {
  			ll_anime_manga_menu.setVisibility(View.GONE);
  		}
  		  
  		protected void onPostExecute(Void result1) {
  			if (Functions.isNetwork(getActivity().getBaseContext())) al = new AsyncLoading().execute();
  			ll_anime_manga_menu.setVisibility(View.VISIBLE);
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			if (Activity_AnimeManga.target_type.equals("anime")){
  				SSDK_API.setAnimeMangaRate("Anime", anime.user_rate_id, anime.user_list, ep, anime.user_score, anime.rewatches, anime.text);
  			} else {
  				SSDK_API.setAnimeMangaRate("Manga", manga.user_rate_id, manga.user_list, ep, manga.user_score, manga.rewatches, manga.text);
  			}
  			return null;
  		}
  	}
	
	//Изменение количества просмотров
	private class AsyncChangeRewatch extends AsyncTask<Void, Void, Void> {
	  		String rewatch;
	  		
	  		public AsyncChangeRewatch(String rewatch) {
	  			this.rewatch = rewatch;
	  		}
	  		  
	  		@Override
	  		protected void onPreExecute() {
	  			ll_anime_manga_menu.setVisibility(View.GONE);
	  		}
	  		  
	  		protected void onPostExecute(Void result1) {
	  			if (Functions.isNetwork(getActivity().getBaseContext())) al = new AsyncLoading().execute();
	  			ll_anime_manga_menu.setVisibility(View.VISIBLE);
	  		}

	  		@Override
	  		protected Void doInBackground(Void... arg0) {
	  			if (Activity_AnimeManga.target_type.equals("anime")){
	  				SSDK_API.setAnimeMangaRate("Anime", anime.user_rate_id, anime.user_list, anime.user_episodes, anime.user_score, rewatch, anime.text);
	  			} else {
	  				SSDK_API.setAnimeMangaRate("Manga", manga.user_rate_id, manga.user_list, manga.user_chapters, manga.user_score, rewatch, manga.text);
	  			}
	  			return null;
	  		}
	  	}
	
	//Изменение Текста
	private class AsyncChangeText extends AsyncTask<Void, Void, Void> {
		String text;
		  		
		public AsyncChangeText(String text) {
			this.text = text;
		}
		  		  
		@Override
		protected void onPreExecute() {
			ll_anime_manga_menu.setVisibility(View.GONE);
		}
		  		  
		protected void onPostExecute(Void result1) {
			if (Functions.isNetwork(getActivity().getBaseContext())) al = new AsyncLoading().execute();
			ll_anime_manga_menu.setVisibility(View.VISIBLE);
				Toast.makeText(getActivity().getBaseContext(), "Заметка сохранена.", Toast.LENGTH_SHORT).show();
		}

		  		@Override
		  		protected Void doInBackground(Void... arg0) {
					if (Activity_AnimeManga.target_type.equals("anime")){
						SSDK_API.setAnimeMangaRate("Anime", anime.user_rate_id, anime.user_list, anime.user_episodes, anime.user_score, anime.rewatches, text);
					} else {
						SSDK_API.setAnimeMangaRate("Manga", manga.user_rate_id, manga.user_list, manga.user_chapters, manga.user_score, manga.rewatches, text);
					}
		  			return null;
		  		}
		  	}
	
	//
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
        outState.putSerializable("anime", anime);
        outState.putSerializable("manga", manga);
        outState.putBoolean("in_list", in_list);
        outState.putInt("what_list", what_list);
        outState.putBoolean("in_favourites", in_favourites);
        outState.putString("all_episods", all_episods);
        outState.putString("see_episods", see_episods);
        outState.putFloat("my_score", my_score);
        
    }
 
}