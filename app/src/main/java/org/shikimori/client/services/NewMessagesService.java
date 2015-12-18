package org.shikimori.client.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONObject;
import org.shikimori.client.tool.GetMessageLastForPush;
import org.shikimori.client.tool.PreferenceHelper;
import org.shikimori.client.tool.PushHelperShiki;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.objects.one.Notification;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.hs;

/**
 * Created by Владимир on 15.06.2015.
 */
public class NewMessagesService extends Service implements QueryShiki.OnQuerySuccessListener<ShikiStatusResult>, QueryShiki.OnQueryErrorListener<ShikiStatusResult> {
    public static final String TAG = "serviceshiki";
    private ShikiUser user;
    private QueryShiki query;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            getMessages();
            timerHandler.postDelayed(this, QueryShiki.FIVE_MIN);
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
        timerHandler.postDelayed(timerRunnable, 0);
    }

    void initQuery() {
        query = new QueryShiki(this)
                .init(ShikiApi.getUrl(ShikiPath.UNREAD_MESSAGES, ShikiUser.USER_ID))
                .setErrorListener(this);
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
        onTaskRemoved(null);
        Log.d(TAG, "onDestroy NewMessagesService");
    }

    protected void getMessages() {
        if (!isNotifyEnable())
            return;
        if (user == null)
            user = new ShikiUser(this);
        if (!user.isAutorize())
            return;
        if (ShikiUser.USER_ID == null || !hs.getConnection(this))
            return;
        if (query == null)
            initQuery();
        query.getResult(this);
    }

    @Override
    public void onQuerySuccess(ShikiStatusResult res) {
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
            phelp.sendNewNews(notify.news);
        if (userNotify.messages < notify.messages && PreferenceHelper.getNotifyMessage(this)) {
            notifyMessage();
//            phelp.sendNewMessages(notify.messages);
        }

        // change data
        if (userNotify.notifications != notify.notifications ||
                userNotify.news != notify.news ||
                userNotify.messages != notify.messages) {
            query.invalidateCache(ShikiApi.getUrl(ShikiPath.UNREAD_MESSAGES, ShikiUser.USER_ID));
            user.setNotification(notify);
        }

    }

    private void notifyMessage() {
        GetMessageLastForPush.notifyMessage(query);
    }

    @Override
    public void onQueryError(ShikiStatusResult res) {
        String errorMessage = res.getMsg();
        if (errorMessage.contains("token") || errorMessage.contains("Вам необходимо войти в систему")) {
            user.logout();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.d(TAG, "onTaskRemoved NewMessagesService");
            Intent restartService = new Intent(getApplicationContext(),
                    this.getClass());
            restartService.setPackage(getPackageName());
            PendingIntent restartServicePI = PendingIntent.getService(
                    getApplicationContext(), 1, restartService,
                    PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 10000, restartServicePI);
        }
    }
}
