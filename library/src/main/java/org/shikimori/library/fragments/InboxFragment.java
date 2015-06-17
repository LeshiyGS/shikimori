package org.shikimori.library.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

import org.shikimori.library.R;
import org.shikimori.library.adapters.InboxAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.ItemDialogs;
import org.shikimori.library.tool.baselisteners.BaseAnimationListener;
import org.shikimori.library.tool.controllers.ReadMessageController;
import org.shikimori.library.tool.pmc.PopupMenuCompat;

import java.util.List;

/**
 * Created by Феофилактов on 06.05.2015.
 */
public class InboxFragment extends BaseListViewFragment implements View.OnClickListener {

    private InboxAdapter adptr;

    @Override
    protected boolean isOptionsMenu() {
        return false;
    }

    public static InboxFragment newInstance() {
        return new InboxFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ReadMessageController.newInstance(query);
        showRefreshLoader();
        loadData();
    }

    @Override
    public void loadData() {
        query.init(ShikiApi.getUrl(ShikiPath.DIALOGS), StatusResult.TYPE.ARRAY)
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
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        ObjectBuilder<ItemDialogs> builder = new ObjectBuilder<>(res.getResultArray(), ItemDialogs.class);
        prepareData(builder.list, true, true);
    }

    @Override
    public ArrayAdapter<ItemDialogs> getAdapter(List list) {
        adptr = new InboxAdapter(activity, list);
        adptr.setOnSettingsListener(this);
        return adptr;
    }

    void clearData() {
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.DIALOGS));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ItemDialogs item = (ItemDialogs) parent.getAdapter().getItem(position);
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
        activity.getLoaderController().show();
        query.init(ShikiApi.getUrl(ShikiPath.DIALOGS) + "/" + item.message.from.nickname)
                .setMethod(Query.METHOD.DELETE)
                .getResult(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {
                        activity.getLoaderController().hide();
                        YoYo.with(Techniques.FadeOutUp)
                                .withListener(new BaseAnimationListener() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        Parcelable state = getListView().onSaveInstanceState();
                                        InboxFragment.this.removeItem(dialogposition);
                                        clearData();
                                        getListView().onRestoreInstanceState(state);
                                    }
                                })
                                .duration(300)
                                .playOn(parent);
                    }
                });
    }
}
