package org.shikimori.library.tool;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class ShikiUser {
    public static String TOKEN;
    private static final String COOKIE = "cookie";
    private static final String NICKNAME = "nickname";
    private static final String AVATAR = "avatar";
    private static final String ID = "id";

    private final SharedPreferences prefs;
    private Context mContext;

    public ShikiUser(Context mContext){
        this.mContext = mContext;
        prefs = mContext.getSharedPreferences(Constants.SETTINGS_USER, Context.MODE_PRIVATE);
        TOKEN = prefs.getString(COOKIE, null);
    }

    public void setToken(String cookie){
        prefs.edit().putString(COOKIE, cookie).apply();
    }

    public static String getToken(){
        return TOKEN;
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

    public String getId(){
        return prefs.getString(ID, null);
    }

    public void setData(JSONObject data) {
        if(data == null)
            return;

        String ava = data.optString(AVATAR);
        if(ava!=null)
            ava = ava.replace("x48", "x80");

        prefs.edit()
             .putString(NICKNAME, data.optString(NICKNAME))
             .putString(AVATAR, ava)
             .putString(ID, data.optString(ID))
             .apply();
    }
}
