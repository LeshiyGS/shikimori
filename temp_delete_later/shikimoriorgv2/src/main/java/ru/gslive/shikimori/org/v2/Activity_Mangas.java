package ru.gslive.shikimori.org.v2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.viewpagerindicator.TitlePageIndicator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_Mangas extends ShikiSherlockFragmentActivity {
	
	ListView lv_menu;
	MenuDrawer mDrawer;
	TextView tv_unread;
	static TitlePageIndicator titleIndicator;
	int mPagerOffsetPixels;
    int mPagerPosition;
    
    static String SearchText = "";
    String t_SearchText = "";
    static String fstatus = "";
	static String ftype = "";
	static String fseason = "";
	static String forder_by = "";
	static String fmylist = "";
	static String fgenre = "";
	String t_fstatus = "";
	String t_ftype = "";
	String t_fseason = "";
	String t_forder_by = "";
	String t_fmylist = "";
	String t_fgenre = "";
    
	
	public static Functions.MenuAdapter adapter_menu;
	public static ActionBar actionbar;
	
	public String[] menu_content;
	
    ActionBar mActionBar;
    ViewPager mPager;
    private Menu ABSMenu;
 		
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
		
		if (Functions.preference.theme.equals("ligth")){
			mDrawer = Functions.setMenuDrawer(actionbar, "Манга", R.layout.w_activity_main_profile, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, "Манга", R.layout.d_activity_main_profile, getWindowManager().getDefaultDisplay().getRotation(),
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
        Adapter_Mangas viewpageradapter = new Adapter_Mangas(fm);
        mPager.setAdapter(viewpageradapter);
        mPager.setOffscreenPageLimit(2);
        titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);
        titleIndicator.setViewPager(mPager);
        titleIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPagerPosition = position;
                mPagerOffsetPixels = positionOffsetPixels;
            }
        });    
        
        if ((savedInstanceState != null)) {
        	SearchText = savedInstanceState.getString("SearchText");
			t_SearchText = savedInstanceState.getString("t_SearchText");
			fstatus = savedInstanceState.getString("fstatus");
			ftype = savedInstanceState.getString("ftype");
			fseason = savedInstanceState.getString("fseason");
			forder_by = savedInstanceState.getString("forder_by");
			fmylist = savedInstanceState.getString("fmylist");
			fgenre = savedInstanceState.getString("fgenre");
			
			t_fstatus = savedInstanceState.getString("t_fstatus");
			t_ftype = savedInstanceState.getString("t_ftype");
			t_fseason = savedInstanceState.getString("t_fseason");
			t_forder_by = savedInstanceState.getString("t_forder_by");
			t_fmylist = savedInstanceState.getString("t_fmylist");
			t_fgenre = savedInstanceState.getString("t_fgenre");
			
			
			mPagerPosition = savedInstanceState.getInt("mPagerPosition");
			mPagerOffsetPixels = savedInstanceState.getInt("mPagerOffsetPixels");
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ABSMenu = menu;		
		getSupportMenuInflater().inflate(R.menu.search_item, menu);
			MenuItem searchItem = menu.findItem(R.id.search_item);
			final SearchView searchView = (SearchView) searchItem.getActionView();
			searchView.setOnQueryTextListener(new OnQueryTextListener() {

		        @Override
		        public boolean onQueryTextSubmit(String query) {
		            onSearchRequested();
		    		try {
						SearchText = URLEncoder.encode(query, "UTF-8");
						t_SearchText = URLEncoder.encode(query, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
		    		Log.d("search ", "->" + SearchText);
		    		Fragment_Mangas.adapter_list.notifyDataSetChanged();
		    		Fragment_Mangas.mPullToRefreshGridView.setRefreshing();
		    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		            return false;
		        }

		        @Override
		        public boolean onQueryTextChange(String newText) {

		            return false;
		        }
		    });	
			
			menu.add(0, 10, 0, "Очистить поиск")
				.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
			
	        return true;
		
	}
	
	//Восстановление значений при возврате в активити
  	@Override
    protected void onResume() {
  		SearchText = t_SearchText;
  		fstatus = t_fstatus;
  		ftype = t_ftype;
  		fseason = t_fseason;
  		forder_by = t_forder_by;
  		fmylist = t_fmylist;
  		fgenre = t_fgenre;
        super.onResume();
    }
  	
  	//Сохранение значений элементов
  	@Override
  	public void onSaveInstanceState(Bundle outState) {
  		super.onSaveInstanceState(outState);
  		outState.putString("SearchText", SearchText);
  		outState.putString("t_SearchText", t_SearchText);
  		outState.putString("fstatus", fstatus);
  		outState.putString("ftype",ftype);
  		outState.putString("fseason",fseason);
  		outState.putString("forder_by",forder_by);
  		outState.putString("fmylist",fmylist);
 		outState.putString("fgenre",fgenre);
 		outState.putString("t_fstatus", t_fstatus);
  		outState.putString("t_ftype",t_ftype);
  		outState.putString("t_fseason",t_fseason);
  		outState.putString("t_forder_by",t_forder_by);
  		outState.putString("t_fmylist",t_fmylist);
 		outState.putString("t_fgenre",t_fgenre);
 		
 		outState.putInt("mPagerOffsetPixels", mPagerOffsetPixels);
  		outState.putInt("mPagerPosition", mPagerPosition);
  	}
	
    //Actions меню
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
          	case 10:
          		SearchText = "";
          		t_SearchText = "";
          		Fragment_Mangas.mPullToRefreshGridView.setRefreshing(true);
    			return true;
    		case 11:
       			SearchText = "";
          		t_SearchText = "";
    			return true;
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
  	    if (keyCode == KeyEvent.KEYCODE_SEARCH) {
	    	MenuItem searchItem = ABSMenu.findItem(R.id.search_item);
			final SearchView searchView = (SearchView) searchItem.getActionView();
			searchView.setIconified(false);
	        return true;
	    }
  	    return super.onKeyDown(keyCode, event);
  	}
}