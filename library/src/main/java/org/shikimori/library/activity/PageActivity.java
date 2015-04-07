package org.shikimori.library.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.shikimori.library.R;
import org.shikimori.library.fragments.AnimeDeatailsFragment;
import org.shikimori.library.fragments.DiscusionFragment;
import org.shikimori.library.fragments.MangaDeatailsFragment;
import org.shikimori.library.fragments.base.PagerAdapterFragment;
import org.shikimori.library.interfaces.UpdateCommentsListener;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.tool.constpack.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 07.04.2015.
 */
public class PageActivity extends BaseActivity implements UpdateCommentsListener {

    protected int page;
    private List<Fragment> pageList;
    private List<String> titleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    protected void addPageFragment(Fragment frag, String title){
        if(pageList==null){
            pageList = new ArrayList<>();
            titleList = new ArrayList<>();
        }
        pageList.add(frag);
        titleList.add(title);
    }

    protected void addPageFragment(Fragment frag, int title){
        addPageFragment(frag, getString(title));
    }

    protected void showPages() {

        String[] titles = new String[titleList.size()];
        titleList.toArray(titles); // fill the array

        loadPage(PagerAdapterFragment.newInstance(
                pageList,
                titles
        ));

    }

    @Override
    public void startLoadComments(String treadId) {
        if(pageList!=null)
            for (Fragment fragment : pageList) {
                if(fragment instanceof DiscusionFragment)
                    ((DiscusionFragment) fragment).startLoadComments(treadId);
            }
    }

    private void initData() {
        Bundle b = getIntent().getExtras();
        if(b==null)
            return;
        page = b.getInt(Constants.PAGE_FRAGMENT);
    }


    /****************************************************
     * SYSTEMS
     * @return
     ***************************************************/

    @Override
    protected int getLayoutId() {
        return R.layout.view_simple_content;
    }

    @Override
    public SharedPreferences getSettings() {
        return getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
    }

    @Override
    public Query prepareQuery(boolean separate) {
        if(separate){
            Query q = new Query(this);
            q.setLoader(query.getLoader());
            return q;
        }

        return query;
    }

}
