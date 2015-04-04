package org.shikimori.library.fragments;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import org.shikimori.library.adapters.CommentsAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class DiscusionFragment extends BaseListViewFragment{

    private String treadId;

    public static DiscusionFragment newInstance(Bundle b) {
        DiscusionFragment frag = new DiscusionFragment();
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
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.COMMENTS));
        loadData();
    }
    // TODO create loader list
    public void loadData() {
        if(query==null)
            return;
        query.init(ShikiApi.getUrl(ShikiPath.COMMENTS), StatusResult.TYPE.ARRAY)
                .addParam("commentable_id", treadId)
                .addParam("commentable_type", "Entry")
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .addParam("desc", "1")
                .setCache(true, Query.HOUR)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        ObjectBuilder builder = new ObjectBuilder(res.getResultArray(), ItemCommentsShiki.class);
        prepareData(builder.list, true, true);
    }

    @Override
    public ArrayAdapter<ItemCommentsShiki> getAdapter(List list) {
        return new CommentsAdapter(activity, list);
    }

    public void startLoadComments(String treadId) {
        this.treadId = treadId;
        // Ждем пока фрагмент присасется к активити и не инициализируеться
        Thread mythread = new Thread() {
            public void run() {
                try {
                    while (query == null){
                        sleep(500);
                    }
                } catch (Exception e) {}

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showRefreshLoader();
                        loadData();
                    }
                });
            }
        };
        mythread.start();
    }
}
