package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_OST_List extends ShikiSherlockActivity {

	String OST_title;
	int rotate;
	
	ListView lv_character;
	TextView tv_unread;
	MenuDrawer mDrawer;
	
	ArrayList<String> OST_Album_Name = new ArrayList<String>();
	ArrayList<String> OST_Album_Name_link = new ArrayList<String>();
	
	ArrayAdapter<String> adapter_ost;
	
	Functions.MenuAdapter adapter_menu;
	ActionBar actionbar;
	
	AsyncTask<Void, Void, Void> al;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Functions.getPreference(getBaseContext());
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		int rotate = getWindowManager().getDefaultDisplay().getRotation();
		if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
			setTheme(R.style.AppTheme_ActionBarStyle_BackHome);
		}else{
			setTheme(R.style.AppTheme_ActionBarStyle);
		}
		
		actionbar = getSupportActionBar();
	
		super.onCreate(savedInstanceState);

		OST_title = getIntent().getExtras().getString("title");

		//Подключение и выбор варианта меню.
		
		mDrawer = Functions.setMenuDrawer(actionbar, OST_title, R.layout.w_activity_manga_character, getWindowManager().getDefaultDisplay().getRotation(),
				getBaseContext(), this);
		
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
		        
        lv_character = (ListView) findViewById(R.id.lv_character);
        adapter_ost = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, OST_Album_Name);
	    lv_character.setAdapter(adapter_ost);
        
	    lv_character.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_player = new Intent(Activity_OST_List.this, Activity_OST_Player.class);;
		    	  intent_player.putExtra("album_link", OST_Album_Name_link.get(position));
		    	  startActivity(intent_player);
		      }
	    });
	          
        al = (OSTSearchLoader) getLastNonConfigurationInstance();
	    if (al == null) {
	      al = new OSTSearchLoader();
	    }
        
        if (savedInstanceState != null) {
        	OST_Album_Name.clear();
        	OST_Album_Name_link.clear();
        	OST_Album_Name.addAll(savedInstanceState.getStringArrayList("OST_Album_Name"));
        	OST_Album_Name_link.addAll(savedInstanceState.getStringArrayList("OST_Album_Name_link"));
	        adapter_ost.notifyDataSetChanged();
		}else{
			if (Functions.isNetwork(getBaseContext()))  al = new OSTSearchLoader().execute();
		}
        
	}

	//События нажатий на меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		switch (item.getItemId()) {
          	case android.R.id.home:
          		if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
        	    	finish();
        	    }else{
        	    	mDrawer.toggleMenu(true);
        	    }
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
	
  	@Override
    protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
        case Constants.IDD_NO_SEARCH:
        	builder.setMessage("К сожалению мы не можем найти OSTы.")
	            .setCancelable(false)
	            .setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
	                        @SuppressWarnings("deprecation")
							public void onClick(DialogInterface dialog, int id) {
	                            removeDialog(Constants.IDD_NO_SEARCH);
	                            finish();
	                        }
	                    });
			}
			return builder.create();
    }
  	
  	private class OSTSearchLoader extends AsyncTask<Void, Void, Void> {
		  
		@Override
		protected void onPreExecute() {
			OST_Album_Name.clear();
			OST_Album_Name_link.clear();
			setSupportProgressBarIndeterminateVisibility(true);		
		}
		  
		@SuppressWarnings("deprecation")
		protected void onPostExecute(Void result1) {
			adapter_ost.notifyDataSetChanged();
			setSupportProgressBarIndeterminateVisibility(false);
			if (OST_Album_Name_link.size() == 0) showDialog(Constants.IDD_NO_SEARCH);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				String eng_title_temp;
				if (OST_title == null) OST_title = getIntent().getExtras().getString("title");
				OST_title = OST_title.replace(" 2nd Season", "");
				OST_title = OST_title.replaceAll(";", " ");
				OST_title = OST_title.replaceAll("\\.", " ").trim();
				if (OST_title.contains(" ")){
					eng_title_temp = OST_title.substring(0, OST_title.indexOf(" "));
				}else{
					eng_title_temp = OST_title;
				}
				Document res = Jsoup
					    .connect("http://animespirit.ru/index.php?do=search")
						.data("do","search")
						.data("subaction","search")
						.data("search_start","1")
						.data("full_search","1")
						.data("story", eng_title_temp)
						.data("titleonly","3")
						.data("searchuser","")
						.data("replyless","0")
						.data("replylimit","0")
						.data("searchdate","0")
						.data("beforeafter","after")
						.data("sortby","news_read")
						.data("resorder","desc")
						.data("result_num","40")
						.data("result_from","1")
						.data("showposts","0")
						.data("catlist[]","51")
						.method(Method.POST)
					    .get();
				Elements find_obj = Jsoup.parse(res.html()).select("div[class=content-block-title]");
				
				for(Element src : find_obj){
						OST_Album_Name.add(src.text());
						Elements link = src.select("a[href]");
						for(Element src_link : link){
							OST_Album_Name_link.add(src_link.attr("abs:href"));
						}
				}
			} catch (IOException e) {
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
  	
	//Сохранение значений элементов
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("OST_Album_Name", OST_Album_Name);
        outState.putStringArrayList("OST_Album_Name_link", OST_Album_Name_link);

    }  	
  	
}
