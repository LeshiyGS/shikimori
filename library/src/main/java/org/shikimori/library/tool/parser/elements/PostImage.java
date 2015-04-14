package org.shikimori.library.tool.parser.elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.objects.one.ItemImageShiki;
import org.shikimori.library.tool.h;

/**
 * Created by Феофилактов on 09.04.2015.
 */
public class PostImage {
    private final Context context;
    private ImageView image;
    private ItemImageShiki imageData;

    public PostImage(Context activity, ItemImageShiki imageData) {
        this.context = activity;
        this.imageData = imageData;
        initImage();
    }

    public ImageView getImage() {
        return image;
    }

    private void initImage() {
        //Вставляем картинку
        image = new ImageView(context);
        image.setBackgroundColor(Color.DKGRAY);
        ViewGroup.LayoutParams params = h.getDefaultParams();
        params.height = h.pxToDp(150, context);
        image.setLayoutParams(params);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        image.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View view) {

                // TODO if simple image show big view or go to anime or manga


                if(imageData.getClickListener()!=null)
                    imageData.getClickListener().onClick(view);

            }
        });

        if(imageData.getThumb() == null)
            return;

        if(imageData.getThumb().contains(".gif")){
            Ion.with(image)
                .animateLoad(R.anim.spin_animation)
                .error(R.drawable.missing_preview)
                .load(imageData.getThumb());
        } else {
            ImageLoader.getInstance().displayImage(imageData.getThumb(), image);
        }

    }

    public void loadImage(){
        ImageLoader.getInstance().displayImage(imageData.getThumb(), image);
    }
}
