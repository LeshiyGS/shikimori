package org.shikimori.library.tool.parser.elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.objects.one.ItemImageShiki;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.parser.ImageController;

/**
 * Created by Феофилактов on 09.04.2015.
 */
public class VideoImage extends ImageController{
    private final Context context;
    private String url;
    private View v;
    private TextView tvMarker;

    public VideoImage(Context activity, ItemImageShiki imageData, String url) {
        this.context = activity;
        this.url = url;
        this.imageData = imageData;
        initImage();
    }

    public View getView(){
        return v;
    }

    private void initImage() {
        //Вставляем картинку
        v = LayoutInflater.from(context).inflate(R.layout.shiki_video_view, null);

        ViewGroup.LayoutParams params = hs.getDefaultParams();
        params.width = hs.pxToDp(250, context);
        v.setLayoutParams(params);
        image = (ImageView) v.findViewById(R.id.vBack);
        tvMarker = (TextView) v.findViewById(R.id.tvMarker);
        ImageView icon = (ImageView) v.findViewById(R.id.icon);

        icon.setTag(url);
        icon.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View view) {
                String url = (String) view.getTag();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
//                if (imageData.getClickListener() != null)
//                    imageData.getClickListener().onClick(view);

            }
        });
    }

    public void setMarker(String html) {
        tvMarker.setText(html);
    }
}
