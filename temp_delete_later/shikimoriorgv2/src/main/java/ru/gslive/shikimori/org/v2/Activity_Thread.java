package ru.gslive.shikimori.org.v2;

import java.util.ArrayList;
import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.viewpagerindicator.TitlePageIndicator;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;

public class Activity_Thread extends ShikiSherlockFragmentActivity {
	int rotate;
	int mPagerOffsetPixels;
    int mPagerPosition;
	
	static String target_id;
	String t_target_id;
	
	Boolean in_fav = false;
	
	TextView tv_unread;
	MenuDrawer mDrawer;
	
	Functions.MenuAdapter adapter_menu;
	static ActionBar actionbar;
	
    ActionBar mActionBar;
    ViewPager mPager;
    
    Menu faw_menu;
    
    AsyncTask<Void, Void, Void> al_read_all;
 		
	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
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
		
		t_target_id = getIntent().getExtras().getString("id");
		target_id = t_target_id;
		
		if (Functions.preference.theme.equals("ligth")){
			mDrawer = Functions.setMenuDrawer(actionbar, "", R.layout.w_activity_main_profile, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, "", R.layout.d_activity_main_profile, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}
		
		mDrawer.setOnInterceptMoveEventListener(new MenuDrawer.OnInterceptMoveEventListener() {
            @Override
            public boolean isViewDraggable(View v, int dx, int x, int y) {
                if (v == mPager) {
                    return !(mPagerPosition == 0 && mPagerOffsetPixels == 0) || dx < 0;
                }

                return false;
            }
        });
		
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
		        
        mPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fm = getSupportFragmentManager();
        Adapter_Thread viewpageradapter = new Adapter_Thread(fm);
        mPager.setAdapter(viewpageradapter);
        TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);
        titleIndicator.setViewPager(mPager);
        titleIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPagerPosition = position;
                mPagerOffsetPixels = positionOffsetPixels;
            }
        });  
        
        for (int i=0; i < Functions.preference.topic_fav.length; i++){
        	if(Functions.preference.topic_fav[i].equals(t_target_id)){
            	in_fav = true;
            }
        }
        
        
        if ((savedInstanceState != null)) {
	    	mPagerPosition = savedInstanceState.getInt("mPagerPosition");
			mPagerOffsetPixels = savedInstanceState.getInt("mPagerOffsetPixels");
			t_target_id = savedInstanceState.getString("target_id");
			target_id = t_target_id;
	    	tv_unread.setText(String.valueOf(Functions.count_unread));
		}
      
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		faw_menu = menu;
		
		if (in_fav){
			menu.add(0, 10, 0, "")
			.setIcon(R.drawable.ic_toggle_star)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
		}else{
			menu.add(0, 10, 0, "")
			.setIcon(R.drawable.ic_toggle_star_outline)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
		}
		
		menu.add(0, 11, 0, "Добавить комментарий")
			.setIcon(R.drawable.ic_communication_textsms)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
		
		menu.add(0, 12, 0, "")
		.setIcon(getBaseContext().getResources().getDrawable(R.drawable.ic_communication_clear_all))
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
        return true;
	}
	
    //Actions меню
  	@SuppressWarnings("static-access")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		Intent intent_add = new Intent(Activity_Thread.this, Activity_Add_Edit_Input.class);
  		switch (item.getItemId()) {
          	case android.R.id.home:
          		if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
        	    	finish();
        	    }else{
        	    	mDrawer.toggleMenu(true);
        	    }
          		break;
    		case 11:
    			//Сообщение в ленте
	    		intent_add.putExtra("type", "comment");
	    		intent_add.putExtra("commentable_id", target_id);
	    		startActivityForResult(intent_add, 100); //100 add message or comments
    			return true;
    		case 10:
    			if (in_fav){
    				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    				SharedPreferences.Editor edit = prefs.edit();
    				List<String> temp_fav = new ArrayList<String>();
    				for(int i= 0;i<Functions.preference.topic_fav.length;i++){
	    				 if(t_target_id.equals(Functions.preference.topic_fav[i])){
	    				   // No operation here 
	    				 } else{
	    					 temp_fav.add(String.valueOf(Functions.preference.topic_fav[i]));
	    				 }
    				}
    				String[] list = temp_fav.toArray(new String[temp_fav.size()]);
    				String str = "";
    				for (int i = 0; i < list.length; i++) {
    					if (i == 0){
    						str = list[i];
    					}else{
    						str = str + "," + String.valueOf(list[i]);
    					}
    				}
    				edit.putString(Constants.APP_PREFERENCES_TOPIC_FAV, str.toString());
    				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
        				edit.apply();
        			}else {
        				edit.commit();
        			}
    				
    				in_fav = false;
    				faw_menu.findItem(10).setIcon(R.drawable.ic_toggle_star_outline);
    			}else{

    				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    				SharedPreferences.Editor edit = prefs.edit();
    				String[] list = new String[Functions.preference.topic_fav.length + 1];
    				for (int i = 0; i < Functions.preference.topic_fav.length; i++) {
    					list[i] = Functions.preference.topic_fav[i];
    				}
    				list[Functions.preference.topic_fav.length] = t_target_id;
    				String str = "";
    				for (int i = 0; i < list.length; i++) {
    					if (i == 0){
    						str = String.valueOf(list[i]);
    					}else{
    						str = str + "," + String.valueOf(list[i]);
    					}
    				}
    				edit.putString(Constants.APP_PREFERENCES_TOPIC_FAV, str.toString());
    				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
        				edit.apply();
        			}else {
        				edit.commit();
        			}

    				in_fav = true;
    				faw_menu.findItem(10).setIcon(R.drawable.ic_toggle_star);
    			}
    			Functions.getPreference(getBaseContext());
    			return true;
    		case 12:
    			al_read_all = new AsyncRead().execute();
    			return true;
  		}
  		intent_add = null;
  		return super.onOptionsItemSelected(item);        
    }

  	//
  	@Override
 	public void onActivityResult(int requestCode, int resultCode, Intent data) {
   		super.onActivityResult(requestCode, resultCode, data);
   		if (requestCode == Constants.ADD_NEW_COMMENT) {
  			if (resultCode == 1) {
  				Fragment_ThreadComments.lv_main.setRefreshing(true);
  			}
  		}
   	}
   
  	//При нажатии кнопки меню открываем основное меню
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
    protected void onResume() {
  		target_id = t_target_id;
        super.onResume();
    }
  	
  	private class AsyncRead extends AsyncTask<Void, Void, Void> {
  		@Override
  		protected void onPreExecute() {
  			setSupportProgressBarIndeterminateVisibility(true);
  		}
  				  
  		protected void onPostExecute(Void result1) {
  			Fragment_ThreadComments.adapter_list.notifyDataSetChanged();
  			setSupportProgressBarIndeterminateVisibility(false);
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			for (int i=0; i<Fragment_ThreadComments.share_result.size();i++){
  				if (!Fragment_ThreadComments.share_result.get(i).viewed){
  					SSDK_API.readComment(Fragment_ThreadComments.share_result.get(i).id);
  					Fragment_ThreadComments.share_result.get(i).viewed = true;
  				}
  			}
  			SSDK_API.getUnread(Functions.preference.kawai);
  			return null;
  		}

  		
  	}
  	
  	//Сохранение значений элементов
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("target_id", t_target_id);
        outState.putInt("mPagerOffsetPixels", mPagerOffsetPixels);
        outState.putInt("mPagerPosition", mPagerPosition);
    }

}
