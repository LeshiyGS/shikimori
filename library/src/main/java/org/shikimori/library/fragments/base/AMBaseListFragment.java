package org.shikimori.library.fragments.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.R;
import org.shikimori.library.adapters.AMAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.AMShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;

import java.util.List;

/**
 * Created by Владимир on 27.03.2015.
 */
public abstract class AMBaseListFragment extends BaseGridViewFragment implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

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

    protected abstract String getLoadPath();

    @Override
    public void loadData() {
        query.init(ShikiApi.getUrl(getLoadPath()), StatusResult.TYPE.ARRAY)
            .addParam("limit", LIMIT)
            .addParam("page", page)
            .addParam("search", getSearchText())
            .setCache(true, Query.DAY)
            .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        super.onQuerySuccess(res);
        ObjectBuilder builder = new ObjectBuilder(res.getResultArray(), AMShiki.class);
        prepareData(builder.list, true, true);
    }

    @Override
    public ArrayAdapter<AMShiki> getAdapter(List<?> list) {
        return new AMAdapter(activity, (List<AMShiki>) list);
    }

}
