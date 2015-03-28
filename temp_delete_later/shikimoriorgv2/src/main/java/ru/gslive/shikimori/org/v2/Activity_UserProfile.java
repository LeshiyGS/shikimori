package ru.gslive.shikimori.org.v2;

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
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_UserProfile extends ShikiSherlockFragmentActivity {
	int rotate;
	int mPagerOffsetPixels;
    int mPagerPosition;
	static String target_id;
	String t_target_id;
	
	static Boolean in_fav = false;
	static Menu faw_menu;
	
	static TextView tv_unread;
	MenuDrawer mDrawer;
	
	static Functions.MenuAdapter adapter_menu;
	static ActionBar actionbar;
	
	public String[] menu_content;
	
    ActionBar mActionBar;
    ViewPager mPager;
    
    AsyncTask<Void, Void, Void> al_fav_add;
    AsyncTask<Void, Void, Void> al_fav_del;
 		
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
        Adapter_UserProfile viewpageradapter = new Adapter_UserProfile(fm);
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
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		faw_menu = menu;
		if (!Functions.preference.user_id.equals(t_target_id)){
			if (in_fav){
				menu.add(0, 12, 0, "")
				.setIcon(R.drawable.ic_social_person)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
			}else{
				menu.add(0, 12, 0, "")
				.setIcon(R.drawable.ic_social_person_outline)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
			}
		}
									
		SubMenu sub = menu.addSubMenu("");
		sub.setIcon(R.drawable.ic_communication_textsms);
        sub.add(0, 10, 0, "Личное сообщение");
        sub.add(0, 11, 0, "Сообщение в ленте");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
        return true;
	}
	
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
	        case Constants.IDD_DEL_ITEM:
	           builder.setMessage("Удалить пользователя из друзей?")
	                    .setCancelable(false)
	                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int id) {
	                                	al_fav_del = new AsyncDelFriend(t_target_id).execute();
	                    				in_fav = false;
	                    				faw_menu.findItem(12).setIcon(R.drawable.ic_social_person_outline);
	                                }
	                            })
	                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
	                                @SuppressWarnings("deprecation")
									public void onClick(DialogInterface dialog, int id) {
	                                    removeDialog(Constants.IDD_DEL_ITEM);
	                                }
	                            });
	            return builder.create();    
	        case Constants.IDD_ADD_ITEM:
	           builder.setMessage("Добавить пользователя в друзья?")
	                    .setCancelable(false)
	                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int id) {
	                                	al_fav_add = new AsyncAddFriend(t_target_id).execute();
	                    				in_fav = true;
	                    				faw_menu.findItem(12).setIcon(R.drawable.ic_social_person);
	                                	
	                                }
	                            })
	                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
	                         @SuppressWarnings("deprecation")
							public void onClick(DialogInterface dialog, int id) {
	                        	 removeDialog(Constants.IDD_ADD_ITEM);
	                         }
	                     });
	           return builder.create();
	         default:
	            return null;
        }
    }
	
    //Actions меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		Intent intent_add = new Intent(Activity_UserProfile.this, Activity_Add_Edit_Input.class);
  		switch (item.getItemId()) {
          	case android.R.id.home:
          		if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
        	    	finish();
        	    }else{
        	    	mDrawer.toggleMenu(true);
        	    }
          		break;
          	case 10:
    			//Личное сообщение
	    		intent_add.putExtra("type", "message");
	    		intent_add.putExtra("commentable_id", target_id);
	    		intent_add.putExtra("to_id", t_target_id);
	    		startActivity(intent_add);
    			return true;
    		case 11:
    			//Сообщение в ленте
	    		intent_add.putExtra("type", "user_page");
	    		intent_add.putExtra("commentable_id", target_id);
	    		startActivityForResult(intent_add, 100); //100 add message or comments
    			return true;
    		case 12:
    			if (in_fav){
    				showDialog(Constants.IDD_DEL_ITEM);
    			}else{
    				showDialog(Constants.IDD_ADD_ITEM);
    			}
    			return true;
  		}
  		intent_add = null;
  		return super.onOptionsItemSelected(item);        
    }

  	//
  	@Override
 	public void onActivityResult(int requestCode, int resultCode, Intent data) {
   		super.onActivityResult(requestCode, resultCode, data);
   		if (requestCode == 0) {
   			if (resultCode == 3) {
   				if (Functions.isNetwork(getBaseContext())){
   					Fragment_UserWall.al_clear.execute();
   				}
   			}
   		}
   		if (requestCode == Constants.ADD_NEW_COMMENT) {
  			if (resultCode == 1) {
  				Fragment_UserWall.al_clear.execute();
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
  	
  	private class AsyncAddFriend extends AsyncTask<Void, Void, Void> {
		private String id; 
		
		public AsyncAddFriend(String id) {
			 this.id = id;
		}
		
		@Override
		protected void onPreExecute() {
		}	
		  	
		protected void onPostExecute(Void result1) {
			tv_unread.setText(String.valueOf(Functions.count_unread));
			Functions.updateWidget(getBaseContext());
			Toast.makeText(getBaseContext(), "Добавлен(а) в друзья.", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			SSDK_API.addFriend(id);
			SSDK_API.getUnread(Functions.preference.kawai);				
			return null;
		}
		
	}
  	
  	private class AsyncDelFriend extends AsyncTask<Void, Void, Void> {
		private String id; 
		
		public AsyncDelFriend(String id) {
			 this.id = id;
		}
		
		@Override
		protected void onPreExecute() {
		}	
		  	
		protected void onPostExecute(Void result1) {
			tv_unread.setText(String.valueOf(Functions.count_unread));
			Functions.updateWidget(getBaseContext());
			Toast.makeText(getBaseContext(), "Удален(а) из друзей.", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			SSDK_API.delFriend(id);
			SSDK_API.getUnread(Functions.preference.kawai);				
			return null;
		}
		
	}

  	@Override
    protected void onResume() {
  		target_id = t_target_id;
        super.onResume();
    }
  	
  	static public void friends(){
  		if (in_fav && faw_menu != null){
			faw_menu.findItem(12).setIcon(R.drawable.ic_social_person);
		}else{
			faw_menu.findItem(12).setIcon(R.drawable.ic_social_person_outline);
		}
  	}
  	
  	//Сохранение значений элементов
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("target_id", target_id);
    }

}
