package ru.gslive.shikimori.org.v2;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.viewpagerindicator.TitlePageIndicator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_People extends ShikiSherlockFragmentActivity {

	int rotate;
	int mPagerOffsetPixels;
    int mPagerPosition = 1;
	
	ListView lv_menu;
	static TextView tv_unread;
	MenuDrawer mDrawer;
	
	static Functions.MenuAdapter adapter_menu;
	static ActionBar actionbar;
	
	static String target_id;
	String t_target_id;
	static String target_type;
	String t_target_type;
	
	private final int DIALOG_DELETE_FAV = 1; //Подтверждение удаления из списка избранного
	
	static AsyncTask<Void, Void, Void> add_favour;
	static AsyncTask<Void, Void, Void> delete_favour;
	
    ActionBar mActionBar;
    ViewPager mPager;
 		
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
		
		t_target_id = getIntent().getExtras().getString("id");
        if (t_target_id == null){
        	t_target_id = getIntent().getData().toString().substring(getIntent().getData().toString().lastIndexOf("/") + 1, getIntent().getData().toString().lastIndexOf("-"));
        }
        target_id = t_target_id;
        
		//Подключение и выбор варианта меню.
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
        Adapter_People viewpageradapter = new Adapter_People(fm);
        mPager.setAdapter(viewpageradapter);
        mPager.setOffscreenPageLimit(3);
        TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);
        titleIndicator.setViewPager(mPager);
        titleIndicator.setCurrentItem(1);
        titleIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPagerPosition = position;
                mPagerOffsetPixels = positionOffsetPixels;
            }
        });
                
    }
	
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
	        case DIALOG_DELETE_FAV:
	           builder.setMessage("Вы действительно хотите удалить из избранного?")
	                    .setCancelable(false)
	                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int id) {
	                                	if (Functions.isNetwork(getBaseContext())) 
	                                			delete_favour = new AsyncDelFavour().execute();
	                                }
	                            })
	                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
	                         @SuppressWarnings("deprecation")
							public void onClick(DialogInterface dialog, int id) {
	                             removeDialog(DIALOG_DELETE_FAV);
	                         }
	                     });
	           return builder.create();
	         default:
	            return null;
        }
    }
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu sub = menu.addSubMenu("");
		sub.setIcon(R.drawable.ic_communication_textsms);
        sub.add(0, 10, 0, "Комментировать");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
        return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
  		super.onActivityResult(requestCode, resultCode, data);
  		if (requestCode == 100) {
  			if (resultCode == 1) {
  				Fragment_People_Comments.lv_main.setRefreshing(true);
  			}
  		}
  	}
	
    //Actions меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		Intent intent_add = new Intent(Activity_People.this, Activity_Add_Edit_Input.class);
  		switch (item.getItemId()) {
          	case android.R.id.home:
          		if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
        	    	finish();
        	    }else{
        	    	mDrawer.toggleMenu(true);
        	    }
          		break;
          	case 10:
    			//Комментарий
	    		intent_add.putExtra("type", "comment");
    			intent_add.putExtra("commentable_id", Fragment_People_Info.result.thread_id);
    			startActivityForResult(intent_add, 100); //100 add message or comments
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

  	//Восстановление значений при возврате в активити
  	@Override
    protected void onResume() {
  		target_id = t_target_id;
        super.onResume();
    }
  	
  	private class AsyncDelFavour extends AsyncTask<Void, Void, Void> {
		
		public AsyncDelFavour() {

	    }  
				
		@Override
		protected void onPreExecute() {

		}
		  
		protected void onPostExecute(Void result1) {
			Fragment_People_Menu.in_list_adapter.notifyDataSetChanged();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			String fav_link;
			if (Fragment_People_Menu.person_type.equals("producer")){
				fav_link = Constants.SERVER + "/favourites/producer/Person" + Fragment_People_Info.result.url.substring(Fragment_People_Info.result.url.lastIndexOf("/"));
			}else if(Fragment_People_Menu.person_type.equals("mangaka")){
				fav_link = Constants.SERVER + "/favourites/mangaka/Person" + Fragment_People_Info.result.url.substring(Fragment_People_Info.result.url.lastIndexOf("/"));
			}else if(Fragment_People_Menu.person_type.equals("seyu")){
				fav_link = Constants.SERVER + "/favourites/seyu/Person" + Fragment_People_Info.result.url.substring(Fragment_People_Info.result.url.lastIndexOf("/"));
			}else{
				fav_link = Constants.SERVER + "/favourites/person/Person" + Fragment_People_Info.result.url.substring(Fragment_People_Info.result.url.lastIndexOf("/"));
			}
			
			Log.d("del_favor", "-> " + fav_link);
			
			try {
				String[] token = SSDK_API.getToken(Functions.preference.kawai);
				Response res = Jsoup
					.connect(fav_link)
					.header("X-CSRF-Token", token[0])
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
					.ignoreContentType(true)
					.cookie("_kawai_session", token[1])
					.method(Method.POST)
					.data("_method","delete")
					.execute();
				if (res.statusCode() == 200){
					Fragment_People_Menu.in_favourites = false;
  				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

			
	 }
}
