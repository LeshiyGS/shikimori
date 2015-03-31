package org.shikimori.library.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.objects.ItemCaclendarShiki;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class CalendarAdapter extends ArrayAdapter<ItemCaclendarShiki> {

    LayoutInflater inflater;

    public CalendarAdapter(Context context, List<ItemCaclendarShiki> list) {
        super(context, 0, list);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View v = convertView;
        ItemCaclendarShiki item = getItem(position);

        if (convertView == null) {
            v = inflater.inflate(R.layout.item_shiki_calendar, parent, false);
            viewHolder = getViewHolder(v);
            viewHolder.ivImage.setOnTouchListener(h.getImageHighlight);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.tvTitle.setText(item.name);
        viewHolder.tvTitleRus.setText(item.russianName);

        if(item.ongoing){
            viewHolder.tvEpisode.setText(getContext().getString(R.string.serie_name) + " " + item.nextEpisode);
            h.setVisible(viewHolder.tvEpisode, true);
        }else{
            h.setVisibleGone(viewHolder.tvEpisode);
        }

        viewHolder.ivImage.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(ShikiApi.getUrl(item.imgOrigin), viewHolder.ivImage);

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
