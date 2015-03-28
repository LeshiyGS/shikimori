package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
 
public class Fragment_UserProfile extends SherlockFragment {
		
	//Выводимые данные
	SSDK_User user_infos = new SSDK_User();
	String user_info = "";
	String[] anime_list;
	String[] manga_list;
		
	Boolean refresh = true;
	View view;
	
	static AsyncTask<Void, Void, Void> al;
	
	//Массивы списков
	static ArrayList<String> anime = new ArrayList<String>();
	static ArrayList<String> manga = new ArrayList<String>();
	
	String[] menu_content = new String[] { "ИСТОРИЯ", "КЛУБЫ", "ИЗБРАННОЕ", "ДРУЗЬЯ", "СТАТИСТИКА"};
	@SuppressWarnings("rawtypes")
	public Class[] mThumbActivity = { Activity_History.class, Activity_Clubs.class, Activity_Favourites.class, Activity_Friends.class, Activity_UserStat.class,};
	
	
	TextView tv_nickname, tv_user_info, tv_online, tv_anime_list, tv_manga_list;
	ImageView iv_avatar;
	View_GridView gv_history;
	View_GridView gv_menu;
	View_GridView gv_anime;
	View_GridView gv_manga;
	View_TextProgressBar pb_anime, pb_manga;
	
	PullToRefreshScrollView mPullRefreshScrollView;
	LinearLayout ll_load, ll_anime, ll_manga;
	
	//Асинхронная загрузка
	private AsyncLogin loader; //Task Получения данных
	
	PMenuAdapter adapter_profile_menu;
	//Адаптеры спинеров
	AnimeListAdapter adapter_anime;
	MangaListAdapter adapter_manga;
	PHistoryAdapter adapter_history;

	ArrayList<SSDK_History> history = new ArrayList<SSDK_History>();
	ArrayList<SSDK_History> t_history = new ArrayList<SSDK_History>();
	
	Context context;
	
	public class AnimeListAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public AnimeListAdapter (Context ctx) {  
		      mLayoutInflater = LayoutInflater.from(ctx);  
		}

		@Override
		public int getCount() {
			return anime.size(); // длина массива
		}

		@Override
		public Object getItem(int position) {
			return adapter_anime.getItem(position);
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
			
			
    		holder.itemView.setText(anime.get(position));

			return convertView;
		}
	}
	
	public class MangaListAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
	
		public MangaListAdapter (Context ctx) {  
		      mLayoutInflater = LayoutInflater.from(ctx);  
		}
	
		@Override
		public int getCount() {
			return manga.size(); // длина массива
		}
	
		@Override
		public Object getItem(int position) {
			return adapter_manga.getItem(position);
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
			
			
			holder.itemView.setText(manga.get(position));
	
			return convertView;
		}
	}

	
	//Адаптер меню
	public class PMenuAdapter extends BaseAdapter {
			private LayoutInflater mLayoutInflater;

			public PMenuAdapter (Context ctx) {  
			      mLayoutInflater = LayoutInflater.from(ctx);  
			}

			@Override
			public int getCount() {
				return menu_content.length; // длина массива
			}

			@Override
			public Object getItem(int position) {
				return menu_content[position];
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
				
				
	    		holder.itemView.setText(menu_content[position]);
    			holder.newView.setText("");

				return convertView;
			}
		}
	
	//Адаптер Истории
	public class PHistoryAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public PHistoryAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}

		public int getCount () {  
		      return history.size();  
		    }  
		          
		    public Object getItem (int position) {  
		      return position;  
		    }  
		          
		    public long getItemId (int position) {  
		      return position;  
		    }  
		          
		    public String getString (int position) {  
		      return history.get(position).id;  
		    }  
		    
		    @SuppressLint("InflateParams")
			public View getView(int position, View convertView, ViewGroup parent) {   
		    	final Functions.ViewHolder holder;
			      
		    	if (convertView == null) {
		    		holder = new Functions.ViewHolder();
		    		if (Functions.preference.theme.equals("ligth")){
		    			convertView = mLayoutInflater.inflate(R.layout.w_item_history, null);  
		    		}else{
		    			convertView = mLayoutInflater.inflate(R.layout.d_item_history, null);  
		    		}
		    		holder.textView = (TextView) convertView.findViewById(R.id.lbl_h_name);
		    		holder.infoView = (TextView) convertView.findViewById(R.id.lbl_h_info);
		    		holder.dateView = (TextView) convertView.findViewById(R.id.lbl_h_date);
		    		holder.imageView = (ImageView) convertView.findViewById(R.id.img_h_image);
		    		convertView.setTag(holder);
		    	} else {
		    		holder = (Functions.ViewHolder) convertView.getTag();
		    	}	
			      
		    	Functions.setFirstTitle(holder.textView, history.get(position).name_russian, history.get(position).name);

		    	holder.infoView.setText(Html.fromHtml(history.get(position).info)); 
		    	holder.dateView.setVisibility(View.GONE);
		    	
		    	ImageLoader.getInstance().displayImage(history.get(position).img, holder.imageView);

		    	return convertView;  
		    }
	}
		
	//Создание
    @SuppressWarnings("unchecked")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	Functions.getPreference(getActivity().getBaseContext());
        		
		anime_list = getResources().getStringArray(R.array.anime_list);
		manga_list = getResources().getStringArray(R.array.manga_list);
		
		if (Functions.preference.theme.equals("ligth")){
			view = inflater.inflate(R.layout.w_fragment_mainprofile, container, false);
		}else{
			view = inflater.inflate(R.layout.d_fragment_mainprofile, container, false);
		}
        context = getActivity().getBaseContext();
        //Подключаем view
        ll_load = (LinearLayout) view.findViewById(R.id.ll_load);
        pb_anime = (View_TextProgressBar) view.findViewById(R.id.pb_anime);
        pb_manga = (View_TextProgressBar) view.findViewById(R.id.pb_manga);
             
        ll_anime = (LinearLayout) view.findViewById(R.id.ll_anime);
        ll_manga = (LinearLayout) view.findViewById(R.id.ll_manga);
        
        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
        tv_user_info = (TextView) view.findViewById(R.id.tv_info);
        tv_online = (TextView) view.findViewById(R.id.tv_online);
        
        tv_anime_list = (TextView) view.findViewById(R.id.tv_anime_list);
        tv_anime_list.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (ll_anime.getMeasuredHeight() != 0){
        			Functions.slideIn(ll_anime, gv_anime.getMeasuredHeight(), 800);
        			tv_anime_list.setText("ПОКАЗАТЬ СПИСКИ АНИМЕ");
        		}else{
        			Functions.slideOut(ll_anime, gv_anime.getMeasuredHeight(), 800);
        			tv_anime_list.setText("СКРЫТЬ СПИСКИ АНИМЕ");
        		}
        	}
        });
        
        tv_manga_list = (TextView) view.findViewById(R.id.tv_manga_list);
        tv_manga_list.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (ll_manga.getMeasuredHeight() != 0){
        			Functions.slideIn(ll_manga, gv_manga.getMeasuredHeight(), 800);
        			tv_manga_list.setText("ПОКАЗАТЬ СПИСКИ МАНГИ");
        		}else{
        			Functions.slideOut(ll_manga, gv_manga.getMeasuredHeight(), 800);
        			tv_manga_list.setText("СКРЫТЬ СПИСКИ МАНГИ");
        		}
        	}
        });
        
        gv_menu = (View_GridView) view.findViewById(R.id.gv_profile_menu);
        gv_menu.setExpanded(true);
        adapter_profile_menu = new PMenuAdapter(context);
		gv_menu.setAdapter(adapter_profile_menu);
		
		gv_history = (View_GridView) view.findViewById(R.id.gv_history);
        gv_history.setExpanded(true);
        adapter_history = new PHistoryAdapter(context);
		gv_history.setAdapter(adapter_history);
		
		if (Functions.isTablet(context)){
			gv_menu.setNumColumns(4);
		}
		
				
		//Привязка меню
		adapter_anime = new AnimeListAdapter(context);
        adapter_manga = new MangaListAdapter(context);
		
        
        gv_anime = (View_GridView) view.findViewById(R.id.gv_anime);
		gv_anime.setExpanded(true);
        gv_anime.setAdapter(adapter_anime);
        gv_anime.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_anime_list = new Intent(getActivity().getBaseContext(), Activity_User_AnimeList.class);
	        		if (!user_infos.user_anime[position].equals("0")){
	        			Functions.slideIn(ll_anime, gv_anime.getMeasuredHeight(), 800);
	        			tv_anime_list.setText("ПОКАЗАТЬ СПИСКИ АНИМЕ");	
	        			intent_anime_list.putExtra("list", user_infos.user_anime_id[position]);
			        	intent_anime_list.putExtra("user_id", Activity_UserProfile.target_id);
						startActivity(intent_anime_list);
	        		}
		      }
		    });
        
        gv_manga = (View_GridView) view.findViewById(R.id.gv_manga);
        gv_manga.setExpanded(true);
        gv_manga.setAdapter(adapter_manga);
        gv_manga.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_manga_list = new Intent(context, Activity_User_MangaList.class);
	        		if (!user_infos.user_manga[position].equals("0")){
	        			Functions.slideIn(ll_manga, gv_manga.getMeasuredHeight(), 800);
	        			tv_manga_list.setText("ПОКАЗАТЬ СПИСКИ МАНГИ");
	        			intent_manga_list.putExtra("list", user_infos.user_manga_id[position]);
		        		intent_manga_list.putExtra("user_id", Activity_UserProfile.target_id);
						startActivity(intent_manga_list);
							
	        		}
		      }
		    });
		
        gv_menu.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		      	
		    	  Intent intent_Messanger = new Intent(context, mThumbActivity[position]);
		    	  intent_Messanger.putExtra("id", Activity_UserProfile.target_id);
		    	  intent_Messanger.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		      	  startActivityForResult(intent_Messanger, 1);
    
		      }
		    });
        
        gv_history.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		    	  if (!history.get(position).id.equals("") && !history.get(position).type.equals("")){
			    	  	Intent intent_com = new Intent(context, Activity_AnimeManga.class);
			        	intent_com.putExtra("id", history.get(position).id);
			        	intent_com.putExtra("type", history.get(position).type);
			  			startActivity(intent_com);
		    	  }
		      }
		    });
        
        mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
        mPullRefreshScrollView.setVisibility(View.GONE);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if (Functions.isNetwork(context)) startLoading();
			}
		});
		
		al = new AsyncLogin();
		
       	if (savedInstanceState != null || user_infos.id != null) {
       		user_infos = (SSDK_User) savedInstanceState.getSerializable("user_infos");
 			user_info = savedInstanceState.getString("user_info");
 			if (user_infos.id != null && user_info != null){
	       		ll_load.setVisibility(View.GONE);
		  		mPullRefreshScrollView.setVisibility(View.VISIBLE);
        		
    			tv_user_info.setText(Html.fromHtml(user_info));
    			tv_nickname.setText(user_infos.nickname);
    			Activity_UserProfile.actionbar.setSubtitle(user_infos.nickname);

    			anime = savedInstanceState.getStringArrayList("anime");
    			manga = savedInstanceState.getStringArrayList("manga");
    			
    			history = (ArrayList<SSDK_History>) savedInstanceState.getSerializable("history");
    			t_history = (ArrayList<SSDK_History>) savedInstanceState.getSerializable("t_history");

    			Activity_UserProfile.tv_unread.setText(String.valueOf(Functions.count_unread));

    			ImageLoader.getInstance().displayImage(user_infos.image_x148, iv_avatar);

                adapter_anime.notifyDataSetChanged();
  		  		adapter_manga.notifyDataSetChanged();
  		  		adapter_profile_menu.notifyDataSetChanged();
  		  		adapter_history.notifyDataSetChanged();
  		  		
  		  		tv_online.setText("["+user_infos.last_online+"]");
  		  		
  		  		pb_anime.setMax(user_infos.total_see);
		  		pb_anime.setProgress(user_infos.see);
		  		pb_anime.setSecondaryProgress(user_infos.total_see - user_infos.adrop);
		  		pb_anime.invalidate();
		  		pb_anime.setText("Просмотрено " + user_infos.see + " из " + user_infos.total_see);
		  		
		  		pb_manga.setMax(user_infos.total_read);
		  		pb_manga.setProgress(user_infos.read);
		  		pb_manga.setSecondaryProgress(user_infos.total_read - user_infos.mdrop);
		  		pb_manga.invalidate();
		  		pb_manga.setText("Прочитано " + user_infos.read + " из " + user_infos.total_read);
		  		if (!Functions.preference.user_id.equals(Activity_UserProfile.target_id)){
			  		Activity_UserProfile.in_fav = user_infos.is_friend;
			  		Activity_UserProfile.friends();
		  		}
 			}else{
    			if (Functions.isNetwork(context)) al = new AsyncLogin().execute();
 			}
       	}else{
       		if (Functions.isNetwork(context)) startLoading();
       	}
	
        return view;
        
    }
         
    //Определение асинхронной загрузки (Логирования)
  	public void startLoading() {
  		if (loader == null || loader.getStatus().equals(AsyncTask.Status.FINISHED)) {
      		loader = new AsyncLogin();
      		loader.execute();
      	} else {
      		Toast.makeText(context, "Ждите завершения загрузки", Toast.LENGTH_SHORT)
  			.show();
      	}
      }
  	
  	//Асинхронная загрузка (Логинимся и получаем информацию о пользователе)
  	private class AsyncLogin extends AsyncTask<Void, Void, Void> {
  	  
  	  	@Override
  	  	protected void onPreExecute() {
  	  		user_info = null;
	    	anime.clear();
	    	manga.clear();
	  		history.clear();
			t_history.clear();
  		}
  		 
  	  	@Override
  	  	protected void onPostExecute(Void result1) {
  	  		//Показываем полученную информацию..
	  		Activity_UserProfile.actionbar.setSubtitle(user_infos.nickname);
  		  	tv_nickname.setText(user_infos.nickname);
  		  	Functions.tv_menu_login.setText(Functions.preference.login);
  		  	
  		  	user_info = "";
			if (!user_infos.name.equals("null") && !user_infos.name.equals("")) user_info += user_infos.name;
			if (!user_infos.sex.equals("null") && !user_infos.sex.equals("")) user_info += " / " + user_infos.sex.replace("female", "жен").replace("male", "муж");
			if (!user_infos.full_years.equals("null") && !user_infos.full_years.equals("")) user_info += " / " + user_infos.full_years;
			if (!user_infos.location.equals("") && !user_infos.location.equals("null")) user_info += " / " + user_infos.location;
			if (!user_infos.website.equals("null") && !user_infos.website.equals("")) user_info += " / " + user_infos.website;
			user_info += " / " + user_infos.start_date;
			tv_user_info.setText(Html.fromHtml(user_info));

  		  	ImageLoader.getInstance().displayImage(user_infos.image_x148, iv_avatar);
  		  		  		  		
  		  	tv_online.setText("["+user_infos.last_online+"]");
  		  		
  		  	for (int i=0;i<user_infos.user_anime.length;i++){
  		  		anime.add(user_infos.user_anime_name[i] + " (" + user_infos.user_anime[i]+")");
  		  	}
  		  	for (int i=0;i<user_infos.user_manga.length;i++){
  		  		manga.add(user_infos.user_manga_name[i] + " (" + user_infos.user_manga[i]+")");
  		  	}
  		  		
  		  	adapter_anime.notifyDataSetChanged();
  		  	adapter_manga.notifyDataSetChanged();
  		  	adapter_profile_menu.notifyDataSetChanged();
  		  	history.addAll(t_history);
  		  	adapter_history.notifyDataSetChanged();
  		  	
  		  	pb_anime.setMax(user_infos.total_see);
  		  	pb_anime.setProgress(user_infos.see);
  		  	pb_anime.setSecondaryProgress(user_infos.total_see - user_infos.adrop);
  		  	pb_anime.invalidate();
		  	pb_anime.setText("Просмотрено " + user_infos.see + " из " + user_infos.total_see);
  		  	
  		  	pb_manga.setMax(user_infos.total_read);
  		  	pb_manga.setProgress(user_infos.read);
  		  	pb_manga.setSecondaryProgress(user_infos.total_read - user_infos.mdrop);
  		  	pb_manga.invalidate();
  		  	pb_manga.setText("Прочитано " + user_infos.read + " из " + user_infos.total_read);
  		  	
  		  	Activity_UserProfile.tv_unread.setText(String.valueOf(Functions.count_unread));
  	  		mPullRefreshScrollView.onRefreshComplete();
  	  		al = new AsyncLogin();
	  	  	ll_load.setVisibility(View.GONE);
	  		mPullRefreshScrollView.setVisibility(View.VISIBLE);
	  		
	  		if (Functions.preference.see_to_open){
	 			Intent intent_anime_list = new Intent(getActivity().getBaseContext(), Activity_User_AnimeList.class);
	 			intent_anime_list.putExtra("list", "1");
	 	    	intent_anime_list.putExtra("user_id", Activity_UserProfile.target_id);
	 			startActivity(intent_anime_list);
	 		}
	  		
	  		if (!Functions.preference.user_id.equals(Activity_UserProfile.target_id)){
		  		Activity_UserProfile.in_fav = user_infos.is_friend;
		  		Activity_UserProfile.friends();
	  		}
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			try {
				t_history = SSDK_API.getHistory(Activity_UserProfile.target_id, Functions.preference.kawai, 3, 1);
				user_infos = SSDK_API.getUser(Activity_UserProfile.target_id, Functions.preference.kawai);
				SSDK_API.getUnread(Functions.preference.kawai);
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
  	 	  	
    //Сохранение значений элементов
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
        outState.putBoolean("refresh", false);
        outState.putString("user_info", user_info);
        outState.putSerializable("user_infos", user_infos);
        outState.putStringArrayList("anime", anime);
        outState.putStringArrayList("manga", manga);
        
        outState.putSerializable("history", history);
        outState.putSerializable("t_history", t_history);

    }
 
}