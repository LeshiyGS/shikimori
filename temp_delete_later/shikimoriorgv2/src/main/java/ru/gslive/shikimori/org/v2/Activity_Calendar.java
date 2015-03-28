package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;

import net.simonvt.menudrawer.MenuDrawer;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_Calendar extends ShikiSherlockActivity {

	int rotate;
	
	ActionBar actionbar;
	
	AsyncTask<Void, Void, Void> al;
	
	ArrayList<SSDK_Calendar> result = new ArrayList<SSDK_Calendar>();
	
	ArrayList<SSDK_Calendar> d0 = new ArrayList<SSDK_Calendar>();
	ArrayList<SSDK_Calendar> d1 = new ArrayList<SSDK_Calendar>();
	ArrayList<SSDK_Calendar> d2 = new ArrayList<SSDK_Calendar>();
	ArrayList<SSDK_Calendar> d3 = new ArrayList<SSDK_Calendar>();
	ArrayList<SSDK_Calendar> d4 = new ArrayList<SSDK_Calendar>();
	ArrayList<SSDK_Calendar> d5 = new ArrayList<SSDK_Calendar>();
	ArrayList<SSDK_Calendar> d6 = new ArrayList<SSDK_Calendar>();
	ArrayList<SSDK_Calendar> d7 = new ArrayList<SSDK_Calendar>();
			
	Functions.MenuAdapter adapter_menu;
	
	MenuDrawer mDrawer;
	TextView tv_unread;
	
	LinearLayout ll_load;
	TextView tv_d0,tv_d1,tv_d2,tv_d3,tv_d4,tv_d5,tv_d6,tv_d7;
	View_GridView gv_d0,gv_d1,gv_d2,gv_d3,gv_d4,gv_d5,gv_d6,gv_d7;
	
	CalendarAdapterD0 adapter_d0;
	CalendarAdapterD1 adapter_d1;
	CalendarAdapterD2 adapter_d2;
	CalendarAdapterD3 adapter_d3;
	CalendarAdapterD4 adapter_d4;
	CalendarAdapterD5 adapter_d5;
	CalendarAdapterD6 adapter_d6;
	CalendarAdapterD7 adapter_d7;
	
	
	//Адаптер меню
	public class CalendarAdapterD0 extends BaseAdapter {
				private LayoutInflater mLayoutInflater;

				public CalendarAdapterD0 (Context ctx) {  
				      mLayoutInflater = LayoutInflater.from(ctx);  
				}

				@Override
				public int getCount() {
					if (d0.size() != 0) return d0.size();
					else return 1;
				}

				@Override
				public Object getItem(int position) {
					return d0.get(position);
				}

				@Override
				public long getItemId(int position) {
					return 0;
				}

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					final Functions.ViewHolder holder;
					if (d0.size() != 0){
						if (convertView == null) {
							holder = new Functions.ViewHolder();
							if (Functions.preference.theme.equals("ligth")){
								convertView = mLayoutInflater.inflate(R.layout.w_item_faivourits, null);
							}else{
								convertView = mLayoutInflater.inflate(R.layout.d_item_faivourits, null); 
							}
					    	
					    	holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
				    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
				    		convertView.setTag(holder);
						} else {
							holder = (Functions.ViewHolder) convertView.getTag();
						}
	
						ImageLoader.getInstance().displayImage(d0.get(position).anime_image_original, holder.imageView);
						holder.textView.setText(d0.get(position).anime_name);
						return convertView;
					}else{
						if (convertView == null) {
							holder = new Functions.ViewHolder();
							if (Functions.preference.theme.equals("ligth")){
								convertView = mLayoutInflater.inflate(R.layout.w_item_no_episods, null);
							}else{
								convertView = mLayoutInflater.inflate(R.layout.d_item_no_episods, null);
							}
				    		convertView.setTag(holder);
						} else {
							holder = (Functions.ViewHolder) convertView.getTag();
						}
						return convertView;
					}	
				}
			}
	
	//Адаптер меню
		public class CalendarAdapterD1 extends BaseAdapter {
					private LayoutInflater mLayoutInflater;

					public CalendarAdapterD1 (Context ctx) {  
					      mLayoutInflater = LayoutInflater.from(ctx);  
					}

					@Override
					public int getCount() {
						if (d1.size() != 0) return d1.size();
						else return 1;
					}

					@Override
					public Object getItem(int position) {
						return d1.get(position);
					}

					@Override
					public long getItemId(int position) {
						return 0;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						final Functions.ViewHolder holder;
				        
						if (d1.size() != 0){
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_faivourits, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_faivourits, null); 
								}
								holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
					    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
	
							ImageLoader.getInstance().displayImage(d1.get(position).anime_image_original, holder.imageView);
							holder.textView.setText(d1.get(position).anime_name);
							return convertView;
						}else{
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_no_episods, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_no_episods, null);
								}
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
							return convertView;
						}	
					}
				}
		
		//Адаптер меню
		public class CalendarAdapterD2 extends BaseAdapter {
					private LayoutInflater mLayoutInflater;

					public CalendarAdapterD2 (Context ctx) {  
					      mLayoutInflater = LayoutInflater.from(ctx);  
					}

					@Override
					public int getCount() {
						if (d2.size() != 0) return d2.size();
						else return 1;
					}

					@Override
					public Object getItem(int position) {
						return d2.get(position);
					}

					@Override
					public long getItemId(int position) {
						return 0;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						final Functions.ViewHolder holder;
				        
						if (d2.size() != 0){
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_faivourits, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_faivourits, null); 
								}
							    holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
					    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
	
							ImageLoader.getInstance().displayImage(d2.get(position).anime_image_original, holder.imageView);
							holder.textView.setText(d2.get(position).anime_name);
							return convertView;
						}else{
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_no_episods, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_no_episods, null);
								}
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
							return convertView;
						}	
					}
				}
		
		//Адаптер меню
		public class CalendarAdapterD3 extends BaseAdapter {
					private LayoutInflater mLayoutInflater;

					public CalendarAdapterD3 (Context ctx) {  
					      mLayoutInflater = LayoutInflater.from(ctx);  
					}

					@Override
					public int getCount() {
						if (d3.size() != 0) return d3.size();
						else return 1;
					}

					@Override
					public Object getItem(int position) {
						return d3.get(position);
					}

					@Override
					public long getItemId(int position) {
						return 0;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						final Functions.ViewHolder holder;
				        
						if (d3.size() != 0){
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_faivourits, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_faivourits, null); 
								}
							    holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
					    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
	
							ImageLoader.getInstance().displayImage(d3.get(position).anime_image_original, holder.imageView);
							holder.textView.setText(d3.get(position).anime_name);
							return convertView;
						}else{
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_no_episods, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_no_episods, null);
								}
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
							return convertView;
						}	
					}
				}
		
		//Адаптер меню
		public class CalendarAdapterD4 extends BaseAdapter {
					private LayoutInflater mLayoutInflater;

					public CalendarAdapterD4 (Context ctx) {  
					      mLayoutInflater = LayoutInflater.from(ctx);  
					}

					@Override
					public int getCount() {
						if (d4.size() != 0) return d4.size();
						else return 1;
					}

					@Override
					public Object getItem(int position) {
						return d4.get(position);
					}

					@Override
					public long getItemId(int position) {
						return 0;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						final Functions.ViewHolder holder;
				        
						if (d4.size() != 0){
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_faivourits, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_faivourits, null); 
								}
							    holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
					    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
	
							ImageLoader.getInstance().displayImage(d4.get(position).anime_image_original, holder.imageView);
							holder.textView.setText(d4.get(position).anime_name);
							return convertView;
						}else{
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_no_episods, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_no_episods, null);
								}
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
							return convertView;
						}	
					}
				}
		
		//Адаптер меню
		public class CalendarAdapterD5 extends BaseAdapter {
					private LayoutInflater mLayoutInflater;

					public CalendarAdapterD5 (Context ctx) {  
					      mLayoutInflater = LayoutInflater.from(ctx);  
					}

					@Override
					public int getCount() {
						if (d5.size() != 0) return d5.size();
						else return 1;
					}

					@Override
					public Object getItem(int position) {
						return d5.get(position);
					}

					@Override
					public long getItemId(int position) {
						return 0;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						final Functions.ViewHolder holder;
				        
						if (d5.size() != 0){
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_faivourits, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_faivourits, null); 
								}
							    holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
					    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
	
							ImageLoader.getInstance().displayImage(d5.get(position).anime_image_original, holder.imageView);
							holder.textView.setText(d5.get(position).anime_name);
							return convertView;
						}else{
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_no_episods, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_no_episods, null);
								}
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
							return convertView;
						}	
					}
				}
		
		//Адаптер меню
		public class CalendarAdapterD6 extends BaseAdapter {
					private LayoutInflater mLayoutInflater;

					public CalendarAdapterD6 (Context ctx) {  
					      mLayoutInflater = LayoutInflater.from(ctx);  
					}

					@Override
					public int getCount() {
						if (d6.size() != 0) return d6.size();
						else return 1;
					}

					@Override
					public Object getItem(int position) {
						return d6.get(position);
					}

					@Override
					public long getItemId(int position) {
						return 0;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						final Functions.ViewHolder holder;
				        
						if (d6.size() != 0){
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_faivourits, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_faivourits, null); 
								}
							    holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
					    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
	
							ImageLoader.getInstance().displayImage(d6.get(position).anime_image_original, holder.imageView);
							holder.textView.setText(d6.get(position).anime_name);
							return convertView;
						}else{
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_no_episods, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_no_episods, null);
								}
								convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
							return convertView;
						}	
					}
				}
		
		//Адаптер меню
		public class CalendarAdapterD7 extends BaseAdapter {
					private LayoutInflater mLayoutInflater;

					public CalendarAdapterD7 (Context ctx) {  
					      mLayoutInflater = LayoutInflater.from(ctx);  
					}

					@Override
					public int getCount() {
						if (d7.size() != 0) return d7.size();
						else return 1;
					}

					@Override
					public Object getItem(int position) {
						return d7.get(position);
					}

					@Override
					public long getItemId(int position) {
						return 0;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						final Functions.ViewHolder holder;
						
						if (d7.size() != 0){
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_faivourits, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_faivourits, null); 
								}
							    holder.textView = (TextView) convertView.findViewById(R.id.tv_item_name);
					    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_item_preview);
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
	
							ImageLoader.getInstance().displayImage(d7.get(position).anime_image_original, holder.imageView);
							holder.textView.setText(d7.get(position).anime_name);
							return convertView;
						}else{
							if (convertView == null) {
								holder = new Functions.ViewHolder();
								if (Functions.preference.theme.equals("ligth")){
									convertView = mLayoutInflater.inflate(R.layout.w_item_no_episods, null);
								}else{
									convertView = mLayoutInflater.inflate(R.layout.d_item_no_episods, null);
								}
						    	
					    		convertView.setTag(holder);
							} else {
								holder = (Functions.ViewHolder) convertView.getTag();
							}
							return convertView;
						}
					}
				}
		
	//Создание активити
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		//Чтение настроек
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
			mDrawer = Functions.setMenuDrawer(actionbar, "Календарь онгоингов", R.layout.w_activity_calendar, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, "Календарь онгоингов", R.layout.d_activity_calendar, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}
		

		ll_load = (LinearLayout) findViewById(R.id.ll_load);
		
		tv_d0 = (TextView) findViewById(R.id.tv_d0);
		tv_d0.setOnClickListener(new OnClickListener() {
	        	@Override
	        	public void onClick(View view) {
	        		if (gv_d0.getVisibility() == View.VISIBLE){
	        			gv_d0.setVisibility(View.GONE);
	        		}else{
	        			gv_d0.setVisibility(View.VISIBLE);
	        		}
	        	}
	        });
		tv_d1 = (TextView) findViewById(R.id.tv_d1);
		tv_d1.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (gv_d1.getVisibility() == View.VISIBLE){
        			gv_d1.setVisibility(View.GONE);
        		}else{
        			gv_d1.setVisibility(View.VISIBLE);
        		}
        	}
        });
		tv_d2 = (TextView) findViewById(R.id.tv_d2);
		tv_d2.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (gv_d2.getVisibility() == View.VISIBLE){
        			gv_d2.setVisibility(View.GONE);
        		}else{
        			gv_d2.setVisibility(View.VISIBLE);
        		}
        	}
        });
		tv_d3 = (TextView) findViewById(R.id.tv_d3);
		tv_d3.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (gv_d3.getVisibility() == View.VISIBLE){
        			gv_d3.setVisibility(View.GONE);
        		}else{
        			gv_d3.setVisibility(View.VISIBLE);
        		}
        	}
        });
		tv_d4 = (TextView) findViewById(R.id.tv_d4);
		tv_d4.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (gv_d4.getVisibility() == View.VISIBLE){
        			gv_d4.setVisibility(View.GONE);
        		}else{
        			gv_d4.setVisibility(View.VISIBLE);
        		}
        	}
        });
		tv_d5 = (TextView) findViewById(R.id.tv_d5);
		tv_d5.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (gv_d5.getVisibility() == View.VISIBLE){
        			gv_d5.setVisibility(View.GONE);
        		}else{
        			gv_d5.setVisibility(View.VISIBLE);
        		}
        	}
        });
		tv_d6 = (TextView) findViewById(R.id.tv_d6);
		tv_d6.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (gv_d6.getVisibility() == View.VISIBLE){
        			gv_d6.setVisibility(View.GONE);
        		}else{
        			gv_d6.setVisibility(View.VISIBLE);
        		}
        	}
        });
		tv_d7 = (TextView) findViewById(R.id.tv_d7);
		tv_d7.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if (gv_d7.getVisibility() == View.VISIBLE){
        			gv_d7.setVisibility(View.GONE);
        		}else{
        			gv_d7.setVisibility(View.VISIBLE);
        		}
        	}
        });
		
		gv_d0 = (View_GridView)findViewById(R.id.gv_d0);
		gv_d0.setExpanded(true);
        adapter_d0 = new CalendarAdapterD0(getBaseContext());
        gv_d0.setAdapter(adapter_d0);
        gv_d0.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_anime = new Intent(Activity_Calendar.this, Activity_AnimeManga.class);
		    	  intent_anime.putExtra("id", d0.get(position).id);
		    	  intent_anime.putExtra("type", "anime");
		    	  startActivity(intent_anime);
		      }
		    });
        
        gv_d1 = (View_GridView)findViewById(R.id.gv_d1);
		gv_d1.setExpanded(true);
        adapter_d1 = new CalendarAdapterD1(getBaseContext());
        gv_d1.setAdapter(adapter_d1);
        gv_d1.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_anime = new Intent(Activity_Calendar.this, Activity_AnimeManga.class);
		    	  intent_anime.putExtra("id", d1.get(position).id);
		    	  intent_anime.putExtra("type", "anime");
		    	  startActivity(intent_anime);
		      }
		    });
        
        gv_d2 = (View_GridView)findViewById(R.id.gv_d2);
		gv_d2.setExpanded(true);
        adapter_d2 = new CalendarAdapterD2(getBaseContext());
        gv_d2.setAdapter(adapter_d2);
        gv_d2.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_anime = new Intent(Activity_Calendar.this, Activity_AnimeManga.class);
		    	  intent_anime.putExtra("id", d2.get(position).id);
		    	  intent_anime.putExtra("type", "anime");
		    	  startActivity(intent_anime);
		      }
		    });
        
        gv_d3 = (View_GridView)findViewById(R.id.gv_d3);
		gv_d3.setExpanded(true);
        adapter_d3 = new CalendarAdapterD3(getBaseContext());
        gv_d3.setAdapter(adapter_d3);
        gv_d3.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_anime = new Intent(Activity_Calendar.this, Activity_AnimeManga.class);
		    	  intent_anime.putExtra("id", d3.get(position).id);
		    	  intent_anime.putExtra("type", "anime");
		    	  startActivity(intent_anime);
		      }
		    });
        
        gv_d4 = (View_GridView)findViewById(R.id.gv_d4);
		gv_d4.setExpanded(true);
        adapter_d4 = new CalendarAdapterD4(getBaseContext());
        gv_d4.setAdapter(adapter_d4);
        gv_d4.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_anime = new Intent(Activity_Calendar.this, Activity_AnimeManga.class);
		    	  intent_anime.putExtra("id", d4.get(position).id);
		    	  intent_anime.putExtra("type", "anime");
		    	  startActivity(intent_anime);
		      }
		    });
        
        gv_d5 = (View_GridView)findViewById(R.id.gv_d5);
		gv_d5.setExpanded(true);
        adapter_d5 = new CalendarAdapterD5(getBaseContext());
        gv_d5.setAdapter(adapter_d5);
        gv_d5.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_anime = new Intent(Activity_Calendar.this, Activity_AnimeManga.class);
		    	  intent_anime.putExtra("id", d5.get(position).id);
		    	  intent_anime.putExtra("type", "anime");
		    	  startActivity(intent_anime);
		      }
		    });
        
        gv_d6 = (View_GridView)findViewById(R.id.gv_d6);
		gv_d6.setExpanded(true);
        adapter_d6 = new CalendarAdapterD6(getBaseContext());
        gv_d6.setAdapter(adapter_d6);
        gv_d6.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_anime = new Intent(Activity_Calendar.this, Activity_AnimeManga.class);
		    	  intent_anime.putExtra("id", d6.get(position).id);
		    	  intent_anime.putExtra("type", "anime");
		    	  startActivity(intent_anime);
		      }
		    });
        
        gv_d7 = (View_GridView)findViewById(R.id.gv_d7);
		gv_d7.setExpanded(true);
        adapter_d7 = new CalendarAdapterD7(getBaseContext());
        gv_d7.setAdapter(adapter_d7);
        gv_d7.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  Intent intent_anime = new Intent(Activity_Calendar.this, Activity_AnimeManga.class);
		    	  intent_anime.putExtra("id", d7.get(position).id);
		    	  intent_anime.putExtra("type", "anime");
		    	  startActivity(intent_anime);
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

	    al = (AsyncLoading) getLastNonConfigurationInstance();
	    if (al == null) {
	      al = new AsyncLoading();
	    }
	    
	    if ((savedInstanceState != null)) {
	    	
	    	result = (ArrayList<SSDK_Calendar>)  savedInstanceState.getSerializable("result");

final Calendar cal_title = Calendar.getInstance();
			
			SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMM");
			
			final Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			final Calendar cal0 = Calendar.getInstance();
			cal0.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),0,0,0);
			final Calendar cal1 = Calendar.getInstance();
			cal1.setTime(cal0.getTime());
			cal1.add(Calendar.DATE, 1);
			final Calendar cal2 = Calendar.getInstance();
			cal2.setTime(cal0.getTime());
			cal2.add(Calendar.DATE, 2);
			final Calendar cal3 = Calendar.getInstance();
			cal3.setTime(cal0.getTime());
			cal3.add(Calendar.DATE, 3);
			final Calendar cal4 = Calendar.getInstance();
			cal4.setTime(cal0.getTime());
			cal4.add(Calendar.DATE, 4);
			final Calendar cal5 = Calendar.getInstance();
			cal5.setTime(cal0.getTime());
			cal5.add(Calendar.DATE, 5);
			final Calendar cal6 = Calendar.getInstance();
			cal6.setTime(cal0.getTime());
			cal6.add(Calendar.DATE, 6);
			final Calendar cal7 = Calendar.getInstance();
			cal7.setTime(cal0.getTime());
			cal7.add(Calendar.DATE, 7);
			
			for (int i=0;i<result.size();i++){
				cal_title.setTimeInMillis(result.get(i).next_time);
				//Log.d(Constants.LogTag, cal.getTimeInMillis() + "|" + cal.getTime().getYear() + "|" + cal.get(Calendar.MONTH) +"|" + cal.getTime().getDate() + " ?? " + 
										//result.get(i).next_time + "|" + cal2.getTime().getYear() + "|" + cal2.getTime().getMonth() +"|" + cal2.getTime().getDate());
				if (cal_title.before(cal)){
					d0.add(result.get(i));
				}else{
					if (cal_title.after(cal)&&(cal_title.before(cal1))){
						d1.add(result.get(i));
					}else{
						if (cal_title.after(cal1)&&(cal_title.before(cal2))){
							d2.add(result.get(i));
						}else{
							if (cal_title.after(cal2)&&(cal_title.before(cal3))){
								d3.add(result.get(i));
							}else{
								if (cal_title.after(cal3)&&(cal_title.before(cal4))){
									d4.add(result.get(i));
								}else{
									if (cal_title.after(cal4)&&(cal_title.before(cal5))){
										d5.add(result.get(i));
									}else{
										if (cal_title.after(cal5)&&(cal_title.before(cal6))){
											d6.add(result.get(i));
										}else{
											if (cal_title.after(cal6)&&(cal_title.before(cal7))){
												d7.add(result.get(i));
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			tv_d0.setText("Уже должно было выйти".toUpperCase());
			tv_d1.setText("Сегодня".toUpperCase());
			tv_d2.setText(sdf.format(cal1.getTime()).toUpperCase());
			tv_d3.setText(sdf.format(cal2.getTime()).toUpperCase());
			tv_d4.setText(sdf.format(cal3.getTime()).toUpperCase());
			tv_d5.setText(sdf.format(cal4.getTime()).toUpperCase());
			tv_d6.setText(sdf.format(cal5.getTime()).toUpperCase());
			tv_d7.setText(sdf.format(cal6.getTime()).toUpperCase());
			
			adapter_d0.notifyDataSetChanged();
			adapter_d1.notifyDataSetChanged();
			adapter_d2.notifyDataSetChanged();
			adapter_d3.notifyDataSetChanged();
			adapter_d4.notifyDataSetChanged();
			adapter_d5.notifyDataSetChanged();
			adapter_d6.notifyDataSetChanged();
			adapter_d7.notifyDataSetChanged();
	    	
	    	tv_unread.setText(String.valueOf(Functions.count_unread));

	    	ll_load.setVisibility(View.GONE);
		}else{
			result.clear();
			if (Functions.isNetwork(getBaseContext()))  al = new AsyncLoading().execute();
		}
	    
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

        return true;
	}
	
	//Асинхронная задача подгрузки новостей
	private class AsyncLoading extends AsyncTask<Void, Void, Void> {
	  
		@Override
		protected void onPreExecute() {
			setSupportProgressBarIndeterminateVisibility(true);
		}
	  
		protected void onPostExecute(Void result1) {
			//Разбиваем на дни..
			final Calendar cal_title = Calendar.getInstance();
			
			SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMM");
			
			final Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			final Calendar cal0 = Calendar.getInstance();
			cal0.clear();
			cal0.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
			final Calendar cal1 = Calendar.getInstance();
			cal1.clear();
			cal1.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
			cal1.add(Calendar.DATE, 1);
			final Calendar cal2 = Calendar.getInstance();
			cal2.clear();
			cal2.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
			cal2.add(Calendar.DATE, 2);
			final Calendar cal3 = Calendar.getInstance();
			cal3.clear();
			cal3.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
			cal3.add(Calendar.DATE, 3);
			final Calendar cal4 = Calendar.getInstance();
			cal4.clear();
			cal4.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
			cal4.add(Calendar.DATE, 4);
			final Calendar cal5 = Calendar.getInstance();
			cal5.clear();
			cal5.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
			cal5.add(Calendar.DATE, 5);
			final Calendar cal6 = Calendar.getInstance();
			cal6.clear();
			cal6.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
			cal6.add(Calendar.DATE, 6);
			final Calendar cal7 = Calendar.getInstance();
			cal7.clear();
			cal7.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
			cal7.add(Calendar.DATE, 7);
			
			for (int i=0;i<result.size();i++){
				cal_title.setTimeInMillis(result.get(i).next_time);
				
				if (cal_title.before(cal)){
					d0.add(result.get(i));
					//Log.d(Constants.LogTag, "D0 " + cal0.DATE + " " + result.get(i).next_episode_at + " " + result.get(i).next_time);
				}else{
					if ((cal_title.after(cal) || cal_title.equals(cal))&&(cal_title.before(cal1))){
						d1.add(result.get(i));
						//Log.d(Constants.LogTag, "D1 " + cal1.DATE + " " + result.get(i).next_episode_at + " " + result.get(i).next_time);
					}else{
						if ((cal_title.after(cal1) || cal_title.equals(cal1))&&(cal_title.before(cal2))){
							d2.add(result.get(i));
							//Log.d(Constants.LogTag, "D2 " + cal2.DATE + " " + result.get(i).next_episode_at + " " + result.get(i).next_time);
						}else{
							if ((cal_title.after(cal2) || cal_title.equals(cal2))&&(cal_title.before(cal3))){
								d3.add(result.get(i));
								//Log.d(Constants.LogTag, "D3 " + cal3.DATE + " " + result.get(i).next_episode_at + " " + result.get(i).next_time);
							}else{
								if ((cal_title.after(cal3) || cal_title.equals(cal3))&&(cal_title.before(cal4))){
									d4.add(result.get(i));
									//Log.d("Cal", "" + cal3.getTimeInMillis() + " - " + cal4.getTimeInMillis());
									//Log.d(Constants.LogTag, "D4 " + cal4.DATE + " " + result.get(i).next_episode_at + " " + result.get(i).next_time);
								}else{
									if ((cal_title.after(cal4) || cal_title.equals(cal4))&&(cal_title.before(cal5))){
										d5.add(result.get(i));
										//Log.d(Constants.LogTag, "D5 " + cal5.DATE + " " + result.get(i).next_episode_at + " " + result.get(i).next_time);
									}else{
										if ((cal_title.after(cal5) || cal_title.equals(cal5))&&(cal_title.before(cal6))){
											d6.add(result.get(i));
											//Log.d(Constants.LogTag, "D6 " + cal6.DATE + " " + result.get(i).next_episode_at + " " + result.get(i).next_time);
										}else{
											if ((cal_title.after(cal6) || cal_title.equals(cal6))&&(cal_title.before(cal7))){
												d7.add(result.get(i));
												//Log.d(Constants.LogTag, "D7 " + cal7.DATE + " " + result.get(i).next_episode_at + " " + result.get(i).next_time);
											}
										}
									}
								}
							}
						}
					}
				}
			}
					
			tv_d0.setText("Уже должно было выйти".toUpperCase());
			tv_d1.setText("Сегодня".toUpperCase());
			tv_d2.setText(sdf.format(cal1.getTime()).toUpperCase());
			tv_d3.setText(sdf.format(cal2.getTime()).toUpperCase());
			tv_d4.setText(sdf.format(cal3.getTime()).toUpperCase());
			tv_d5.setText(sdf.format(cal4.getTime()).toUpperCase());
			tv_d6.setText(sdf.format(cal5.getTime()).toUpperCase());
			tv_d7.setText(sdf.format(cal6.getTime()).toUpperCase());
			
			adapter_d0.notifyDataSetChanged();
			adapter_d1.notifyDataSetChanged();
			adapter_d2.notifyDataSetChanged();
			adapter_d3.notifyDataSetChanged();
			adapter_d4.notifyDataSetChanged();
			adapter_d5.notifyDataSetChanged();
			adapter_d6.notifyDataSetChanged();
			adapter_d7.notifyDataSetChanged();
			
			
			tv_unread.setText(String.valueOf(Functions.count_unread));
			ll_load.setVisibility(View.GONE);
			setSupportProgressBarIndeterminateVisibility(false);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				result = SSDK_API.getCalendar(Functions.preference.kawai);
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
	

	//События нажатий на меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
	
  	//Сохранение значения Асинхронной задачи
  	public Object onRetainNonConfigurationInstance() {
  		return al;
  	}
  	
	//Сохранение значений элементов
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("result", result);
    }
}
