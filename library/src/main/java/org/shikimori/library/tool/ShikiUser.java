package org.shikimori.library.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.shikimori.library.objects.one.Notification;
import org.shikimori.library.tool.constpack.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class ShikiUser {
    public static String TOKEN;
    public static String USER_ID;
    public static String USER_NAME;
    private static final String COOKIE = "cookie";
    private static final String DEVICE_ID = "device_id";
    private static final String NICKNAME = "nickname";
    private static final String AVATAR = "avatar";
    private static final String ID = "id";
    private static final String NOTIFICATION = "notification";

    private final SharedPreferences prefs;
    private Context mContext;

    public ShikiUser(Context mContext){
        this.mContext = mContext;
        prefs = mContext.getSharedPreferences(Constants.SETTINGS_USER, Context.MODE_PRIVATE);
        initStaticParams();
    }

    public void setToken(String cookie){
        prefs.edit().putString(COOKIE, cookie).apply();
    }

    public static String getToken(){
        return TOKEN;
    }

    public void logout(){
        TOKEN = null;
        USER_ID = null;
        USER_NAME = null;
        prefs.edit().clear().apply();
    }

    public String getNickname(){
        return prefs.getString(NICKNAME, null);
    }
    public String getAvatar(){
        return prefs.getString(AVATAR, null);
    }


    public void initStaticParams(){
        USER_ID = prefs.getString(ID, null);
        TOKEN = prefs.getString(COOKIE, null);
        USER_NAME = encodeLogin(prefs.getString(NICKNAME, null));
    }

    public boolean isAutorize(){
        if(TOKEN != null)
            return true;
        initStaticParams();
        return TOKEN != null;
    }

    public String getId(){
        return prefs.getString(ID, "");
    }

    public void setNotification(Notification notification){
        prefs.edit().putString(NOTIFICATION, notification.json.toString()).apply();
    }

    public void clearNotification(){
        prefs.edit().putString(NOTIFICATION, null).apply();
    }

    public Notification getNotification(){
        try {
            String n = prefs.getString(NOTIFICATION, null);
            if(n!=null)
                return new Notification(new JSONObject(n));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Notification(null);
    }

    public int getNotificationCount(){
        Notification n = getNotification();
        int count = 0;
        count += n.news;
        count += n.messages;
        count += n.notifications;
        return count;
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

    public void setName(String login) {
        prefs.edit().putString(NICKNAME, login).apply();
    }

    private String encodeLogin(String login) {
        if(!TextUtils.isEmpty(login)) {
            try {
                return URLEncoder.encode(login, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isAuthorized() {
        return !TextUtils.isEmpty(getToken());
    }

    public void setDeviceId(String tokenId) {
        prefs.edit().putString(DEVICE_ID, tokenId).apply();
    }

    public boolean isDeviceIdCreated(){
        return !TextUtils.isEmpty(prefs.getString(DEVICE_ID, null));
    }

    public String getDeviceId(){
        return prefs.getString(DEVICE_ID, null);
    }
}
