package ru.gslive.shikimori.org.v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Reciver_Start_On_Boot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Functions.getPreference(context);
        if (Functions.preference.notify) {
            context.startService(new Intent(context, Service_Notification.class));
        }
    }
}