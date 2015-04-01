package org.shikimori.library.adapters.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Владимир on 01.04.2015.
 */
public abstract class BaseAnimeGridAdapter<T> extends SimpleBaseAdapter<T, BaseAnimeGridAdapter.ViewHolder> {

    public BaseAnimeGridAdapter(Context context, List<T> list) {
        super(context, list, R.layout.item_shiki_anime_grid);
    }

    @Override
    public void setListeners(ViewHolder holder) {
        super.setListeners(holder);
        holder.ivImage.setOnTouchListener(h.getImageHighlight);
    }

    @Override
    public ViewHolder getViewHolder(View v){
        ViewHolder holder = new ViewHolder();
        holder.ivImage = (ImageView) v.findViewById(R.id.ivImage);
        holder.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        holder.tvTitleRus = (TextView) v.findViewById(R.id.tvTitleRus);
        holder.tvEpisode = (TextView) v.findViewById(R.id.tvEpisode);
        return holder;
    }

    public static class ViewHolder {
        public ImageView ivImage;
        public TextView tvTitle, tvTitleRus, tvEpisode;
    }
}
