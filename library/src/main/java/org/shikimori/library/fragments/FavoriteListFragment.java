package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.adapters.AMAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.ProjectTool;

import java.util.List;

/**
 * Created by Владимир on 27.03.2015.
 */
public class FavoriteListFragment extends BaseGridViewFragment implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    private List<AMShiki> list;

    public static FavoriteListFragment newInstance(List<AMShiki> list){

        // TODO переделать на позицию и грузить с инета страницы

        FavoriteListFragment frag = new FavoriteListFragment();
        frag.setListData(list);
        return frag;
    }

    private void setListData(List<AMShiki> list) {
        this.list = list;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPullToRefreshLayout().setEnabled(false);
        loadData();
    }

    @Override
    public void loadData() {

        prepareData(list, false,false);
        hasMoreItems(false);
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
        if(type == ProjectTool.TYPE.ANIME) {

        }else if(type == ProjectTool.TYPE.MANGA){

        }
    }
}
