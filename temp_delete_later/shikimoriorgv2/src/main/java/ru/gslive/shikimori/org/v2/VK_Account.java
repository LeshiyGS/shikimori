package ru.gslive.shikimori.org.v2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

public class VK_Account {
    public String access_token;
    public long user_id;
    
    public void save(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor=prefs.edit();
        editor.putString("access_token", access_token);
        editor.putLong("user_id", user_id);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
			editor.apply();
		}else {
			editor.commit();
		}
    }
    
    public void restore(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        access_token=prefs.getString("access_token", null);
        user_id=prefs.getLong("user_id", 0);
    }
}
