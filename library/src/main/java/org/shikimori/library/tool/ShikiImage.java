package org.shikimori.library.tool;

import android.os.Build;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiApi;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ShikiImage {
    public static boolean show(String url, ImageView imageView){
        return show(url, imageView, null);
    }

    public static boolean show(String url, ImageView imageView, ImageLoadingListener listener){
        imageView.setImageDrawable(null);
        if(url == null)
            return false;

        if(!url.startsWith("http"))
            url = ShikiApi.HTTP_SERVER + url;

        if(url.contains(".gif") && Build.VERSION.SDK_INT > 13){
            Ion.with(imageView).placeholder(R.drawable.ic_loading).load(url);
            if(listener!=null)
                listener.onLoadingComplete(null,null,null);
        }
        else
            ImageLoader.getInstance().displayImage(url, imageView, listener);
        return true;
    }



    public static boolean show(String url, ImageView imageView, boolean hideIfEnpty){
        boolean loaded = show(url, imageView) && hideIfEnpty;
        h.setVisibleGone(!loaded, imageView);
        return loaded;
    }
}
