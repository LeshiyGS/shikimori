package org.shikimori.library.tool.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Владимир on 15.06.2015.
 */
public class PushHelper {
    private static String RECEIVE_ID;
    protected Context context;

    public PushHelper(Context context){
        this.context = context;
        if(RECEIVE_ID==null)
            RECEIVE_ID = context.getPackageName()+".im.RECEIVE";
    }

    public void sendBroadCast(String action, String title, String message){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(RECEIVE_ID);
        Bundle b = new Bundle();
        b.putString(PushHelperReceiver.ACTION, action);
        b.putString(PushHelperReceiver.MSG_TITLE, title);
        b.putString(PushHelperReceiver.MSG_BODY, message);
        b.putString(PushHelperReceiver.MSG_BODY, message);
        broadcastIntent.putExtras(b);
        context.sendBroadcast(broadcastIntent);
    }

}
