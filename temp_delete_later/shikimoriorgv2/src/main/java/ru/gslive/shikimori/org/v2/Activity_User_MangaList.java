package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONException;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_User_MangaList extends ShikiSherlockActivity {
	
	MenuDrawer mDrawer;
	PullToRefreshListView lv_main;
	TextView tv_load;
	TextView tv_unread;
	String limit = "500";
	int page_no = 1;
	
	Functions.MenuAdapter adapter_menu;
	ActionBar actionbar;
	SharedPreferences mSettings;
	
	ArrayList<SSDK_MangaList> result = new ArrayList<SSDK_MangaList>();
	ArrayList<SSDK_MangaList> t_result = new ArrayList<SSDK_MangaList>();
	
	ListAdapter adapter_list;
	
	AsyncTask<Void, Void, Void> al;
	AsyncTask<Void, Void, Void> change_ep;
	
	String list_type;
	String user_id;
	int sort_type = 0;
	
	public class DateSortAZ implements Comparator<SSDK_MangaList> {

		@Override
		public int compare(SSDK_MangaList arg0, SSDK_MangaList arg1) {
			// TODO Auto-generated method stub
			return arg0.manga_russian.compareTo(arg1.manga_russian);
		}
	}
	
	public class DateSortAZ2 implements Comparator<SSDK_MangaList> {

		@Override
		public int compare(SSDK_MangaList arg0, SSDK_MangaList arg1) {
			// TODO Auto-generated method stub
			return arg0.manga_name.compareTo(arg1.manga_name);
		}
	}
	
	public class DateSortStatus implements Comparator<SSDK_MangaList> {

		@Override
		public int compare(SSDK_MangaList arg0, SSDK_MangaList arg1) {
			// TODO Auto-generated method stub
			return arg0.status_name.compareTo(arg1.status_name);
		}
	}
	
	public class DateSortSize implements Comparator<SSDK_MangaList> {

		@Override
		public int compare(SSDK_MangaList arg0, SSDK_MangaList arg1) {
			// TODO Auto-generated method stub
			return arg0.manga_chapters.compareTo(arg1.manga_chapters);
		}
	}
	
	public class DateSortStars implements Comparator<SSDK_MangaList> {

		@Override
		public int compare(SSDK_MangaList arg0, SSDK_MangaList arg1) {
			int result = 0;

			if (arg0 != arg1) {
			   if (arg0 == null) { 
			      result = -1;
			   } else if (arg1 == null) {
			      result = 1;
			   }
			   if (arg0.score_int < arg1.score_int) {
			      result = 1;
			   }
			   if (arg0.score_int > arg1.score_int) {
			      result = -1;
			   }
			}

			return result;
		}
	}
	
	
	//Адаптер ListView
	public class ListAdapter extends BaseAdapter {
			private LayoutInflater mLayoutInflater;
			
			public ListAdapter (Context ctx) {  
				mLayoutInflater = LayoutInflater.from(ctx);  
			} 
			
		    @Override
		    public int getCount() {
		        return result.size();
		    }

		    @Override
		    public Object getItem(int position) {
		        return result.get(position);
		    }

		    @Override
		    public long getItemId(int position) {
		        return position;
		    }

		    @Override
		    public View getView(final int position, View convertView, ViewGroup parent) {
		    	final Functions.ViewHolder holder;

			    if (convertView == null) {
			    	holder = new Functions.ViewHolder();		  
			    	
			    	if (Functions.preference.theme.equals("ligth")){
			    		convertView = mLayoutInflater.inflate(R.layout.w_item_anime_user_list, null);
					}else{
						convertView = mLayoutInflater.inflate(R.layout.d_item_anime_user_list, null);
					}
			        
			    	holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
			        holder.infoView = (TextView) convertView.findViewById(R.id.tv_status);
			        holder.titleView = (TextView) convertView.findViewById(R.id.tv_score);
			        holder.itemView = (TextView) convertView.findViewById(R.id.tv_episodes);
			        holder.imageView = (ImageView) convertView.findViewById(R.id.iv_star);
			        holder.userView = (ImageView) convertView.findViewById(R.id.iv_preview);
			        holder.clientView = (TextView) convertView.findViewById(R.id.tv_user_text);
			        holder.plusView = (TextView) convertView.findViewById(R.id.tv_plus_ep);
			        convertView.setTag(holder);

			    } else {
		            holder = (Functions.ViewHolder) convertView.getTag();
		        }

			    convertView.findViewById(R.id.tv_plus_ep).setOnClickListener(new OnClickListener() {
		        	
	        		@Override
		            public void onClick(View v) {
		        		holder.plusView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		        		if (Functions.isNetwork(getBaseContext())){
            				change_ep = new AsyncChangeEp(String.valueOf(Integer.parseInt(result.get(position).chapters)+1), result.get(position).id, position).execute();
		        		}
	        		}
		        });
			    
			    if (result.get(position).manga_russian.equals("null")){
			    	holder.textView.setText(result.get(position).manga_name);  
			    }else{
			    	if (sort_type == 1){
			    		holder.textView.setText(result.get(position).manga_name + " / " + result.get(position).manga_russian);  
			    	}else if (sort_type == 0){
			    		holder.textView.setText(result.get(position).manga_russian + " / " + result.get(position).manga_name);  
			    	}else{
			    		Functions.setFirstTitle(holder.textView, result.get(position).manga_russian, result.get(position).manga_name);
			    	}
			    }
			    
			    
			    holder.titleView.setText(result.get(position).score);  
		        //
			    holder.imageView.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
			    holder.itemView.setText("Прочитано " + result.get(position).chapters + " из " + result.get(position).manga_chapters + " глав");
			    holder.infoView.setText("");
			    holder.infoView.setTextColor(getResources().getColor(R.color.black));
			    if (result.get(position).manga_ongoing){
			    	holder.infoView.setText("Онгоинг");
			    	holder.infoView.setTextColor(getResources().getColor(R.color.green));
			    }
			    if (result.get(position).manga_anons){
			    	holder.infoView.setText("Анонс");
			    	holder.infoView.setTextColor(getResources().getColor(R.color.orange));
			    }
			    holder.clientView.setText(result.get(position).manga_user_text);
			    ImageLoader.getInstance().displayImage(result.get(position).manga_image_original, holder.userView);

		        return convertView;
		    }
		}
		
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Functions.getPreference(getBaseContext());
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		//Подключение и выбор варианта меню.
		int rotate = getWindowManager().getDefaultDisplay().getRotation();
		if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
	    	setTheme(R.style.AppTheme_ActionBarStyle_BackHome);
	    }else{
	    	setTheme(R.style.AppTheme_ActionBarStyle);
	    }
		
		actionbar = getSupportActionBar();
		mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		super.onCreate(savedInstanceState);

		list_type = getIntent().getExtras().getString("list");
		user_id = getIntent().getExtras().getString("user_id");
		sort_type = Functions.preference.sort_manga;

        String subTitle = "";
		switch (Integer.parseInt(list_type)){
			case 0:
                subTitle = "Запланировано";
				break;
			case 1:
                subTitle = "Читаю";
				break;
			case 2:
                subTitle = "Прочитано";
				break;
			case 3:
                subTitle = "Отложено";
				break;
			case 4:
                subTitle = "Брошено";
			case 9:
                subTitle = "Перечитываю";
				break;
		}
		
		//Подключение и выбор варианта меню.
		if (Functions.preference.theme.equals("ligth")){
			mDrawer = Functions.setMenuDrawer(actionbar, subTitle, R.layout.w_activity_scroll_list, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, subTitle, R.layout.d_activity_scroll_list, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}

		tv_unread = (TextView) mDrawer.findViewById(R.id.tv_unread);
		tv_unread.addTextChangedListener(new TextWatcher() {
  	        @Override
  	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
  	        @Override
  	        public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void afterTextChanged(Editable arg0) {
				if (Functions.count_unread == 0) tv_unread.setVisibility(View.GONE);
					else tv_unread.setVisibility(View.VISIBLE);
			} 
  	    });
		tv_unread.setText(String.valueOf(Functions.count_unread));
		
		//Инициализация элементов
        tv_load = (TextView) findViewById(R.id.tv_load);
        tv_load.setVisibility(View.GONE);
        
        lv_main = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
	    adapter_list = new ListAdapter(Activity_User_MangaList.this);
	    lv_main.setAdapter(adapter_list);
		
	    lv_main.setOnItemClickListener(new OnItemClickListener() {
	    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    		Intent intent_com = new Intent(getBaseContext(), Activity_AnimeManga.class);
	    	  	intent_com.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
	        	intent_com.putExtra("id", result.get(position-1).manga_id);
	        	intent_com.putExtra("type", "manga");
	  			startActivity(intent_com);
	    	}
	    });
	    
	    lv_main.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				result.clear();
				page_no = 1;
				if (Functions.isNetwork(getBaseContext())) al = new AsyncListLoading().execute();
			}
		});
	    
	    if ((savedInstanceState != null)) {
	    	result = (ArrayList<SSDK_MangaList>) savedInstanceState.getSerializable("result");
		   	//t_result = (ArrayList<SSDK_MangaList>) savedInstanceState.getSerializable("t_result");
			tv_unread.setText(String.valueOf(Functions.count_unread));
		  	adapter_list.notifyDataSetChanged();
		}else{
			if (Functions.isNetwork(getBaseContext())) { 
				al = new AsyncListLoading().execute();
			}
		}
	}

	//Сохранение значения Асинхронной задачи
	public Object onRetainNonConfigurationInstance() {
	    return al;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu sub = menu.addSubMenu("Сортировать");
		sub.setIcon(getBaseContext().getResources().getDrawable(R.drawable.ic_content_sort));
        sub.add(0, 11, 0, "По рус. названию");
        sub.add(0, 12, 0, "По анг. названию");
        sub.add(0, 13, 0, "По статусу");
        //sub.add(0, 14, 0, "По кол-ву серий");
        sub.add(0, 15, 0, "По оценке ⇑");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
		return true;
	}
	
	//События нажатий на меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		int rotate = getWindowManager().getDefaultDisplay().getRotation();
  		switch (item.getItemId()) {
          	case android.R.id.home:
          		if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
        	    	finish();
        	    }else{
        	    	mDrawer.toggleMenu(true);
        	    }
          		break;
          	case 11:
             	DateSortAZ ds = new DateSortAZ();
                try{
                	Collections.sort(result, ds);
                	adapter_list.notifyDataSetChanged();
                	sort_type = 0;
                	Editor editor = mSettings.edit();
        			editor.putInt(Constants.APP_PREFERENCES_SORT_MANGA, sort_type);
        			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
        				editor.apply();
        			}else {
        				editor.commit();
        			}
                }catch(Exception es){};
            	break;
            case 12:
             	DateSortAZ2 ds2 = new DateSortAZ2();
                try{
           	    	Collections.sort(result, ds2);
           	    	adapter_list.notifyDataSetChanged();
           	    	sort_type = 1;
           	    	Editor editor = mSettings.edit();
        			editor.putInt(Constants.APP_PREFERENCES_SORT_MANGA, sort_type);
        			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
        				editor.apply();
        			}else {
        				editor.commit();
        			}
           	    }catch(Exception es){};
           		break;
           	case 13:
           		DateSortStatus ds3 = new DateSortStatus();
           	    try{
           	    	Collections.sort(result, ds3);
           	    	adapter_list.notifyDataSetChanged();
           	    	sort_type = 2;
           	    	Editor editor = mSettings.edit();
        			editor.putInt(Constants.APP_PREFERENCES_SORT_MANGA, sort_type);
        			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
        				editor.apply();
        			}else {
        				editor.commit();
        			}
           	    }catch(Exception es){};
           		break;
           	case 14:
           		DateSortSize ds4 = new DateSortSize();
           	    try{
           	    	Collections.sort(result, ds4);
           	    	adapter_list.notifyDataSetChanged();
           	    	sort_type = 3;
           	    	Editor editor = mSettings.edit();
        			editor.putInt(Constants.APP_PREFERENCES_SORT_MANGA, sort_type);
        			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
        				editor.apply();
        			}else {
        				editor.commit();
        			}
           	    }catch(Exception es){};
           		break;
           	case 15:
           		DateSortStars ds5 = new DateSortStars();
           	    try{
           	    	Collections.sort(result, ds5);
           	    	adapter_list.notifyDataSetChanged();
           	    	sort_type = 4;
           	    	Editor editor = mSettings.edit();
        			editor.putInt(Constants.APP_PREFERENCES_SORT_MANGA, sort_type);
        			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
        				editor.apply();
        			}else {
        				editor.commit();
        			}
           	    }catch(Exception es){};
           		break;
  			}
  		return super.onOptionsItemSelected(item);        
    }
   
  	//Обработка нажатий кнопок
  	@Override
  	public boolean onKeyDown(int keyCode, KeyEvent event) {
  	    if (keyCode == KeyEvent.KEYCODE_MENU) {
  	        event.startTracking();
      		mDrawer.toggleMenu(true);
  	        return true;
  	    }
  	    return super.onKeyDown(keyCode, event);
  	}
	
  	//Асинхронная задача загрузки списка
	private class AsyncListLoading extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
			
		}
				  
		protected void onPostExecute(Void result1) {
			//result.addAll(t_result);
			tv_unread.setText(String.valueOf(Functions.count_unread));

			switch(sort_type){
			case 0:
          		DateSortAZ ds = new DateSortAZ();
        	    try{Collections.sort(result, ds);}catch(Exception es){};
          		break;
          	case 1:
          		DateSortAZ2 ds2 = new DateSortAZ2();
        	    try{Collections.sort(result, ds2);}catch(Exception es){};
          		break;
          	case 2:
          		DateSortStatus ds3 = new DateSortStatus();
        	    try{Collections.sort(result, ds3);}catch(Exception es){};
          		break;
          	case 3:
          		DateSortSize ds4 = new DateSortSize();
        	    try{Collections.sort(result, ds4);}catch(Exception es){};
          		break;
          	case 4:
          		DateSortStars ds5 = new DateSortStars();
        	    try{Collections.sort(result, ds5);}catch(Exception es){};
          		break;
			}
			
			adapter_list.notifyDataSetChanged();
			lv_main.onRefreshComplete();
			tv_load.setVisibility(View.GONE);
			setSupportProgressBarIndeterminateVisibility(false);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				do{
					t_result = SSDK_API.getMangaList(user_id, limit, String.valueOf(page_no), list_type ,Functions.preference.kawai);
					if (page_no != 1 && t_result.size() > 0){
						t_result.remove(0);
					}
					result.addAll(t_result);
					page_no++;
				}while(t_result.size() > 0);
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
	
	//Изменение количества эпизодов
		private class AsyncChangeEp extends AsyncTask<Void, Void, Void> {
		  		String ep;
		  		String id;
		  		int pos;
		  		
		  		public AsyncChangeEp(String ep, String id, int pos) {
		  			this.ep = ep;
		  			this.id = id;
		  			this.pos = pos;
		  		}
		  		  
		  		@Override
		  		protected void onPreExecute() {
		  			
		  		}
		  		  
		  		protected void onPostExecute(Void result1) {
		  			Toast.makeText(getBaseContext(), "Прочитана глава", Toast.LENGTH_SHORT).show();
		  			result.get(pos).chapters = String.valueOf(Integer.parseInt(result.get(pos).chapters) + 1);
		  			adapter_list.notifyDataSetChanged();
		  		}

		  		@Override
		  		protected Void doInBackground(Void... arg0) {
	  				SSDK_API.setAnimeMangaEp("Manga", id, ep);
		  			return null;
		  		}
		  	}

	//Сохранение значений элементов
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("result", result);
        //outState.putSerializable("t_result", t_result);

    }
	
}
