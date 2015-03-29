package org.shikimori.library.tool;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class ShikiUser {
    private static final String COOKIE = "cookie";
    private static final String NICKNAME = "nickname";
    private static final String AVATAR = "avatar";
    private static final String IMAGE = "image";
    private static final String LAST_ONLINE_AT = "last_online_at";

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

    public String getNickname(){
        return prefs.getString(NICKNAME, null);
    }
    public String getAvatar(){
        return prefs.getString(AVATAR, null);
    }

    public String getLastOnlineAt(){
        return prefs.getString(LAST_ONLINE_AT, null);
    }

    public void setData(JSONObject data) {
        if(data == null)
            return;
        prefs.edit()
             .putString(NICKNAME, data.optString(NICKNAME))
             .putString(AVATAR, data.optString(AVATAR))
             .putString(LAST_ONLINE_AT, data.optString(LAST_ONLINE_AT))
             .apply();
    }
}
