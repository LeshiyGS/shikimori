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
import com.actionbarsherlock.view.Window;

import com.viewpagerindicator.TitlePageIndicator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class Activity_AnimeManga extends ShikiSherlockFragmentActivity {
	
	public MenuDrawer mDrawer;
	TextView tv_unread;
	int mPagerOffsetPixels;
    int mPagerPosition = 1;
    int rotate;
	
	Functions.MenuAdapter adapter_menu;
	public static ActionBar actionbar;
	
	static String target_id;
	static String target_type;
	static String target_rate;
	String t_target_id;
	String t_target_type;
	String t_target_rate;
	
	static InputMethodManager imm;
	
	AsyncTask<Void, Void, Void> change_score;
	AsyncTask<Void, Void, Void> delete_favour;
	AsyncTask<Void, Void, Void> delete;
	AsyncTask<Void, Void, Void> al_read_all;
		
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
		
		t_target_id = getIntent().getExtras().getString("id");
        t_target_type = getIntent().getExtras().getString("type");
        if (t_target_id == null){
        	if (getIntent().getData().toString().contains("/animes/")){
        		t_target_id = getIntent().getData().toString().substring(getIntent().getData().toString().lastIndexOf("/animes/") + 8, getIntent().getData().toString().lastIndexOf("-"));
        		t_target_type = "anime";
        	}else{
        		t_target_id = getIntent().getData().toString().substring(getIntent().getData().toString().lastIndexOf("/mangas/") + 8, getIntent().getData().toString().lastIndexOf("-"));
        		t_target_type = "manga";
        	}
        }
        
		target_id = t_target_id;
		target_type = t_target_type;
		
		
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		
		if (Functions.preference.theme.equals("ligth")){
			mDrawer = Functions.setMenuDrawer(actionbar, "Аниме", R.layout.w_activity_main_profile, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, "Аниме", R.layout.d_activity_main_profile, getWindowManager().getDefaultDisplay().getRotation(),
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
        Adapter_Anime_Manga viewpageradapter = new Adapter_Anime_Manga(fm);
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
	    	tv_unread.setText(String.valueOf(Functions.count_unread));
		}
        
    }
	   
	//
    protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
	        case Constants.IDD_DEL_ITEM:
	           builder.setMessage("Вы действительно хотите удалить из списка?")
	                    .setCancelable(false)
	                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int id) {
	                                	if (Functions.isNetwork(getBaseContext())) 
	                                		if (target_type.equals("anime")){
	                                			delete = new AsyncDelete(target_rate).execute();
	                                		}else{
	                                			delete = new AsyncDelete(target_rate).execute();
	                                		}
	                                }
	                            })
	                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
	                                @SuppressWarnings("deprecation")
									public void onClick(DialogInterface dialog, int id) {
	                                    removeDialog(Constants.IDD_DEL_ITEM);
	                                }
	                            });
	            return builder.create();    
	        case Constants.IDD_DEL_FAV:
	           builder.setMessage("Вы действительно хотите удалить из избранного?")
	                    .setCancelable(false)
	                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
	                                public void onClick(DialogInterface dialog, int id) {
	                                	if (Functions.isNetwork(getBaseContext())) 
	                                		if (target_type.equals("anime")){
	                                			delete_favour = new AsyncDeleteFavour("http://shikimori.org" + Fragment_Anime_Manga_Menu.url).execute();
	                                		}else{
	                                			delete_favour = new AsyncDeleteFavour("http://shikimori.org" + Fragment_Anime_Manga_Menu.url).execute();
	                                		}
	                                }
	                            })
	                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
	                         @SuppressWarnings("deprecation")
							public void onClick(DialogInterface dialog, int id) {
	                        	 removeDialog(Constants.IDD_DEL_FAV);
	                         }
	                     });
	           return builder.create();
	        case Constants.IDD_CHANCHE_SCORE:
		           builder.setMessage("Вы действительно хотите изменить оценку?")
		                    .setCancelable(false)
		                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
		                                public void onClick(DialogInterface dialog, int id) {
		                                	if (Functions.isNetwork(getBaseContext())) 
		                                		if (target_type.equals("anime")){
		                        					change_score = new AsyncChangeScore(Float.toString(Fragment_Anime_Manga_Menu.temp_rating*2)).execute();
		                        				}else{
		                        					change_score = new AsyncChangeScore(Float.toString(Fragment_Anime_Manga_Menu.temp_rating*2)).execute();
		                        				}
		                                }
		                            })
		                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
		                                @SuppressWarnings("deprecation")
										public void onClick(DialogInterface dialog, int id) {
		                                	Fragment_Anime_Manga_Menu.rb_my_score.setRating(Fragment_Anime_Manga_Menu.my_score/2);
		                                	removeDialog(Constants.IDD_CHANCHE_SCORE);
		                                }
		                            });
		            return builder.create();   
		            
	        	case Constants.IDD_WAIT:
		        	ProgressDialog dialog = new ProgressDialog(this);
		        	dialog.setMessage("Обновляем информацию");
		        	dialog.setCancelable(false);
		        	return dialog;
	         default:
	            return null;
        }
    }
	
	//
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu sub = menu.addSubMenu("");
		sub.setIcon(R.drawable.ic_communication_textsms);
        sub.add(0, 10, 0, "Комментировать");
        sub.add(0, 11, 0, "Отзыв");
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
        menu.add(0, 12, 0, "")
		.setIcon(getBaseContext().getResources().getDrawable(R.drawable.ic_communication_clear_all))
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT );
        return true;
	}
	
    //Actions меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		Intent intent_add = new Intent(Activity_AnimeManga.this, Activity_Add_Edit_Input.class);
  		rotate = getWindowManager().getDefaultDisplay().getRotation();
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
    			intent_add.putExtra("commentable_id", Fragment_Anime_Manga_Info.thread_id);
	    		startActivityForResult(intent_add, 100); //100 add message or comments
	    		break;
          	case 11:
    			//Отзыв
	    		intent_add.putExtra("type", "review");
    			intent_add.putExtra("commentable_id", Fragment_Anime_Manga_Info.thread_id);
	    		startActivityForResult(intent_add, 100); //100 add message or comments
	    		break;
          	case 12:
    			al_read_all = new AsyncRead().execute();
	    		break;
  			}
  		return super.onOptionsItemSelected(item);        
    }
   
  	//
  	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
  		super.onActivityResult(requestCode, resultCode, data);
  		if (requestCode == 100) {
  			if (resultCode == 1) {
  				Fragment_Anime_Manga_Comments.al.execute();
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
  	  	
  	//Восстановление значений при возврате в активити
  	@Override
    protected void onResume() {
  		target_id = t_target_id;
  		target_type = t_target_type;
  		target_rate = t_target_rate;
        super.onResume();
    }
  	
  	//Удаление из списка  
  	private class AsyncDelete extends AsyncTask<Void, Void, Void> {
		String user_rate;
		
		public AsyncDelete(String user_rate) {
	        this.user_rate = user_rate;

	    }	  
		
		@Override
		protected void onPreExecute() {

		}
			  
		protected void onPostExecute(Void result1) {
			Fragment_Anime_Manga_Menu.al.execute();	
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			SSDK_API.AnimeMangaDelete(user_rate);
			return null;
		}
	}
  	
  	//Удаление из избранного
  	private class AsyncDeleteFavour extends AsyncTask<Void, Void, Void> {
  		String link;
  		
  		public AsyncDeleteFavour(String link) {
  	        this.link = link;

  	    }  
  		
  		@Override
  		protected void onPreExecute() {

  		}
  		  
  		protected void onPostExecute(Void result1) {
  			Fragment_Anime_Manga_Menu.al.execute();		
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			String fav_link;
  			if (Activity_AnimeManga.target_type.equals("anime")){
  				fav_link = "http://shikimori.org/favourites/Anime" + link.substring(link.lastIndexOf("/"));
  			}else{
  				fav_link = "http://shikimori.org/favourites/Manga" + link.substring(link.lastIndexOf("/"));
  			}
  			try {
  				String[] token = SSDK_API.getToken(Functions.preference.kawai);
  				Response res = Jsoup
  					.connect(fav_link)
  					.ignoreContentType(true)
  					.header("X-CSRF-Token", token[0])
  					.cookie("_kawai_session", token[1])
  					.data("_method","delete")
  					.method(Method.POST)
  					.execute();
  				if (res.statusCode() == 200){
    					//Log.d("ListChange", "OK");
    				}
  			} catch (IOException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			return null;
  		}

  			
  	 }
  	
    //Изменение оценки
  	private class AsyncChangeScore extends AsyncTask<Void, Void, Void> {
  		String score;
  		
  		public AsyncChangeScore(String score) {
    			this.score = score;
      		}
  		
  		@Override
  		protected void onPreExecute() {
  			t_target_rate = target_rate;
  		}
  		  
  		protected void onPostExecute(Void result1) {
  			if (Functions.isNetwork(getBaseContext())) Fragment_Anime_Manga_Menu.al.execute();
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			if (target_type.equals("anime")){
  				SSDK_API.setAnimeMangaScore("Anime", target_rate, score);
  			} else {
  				SSDK_API.setAnimeMangaScore("Manga", target_rate, score);
  			}
  			return null;
  		}
  	}
  	
  	
  	private class AsyncRead extends AsyncTask<Void, Void, Void> {
  		@Override
  		protected void onPreExecute() {
  			setSupportProgressBarIndeterminateVisibility(true);
  		}
  				  
  		protected void onPostExecute(Void result1) {
  			Fragment_Anime_Manga_Comments.adapter_list.notifyDataSetChanged();
  			setSupportProgressBarIndeterminateVisibility(false);
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			for (int i=0; i<Fragment_Anime_Manga_Comments.share_result.size();i++){
  				if (!Fragment_Anime_Manga_Comments.share_result.get(i).viewed){
  					SSDK_API.readComment(Fragment_Anime_Manga_Comments.share_result.get(i).id);
  					Fragment_Anime_Manga_Comments.share_result.get(i).viewed = true;
  				}
  			}
  			SSDK_API.getUnread(Functions.preference.kawai);
  			return null;
  		}

  		
  	}
  	
  	//
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(target_type, target_type);
        outState.putString(t_target_type, t_target_type);
        outState.putString(target_id, target_id);
        outState.putString(t_target_id, t_target_id);
        outState.putInt("mPagerOffsetPixels", mPagerOffsetPixels);
  		outState.putInt("mPagerPosition", mPagerPosition);
    }
 
}
