package org.shikimori.library.features.anime;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.features.anime.adapter.UserListAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.objects.ItemUserListShiki;
import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class AnimeUserListFragment extends BaseListViewFragment {

    private String listId;
    ObjectBuilder builder = new ObjectBuilder();

    public static AnimeUserListFragment newInstance(Bundle b) {
        AnimeUserListFragment frag = new AnimeUserListFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    protected boolean isOptionsMenu() {
        return false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArgiments();
        showRefreshLoader();
        loadData();
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        getFC().getQuery().invalidateCache(ShikiApi.getUrl(ShikiPath.GET_USER_ANIME_LIST, getFC().getUserId()));
        loadData();
    }

    public void loadData() {
        getFC().getQuery().init(ShikiApi.getUrl(ShikiPath.GET_USER_ANIME_LIST, getFC().getUserId()), ShikiStatusResult.TYPE.ARRAY)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .addParam("status", listId)
                .setCache(true, QueryShiki.DAY)
                .getResult(this);
    }

    private void initArgiments() {
        Bundle b = getArguments();
        if (b == null)
            return;

        listId = getArguments().getString(Constants.LIST_ID);
    }

    @Override
    public void onQuerySuccess(ShikiStatusResult res) {
        if(activity == null)
            return;
        stopRefresh();
        List<ItemUserListShiki> list = builder.getDataList(res.getResultArray(), ItemUserListShiki.class);
        prepareData(list, true, true);
    }

    @Override
    public ArrayAdapter<ItemUserListShiki> getAdapter(List<?> list) {
        return new UserListAdapter(activity, (List<ItemUserListShiki>) list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ItemUserListShiki item = (ItemUserListShiki) parent.getAdapter().getItem(position);
        Intent i = new Intent(activity, ShowPageActivity.class);
        i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.ANIME_PAGE);
        i.putExtra(Constants.ITEM_ID, item.amDetails.id);
        activity.startActivity(i);
    }
}