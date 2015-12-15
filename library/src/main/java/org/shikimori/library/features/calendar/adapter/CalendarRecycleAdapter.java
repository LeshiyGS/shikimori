package org.shikimori.library.features.calendar.adapter;

import android.content.Context;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.abstracts.recycleview.ListRecycleAdapter;
import org.shikimori.library.fragments.base.abstracts.recycleview.OnItemClickRecycleListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.objects.one.ItemCaclendarShiki;
import org.shikimori.library.tool.hs;

import java.util.List;

import ru.altarix.basekit.library.tools.h;

/**
 * Created by Феофилактов on 15.12.2015.
 */
public class CalendarRecycleAdapter extends ListRecycleAdapter<ItemCaclendarShiki, CalendarRecycleHolder> {
    public CalendarRecycleAdapter(Context context, List<ItemCaclendarShiki> items, OnItemClickRecycleListener clickRecycleListener) {
        super(context, items, R.layout.item_shiki_anime_calendar);
        setOnItemClickListener(clickRecycleListener);
    }

    @Override
    public CalendarRecycleHolder getViewHolder(View view) {
        return new CalendarRecycleHolder(view);
    }

    @Override
    public void setListeners(CalendarRecycleHolder calendarRecycleHolder) {

    }

    @Override
    public void setValues(CalendarRecycleHolder holder, ItemCaclendarShiki item, int i) {
        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        h.setVisibleGone(!item.isDayHeader, holder.llDay);
        holder.ivImage.setImageDrawable(null);
        if(item.isDayHeader){
            holder.tvDay.setText(item.name);
        } else {
            holder.tvTitle.setText(item.name);
            holder.tvTitleRus.setText(item.russianName);
            holder.tvDayAnime.setText(item.day);
            hs.setVisibleGone(!item.ongoing, holder.tvEpisode);
            holder.tvEpisode.setText(getContext().getString(R.string.serie_name, item.nextEpisode));
            ImageLoader.getInstance().displayImage(ShikiApi.getUrl(item.imgOrigin), holder.ivImage);
        }
    }
}
