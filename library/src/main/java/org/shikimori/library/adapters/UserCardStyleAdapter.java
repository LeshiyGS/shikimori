package org.shikimori.library.adapters;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.adapters.base.BaseAnimeGridAdapter;
import org.shikimori.library.adapters.base.BaseCardGridAdapter;
import org.shikimori.library.objects.ItemUserShiki;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.h;

import java.util.Date;
import java.util.List;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class UserCardStyleAdapter extends BaseCardGridAdapter<ItemUserShiki> {

    public UserCardStyleAdapter(Context context, List<ItemUserShiki> list) {
        super(context, list);
    }

    @Override
    public void setValues(ViewHolder holder, ItemUserShiki item) {
        holder.tvTitle.setText(item.nickname);
        Date date = h.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.last_online_at);
        long time = date.getTime();
        holder.tvTitleRus.setText(formatDate(time, "HH:mm:ss dd/MM/yyyy"));

        h.setVisibleGone(holder.tvEpisode);

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivImage.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(item.img_x160, holder.ivImage);
    }

    String formatDate(long date, String format) {
        return h.getStringDate(format, new Date(date));
    }
}
