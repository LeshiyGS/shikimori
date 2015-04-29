package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.R;
import org.shikimori.library.adapters.UserCardStyleAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.ItemUserShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;

import java.util.List;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class CommunityUsersFragment extends BaseGridViewFragment implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    public static CommunityUsersFragment newInstance() {
        return new CommunityUsersFragment();
    }

//    @Override
//    public int getActionBarTitle() {
//        return R.string.community;
//    }

    protected String getLoadPath() {
        return ShikiApi.getUrl(ShikiPath.GET_USERS_LIST);
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        query.invalidateCache(getLoadPath());
        loadData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        StartFirstLoad();
    }

    @Override
    public void loadData() {
        query.init(getLoadPath(), StatusResult.TYPE.ARRAY)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .addParam("search", getSearchText())
                .setCache(true, Query.FIVE_MIN)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        super.onQuerySuccess(res);
        ObjectBuilder builder = new ObjectBuilder(res.getResultArray(), ItemUserShiki.class);
        prepareData(builder.list, true, true);
    }

    @Override
    public ArrayAdapter<ItemUserShiki> getAdapter(List<?> list) {
        return new UserCardStyleAdapter(activity, (List<ItemUserShiki>) list);
    }

    @Override
    protected Menu getActionBarMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.community_menu, menu);
        inflateSearch(menu);
        return menu;
    }

}
