package org.shikimori.library.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Феофилактов on 28.12.2015.
 */
public class MosaicView extends FrameLayout {
    private static final String TAG = "MosaicView";
    private int checkWidth;
    private int maxRightOffset = 100;

    public MosaicView(Context context) {
        this(context, null);
    }

    public MosaicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MosaicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

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
                if (checkWidth == getWidth())
                    return;
                buildViews();
            }
        });
    }

    private void buildViews() {
        checkWidth = getWidth();
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

        for (int i = 0; i < viewCount; i++) {
            View v = getChildAt(i);

            int vWidth = v.getWidth();
            int vHeight = v.getHeight();

            if(vWidth == 0)
                vWidth = gridSize;
            if(vHeight == 0)
                vHeight = 150;

            float ration = 1;
            ration = vWidth > vHeight ? vWidth / vHeight : vHeight / vWidth;

            FrameLayout.LayoutParams itemParams = (FrameLayout.LayoutParams) v.getLayoutParams();

            if (viewHeight == 0)
                viewHeight = (int) (vWidth / ration);


            if(nextRow){
                viewWidth = 0;
                viewHeight = (int) (vWidth / ration);
                nextRow = false;
            }

            itemParams.setMargins(viewWidth, globalHeight, 0,0);

//            itemParams.leftMargin = viewWidth;
//            if (viewWidth == 0 && globalHeight > 0)
//            itemParams.topMargin = globalHeight;


//                    if(vWidth > vHeight){
//            if (maxRightOffset > (gridSize - viewWidth)) {
//                vWidth = gridSize - viewWidth;
//                vHeight = viewHeight;
//                globalHeight += vHeight;
//                Log.d(TAG, "addView last: width-"+vWidth+" height-"+vHeight);
//                viewWidth = 0;
//                viewHeight = 0;
//            } else {


                viewWidth += vWidth;
                vHeight = viewHeight;

                if(viewWidth>=gridSize){
                    vWidth -= (gridSize - viewWidth);
                    Log.d(TAG, "vWidth -= (gridSize - viewWidth)"+vWidth);
                    globalHeight += vHeight;
                    nextRow = true;
                } else if ((viewWidth+maxRightOffset)>= gridSize){
                    vWidth += (gridSize - viewWidth);
                    Log.d(TAG, "vWidth += (gridSize - viewWidth)"+vWidth);
                    globalHeight += vHeight;
                    nextRow = true;
                }

//                if (maxRightOffset > (gridSize - viewWidth)) {
//                    int checkWidth = gridSize - viewWidth;
//                    vWidth = gridSize - viewWidth;
//                    vHeight = viewHeight;
//                    globalHeight += vHeight;
//                    Log.d(TAG, "addView last: width-"+vWidth+" height-"+vHeight);
//                    viewWidth = 0;
//                    viewHeight = 0;
//                }
                Log.d(TAG, "addView row: width-"+vWidth+" height-"+vHeight);
//                            vHeight = (int) (vWidth / ration);
//            }
//                    }


            itemParams.width = vWidth;
            itemParams.height = vHeight;
            v.setLayoutParams(itemParams);
        }

//        ViewGroup.LayoutParams bodyParams = getLayoutParams();
//        if(globalHeight>0)
//            bodyParams.height = globalHeight;
//        setLayoutParams(bodyParams);
        Log.d(TAG, "globalHeight: " + globalHeight);
    }


}
