package org.shikimori.client.tool;

import android.content.Context;

import org.shikimori.client.R;
import org.shikimori.client.ShikiApplikation;
import org.shikimori.library.tool.push.PushHelper;

/**
 * Created by Владимир on 22.06.2015.
 */
public class PushHelperShiki extends PushHelper {
    public PushHelperShiki(Context context) {
        super(context);
    }

    public void sendNewMessages(int message){
        sendBroadCast(ShikiApplikation.NEW_MESSAGES, "", String.format(context.getString(R.string.new_messages), message));
    }

    public void sendNewNews(int message){
        sendBroadCast(ShikiApplikation.NEW_NEWS, "", String.format(context.getString(R.string.new_news),message));
    }

    public void sendNewNotify(int message){
        sendBroadCast(ShikiApplikation.NEW_NEWS, "", String.format(context.getString(R.string.new_notify),message));
    }
}
