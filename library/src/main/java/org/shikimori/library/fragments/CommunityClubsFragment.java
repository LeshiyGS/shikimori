package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.R;
import org.shikimori.library.adapters.ClubCardStyleAdapter;
import org.shikimori.library.adapters.UserCardStyleAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseCardGridViewFragment;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemClubShiki;
import org.shikimori.library.objects.ItemUserShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;

import java.util.List;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class CommunityClubsFragment extends BaseCardGridViewFragment implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    @Override
    protected Menu getActionBarMenu(Menu menu, MenuInflater inflater){
        return menu;
    }

    public static CommunityClubsFragment newInstance() {
        return new CommunityClubsFragment();
    }

    @Override
    public int getActionBarTitle() {
        return R.string.community;
    }

    protected String getLoadPath() {
        return ShikiPath.GET_CLUBS_LIST;
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        query.invalidateCache(ShikiApi.getUrl(getLoadPath()));
        loadData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        StartFirstLoad();
    }

    @Override
    public void loadData() {
        query.init(ShikiApi.getUrl(getLoadPath()), StatusResult.TYPE.ARRAY)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .setCache(true, Query.FIVE_MIN)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        super.onQuerySuccess(res);
        ObjectBuilder builder = new ObjectBuilder(res.getResultArray(), ItemClubShiki.class);
        prepareData(builder.list, true, true);
    }

    @Override
    public ArrayAdapter<ItemClubShiki> getAdapter(List<?> list) {
        return new ClubCardStyleAdapter(activity, (List<ItemClubShiki>) list);
    }

}
