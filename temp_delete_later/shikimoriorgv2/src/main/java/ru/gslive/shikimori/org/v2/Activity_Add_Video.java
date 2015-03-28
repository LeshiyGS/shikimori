package ru.gslive.shikimori.org.v2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.perm.kate.api.Api;
import com.perm.kate.api.Video;

import net.simonvt.menudrawer.MenuDrawer;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Activity_Add_Video extends ShikiSherlockActivity {

	int rotate;
	
	ActionBar actionbar;
    String title, episod, id, url;

	
	MenuDrawer mDrawer;
	TextView tv_unread, lbl_title;
    EditText tv_episod, tv_url, tv_member;
    Button btn_add_video;
    Spinner sp_type;

    String[] type = new String[]{"Озвучка", "Субтитры", "Оригинал"};
    String[] type2 = new String[]{"fandub", "Субтитры", "Оригинал"};
    ListAdapter my_adapter;

    AsyncTask<Void, Void, Void> al;

	Functions.MenuAdapter adapter_menu;


    public class ListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        public ListAdapter (Context ctx) {
            mLayoutInflater = LayoutInflater.from(ctx);
        }

        @Override
        public int getCount() {
            return type.length; // длина массива
        }

        @Override
        public Object getItem(int position) {
            return type[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint({ "ViewHolder", "InflateParams" })
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Functions.ViewHolder holder;
            if (Functions.preference.theme.equals("ligth")){
                convertView = mLayoutInflater.inflate(R.layout.w_item_list, null);
            }else{
                convertView = mLayoutInflater.inflate(R.layout.d_item_list, null);
            }
            holder = new Functions.ViewHolder();
            holder.itemView = (TextView) convertView.findViewById(R.id.tv_list_item);
            convertView.setTag(holder);


            holder.itemView.setText(type[position]);

            return convertView;
        }
    }

	//Создание активити и подключение view
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

        id = getIntent().getExtras().getString("id");
        title = getIntent().getExtras().getString("title");
        episod = getIntent().getExtras().getString("episod");
        url = getIntent().getExtras().getString("url");

		//Подключение и выбор варианта меню.
		if (Functions.preference.theme.equals("ligth")){
			mDrawer = Functions.setMenuDrawer(actionbar, "Добавление видео на сайт", R.layout.w_activity_add_video, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}else{
			mDrawer = Functions.setMenuDrawer(actionbar, "Добавление видео на сайт", R.layout.w_activity_add_video, getWindowManager().getDefaultDisplay().getRotation(),
					getBaseContext(), this);
		}
		

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

        tv_episod = (EditText) findViewById(R.id.tv_episod);
        tv_url = (EditText) findViewById(R.id.tv_url);
        tv_member = (EditText) findViewById(R.id.tv_member);
        lbl_title = (TextView) findViewById(R.id.lbl_title);
        btn_add_video = (Button) findViewById(R.id.btn_add_video);

        btn_add_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                al = new AsyncLoader().execute();
            }
        });

        lbl_title.setText(title);
        tv_episod.setText(episod);
        tv_url.setText(url);

        my_adapter = new ListAdapter(getBaseContext());
        sp_type = (Spinner) findViewById(R.id.sp_type);
        sp_type.setAdapter(my_adapter);

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

    private class AsyncLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        protected void onPostExecute(Void result1) {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                String[] token = SSDK_API.getToken(Functions.preference.kawai);
                Connection.Response res = Jsoup
                        .connect("")
                        .ignoreContentType(true)
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
                        .header("X-CSRF-Token", token[0])
                        .cookie("_kawai_session", token[1])
                        .data("anime_video[anime_id]",id)
                        .data("anime_video[source]","shikimori.org")
                        .data("anime_video[state]","uploaded")
                        .data("anime_video[episode]",tv_episod.getText().toString())
                        .data("anime_video[url]",tv_url.getText().toString())
                        .data("anime_video[author_name]",tv_member.getText().toString())
                        .data("anime_video[kind]",type2[sp_type.getSelectedItemPosition()])
                        .data("commit","Работает. Сохранить")
                        .method(Connection.Method.POST)
                        .execute();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

            return null;
        }


    }
	
	
}
