package org.shikimori.library.tool.parser.elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.objects.one.ItemImageShiki;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.parser.ImageController;

/**
 * Created by Владимир on 11.06.2015.
 */
public class PostAnime extends ImageController {
    private Context context;
    TextView tvTitle;

    public String getUrl() {
        return url;
    }

    String url;
    private View v;

    public PostAnime(Context context, ItemImageShiki imageData, String url){
        this.context = context;
        this.url = url;
        this.imageData = imageData;
        initData();
    }

    public void setUrl(String url){
        this.url = url;
    }

    private void initData() {
        LayoutInflater inf = LayoutInflater.from(context);
        v = inf.inflate(R.layout.item_shiki_anime_grid_small, null);
        image = hs.get(v, R.id.ivImage);
        tvTitle = hs.get(v, R.id.tvTitle);
        image.setTag(url);
        image.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View view) {
                if(imageData.getClickListener()!=null)
                    imageData.getClickListener().onClick(view);
            }
        });
    }

    public void setTitle(String text){
        tvTitle.setText(text);
    }

    public View getView() {
        return v;
    }
}
