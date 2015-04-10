package org.shikimori.library.tool.parser.elements;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.tool.parser.ParcerTool;
import org.shikimori.library.tool.parser.UILImageGetter;

/**
 * Created by Владимир on 10.04.2015.
 */
public class ImageGetter {
    public static Html.ImageGetter imageGetter;
    private final Drawable defaultImage;
    int countCals = 0;

    public ImageGetter (Context context){
        defaultImage = context.getResources().getDrawable(R.drawable.missing_preview);
        defaultImage.setBounds(0,0,300, 150);
    }

    public void load(final TextView textView, final String text){
        load(textView, text, null);
    }

    public void load(final TextView textView, final String text, final TextView.BufferType type){
        textView.setText(ParcerTool.fromHtml(text, new UILImageGetter(textView, textView.getContext()), null));
//        textView.setText(ParcerTool.fromHtml(text, getImgGetter(textView.getContext(), defaultImage, new LoadImageDone() {
//            @Override
//            public void loadImageComplete() {
//                textView.invalidate();
//                //load(textView, text, type);
//                Log.d("count calls", textView.toString()+" / " + (++countCals));
////                textView.setText(text);
//            }
//        }), null), type == null ? TextView.BufferType.NORMAL : type);
    }

    public static Html.ImageGetter getImgGetter(final Context context, final Drawable defaultImage, final LoadImageDone l) {
//        if(imageGetter != null)
//            return imageGetter;

        return new Html.ImageGetter() {

            public Drawable getDrawable(String source) {
                if (source.contains("missing_logo")) {
                    source = ShikiApi.HTTP_SERVER + "/assets/globals/missing_original.jpg";
                }
                if (!source.contains("http")) {
                    source = ShikiApi.HTTP_SERVER + source;
                }

                Bitmap img = ImageLoader.getInstance().getMemoryCache().get(source);
                if (img != null){

                    BitmapDrawable bitmap = new BitmapDrawable(context.getResources(), img);
                    bitmap.setBounds(0,0, bitmap.getIntrinsicWidth(), bitmap.getIntrinsicHeight());
                    return bitmap;
                }

                URLDrawable b = new URLDrawable();
                b.drawable = defaultImage;

                loadImage(context, source, l, b);

                return b;
            }
        };
    }

    static void loadImage(final Context context, String source, final LoadImageDone l, final URLDrawable b) {
        ImageSize size = new ImageSize(200,150);
        ImageLoader.getInstance().loadImage(source, size, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                ImageLoader.getInstance().getMemoryCache().put(imageUri, loadedImage);
                BitmapDrawable drawable = new BitmapDrawable(context.getResources(), loadedImage);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                b.drawable = drawable;
                if(l!=null)
                    l.loadImageComplete();
            }
        });
    }

    public interface LoadImageDone{
        public void loadImageComplete();
    }

    public static class URLDrawable extends BitmapDrawable {
        // the drawable that you need to set, you could set the initial drawing
        // with the loading image if you need to
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }
}
