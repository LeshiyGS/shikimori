package org.shikimori.client.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import org.shikimori.client.activity.AnimeDetailsActivity;
import org.shikimori.library.fragments.AnimeDeatailsFragment;
import org.shikimori.library.fragments.CalendarFragment;
import org.shikimori.library.objects.ItemCaclendarShiki;

/**
 * Created by Владимир on 31.03.2015.
 */
public class CalendarShikiFragment extends CalendarFragment {


    public static CalendarShikiFragment newInstance() {
        return new CalendarShikiFragment();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        ItemCaclendarShiki item = (ItemCaclendarShiki) parent.getAdapter().getItem(position);
        Intent i = new Intent(activity, AnimeDetailsActivity.class);
        i.putExtra(AnimeDeatailsFragment.ANIME_ID, item.id);
        activity.startActivity(i);
    }
}
