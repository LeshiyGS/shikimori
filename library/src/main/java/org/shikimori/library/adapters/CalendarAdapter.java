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

import org.shikimori.library.adapters.base.BaseAnimeGridAdapter;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.objects.ItemCaclendarShiki;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class CalendarAdapter extends BaseAnimeGridAdapter<ItemCaclendarShiki> {

    public CalendarAdapter(Context context, List<ItemCaclendarShiki> list) {
        super(context, list);
    }

    @Override
    public void setValues(BaseAnimeGridAdapter.ViewHolder holder, ItemCaclendarShiki item) {
        holder.tvTitle.setText(item.name);
        holder.tvTitleRus.setText(item.russianName);

        if(item.ongoing){
            holder.tvEpisode.setText(getContext().getString(R.string.serie_name) + " " + item.nextEpisode);
            h.setVisible(holder.tvEpisode, true);
        }else{
            h.setVisibleGone(holder.tvEpisode);
        }
        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivImage.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(ShikiApi.getUrl(item.imgOrigin), holder.ivImage);
    }

}
