package ru.gslive.shikimori.org.v2;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;

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
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
 
public class Fragment_Club_Menu extends SherlockFragment{
 	
	//Массивы списков
	String[] status;
	
	String[] in_list_no = new String[]{"ВСТУПИТЬ"};
	String[] in_list_yes = new String[]{"ПОКИНУТЬ"};
	String[] addons = new String[]{"УЧАСТНИКИ","КАРТИНКИ","АНИМЕ","МАНГА","ПЕРСОНАЖИ"};
	String[] addons_type = new String[]{"members","images","animes","mangas","characters"};
	
	@SuppressWarnings("rawtypes")
	public Class[] activity_club = { Activity_ClubList.class, Activity_ClubImages.class, Activity_ClubList.class, Activity_ClubList.class, Activity_ClubList.class};
	
	View_GridView gv_in_list;
	View_GridView gv_club_addons;
	static InListAdapter in_list_adapter;
	ClubAdapter addons_adapter;
	View view;
	
	public static AsyncTask<Void, Void, Void> al;
	public static AsyncTask<Void, Void, Void> al_out;
		
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
								
			if (!Fragment_Club_Info.in_club) holder.itemView.setText(in_list_no[position]);
				else holder.itemView.setText(in_list_yes[position]);
			holder.newView.setText("");

			return convertView;
		}
	}
	
	//Адаптер меню
	public class ClubAdapter extends BaseAdapter {
			private LayoutInflater mLayoutInflater;

			public ClubAdapter (Context ctx) {  
				mLayoutInflater = LayoutInflater.from(ctx);  
			}

			@Override
			public int getCount() {
				return addons.length;
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
									
				holder.itemView.setText(addons[position]);
				holder.newView.setText("");

				return convertView;
			}
		}
		
	//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	if (Functions.preference.theme.equals("ligth")){
    		view = inflater.inflate(R.layout.w_fragment_club_menu, container, false);
		}else{
			view = inflater.inflate(R.layout.d_fragment_club_menu, container, false);
		}
        
        gv_in_list = (View_GridView) view.findViewById(R.id.gv_in_list);
        in_list_adapter = new InListAdapter(getActivity().getBaseContext());
        gv_in_list.setAdapter(in_list_adapter);
        gv_in_list.setExpanded(true);
        
        gv_in_list.setOnItemClickListener(new OnItemClickListener() {
		      @SuppressWarnings("deprecation")
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    		  if (Fragment_Club_Info.in_club){
	    			  getActivity().showDialog(Constants.IDD_CLUB_OUT);
	    		  }else{
	    			  new AsyncAdd().execute(); 
	    		  }
		      }
		    });
        
        gv_club_addons = (View_GridView) view.findViewById(R.id.gv_club_addons);
        addons_adapter = new ClubAdapter(getActivity().getBaseContext());
        gv_club_addons.setAdapter(addons_adapter);
        gv_club_addons.setExpanded(true);
        
        gv_club_addons.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_m_addon = new Intent(getActivity().getBaseContext(), activity_club[position]);
		    	  intent_m_addon.putExtra("club_id", Activity_Club.target_id);
		    	  intent_m_addon.putExtra("type", addons_type[position]);
		    	  startActivity(intent_m_addon); 
		      }
		    });
        
        al_out = new AsyncDelete();
        
       	in_list_adapter.notifyDataSetChanged();
        return view;
        
        
    }
    
    //
    private class AsyncAdd extends AsyncTask<Void, Void, Void> {
    	// ProgressDialog prog1 = new ProgressDialog(InfoActivity.this);
    	
    	@Override
    	protected void onPreExecute() {
    		((SherlockFragmentActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
    	}	
		  
    	protected void onPostExecute(Void result1) {
    		Fragment_Club_Info.al.execute();
    		((SherlockFragmentActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
		}

    	@Override
    	protected Void doInBackground(Void... arg0) {
			String[] token = SSDK_API.getToken(Functions.preference.kawai);
			try {
				Jsoup
				    .connect("http://shikimori.org/groups/"+Fragment_Club_Info.club_id+"/roles")
				    .ignoreContentType(true)
				    .header("X-CSRF-Token", token[0])
				    .cookie("_kawai_session", token[1])
				    .method(Method.POST)
				    .execute();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			return null;
		
    	}
    }
	
    //
    private class AsyncDelete extends AsyncTask<Void, Void, Void> {
		  
    	@Override
    	protected void onPreExecute() {
    		((SherlockFragmentActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(true);
    	}
		  
    	protected void onPostExecute(Void result1) {
    		al_out = new AsyncDelete();
    		Fragment_Club_Info.al.execute();
    		in_list_adapter.notifyDataSetChanged();
    		((SherlockFragmentActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
    	}

    	@Override
    	protected Void doInBackground(Void... arg0) {
    		String[] token = SSDK_API.getToken(Functions.preference.kawai);
    		try {
    			Jsoup
    				.connect("http://shikimori.org/groups/"+Fragment_Club_Info.club_id+"/roles")
    				.ignoreContentType(true)
    				.header("X-CSRF-Token", token[0])
    				.cookie("_kawai_session", token[1])
    				.data("_method","delete")
    				.method(Method.POST)
    				.execute();
    		}catch (IOException e) {
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
      	
	//
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);       
    }
 
}