package org.shikimori.library.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.features.profile.controllers.NotifyProfileController;
import org.shikimori.library.tool.hs;

import java.util.List;

import ru.altarix.basekit.library.tools.SimpleBaseAdapter;

/**
 * Created by Владимир on 17.04.2015.
 */
public class NotifyProfileAdapter extends SimpleBaseAdapter<NotifyProfileController.Item, NotifyProfileAdapter.ViewHolder> {


    public NotifyProfileAdapter(Context context, List<NotifyProfileController.Item> list) {
        super(context, list, R.layout.custom_profile_text_view);
    }

    @Override
    public void setValues(ViewHolder holder, NotifyProfileController.Item item, int position) {
        if(item.count > 0){
            hs.setVisible(holder.tvCount, true);
        } else {
            hs.setVisible(holder.tvCount, false);
        }
        holder.tvCount.setText(String.valueOf(item.count));
        holder.tvText.setText(item.name);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.tvText  = find(v, R.id.tvText);
        holder.tvCount = find(v, R.id.tvCount);
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
