package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.shikimori.library.R;
import org.shikimori.library.adapters.RelationAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.objects.one.Relation;
import org.shikimori.library.tool.LinkHelper;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;
import ru.altarix.basekit.library.tools.pagecontroller.Page;

/**
 * Created by Владимир on 27.03.2015.
 */
@Page(key1 = Constants.ITEM_ID, key2 = Constants.TYPE, key3 = Constants.CUSTOM_URL)
public class LinkedListFragment extends BaseGridViewFragment implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    protected ObjectBuilder builder = new ObjectBuilder();
    private String type,itemId,customUrl;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        type = getParam(Constants.TYPE);
        itemId = getParam(Constants.ITEM_ID);
        customUrl = getParam(Constants.CUSTOM_URL);

        loadData();
    }

    @Override
    public int getActionBarTitle() {
        return R.string.link;
    }

    public String getUrl(){
        if(!TextUtils.isEmpty(customUrl))
            return customUrl;
        return ShikiApi.getUrl(
            Constants.ANIME.equals(type) ? ShikiPath.ANIME_LINK : ShikiPath.MANGA_LINK,
            itemId
        );
    }

    @Override
    public void loadData() {
        getFC().getQuery().init(getUrl())
                .setCache(true, Query.DAY)
                .getResultArray(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {

        if (activity == null)
            return;
        JSONArray data = res.getResultArray();
        if (data == null || data.length() == 0)
            return;

        prepareData(getDataList(data), true, true);
    }

    protected List<? extends Object> getDataList(JSONArray data){
        return builder.getDataList(data, Relation.class);
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        getFC().getQuery().invalidateCache(getUrl());
        loadData();
    }

    @Override
    public ArrayAdapter<Relation> getAdapter(List<?> list) {
        return new RelationAdapter(activity, (List<Relation>) list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Relation object = (Relation) parent.getAdapter().getItem(position);
        AMShiki item = object.getAnime().id == null ? object.getManga() : object.getAnime();

        LinkHelper.goToUrl(activity, item.url);

//        ProjectTool.TYPE type = ProjectTool.getTypeFromUrl(item.url);
//
//        Intent i = new Intent(activity, ShowPageActivity.class);
//        if (type == ProjectTool.TYPE.ANIME) {
//            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.ANIME_PAGE);
//        } else if (type == ProjectTool.TYPE.MANGA)
//            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.MANGA_PAGE);
//        i.putExtra(Constants.ITEM_ID, item.id);
//        activity.startActivity(i);
    }


}
