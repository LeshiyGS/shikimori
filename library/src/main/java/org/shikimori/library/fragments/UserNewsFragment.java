package org.shikimori.library.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;

import org.json.JSONArray;
import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.NewsUserAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemTopicsShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.actionmode.ActionDescription;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ReadMessageController;
import org.shikimori.library.tool.parser.jsop.BodyBuild;
import org.shikimori.library.tool.popup.TextPopup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class UserNewsFragment extends BaseListViewFragment implements BaseActivity.OnFragmentBackListener, AdapterView.OnItemLongClickListener {

    private String type;
    private int title;
    private TextPopup popup;
    private BodyBuild bodyBuild;

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
            case Constants.INBOX:
                title = R.string.inbox;
                break;
            case Constants.NEWS:
                title = R.string.news;
                break;
            case Constants.NOTIFYING:
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
        ReadMessageController.newInstance(query);
        activity.setOnFragmentBackListener(this);
        bodyBuild = new BodyBuild(activity);
        if(type.equals(Constants.INBOX))
            getListView().setOnItemLongClickListener(this);
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
        loadAsyncBuild(bodyBuild, res.getResultArray(), 300, ItemNewsUserShiki.class);
    }

    @Override
    public ArrayAdapter<ItemNewsUserShiki> getAdapter(List list) {
        NewsUserAdapter adptr = new NewsUserAdapter(activity, query, list);
        adptr.setType(type);
        return adptr;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        Adapter adp = parent.getAdapter();
        if(adp == null)
            return;

        ItemNewsUserShiki item = (ItemNewsUserShiki) adp.getItem(position);
        if(type.equals(Constants.INBOX)){
            activity.loadPage(InboxFragment.newInstance(), true, false);
        } else if (type.equals(Constants.NEWS)){
            Intent intent = ProjectTool.getSimpleIntentDetails(activity, item.linked.type);
            if(intent!=null){
                intent.putExtra(Constants.ITEM_ID, item.linked.id);
                activity.startActivity(intent);
                return;
            }

            if(item.kind.toLowerCase().equals(Constants.SITENEWS)){
                intent = new Intent(activity, ShowPageActivity.class);
                intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.OFTOPIC_PAGE);
                intent.putExtra(Constants.TREAD_ID, item.linked.id);
                activity.startActivity(intent);

                return;
            }
        }
    }

    void showPopupText(String html){
        popup = new TextPopup(activity);
        popup.showLoader();
//        bodyBuild.parce(item.doc, popup.getBody());
        bodyBuild.parceAsync(html, new BodyBuild.ParceDoneListener() {
            @Override
            public void done(ViewGroup view) {
                popup.hideLoader();
                popup.setBody(view);
            }
        });
        popup.show();
    }

    /**
     * Скрываем попап если он открыт
     * @return
     */
    @Override
    public boolean onBackPressed() {
        if(popup!=null && popup.hide()){
            return true;
        }
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ActionDescription confirmDeleteAction = new ActionDescription() {

            @Override
            public void act(int[] selectedItems) {
                List<Object> lists = getAllList();
                CopyOnWriteArrayList<String> ids = new CopyOnWriteArrayList<>();
                for (int i = 0; i < selectedItems.length; i++) {
                    ItemNewsUserShiki obj = (ItemNewsUserShiki) lists.get(selectedItems[i]);
                    ids.add(obj.id);
                }
                if(ids.size() > 0){
                    String strIds = TextUtils.join(",", ids);
                    // TODO delete collections messages
                }
            }
        };
        showDeleteFromListInterface(confirmDeleteAction);
        return true;
    }
}
