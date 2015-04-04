package org.shikimori.library.fragments;

import android.widget.ArrayAdapter;

import org.shikimori.library.R;
import org.shikimori.library.adapters.NewsUserAdapter;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.ItemNewsUserShiki;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class NotificationsUserFragment extends NewsUserFragment {

    public static NotificationsUserFragment newInstance() {
        return new NotificationsUserFragment();
    }

    @Override
    public int getActionBarTitle() {
        return R.string.notifying;
    }

    // TODO create loader list
    public void loadData() {
        if (query == null)
            return;

        query.init(url(), StatusResult.TYPE.ARRAY)
                .addParam("type", "notifications")
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .addParam("desc", "1")
                .setCache(true, Query.HOUR)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        ObjectBuilder<ItemNewsUserShiki> builder = new ObjectBuilder<>(res.getResultArray(), ItemNewsUserShiki.class);
        prepareData(builder.list, true, true);
    }

    @Override
    public ArrayAdapter<ItemNewsUserShiki> getAdapter(List list) {
        return new NewsUserAdapter(activity, list);
    }

}
