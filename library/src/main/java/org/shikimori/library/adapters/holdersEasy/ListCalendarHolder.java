package org.shikimori.library.adapters.holdersEasy;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;


/**
 * Created by Феофилактов on 24.02.2015.
 */
//@LayoutId(R.layout.item_shiki_anime_grid)
public class ListCalendarHolder  {

//    @ViewId(R.id.ivImage)
    ImageView ivImage;

//    @ViewId(R.id.tvTitle)
    TextView tvTitle;

//    @ViewId(R.id.tvTitleRus)
    TextView tvTitleRus;

//    @ViewId(R.id.tvEpisode)
    TextView tvEpisode;

    public void onSetValues(String item) {
//        tvTitle.setText(item.name);
//        tvTitleRus.setText(item.russianName);
//
//        if(item.ongoing){
            tvEpisode.setText(R.string.serie_name);
//            h.setVisible(tvEpisode, true);
//        }else{
//            h.setVisibleGone(tvEpisode);
//        }
//
//        ivImage.setImageDrawable(null);
//        ImageLoader.getInstance().displayImage(ShikiApi.getUrl(item.imgOrigin), ivImage);
    }
}
