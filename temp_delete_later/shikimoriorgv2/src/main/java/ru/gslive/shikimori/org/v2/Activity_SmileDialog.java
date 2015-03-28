package ru.gslive.shikimori.org.v2;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.koushikdutta.ion.Ion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;


public class Activity_SmileDialog extends ShikiSherlockActivity {

	GridView gv_smile;
	CustomAdapter adapter;
	
	String[] smile_img;
	
	ActionBar actionbar;
	
	public class CustomAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public CustomAdapter (Context ctx) {  
		      mLayoutInflater = LayoutInflater.from(ctx);  
		}

		@Override
		public int getCount() {
			return smile_img.length; // длина массива
		}

		@Override
		public Object getItem(int position) {
			return smile_img[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;
	        
			convertView = mLayoutInflater.inflate(R.layout.smile_item, null);
			
			holder = new Functions.ViewHolder();
    		holder.imageView = (ImageView) convertView.findViewById(R.id.iv_smile);
    		convertView.setTag(holder);

            Ion.with(holder.imageView)
                    .load("http://shikimori.org/images/smileys/" + smile_img[position] + ".gif");

			return convertView;
		}
	}
	
	@SuppressLint("Recycle")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
		Context context = getBaseContext();
	    s.setSpan(new Functions.TypefaceSpan(context, getString(R.string.shiki_font)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    		
	    setTheme(R.style.AppTheme_ActionBarStyle_BackHome);
	    
		actionbar = getSupportActionBar();
		actionbar.setTitle(s);
		actionbar.setSubtitle("Смайлы");
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smile_dialog);
		
		smile_img = getResources().getStringArray(R.array.smile_imgs);
		
		gv_smile = (GridView) findViewById(R.id.gv_smile);
		adapter = new CustomAdapter(this);
		gv_smile.setAdapter(adapter);
		
		gv_smile.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  setResult(position, new Intent());
		    	  finish();
		      }
		});
	}
	
	//—обыти¤ нажатий на меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		switch (item.getItemId()) {
          	case android.R.id.home:
       	    	finish();
          		break;
  			}
  		return super.onOptionsItemSelected(item);        
    }
}
