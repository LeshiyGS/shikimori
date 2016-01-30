package org.shikimori.library.fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.NewsUserAdapter;
import org.shikimori.library.features.profile.InboxFragment2;
import org.shikimori.library.fragments.base.abstracts.recycleview.BaseRecycleViewFragment;
import org.shikimori.library.fragments.base.abstracts.recycleview.ListRecycleAdapter;
import org.shikimori.library.fragments.base.abstracts.recycleview.OnItemClickRecycleListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import com.gars.querybuilder.BaseQuery;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.LinkHelper;
import org.shikimori.library.tool.LoadAsyncBuildHelper;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;

import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ReadMessageController;
import org.shikimori.library.tool.parser.jsop.BodyBuild;
import org.shikimori.library.tool.pmc.PopupMenuCompat;
import org.shikimori.library.tool.popup.TextPopup;

import java.util.List;

import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.basekit.library.tools.DialogCompat;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class UserNewsFragment extends BaseRecycleViewFragment implements BaseKitActivity.OnFragmentBackListener, View.OnClickListener, BaseQuery.OnQuerySuccessListener<ShikiStatusResult>, OnItemClickRecycleListener<ItemNewsUserShiki> {

    private String type;
    private int title;
    private TextPopup popup;
    private BodyBuild bodyBuild;
    private LoadAsyncBuildHelper lah;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.read_all_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                               .getResult(new BaseQuery.OnQuerySuccessListener<ShikiStatusResult>() {
                                   @Override
                                   public void onQuerySuccess(ShikiStatusResult res) {
                                       if(activity == null)
                                           return;
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
        lah = new LoadAsyncBuildHelper(activity, this);
        ReadMessageController.newInstance(getFC().getQuery());
        activity.setOnFragmentBackListener(this);
        bodyBuild = new BodyBuild(activity);
//        if(Build.VERSION.SDK_INT >= 21)
//            getListView().setNestedScrollingEnabled(false);
//        if (type.equals(Constants.INBOX))
//            getListView().setOnItemLongClickListener(this);
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

        getFC().getQuery().init(url(), ShikiStatusResult.TYPE.ARRAY)
                .addParam("type", type)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .addParam("desc", "1")
                .setCache(true, QueryShiki.FIVE_MIN)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(ShikiStatusResult res) {
        if(activity == null)
            return;
        lah.loadAsyncBuild(bodyBuild, res.getResultArray(), 600, ItemNewsUserShiki.class);
    }

    @Override
    public ListRecycleAdapter getAdapter(List list) {
        NewsUserAdapter adptr = new NewsUserAdapter(activity, getAdapterLayout(), getFC().getQuery(), list);
        adptr.setType(type);
        adptr.setOnSettingsListener(this);
        adptr.setOnItemClickListener(this);
        return adptr;
    }

    @Override
    public void onItemClick(ItemNewsUserShiki item, int posotion) {
        if (type.equals(Constants.INBOX)) {
            activity.loadPage(InboxFragment2.newInstance());
        } else if (type.equals(Constants.NEWS)) {
            Intent intent = ProjectTool.getSimpleIntentDetails(activity, item.linked.type);
            if (intent != null) {
                intent.putExtra(Constants.ITEM_ID, item.linked.id);
                activity.startActivity(intent);
                return;
            }

            if (Constants.SITENEWS.equalsIgnoreCase(item.kind)) {
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

            item.isExpandedBtns = !item.isExpandedBtns;
            getAdapter().notifyItem(posotion);
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        super.onItemClick(parent, view, position, id);
//
//        Adapter adp = parent.getAdapter();
//        if (adp == null)
//            return;
//
//        ItemNewsUserShiki item = (ItemNewsUserShiki) adp.getItem(position);
//        if (type.equals(Constants.INBOX)) {
//            activity.loadPage(InboxFragment2.newInstance());
//        } else if (type.equals(Constants.NEWS)) {
//            Intent intent = ProjectTool.getSimpleIntentDetails(activity, item.linked.type);
//            if (intent != null) {
//                intent.putExtra(Constants.ITEM_ID, item.linked.id);
//                activity.startActivity(intent);
//                return;
//            }
//
//            if (item.kind.toLowerCase().equals(Constants.SITENEWS)) {
//                intent = new Intent(activity, ShowPageActivity.class);
//                intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.OFTOPIC_PAGE);
//                intent.putExtra(Constants.TREAD_ID, item.linked.id);
//                activity.startActivity(intent);
//
//                return;
//            }
//        } else if (type.equals(Constants.NOTIFYING)) {
//            if(item.kind.equalsIgnoreCase(Constants.GROUP_REQUEST)){
//                LinkHelper.goToUrl(activity, item.htmlBody);
//                return;
//            } else if(item.kind.equalsIgnoreCase(Constants.FRIEND_REQUEST)){
//                ProjectTool.goToUser(activity, item.from.id);
//                return;
//            }
//            if(item.linked == null || item.linked.id == null)
//                return;
//            View btn = view.findViewById(R.id.llActions);
//            hs.setGoneToggle(btn);
//
//        }
//    }

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

//    @Override
//    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        ActionDescription confirmDeleteAction = new ActionDescription() {
//
//            @Override
//            public void act(int[] selectedItems) {
//                List<Object> lists = getAllList();
//                CopyOnWriteArrayList<String> ids = new CopyOnWriteArrayList<>();
//                for (int i = 0; i < selectedItems.length; i++) {
//                    ItemNewsUserShiki obj = (ItemNewsUserShiki) lists.get(selectedItems[i]);
//                    ids.add(obj.id);
//                }
//                if (ids.size() > 0) {
//                    String strIds = TextUtils.join(",", ids);
//                    // TODO delete collections messages
//                }
//            }
//        };
//        showDeleteFromListInterface(confirmDeleteAction);
//        return true;
//    }

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
                        deleteDialod((ItemNewsUserShiki) v.getTag());
                        return true;
                    }
                    return false;
                }
            });
            popup.show();
        }
    }

    private void deleteDialod(ItemNewsUserShiki item) {
        ProjectTool.deleteItem(activity, ShikiApi.getUrl(ShikiPath.MESSAGESPRIVATE_ID, item.id));
        UserNewsFragment.this.removeItem(item);
        invalidate();
    }

}
