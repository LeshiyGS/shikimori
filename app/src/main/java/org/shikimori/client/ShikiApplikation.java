package org.shikimori.client;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.webkit.WebView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.client.activity.AboutActivity;
import org.shikimori.client.activity.log.SendLogActivity;
import org.shikimori.client.tool.PreferenceHelper;
import org.shikimori.library.fragments.AnimeDeatailsFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.push.PushHelperReceiver;

import java.io.PrintWriter;
import java.io.StringWriter;

import ru.altarix.basekit.library.tools.pagecontroller.PageController;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class ShikiApplikation extends Application {

    public static final String OPEN_PAGE = "open_launch_page";
    public static final String NEW_MESSAGES = "new_messages";
    public static final String NEW_NEWS = "new_news";
    public static final String NEW_NOTIFY = "new_notify";
    public static final String NEW_VERSION = "new_version";


    public static final int MESSAGES_ID = 3;
    public static final int NEWS_ID = 4;
    public static final int NOTIFY_ID = 5;
    public static final int VERSION_ID = 6;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Class.forName("android.os.AsyncTask");
            Class.forName("android.support.v7.internal.view.menu.MenuBuilder");
        } catch (Throwable ignore) {
            // ignored
        }


        // Setup handler for uncaught exceptions.
        if (!BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable e) {
                    handleUncaughtException(thread, e);
                }
            });
        }

        new ShikiUser(this);
        ShikiApi.setIsDebug(BuildConfig.DEBUG);
        initImageLoader(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(ShikiApi.isDebug);
        }

        PushHelperReceiver.addAction(NEW_MESSAGES, getMessgesPushAction(MESSAGES_ID));
        PushHelperReceiver.addAction(NEW_NOTIFY, getMessgesPushAction(NEWS_ID));
        PushHelperReceiver.addAction(NEW_NEWS, getMessgesPushAction(NOTIFY_ID));
        PushHelperReceiver.addAction(NEW_VERSION, getMessgesPushAction(VERSION_ID));

        PageController.baseActivityController = ShikiAC.class;
        ProjectTool.buildType = BuildConfig.BUILD_TYPE;
        AnimeDeatailsFragment.UPDATE_AUTO_SERIES = PreferenceHelper.isUpdateSeries(this);
        runService();
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        e.printStackTrace();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        Intent intent = new Intent();
        intent.putExtra(SendLogActivity.MSG, sw.toString());
        intent.setAction("ru.altarix.mos.reception.SEND_LOG"); // see step 5.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);

        System.exit(1); // kill off the crashed app
    }

    public static ImageLoader initImageLoader(Context c) {
        return hs.initImageLoader(c);
    }

    public static DisplayImageOptions getImageLoaderOptions() {
        return hs.getImageLoaderOptionsBuilder().build();
    }

    public static DisplayImageOptions.Builder getImageLoaderOptionsBuilder() {
        return hs.getImageLoaderOptionsBuilder();
    }

    public PushHelperReceiver.PushAction getMessgesPushAction(final int id) {
        return new PushHelperReceiver.PushActionSimple() {

            @Override
            public int getNotifyId() {
                return super.getNotifyId() + id;
            }

            @Override
            public Intent getNotifyIntent() {
                Intent newIntent;
                if(id == 6){
                    newIntent = new Intent(ShikiApplikation.this, AboutActivity.class);
                } else {
                    newIntent = new Intent(ShikiApplikation.this, MainActivity.class);
                    newIntent.putExtra(OPEN_PAGE, id);
                }
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
        broadcastIntent.setAction(getPackageName() + ".LAUNCH_FROM_APP");
        sendBroadcast(broadcastIntent);
    }
}
