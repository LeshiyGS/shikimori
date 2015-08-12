package org.shikimori.library.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.ClubCardStyleAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;
import org.shikimori.library.objects.one.ItemClubShiki;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class UserClubsFragment extends BaseGridViewFragment implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    private String userId;
    ObjectBuilder builder = new ObjectBuilder();
    public static UserClubsFragment newInstance() {
        return new UserClubsFragment();
    }

    public static UserClubsFragment newInstance(Bundle b) {
        UserClubsFragment frag = new UserClubsFragment();
        frag.setArguments(b);
        return frag;
    }

    private void initArgiments() {
        userId = getParam(Constants.USER_ID);
    }

    @Override
    protected boolean isOptionsMenu() {
        return false;
    }

    protected String getLoadPath() {
        return ShikiApi.getUrl(ShikiPath.GET_USER_CLUBS_LIST, userId);
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
        activity.setTitle(R.string.forums);
        initArgiments();
        StartFirstLoad();
    }

    @Override
    public void loadData() {
        getFC().getQuery().init(getLoadPath(), StatusResult.TYPE.ARRAY)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .setCache(true, Query.FIVE_MIN)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
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
