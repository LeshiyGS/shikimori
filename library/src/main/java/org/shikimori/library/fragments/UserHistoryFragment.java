package org.shikimori.library.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.UserHistoryListAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.ItemUserHistory;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class UserHistoryFragment extends BaseListViewFragment {

    public static UserHistoryFragment newInstance() {
        return new UserHistoryFragment();
    }
    public static UserHistoryFragment newInstance(String userId) {
        Bundle b = new Bundle();
        b.putString(Constants.USER_ID, userId);
        UserHistoryFragment frag = new UserHistoryFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getActionBarTitle() {
        return R.string.history;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showRefreshLoader();
        loadData();
    }

    protected String url() {
        return ShikiApi.getUrl(ShikiPath.HISTORY, getUserId());
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        query.invalidateCache(url());
        loadData();
    }

    // TODO create loader list
    public void loadData() {
        if (query == null)
            return;

        query.init(url(), StatusResult.TYPE.ARRAY)
            .addParam("limit", LIMIT)
            .addParam("page", page)
            .addParam("desc", "1")
            .setCache(true, Query.FIVE_MIN)
            .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        ObjectBuilder<ItemUserHistory> builder = new ObjectBuilder<>(res.getResultArray(), ItemUserHistory.class);
        prepareData(builder.list, true, true);
    }

    @Override
    public ArrayAdapter<ItemUserHistory> getAdapter(List list) {
        return new UserHistoryListAdapter(activity, list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        Adapter adp = parent.getAdapter();
        if(adp == null)
            return;

        ItemUserHistory item = (ItemUserHistory) adp.getItem(position);
        Intent i = new Intent(activity, ShowPageActivity.class);

        //TODO
        //Проверка на наличие епизодов.. если  нет то это манга, а не аниме.
        //Нужно Сделать проверку на тип в самом AMShiki и удалить ItemMangaShiki
        //Т.К. в Истории былают еще и записи импорта с других сайтов
        if (item.target.episodes != null)
            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.ANIME_PAGE);
        else
            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.MANGA_PAGE);
        i.putExtra(Constants.ITEM_ID, item.target.id);
        activity.startActivity(i);

    }

}
