package org.shikimori.client;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.client.tool.PreferenceHelper;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.push.PushHelperReceiver;

import ru.altarix.basekit.library.tools.pagecontroller.PageController;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class ShikiApplikation extends Application {

    public static final String NEW_MESSAGES = "new_messages";
    public static final String NEW_NEWS = "new_news";
    public static final String NEW_NOTIFY = "new_notify";

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

        PushHelperReceiver.addAction(NEW_MESSAGES, getMessgesPushAction(3));
        PushHelperReceiver.addAction(NEW_NOTIFY, getMessgesPushAction(4));
        PushHelperReceiver.addAction(NEW_NEWS, getMessgesPushAction(5));
        PageController.baseActivityController = ShikiAC.class;
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

    public PushHelperReceiver.PushAction getMessgesPushAction(final int id) {
        return new PushHelperReceiver.PushActionSimple() {

            @Override
            public int getNotifyId() {
                return super.getNotifyId()+id;
            }

            @Override
            public Intent getNotifyIntent() {
                Intent newIntent = new Intent(ShikiApplikation.this, MainActivity.class);
                return newIntent;
            }

            @Override
            public boolean isSound() {
                return PreferenceHelper.getNotifySound(getApplicationContext());
            }
        };
    }

    private void runService() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(getPackageName()+".LAUNCH_FROM_APP");
        sendBroadcast(broadcastIntent);
    }
}
