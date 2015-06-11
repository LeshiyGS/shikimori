package org.shikimori.library.adapters;

import android.content.Context;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseCardGridAdapter;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class AniPostGaleryAdapter extends BaseCardGridAdapter<AMShiki> {

    public AniPostGaleryAdapter(Context context, List<AMShiki> list) {
        super(context, list, R.layout.item_shiki_anime_grid_small);
    }

    @Override
    public void setValues(ViewHolder holder, AMShiki item, int position) {
        holder.tvTitle.setText(item.name);
        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivImage.setImageDrawable(null);
        ShikiImage.show(item.image.preview, holder.ivImage);
    }

}
