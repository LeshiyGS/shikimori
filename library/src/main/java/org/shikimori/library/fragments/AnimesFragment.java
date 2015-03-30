package org.shikimori.library.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.adapters.AnimesAdapter;
import org.shikimori.library.adapters.CalendarAdapter;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemAnimesShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.h;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter;
import dev.dworks.libs.astickyheader.ui.PinnedSectionGridView;

/**
 * Created by Владимир on 27.03.2015.
 */
public class AnimesFragment extends PullableFragment<BaseActivity> implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    private PinnedSectionGridView gvList;
    private SimpleSectionedGridAdapter simpleSectionedGridAdapter;
    private String search="";

    public static AnimesFragment newInstance() {
        return new AnimesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_animes, null);
        gvList = (PinnedSectionGridView) v.findViewById(R.id.gvList);
        gvList.setOnItemClickListener(this);
        return v;
    }

    @Override
    public int pullableViewId() {
        return R.id.gvList;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity.getSupportActionBar().setTitle(R.string.app_name);
        activity.getSupportActionBar().setSubtitle(R.string.anime);

        showRefreshLoader();
        loadAnimes();
    }

    @Override
    public void onStartRefresh() {
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.ANIMES));
        loadAnimes();
    }

    private void loadAnimes() {
        query.init(ShikiApi.getUrl(ShikiPath.ANIMES), StatusResult.TYPE.ARRAY)
                .addParam("limit", "20")
                .addParam("page", "1")
                .addParam("search", search)
                .setCache(true, Query.DAY)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        ObjectBuilder builder = new ObjectBuilder(res.getResultArray(), ItemAnimesShiki.class);
        prepareData(builder.list);
        stopRefresh();
    }

    private void prepareData(List<ItemAnimesShiki> list) {
        CopyOnWriteArrayList<String> headers = new CopyOnWriteArrayList<>();
        ArrayList<Object> sections = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ItemAnimesShiki itemAnimesShiki = list.get(i);
        }

        AnimesAdapter adapter = new AnimesAdapter(activity, list);
        simpleSectionedGridAdapter = new SimpleSectionedGridAdapter(activity, adapter,
                R.layout.item_shiki_calendar_header, R.id.header_layout, R.id.header);
        simpleSectionedGridAdapter.setGridView(gvList);
        simpleSectionedGridAdapter.setSections(sections.toArray(new SimpleSectionedGridAdapter.Section[0]));
        gvList.setAdapter(simpleSectionedGridAdapter);

    }


    String formatDate(String date, String format) {
        Date _date = h.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", date);
        return h.getStringDate(format, _date);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        h.showMsg(activity, "click");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        gvList.invalidate();
        if(simpleSectionedGridAdapter!=null)
            simpleSectionedGridAdapter.onConfigurationChange(activity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_animes_filter){

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.animes_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.anime_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                showRefreshLoader();
                loadAnimes();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
