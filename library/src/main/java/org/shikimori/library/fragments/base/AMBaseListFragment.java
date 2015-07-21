package org.shikimori.library.fragments.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.loopj.android.http.RequestParams;

import org.shikimori.library.adapters.AMAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;

import java.util.List;

/**
 * Created by Владимир on 27.03.2015.
 */
public abstract class AMBaseListFragment extends BaseGridViewFragment implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        query.invalidateCache(getLoadPath());
        loadData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        StartFirstLoad();
    }

    protected abstract String getLoadPath();

    @Override
    public void loadData() {
        query.init(getLoadPath(), StatusResult.TYPE.ARRAY);

        RequestParams filter = getFilterParams();
        if(filter!=null)
            query.setParams(filter);

        query.addParam("limit", LIMIT)
            .addParam("page", page)
            .addParam("search", getSearchText())
            .setCache(true, Query.DAY)
            .getResult(this);
    }

    public RequestParams getFilterParams(){
        return null;
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        super.onQuerySuccess(res);
        if(query!=null)
            query.getLoader().hide();
        ObjectBuilder builder = new ObjectBuilder(res.getResultArray(), AMShiki.class);
        prepareData(builder.list, true, true);
    }

    @Override
    public ArrayAdapter<AMShiki> getAdapter(List<?> list) {
        return new AMAdapter(activity, (List<AMShiki>) list);
    }

}
