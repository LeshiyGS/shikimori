package org.shikimori.library.features.calendar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import org.shikimori.library.R;
import org.shikimori.library.features.calendar.adapter.CalendarRecycleAdapter;
import org.shikimori.library.fragments.base.abstracts.recycleview.BaseRecycleViewGridFragment;
import org.shikimori.library.fragments.base.abstracts.recycleview.ListRecycleAdapter;
import org.shikimori.library.fragments.base.abstracts.recycleview.OnItemClickRecycleListener;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.objects.one.ItemCaclendarShiki;
import org.shikimori.library.tool.hs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;

/**
 * Created by Владимир on 27.03.2015.
 */
public class CalendarFragment extends BaseRecycleViewGridFragment
        implements QueryShiki.OnQuerySuccessListener<ShikiStatusResult>, OnItemClickRecycleListener<ItemCaclendarShiki> {

//    private PinnedSectionGridView gvList;
//    private SimpleSectionedGridAdapter simpleSectionedGridAdapter;
    ObjectBuilder builder = new ObjectBuilder();
    public static CalendarFragment newInstance() {
        return new CalendarFragment();
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
        initColumn();
        loadData();
    }

    @Override
    public ListRecycleAdapter getAdapter(List<?> list) {
        return new CalendarRecycleAdapter(activity, (List<ItemCaclendarShiki>) list, this);
    }

    @Override
    public void loadData() {
        getFC().getQuery().init(ShikiApi.getUrl(ShikiPath.CALENDAR), ShikiStatusResult.TYPE.ARRAY)
             .setCache(true, QueryShiki.DAY)
             .getResult(this);
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        getFC().getQuery().invalidateCache(ShikiApi.getUrl(ShikiPath.CALENDAR));
        loadData();
    }

    @Override
    public void onQuerySuccess(ShikiStatusResult res) {
        if(activity == null)
            return;
        List<ItemCaclendarShiki> list = builder.getDataList(res.getResultArray(), ItemCaclendarShiki.class,
                new ObjectBuilder.AdvanceChecker<ItemCaclendarShiki>() {
            @Override
            public boolean check(ItemCaclendarShiki item, int position) {
                Date date = hs.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.nextEpisodeAt);
                item.order = date.getTime();
                item.day = hs.getStringDate("EE", date);
                return false;
            }
        });

        prepareData(list);
        stopRefresh();
    }

    private void prepareData(List<ItemCaclendarShiki> list) {
        CopyOnWriteArrayList<String> headers = new CopyOnWriteArrayList<>();
        List<Section> sections = new ArrayList<>();
        // Сортировка списка по дате
        Collections.sort(list, new Comparator<ItemCaclendarShiki>() {
            public int compare(ItemCaclendarShiki emp1, ItemCaclendarShiki emp2) {
                return emp1.order.compareTo(emp2.order);
            }
        });
        // Строим список с загаловками
        for (int i = 0; i < list.size(); i++) {
            ItemCaclendarShiki itemCaclendarShiki = list.get(i);
            String date = formatDate(itemCaclendarShiki.order, "EEEE\ndd MMMM\nyyyy");

            if(!headers.contains(date)){
                sections.add(new Section(i== 0? i : i + sections.size(), date));
                headers.add(date);
            }
        }
        headers.clear();
        headers = null;

        for (Section section : sections) {
            ItemCaclendarShiki item = new ItemCaclendarShiki();
            item.isDayHeader = true;
            item.name = section.getTitle().toString();
            list.add(section.position, item);
        }

        sections = null;


        prepareData(list, false, false);
        hasMoreItems(false);
        // создаем адаптер
//        CalendarAdapter adapter = new CalendarAdapter(activity, list);
//        simpleSectionedGridAdapter = new SimpleSectionedGridAdapter(activity, adapter,
//                R.layout.item_shiki_calendar_header, R.id.header_layout, R.id.header);
//        simpleSectionedGridAdapter.setGridView(gvList);
//        simpleSectionedGridAdapter.setSections(sections.toArray(new SimpleSectionedGridAdapter.Section[0]));
//        gvList.setAdapter(simpleSectionedGridAdapter);

    }

    @Override
    public void onItemClick(ItemCaclendarShiki item, int posotion) {

    }

    public static class Section {
        int position;
        CharSequence title;

        public Section(int position, CharSequence title) {
            this.position = position;
            this.title = title;
        }

        public CharSequence getTitle() {
            return title;
        }
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(hs.getOrientation(activity) == hs.ORIENTATION.LAND)
            ((GridLayoutManager)getRecyclerView().getLayoutManager()).setSpanCount(3);
        else
            ((GridLayoutManager)getRecyclerView().getLayoutManager()).setSpanCount(2);

//        gvList.invalidate();
//        if(simpleSectionedGridAdapter!=null)
//            simpleSectionedGridAdapter.onConfigurationChange(activity);
    }

    void initColumn(){
        if(hs.getOrientation(activity) == hs.ORIENTATION.LAND)
            ((GridLayoutManager)getRecyclerView().getLayoutManager()).setSpanCount(3);
        else
            ((GridLayoutManager)getRecyclerView().getLayoutManager()).setSpanCount(2);
    }
}
