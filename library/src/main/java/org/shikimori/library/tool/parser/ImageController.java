package org.shikimori.library.tool.parser;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.shikimori.library.R;
import org.shikimori.library.objects.one.ItemImageShiki;

/**
 * Created by Владимир on 16.04.2015.
 */
public class ImageController {
    protected ImageView image;
    protected ItemImageShiki imageData;

    public ImageController(){}

    public ImageController(ImageView image, ItemImageShiki imageData){
        this.image = image;
        this.imageData = imageData;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public void setImageData(ItemImageShiki imageData) {
        this.imageData = imageData;
    }

    public void loadImage(){
        if(imageData==null)
            return;

        if(imageData.getThumb().contains(".gif")){
            Ion.with(image)
                    .animateGif(AnimateGifMode.ANIMATE)
//                    .animateLoad(R.anim.spin_animation)
                    .smartSize(true)
                    .error(R.drawable.missing_preview)
                    .load(imageData.getThumb());
        } else {
            ImageLoader.getInstance().displayImage(imageData.getThumb(), image, new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    imageData.setWidth(loadedImage.getWidth());
                    imageData.setHeight(loadedImage.getHeight());
                }
            });
        }
    }

    public ItemImageShiki getImageData() {
        return imageData;
    }
}
