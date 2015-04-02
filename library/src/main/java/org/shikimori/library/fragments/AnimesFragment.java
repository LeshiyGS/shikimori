package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import org.shikimori.library.R;
import org.shikimori.library.adapters.AnimesAdapter;
import org.shikimori.library.fragments.base.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemAnimesShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;

import java.util.List;

/**
 * Created by Владимир on 27.03.2015.
 */
public class AnimesFragment extends BaseGridViewFragment implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    private ObjectBuilder builder;
    private static int LIMIT = 20;
    private AnimesAdapter adapter;

    public static AnimesFragment newInstance() {
        return new AnimesFragment();
    }

    @Override
    public int getActionBarTitle() {
        return R.string.anime;
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.ANIMES));
        loadData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        StartFirstLoad();
    }

    @Override
    public void loadData() {
        query.init(ShikiApi.getUrl(ShikiPath.ANIMES), StatusResult.TYPE.ARRAY)
            .addParam("limit", LIMIT)
            .addParam("page", page)
            .addParam("search", getSearchText())
            .setCache(true, Query.DAY)
            .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        super.onQuerySuccess(res);
        builder = new ObjectBuilder(res.getResultArray(), ItemAnimesShiki.class);
        int size = builder.list.size();
        // если предыдущее количество кратно limit+1
        // значит есть еще данные
        if(size!=0 && size%((LIMIT)+1) == 0){
            hasMoreItems(true);
            // удаляем последний элемент
            builder.list.remove(builder.list.size() - 1);
        } else
            hasMoreItems(false);

        prepareData(builder.list);
    }

    private void prepareData(List<ItemAnimesShiki> list) {
        if (adapter == null){
            adapter = new AnimesAdapter(activity, list);
            setAdapter(adapter);
        }else {
            if(page == DEFAULT_FIRST_PAGE)
                adapter.clear();

            for (int i = 0; i < list.size(); i++) {
                adapter.add(list.get(i));
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_animes_filter) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
