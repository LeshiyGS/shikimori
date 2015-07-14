package org.shikimori.library.custom;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.fragments.dialogs.AddRateDialogFragment;
import org.shikimori.library.interfaces.OnNewMenuListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.UserRate;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.controllers.ApiRatesController;
import org.shikimori.library.tool.h;

/**
 * Created by Владимир on 14.07.2015.
 */
public class CustomAddRateView extends FrameLayout implements AddRateDialogFragment.ControllListenerRate {
    private Button bAddToList;
    private View bListSettings, bListPlus;
    private ApiRatesController apiRateController;
    private Query query;
    private String userId;
    private ProjectTool.TYPE type;
    private String itemId;
    private UserRate rate;
    private BaseActivity activity;

    public CustomAddRateView(Context context) {
        this(context, null);
    }

    public CustomAddRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomAddRateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View v = inflate(getContext(), R.layout.custom_add_rate_view, null);
        addView(v);
        bAddToList = h.get(this, R.id.bAddToList);
        bListSettings = h.get(this, R.id.bListSettings);
        bListPlus = h.get(this, R.id.bListPlus);

        bAddToList.setOnClickListener(clickListener);
        bListSettings.setOnClickListener(clickListener);
        bListPlus.setOnClickListener(clickListener);
    }

    public void initParams(BaseActivity activity, String userId) {
        this.activity = activity;
        this.query = activity.prepareQuery(false);
        this.userId = userId;
        apiRateController = new ApiRatesController(query);
    }

    public void setRate(String itemId, UserRate rate, ProjectTool.TYPE type) {
        this.itemId = itemId;
        this.rate = rate;
        this.type = type;
        setButtonName();
    }

    /**
     * Name of
     */
    public void setButtonName() {
        String name = ProjectTool.getListStatusName(getContext(), rate.status, type);
        if (name == null)
            name = getContext().getString(R.string.add_to_list);
        else if (rate.status != UserRate.Status.COMPLETED) {
            StringBuilder str = new StringBuilder(name)
                    .append(" - ")
                    .append(rate.episodes);
            name = str.toString();
        }
        if (rate.status == UserRate.Status.WATCHING ||
                rate.status == UserRate.Status.REWATCHING) {
            h.setVisible(bListSettings, true);
            h.setVisible(bListPlus, true);
        } else {
            h.setVisibleGone(bListSettings);
            h.setVisibleGone(bListPlus);
        }
        bAddToList.setText(name);
    }

    /**
     * Обновление "добавить в список"
     *
     * @param itemId id menu
     */
    protected void setRate(int itemId) {
        if (itemId == R.id.delete) {
            deleteRate(rate.id, rate, type);
            return;
        }
        // update object rate
        rate.status = ProjectTool.getListStatusValue(itemId);
        rate.statusInt = UserRate.Status.fromStatus(rate.status);
        setRate();
    }


    protected void setRate() {
        invalidate();
        apiRateController.init();

        // set button name
        setButtonName();
        apiRateController.setUserRate(rate);
        // create rate
        if (rate.id == null) {
            query.getLoader().show();
            apiRateController.createRate(userId, itemId, type, new Query.OnQuerySuccessListener() {
                @Override
                public void onQuerySuccess(StatusResult res) {
                    rate.createFromJson(res.getResultObject());
                    query.getLoader().hide();
                }
            });
            // update rate
        } else {
            apiRateController.updateRate(rate.id);
        }
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.GET_USER_DETAILS) + ShikiUser.USER_ID);
    }

    /**
     * Remove rate from user list
     *
     * @param id
     * @param userRate
     */
    protected void deleteRate(String id, UserRate userRate, ProjectTool.TYPE type) {
        invalidate();
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.GET_USER_DETAILS) + ShikiUser.USER_ID);
        apiRateController.deleteRate(id);
        userRate.id = null;
        userRate.status = UserRate.Status.NONE;
        setButtonName();
    }

    OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.bAddToList) {
                addToListPopup(v, R.menu.add_to_list_anime_menu, new OnNewMenuListener() {

                    @Override
                    public boolean onMenuItemClick(PopupMenu menu, MenuItem menuItem) {
                        setRate(menuItem.getItemId());
                        return true;
                    }
                });
            } else if (v.getId() == R.id.bListSettings) {
                AddRateDialogFragment frag = AddRateDialogFragment.newInstance();
                frag.setUpdateListener(CustomAddRateView.this);
                frag.setType(type);
                frag.show(activity.getFragmentManagerLocal(), "");
            } else if (v.getId() == R.id.bListPlus) {
                rate.episodes++;
                updateRateFromDialog();
            }
        }

    };

    protected void addToListPopup(View v, int menu, OnNewMenuListener listener) {
        PopupMenu popupMenu = new PopupMenu(activity, v, Gravity.CENTER_VERTICAL);
        popupMenu.inflate(menu);
        if (rate.id == null) {
            popupMenu.getMenu().removeItem(R.id.delete);
        } else {
            int idMenu = ProjectTool.getItemIdFromStatus(rate.status);
            if (idMenu > 0)
                popupMenu.getMenu().removeItem(idMenu);
        }
        listener.setMenu(popupMenu);
        popupMenu.setOnMenuItemClickListener(listener);
        popupMenu.show();
    }

    @Override
    public UserRate getRateUser() {
        return rate;
    }

    @Override
    public void updateRateFromDialog() {
        setRate();
        setButtonName();
    }
}
