package org.shikimori.client.adapters.holdersEasy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.client.R;
import org.shikimori.client.loaders.ShikiApi;
import org.shikimori.client.objects.ItemCaclendarShiki;
import org.shikimori.client.tool.h;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

/**
 * Created by Феофилактов on 24.02.2015.
 */
@LayoutId(R.layout.item_shiki_calendar)
public class ListCalendarHolder extends ItemViewHolder<ItemCaclendarShiki> {

    @ViewId(R.id.ivImage)
    ImageView ivImage;

    @ViewId(R.id.tvTitle)
    TextView tvTitle;

    @ViewId(R.id.tvTitleRus)
    TextView tvTitleRus;

    @ViewId(R.id.tvEpisode)
    TextView tvEpisode;

    public ListCalendarHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(ItemCaclendarShiki item, PositionInfo positionInfo) {
        tvTitle.setText(item.name);
        tvTitleRus.setText(item.russianName);

        if(item.ongoing){
            tvEpisode.setText(getContext().getString(R.string.serie_name) + " " + item.nextEpisode);
            h.setVisible(tvEpisode, true);
        }else{
            h.setVisibleGone(tvEpisode);
        }

        ivImage.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(ShikiApi.getUrl(item.imgOrigin), ivImage);
    }
}
