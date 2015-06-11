package org.shikimori.library.tool.parser.elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.objects.one.ItemImageShiki;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.ImageController;

/**
 * Created by Владимир on 11.06.2015.
 */
@Deprecated
public class PostAnime extends ImageController {
    private Context context;
    TextView tvTitle;
    private View v;

    public PostAnime(Context context, ItemImageShiki imageData){
        this.context = context;
        this.imageData = imageData;
        initData();
    }

    private void initData() {
        LayoutInflater inf = LayoutInflater.from(context);
        v = inf.inflate(R.layout.item_shiki_anime_grid_small, null);
        image = h.get(v, R.id.ivImage);
        tvTitle = h.get(v, R.id.tvTitle);
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
