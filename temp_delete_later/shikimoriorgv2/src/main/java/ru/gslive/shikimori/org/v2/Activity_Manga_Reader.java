package ru.gslive.shikimori.org.v2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.Toast;

public class Activity_Manga_Reader extends FragmentActivity {

	Boolean loadtype;
	static String link = "";
	private AsyncLogin loader;
	
	private final int DIALOG_SEARCH = 1;
	private final int DIALOG_SEPARATE = 2;
	private final int DIALOG_LOAD = 3;
	
	String pic_html ="";
	Boolean fullscreen = false;
		
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
		
	ListView lv_main;
	WebView web;
		
	static ArrayList<String> page_link = new ArrayList<String>();
	ArrayList<String> t_page_link = new ArrayList<String>();

		
		//
		public class SectionsPagerAdapter extends FragmentPagerAdapter {
			
			public SectionsPagerAdapter(FragmentManager fm) {
				super(fm);
			}

			@Override
			public Fragment getItem(int position) {
				Fragment fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position);
				fragment.setArguments(args);
				return fragment;
			}

			@Override
			public int getCount() {
				return t_page_link.size();
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return "Страница " + (position+1) + " из " + (t_page_link.size());
			}
		}
		
		//
		public class DummySectionFragment extends Fragment {

			public static final String ARG_SECTION_NUMBER = "section_number";

			public DummySectionFragment() {
				
			}

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container,
					Bundle savedInstanceState) {
				View rootView = inflater.inflate(R.layout.web_layout,container, false);
				WebView web = (WebView) rootView.findViewById(R.id.web_image);
                web.getSettings().setLoadWithOverviewMode(true);
                web.getSettings().setUseWideViewPort(true);
                web.getSettings().setSupportZoom(true);
                web.getSettings().setBuiltInZoomControls(true);
                web.getSettings().setDisplayZoomControls(false);
				int page;
				page = getArguments().getInt(ARG_SECTION_NUMBER)+1;
			    String temp_dir = link.split("http://")[1];
			    
				temp_dir = temp_dir.substring(temp_dir.indexOf("/"), temp_dir.length()) + "/";
			    File file = new File(Functions.preference.cache_dir + "/shikimori/manga/" + temp_dir, page + ".jpg");
	            if(file.exists()){
                    web.loadUrl("file:///" + file);
	            	//aq.id(web).progress(null).webImage("file:///" + file, true, false, 0);
	            } else{
                    web.loadUrl(page_link.get(getArguments().getInt(ARG_SECTION_NUMBER)));
	            	//aq.id(web).progress(null).webImage(page_link.get(getArguments().getInt(ARG_SECTION_NUMBER)), true, false, 0);
	            }
				return rootView;
			}
		}

		//
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.blank, menu);
			return true;
		}
		
		@Override
	  	public boolean onKeyDown(int keyCode, KeyEvent event) {
	  	    if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
	  	    	if (mViewPager.getCurrentItem() > 0){
	  	    		mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
	  	    	}
	  	    	return true;
	  	    }
	  	    if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
	  	    	if (mViewPager.getCurrentItem() < page_link.size()){
	  	    		mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
	  	    	}
	  	    	return true;
	  	    }
	  	    
	  	    return super.onKeyDown(keyCode, event);
	  	}
		
		//
		@Override
	    protected Dialog onCreateDialog(int id) {
			switch (id) {
	         case DIALOG_SEARCH:
	        	ProgressDialog dialog = new ProgressDialog(this);
	        	dialog.setMessage("Ищем страницы манги..");
	        	dialog.setCancelable(false);
	        	return dialog;
	         case DIALOG_SEPARATE:
	         	ProgressDialog dialog2 = new ProgressDialog(this);
	         	dialog2.setMessage("Разбираем страницы манги...");
	         	dialog2.setCancelable(false);
	         	return dialog2;
	         case DIALOG_LOAD:
		         	ProgressDialog dialog3 = new ProgressDialog(this);
		         	dialog3.setMessage("Загружаем скаченную мангу...");
		         	dialog3.setCancelable(false);
		         	return dialog3;
	        default:
	            return null;
	        }
	    }
				
		//
		@SuppressLint("NewApi")
		@Override
		public void onCreate(Bundle savedInstanceState) {

			Functions.getPreference(getBaseContext());
			
			super.onCreate(savedInstanceState);
			
			getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
			
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	    	if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
	    		getActionBar().hide();
	    	} else{
	    		requestWindowFeature(Window.FEATURE_ACTION_BAR);
	    	}
			
			setContentView(R.layout.w_activity_manga_reader);
				
			link = getIntent().getExtras().getString("link");
			
			mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
			mSectionsPagerAdapter.notifyDataSetChanged();

			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mSectionsPagerAdapter);
			mViewPager.setOffscreenPageLimit(3);
			page_link.clear();
			startLoading();
		    
		}
			
		//
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub

			// Операции для выбранного пункта меню
			switch (item.getItemId()) {
			case R.id.action_settings:
				if (fullscreen){
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					fullscreen = false;
				}else{
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					fullscreen = true;
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}
		
		//Определение асинхронной загрузки (Логирования)
		public void startLoading() {
			if (loader == null || loader.getStatus().equals(AsyncTask.Status.FINISHED)) {
	    		loader = new AsyncLogin();
	    		loader.execute();
	    	} else {
	    		Toast.makeText(Activity_Manga_Reader.this, "Ждите завершения загрузки", Toast.LENGTH_SHORT)
				.show();
	    	}
	    }
		
		//Асинхронная загрузка (Логинимся и получаем информацию о пользователе)
		private class AsyncLogin extends AsyncTask<Void, Void, Void> {
		  
		  	@SuppressWarnings("deprecation")
			@Override
		  	protected void onPreExecute() {
		  		t_page_link.clear();
		  		showDialog(DIALOG_SEARCH);
			
			}
			  
		  	@SuppressWarnings("deprecation")
			protected void onPostExecute(Void result1) {
		  		removeDialog(DIALOG_SEARCH);
		  		page_link.clear();
		  		page_link = t_page_link;
		  		mSectionsPagerAdapter.notifyDataSetChanged();
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				Document doc = null;
				try {
					doc = Jsoup
						    .connect(link+"?mature=1")
						    .userAgent("Mozilla")
						    .referrer("http://shikimori.org/")
						    .timeout(60000)
						    .get();
					/*	
					if (doc.select("div[class^=pageBlock reader]").size()<0){
						doc = Jsoup
								.connect(link)
								.userAgent("Mozilla")
								.referrer("http://shikimori.org/")
								.timeout(60000)
								.get();
					}
						*/
					pic_html = doc.select("div[class^=pageBlock reader]").first().select("script[type=text/javascript]").html();
					pic_html = pic_html.substring(pic_html.indexOf("["),pic_html.indexOf("]"));
					pic_html = pic_html.replaceAll("\",w", "#");
							
					for (String src : pic_html.split("#")){
						if (src.contains("http")){
							t_page_link.add(src.substring(src.indexOf("http"), src.length()));
						}
					}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				return null;
			}

			
	  }
		
	}
