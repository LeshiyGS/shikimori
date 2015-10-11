package org.shikimori.library.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.loopj.android.http.RequestParams;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.AMBaseListFragment;
import org.shikimori.library.fragments.dialogs.FiltersDialogFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by Владимир on 27.03.2015.
 */
public class AMListMediaFragment extends AMBaseListFragment implements FiltersDialogFragment.OnFilterListener {

    private String type;
    private boolean selectFromOutside;
    private int title;
    private FiltersDialogFragment.FilterController filterController;

    public static Fragment newInstance(String type) {
        return newInstance(type, null);
    }

    public static AMListMediaFragment newInstance(Bundle b) {
        AMListMediaFragment frag = new AMListMediaFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    protected boolean isOptionsMenu() {
        return true;
    }

    public static Fragment newInstance(String type, String userId) {
        Bundle b = new Bundle();
        b.putString(Constants.TYPE, type);
        b.putString(Constants.USER_ID, userId);
        AMListMediaFragment frag = new AMListMediaFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getActionBarTitle() {
        switch (type){
            case Constants.ANIME: title = R.string.anime; break;
            case Constants.MANGA : title = R.string.manga; break;
        }

        return title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams();
    }

    private void initParams() {
        Bundle b = getArguments();
        if(b == null)
            return;

        type = b.getString(Constants.TYPE);
    }

    @Override
    protected String getLoadPath() {
        String url = null;
        switch (type){
            case Constants.ANIME: url = ShikiApi.getUrl(ShikiPath.ANIMES); break;
            case Constants.MANGA : url = ShikiApi.getUrl(ShikiPath.MANGAS); break;
        }

        return url;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_animes_filter) {
            FiltersDialogFragment frag = FiltersDialogFragment.newInstance();
            frag.setFilterController(filterController);
            frag.setType(type);
            frag.setOnFilterListener(this);
            frag.show(activity.getFragmentManagerLocal(), "");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void filtered(FiltersDialogFragment.FilterController controller) {
        filterController = controller;
        getFC().getQuery().getLoader().show();
        loadData();
    }

    @Override
    public RequestParams getFilterParams() {
        if(filterController!=null)
            return filterController.getRequestParams();
        return super.getFilterParams();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(selectFromOutside){
            AMShiki item = (AMShiki) parent.getAdapter().getItem(position);
            Bundle b = new Bundle();
            b.putAll(getArguments());

            String name = TextUtils.isEmpty(item.russianName) ? item.name : item.russianName;
            b.putString(Constants.ITEM_NAME, name);
            b.putString(Constants.ITEM_ID, item.id);
            Intent intent = new Intent();
            intent.putExtras(b);
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
        }
    }

    public void setSelectOutside(boolean selectOutside) {
        this.selectFromOutside = selectOutside;
    }
}
