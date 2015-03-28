package ru.gslive.shikimori.org.v2;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
 
@SuppressLint("InflateParams")
public class Fragment_Character_Menu extends SherlockFragment{
 
	String target_id;
	String target_type;
	static float temp_rating;
	
	static Boolean in_favourites = false;
	
	//Массивы списков
	String[] status;
	
	String[] in_list_no = new String[]{"ДОБАВИТЬ В ИЗБРАННОЕ"};
	String[] in_list_yes = new String[]{"УДАЛИТЬ ИЗ ИЗБРАННОГО"};
	String[] addon = new String[]{};
	String[] addon_m = new String[]{};
	
	@SuppressWarnings("rawtypes")
	public Class[] activity_anime = { Activity_NoAPI.class, Activity_NoAPI.class, Activity_NoAPI.class, Activity_NoAPI.class, Activity_NoAPI.class, Activity_Anime_VKSearch.class, Activity_NoAPI.class};
	@SuppressWarnings("rawtypes")
	public Class[] activity_manga = { Activity_NoAPI.class, Activity_NoAPI.class, Activity_NoAPI.class, Activity_NoAPI.class, Activity_NoAPI.class, Activity_MangaCharacter.class, Activity_NoAPI.class};
	
	View_GridView gv_in_list;
	static InListAdapter in_list_adapter;
	
	private final int DIALOG_DELETE_FAV = 1; //Подтверждение удаления из списка избранного
	
	AsyncTask<Void, Void, Void> add_favour;
	AsyncTask<Void, Void, Void> del_favour;
	
		
	//Адаптер меню
	public class InListAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public InListAdapter (Context ctx) {  
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

		@SuppressLint("ViewHolder")
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
								
			if (!in_favourites) holder.itemView.setText(in_list_no[position]);
				else holder.itemView.setText(in_list_yes[position]);
			holder.newView.setText("");

			return convertView;
		}
	}
			
	//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	//Чтение настроек
    	Functions.getPreference(getActivity());
    	
    	target_id = Activity_Character.target_id;
    
        View view;
        if (Functions.preference.theme.equals("ligth")){
            view = inflater.inflate(R.layout.w_fragment_character_menu, container, false);
        }else{
            view = inflater.inflate(R.layout.d_fragment_character_menu, container, false);
        }
        
        gv_in_list = (View_GridView) view.findViewById(R.id.gv_in_list);
        in_list_adapter = new InListAdapter(getActivity().getBaseContext());
        gv_in_list.setAdapter(in_list_adapter);
        gv_in_list.setExpanded(true);
                
        gv_in_list.setOnItemClickListener(new OnItemClickListener() {
		      @SuppressWarnings("deprecation")
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  switch (position) {
		    	  	case 0:
		    	  		if (in_favourites){
		    	  			getActivity().showDialog(DIALOG_DELETE_FAV);
		    	  		}else{
    	  					//Добавляем в избранное
		    	  			add_favour = new AsyncAddFavour().execute();
				    	}
		    	  		break;
		    	  	default:
		    	  		break;
		    	  }

		      }
		    });
        
        if ((savedInstanceState != null)) {
        	in_favourites = savedInstanceState.getBoolean("in_favourites");
        	in_list_adapter.notifyDataSetChanged();
		}
        return view;
        
        
    }
     
    //Сохранение значения Асинхронной задачи
  	public Object onRetainNonConfigurationInstance() {
  		return add_favour;
  	}
      	
  //Добавление в избранное (Надо перевести на API)
  	private class AsyncAddFavour extends AsyncTask<Void, Void, Void> {
		
		public AsyncAddFavour() {

		}  
				
		@Override
		protected void onPreExecute() {

		}
		  
		protected void onPostExecute(Void result1) {
			in_list_adapter.notifyDataSetChanged();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			String fav_link = Constants.SERVER + "/favourites/Character" + Fragment_Character_Info.result.url.substring(Fragment_Character_Info.result.url.lastIndexOf("/"));
			
			try {
				String[] token = SSDK_API.getToken(Functions.preference.kawai);
				Response res = Jsoup
					.connect(fav_link)
					.header("X-CSRF-Token", token[0])
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
					.ignoreContentType(true)
					.cookie("_kawai_session", token[1])
					.method(Method.POST)
					.execute();
				if (res.statusCode() == 200){
					in_favourites = true;
  				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

			
	 }
  
  	
	//
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
        outState.putBoolean("in_favourites", in_favourites);        
    }
 
}