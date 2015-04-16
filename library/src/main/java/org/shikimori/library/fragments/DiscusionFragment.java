package org.shikimori.library.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.shikimori.library.adapters.CommentsAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.loaders.BackGroubdLoader;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemTopicsShiki;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class DiscusionFragment extends BaseListViewFragment implements ExtraLoadInterface {

    private String treadId;
    BodyBuild bodyBuilder;

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
        bodyBuilder = new BodyBuild(activity);

        initParams();

        if(treadId!=null){
            showRefreshLoader();
            loadData();
        }
    }

    private void initParams() {
        Bundle b = getArguments();
        if(b == null)
            return;
        if(treadId==null)
            treadId = b.getString(Constants.TREAD_ID);
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
    public void onQuerySuccess(final StatusResult res) {
        loadAsyncBuild(bodyBuilder,  res.getResultArray(), ItemCommentsShiki.class);
    }

    @Override
    public ArrayAdapter<ItemCommentsShiki> getAdapter(List list) {
        return new CommentsAdapter(activity, list);
    }

    @Override
    public void extraLoad(String itemId) {
        this.treadId = itemId;
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
