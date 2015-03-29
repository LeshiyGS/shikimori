package org.shikimori.library.tool;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class ShikiUser {
    public static final String COOKIE = "cookie";

    private final SharedPreferences prefs;
    private Context mContext;

    public ShikiUser(Context mContext){
        this.mContext = mContext;
        prefs = mContext.getSharedPreferences(Constants.SETTINGS_USER, Context.MODE_PRIVATE);
    }

    public void setCookie(String cookie){
        prefs.edit().putString(COOKIE, cookie).apply();
    }

    public String getCookie(){
        return prefs.getString(COOKIE, null);
    }

    public void logout(){
        prefs.edit().clear().apply();
    }
}
