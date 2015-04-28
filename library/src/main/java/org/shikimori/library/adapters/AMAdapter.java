package org.shikimori.library.adapters;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.adapters.base.BaseCardGridAdapter;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class AMAdapter extends BaseCardGridAdapter<AMShiki> {

    public AMAdapter(Context context, List<AMShiki> list) {
        super(context, list);
    }

    @Override
    public void setValues(BaseCardGridAdapter.ViewHolder holder, AMShiki item) {
        holder.tvTitle.setText(item.name);
        holder.tvTitleRus.setText(item.russianName);

        h.setVisibleGone(holder.tvEpisode);

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivImage.setImageDrawable(null);
        if(item.image == null || item.image.original == null)
            ImageLoader.getInstance().displayImage(item.poster, holder.ivImage);
        else
            ImageLoader.getInstance().displayImage(item.image.original, holder.ivImage);
    }
}
