package org.shikimori.library.tool.parser.elements;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.shikimori.library.loaders.ShikiApi;

/**
 * Created by Владимир on 09.04.2015.
 */
public class HtmlText extends BitmapDrawable {
    private final ImageLoader loader;
    private TextView tvText;
    private Context context;

    private TextView text;
    private boolean isLink;
    private Html.ImageGetter imgGetter;
    protected Drawable drawable;
    private TextView.BufferType spannable;

    public HtmlText(Context context, boolean isLink){
        this.context = context;
        this.isLink = isLink;
        loader = ImageLoader.getInstance();
    }

    public void setText(String htmltext){
        if(tvText == null)
            tvText = new TextView(context);
        if(spannable!=null)
            text.setText(Html.fromHtml(htmltext, getImgGetter(),null),spannable);
        else
            text.setText(Html.fromHtml(htmltext, getImgGetter(),null));
        if(isLink)
            text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setText(String htmltext, TextView test){
        tvText = test;
        setText(htmltext);
    }

    public TextView getText() {
        return text;
    }

    Html.ImageGetter getImgGetter() {
        imgGetter = new Html.ImageGetter() {

            public Drawable getDrawable(String source) {
                if (source.contains("missing_logo")){
                    source = ShikiApi.HTTP_SERVER + "/assets/globals/missing_original.jpg";
                }
                if (!source.contains("http")){
                    source = ShikiApi.HTTP_SERVER + source;
                }

                Bitmap img = loader.getMemoryCache().get(source);
                if(img!=null){
                    return new BitmapDrawable(context.getResources(),img);
                }

                loadImage(source);

                return HtmlText.this;
            }
        };
        return imgGetter;
    }

    @Override
    public void draw(Canvas canvas) {
        // override the draw to facilitate refresh function later
        if(drawable != null) {
            drawable.draw(canvas);
        }
    }

    public void loadImage(String source) {
        ImageLoader.getInstance().loadImage(source, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                drawable = new BitmapDrawable(context.getResources(), loadedImage);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                text.invalidate();
            }
        });
    }

    public void setType(TextView.BufferType spannable) {
        this.spannable = spannable;
    }
}
