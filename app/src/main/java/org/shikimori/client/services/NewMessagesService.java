package org.shikimori.client.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;
import org.shikimori.client.R;
import org.shikimori.client.ShikiApplikation;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.Notification;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.push.PushHelper;

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
            timerHandler.postDelayed(this, Query.FIVE_MIN * 2);
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
        Log.d(TAG, "onDestroy NewMessagesService");
    }

    protected void getMessages() {
        user = new ShikiUser(this);
        if(!h.getConnection(this) || ShikiUser.USER_ID == null)
            return;
        query.getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        load(res.getResultObject());
    }

    public void load(JSONObject dataFromServer) {
        if (dataFromServer == null)
            return;
        Notification notify = new Notification(dataFromServer);
        showNotification(notify);
    }

    private void showNotification(Notification notify) {

        Log.d(TAG, "onStartCommand NewMessagesService");

        Notification userNotify = user.getNotification();
        StringBuilder str = new StringBuilder();
        if(userNotify.notifications < notify.notifications)
            appendString(str, R.string.new_notify, notify.notifications);
        if(userNotify.news < notify.news)
            appendString(str, R.string.new_news, notify.news);
        if (userNotify.messages < notify.messages)
            appendString(str, R.string.new_messages, notify.messages);

        if(str.length() > 0){
            PushHelper pushHelper = new PushHelper(this);
            pushHelper.sendBroadCast(
                    ShikiApplikation.NEW_MESSAGES,
                    getString(R.string.incomming_messages),
                    str.toString());
            user.setNotification(notify);
        }
    }

    void appendString(StringBuilder str, int mss, int count){
        if(str.length() > 0)
            str.append("/");
        str.append(String.format(getString(mss), count));
    }

    @Override
    public void onQueryError(StatusResult res) {

    }
}
