package org.shikimori.client;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.h;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class ShikiApplikation extends Application {

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
}
