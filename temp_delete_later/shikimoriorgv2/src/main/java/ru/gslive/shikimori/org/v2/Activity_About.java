package ru.gslive.shikimori.org.v2;

import java.io.InputStream;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;

public class Activity_About extends ShikiSherlockActivity {

	int rotate;
	
	ActionBar actionbar;
	
	MenuDrawer mDrawer;
	TextView tv_unread;
	
	Functions.MenuAdapter adapter_menu;
	
	//Создание активити и подключение view
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Functions.getPreference(getBaseContext());
		
		//Подключение и выбор варианта меню.
		int rotate = getWindowManager().getDefaultDisplay().getRotation();
		if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
			setTheme(R.style.AppTheme_ActionBarStyle_BackHome);
		}else{
			setTheme(R.style.AppTheme_ActionBarStyle);
		}
		
		actionbar = getSupportActionBar();

		super.onCreate(savedInstanceState);
		
		//Подключение и выбор варианта меню.
		if (Functions.preference.theme.equals("ligth")){
			mDrawer = Functions.setMenuDrawer(actionbar, "О программе", R.layout.w_activity_about, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, "О программе", R.layout.d_activity_about, getWindowManager().getDefaultDisplay().getRotation(),
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
        
        TextView tv_app_version = (TextView) findViewById(R.id.tv_app_version);
        try {
			tv_app_version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
        
        TextView tv_about = (TextView) findViewById(R.id.tv_about);
        try {
            InputStream in_s = getResources().openRawResource(R.raw.changelog);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            tv_about.setText(new String(b));
        } catch (Exception e) {
         	tv_about.setText("Ошибка загрузки.");
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
	
	
}
