package ru.gslive.shikimori.org.v2;

import java.io.Serializable;
import java.util.StringTokenizer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

public class SSDK_Preference implements Serializable {
	private static final long serialVersionUID = 1L;
	String user_id;
	String login;
	String avatar;
	String kawai;
	Boolean notify;
	int notify_time;
	String cache_dir;
	String unread_temp;
	Boolean legend_onlist;
	Boolean see_to_open;
	Boolean animations;
	int sort_anime;
	int sort_manga;
    int sort_anime_view;
    int sort_manga_view;
	Boolean english_first;
	static String[] topic_fav;
	int time_zone;
	String theme;
	


	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static SSDK_Preference parse(Context context){
        SSDK_Preference preference = new SSDK_Preference();
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context);
        //
        if(mSettings.contains(Constants.APP_PREFERENCES_USER_ID)) {
			preference.user_id = mSettings.getString(Constants.APP_PREFERENCES_USER_ID, null);
		}else{
			preference.user_id = null;
		}
        //
        if(mSettings.contains(Constants.APP_PREFERENCES_UNREAD)) {
			preference.unread_temp = mSettings.getString(Constants.APP_PREFERENCES_UNREAD, "0;0;0");
		}else{
			preference.unread_temp = "0;0;0";
		}
		//
        if(mSettings.contains(Constants.APP_PREFERENCES_LOGIN)) {
			preference.login = mSettings.getString(Constants.APP_PREFERENCES_LOGIN, null);
		}else{
			preference.login = null;
		}
        //
		if(mSettings.contains(Constants.APP_PREFERENCES_AVATAR)) {
			preference.avatar = mSettings.getString(Constants.APP_PREFERENCES_AVATAR, null);
		}else{
			preference.avatar = null;
		}
		//
		if(mSettings.contains(Constants.APP_PREFERENCES_SESSION)) {
			preference.kawai = mSettings.getString(Constants.APP_PREFERENCES_SESSION, null);
		}else{
			preference.kawai = null;
		}
		//
		if(mSettings.contains(Constants.APP_PREFERENCES_CASH_DIR)) {
			preference.cache_dir = mSettings.getString(Constants.APP_PREFERENCES_CASH_DIR, Environment.getExternalStorageDirectory().getPath());
		}else{
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
				preference.cache_dir = Environment.getExternalStorageDirectory().getPath();
			}else {
				if (Environment.getExternalStorageDirectory().canWrite()){
					preference.cache_dir = Environment.getExternalStorageDirectory().getPath();
				}else{
					preference.cache_dir = context.getCacheDir().getPath();
				}
			}
			//Запись директории кеша
			Editor editor=mSettings.edit();
            editor.putString("cache_dir", preference.cache_dir);
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
				editor.apply();
			}else {
				editor.commit();
			}
		}
		//
		if(mSettings.contains(Constants.APP_PREFERENCES_NOTIFY)) {
			preference.notify = mSettings.getBoolean(Constants.APP_PREFERENCES_NOTIFY, true);
		}else{
			preference.notify = true;
		}
		//
		if(mSettings.contains(Constants.APP_PREFERENCES_NOTIFY_TIME)) {
			preference.notify_time = mSettings.getInt(Constants.APP_PREFERENCES_NOTIFY_TIME, 600000);
		}else{
			preference.notify_time = 600000;
		}
		//
		if(mSettings.contains(Constants.APP_PREFERENCES_SEE_TO_OPEN)) {
			preference.see_to_open = mSettings.getBoolean(Constants.APP_PREFERENCES_SEE_TO_OPEN, false);
		}else{
			preference.see_to_open = false;
		}
		//
		if(mSettings.contains(Constants.APP_PREFERENCES_LEGEND)) {
			preference.legend_onlist = mSettings.getBoolean(Constants.APP_PREFERENCES_LEGEND, true);
		}else{
			preference.legend_onlist = true;
		}
		if(mSettings.contains(Constants.APP_PREFERENCES_ANIMATIONS)) {
			preference.animations = mSettings.getBoolean(Constants.APP_PREFERENCES_ANIMATIONS, true);
		}else{
			preference.animations = true;
		}
		
		if(mSettings.contains(Constants.APP_PREFERENCES_ENGLISH_FIRST)) {
			preference.english_first = mSettings.getBoolean(Constants.APP_PREFERENCES_ENGLISH_FIRST, false);
		}else{
			preference.english_first = false;
		}
		
		if(mSettings.contains(Constants.APP_PREFERENCES_SORT_ANIME)) {
			preference.sort_anime = mSettings.getInt(Constants.APP_PREFERENCES_SORT_ANIME, 0);
		}else{
			preference.sort_anime = 0;
		}
		
		if(mSettings.contains(Constants.APP_PREFERENCES_SORT_MANGA)) {
            preference.sort_manga = mSettings.getInt(Constants.APP_PREFERENCES_SORT_MANGA, 0);
        }else{
            preference.sort_manga = 0;
        }

        if(mSettings.contains(Constants.APP_PREFERENCES_SORT_ANIME_VIEW)) {
            preference.sort_anime_view = mSettings.getInt(Constants.APP_PREFERENCES_SORT_ANIME_VIEW, 0);
        }else{
            preference.sort_anime_view = 0;
        }

        if(mSettings.contains(Constants.APP_PREFERENCES_SORT_MANGA_VIEW)) {
            preference.sort_manga_view = mSettings.getInt(Constants.APP_PREFERENCES_SORT_MANGA_VIEW, 0);
        }else{
            preference.sort_manga_view = 0;
        }
		
		
		if(mSettings.contains(Constants.APP_PREFERENCES_TOPIC_FAV)) {
			String savedString = mSettings.getString(Constants.APP_PREFERENCES_TOPIC_FAV, "");
			if (!savedString.equals("")){
				topic_fav = new String[savedString.split(",").length];
				for (int i = 0; i < savedString.split(",").length; i++) {
					topic_fav[i] = savedString.split(",")[i];
				}
			}else{
				topic_fav = new String[0];
			}
		}else{
			topic_fav = new String[0];
		}
		if(mSettings.contains(Constants.APP_PREFERENCES_THEME)) {
			preference.theme = mSettings.getString(Constants.APP_PREFERENCES_THEME, "ligth");
		}else{
			preference.theme = "ligth";
		}
		
		
        
        return preference;
    }
	
}
