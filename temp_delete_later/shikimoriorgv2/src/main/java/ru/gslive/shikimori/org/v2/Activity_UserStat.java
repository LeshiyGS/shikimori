package ru.gslive.shikimori.org.v2;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import com.viewpagerindicator.TitlePageIndicator;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;

public class Activity_UserStat extends ShikiSherlockFragmentActivity {
	
	public MenuDrawer mDrawer;
	TextView tv_unread;
	int mPagerOffsetPixels;
    int mPagerPosition = 1;
    int rotate;
	
	Functions.MenuAdapter adapter_menu;
	public static ActionBar actionbar;
	
	static SSDK_User result = new SSDK_User();
	
	AsyncTask<Void, Void, Void> al;

	String user_id;
			
    ActionBar mActionBar;
    ViewPager mPager;
    		    
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
				
		user_id = getIntent().getExtras().getString("id");
		if (Functions.preference.theme.equals("ligth")){
			mDrawer = Functions.setMenuDrawer(actionbar, "Статистика", R.layout.w_activity_main_profile, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this); 
  	  	}else{
  	  		mDrawer = Functions.setMenuDrawer(actionbar, "Статистика", R.layout.d_activity_main_profile, getWindowManager().getDefaultDisplay().getRotation(),
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
        Adapter_UserStatistic viewpageradapter = new Adapter_UserStatistic(fm);
        mPager.setAdapter(viewpageradapter);
        mPager.setOffscreenPageLimit(2);
        TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);
        titleIndicator.setViewPager(mPager);
        titleIndicator.setCurrentItem(0);
        titleIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPagerPosition = position;
                mPagerOffsetPixels = positionOffsetPixels;
            }
        });

        if ((savedInstanceState != null)) {
	    	mPagerPosition = savedInstanceState.getInt("mPagerPosition");
			mPagerOffsetPixels = savedInstanceState.getInt("mPagerOffsetPixels");
	    	tv_unread.setText(String.valueOf(Functions.count_unread));
	    	result = (SSDK_User) savedInstanceState.getSerializable("result");
	    	Fragment_UserStatistic_Anime.setStatistic(getBaseContext());
	    	Fragment_UserStatistic_Manga.setStatistic(getBaseContext());
		}else{
			if (Functions.isNetwork(getBaseContext()))  al = new AsyncLoading().execute();
		}
        
    }
	   	
    //Actions меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		rotate = getWindowManager().getDefaultDisplay().getRotation();
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
  	  
  	//Асинхронная загрузка (Логинимся и получаем информацию о пользователе)
  	private class AsyncLoading extends AsyncTask<Void, Void, Void> {
  	  
  	  	@Override
  	  	protected void onPreExecute() {
  	  		setSupportProgressBarIndeterminateVisibility(true);
  		}
  		 
		@SuppressLint("ResourceAsColor")
		@Override
  	  	protected void onPostExecute(Void result1) {
			Fragment_UserStatistic_Anime.setStatistic(getBaseContext());
			Fragment_UserStatistic_Manga.setStatistic(getBaseContext());
			setSupportProgressBarIndeterminateVisibility(false);
  	  		//mPullRefreshScrollView.onRefreshComplete();
  	  		
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
			result = SSDK_API.getUser(user_id, Functions.preference.kawai);
			SSDK_API.getUnread(Functions.preference.kawai);
			return null;
  		}

  		
    }

  	//
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mPagerOffsetPixels", mPagerOffsetPixels);
  		outState.putInt("mPagerPosition", mPagerPosition);
  		outState.putSerializable("result", result);
    }
 
}
