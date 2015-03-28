package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressWarnings("deprecation")
public class Activity_ClubImages extends ShikiSherlockActivity {
	int rotate;
	
	MenuDrawer mDrawer;
	
	Functions.MenuAdapter adapter_menu;
	ActionBar actionbar;
	
	ArrayList<SSDK_Images> result = new ArrayList<SSDK_Images>();
	
	AsyncTask<Void, Void, Void> al;
	
	String club_id;
	
	Gallery gallery;
	WebView wv_gallery;
	
	ImageAdapter adapter;
	
	private GestureDetector gs = null;
	
	//
	public class ImageAdapter extends BaseAdapter {
		
	    @Override
	    public int getCount() {
	        return result.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return result.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    @Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
	    	ImageView iv = new ImageView(getBaseContext());
	    	ImageLoader.getInstance().displayImage(result.get(position).preview, iv);
			//iv.setScaleType(ImageView.ScaleType.FIT_XY);
			//iv.setLayoutParams(new Gallery.LayoutParams(150, 120));
			iv.setBackgroundColor(getBaseContext().getResources().getColor(R.color.white));
			return iv;
	    }
	}
	
	//
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Functions.getPreference(getBaseContext());
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setTheme(R.style.AppTheme_ActionBarStyle_BackHome);
			
		if (Functions.preference.theme.equals("ligth")){
			setContentView(R.layout.w_activity_club_images);
		}else{
			setContentView(R.layout.d_activity_club_images);
		}
		
		
		actionbar = getSupportActionBar();
		actionbar.setSubtitle("Галлерея клуба");
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		super.onCreate(savedInstanceState);
		
		
		
		club_id = getIntent().getExtras().getString("club_id");
		
		//Инициализация элементов    
		gallery = (Gallery) findViewById(R.id.gv_club);
		wv_gallery = (WebView) findViewById(R.id.wv_gallery);
		adapter = new ImageAdapter();
		gallery.setAdapter(adapter);
		gallery.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  //AQuery aq;
		    	  //aq = new AQuery(getBaseContext());
		    	  //aq.id(wv_gallery).progress(R.id.progress).webImage(result.get(position).original, true, false, 0);
                  wv_gallery.loadUrl(result.get(position).original);
		    	  //imageLoader.DisplayImage(result.get(position).original, iv_gallery);
		      }
		    });
		
		
		
		OnTouchListener onTouch = new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
	        	if (gs == null) {
	                gs = new GestureDetector(getBaseContext(),
	                        new GestureDetector.SimpleOnGestureListener() {
	                            @SuppressLint("InlinedApi")
								@Override
	                            public boolean onDoubleTapEvent(MotionEvent e) {

	                                //Double Tap
	                            	
	                            	Dialog mSplashDialog = new Dialog(Activity_ClubImages.this, android.R.style.Theme_Black_NoTitleBar);
	                        		mSplashDialog.setContentView(R.layout.fullscreen_image);
	                        		ImageView iv = (ImageView) mSplashDialog.findViewById(R.id.iv_fullScreen);
	                        		//AQuery aq;
	                 		        //aq = new AQuery(getBaseContext());
	                 		        //aq.id(iv).progress(R.id.progress).image(result.get(gallery.getSelectedItemPosition()).original, false, true);
                                    Ion.with(iv)
                                            .animateLoad(R.anim.spin_animation)
                                            .error(R.drawable.missing_preview)
                                            .load(result.get(gallery.getSelectedItemPosition()).original);
                                    mSplashDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	                        		mSplashDialog.setCancelable(true);
	                        		mSplashDialog.show();
	                            	return true;
	                            }

	                            @Override
	                            public boolean onSingleTapConfirmed(MotionEvent e) {

	                                //Single Tab
	                            	//wv_gallery.zoomOut();// Zoom out
	                                return false;
	                            };
	                        });
	            }

	            gs.onTouchEvent(event);

	            return false;
	        }
	    }; 
	    
	    wv_gallery.setOnTouchListener(onTouch);
				
        //Настройка меню

		if ((savedInstanceState != null)) {
        	result = (ArrayList<SSDK_Images>) savedInstanceState.getSerializable("result");
        	if (result.size() > 0) {
	  			//AQuery aq = new AQuery(getBaseContext());
	  	  	  	//aq.id(wv_gallery).progress(R.id.progress).webImage(result.get(0).original, true, false, 0);
                wv_gallery.loadUrl(result.get(0).original);
  			}
        	adapter.notifyDataSetChanged();
		}else{
			if (Functions.isNetwork(getBaseContext())) { 
				al = new AsyncListLoading().execute();
			}
		}
		
		
	}

	//События нажатий на меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		switch (item.getItemId()) {
          	case android.R.id.home:
       	    	finish();
          		break;
  			}
  		return super.onOptionsItemSelected(item);        
    }
  	
  	//Асинхронная задача загрузки Изображений
  	private class AsyncListLoading extends AsyncTask<Void, Void, Void> {

  		@Override
  		protected void onPreExecute() {
  			setSupportProgressBarIndeterminateVisibility(true);
  		}
  				  
  		protected void onPostExecute(Void result1) {
  			//tv_unread.setText(String.valueOf(Functions.count_unread));
  			if (result.size() > 0) {
	  			//AQuery aq = new AQuery(getBaseContext());
	  	  	  	//aq.id(wv_gallery).progress(R.id.progress).webImage(result.get(0).original, true, false, 0);
                wv_gallery.loadUrl(result.get(0).original);
  			}
  			adapter.notifyDataSetChanged();
  			setSupportProgressBarIndeterminateVisibility(false);
  		}

  		@Override
  		protected Void doInBackground(Void... arg0) {
  			try {
  				result = SSDK_API.getClubImages(club_id,Functions.preference.kawai);
  				SSDK_API.getUnread(Functions.preference.kawai);
  				//if (t_result.size()>0) stop_update = false; else stop_update = true;
  			} catch (IOException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			} catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			return null;
  		}
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
	
  	/*@Override
    public void onLowMemory(){  
        BitmapAjaxCallback.clearCache();
    }*/
  	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("result", result);
    }

}
