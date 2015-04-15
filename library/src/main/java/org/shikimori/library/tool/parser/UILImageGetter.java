package org.shikimori.library.tool.parser;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.InputStream;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiApi;

public class UILImageGetter implements Html.ImageGetter {
    Context c;
    TextView container;

    /***
     * Construct the UILImageGetter which will execute AsyncTask and refresh the container
     * @param t
     * @param c
     */
    public UILImageGetter(View t, Context c) {
        this.c = c;
        this.container = (TextView)t;
    }

    @Override
    public Drawable getDrawable(String source) {

        if(source.contains("/images/user/"))
            source = source.replace("x16", "x48");

        if (source.contains("missing_logo")) {
            source = ShikiApi.HTTP_SERVER + "/assets/globals/missing_original.jpg";
        }
        if (!source.contains("http")) {
            source = ShikiApi.HTTP_SERVER + source;
        }
        UrlImageDownloader urlDrawable = new UrlImageDownloader();
//        urlDrawable.drawable = c.getResources().getDrawable(R.drawable.missing_preview);
//        urlDrawable.setBounds(0,0,50,50);


        ImageSize size = new ImageSize(200,150);
        ImageLoader.getInstance().loadImage(source, size, new SimpleListener(urlDrawable));
        return urlDrawable;
    }

    private class SimpleListener extends SimpleImageLoadingListener
    {
        UrlImageDownloader urlImageDownloader;

        public SimpleListener(UrlImageDownloader downloader) {
            super();
            urlImageDownloader = downloader;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            Drawable result = new BitmapDrawable(c.getResources(), loadedImage);
            result.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());
            urlImageDownloader.setBounds(0, 0, result.getIntrinsicWidth(), result.getIntrinsicHeight());
            urlImageDownloader.drawable = result;
            container.setText(container.getText());
        }
    }

    private class UrlImageDownloader extends BitmapDrawable
    {
        public Drawable drawable;

        /**
         * Create a drawable by decoding a bitmap from the given input stream.
         *
         * @param res
         * @param is
         */
        public UrlImageDownloader(Resources res, InputStream is) {
            super(res, is);
        }

        /**
         * Create a drawable by opening a given file path and decoding the bitmap.
         *
         * @param res
         * @param filepath
         */
        public UrlImageDownloader(Resources res, String filepath) {
            super(res, filepath);
            drawable = new BitmapDrawable(res, filepath);
        }

        /**
         * Create drawable from a bitmap, setting initial target density based on
         * the display metrics of the resources.
         *
         * @param res
         * @param bitmap
         */
        public UrlImageDownloader(Resources res, Bitmap bitmap) {
            super(res, bitmap);
        }

        public UrlImageDownloader() {

        }

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if(drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
}