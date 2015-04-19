package org.shikimori.library.tool;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Author: Artemiy Garin
 * Date: 30.04.13
 */
public class PagerContainer extends FrameLayout implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private boolean needsRedraw = false;
    private float x;

    public PagerContainer(Context context) {
        super(context);
        init();
    }

    public PagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PagerContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setClipChildren(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onFinishInflate() {
        try {
            viewPager = (ViewPager) getChildAt(0);
            viewPager.setOnPageChangeListener(this);
            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    boolean leftScroll = false;
                    switch(event.getAction())
                    {
                        case MotionEvent.ACTION_MOVE:
                            if(x <= 0)
                                x = event.getX();
                            leftScroll = x >= event.getX();
                            break;
                    }

                    // fix shaker images
                    if(viewPager.getCurrentItem() < 2 && leftScroll && viewPager.getAdapter().getCount() < 3)
                        return true;

                    //view.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("The root child of PagerContainer must be a ViewPager");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return viewPager.dispatchTouchEvent(motionEvent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (needsRedraw) invalidate();
    }

    @Override
    public void onPageSelected(int i) {
//        int count = viewPager.getChildCount();
//        if(count < 2)
//            return;
//        if(i >= count-1){
//            viewPager.setCurrentItem(count-2);
//        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        needsRedraw = true;
        if(state == 0){
            x = 0f;
        }
    }

}
