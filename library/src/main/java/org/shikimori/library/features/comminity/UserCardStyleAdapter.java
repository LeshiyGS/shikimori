package org.shikimori.library.features.comminity;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseCardGridAdapter;
import org.shikimori.library.loaders.Query;
import org.shikimori.library.objects.one.ItemUser;
import org.shikimori.library.tool.hs;

import java.util.List;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class UserCardStyleAdapter extends BaseCardGridAdapter<ItemUser> {

    private final int online;
    private final int notOnline;

    public UserCardStyleAdapter(Context context, List<ItemUser> list) {
        super(context, list, R.layout.item_shiki_card_grid);
        online = context.getResources().getColor(R.color.greenColor);
        notOnline = context.getResources().getColor(R.color.altarixUiLabelColor);
    }

    @Override
    public void setValues(ViewHolder holder, ItemUser item, int position) {
        holder.tvTitle.setText(item.nickname);
        long time = hs.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.lastOnlineAt).getTime();
        long curTime = System.currentTimeMillis() - Query.FIVE_MIN;
        if(time > curTime){
            holder.tvTitleRus.setText(R.string.online);
            holder.tvTitleRus.setTextColor(online);
        }
        else{
            holder.tvTitleRus.setText(hs.getTimeAgo(item.lastOnlineAt));
            holder.tvTitleRus.setTextColor(notOnline);
        }

        hs.setVisibleGone(holder.tvEpisode);

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivImage.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(item.img148, holder.ivImage);
    }
}
