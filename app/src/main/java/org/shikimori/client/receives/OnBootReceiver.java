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
        if (intent!=null && "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            context.startService(new Intent(context, NewMessagesService.class));
            Log.v(this.getClass().getName(), "Service started while device boot completed.");
        }
    }
}