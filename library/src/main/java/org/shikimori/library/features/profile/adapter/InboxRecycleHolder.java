package org.shikimori.library.features.profile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.adapters.holder.SettingsRecycleHolder;
import org.shikimori.library.fragments.base.abstracts.recycleview.BaseRecycleHolder;

/**
 * Created by Владимир on 06.04.2015.
 */
public class InboxRecycleHolder extends SettingsRecycleHolder {
    public View llFromUserName;
    public ImageView ivFromUser;
    public TextView tvFromUser;

    public InboxRecycleHolder(View itemView) {
        super(itemView);
        llFromUserName = find(R.id.llFromUserName);
        ivFromUser = find(R.id.ivFromUser);
        tvFromUser = find(R.id.tvFromUser);
    }
}
