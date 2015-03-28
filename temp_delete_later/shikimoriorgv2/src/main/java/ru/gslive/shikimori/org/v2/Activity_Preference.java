package ru.gslive.shikimori.org.v2;


import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Spannable;
import android.text.SpannableString;

public class Activity_Preference extends SherlockPreferenceActivity {

	SharedPreferences mSettings;
	String cache_dir ="";

	
	String m_chosenDir = "";
    boolean m_newFolderEnabled = true;
    String select_mnt = "";
    
    private final int IDD_DEL_ITEM = 0;
    private static final int PICKFILE_RESULT_CODE = 1;
	
    Preference button;
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		if(mSettings.contains(Constants.APP_PREFERENCES_CASH_DIR)) {
		    cache_dir = mSettings.getString(Constants.APP_PREFERENCES_CASH_DIR, Environment.getExternalStorageDirectory().getPath().toString());
		}
    			
		//Выбор темы
		setTheme(R.style.AppTheme_ActionBarStyle_BackHome);

		SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
		Context context = getBaseContext();
	    s.setSpan(new Functions.TypefaceSpan(context, getString(R.string.shiki_font)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setSubtitle("Настройки");
	    getSupportActionBar().setTitle(s);
	    getSupportActionBar().setDisplayShowCustomEnabled(true);
		
		
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);
        
        Preference button = (Preference)findPreference("button");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference arg0) { 

                        	showDialog(IDD_DEL_ITEM);
                            return true;
                        }
                    });
        
        /*Preference cash = (Preference)findPreference("cache_dir");
        cash.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference arg0) { 

                        	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("file/*");
                            startActivityForResult(intent,PICKFILE_RESULT_CODE);
                        	
                            return true;
                        }
                    });*/
    }
    
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode == RESULT_OK){
                    String FilePath = data.getData().getPath();
                    Editor editor = mSettings.edit();
        			editor.putString(Constants.APP_PREFERENCES_CASH_DIR, FilePath);
        			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
        				editor.apply();
        			}else {
        				editor.commit();
        			}
                }
                break;
        }
    }
	
	@Override
	protected Dialog onCreateDialog(int id) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			switch (id) {
	        case IDD_DEL_ITEM:
	           builder.setMessage("Вы действительно хотите очистить кеш?")
	                    .setCancelable(false)
	                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
	                                @SuppressWarnings("deprecation")
									public void onClick(DialogInterface dialog, int id) {
	                                	//Чистим папку с кешем
	                                	removeDialog(IDD_DEL_ITEM);
	                                }
	                            })
	                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
	                                @SuppressWarnings("deprecation")
									public void onClick(DialogInterface dialog, int id) {
	                                    removeDialog(IDD_DEL_ITEM);
	                                }
	                            });
	 
	            return builder.create();
	        default:
	            return null;
	        }
	    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
		}
		return super.onOptionsItemSelected(item);        
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        
        if (mSettings.getBoolean(Constants.APP_PREFERENCES_NOTIFY, false)){
        	startService(new Intent(Activity_Preference.this, Service_Notification.class));
        }else{
        	stopService(new Intent(Activity_Preference.this, Service_Notification.class));
        }
        
    }
    
}
