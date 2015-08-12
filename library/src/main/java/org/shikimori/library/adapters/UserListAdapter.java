package org.shikimori.library.adapters;

/**
 * Created by LeshiyGS on 04.04.2015.
 */
import android.content.Context;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.BaseHolder;
import org.shikimori.library.objects.ItemUserListShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.hs;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class UserListAdapter extends BaseListAdapter<ItemUserListShiki, BaseHolder> {

    public UserListAdapter(Context context, List<ItemUserListShiki> list) {
        super(context, list, R.layout.item_shiki_user_list);
    }

    @Override
    public void setValues(BaseHolder holder, ItemUserListShiki item, int position) {
        if(item.amDetails!=null){
            holder.tvName.setText(item.amDetails.name);
            holder.tvRusName.setText(item.amDetails.russianName);
            if (ProjectTool.getTypeFromUrl(item.amDetails.url) == ProjectTool.TYPE.MANGA){
                holder.tvText.setText(String.format(getContext().getString(R.string.reading_all), item.chapters, item.amDetails.chapters));
            }else if (ProjectTool.getTypeFromUrl(item.amDetails.url) == ProjectTool.TYPE.ANIME) {
                holder.tvText.setText(String.format(getContext().getString(R.string.seeing_all), item.episodes, item.amDetails.episodesAired, item.amDetails.episodes));
            }
            if(item.amDetails!=null){
                holder.tvStatus.setText(ProjectTool.getStatus(getContext(), item.amDetails.anons, item.amDetails.ongoing));
                ProjectTool.setStatusColor(getContext(), holder.tvStatus, item.amDetails.anons, item.amDetails.ongoing);
            } else
                hs.setVisibleGone(holder.tvStatus);

            if(item.amDetails!=null)
                ShikiImage.show(item.amDetails.image.original, holder.ivPoster, true);
            else
                hs.setVisibleGone(holder.ivPoster);

        }
    }
}