package org.shikimori.library.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import org.shikimori.library.tool.parser.ImageController;
import org.shikimori.library.tool.parser.elements.PostImage;

/**
 * Created by Феофилактов on 28.12.2015.
 */
public class GalleryView extends MosaicView {
    public GalleryView(Context context) {
        super(context);
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {


                Log.d("MosaicView", "onGlobalLayout: ");

//                int count = getChildCount();
//
//                for (int i = 0; i < count; i++) {
//                    View v = getChildAt(i);
//                    ImageController item = (ImageController) v.getTag();
//                    item.loadImage();
//                }

                GalleryView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

}
