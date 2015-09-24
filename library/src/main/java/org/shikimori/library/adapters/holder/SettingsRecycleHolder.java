package org.shikimori.library.adapters.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.abstracts.recycleview.BaseRecycleHolder;

/**
 * Created by Владимир on 06.04.2015.
 */
public class SettingsRecycleHolder extends BaseRecycleHolder {
    public View ivSettings, tvRead;
    public ImageView ivUser, ivPoster;
    public TextView tvName, tvText, tvDate, tvRusName, tvStatus, tvType;
    public ViewGroup llBodyHtml;
    public SettingsRecycleHolder(View itemView) {
        super(itemView);

        ivSettings = find(R.id.icSettings);
        tvRead = find(R.id.tvRead);

        ivPoster = find(R.id.ivPoster);
        ivUser = find(R.id.ivUser);
        tvDate = find(R.id.tvDate);
        tvText = find(R.id.tvText);
        tvName = find(R.id.tvName);
        tvType = find(R.id.tvType);
        tvRusName = find(R.id.tvRusName);
        tvStatus = find(R.id.tvStatus);
        llBodyHtml = find(R.id.llBodyHtml);
    }
}
