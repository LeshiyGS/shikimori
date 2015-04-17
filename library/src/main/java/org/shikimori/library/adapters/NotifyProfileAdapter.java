package org.shikimori.library.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.SimpleBaseAdapter;
import org.shikimori.library.tool.controllers.NotifyProfileController;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Владимир on 17.04.2015.
 */
public class NotifyProfileAdapter extends SimpleBaseAdapter<NotifyProfileController.Item, NotifyProfileAdapter.ViewHolder> {


    public NotifyProfileAdapter(Context context, List<NotifyProfileController.Item> list) {
        super(context, list, R.layout.custom_profile_text_view);
    }

    @Override
    public void setValues(ViewHolder holder, NotifyProfileController.Item item) {
        if(item.count > 0){
            h.setVisible(holder.tvCount, true);
        } else {
            h.setVisible(holder.tvCount, false);
        }
        holder.tvCount.setText(String.valueOf(item.count));
        holder.tvText.setText(item.name);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.tvText  = get(v, R.id.tvText);
        holder.tvCount = get(v, R.id.tvCount);
        return holder;
    }

    public void setData(List<NotifyProfileController.Item> list){
        clear();
        for (NotifyProfileController.Item item : list) {
            add(item);
        }
    }

    class ViewHolder {
        TextView tvText, tvCount;
    }

}
