package org.shikimori.library.tool;

import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiApi;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ShikiImage {
    public static boolean show(String url, ImageView imageView){
        imageView.setImageDrawable(null);
        if(url == null)
            return false;

        if(!url.startsWith("http"))
            url = ShikiApi.HTTP_SERVER + url;

        if(url.contains(".gif"))
            Ion.with(imageView).placeholder(R.drawable.ic_loading).load(url);
        else
            ImageLoader.getInstance().displayImage(url, imageView);
        return true;
    }
    public static boolean show(String url, ImageView imageView, boolean hideIfEnpty){
        if(!show(url, imageView) && hideIfEnpty){
            h.setVisibleGone(imageView);
            return false;
        }else
            h.setVisible(imageView, true);
        return true;
    }
}
