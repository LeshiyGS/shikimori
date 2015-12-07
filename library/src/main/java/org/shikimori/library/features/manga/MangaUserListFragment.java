package org.shikimori.library.features.manga;

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
import org.shikimori.library.loaders.Query;
import org.shikimori.library.loaders.httpquery.MyStatusResult;
import org.shikimori.library.objects.ItemUserListShiki;
import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class MangaUserListFragment extends BaseListViewFragment {

    private String listId;
    ObjectBuilder builder = new ObjectBuilder();
    public static MangaUserListFragment newInstance(Bundle b) {
        MangaUserListFragment frag = new MangaUserListFragment();
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
        showRefreshLoader();
        loadData();
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        getFC().getQuery().invalidateCache(ShikiApi.getUrl(ShikiPath.GET_USER_MANGA_LIST, getFC().getUserId()));
        loadData();
    }

    public void loadData() {
        getFC().getQuery().init(ShikiApi.getUrl(ShikiPath.GET_USER_MANGA_LIST, getFC().getUserId()), MyStatusResult.TYPE.ARRAY)
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
    }

    @Override
    public void onQuerySuccess(MyStatusResult res) {
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
        i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.MANGA_PAGE);
        i.putExtra(Constants.ITEM_ID, item.amDetails.id);
        activity.startActivity(i);
    }
}