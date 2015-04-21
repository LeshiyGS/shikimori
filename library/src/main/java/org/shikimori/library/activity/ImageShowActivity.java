package org.shikimori.library.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

public class ImageShowActivity extends Activity {

    public static String IMAGE_URL = "image";
    public static String IMAGE_FULL_PATH = "image_full_path";
    public View loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_shiki_image_activity);
        loader = findViewById(R.id.pbLoader);

        Bundle extra = getIntent().getExtras();
        String url = extra.getString(IMAGE_URL);
        String name_full_path = extra.getString(IMAGE_FULL_PATH);
        if (url == null && name_full_path == null) {
            h.showMsg(this, R.string.cant_load_image);
            finish();
        } else {
            if (h.getConnection(this)) {
                ImageView iv = (ImageView) findViewById(R.id.ivBigImage);
                h.initImageLoader(this).displayImage(url, iv, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        h.setVisibleGone(loader);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        h.setVisibleGone(loader);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            } else {
                h.showMsg(this, R.string.error_connection);
            }

        }
    }
}
