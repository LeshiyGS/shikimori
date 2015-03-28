package ru.gslive.shikimori.org.v2;

import java.util.concurrent.atomic.AtomicInteger;

import net.simonvt.menudrawer.MenuDrawer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
/*import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;*/
import com.viewpagerindicator.TitlePageIndicator;

public class Activity_MainProfile extends ShikiSherlockFragmentActivity {
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    //private static final String PROPERTY_APP_VERSION = "appVersion";
    //private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static Boolean TO_START = true;

    //String URL = "http://shikimori.org/api/devices";
    
    static final String TAG = "GCMDemo";

    TextView mDisplay;
    //GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;
	
	String[] menu_content;

	ListView lv_menu;
	MenuDrawer mDrawer;
	static TextView tv_unread;
		
	static ActionBar actionbar;
	ViewPager mPager;
	int mPagerOffsetPixels;
    int mPagerPosition;

	Functions.MenuAdapter adapter_menu;
 		
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
		if (Functions.preference.theme.equals("ligth"))
			mDrawer = Functions.setMenuDrawer(actionbar, Functions.preference.login, R.layout.w_activity_main_profile, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		else
            mDrawer = Functions.setMenuDrawer(actionbar, Functions.preference.login, R.layout.d_activity_main_profile, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);


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
        Adapter_MainProfile viewpageradapter = new Adapter_MainProfile(fm);
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
              
        if (Functions.preference.notify){
        	startService(new Intent(Activity_MainProfile.this, Service_Notification.class));
        }
        
        if ((savedInstanceState != null)) {
			mPagerPosition = savedInstanceState.getInt("mPagerPosition");
			mPagerOffsetPixels = savedInstanceState.getInt("mPagerOffsetPixels");
		}
                
    }
	
	
	public void btn_reload(View view){
    	TextView tv_loading = (TextView) view.findViewById(R.id.tv_loading);
    	tv_loading.setVisibility(View.VISIBLE);
    	ProgressBar pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
    	pb_loading.setVisibility(View.VISIBLE);
    	Button btn_reload = (Button) view.findViewById(R.id.btn_reload);
    	btn_reload.setVisibility(View.GONE);
    	if (Functions.isNetwork(this)) Fragment_MainProfile.al.execute();
    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu sub = menu.addSubMenu("");
		sub.setIcon(R.drawable.ic_communication_textsms);
        sub.add(0, 11, 0, "Сообщение в ленте");
        //sub.add(0, 100, 0, "Авторизация GCM");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
        return true;
	}
	
    //Actions меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		//Обновляем данные
  		Functions.getPreference(getBaseContext());
  		
  		Intent intent_add = new Intent(Activity_MainProfile.this, Activity_Add_Edit_Input.class);
  		switch (item.getItemId()) {
          	case android.R.id.home:
          		mDrawer.toggleMenu(true);
          		break;
          	case 11:
    			//Сообщение в ленте
          		intent_add.putExtra("post_type", "User");
	    		intent_add.putExtra("type", "user_page");
	    		intent_add.putExtra("commentable_id", Functions.preference.user_id);
	    		startActivityForResult(intent_add, 100); //100 add message or comments
    			return true;
          	case 100:
          		/*if (checkPlayServices()) {
                    Log.i(TAG, "VALID VALID");
                    gcm = GoogleCloudMessaging.getInstance(this);
                    
                    registerInBackground();
                    
                } else {
                    Log.i(TAG, "No valid Google Play Services APK found.");
                }*/
          		break;
  			}
  		intent_add = null;
  		return super.onOptionsItemSelected(item);        
    }
  	
  	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
  		super.onActivityResult(requestCode, resultCode, data);
  		
  		if (requestCode == 100) {
  			if (resultCode == 1) {
  				Fragment_MainWall.lv_main.setRefreshing(true);
  			}
  		}
  		Fragment_MainProfile.adapter_profile_menu.notifyDataSetChanged();
  		tv_unread.setText(String.valueOf(Functions.count_unread));
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
  	
  	//Сохранение значений элементов
  	@Override
  	public void onSaveInstanceState(Bundle outState) {
  		super.onSaveInstanceState(outState);
  		outState.putInt("mPagerOffsetPixels", mPagerOffsetPixels);
  		outState.putInt("mPagerPosition", mPagerPosition);
  	}

  	//////////////////////////////////////////////
  	
  	@Override
	protected void onResume() {
	    super.onResume();
	    //checkPlayServices();
	    //Log.d("refresh", "Обновляем данные");
	}
	
  	@Override
	public void onDestroy() {
	    TO_START = false;
	    super.onDestroy();
	}
  	
  	/*private void sendRegistrationIdToBackend() {
  		Log.d("GCM", "user: " + Functions.preference.user_id + " - token: " + this.regid);
	    SSDK_API.setGCMId(Functions.preference.user_id, this.regid, Functions.preference.kawai);
	}*/
  	
  	/*private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}*/
	
  	/*private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(Activity_MainProfile.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}*/
  	
  	/*private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}*/
	
  	/*
  	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}*/
	
  	/*
	private void registerInBackground() {
	    new AsyncTask<String, String, String>() {

	    	@Override
	        protected String doInBackground(String... params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(Constants.GCMID);
	                msg = "Device registered, registration ID=" + regid;
	                sendRegistrationIdToBackend();
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	            }
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	            Log.d("GCM1111",msg + "\n");
	        }
	        
	    }.execute(null, null, null);
	    
	}*/
	
	
}
