package org.shikimori.library.features.calendar.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.tool.hs;

import ru.altarix.basekit.library.fragment.list.recycleview.BaseRecycleHolder;

/**
 * Created by Феофилактов on 15.12.2015.
 */
public class CalendarRecycleHolder extends BaseRecycleHolder {
    public ImageView ivImage;
    public TextView tvTitle,tvDayAnime,tvTitleRus,tvEpisode,tvDay;
    public View llDay;

    public CalendarRecycleHolder(View itemView) {
        super(itemView);

        ivImage = find(R.id.ivImage);
        ivImage.setOnTouchListener(hs.getImageHighlight);
        tvTitle = find(R.id.tvTitle);
        tvTitleRus = find(R.id.tvTitleRus);
        tvEpisode = find(R.id.tvEpisode);
        llDay = find(R.id.llDay);
        tvDay = find(R.id.tvDay);
        tvDayAnime = find(R.id.tvDayAnime);

    }
}
