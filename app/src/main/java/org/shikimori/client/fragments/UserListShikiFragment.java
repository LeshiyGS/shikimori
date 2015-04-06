package org.shikimori.client.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import org.shikimori.client.activity.AnimeDetailsActivity;
import org.shikimori.library.fragments.AnimeUserListFragment;
import org.shikimori.library.objects.ItemUserListShiki;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by LeshiyGS on 06.04.2015.
 */
public class UserListShikiFragment extends AnimeUserListFragment {

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
