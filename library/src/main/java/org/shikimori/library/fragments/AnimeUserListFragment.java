package org.shikimori.library.fragments;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.adapters.UserListAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemUserListShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class AnimeUserListFragment extends BaseListViewFragment {

    private String userId;
    private String listId;
    public static final int LIMIT = 250;
    private UserListAdapter adapter;

    public static AnimeUserListFragment newInstance(Bundle b) {
        AnimeUserListFragment frag = new AnimeUserListFragment();
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
//        ActionBar actionBar = getActivity().getActionBar();
        if (listId.equals("0")) ;
        else if (listId.equals("1")) ;
        else if (listId.equals("2")) ;
        else if (listId.equals("3")){  listId = "9";}
        else if(listId.equals("4")){  listId = "3";}
        else if(listId.equals("5")){  listId = "4";}
        showRefreshLoader();
        loadData();
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        query.invalidateCache(ShikiApi.getUrl(String.format(ShikiPath.GET_USER_ANIME_LIST, userId)));
        loadData();
    }

    public void loadData() {
        query.init(ShikiApi.getUrl(String.format(ShikiPath.GET_USER_ANIME_LIST, userId)), StatusResult.TYPE.ARRAY)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .addParam("status", listId)
                .setCache(true, Query.DAY)
                .getResult(this);
    }

    private void initArgiments() {
        Bundle b = getArguments();
        if (b == null)
            return;

        listId = getArguments().getString(Constants.LIST_ID);
        userId = getArguments().getString(Constants.USER_ID);

    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        ObjectBuilder builder = new ObjectBuilder(res.getResultArray(), ItemUserListShiki.class);
        prepareData(builder.list, true, true);
    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public ArrayAdapter<ItemUserListShiki> getAdapter(List<?> list) {
        return new UserListAdapter(activity, (List<ItemUserListShiki>) list);
    }

}