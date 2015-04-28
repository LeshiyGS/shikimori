package org.shikimori.library.adapters;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseCardGridAdapter;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.objects.one.ItemUserShiki;
import org.shikimori.library.tool.h;

import java.util.Date;
import java.util.List;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class UserCardStyleAdapter extends BaseCardGridAdapter<ItemUserShiki> {

    private final int online;
    private final int notOnline;

    public UserCardStyleAdapter(Context context, List<ItemUserShiki> list) {
        super(context, list, R.layout.item_shiki_card_grid);
        online = context.getResources().getColor(R.color.greenColor);
        notOnline = context.getResources().getColor(R.color.altarixUiLabelColor);
    }

    @Override
    public void setValues(ViewHolder holder, ItemUserShiki item) {
        holder.tvTitle.setText(item.nickname);
        long time = h.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.last_online_at).getTime();
        long curTime = System.currentTimeMillis() - Query.FIVE_MIN;
        if(time > curTime){
            holder.tvTitleRus.setText(R.string.online);
            holder.tvTitleRus.setTextColor(online);
        }
        else{
            holder.tvTitleRus.setText(h.getTimeAgo(item.last_online_at));
            holder.tvTitleRus.setTextColor(notOnline);
        }

        h.setVisibleGone(holder.tvEpisode);

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivImage.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(item.img_x160, holder.ivImage);
    }
}
