package org.shikimori.library.features.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.R;
import org.shikimori.library.adapters.UserHistoryListAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.ItemUserHistory;
import org.shikimori.library.tool.LinkHelper;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.List;

import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class UserHistoryFragment extends BaseListViewFragment {

    ObjectBuilder builder = new ObjectBuilder();

    public static UserHistoryFragment newInstance() {
        return new UserHistoryFragment();
    }

    public static UserHistoryFragment newInstance(String userId) {
        Bundle b = new Bundle();
        b.putString(Constants.USER_ID, userId);
        UserHistoryFragment frag = new UserHistoryFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getActionBarTitle() {
        return R.string.history;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showRefreshLoader();
        loadData();
    }

    protected String url() {
        return ShikiApi.getUrl(ShikiPath.HISTORY, getFC().getUserId());
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        getFC().getQuery().invalidateCache(url());
        loadData();
    }

    // TODO create loader list
    public void loadData() {
        if (getFC().getQuery() == null)
            return;

        getFC().getQuery().init(url(), StatusResult.TYPE.ARRAY)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .addParam("desc", "1")
                .setCache(true, Query.FIVE_MIN)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        prepareData(builder.getDataList(res.getResultArray(), ItemUserHistory.class), true, true);
    }

    @Override
    public ArrayAdapter<ItemUserHistory> getAdapter(List list) {
        return new UserHistoryListAdapter(activity, list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        Adapter adp = parent.getAdapter();
        if (adp == null)
            return;

        ItemUserHistory item = (ItemUserHistory) adp.getItem(position);
        if (item.target != null)
            LinkHelper.goToUrl(activity, item.target.url, ProjectTool.getBodyBuilder(activity, BodyBuild.CLICKABLETYPE.NOT));
    }

}
