package org.shikimori.library.tool.parser.elements;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * Created by Владимир on 09.04.2015.
 */
public class HtmlText {
    private final ImageGetter imageGeter;
    private TextView tvText;
    private Context context;

    private boolean isLink;
    private TextView.BufferType spannable;
    private String htmltext;

    public HtmlText(Context context, boolean isLink) {
        this.context = context;
        this.isLink = isLink;
        imageGeter = new ImageGetter(context);
    }

    public void setText(String htmltext) {
        this.htmltext = htmltext;
        if (tvText == null)
            tvText = new TextView(context);

        overSetText();
    }

    void overSetText() {
        imageGeter.load(tvText, htmltext, spannable);
        if (isLink)
            tvText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setText(String htmltext, TextView test) {
        tvText = test;
        setText(htmltext);
    }

    public TextView getText() {
        return tvText;
    }


//    @Override
//    public void draw(Canvas canvas) {
//
//        // override the draw to facilitate refresh function later
//        if (drawable != null) {
//            drawable.draw(canvas);
//        } else {
//            defaultImage.draw(canvas);
//        }
//    }

    public void setType(TextView.BufferType spannable) {
        this.spannable = spannable;
    }

//    public class URLDrawable extends BitmapDrawable {
//        // the drawable that you need to set, you could set the initial drawing
//        // with the loading image if you need to
//        protected Drawable drawable;
//
//        @Override
//        public void draw(Canvas canvas) {
//            // override the draw to facilitate refresh function later
//            if(drawable != null) {
//                drawable.draw(canvas);
//            }
//        }
//    }
}
