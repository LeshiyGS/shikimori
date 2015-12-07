package org.shikimori.library.features.comminity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.Query;
import org.shikimori.library.loaders.httpquery.MyStatusResult;
import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;

import org.shikimori.library.objects.one.ItemClubShiki;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class CommunityClubsFragment extends BaseGridViewFragment implements Query.OnQuerySuccessListener<MyStatusResult>, AdapterView.OnItemClickListener {

    public static CommunityClubsFragment newInstance() {
        return new CommunityClubsFragment();
    }
    ObjectBuilder builder = new ObjectBuilder();
    @Override
    protected boolean isOptionsMenu() {
        return false;
    }

//    @Override
//    public int getActionBarTitle() {
//        return R.string.community;
//    }

    protected String getLoadPath() {
        return ShikiApi.getUrl(ShikiPath.GET_CLUBS_LIST);
    }

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

    @Override
    public void loadData() {
        getFC().getQuery().init(getLoadPath(), MyStatusResult.TYPE.ARRAY)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .setCache(true, Query.FIVE_MIN)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(MyStatusResult res) {
        super.onQuerySuccess(res);
        List<ItemClubShiki> list = builder.getDataList(res.getResultArray(), ItemClubShiki.class);
        prepareData(list, true, true);
    }

    @Override
    public ArrayAdapter<ItemClubShiki> getAdapter(List<?> list) {
        return new ClubCardStyleAdapter(activity, (List<ItemClubShiki>) list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        Adapter adp = parent.getAdapter();

        if (position < 0 || position >= adp.getCount())
            return;


        ItemClubShiki item = (ItemClubShiki) adp.getItem(position);
        Intent i = new Intent(activity, ShowPageActivity.class);
        i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.CLUB_PAGE);
        i.putExtra(Constants.ACTION_BAR_TITLE, item.name);
        i.putExtra(Constants.ITEM_ID, item.id);
        activity.startActivity(i);
    }

}
