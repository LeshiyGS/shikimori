package org.shikimori.library.adapters;

import android.content.Context;

import org.shikimori.library.adapters.base.BaseCardGridAdapter;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class AniHistoryAdapter extends BaseCardGridAdapter<AMShiki> {

    public AniHistoryAdapter(Context context, List<AMShiki> list) {
        super(context, list);
    }

    @Override
    public void setValues(ViewHolder holder, AMShiki item) {
        holder.tvTitle.setText(item.name);
        holder.tvTitleRus.setText(item.russianName);
        h.setVisibleGone(holder.tvEpisode);
        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivImage.setImageDrawable(null);
        ShikiImage.show(item.image.preview, holder.ivImage);
    }

}
