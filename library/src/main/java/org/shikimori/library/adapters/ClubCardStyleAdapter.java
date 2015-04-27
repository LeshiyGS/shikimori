package org.shikimori.library.adapters;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.adapters.base.BaseCardGridAdapter;
import org.shikimori.library.objects.ItemClubShiki;
import org.shikimori.library.objects.ItemUserShiki;
import org.shikimori.library.tool.h;

import java.util.Date;
import java.util.List;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class ClubCardStyleAdapter extends BaseCardGridAdapter<ItemClubShiki> {

    public ClubCardStyleAdapter(Context context, List<ItemClubShiki> list) {
        super(context, list);
    }

    @Override
    public void setValues(ViewHolder holder, ItemClubShiki item) {
        holder.tvTitle.setText(item.name);
        h.setVisibleGone(holder.tvTitleRus);
        h.setVisibleGone(holder.tvEpisode);

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivImage.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(item.original, holder.ivImage);
    }

}
