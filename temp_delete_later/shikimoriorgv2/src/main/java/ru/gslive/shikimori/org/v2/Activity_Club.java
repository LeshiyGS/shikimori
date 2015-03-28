package ru.gslive.shikimori.org.v2;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
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

public class Activity_Club extends ShikiSherlockFragmentActivity {
	int rotate;
	int mPagerOffsetPixels;
    int mPagerPosition = 1;
	
	ListView lv_menu;
	TextView tv_unread;
	MenuDrawer mDrawer;
	
	Functions.MenuAdapter adapter_menu;
	static ActionBar actionbar;
	
	static String target_id;
	String t_target_id;
	
    ActionBar mActionBar;
    ViewPager mPager;
    
    AsyncTask<Void, Void, Void> al_read_all;
 	
    //Создаем Активити с фрагментами
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
		if (t_target_id == null){
        	t_target_id = getIntent().getData().toString().substring(getIntent().getData().toString().lastIndexOf("/clubs/") + 7, getIntent().getData().toString().lastIndexOf("-"));
        	Log.d("target url", "-> " + getIntent().getData());	
        }
        target_id = t_target_id;
		
		//Подключение и выбор варианта меню.
        if (Functions.preference.theme.equals("ligth")){
        	mDrawer = Functions.setMenuDrawer(actionbar, "Клуб", R.layout.w_activity_main_profile, getWindowManager().getDefaultDisplay().getRotation(),
    				getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, "Клуб", R.layout.d_activity_main_profile, getWindowManager().getDefaultDisplay().getRotation(),
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
        Adapter_Club viewpageradapter = new Adapter_Club(fm);
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
        
        if ((savedInstanceState != null)) {
	    	mPagerPosition = savedInstanceState.getInt("mPagerPosition");
			mPagerOffsetPixels = savedInstanceState.getInt("mPagerOffsetPixels");
			t_target_id = savedInstanceState.getString("target_id");
			target_id = t_target_id;
	    	tv_unread.setText(String.valueOf(Functions.count_unread));
		}
    }
	
	//
    protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
	        case Constants.IDD_CLUB_OUT:
	           builder.setMessage("Вы действительно хотите выйти из клуба?")
	                    .setCancelable(false)
	                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int id) {
	                                	if (Functions.isNetwork(getBaseContext())){
	                                		Fragment_Club_Menu.al_out.execute();
	                                	}
	                                }
	                            })
	                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
	                         @SuppressWarnings("deprecation")
							public void onClick(DialogInterface dialog, int id) {
	                        	 removeDialog(Constants.IDD_CLUB_OUT);
	                         }
	                     });
	           return builder.create();
	         default:
	            return null;
        }
    }
	
	
	//Создаем меню в акшн баре
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0, 10, 0, "")
		.setIcon(getBaseContext().getResources().getDrawable(R.drawable.ic_communication_textsms))
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
		menu.add(0, 11, 0, "")
		.setIcon(getBaseContext().getResources().getDrawable(R.drawable.ic_communication_clear_all))
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
        return true;
	}
	
	//Обрабатываем ответы от других активити
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
  		super.onActivityResult(requestCode, resultCode, data);
  		if (requestCode == 100) {
  			if (resultCode == 1) {
  				Fragment_Club_Comments.al.execute();
  			}
  		}
  	}
	
    //Actions меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		Intent intent_add = new Intent(Activity_Club.this, Activity_Add_Edit_Input.class);
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
    			intent_add.putExtra("commentable_id", Fragment_Club_Info.thread_id);
    			startActivityForResult(intent_add, 100); //100 add message or comments
	    		break;
          	case 11:
    			al_read_all = new AsyncRead().execute();
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
  	
  	private class AsyncRead extends AsyncTask<Void, Void, Void> {
  		@Override
  		protected void onPreExecute() {
  			setSupportProgressBarIndeterminateVisibility(true);
  		}
  				  
  		protected void onPostExecute(Void result1) {
  			Fragment_Club_Comments.adapter_list.notifyDataSetChanged();
  			setSupportProgressBarIndeterminateVisibility(false);
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			for (int i=0; i<Fragment_Club_Comments.share_result.size();i++){
  				if (!Fragment_Club_Comments.share_result.get(i).viewed){
  					SSDK_API.readComment(Fragment_Club_Comments.share_result.get(i).id);
  					Fragment_Club_Comments.share_result.get(i).viewed = true;
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
