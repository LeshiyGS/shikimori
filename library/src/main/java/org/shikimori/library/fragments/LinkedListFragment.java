package org.shikimori.library.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.AMAdapter;
import org.shikimori.library.adapters.RelationAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.objects.one.Relation;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;
import ru.altarix.basekit.library.tools.pagecontroller.Page;

/**
 * Created by Владимир on 27.03.2015.
 */
@Page(key1 = Constants.ITEM_ID, key2 = Constants.TYPE)
public class LinkedListFragment extends BaseGridViewFragment implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    ObjectBuilder builder = new ObjectBuilder();
    private String type,itemId;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        type = getParam(Constants.TYPE);
        itemId = getParam(Constants.ITEM_ID);

        loadData();
    }

    @Override
    public int getActionBarTitle() {
        return R.string.link;
    }

    public String getUrl(){
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

        List<Relation> list = builder.getDataList(data, Relation.class);

        prepareData(list, true, true);
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
        super.onItemClick(parent, view, position, id);
        Relation object = (Relation) parent.getAdapter().getItem(position);
        AMShiki item = object.getAnime().id == null ? object.getManga() : object.getAnime();

        ProjectTool.TYPE type = ProjectTool.getTypeFromUrl(item.url);

        Intent i = new Intent(activity, ShowPageActivity.class);
        if (type == ProjectTool.TYPE.ANIME) {
            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.ANIME_PAGE);
        } else if (type == ProjectTool.TYPE.MANGA)
            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.MANGA_PAGE);
        i.putExtra(Constants.ITEM_ID, item.id);
        activity.startActivity(i);
    }
}
