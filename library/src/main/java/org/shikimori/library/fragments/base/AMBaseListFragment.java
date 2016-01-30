package org.shikimori.library.fragments.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.loopj.android.http.RequestParams;

import org.shikimori.library.adapters.AMAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.objects.one.AMShiki;
import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;

import java.util.List;

/**
 * Created by Владимир on 27.03.2015.
 */
public abstract class AMBaseListFragment extends BaseGridViewFragment implements QueryShiki.OnQuerySuccessListener<ShikiStatusResult>, AdapterView.OnItemClickListener {

    ObjectBuilder builder = new ObjectBuilder();

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        getFC().getQuery().invalidateCache(getLoadPath());
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
        getFC().getQuery().init(getLoadPath(), ShikiStatusResult.TYPE.ARRAY);

        RequestParams filter = getFilterParams();
        if(filter!=null)
            getFC().getQuery().setParams(filter);

        getFC().getQuery().addParam("limit", LIMIT)
            .addParam("page", page)
            .addParam("search", getSearchText())
            .setCache(true, QueryShiki.DAY)
            .getResult(this);
    }

    public RequestParams getFilterParams(){
        return null;
    }

    @Override
    public void onQuerySuccess(ShikiStatusResult res) {
        if(activity == null)
            return;
        super.onQuerySuccess(res);
        getFC().hideLoader();
        prepareData(builder.getDataList(res.getResultArray(), AMShiki.class), true, true);
    }

    @Override
    public ArrayAdapter<AMShiki> getAdapter(List<?> list) {
        return new AMAdapter(activity, (List<AMShiki>) list);
    }

}
