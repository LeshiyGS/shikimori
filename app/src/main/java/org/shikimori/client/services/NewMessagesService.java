package org.shikimori.client.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;
import org.shikimori.client.tool.PreferenceHelper;
import org.shikimori.client.tool.PushHelperShiki;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.Notification;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.h;

/**
 * Created by Владимир on 15.06.2015.
 */
public class NewMessagesService extends Service implements Query.OnQuerySuccessListener, Query.OnQueryErrorListener {
    public static final String TAG = "serviceshiki";
    private ShikiUser user;
    private Query query;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            getMessages();
            timerHandler.postDelayed(this, Query.FIVE_MIN);
//            timerHandler.postDelayed(this, 10000);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate NewMessagesService");
        query = new Query(this)
                .init(ShikiApi.getUrl(ShikiPath.UNREAD_MESSAGES, ShikiUser.USER_ID))
                .setErrorListener(this);
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand NewMessagesService");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
        user = null;
        Log.d(TAG, "onDestroy NewMessagesService");
    }

    protected void getMessages() {
        if (!isNotifyEnable())
            return;
        user = new ShikiUser(this);
        if (!h.getConnection(this) || ShikiUser.USER_ID == null)
            return;
        query.getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        if (user == null)
            return;
        load(res.getResultObject());
    }

    public void load(JSONObject dataFromServer) {
        if (dataFromServer == null)
            return;
        Notification notify = new Notification(dataFromServer);
        showNotification(notify);
    }

    boolean isNotifyEnable() {
        return (PreferenceHelper.getNotifyNotify(this) && PreferenceHelper.getNotifyNews(this)
                && PreferenceHelper.getNotifyMessage(this));
    }

    private void showNotification(Notification notify) {

        Log.d(TAG, "onStartCommand NewMessagesService");
        PushHelperShiki phelp = new PushHelperShiki(this);
        Notification userNotify = user.getNotification();
        if (userNotify.notifications < notify.notifications && PreferenceHelper.getNotifyNotify(this))
            phelp.sendNewNotify(notify.notifications);
        if (userNotify.news < notify.news && PreferenceHelper.getNotifyNews(this))
            phelp.sendNewNews(notify.notifications);
        if (userNotify.messages < notify.messages && PreferenceHelper.getNotifyMessage(this))
            phelp.sendNewMessages(notify.messages);

        // change data
        if (userNotify.notifications != notify.notifications ||
                userNotify.news != notify.news ||
                userNotify.messages != notify.messages) {
            query.invalidateCache(ShikiApi.getUrl(ShikiPath.UNREAD_MESSAGES, ShikiUser.USER_ID));
            user.setNotification(notify);
        }

    }

    @Override
    public void onQueryError(StatusResult res) {

    }
}
