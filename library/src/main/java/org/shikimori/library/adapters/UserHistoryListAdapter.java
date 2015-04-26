package org.shikimori.library.adapters;

/**
 * Created by LeshiyGS on 04.04.2015.
 */

import android.content.Context;
import android.view.View;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.BaseHolder;
import org.shikimori.library.objects.one.ItemUserHistory;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiImage;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class UserHistoryListAdapter extends BaseListAdapter<ItemUserHistory, BaseHolder> {

    public UserHistoryListAdapter(Context context, List<ItemUserHistory> list) {
        super(context, list, R.layout.item_shiki_user_history_list);
    }

    @Override
    public void setValues(BaseHolder holder, ItemUserHistory item) {

        holder.tvText.setText(item.description);
        holder.tvDate.setText(ProjectTool.formatDatePost(item.createdAt));

        if (item.target != null) {
            holder.tvName.setText(item.target.name);
            ProjectTool.setStatusColor(getContext(), holder.tvStatus, item.target.anons, item.target.ongoing);
            holder.tvStatus.setText(ProjectTool.getStatus(getContext(),
                    item.target.anons, item.target.ongoing));

            holder.tvType.setText(ProjectTool.getTypeFromUrl(getContext(), item.target.url));

            ShikiImage.show(item.target.image.preview, holder.ivPoster, true);
        }
    }
}