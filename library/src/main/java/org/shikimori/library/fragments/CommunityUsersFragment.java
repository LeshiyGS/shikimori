package org.shikimori.library.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.UserCardStyleAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseGridViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemUserListShiki;
import org.shikimori.library.objects.one.ItemClubShiki;
import org.shikimori.library.objects.one.ItemUser;
import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;

import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class CommunityUsersFragment extends BaseGridViewFragment implements Query.OnQuerySuccessListener, AdapterView.OnItemClickListener {

    private static String FRIENDLIST = "FRIENDLIST";
    ObjectBuilder builder = new ObjectBuilder();
    boolean friendList;

    public static CommunityUsersFragment newInstance() {
        return new CommunityUsersFragment();
    }

    public static CommunityUsersFragment newInstance(boolean friendList) {
        return newInstance(friendList, null);
    }
    public static CommunityUsersFragment newInstance(boolean friendList, String userId) {
        Bundle b = new Bundle();
        b.putBoolean(FRIENDLIST, friendList);
        b.putInt(Constants.ACTION_BAR_TITLE, R.string.friends);
        if(userId!=null)
            b.putString(Constants.USER_ID, userId);

        CommunityUsersFragment frag = new CommunityUsersFragment();
        frag.setArguments(b);
        return frag;
    }

//    @Override
//    public int getActionBarTitle() {
//        return R.string.community;
//    }

    protected String getLoadPath() {
        if(friendList)
            return ShikiApi.getUrl(ShikiPath.FRIENDS, getFC().getUserId());
        return ShikiApi.getUrl(ShikiPath.GET_USERS_LIST);
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
        friendList = getParamBoolean(FRIENDLIST);
        StartFirstLoad();
    }

    @Override
    public void loadData() {
        getFC().getQuery().init(getLoadPath(), StatusResult.TYPE.ARRAY)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .addParam("search", getSearchText())
                .setCache(true, Query.FIVE_MIN)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        super.onQuerySuccess(res);
        List<ItemUser> list = builder.getDataList(res.getResultArray(), ItemUser.class);
        prepareData(list, true, true);
    }

    @Override
    public ArrayAdapter<ItemUser> getAdapter(List<?> list) {
        return new UserCardStyleAdapter(activity, (List<ItemUser>) list);
    }

    @Override
    protected Menu getActionBarMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.community_menu, menu);
        inflateSearch(menu);
        return menu;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        Adapter adp = parent.getAdapter();

        if(position < 0 || position >= adp.getCount())
            return;

        ItemUser item = (ItemUser)adp.getItem(position);
        ProjectTool.goToUser(activity, item.id);
    }
}
