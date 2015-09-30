package org.shikimori.library.fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.nineoldandroids.animation.Animator;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.NewsUserAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.BaseQuery;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.ItemDialogs;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.InvalidateTool;
import org.shikimori.library.tool.LinkHelper;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import ru.altarix.basekit.library.actionmode.ActionDescription;

import org.shikimori.library.tool.baselisteners.BaseAnimationListener;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ReadMessageController;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.parser.jsop.BodyBuild;
import org.shikimori.library.tool.pmc.PopupMenuCompat;
import org.shikimori.library.tool.popup.TextPopup;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.basekit.library.tools.DialogCompat;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class UserNewsFragment extends BaseListViewFragment implements BaseKitActivity.OnFragmentBackListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

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
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams();
    }

    @Override
    protected Menu getActionBarMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.read_all_menu, menu);
        return menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.icReadAll){
            new DialogCompat(activity)
                .setNegativeListener(null)
                .setPositiveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getFC().getQuery().in(ShikiPath.READ_ALL)
                               .setMethod(BaseQuery.METHOD.POST)
                               .addParam("profile_id", ShikiUser.USER_ID)
                               .addParam("type", type)
                               .getResult(new BaseQuery.OnQuerySuccessListener() {
                                   @Override
                                   public void onQuerySuccess(StatusResult res) {
                                       invalidate();
                                   }
                               });

                        readAll();
                    }
                }).showConfirm(activity.getString(R.string.read_all) + "?");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void readAll() {
        List<Object> list = getAllList();
        for (int i = 0; i < list.size(); i++) {
            ItemNewsUserShiki item = (ItemNewsUserShiki) list.get(i);
            item.read = true;
        }
        if(getAdapter() != null)
            getAdapter().notifyDataSetChanged();
    }

    private void initParams() {
        Bundle b = getArguments();
        type = b.getString(Constants.TYPE);
        switch (type) {
//            case Constants.INBOX:
//                title = R.string.inbox;
//                break;
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
        ReadMessageController.newInstance(getFC().getQuery());
        activity.setOnFragmentBackListener(this);
        bodyBuild = new BodyBuild(activity);
        if (type.equals(Constants.INBOX))
            getListView().setOnItemLongClickListener(this);
        showRefreshLoader();
        loadData();
    }

    protected String url() {
        return ShikiApi.getUrl(ShikiPath.MESSAGES, ShikiUser.USER_ID);
    }

    private void invalidate(){
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        getFC().getQuery().invalidateCache(url(), cv);
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        invalidate();
        loadData();
    }

    // TODO create loader list
    public void loadData() {
        if (getFC().getQuery() == null)
            return;

        getFC().getQuery().init(url(), StatusResult.TYPE.ARRAY)
                .addParam("type", type)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .addParam("desc", "1")
                .setCache(true, Query.FIVE_MIN)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        loadAsyncBuild(bodyBuild, res.getResultArray(), 600, ItemNewsUserShiki.class);
    }

    @Override
    public ArrayAdapter<ItemNewsUserShiki> getAdapter(List list) {
        NewsUserAdapter adptr = new NewsUserAdapter(activity, getAdapterLayout(),getFC().getQuery(), list);
        adptr.setType(type);
        adptr.setOnSettingsListener(this);
        return adptr;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

        Adapter adp = parent.getAdapter();
        if (adp == null)
            return;

        ItemNewsUserShiki item = (ItemNewsUserShiki) adp.getItem(position);
        if (type.equals(Constants.INBOX)) {
            activity.loadPage(InboxFragment2.newInstance());
        } else if (type.equals(Constants.NEWS)) {
            Intent intent = ProjectTool.getSimpleIntentDetails(activity, item.linked.type);
            if (intent != null) {
                intent.putExtra(Constants.ITEM_ID, item.linked.id);
                activity.startActivity(intent);
                return;
            }

            if (item.kind.toLowerCase().equals(Constants.SITENEWS)) {
                intent = new Intent(activity, ShowPageActivity.class);
                intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.OFTOPIC_PAGE);
                intent.putExtra(Constants.TREAD_ID, item.linked.id);
                activity.startActivity(intent);

                return;
            }
        } else if (type.equals(Constants.NOTIFYING)) {
            if(item.kind.equalsIgnoreCase(Constants.GROUP_REQUEST)){
                LinkHelper.goToUrl(activity, item.htmlBody);
                return;
            } else if(item.kind.equalsIgnoreCase(Constants.FRIEND_REQUEST)){
                ProjectTool.goToUser(activity, item.from.id);
                return;
            }
            if(item.linked == null || item.linked.id == null)
                return;
            View btn = view.findViewById(R.id.llActions);
            hs.setGoneToggle(btn);

        }
    }

    void showPopupText(String html) {
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
     *
     * @return
     */
    @Override
    public boolean onBackPressed() {
        if (popup != null && popup.hide()) {
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
                if (ids.size() > 0) {
                    String strIds = TextUtils.join(",", ids);
                    // TODO delete collections messages
                }
            }
        };
        showDeleteFromListInterface(confirmDeleteAction);
        return true;
    }

    public int getAdapterLayout() {
        if(type.equals(Constants.NEWS))
            return R.layout.item_shiki_message_list_news;
        return R.layout.item_shiki_message_list;
    }

    @Override
    public void onClick(final View v) {
        if(v.getId() == R.id.icSettings){
            PopupMenuCompat popup = PopupMenuCompat.newInstance(activity, v);
            popup.inflate(R.menu.discus_menu);
            popup.getMenu().removeItem(R.id.icAnswer);
            popup.getMenu().removeItem(R.id.icUpdate);
            popup.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.icDelete) {
                        deleteDialod((int) v.getTag(), (View) v.getParent().getParent());
                        return true;
                    }
                    return false;
                }
            });
            popup.show();
        }
    }

    private void deleteDialod(final int position, final View parent) {
        ItemNewsUserShiki item = (ItemNewsUserShiki) getAllList().get(position);
        ProjectTool.deleteItem(activity, ShikiApi.getUrl(ShikiPath.MESSAGESPRIVATE_ID, item.id),
                parent, new BaseAnimationListener(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        UserNewsFragment.this.removeItem(position);
                        invalidate();
                    }
                });
    }

}
