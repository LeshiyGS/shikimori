package org.shikimori.library.tool.parser.elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.shikimori.library.custom.MosaicImageView;
import org.shikimori.library.objects.one.ItemImageShiki;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.parser.ImageController;

/**
 * Created by Феофилактов on 09.04.2015.
 */
public class PostImage extends ImageController {
    private final Context context;
    private boolean isLarge, isLand;

    public PostImage(Context activity, ItemImageShiki imageData) {
        this.context = activity;
        this.imageData = imageData;
    }

    public void setIsGallery() {
        image = new ImageView(context);
        prepareView();
//        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setBackgroundColor(Color.DKGRAY);
//        initImgSize();
    }

    public void setSimple(){
        image = new MosaicImageView(context);
        ((MosaicImageView)image).setIsLarge(isLarge);
        prepareView();
    }

    public void setLarge(boolean large) {
        isLarge = large;
    }

    public boolean isLand() {
        return isLand;
    }

//    public void setScaleType(ImageView.ScaleType type) {
//        image.setScaleType(type);
//    }

//    private void initImgSize() {
//        ViewGroup.LayoutParams params = image.getLayoutParams();
//
//        ViewGroup parent = (ViewGroup) image.getParent();
//
//        int maxWidth = 0;
//        if(parent!=null)
//            maxWidth = parent.getWidth();
//
//        int minHeight = hs.pxToDp(70, context);
//        int minWidth = hs.pxToDp(250, context);
//
//        if(maxWidth == 0)
//            maxWidth = minWidth;
//
//        int iHeight;
//        if (imageData.getHeight() > 0 && imageData.getWidth() > 0) {
//            float ratio = 1;
//
//            if (imageData.getHeight() > imageData.getWidth()) {
//                ratio = imageData.getHeight() / imageData.getWidth();
//
//                int width;
//                if(!isLarge){
//                    width = maxWidth;
//                } else {
//                    width = imageData.getWidth();
//                    if(minWidth > imageData.getWidth())
//                        width = minWidth;
//                }
//
////                iHeight = imageData.getHeight();
//                params.width = width;
//                iHeight = (int) (width * ratio);
//            } else {
//                isLand = true;
//                ratio = imageData.getWidth() / imageData.getHeight();
//
////                if(imageData.getHeight() > minHeight && imageData.getHeight() < iHeight)
////                    iHeight = imageData.getHeight();
//
//                if (isLarge) {
//                    iHeight = hs.pxToDp(150, context);
//                    params.width = (int) (iHeight * ratio);
//                } else {
//                    params.width = maxWidth;
//                    iHeight = (int) (maxWidth / ratio);
//                }
//            }
//
//            params.height = iHeight;
//        } else {
//            params.height = hs.pxToDp(150, context);
//        }
//        image.setLayoutParams(params);
//    }

    @Override
    protected void invalidateSize() {
//        initImgSize();

    }

    public void setFixesSize() {
        ViewGroup.LayoutParams params = image.getLayoutParams();
        params.height = hs.pxToDp(150, context);
        image.setLayoutParams(params);
    }

    private void prepareView(){
        ViewGroup.LayoutParams params = hs.getDefaultParams();
//        params.height = h.pxToDp(150, context);
        image.setLayoutParams(params);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setTag(this);
//        initImgSize();
        image.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View view) {

                // TODO if simple image show big view or go to anime or manga


                if (imageData.getClickListener() != null)
                    imageData.getClickListener().onClick(view);

            }
        });
    }

    public void initMargin() {
        image.setPadding(0, 30, 0, 30);
    }
}
