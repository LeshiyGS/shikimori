package org.shikimori.library.features.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import com.nineoldandroids.animation.Animator;

import org.shikimori.library.R;
import org.shikimori.library.features.profile.adapter.InboxRecycleAdapter;
import org.shikimori.library.fragments.base.abstracts.recycleview.BaseRecycleViewFragment;
import org.shikimori.library.fragments.base.abstracts.recycleview.ListRecycleAdapter;
import org.shikimori.library.fragments.base.abstracts.recycleview.OnItemClickRecycleListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import com.gars.querybuilder.BaseQuery;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.features.profile.model.ItemDialogs;
import org.shikimori.library.tool.LoadAsyncBuildHelper;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.baselisteners.BaseAnimationListener;
import org.shikimori.library.tool.controllers.ReadMessageController;
import org.shikimori.library.tool.parser.jsop.BodyBuild;
import org.shikimori.library.tool.pmc.PopupMenuCompat;

import java.util.List;

/**
 * Created by Феофилактов on 06.05.2015.
 */
public class InboxFragment2 extends BaseRecycleViewFragment implements View.OnClickListener, BaseQuery.OnQuerySuccessListener<ShikiStatusResult>, OnItemClickRecycleListener<ItemDialogs> {

    private InboxRecycleAdapter adptr;
    private BodyBuild bodyBuilder;
    private LoadAsyncBuildHelper lah;

    public static InboxFragment2 newInstance() {
        return new InboxFragment2();
    }

    @Override
    public int getActionBarTitle() {
        return R.string.inbox;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lah = new LoadAsyncBuildHelper(activity, this);
        ReadMessageController.newInstance(getFC().getQuery());
        showRefreshLoader();
        bodyBuilder = ProjectTool.getBodyBuilder(activity, BodyBuild.CLICKABLETYPE.NOT);
        loadData();
    }

    @Override
    public void loadData() {
        getFC().getQuery().init(ShikiApi.getUrl(ShikiPath.DIALOGS), ShikiStatusResult.TYPE.ARRAY)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .getResult(this);
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        clearData();
        loadData();
    }

    @Override
    public void onQuerySuccess(ShikiStatusResult res) {
        if(activity == null)
            return;
        lah.loadAsyncBuild(bodyBuilder, res.getResultArray(), ItemDialogs.class);
    }

    @Override
    public ListRecycleAdapter getAdapter(List<?> list) {
        adptr = new InboxRecycleAdapter(activity, (List<ItemDialogs>) list);
        adptr.setOnSettingsListener(this);
        adptr.setOnItemClickListener(this);
        return adptr;
    }

    void clearData() {
        getFC().getQuery().invalidateCache(ShikiApi.getUrl(ShikiPath.DIALOGS));
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ItemDialogs item = (ItemDialogs) parent.getAdapter().getItem(position);
//        activity.loadPage(ChatFragment.newInstance(item.user.nickname, item.user.id));
//    }

    @Override
    public void onItemClick(ItemDialogs item, int posotion) {
        activity.loadPage(ChatFragment.newInstance(item.user.nickname, item.user.id));
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.icSettings) {
            PopupMenuCompat popup = PopupMenuCompat.newInstance(activity, v);
            popup.inflate(R.menu.discus_menu);
            popup.getMenu().removeItem(R.id.icAnswer);
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

    private void deleteDialod(final int dialogposition, final View parent) {
        ItemDialogs item = adptr.getItem(dialogposition);
        ProjectTool.deleteItem(activity, ShikiApi.getUrl(ShikiPath.DIALOGS) + "/" + item.message.from.nickname,
                parent, new BaseAnimationListener(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        InboxFragment2.this.removeItem(dialogposition);
                        clearData();
                    }
                });
    }

}
