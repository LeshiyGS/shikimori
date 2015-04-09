package org.shikimori.library.tool.parser.elements;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import org.shikimori.library.loaders.ShikiApi;

/**
 * Created by Владимир on 09.04.2015.
 */
public class Images {
    private TextView text;
    private boolean isLink;
    private Html.ImageGetter imgGetter1;

    public Images(TextView text, boolean isLink){
        this.text = text;
        this.isLink = isLink;

        initImages();
    }

    private void initImages() {
        imgGetter1 = new Html.ImageGetter() {

            public Drawable getDrawable(String source) {
                if (source.contains("missing_logo")){
                    source = ShikiApi.HTTP_SERVER + "/assets/globals/missing_original.jpg";
                }
                if (!source.contains("http")){
                    source = ShikiApi.HTTP_SERVER + source;
                }
//                if (mDrawableCache.containsKey(source))
//                    //return mDrawableCache.get(source);
//                    return mDrawableCache.get(source).get();
//                new ImageDownloadAsyncTask2(source, body_elements[finalI], text,  context).execute();
                return null;
            }
        };
//        text.setText(Html.fromHtml(body_elements[i], imgGetter1, null));
//        if(isLink) {
//            text.setMovementMethod(LinkMovementMethod.getInstance());
//        }
    }

}
