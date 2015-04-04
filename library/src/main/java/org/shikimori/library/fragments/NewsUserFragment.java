package org.shikimori.library.fragments;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import org.shikimori.library.R;
import org.shikimori.library.adapters.NewsUserAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ShikiUser;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class NewsUserFragment extends BaseListViewFragment {

    public static NewsUserFragment newInstance() {
        return new NewsUserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public int getActionBarTitle() {
        return R.string.news;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    protected String url() {
        return ShikiApi.getUrl(ShikiPath.MESSAGES, ShikiUser.USER_ID);
    }


    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        query.invalidateCache(url());
        loadData();
    }

    // TODO create loader list
    public void loadData() {
        if (query == null)
            return;

        query.init(url(), StatusResult.TYPE.ARRAY)
                .addParam("type", "news")
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
