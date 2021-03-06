package org.shikimori.library.features.topic;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.TopicsAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.objects.ItemTopicsShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.parser.jsop.BodyBuild;
import org.shikimori.library.tool.popup.TextPopup;

import java.util.List;

import ru.altarix.basekit.library.activities.BaseKitActivity;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class TopicsFragment extends BaseListViewFragment implements BaseKitActivity.OnFragmentBackListener {

    String section = "all";
    BodyBuild bodyBuild;
    private TextPopup popup;

    public static TopicsFragment newInstance() {
        return new TopicsFragment();
    }

    @Override
    protected boolean isOptionsMenu() {
        return true;
    }

    @Override
    public int getActionBarTitle() {
        return R.string.news;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showRefreshLoader();

        // html parser
        bodyBuild = new BodyBuild(activity);
        loadData();
    }

    /**
     * Update data, pull to refresh
     */
    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        ContentValues cv = new ContentValues();
        cv.put("section", section);

        getFC().getQuery().invalidateCache(ShikiApi.getUrl(ShikiPath.TOPICS), cv);
        loadData();
    }

    /**
     * Load data from server
     */
    public void loadData() {
        if (getFC().getQuery() == null)
            return;

        getFC().getQuery().init(ShikiApi.getUrl(ShikiPath.TOPICS), ShikiStatusResult.TYPE.ARRAY)
                .addParam("forum", section)
                .addParam("limit", LIMIT)
                .addParam("page", page)
//                .addParam("desc", "1")
                .setCache(true, QueryShiki.HALFHOUR)
                .getResult(this);
    }

    /**
     * Async build list data
     * @param res
     */
    @Override
    public void onQuerySuccess(final ShikiStatusResult res) {
        loadAsyncBuild(bodyBuild, res.getResultArray(), 300, ItemTopicsShiki.class);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        ItemTopicsShiki item = (ItemTopicsShiki) parent.getAdapter().getItem(position);

        String type = TextUtils.isEmpty(item.linkedType) ? item.type : item.linkedType;
        Intent intent = ProjectTool.getSimpleIntentDetails(activity, type);
        if(intent!=null){
            intent.putExtra(Constants.ITEM_ID, item.linkedId);
            activity.startActivity(intent);
            return;
        }

        intent = new Intent(activity, ShowPageActivity.class);
        switch (type.toLowerCase()) {
            case Constants.GROUP:
                intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.CLUB_PAGE);
                intent.putExtra(Constants.ACTION_BAR_TITLE, item.linked.name);
                intent.putExtra(Constants.TOPIC_ID, item.id);
                break;
            default:
                intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.OFTOPIC_PAGE);
                intent.putExtra(Constants.TOPIC_ID, item.id);
                intent.putExtra(Constants.ITEM_OBJECT, item.jsonObject);
        }
        intent.putExtra(Constants.ITEM_ID, item.linkedId);
        activity.startActivity(intent);
    }

    /**
     * If text is too big, show popup with text and parsed him
     * @param item
     */
    void showPopupText(final ItemTopicsShiki item){
        popup = new TextPopup(activity);
        popup.showLoader();
//        bodyBuild.parce(item.doc, popup.getBody());
        bodyBuild.parceAsync(item.htmlBody, new BodyBuild.ParceDoneListener() {
            @Override
            public void done(ViewGroup view) {
                popup.hideLoader();
                popup.setBody(view);
            }
        });
        popup.show();
    }

    @Override
    public ArrayAdapter<ItemTopicsShiki> getAdapter(List<?> list) {
        return new TopicsAdapter(activity, list);
    }

    @Override
    protected Menu getActionBarMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.topics_menu, menu);
        return menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        section = null;

        if (id == R.id.all) section = "all";
        else if (id == R.id.news) section = "news";
        else if (id == R.id.anime) section = "animanga";
        else if (id == R.id.site) section = "site";
        else if (id == R.id.offtop) section = "offtopic";
        else if (id == R.id.group) section = "clubs";
        else if (id == R.id.reviews) section = "reviews";
        else if (id == R.id.polls) section = "contests";
        else if (id == R.id.vn) section = "vn";
        else if (id == R.id.games) section = "games";
        else if (id == R.id.cosplay) section = "cosplay";

        if (section != null) {
            page = DEFAULT_FIRST_PAGE;
            showRefreshLoader();
            loadData();

            return true;
        } else
            section = "all";

        return super.onOptionsItemSelected(item);
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
}
