package org.shikimori.client.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import org.shikimori.client.activity.AnimeDetailsActivity;
import org.shikimori.library.fragments.AMListMediaFragment;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by Владимир on 31.03.2015.
 */
public class AnimesShikiFragment extends AMListMediaFragment {

    public static AnimesShikiFragment newInstance() {
        Bundle b = new Bundle();
        b.putString(Constants.TYPE, Constants.ANIME);
        AnimesShikiFragment frag = new AnimesShikiFragment();
        frag.setArguments(b);
        return frag;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        AMShiki item = (AMShiki) parent.getAdapter().getItem(position);
        Intent i = new Intent(activity, AnimeDetailsActivity.class);
        i.putExtra(Constants.ITEM_ID, item.id);
        i.putExtra(Constants.ITEM_NAME, item.name);
        activity.startActivity(i);
    }
}
