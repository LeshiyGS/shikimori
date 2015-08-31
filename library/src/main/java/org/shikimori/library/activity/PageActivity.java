package org.shikimori.library.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.PagerAdapterFragment;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.tool.constpack.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 07.04.2015.
 */
public class PageActivity extends BaseActivity implements ExtraLoadInterface {

    protected int page;
    private List<Fragment> pageList;
    private List<String> titleList;

    protected int extraLoad = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    protected void addPageFragment(Fragment frag, String title) {
        if (pageList == null) {
            pageList = new ArrayList<>();
            titleList = new ArrayList<>();
        }
        pageList.add(frag);
        titleList.add(title);
    }

    protected void addPageFragment(Fragment frag, int title) {
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
    public void extraLoad(String treadId, Bundle params) {
        if (pageList != null)
            for (int i = 0; i < pageList.size(); i++) {
                Fragment fragment = pageList.get(i);
                boolean insof = fragment instanceof ExtraLoadInterface;
                if ((extraLoad < 0 && insof) || (extraLoad == i && insof))
                    ((ExtraLoadInterface) fragment).extraLoad(treadId,params);
            }
    }

    private void initData() {
        Bundle b = getIntent().getExtras();
        if (b == null)
            return;
        page = b.getInt(Constants.PAGE_FRAGMENT);
        extraLoad = b.getInt(Constants.PAGE_EXTRA_LOAD, -1);
    }


    /**
     * *************************************************
     * SYSTEMS
     *
     * @return *************************************************
     */

    @Override
    protected int getLayoutId() {
        return R.layout.basekit_simple_activity;
    }
}
