package org.shikimori.client.fragments;

import android.view.View;
import android.widget.AdapterView;

import org.shikimori.library.fragments.TopicsFragment;

/**
 * Created by Владимир on 31.03.2015.
 */
public class TopicsShikiFragment extends TopicsFragment {

    public static TopicsShikiFragment newInstance() {
        return new TopicsShikiFragment();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

//        AMShiki item = (AMShiki) parent.getAdapter().getItem(position);
//        Intent i = new Intent(activity, AnimeDetailsActivity.class);
//        i.putExtra(Constants.ITEM_ID, item.id);
//        i.putExtra(Constants.ITEM_NAME, item.name);
//        activity.startActivity(i);
    }
}
