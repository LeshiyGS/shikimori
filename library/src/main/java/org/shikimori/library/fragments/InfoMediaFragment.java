package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.loopj.android.http.RequestParams;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.AMBaseListFragment;
import org.shikimori.library.fragments.dialogs.AddRateDialogFragment;
import org.shikimori.library.fragments.dialogs.FiltersDialogFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by Владимир on 27.03.2015.
 */
public class InfoMediaFragment extends AMBaseListFragment implements FiltersDialogFragment.OnFilterListener {

    private String type;
    private int title;
    private FiltersDialogFragment.FilterController filterController;

    public static Fragment newInstance(String type) {
        return newInstance(type, null);
    }
    public static Fragment newInstance(String type, String userId) {
        Bundle b = new Bundle();
        b.putString(Constants.TYPE, type);
        b.putString(Constants.USER_ID, userId);
        InfoMediaFragment frag = new InfoMediaFragment();
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
            frag.setOnFilterListener(this);
            frag.show(activity.getFragmentManagerLocal(), "");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void filtered(FiltersDialogFragment.FilterController controller) {
        filterController = controller;
        query.getLoader().show();
        loadData();
    }

    @Override
    public RequestParams getFilterParams() {
        if(filterController!=null)
            return filterController.getRequestParams();
        return super.getFilterParams();
    }
}
