package org.shikimori.library.custom;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ru.altarix.basekit.library.tools.h;

/**
 * Created by Феофилактов on 28.12.2015.
 */
public class MosaicView extends FrameLayout {
    private static final String TAG = "MosaicView";
    private int checkWidth, checkHeight;
    private int minRightOffset = 150;
    private int minHeight, minWidth;

    public MosaicView(Context context) {
        this(context, null);
    }

    public MosaicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MosaicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        minHeight = h.pxToDp(100, context);
        minWidth = h.pxToDp(150, context);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        prepare();
    }

    private void prepare() {
        post(new Runnable() {
            @Override
            public void run() {
                if (checkWidth == getWidth() && checkHeight == getHeight())
                    return;
                rebuildViews();
            }
        });
    }

    public void rebuildViews() {
        checkWidth = getWidth();
        checkHeight = getHeight();
        // если 1 элемент то ничего не делаем
        int viewCount = getChildCount();

//        int column = viewCount > 3 ? 3 : viewCount;
        int gridSize = getWidth()/* == 0 ? screensize.x : getWidth()*/;
        gridSize -= (getPaddingLeft() - getPaddingRight());
//                int parentPadding = ((ViewGroup) getParent()).getPaddingLeft();
//                gridSize -= (parentPadding*2);
//                Point screen = hs.getScreenSize((Activity) getContext());
//                gridSize -= (screen.x - gridSize);


        int viewWidth = 0;
        int viewHeight = 0;
        int globalHeight = 0;
        boolean nextRow = false;

        int i;
        for (i = 0; i < viewCount; i++) {
            View v = getChildAt(i);

            int vWidth = 0, vHeight = 0;
            Rect rect = null;
            if (v instanceof ImageView) {
                Drawable draw = ((ImageView) v).getDrawable();
                if (draw != null){
                    rect = ((ImageView) v).getDrawable().getBounds();
//                    vHeight = ((ImageView) v).getDrawable().getIntrinsicHeight();
//                    vWidth = ((ImageView) v).getDrawable().getIntrinsicWidth();
                }
            }

            if(rect == null || rect.width() == 0 || rect.height() == 0)
                break;

            vHeight = rect.height();
            vWidth = rect.width();
//            Log.d(TAG, "-------------------------------------");
//            Log.d(TAG, "drawable width: " + vWidth + " height: "+vHeight);

//            if (vHeight == 0)
//                vHeight = v.getHeight();
//            if (vWidth == 0)
//                vWidth = v.getWidth();
//
//            if (vWidth == 0)
//                vWidth = 300;
//            if (vHeight == 0)
//                vHeight = minHeight;

            float ration;
            if(vWidth > vHeight){
                ration = (float) vWidth / vHeight;
//                Log.d(TAG, "ratio: " + ration + " vWidth: "+vWidth + " vHeight: "+vHeight);
                if(vHeight < minHeight){
                    vHeight = minHeight;
                    vWidth = Math.round(vHeight * ration);
                }
            } else {
                ration = (float)vHeight / vWidth;
//                Log.d(TAG, "ratio: " + ration + " vWidth: "+vWidth + " vHeight: "+vHeight);
                if(vWidth < minWidth){
                    vWidth = minWidth;
                    vHeight = Math.round(vWidth * ration);
                }
            }

            FrameLayout.LayoutParams itemParams = (FrameLayout.LayoutParams) v.getLayoutParams();

            if (viewHeight == 0)
                viewHeight = vHeight;

            if (nextRow) {
                viewWidth = 0;
                viewHeight = vHeight;
                nextRow = false;
            }

            itemParams.setMargins(viewWidth, globalHeight, 0, 0);


            viewWidth += vWidth;
            vHeight = viewHeight;

            int leftSize = gridSize - viewWidth;

//            Log.d(TAG, i+": viewWidth: " + vWidth + ", leftSize: "+leftSize );

            // 90 < 100
            if(leftSize <= minRightOffset){
                vWidth += leftSize;
//                Log.d(TAG, "vWidth += leftSize: " + vWidth);
                globalHeight += vHeight;
                nextRow = true;
            }

//            Log.d(TAG, "addView row: width-" + vWidth + " height-" + vHeight);

            itemParams.width = vWidth;
            itemParams.height = vHeight;
            v.setLayoutParams(itemParams);

        }

    }


}
