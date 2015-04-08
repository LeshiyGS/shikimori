package org.shikimori.library.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.NewsUserAdapter;
import org.shikimori.library.adapters.TopicsAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemTopicsShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class TopicsFragment extends BaseListViewFragment{

    String section = "all";

    public static TopicsFragment newInstance() {
        return new TopicsFragment();
    }

    @Override
    public int getActionBarTitle() {
        return R.string.news;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showRefreshLoader();
        loadData();
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        ContentValues cv = new ContentValues();
        cv.put("section", section);

        query.invalidateCache(ShikiApi.getUrl(ShikiPath.TOPICS), cv);
        loadData();
    }

    // TODO create loader list
    public void loadData() {
        if (query == null)
            return;

        query.init(ShikiApi.getUrl(ShikiPath.TOPICS), StatusResult.TYPE.ARRAY)
                .addParam("section", section)
                .addParam("limit", LIMIT)
                .addParam("page", page)
//                .addParam("desc", "1")
                .setCache(true, Query.HALFHOUR)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        ObjectBuilder<ItemTopicsShiki> builder = new ObjectBuilder<>(res.getResultArray(), ItemTopicsShiki.class);
        prepareData(builder.list, true, true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        ItemTopicsShiki item = (ItemTopicsShiki) parent.getAdapter().getItem(position);
        Intent intent = new Intent(activity, ShowPageActivity.class);
        String type = item.linkedType == null? item.type : item.linkedType;
        switch (type.toLowerCase()){
            case Constants.ANIME:
                intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.ANIME_PAGE);
                break;
            case Constants.MANGA:
                intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.MANGA_PAGE);
                break;
        }
        intent.putExtra(Constants.ITEM_ID, item.linkedId);
        activity.startActivity(intent);
    }

    @Override
    public ArrayAdapter<ItemTopicsShiki> getAdapter(List<?> list) {
        return new TopicsAdapter(activity, list);
    }

    @Override
    protected Menu getActionBarMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.topics_menu, menu);
        return menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        section = null;

        if(id == R.id.all)         section = "all";
        else if(id == R.id.news)   section = "news";
        else if(id == R.id.anime)  section = "a";
        else if(id == R.id.manga)  section = "m";
        else if(id == R.id.characters) section = "c";
        else if(id == R.id.site)   section = "s";
        else if(id == R.id.offtop) section = "o";
        else if(id == R.id.group)  section = "g";
        else if(id == R.id.reviews)section = "reviews";
        else if(id == R.id.polls)  section = "v";

        if(section!=null){
            page = DEFAULT_FIRST_PAGE;
            showRefreshLoader();
            loadData();

            return true;
        } else
            section = "all";

        return super.onOptionsItemSelected(item);
    }
}
