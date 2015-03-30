package org.shikimori.library.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.objects.ItemAnimesShiki;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class AnimesAdapter extends ArrayAdapter<ItemAnimesShiki> {

    LayoutInflater inflater;

    public AnimesAdapter(Context context, List<ItemAnimesShiki> list) {
        super(context, 0, list);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View v = convertView;
        ItemAnimesShiki item = getItem(position);

        if (convertView == null) {
            v = inflater.inflate(R.layout.item_shiki_calendar, parent, false);
            viewHolder = getViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.tvTitle.setText(item.name);
        viewHolder.tvTitleRus.setText(item.russian);

        viewHolder.tvEpisode.setVisibility(View.GONE);

        viewHolder.ivImage.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(ShikiApi.getUrl(item.original), viewHolder.ivImage);

        return v;
    }

    ViewHolder getViewHolder(View v){
        ViewHolder holder = new ViewHolder();
        holder.ivImage = (ImageView) v.findViewById(R.id.ivImage);
        holder.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        holder.tvTitleRus = (TextView) v.findViewById(R.id.tvTitleRus);
        holder.tvEpisode = (TextView) v.findViewById(R.id.tvEpisode);
        return holder;
    }

    static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvTitleRus, tvEpisode;
    }
}
