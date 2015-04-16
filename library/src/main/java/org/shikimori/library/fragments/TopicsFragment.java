package org.shikimori.library.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.TopicsAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.interfaces.OnViewBuildLister;
import org.shikimori.library.loaders.BackGroubdLoader;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemTopicsShiki;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.jsop.BodyBuild;
import org.shikimori.library.tool.popup.TextPopup;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class TopicsFragment extends BaseListViewFragment {

    String section = "all";
    BodyBuild bodyBuild;

    public static TopicsFragment newInstance() {
        return new TopicsFragment();
    }

    @Override
    public int getActionBarTitle() {
        return R.string.news;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showRefreshLoader();
        bodyBuild = new BodyBuild(activity);
        loadData();
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        ContentValues cv = new ContentValues();
        cv.put("section", section);

        query.invalidateCache(ShikiApi.getUrl(ShikiPath.TOPICS), cv);
        loadData();
    }

    // TODO create loader list
    public void loadData() {
        if (query == null)
            return;

        query.init(ShikiApi.getUrl(ShikiPath.TOPICS), StatusResult.TYPE.ARRAY)
                .addParam("section", section)
                .addParam("limit", LIMIT)
                .addParam("page", page)
//                .addParam("desc", "1")
                .setCache(true, Query.HALFHOUR)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(final StatusResult res) {

        new BackGroubdLoader<ItemTopicsShiki>(activity, bodyBuild, res.getResultArray(), ItemTopicsShiki.class){
            @Override
            public void deliverResult(List data) {
                super.deliverResult(data);
                stopRefresh();
                prepareData(data, true, true);
                bodyBuild.loadPreparedImages();
            }

            @Override
            public boolean onAdvancesCheck(ItemTopicsShiki item, int position) {
                String type = TextUtils.isEmpty(item.linkedType) ? item.type : item.linkedType;
                if(type.equalsIgnoreCase(Constants.TOPIC))
                    return true;
                return false;
            }
        }.forceLoad();
    }

//    private void buildVies(ItemTopicsShiki item) {
//        LinearLayout body = new LinearLayout(activity);
//        body.setLayoutParams(h.getDefaultParams());
//        body.setOrientation(LinearLayout.VERTICAL);
//        item.parsedContent = body;
//        new BodyBuild(activity).parce(item.doc, body);
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        ItemTopicsShiki item = (ItemTopicsShiki) parent.getAdapter().getItem(position);
        Intent intent = new Intent(activity, ShowPageActivity.class);
        String type = TextUtils.isEmpty(item.linkedType) ? item.type : item.linkedType;
        switch (type.toLowerCase()) {
            case Constants.ANIME:
                intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.ANIME_PAGE);
                break;
            case Constants.MANGA:
                intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.MANGA_PAGE);
                break;
            case Constants.TOPIC:
                if (item.section.permalink.equals("o")){
                    intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.OFTOPIC_PAGE);
                    intent.putExtra(Constants.TREAD_ID, item.id);
                }else{
                    showPopupText(item);
                    return;
                }
        }
        intent.putExtra(Constants.ITEM_ID, item.linkedId);
        activity.startActivity(intent);
    }

    void showPopupText(final ItemTopicsShiki item){
        final TextPopup popup = new TextPopup(activity);
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
        else if (id == R.id.anime) section = "a";
        else if (id == R.id.manga) section = "m";
        else if (id == R.id.characters) section = "c";
        else if (id == R.id.site) section = "s";
        else if (id == R.id.offtop) section = "o";
        else if (id == R.id.group) section = "g";
        else if (id == R.id.reviews) section = "reviews";
        else if (id == R.id.polls) section = "v";

        if (section != null) {
            page = DEFAULT_FIRST_PAGE;
            showRefreshLoader();
            loadData();

            return true;
        } else
            section = "all";

        return super.onOptionsItemSelected(item);
    }
}
