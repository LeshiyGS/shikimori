package org.shikimori.library.adapters.base;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Владимир on 01.04.2015.
 */
public abstract class BaseMessageListAdapter<T> extends SimpleBaseAdapter<T, BaseMessageListAdapter.ViewHolder> {

    public BaseMessageListAdapter(Context context, List<T> list) {
        super(context, list, R.layout.item_shiki_message_list);
    }

    @Override
    public void setListeners(ViewHolder holder) {
        super.setListeners(holder);
        holder.ivUser.setOnTouchListener(h.getImageHighlight);
        holder.ivPoster.setOnTouchListener(h.getImageHighlight);
    }

    @Override
    public ViewHolder getViewHolder(View v){
        ViewHolder holder = new ViewHolder();
        holder.ivUser = (ImageView) v.findViewById(R.id.ivUser);
        holder.ivPoster = (ImageView) v.findViewById(R.id.ivPoster);
        holder.tvCommentsDate = (TextView) v.findViewById(R.id.tvCommentsDate);
        holder.tvCommentsText = (TextView) v.findViewById(R.id.tvCommentsText);
        holder.tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        return holder;
    }

    public static class ViewHolder {
        public ImageView ivUser, ivPoster;
        public TextView tvUserName, tvCommentsDate, tvCommentsText;
    }
}
