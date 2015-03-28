package ru.gslive.shikimori.org.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class View_ImageView_fav extends ImageView {
	 
    public View_ImageView_fav(Context context) {
        super(context);
    }
 
    public View_ImageView_fav(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public View_ImageView_fav(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int width = getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        setMeasuredDimension(width, (int)Math.floor(width*1.45));
    }
 
    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, w, oldw, oldh);
    }
 
}