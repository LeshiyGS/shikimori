package ru.gslive.shikimori.org.v2;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;
import android.graphics.Bitmap.CompressFormat;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.HttpSender;

@ReportsCrashes(formKey = "",
        mailTo = "k.leshemy@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class ShikiApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);

        Functions.getPreference(getBaseContext());
        File cacheDir = new File (Functions.preference.cache_dir + "/shikimori/cache");
        
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        	.cacheInMemory(true)
        	.cacheOnDisc(true)
        	.showImageOnLoading(R.drawable.loading) // resource or drawable
        	.showImageForEmptyUri(R.drawable.missing_preview) // resource or drawable
        	.showImageOnFail(R.drawable.missing_preview) // resource or drawable
        	.resetViewBeforeLoading(false)
        	.build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        	.discCache(new UnlimitedDiscCache(cacheDir))
        	.defaultDisplayImageOptions(defaultOptions)
        	.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)
        	.build();
        ImageLoader.getInstance().init(config);
    }
}