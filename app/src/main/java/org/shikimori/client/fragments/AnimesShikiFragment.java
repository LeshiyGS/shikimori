package org.shikimori.client.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import org.shikimori.client.activity.AnimeDetailsActivity;
import org.shikimori.library.fragments.AnimeDeatailsFragment;
import org.shikimori.library.fragments.AnimesFragment;
import org.shikimori.library.objects.ItemAnimesShiki;
import org.shikimori.library.tool.Constants;

/**
 * Created by Владимир on 31.03.2015.
 */
public class AnimesShikiFragment extends AnimesFragment {

    public static AnimesShikiFragment newInstance() {
        return new AnimesShikiFragment();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        ItemAnimesShiki item = (ItemAnimesShiki) parent.getAdapter().getItem(position);
        Intent i = new Intent(activity, AnimeDetailsActivity.class);
        i.putExtra(Constants.ANIME_ID, item.id);
        activity.startActivity(i);
    }
}
