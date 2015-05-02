package org.shikimori.library.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.json.JSONObject;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.AMAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

/**
 * Created by Владимир on 27.03.2015.
 */
public class FavoriteListFragment extends BaseGridViewFragment implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    private int position;

    public static FavoriteListFragment newInstance(int position){
        FavoriteListFragment frag = new FavoriteListFragment();
        Bundle b = new Bundle();
        b.putInt(Constants.LIST_ID, position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initParams();

        loadData();
    }

    private void initParams() {
        Bundle b = getArguments();
        if(b == null)
            return;

        position = b.getInt(Constants.LIST_ID);
    }

    @Override
   public void loadData() {
        query.init(ShikiApi.getUrl(ShikiPath.FAVOURITES, getUserId()))
                .setCache(true, Query.DAY)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {

        if(activity == null)
            return;
        JSONObject data = res.getResultObject();
        if(data == null)
            return;

        ObjectBuilder<AMShiki> builder = new ObjectBuilder<>(data.optJSONArray(getType()), AMShiki.class, new ObjectBuilder.AdvanceCheck<AMShiki>() {
            @Override
            public boolean check(AMShiki item, int position) {
                item.poster = item.poster.replace("x64", "preview");
                return false;
            }
        });

        prepareData(builder.getDataList(), true, true);
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.FAVOURITES, getUserId()));
        loadData();
    }

    @Override
    public ArrayAdapter<AMShiki> getAdapter(List<?> list) {
        return new AMAdapter(activity, (List<AMShiki>) list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        AMShiki item = (AMShiki) parent.getAdapter().getItem(position);
        ProjectTool.TYPE type = ProjectTool.getTypeFromUrl(item.url);

        Intent i = new Intent(activity, ShowPageActivity.class);
        if(type == ProjectTool.TYPE.ANIME) {
            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.ANIME_PAGE);
        }else if(type == ProjectTool.TYPE.MANGA){
            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.MANGA_PAGE);
        }
        i.putExtra(Constants.ITEM_ID, item.id);
        activity.startActivity(i);
    }

    public String getType() {
        String type;
        switch (position) {
            case 1: type = "mangas"; break;
            case 2: type = "characters"; break;
            case 3: type = "people"; break;
            case 4: type = "mangakas"; break;
            case 5: type = "seyu"; break;
            case 6: type = "producers"; break;
            default: type = "animes";
        }

        return type;
    }
}
