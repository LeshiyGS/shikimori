package org.shikimori.library.fragments;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemCommentsShiki;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class TopicsFragment extends BaseListViewFragment{

    private String treadId;

    public static TopicsFragment newInstance(Bundle b) {
        TopicsFragment frag = new TopicsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.TOPICS));
        loadData();
    }
    // TODO create loader list
    public void loadData() {
//        query.init(ShikiApi.getUrl(ShikiPath.TOPICS), StatusResult.TYPE.ARRAY)
//            .addParam("limit", LIMIT)
//            .addParam("page", page)
//            .addParam("desc", "1")
//            .setCache(true, Query.HOUR)
//            .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
//        ObjectBuilder builder = new ObjectBuilder(res.getResultArray(), ItemCommentsShiki.class);
//        prepareData(builder.list, true, true);
    }

    @Override
    public ArrayAdapter<ItemCommentsShiki> getAdapter(List list) {
        return null;
    }
}
