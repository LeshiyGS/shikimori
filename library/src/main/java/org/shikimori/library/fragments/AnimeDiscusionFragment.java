package org.shikimori.library.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.adapters.CommentsAdapter;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemCommentsShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class AnimeDiscusionFragment extends PullableFragment<BaseActivity> implements Query.OnQuerySuccessListener{

    private String animeId;
    private ListView gvList;
    private CommentsAdapter adapter;
    private String treadId;

    public static AnimeDiscusionFragment newInstance(Bundle b) {
        AnimeDiscusionFragment frag = new AnimeDiscusionFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_list, null);
        gvList = (ListView) v.findViewById(R.id.gvList);
        return  v;
    }

    @Override
    public int pullableViewId() {
        return R.id.gvList;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArgiments();
     }

    @Override
    public void onStartRefresh() {
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.COMMENTS));
        loadData();
    }
    // TODO create loader list
    public void loadData() {
        query.init(ShikiApi.getUrl(ShikiPath.COMMENTS), StatusResult.TYPE.ARRAY)
                .addParam("commentable_id", treadId)
                .addParam("commentable_type", "Entry")
                .addParam("limit", "20")
                .addParam("page", "1")
                .addParam("desc", "1")
                .setCache(true, Query.DAY)
                .getResult(this);
    }

    private void initArgiments() {
        Bundle b = getArguments();
        if(b == null)
            return;

        animeId = getArguments().getString(Constants.ANIME_ID);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        ObjectBuilder builder = new ObjectBuilder(res.getResultArray(), ItemCommentsShiki.class);
        prepareData(builder.list);
        stopRefresh();
    }

    private void prepareData(List<ItemCommentsShiki> list) {
        CommentsAdapter adapter = new CommentsAdapter(activity, list);

        gvList.setAdapter(adapter);

    }

    public void startLoadComments(String treadId) {
        this.treadId = treadId;
        showRefreshLoader();
        loadData();
    }
}
