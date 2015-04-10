package org.shikimori.library.tool.parser.elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.koushikdutta.ion.Ion;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

/**
 * Created by Феофилактов on 09.04.2015.
 */
public class PostImage {
    private final Activity activity;
    private final String tag;
    private ImageView image;
    private String bigImageUrl;

    public PostImage(Activity activity, String tag) {
        this.activity = activity;
        this.tag = tag;
        initImage();
    }

    public ImageView getImage() {
        return image;
    }

    private void initImage() {
        //Вставляем картинку
        image = new ImageView(activity);
        image.setBackgroundColor(Color.DKGRAY);
        Display display = activity.getWindowManager().getDefaultDisplay();
        //int width = display.getWidth(); // ((display.getWidth()*20)/100)
        int height;
        if (tag.contains("x64")) {
            height = ((display.getHeight() * 10) / 100);// ((display.getHeight()*30)/100)
        } else {
            height = ((display.getHeight() * 25) / 100);// ((display.getHeight()*30)/100)
        }

        image.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height));

        image.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View view) {

                String imgOrigin = bigImageUrl == null ? getImageUrl() : bigImageUrl;

                h.showMsg(activity, "click image " + imgOrigin);
            }
        });

        Ion.with(image)
            .animateLoad(R.anim.spin_animation)
            .error(R.drawable.missing_preview)
            .load(tag.substring(3, tag.length() - 1));
    }

    String getImageUrl() {
        String url = tag.substring(3, tag.length() - 1);
        if (tag.contains("/images/user_image/thumbnail/"))
            return url.replace("thumbnail", "original");
        else if (tag.contains("/person/x64/") || tag.contains("/character/x64/"))
            return url.replace("x64", "original");
        return url;
    }

    public void setBigImageUrl(String bigImageUrl) {
        this.bigImageUrl = bigImageUrl;
    }
}
