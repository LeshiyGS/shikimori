package org.shikimori.library.adapters;

import android.content.Context;

import org.shikimori.library.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.adapters.base.BaseCardGridAdapter;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.objects.one.ItemCaclendarShiki;
import org.shikimori.library.tool.hs;

import java.util.List;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class CalendarAdapter extends BaseCardGridAdapter<ItemCaclendarShiki> {

    public CalendarAdapter(Context context, List<ItemCaclendarShiki> list) {
        super(context, list);
    }

    @Override
    public void setValues(BaseCardGridAdapter.ViewHolder holder, ItemCaclendarShiki item, int position) {
        holder.tvTitle.setText(item.name);
        holder.tvTitleRus.setText(item.russianName);

        if(item.ongoing){
            holder.tvEpisode.setText(getContext().getString(R.string.serie_name) + " " + item.nextEpisode);
            hs.setVisible(holder.tvEpisode, true);
        }else{
            hs.setVisibleGone(holder.tvEpisode);
        }
        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivImage.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(ShikiApi.getUrl(item.imgOrigin), holder.ivImage);
    }

}
