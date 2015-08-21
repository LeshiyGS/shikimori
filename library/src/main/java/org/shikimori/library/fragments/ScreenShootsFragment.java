package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.shikimori.library.R;
import org.shikimori.library.adapters.ScreenShotAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemScreenShot;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ShikiAC;

import java.util.List;

import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;
import ru.altarix.basekit.library.tools.pagecontroller.Page;

/**
 * Created by Владимир on 06.08.2015.
 */
@Page(key1 = Constants.ITEM_ID)
public class ScreenShootsFragment extends BaseGridViewFragment {

    ObjectBuilder builder = new ObjectBuilder();
    String customUrl;

    @Override
    protected boolean isOptionsMenu() {
        return false;
    }

    @Override
    public int getActionBarTitle() {
        return R.string.screens;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        customUrl = getParam(Constants.CUSTOM_URL);
        showRefreshLoader();
        loadData();
    }

    String getUrl() {
        if (!TextUtils.isEmpty(customUrl))
            return customUrl;
        return ShikiApi.getUrl(ShikiPath.SCREENSHOTS, (String) getParam(Constants.ITEM_ID));
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        getFC().getQuery().invalidateCache(getUrl());
    }

    @Override
    public void loadData() {
        getFC().getQuery().init(getUrl())
                .setCache(true, Query.HOUR * 24)
                .getResultArray(this);
    }

    @Override
    public BaseAdapter getAdapter(List<?> list) {
        return new ScreenShotAdapter(activity, (List<ItemScreenShot>) list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        ItemScreenShot item = (ItemScreenShot) parent.getAdapter().getItem(position);
        if (item.getOriginal() != null)
            activity.getAC().getThumbToImage()
                    .zoom((ImageView) view.findViewById(R.id.ivBigImage), ProjectTool.fixUrl(item.getOriginal()));
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        super.onQuerySuccess(res);
        prepareData(builder.getDataList(res.getResultArray(), ItemScreenShot.class), false, false);
    }
}
