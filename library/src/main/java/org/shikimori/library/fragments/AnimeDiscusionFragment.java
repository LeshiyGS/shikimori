package org.shikimori.library.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.adapters.AnimesAdapter;
import org.shikimori.library.adapters.CommentsAdapter;
import org.shikimori.library.fragments.base.BaseListViewFragment;
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
public class AnimeDiscusionFragment extends BaseListViewFragment{

    private String animeId;
    private String treadId;
    public static final int LIMIT = 20;
    private CommentsAdapter adapter;

    public static AnimeDiscusionFragment newInstance(Bundle b) {
        AnimeDiscusionFragment frag = new AnimeDiscusionFragment();
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
        initArgiments();
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.COMMENTS));
        loadData();
    }
    // TODO create loader list
    public void loadData() {
        query.init(ShikiApi.getUrl(ShikiPath.COMMENTS), StatusResult.TYPE.ARRAY)
                .addParam("commentable_id", treadId)
                .addParam("commentable_type", "Entry")
                .addParam("limit", LIMIT)
                .addParam("page", page)
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
        stopRefresh();
        ObjectBuilder builder = new ObjectBuilder(res.getResultArray(), ItemCommentsShiki.class);

        int size = builder.list.size();
        // если предыдущее количество кратно limit+1
        // значит есть еще данные
        if(size!=0 && size%((LIMIT)+1) == 0){
            hasMoreItems(true);
            // удаляем последний элемент
            builder.list.remove(size - 1);
        } else
            hasMoreItems(false);
        prepareData(builder.list);
    }

    private void prepareData(List<ItemCommentsShiki> list) {
        if (adapter == null){
            adapter = new CommentsAdapter(activity, list);
            setAdapter(adapter);
        }else {
            if(page == DEFAULT_FIRST_PAGE)
                adapter.clear();

            for (int i = 0; i < list.size(); i++) {
                adapter.add(list.get(i));
            }
        }
    }

    public void startLoadComments(String treadId) {
        this.treadId = treadId;
        showRefreshLoader();
        loadData();
    }
}