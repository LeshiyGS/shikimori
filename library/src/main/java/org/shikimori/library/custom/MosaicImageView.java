package org.shikimori.library.custom;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.shikimori.library.tool.hs;

/**
 * Created by Владимир on 29.12.2015.
 */
public class MosaicImageView extends ImageView {
    private int checkWidth;
    private int checkHeight;
    private boolean isLarge;
    private int minWidth;
    private int minHeight;
    private boolean isLand;

    public MosaicImageView(Context context) {
        this(context, null);
    }

    public MosaicImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MosaicImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        minHeight = hs.pxToDp(70, getContext());
        minWidth = hs.pxToDp(150, getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        rebuild();
    }

    public boolean isLand() {
        return isLand;
    }

    private void rebuild() {
        post(new Runnable() {
            @Override
            public void run() {
                if (checkWidth == getWidth() && checkHeight == getHeight())
                    return;
                build();
            }
        });
    }

    public void setIsLarge(boolean isLarge) {
        this.isLarge = isLarge;
    }

    private void build() {
        Drawable dr = getDrawable();
        if (dr == null)
            return;


        Rect bounds = dr.getBounds();
        if (bounds.width() == 0 || bounds.height() == 0)
            return;


        int finalHeight, finalWidth;
        ViewGroup.LayoutParams params = getLayoutParams();

        ViewGroup parent = (ViewGroup) getParent();

        int maxWidth = 0;
        if (parent != null)
            maxWidth = parent.getWidth();

        if (maxWidth == 0)
            maxWidth = minWidth;

        float ratio;

        if (bounds.height() > bounds.width()) {
            ratio = (float) bounds.height() / bounds.width();
            if (!isLarge) {
                finalWidth = maxWidth;
            } else {
                finalWidth = bounds.width() < minWidth ? minWidth : bounds.width();
            }

//                iHeight = imageData.getHeight();
            params.width = finalWidth;
            finalHeight = Math.round(finalWidth * ratio);
        } else {
            isLand = true;
            ratio = (float) bounds.width() / bounds.height();

            int defHeight = hs.pxToDp(150, getContext());
            if (isLarge) {
                finalHeight = bounds.height() < defHeight ? bounds.height() : defHeight;
                params.width = Math.round(finalHeight / ratio);
            } else {
                if(bounds.width() > (maxWidth / 2) )
                    finalWidth = maxWidth;
                else
                    finalWidth = bounds.width();
                params.width = finalWidth;
                finalHeight = Math.round(finalWidth / ratio);
            }
        }

        params.height = finalHeight;
        setLayoutParams(params);

        checkWidth = getWidth();
        checkHeight = getHeight();
    }
}
