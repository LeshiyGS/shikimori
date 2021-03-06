package org.shikimori.client.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import org.shikimori.client.activity.AnimeDetailsActivity;
import org.shikimori.library.features.calendar.CalendarFragment;
import org.shikimori.library.objects.one.ItemCaclendarShiki;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by Владимир on 31.03.2015.
 */
public class CalendarShikiFragment extends CalendarFragment {


    public static CalendarShikiFragment newInstance() {
        return new CalendarShikiFragment();
    }

    @Override
    public void onItemClick(ItemCaclendarShiki item, int posotion) {
        if(item.isDayHeader)
            return;
        Intent i = new Intent(activity, AnimeDetailsActivity.class);
        i.putExtra(Constants.ITEM_ID, item.id);
        i.putExtra(Constants.ITEM_NAME, item.name);
        activity.startActivity(i);
    }
}
