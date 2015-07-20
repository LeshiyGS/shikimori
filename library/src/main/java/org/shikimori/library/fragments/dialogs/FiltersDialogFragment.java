package org.shikimori.library.fragments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.RequestParams;

import org.shikimori.library.R;
import org.shikimori.library.custom.CustomCheckBoxFilter;
import org.shikimori.library.tool.h;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 14.07.2015.
 */
public class FiltersDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    ViewGroup llContainer;
    private View bFilter;
    private FilterController controller;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_filters, null);
        llContainer = h.get(v, R.id.llContainer);
        bFilter = h.get(v, R.id.bFilter);

        bFilter.setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controller = new FilterController(getActivity());
        // status
        addViewList(R.string.status_title, controller.getStatusList());
        // status
        addViewList(R.string.type, controller.getTypeList());
        // my list
        addViewList(R.string.list, controller.getMyList());
        // episodes
        addViewList(R.string.episod_title, controller.getDurationList());
        // rating
        addViewList(R.string.title_rating, controller.getRateList());

    }

    void addViewList(int title, List<CustomCheckBoxFilter.Box> list){
        CustomCheckBoxFilter box = new CustomCheckBoxFilter(getActivity());
        box.setTitle(title);
        box.setList(list);
        llContainer.addView(box);
    }

    public FilterController getController() {
        return controller;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bFilter){

        }
    }

    public static FiltersDialogFragment newInstance() {
        return new FiltersDialogFragment();
    }

    public static class FilterController{

        List<CustomCheckBoxFilter.Box> statusList, myList, durationList,typeList,rateList;
        private Context context;
        public FilterController(Context context){
            this.context = context;
        }

        String getString(int title){
            return context.getString(title);
        }

        public List<CustomCheckBoxFilter.Box> getStatusList() {
            if(statusList == null){
                statusList = new ArrayList<>();
                statusList.add(new CustomCheckBoxFilter.Box(getString(R.string.annoning), "status", "anons"));
                statusList.add(new CustomCheckBoxFilter.Box(getString(R.string.now_going), "status", "ongoing"));
                statusList.add(new CustomCheckBoxFilter.Box(getString(R.string.done), "status", "released"));
                statusList.add(new CustomCheckBoxFilter.Box(getString(R.string.just_done), "status", "latest"));
            }
            return statusList;
        }
        public List<CustomCheckBoxFilter.Box> getTypeList() {
            if(typeList == null){
                typeList = new ArrayList<>();
                typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.tv), "type", "tv"));
                typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.tv_short), "type", "tv_13"));
                typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.tv_middle), "type", "tv_24"));
                typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.tv_long), "type", "tv_48"));
                typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.movies), "type", "movie"));
                typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.ova), "type", "ova"));
                typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.ona), "type", "ona"));
                typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.special), "type", "special"));
                typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.music), "type", "music"));
            }
            return typeList;
        }
        public List<CustomCheckBoxFilter.Box> getMyList() {
            if(myList == null){
                myList = new ArrayList<>();
                myList.add(new CustomCheckBoxFilter.Box(getString(R.string.planned), "mylist", "0"));
                myList.add(new CustomCheckBoxFilter.Box(getString(R.string.watching), "mylist", "1"));
                myList.add(new CustomCheckBoxFilter.Box(getString(R.string.completed), "mylist", "2"));
                myList.add(new CustomCheckBoxFilter.Box(getString(R.string.rewatching), "mylist", "9"));
                myList.add(new CustomCheckBoxFilter.Box(getString(R.string.on_hold), "mylist", "3"));
                myList.add(new CustomCheckBoxFilter.Box(getString(R.string.dropped), "mylist", "4"));
            }
            return myList;
        }
        public List<CustomCheckBoxFilter.Box> getDurationList() {
            if(durationList == null){
                durationList = new ArrayList<>();
                durationList.add(new CustomCheckBoxFilter.Box(getString(R.string.minute_10), "duration", "S"));
                durationList.add(new CustomCheckBoxFilter.Box(getString(R.string.minute_30), "duration", "D"));
                durationList.add(new CustomCheckBoxFilter.Box(getString(R.string.after_30), "duration", "F"));
            }
            return durationList;
        }
        public List<CustomCheckBoxFilter.Box> getRateList() {
            if(rateList == null){
                rateList = new ArrayList<>();
                rateList.add(new CustomCheckBoxFilter.Box(getString(R.string.rating_g), "rating", "g"));
                rateList.add(new CustomCheckBoxFilter.Box(getString(R.string.rating_pg), "rating", "pg"));
                rateList.add(new CustomCheckBoxFilter.Box(getString(R.string.rating_pg_13), "rating", "pg_13"));
                rateList.add(new CustomCheckBoxFilter.Box(getString(R.string.rating_r), "rating", "r"));
                rateList.add(new CustomCheckBoxFilter.Box(getString(R.string.rating_r_plus), "rating", "r_plus"));
                rateList.add(new CustomCheckBoxFilter.Box(getString(R.string.rating_rx), "rating", "rx"));
            }
            return rateList;
        }

        ///api/animes?duration=F&genre=3&limit=1&mylist=1&order=ranked
        // &page=1&rating=NC-17&search=Te&season=2014&studio=2&type=TV

        public RequestParams getRequestParams(){
            RequestParams params = new RequestParams();
            prepareParam(statusList, "status", params);
            prepareParam(myList, "mylist", params);
            prepareParam(durationList, "duration", params);
            prepareParam(rateList, "rating", params);
            prepareParam(typeList, "type", params);
            return params;
        }

        private void prepareParam(List<CustomCheckBoxFilter.Box> list, String keyParsm, RequestParams params){
            if(list == null)
                return;
            StringBuilder builder = new StringBuilder();
            for (CustomCheckBoxFilter.Box box : list) {
                if(box.getStatus() == 1){
                    if(builder.length() > 0)
                        builder.append(",");
                    builder.append(box.getValue());
                } else if(box.getStatus() == 2){
                    if(builder.length() > 0)
                        builder.append(",");
                    builder.append("!").append(box.getValue());
                }
            }
            if(builder.length() > 0)
                params.put(keyParsm, builder.toString());
        }

    }

}
