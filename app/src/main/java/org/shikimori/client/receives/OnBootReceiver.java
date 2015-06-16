package org.shikimori.client.receives;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.shikimori.client.services.NewMessagesService;

public class OnBootReceiver extends BroadcastReceiver {

    /**
     * Запуск сервиса при включении устройства
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("receiveshiki", intent!=null ? intent.getAction() : "none");
        if (intent!=null && checkRunService(context, intent.getAction())) {
            context.startService(new Intent(context, NewMessagesService.class));
            Log.v(this.getClass().getName(), "Service started while device boot completed.");
        } else
            Log.v(this.getClass().getName(), "Service not started");
    }

    boolean checkRunService(Context context, String action){
        String self = context.getPackageName()+".LAUNCH_FROM_APP";
        if(self.equals(action))
            return true;
        switch (action){
            case "android.intent.action.BOOT_COMPLETED":
            case "android.intent.action.PACKAGE_INSTALL":
            case "android.intent.action.PACKAGE_ADDED":
                return true;
            default: return false;
        }
    }
}