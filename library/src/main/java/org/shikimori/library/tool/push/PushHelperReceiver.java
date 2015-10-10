package org.shikimori.library.tool.push;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.shikimori.library.R;
import org.shikimori.library.tool.ShikiUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Владимир on 15.06.2015.
 */
public class PushHelperReceiver extends BroadcastReceiver {
    public static final String ACTION = "action";
    public static final String MSG_BODY = "msgBody";
    public static final String MSG_BODY_BIG = "msgBodyBig";
    public static final String MSG_TITLE = "msgTitle";

    private static final Map<String, PushAction> actions = new HashMap<>();

    private static PushAction nonEmpAction;

    public static void addAction(String actionName, PushAction pushAction) {
        actions.put(actionName, pushAction);
    }

    public static void setNonEmpAction(PushAction pushAction) {
        nonEmpAction = pushAction;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("SERVICES", "Push Recieved");

        Bundle bundle = intent.getExtras();
        if (!bundle.containsKey(ACTION) && nonEmpAction != null) { //some non EMP push
            nonEmpAction.onPushRecived(intent);
            return;
        }

        PushAction action = actions.get(bundle.getString(ACTION));
        if (action == null) { //unknown action
            Log.i("SERVICES", "Unknown EMP Push");
            return;
        }

        String msgBody = bundle.getString(MSG_BODY);
        String msgTitle = bundle.getString(MSG_TITLE);
        boolean isAuthReq = action.isAuthRequired();
        if (!isAuthReq || ShikiUser.USER_ID != null) {
            action.onPushRecived(intent);
            notifyUser(context, msgTitle, msgBody, action, bundle);
        }
    }

    private void notifyUser(Context context, String msgTitle, String msgBody, PushAction action, Bundle b) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Intent intent = action.getNotifyIntent();
        builder.setSmallIcon(intent.getIntExtra(PushAction.SMALL_ICON, R.mipmap.ic_launcher))
//               .setWhen(System.currentTimeMillis())
               .setContentTitle(msgTitle)
               .setContentText(msgBody)
               .setAutoCancel(true)
               .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
               .setTicker(msgTitle);

        if (b.containsKey(PushAction.LARGE_ICON))
            builder.setLargeIcon(b.<Bitmap>getParcelable(PushAction.LARGE_ICON));
        else if (intent.hasExtra(PushAction.LARGE_ICON))
            builder.setLargeIcon(intent.<Bitmap>getParcelableExtra(PushAction.LARGE_ICON));


        // set sound
        if(action.isSound()){
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setSound(alarmSound);
        }

        // big text
        String bigText = b.getString(MSG_BODY_BIG);
        if(bigText!=null){
            builder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(bigText));
        } else if (msgBody.contains("/")) {
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
//            inboxStyle.setBigContentTitle(c.getString(R.string.newed) + ":");
            String[] _tett = msgBody.split("/");
            for (String _stt : _tett) {
                inboxStyle.addLine(_stt.trim());
            }
            builder.setStyle(inboxStyle);
        }

        // send notify
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(action.getNotifyId(), builder.build());
    }

    public static class PushActionSimple implements PushAction {

        @Override
        public void onPushRecived(Intent intent) {

        }

        @Override
        public Intent getNotifyIntent() {
            return null;
        }

        @Override
        public boolean isAuthRequired() {
            return true;
        }

        @Override
        public int getNotifyId() {
            return 55499;
        }

        @Override
        public Type getLocalType() {
            return Type.LOCAL;
        }

        @Override
        public boolean isSound() {
            return true;
        }

    }

    public interface PushAction {

        /**
         * можно передать drawableres в intent в параметре SMALL_ICON  для замены маленькой иконки
         */
        @DrawableRes
        String SMALL_ICON = "PushAction.SMALL_ICON";
        /**
         * можно передать BITMAP в intent в параметре LARGE_ICON  для замены большой иконки
         */
        @DrawableRes
        public String LARGE_ICON = "PushAction.LARGE_ICON";

        public enum Type {
            LOCAL
        }

        /**
         * method for making some additional actions when recieve push notification. For example, when receive push you might want to refresh some data
         *
         * @param intent full push intent.
         */
        public void onPushRecived(Intent intent);

        /**
         * method returns intent which will be used to start activity when user tap on notification in notification bar
         */
        public Intent getNotifyIntent();

        /**
         * some push notifications require authorization, so it won't be shown if user made logout or something
         */
        public boolean isAuthRequired();

        /**
         * all notifications should have unique notify id! two application must have different notify ids!!
         */
        public int getNotifyId();

        public Type getLocalType();

        public boolean isSound();
    }
}
