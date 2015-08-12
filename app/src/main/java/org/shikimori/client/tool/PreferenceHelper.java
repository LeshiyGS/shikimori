package org.shikimori.client.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Владимир on 16.06.2015.
 */
public class PreferenceHelper {
    public static final String NOTIFY_MESSAGE = "notify_message";
    public static final String NOTIFY_NEWS   = "notify_news";
    public static final String NOTIFY_NOTIFY = "notify_notify";
    public static final String NOTIFY_SOUND  = "notify_sound";
    public static final String UPDATE_SERIE_NUMBER  = "update_serie_number";

    public static SharedPreferences init(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c);
    }

    public static boolean getNotifyMessage(Context c){
        return init(c).getBoolean(NOTIFY_MESSAGE, true);
    }
    public static boolean getNotifyNews(Context c){
        return init(c).getBoolean(NOTIFY_NEWS, true);
    }
    public static boolean getNotifyNotify(Context c){
        return init(c).getBoolean(NOTIFY_NOTIFY, true);
    }
    public static boolean getNotifySound(Context c){
        return init(c).getBoolean(NOTIFY_SOUND, true);
    }

    public static boolean isUpdateSeries(Context c){
        return init(c).getBoolean(UPDATE_SERIE_NUMBER, true);
    }
}
