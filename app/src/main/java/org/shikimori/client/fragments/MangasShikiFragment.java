package org.shikimori.client.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import org.shikimori.client.activity.MangaDetailsActivity;
import org.shikimori.library.fragments.MangasFragment;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by Владимир on 31.03.2015.
 */
public class MangasShikiFragment extends MangasFragment {

    public static MangasShikiFragment newInstance() {
        return new MangasShikiFragment();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        AMShiki item = (AMShiki) parent.getAdapter().getItem(position);
        Intent i = new Intent(activity, MangaDetailsActivity.class);
        i.putExtra(Constants.ITEM_ID, item.id);
        i.putExtra(Constants.ITEM_NAME, item.name);
        activity.startActivity(i);
    }
}
