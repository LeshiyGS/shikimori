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
import org.shikimori.library.tool.h;

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
            if(item.amDetails!=null){
                holder.tvStatus.setText(ProjectTool.getStatus(getContext(), item.amDetails.anons, item.amDetails.ongoing));
                ProjectTool.setStatusColor(getContext(), holder.tvStatus, item.amDetails.anons, item.amDetails.ongoing);
            } else
                h.setVisibleGone(holder.tvStatus);

            if(item.amDetails!=null)
                ShikiImage.show(item.amDetails.image.original, holder.ivPoster, true);
            else
                h.setVisibleGone(holder.ivPoster);

        }
    }
}