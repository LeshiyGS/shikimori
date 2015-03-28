package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_MangaSave extends ShikiSherlockActivity {

	String manga_link, manga_title;
	int rotate;
	
	ListView lv_character;
	TextView tv_unread;
	MenuDrawer mDrawer;
	
	ArrayList<String> character = new ArrayList<String>();
	ArrayList<String> character_link = new ArrayList<String>();
	
	ArrayAdapter<String> adapter_character;
	
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

		manga_link = getIntent().getExtras().getString("read_manga");
		manga_title = getIntent().getExtras().getString("title");
		

		//Подключение и выбор варианта меню.
		if (Functions.preference.theme.equals("ligth")){
			mDrawer = Functions.setMenuDrawer(actionbar, manga_title, R.layout.w_activity_manga_character, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, manga_title, R.layout.d_activity_manga_character, getWindowManager().getDefaultDisplay().getRotation(),
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
		        
        lv_character = (ListView) findViewById(R.id.lv_character);
        adapter_character = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, character);
	    lv_character.setAdapter(adapter_character);
	    lv_character.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
	    
	    
        tv_unread.setText(String.valueOf(Functions.count_unread));
        
        al = (ReadLoader) getLastNonConfigurationInstance();
	    if (al == null) {
	      al = new ReadLoader();
	    }
        
        if (savedInstanceState != null) {
        	character.clear();
        	character_link.clear();
	    	character.addAll(savedInstanceState.getStringArrayList("character"));
	    	character_link.addAll(savedInstanceState.getStringArrayList("character_link"));
	        adapter_character.notifyDataSetChanged();
		}else{
			if (Functions.isNetwork(getBaseContext()))  al = new ReadLoader().execute();
		}
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 11, 0, "")
			.setIcon(R.drawable.ic_toggle_check_box)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
		menu.add(0, 12, 0, "")
			.setIcon(R.drawable.ic_toggle_check_box_outline_blank)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
		menu.add(0, 10, 0, "Скачать")
			.setIcon(R.drawable.ic_content_save)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
			
		return true;
		
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
        case Constants.IDD_NO_SEARCH:
        	builder.setMessage("К сожалению мы не можем найти эту мангу.")
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
	
	//События нажатий на меню
  	@SuppressWarnings("deprecation")
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
          	case 10:
          		String links = "";
          		for(int i=0; i < lv_character.getCheckItemIds().length; i++){
          			links += character_link.get((int) lv_character.getCheckItemIds()[i]) + "GLS";
          			
          		}
          		startService(new Intent(Activity_MangaSave.this, Service_Manga_Save.class).putExtra("links", links));
          		break;
          	case 11:
          		for(int i=0; i < character.size(); i++){
          			lv_character.setItemChecked(i, true);
          		}
          		break;
          	case 12:
          		for(int i=0; i < character.size(); i++){
          			lv_character.setItemChecked(i, false);
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
	
  	//
  	private class ReadLoader extends AsyncTask<Void, Void, Void> {
  		  
  		@Override
  		protected void onPreExecute() {
  		    character.clear();
  		    character_link.clear();
  		    setSupportProgressBarIndeterminateVisibility(true);
  		}
  		  
  		@SuppressWarnings("deprecation")
		protected void onPostExecute(Void result1) {
  			adapter_character.notifyDataSetChanged();
  			setSupportProgressBarIndeterminateVisibility(false);
  			if (character_link.size() == 0) showDialog(Constants.IDD_NO_SEARCH);
  		}

  			@Override
  			protected Void doInBackground(Void... arg0) {
  				if (!manga_link.equals("null")){
	  				Document doc = null;
	  				try {
	  					doc = Jsoup
	  						    .connect(manga_link)
	  						    .userAgent("Mozilla")
	  						    .referrer("http://shikimori.org/")
	  						    .get();
	  					Elements vol = doc.select("table[class=cTable]");
	  					for (Element src : vol.select("a[title]")){
	  						character_link.add(src.attr("abs:href"));
	  						character.add(src.text());
	  					}
	  				} catch (IOException e) {
	  					// TODO Auto-generated catch block
	  					e.printStackTrace();
	  				}
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
        outState.putStringArrayList("character", character);
        outState.putStringArrayList("character_link", character_link);

    }  	
  	
}
