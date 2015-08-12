package org.shikimori.library.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.shikimori.library.R;
import org.shikimori.library.adapters.CalendarAdapter;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.ItemCaclendarShiki;
import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter;
import dev.dworks.libs.astickyheader.ui.PinnedSectionGridView;
import ru.altarix.basekit.library.activity.BaseKitActivity;

/**
 * Created by Владимир on 27.03.2015.
 */
public class CalendarFragment extends PullableFragment<BaseKitActivity<ShikiAC>> implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    private PinnedSectionGridView gvList;
    private SimpleSectionedGridAdapter simpleSectionedGridAdapter;
    ObjectBuilder builder = new ObjectBuilder();
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
    public int getActionBarTitle() {
        return R.string.shiki_calendar;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showRefreshLoader();
        loadCalendar();
    }

    @Override
    public void onStartRefresh() {
        getFC().getQuery().invalidateCache(ShikiApi.getUrl(ShikiPath.CALENDAR));
        loadCalendar();
    }

    private void loadCalendar() {
        getFC().getQuery().init(ShikiApi.getUrl(ShikiPath.CALENDAR), StatusResult.TYPE.ARRAY)
             .setCache(true, Query.DAY)
             .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        List<ItemCaclendarShiki> list = builder.getDataList(res.getResultArray(), ItemCaclendarShiki.class,
                new ObjectBuilder.AdvanceCheck<ItemCaclendarShiki>() {
            @Override
            public boolean check(ItemCaclendarShiki item, int position) {
                Date date = hs.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.nextEpisodeAt);
                item.order = date.getTime();
                return false;
            }
        });
        prepareData(list);
        stopRefresh();
    }

    private void prepareData(List<ItemCaclendarShiki> list) {
        CopyOnWriteArrayList<String> headers = new CopyOnWriteArrayList<>();
        ArrayList<Object> sections = new ArrayList<>();
        // Сортировка списка по дате
        Collections.sort(list, new Comparator<ItemCaclendarShiki>() {
            public int compare(ItemCaclendarShiki emp1, ItemCaclendarShiki emp2) {
                return emp1.order.compareTo(emp2.order);
            }
        });
        // Строим список с загаловками
        for (int i = 0; i < list.size(); i++) {
            ItemCaclendarShiki itemCaclendarShiki = list.get(i);
            String date = formatDate(itemCaclendarShiki.order, "EEEE - dd MMMM yyyy");

            if(!headers.contains(date)){
                sections.add(new SimpleSectionedGridAdapter.Section(i, date));
                headers.add(date);
            }
        }
        // создаем адаптер
        CalendarAdapter adapter = new CalendarAdapter(activity, list);
        simpleSectionedGridAdapter = new SimpleSectionedGridAdapter(activity, adapter,
                R.layout.item_shiki_calendar_header, R.id.header_layout, R.id.header);
        simpleSectionedGridAdapter.setGridView(gvList);
        simpleSectionedGridAdapter.setSections(sections.toArray(new SimpleSectionedGridAdapter.Section[0]));
        gvList.setAdapter(simpleSectionedGridAdapter);

    }

//
//    String formatDate(String date, String format) {
//        Date _date = h.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", date);
//        return h.getStringDate(format, _date);
//    }

    String formatDate(long date, String format) {
        return hs.getStringDate(format, new Date(date));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        gvList.invalidate();
        if(simpleSectionedGridAdapter!=null)
            simpleSectionedGridAdapter.onConfigurationChange(activity);
    }
}
