package org.shikimori.library.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;

import org.shikimori.library.R;
import org.shikimori.library.adapters.NewsUserAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class UserNewsFragment extends BaseListViewFragment {

    private String type;
    private int title;

    public static UserNewsFragment newInstance(String type) {
        Bundle b = new Bundle();
        b.putString(Constants.TYPE, type);

        UserNewsFragment frag = new UserNewsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getActionBarTitle() {
        return title;
    }

    @Override
    protected boolean isOptionsMenu() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams();
    }

    private void initParams() {
        Bundle b = getArguments();
        type = b.getString(Constants.TYPE);
        switch (type) {
            case "inbox":
                title = R.string.inbox;
                break;
            case "news":
                title = R.string.news;
                break;
            case "notifications":
                title = R.string.notifying;
                break;
        }
    }

    /**
     * inbox ttp://shikimori.org/messages
     * message[kind]:Private
     * message[from_id]:35934
     * message[to_id]:1
     * message[body]:[message=30068968]morr[/message], ага, работа
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showRefreshLoader();
        loadData();
    }

    protected String url() {
        return ShikiApi.getUrl(ShikiPath.MESSAGES, ShikiUser.USER_ID);
    }


    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        query.invalidateCache(url(), cv);
        loadData();
    }

    // TODO create loader list
    public void loadData() {
        if (query == null)
            return;

        query.init(url(), StatusResult.TYPE.ARRAY)
                .addParam("type", type)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .addParam("desc", "1")
                .setCache(true, Query.FIVE_MIN)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        ObjectBuilder<ItemNewsUserShiki> builder = new ObjectBuilder<>(res.getResultArray(), ItemNewsUserShiki.class);
        prepareData(builder.list, true, true);
    }

    @Override
    public ArrayAdapter<ItemNewsUserShiki> getAdapter(List list) {
        return new NewsUserAdapter(activity, list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        Adapter adp = parent.getAdapter();
        if(adp == null)
            return;


        ItemNewsUserShiki obj = (ItemNewsUserShiki) adp.getItem(position);
        // TODO start chat with user
    }
}
