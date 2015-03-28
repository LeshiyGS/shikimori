package ru.gslive.shikimori.org.v2;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Login extends ShikiSherlockActivity {
	SharedPreferences mSettings;

	String kawai = null;
	String t_login = null;
	String t_password = null;
	String user_login = "";
	String id ="";
	
	EditText et_login;
	EditText et_pass;
	TextView tv_not_login;
	
	private final int DIALOG_WAIT = 0;
	
	private AsyncLogin loader;
	private AsyncLoginVK loader1;
	
	//Создание диалогов
	@Override
    protected Dialog onCreateDialog(int id) {
		switch (id) {
        case DIALOG_WAIT:
        	ProgressDialog dialog = new ProgressDialog(this);
        	dialog.setMessage("Подождите...");
        	dialog.setCancelable(false);
        	return dialog;
        default:
            return null;
        }
    }
	
	//Создание Активити
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
		Context context = getBaseContext();
	    s.setSpan(new Functions.TypefaceSpan(context, getString(R.string.shiki_font)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		setTheme(R.style.AppTheme_ActionBarStyle);
		getSupportActionBar().setTitle(s);
		getSupportActionBar().setSubtitle(getString(R.string.auth));
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shiki_login);
		
		TextView logo = (TextView) findViewById(R.id.tv_site);
		logo.setText(s);
		
		et_login = (EditText) findViewById(R.id.et_login);
		et_pass = (EditText) findViewById(R.id.et_pass);
		tv_not_login = (TextView) findViewById(R.id.tv_not_login);
	}

	//Стандартная авторизация
	public void onBtnClick(View view){
		if (Functions.isNetwork(getBaseContext())) startLoading();
	}
	
	//Авторизация через ВК
	public void ibVkOnClick(View view){
		if (Functions.isNetwork(getBaseContext())){
			Editor editor = mSettings.edit();
			editor.putString(Constants.APP_PREFERENCES_SESSION, null);
			editor.putString(Constants.APP_PREFERENCES_LOGIN, "");
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
				editor.apply();
			}else {
				editor.commit();
			}
			Intent intent_vk = new Intent(Activity_Login.this, Activity_Auth.class);
			intent_vk.putExtra("site", "vkontakte");
			startActivityForResult(intent_vk, 0);
		}
	}
		
	//Авторизация через FaceBook
	public void ibFbOnClick(View view){
		if (Functions.isNetwork(getBaseContext())){
			Editor editor = mSettings.edit();
			editor.putString(Constants.APP_PREFERENCES_SESSION, null);
			editor.putString(Constants.APP_PREFERENCES_LOGIN, "");
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
				editor.apply();
			}else {
				editor.commit();
			}
			Intent intent_fb = new Intent(Activity_Login.this, Activity_Auth.class);
			intent_fb.putExtra("site", "facebook");
			startActivityForResult(intent_fb, 0);
		}
	}
	
	//Авторизация через Tweeter
	public void ibTwOnClick(View view){
		if (Functions.isNetwork(getBaseContext())){
			Editor editor = mSettings.edit();
			editor.putString(Constants.APP_PREFERENCES_SESSION, null);
			editor.putString(Constants.APP_PREFERENCES_LOGIN, "");
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
				editor.apply();
			}else {
				editor.commit();
			}
			Intent intent_tw = new Intent(Activity_Login.this, Activity_Auth.class);
			intent_tw.putExtra("site", "twitter");
			startActivityForResult(intent_tw, 0);
		}
	}
	
	//Ожидаем авторизации от VK Facebook Tweeter
	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				showDialog(DIALOG_WAIT);
				startLoadingVK();
			}
		}
	}
	
	//Задание VK Facebook Tweeter
	public void startLoadingVK() {
		if (loader1 == null || loader1.getStatus().equals(AsyncTask.Status.FINISHED)) {
    		loader1 = new AsyncLoginVK();
    		loader1.execute();
    	} else {
    		Toast.makeText(Activity_Login.this, "Ждите завершения загрузки", Toast.LENGTH_SHORT).show();
    	}
    }

	private class AsyncLoginVK extends AsyncTask<Void, Void, Void> {
		  
	  	@Override
	  	protected void onPreExecute() {
	  		if(mSettings.contains(Constants.APP_PREFERENCES_SESSION)) {
			    kawai = mSettings.getString(Constants.APP_PREFERENCES_SESSION, null);
			}
		}
		  
	  	@SuppressWarnings("deprecation")
		protected void onPostExecute(Void result1) {
			dismissDialog(DIALOG_WAIT);
			setResult(1, new Intent());
  			finish();
		}
  	
	  	
		@Override
		protected Void doInBackground(Void... arg0) {
			//Заходим на сайт
			SSDK_API.getWhoAmI(kawai, getBaseContext());
			return null;
		}

		
  }
	
	//Задание Авторизации
	public void startLoading() {
		if (loader == null || loader.getStatus().equals(AsyncTask.Status.FINISHED)) {
    		loader = new AsyncLogin();
    		loader.execute();
    	} else {
    		Toast.makeText(Activity_Login.this, "Ждите завершения загрузки", Toast.LENGTH_SHORT).show();
    	}
    }
	
	private class AsyncLogin extends AsyncTask<Void, Void, Void> {
		  
	  	@Override
	  	protected void onPreExecute() {
	  		tv_not_login.setVisibility(View.GONE);
		}
		  
	  	@Override
	  	protected void onPostExecute(Void result1) {

	  		if (t_login != null){
	  			Editor editor = mSettings.edit();
				editor.putString(Constants.APP_PREFERENCES_LOGIN, t_login);
				editor.putString(Constants.APP_PREFERENCES_SESSION, kawai);
				editor.putString(Constants.APP_PREFERENCES_USER_ID, id);
				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
					editor.apply();
				}else {
					editor.commit();
				}
	  			setResult(1, new Intent());
	  			finish();
	  		}else{
	  			tv_not_login.setVisibility(View.VISIBLE);
	  		}
		}

		@Override
		protected Void doInBackground(Void... arg0) {
		
			String[] result = SSDK_API.getSession(et_login.getText().toString(), et_pass.getText().toString());
				
			if (!result[0].equals("")){
				kawai = result[0];
				t_login = result[1];
				id = result[2];
			}

			return null;
		}

		
  }
	
}
