package org.shikimori.library.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.adapters.CalendarAdapter;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemCaclendarShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.h;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter;
import dev.dworks.libs.astickyheader.ui.PinnedSectionGridView;

/**
 * Created by Владимир on 27.03.2015.
 */
public class CalendarFragment extends PullableFragment<BaseActivity> implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    private PinnedSectionGridView gvList;
    private SimpleSectionedGridAdapter simpleSectionedGridAdapter;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_calendar, null);
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
        activity.getSupportActionBar().setSubtitle(R.string.shiki_calendar);

        showRefreshLoader();
        loadCalendar();
    }

    @Override
    public void onStartRefresh() {
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.Calendar));
        loadCalendar();
    }

    private void loadCalendar() {
        query.init(ShikiApi.getUrl(ShikiPath.Calendar), StatusResult.TYPE.ARRAY)
             .setCache(true, Query.DAY)
             .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        ObjectBuilder builder = new ObjectBuilder(res.getResultArray(), ItemCaclendarShiki.class);
        prepareData(builder.list);
        stopRefresh();
    }

    private void prepareData(List<ItemCaclendarShiki> list) {
        CopyOnWriteArrayList<String> headers = new CopyOnWriteArrayList<>();
        ArrayList<Object> sections = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ItemCaclendarShiki itemCaclendarShiki = list.get(i);
            String date = formatDate(itemCaclendarShiki.nextEpisodeAt, "EEEE - dd MMMM yyyy");

            if(!headers.contains(date)){
                sections.add(new SimpleSectionedGridAdapter.Section(i, date));
                headers.add(date);
            }
        }

        CalendarAdapter adapter = new CalendarAdapter(activity, list);
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
}
