package org.shikimori.client.tool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import org.shikimori.client.R;
import org.shikimori.client.ShikiApplikation;
import org.shikimori.library.tool.push.PushHelper;
import org.shikimori.library.tool.push.PushHelperReceiver;

/**
 * Created by Владимир on 22.06.2015.
 */
public class PushHelperShiki extends PushHelper {
    public PushHelperShiki(Context context) {
        super(context);
    }

    public void sendNewMessages(int message){
        sendBroadCast(ShikiApplikation.NEW_MESSAGES, context.getString(R.string.new_title), String.format(context.getString(R.string.new_messages), message));
    }

    public void sendNewNews(int message){
        String messe = String.format(context.getString(R.string.new_news), message);
        sendBroadCast(ShikiApplikation.NEW_NEWS, context.getString(R.string.newsnya), messe);
    }

    public void sendNewNotify(int message){
        sendBroadCast(ShikiApplikation.NEW_NOTIFY, context.getString(R.string.notifynya), String.format(context.getString(R.string.new_notify),message));
    }

    public void sendNewVersion(String message){
        sendBroadCast(ShikiApplikation.NEW_VERSION, context.getString(R.string.new_version),
                String.format(context.getString(R.string.new_version),message));
    }

    public void sendLastMessage(String title, String message, Bitmap bitmap){
        Intent broad = prepareIntent(ShikiApplikation.NEW_MESSAGES, title, message);
        broad.putExtra(PushHelperReceiver.PushAction.LARGE_ICON, bitmap);
        send(broad);
    }
}
