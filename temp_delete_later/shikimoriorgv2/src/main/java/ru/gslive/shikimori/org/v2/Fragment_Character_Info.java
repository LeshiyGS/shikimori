package ru.gslive.shikimori.org.v2;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
 
public class Fragment_Character_Info extends SherlockFragment {
 
	Boolean savetype = true;
	//String id;
		
	ImageView iv_preview, iv_c_avatar;
	TextView tv_title, tv_tom, tv_info, tv_rating, tv_c_name;
	LinearLayout tv_c_info;
	TextView tv_c_altname;
	RatingBar rb_shiki, rb_my_shikimori;
	static View_GridView gv_seyu;
	static View_GridView gv_anime;
	static View_GridView gv_manga;
	
	static SSDK_Character result = new SSDK_Character();
	ArrayList<String> al_seyu = new ArrayList<String>();
	ArrayList<String> al_anime = new ArrayList<String>();
	ArrayList<String> al_manga = new ArrayList<String>();
	
	PullToRefreshScrollView mPullRefreshScrollView;
	
	AsyncTask<Void, Void, Void> al;

	ImageAdapterSeyu adapter_seyu;
	ImageAdapterAnime adapter_anime;
	ImageAdapterManga adapter_manga;

	
	//
	public class ImageAdapterSeyu extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		
		public ImageAdapterSeyu (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}
		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return al_seyu.size(); // длина массива
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return al_seyu.get(position);
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
			
    		ImageLoader.getInstance().displayImage("http://shikimori.org" + al_seyu.get(position).split(";")[2], holder.imageView);

    		holder.infoView.setText(al_seyu.get(position).split(";")[1]);
		
			return convertView;
		}
	}
	
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
			
    		ImageLoader.getInstance().displayImage(Constants.SERVER + al_anime.get(position).split(";")[2], holder.imageView);

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
			
    		ImageLoader.getInstance().displayImage(Constants.SERVER + al_manga.get(position).split(";")[2], holder.imageView);

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
            view = inflater.inflate(R.layout.w_fragment_character_info, container, false);
        }else{
            view = inflater.inflate(R.layout.d_fragment_character_info, container, false);
        }

        tv_c_name = (TextView) view.findViewById(R.id.tv_character_name);
        tv_c_altname = (TextView) view.findViewById(R.id.tv_character_altname);
        iv_c_avatar = (ImageView) view.findViewById(R.id.iv_character_avatar);
        tv_c_info = (LinearLayout) view.findViewById(R.id.ll_info);
        
        gv_seyu = (View_GridView) view.findViewById(R.id.gv_seyu);
        gv_seyu.setExpanded(true);
        adapter_seyu = new ImageAdapterSeyu(getActivity().getBaseContext());
        gv_seyu.setAdapter(adapter_seyu);
        gv_seyu.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		Intent intent_com = new Intent(getActivity().getBaseContext(), Activity_People.class);
		    	intent_com.putExtra("id", al_seyu.get(position).split(";")[0]);
		    	startActivity(intent_com);
        	}
        });
        
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
        
		
		TextView lbl_info = (TextView) view.findViewById(R.id.tv_lbl_info);
		lbl_info.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	if (tv_c_info.getVisibility() == View.VISIBLE){
					tv_c_info.setVisibility(View.GONE);
				}else{
					tv_c_info.setVisibility(View.VISIBLE);
				} 
		    }
		});
		TextView lbl_seyu = (TextView) view.findViewById(R.id.tv_lbl_seiy);
		lbl_seyu.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	if (gv_seyu.getVisibility() == View.VISIBLE){
		    		gv_seyu.setVisibility(View.GONE);
				}else{
					gv_seyu.setVisibility(View.VISIBLE);
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
	    	result = (SSDK_Character) savedInstanceState.getSerializable("result");
	    	
	    	al_seyu = savedInstanceState.getStringArrayList("al_seyu");
	    	al_anime = savedInstanceState.getStringArrayList("al_anime");
	    	al_manga = savedInstanceState.getStringArrayList("al_manga");
	    	
	    	tv_c_name.setText(result.name);

            Functions.addComment(result.description_clean, result.spoiler_names, tv_c_info, getActivity(), getActivity(), true);

  			//tv_c_info.setText(Html.fromHtml(result.description_html));
  			tv_c_altname.setText("Имена: \n" + result.russian + "\n" + result.altname + "\n" + result.japanese);
            Ion.with(iv_c_avatar)
                    .animateLoad(R.anim.spin_animation)
                    .error(R.drawable.missing_preview)
                    .load(Constants.SERVER + result.image_original);
  			//aq.id(iv_c_avatar).image(Constants.SERVER + result.image_original, false, false);
  			
  			al_seyu.clear();
  			al_seyu.addAll(result.seyu);
  			adapter_seyu.notifyDataSetChanged();
  			al_anime.clear();
  			al_anime.addAll(result.anime);
  			adapter_anime.notifyDataSetChanged();
  			al_manga.clear();
  			al_manga.addAll(result.manga);
  			adapter_manga.notifyDataSetChanged();

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

            Functions.addComment(result.description_clean, result.spoiler_names, tv_c_info, getActivity(), getActivity(), true);

            //tv_c_info.setText(Html.fromHtml(result.description_html));
  			tv_c_altname.setText("Имена: \n" + result.russian + "\n" + result.altname + "\n" + result.japanese);
            Ion.with(iv_c_avatar)
                    .animateLoad(R.anim.spin_animation)
                    .error(R.drawable.missing_preview)
                    .load(Constants.SERVER + result.image_original);
  			//aq.id(iv_c_avatar).image(Constants.SERVER + result.image_original, false, false);
  			
  			al_seyu.clear();
  			al_seyu.addAll(result.seyu);
  			adapter_seyu.notifyDataSetChanged();
  			al_anime.clear();
  			al_anime.addAll(result.anime);
  			adapter_anime.notifyDataSetChanged();
  			al_manga.clear();
  			al_manga.addAll(result.manga);
  			adapter_manga.notifyDataSetChanged();
  			
  			Activity_Character.actionbar.setSubtitle(result.name);
  			
  			Fragment_Character_Menu.in_favourites = result.favoured;
  			Fragment_Character_Menu.in_list_adapter.notifyDataSetChanged();
  			
  			if (all_update){
  				Fragment_Character_Comments.al.execute();
  			}
	        mPullRefreshScrollView.onRefreshComplete();
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			result = SSDK_API.getCharacter(Activity_Character.target_id, Functions.preference.kawai);
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
        outState.putStringArrayList("al_seyu", al_seyu);
        outState.putStringArrayList("al_anime", al_anime);
        outState.putStringArrayList("al_manga", al_manga);
    }
  	
}