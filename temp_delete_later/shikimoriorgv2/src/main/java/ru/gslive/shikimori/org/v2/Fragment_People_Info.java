package ru.gslive.shikimori.org.v2;

import java.util.ArrayList;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
 
public class Fragment_People_Info extends SherlockFragment {
 
	Boolean savetype = true;
	//String id;
		
	ImageView iv_preview, iv_c_avatar;
	TextView tv_title, tv_tom, tv_info, tv_rating, tv_c_name, lbl_anime, lbl_manga;
	static LinearLayout tv_c_info;
	TextView tv_eng_name, tv_jp_name, tv_birthday, tv_role, tv_web;
	RatingBar rb_shiki, rb_my_shikimori;
	static View_GridView gv_seyu;
	static View_GridView gv_anime;
	static View_GridView gv_manga;
	
	static SSDK_People result = new SSDK_People();
	ArrayList<String> al_anime = new ArrayList<String>();
	ArrayList<String> al_manga = new ArrayList<String>();
	
	PullToRefreshScrollView mPullRefreshScrollView;
	
	AsyncTask<Void, Void, Void> al;

	ImageAdapterAnime adapter_anime;
	ImageAdapterManga adapter_manga;
	
	ArrayList<String> people_sname = new ArrayList<String>();
	int iii=0;
	
	
	//
	public class ImageAdapterAnime extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		
		public ImageAdapterAnime (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}
		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return al_anime.size(); // длина массива
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return al_anime.get(position);
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
    		holder.infoView = (TextView) convertView.findViewById(R.id.tv_item_name);
    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
    		convertView.setTag(holder);
			
    		ImageLoader.getInstance().displayImage("http://shikimori.org" + al_anime.get(position).split(";")[2], holder.imageView);

    		holder.infoView.setText(al_anime.get(position).split(";")[1]);
		
			return convertView;
		}
	}
	
	//
	public class ImageAdapterManga extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		
		public ImageAdapterManga (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}
		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return al_manga.size(); // длина массива
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return al_manga.get(position);
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
    		holder.infoView = (TextView) convertView.findViewById(R.id.tv_item_name);
    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
    		convertView.setTag(holder);
			
    		ImageLoader.getInstance().displayImage("http://shikimori.org" + al_manga.get(position).split(";")[2], holder.imageView);

    		holder.infoView.setText(al_manga.get(position).split(";")[1]);
		
			return convertView;
		}
	}
	
	//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//Чтение настроек

    	Functions.getPreference(getActivity().getBaseContext());
        View view;
        if (Functions.preference.theme.equals("ligth")){
            view = inflater.inflate(R.layout.w_fragment_people_info, container, false);
        }else{
            view = inflater.inflate(R.layout.d_fragment_people_info, container, false);
        }

        tv_c_name = (TextView) view.findViewById(R.id.tv_character_name);
        tv_role = (TextView) view.findViewById(R.id.tv_role);
        tv_web = (TextView) view.findViewById(R.id.tv_web);
        tv_eng_name = (TextView) view.findViewById(R.id.tv_eng_name);
        tv_jp_name = (TextView) view.findViewById(R.id.tv_jp_name);
        tv_birthday = (TextView) view.findViewById(R.id.tv_birthday);
        iv_c_avatar = (ImageView) view.findViewById(R.id.iv_character_avatar);
        tv_c_info = (LinearLayout) view.findViewById(R.id.ll_info);
        
        
        
        gv_anime = (View_GridView) view.findViewById(R.id.gv_character_anime);
        gv_anime.setExpanded(true);
        adapter_anime = new ImageAdapterAnime(getActivity().getBaseContext());
        gv_anime.setAdapter(adapter_anime);
        gv_anime.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		Intent intent_com = new Intent(getActivity().getBaseContext(), Activity_AnimeManga.class);
	    		intent_com.putExtra("id", al_anime.get(position).split(";")[0]);
	    		intent_com.putExtra("type", "anime");
	    		startActivity(intent_com);
        	}
        });
        
        gv_manga = (View_GridView) view.findViewById(R.id.gv_character_manga);
        gv_manga.setExpanded(true);
        adapter_manga = new ImageAdapterManga(getActivity().getBaseContext());
        gv_manga.setAdapter(adapter_manga);
        gv_manga.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		Intent intent_com = new Intent(getActivity().getBaseContext(), Activity_AnimeManga.class);
	    		intent_com.putExtra("id", al_manga.get(position).split(";")[0]);
	    		intent_com.putExtra("type", "manga");
	    		startActivity(intent_com);
        	}
        });
        
        mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				if (Functions.isNetwork(getActivity().getBaseContext()))  al = new AsyncLoading(false).execute();
			}
		});
        
		
		lbl_anime = (TextView) view.findViewById(R.id.tv_lbl_anime);
		lbl_manga = (TextView) view.findViewById(R.id.tv_lbl_manga);

		lbl_anime.setVisibility(View.GONE);
		lbl_manga.setVisibility(View.GONE);
        
		TextView lbl_role = (TextView) view.findViewById(R.id.tv_lbl_role);
		lbl_role.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	if (tv_role.getVisibility() == View.VISIBLE){
		    		tv_role.setVisibility(View.GONE);
				}else{
					tv_role.setVisibility(View.VISIBLE);
				} 
		    }
		});
		
		TextView lbl_anime = (TextView) view.findViewById(R.id.tv_lbl_anime);
		lbl_anime.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	if (gv_anime.getVisibility() == View.VISIBLE){
		    		gv_anime.setVisibility(View.GONE);
				}else{
					gv_anime.setVisibility(View.VISIBLE);
				} 
		    }
		});
		TextView lbl_manga = (TextView) view.findViewById(R.id.tv_lbl_manga);
		lbl_manga.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	if (gv_manga.getVisibility() == View.VISIBLE){
		    		gv_manga.setVisibility(View.GONE);
				}else{
					gv_manga.setVisibility(View.VISIBLE);
				} 
		    }
		});
		
		al = (AsyncLoading) onRetainNonConfigurationInstance();
	    if (al == null) {
	      al = new AsyncLoading(true);
	    }
		
		if ((savedInstanceState != null)) {
	    	result = (SSDK_People) savedInstanceState.getSerializable("result");

	    	al_anime = savedInstanceState.getStringArrayList("al_anime");
	    	al_manga = savedInstanceState.getStringArrayList("al_manga");
	    	
	    	tv_c_name.setText(result.name);
	    	
	    	iii = savedInstanceState.getInt("iii");
	    	people_sname = savedInstanceState.getStringArrayList("character_sname");
	    	
	    	tv_c_name.setText(result.name);
  			tv_eng_name.setText("По-английски: " + result.name);
  			tv_jp_name.setText("По-японски: " + result.japanese);
  			if (result.birthday.equals("-")){
  				tv_birthday.setText("День рождения: -");
  			}else{
  				tv_birthday.setText("День рождения: " + Functions.in_time(result.birthday, false));
  			}
            Ion.with(iv_c_avatar)
                    .animateLoad(R.anim.spin_animation)
                    .error(R.drawable.missing_preview)
                    .load(result.image_original);
  			//aq.id(iv_c_avatar).image(result.image_original, false, false);
  			
  			tv_role.setText(Functions.shiki_translate(result.groupped_roles));
  			tv_web.setText("Сайт: " + result.website);
  			
  			al_anime.clear();
  			al_anime.addAll(result.anime);
  			adapter_anime.notifyDataSetChanged();
  			al_manga.clear();
  			al_manga.addAll(result.manga);
  			adapter_manga.notifyDataSetChanged();
  			
  			Activity_People.actionbar.setSubtitle(result.job_title);
  			
  			Fragment_People_Menu.in_favourites = result.favoured;
  			if (result.mangaka){
  				Fragment_People_Menu.person_type = "mangaka";
  			}
  			if (result.seyu){
  				Fragment_People_Menu.person_type = "seyu";
  			}
  			if (result.producer){
  				Fragment_People_Menu.person_type = "producer";
  			}
  			if (result.owner){
  				Fragment_People_Menu.person_type = "ower";
  			}
  			Fragment_People_Menu.in_list_adapter.notifyDataSetChanged();
  			  			
  			if (result.anime.size() > 0){
  				lbl_anime.setVisibility(View.VISIBLE);
  			}
  			if (result.manga.size() > 0){
  				lbl_manga.setVisibility(View.VISIBLE);
  			}
	        mPullRefreshScrollView.onRefreshComplete();
		}else{
			if (Functions.isNetwork(getActivity().getBaseContext()))  al = new AsyncLoading(true).execute();
		}
        

        return view;
    }
        
    //Асинхронная задача подгрузки новостей
  	private class AsyncLoading extends AsyncTask<Void, Void, Void> {
  		Boolean all_update = true;
  		
  		public AsyncLoading (Boolean update) {  
			this.all_update = update;
		}
  		
  		@Override
  		protected void onPreExecute() {
  			
  		}
  	  
  		protected void onPostExecute(Void result1) {
  			tv_c_name.setText(result.name);
  			tv_eng_name.setText("По-английски: " + result.name);
  			tv_jp_name.setText("По-японски: " + result.japanese);
  			if (result.birthday.equals("-")){
  				tv_birthday.setText("День рождения: -");
  			}else{
  				tv_birthday.setText("День рождения: " + Functions.in_time(result.birthday, false));
  			}
            Ion.with(iv_c_avatar)
                    .animateLoad(R.anim.spin_animation)
                    .error(R.drawable.missing_preview)
                    .load(result.image_original);
  			//aq.id(iv_c_avatar).image(result.image_original, false, false);
  			
  			tv_role.setText(Functions.shiki_translate(result.groupped_roles));
  			tv_web.setText("Сайт: " + result.website);
  			
  			al_anime.clear();
  			al_anime.addAll(result.anime);
  			adapter_anime.notifyDataSetChanged();
  			al_manga.clear();
  			al_manga.addAll(result.manga);
  			adapter_manga.notifyDataSetChanged();
  			
  			Activity_People.actionbar.setSubtitle(result.job_title);
  			
  			Fragment_People_Menu.in_favourites = result.favoured;
  			if (result.mangaka){
  				Fragment_People_Menu.person_type = "mangaka";
  			}
  			if (result.seyu){
  				Fragment_People_Menu.person_type = "seyu";
  			}
  			if (result.producer){
  				Fragment_People_Menu.person_type = "producer";
  			}
  			if (result.owner){
  				Fragment_People_Menu.person_type = "ower";
  			}
  			Fragment_People_Menu.in_list_adapter.notifyDataSetChanged();
  			
  			if (all_update){
  				Fragment_People_Comments.al.execute();
  			}
  			
  			if (result.anime.size() > 0){
  				lbl_anime.setVisibility(View.VISIBLE);
  			}
  			if (result.manga.size() > 0){
  				lbl_manga.setVisibility(View.VISIBLE);
  			}
	        mPullRefreshScrollView.onRefreshComplete();
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			result = SSDK_API.getPeople(Activity_People.target_id, Functions.preference.kawai);
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
        outState.putStringArrayList("al_anime", al_anime);
        outState.putStringArrayList("al_manga", al_manga);
        outState.putStringArrayList("character_sname", people_sname);
        outState.putInt("iii", iii);
    }
  	
}