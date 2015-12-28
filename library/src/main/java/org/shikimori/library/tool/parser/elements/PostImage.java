package org.shikimori.library.tool.parser.elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        initImage();
    }

    public void setIsGallery() {
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setBackgroundColor(Color.DKGRAY);
        initImgSize(hs.pxToDp(150, context));
    }

    public void setLarge(boolean large) {
        isLarge = large;
    }

    public boolean isLand() {
        return isLand;
    }

    public void setScaleType(ImageView.ScaleType type) {
        image.setScaleType(type);
    }

    private void initImgSize(int iHeight) {
        ViewGroup.LayoutParams params = image.getLayoutParams();

        if (imageData.getHeight() > 0 && imageData.getWidth() > 0) {
            float ratio = 1;

            if (iHeight == 0)
                iHeight = hs.pxToDp(150, context);
            if (imageData.getHeight() > imageData.getWidth()) {
                ratio = imageData.getHeight() / imageData.getWidth();
                params.width = (int) (iHeight / ratio);
            } else {
                isLand = true;
                ratio = imageData.getWidth() / imageData.getHeight();
                if (isLarge) {
                    params.width = (int) (iHeight * ratio);
                }
            }

            params.height = iHeight;
        } else {
            if (iHeight > 0)
                params.height = hs.pxToDp(150, context);
        }
        image.setLayoutParams(params);
    }

    public void setFixesSize() {
        ViewGroup.LayoutParams params = image.getLayoutParams();
        params.height = hs.pxToDp(150, context);
        image.setLayoutParams(params);
    }

    private void initImage() {
        //Вставляем картинку
        image = new ImageView(context);
//        image.setBackgroundColor(Color.DKGRAY);
        ViewGroup.LayoutParams params = hs.getDefaultParams();
//        params.height = h.pxToDp(150, context);
        image.setLayoutParams(params);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setTag(this);
        initImgSize(0);
        image.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View view) {

                // TODO if simple image show big view or go to anime or manga


                if (imageData.getClickListener() != null)
                    imageData.getClickListener().onClick(view);

            }
        });

        if (imageData.getThumb() == null)
            return;
    }

    public void initMargin() {
        image.setPadding(0, 30, 0, 30);
    }
}
