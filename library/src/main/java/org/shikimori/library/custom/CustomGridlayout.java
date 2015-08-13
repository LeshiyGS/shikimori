package org.shikimori.library.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.library.tool.hs;

/**
 * Created by Владимир on 13.08.2015.
 */
public class CustomGridlayout extends GridLayout {

    int checkWifth = 0;

    public CustomGridlayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomGridlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomGridlayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        buildViews();
    }



    private void buildViews() {
        post(new Runnable() {
            @Override
            public void run() {
                if(checkWifth == getWidth())
                    return;
                checkWifth = getWidth();
                // если 1 элемент то ничего не делаем
                int viewCount = getChildCount();
                int column = viewCount > 3 ? 3 : viewCount;
                int gridSize = getWidth()/* == 0 ? screensize.x : getWidth()*/;
                gridSize -= (getPaddingLeft() - getPaddingRight());
                int parentPadding = ((ViewGroup) getParent()).getPaddingLeft();
                gridSize -= (parentPadding*2);
//                Point screen = hs.getScreenSize((Activity) getContext());
//                gridSize -= (screen.x - gridSize);

                for (int i = 0; i < viewCount; i++) {
                    View v = getChildAt(i);
                    GridLayout.LayoutParams itemParams = (GridLayout.LayoutParams) v.getLayoutParams();

                    gridSize -= (itemParams.rightMargin - itemParams.leftMargin);
                    itemParams.width = (gridSize / column);
                    v.setLayoutParams(itemParams);
                }
            }
        });
    }
}
