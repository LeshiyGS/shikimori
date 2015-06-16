package org.shikimori.client;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.push.PushHelper;
import org.shikimori.library.tool.push.PushHelperReceiver;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class ShikiApplikation extends Application {

    public static final String NEW_MESSAGES = "new_messages";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Class.forName("android.os.AsyncTask");
            Class.forName("android.support.v7.internal.view.menu.MenuBuilder");
        }
        catch(Throwable ignore) {
            // ignored
        }
        new ShikiUser(this);
        ShikiApi.setIsDebug(BuildConfig.DEBUG);
        initImageLoader(getApplicationContext());

        PushHelperReceiver.addAction(NEW_MESSAGES, getRequestPushAction());

        runService();
    }

    public static ImageLoader initImageLoader(Context c) {
        return h.initImageLoader(c);
    }

    public static DisplayImageOptions getImageLoaderOptions() {
        return h.getImageLoaderOptionsBuilder().build();
    }

    public static DisplayImageOptions.Builder getImageLoaderOptionsBuilder() {
        return h.getImageLoaderOptionsBuilder();
    }

    public PushHelperReceiver.PushAction getRequestPushAction() {
        return new PushHelperReceiver.PushActionSimple() {

            @Override
            public Intent getNotifyIntent() {
                Intent newIntent = new Intent(ShikiApplikation.this, MainActivity.class);
                return newIntent;
            }
        };
    }

    private void runService() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(getPackageName()+".LAUNCH_FROM_APP");
        sendBroadcast(broadcastIntent);
    }
}
