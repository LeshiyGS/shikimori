package org.shikimori.client.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.shikimori.client.R;
import org.shikimori.library.fragments.AnimeDeatailsFragment;
import org.shikimori.library.fragments.AnimeDiscusionFragment;
import org.shikimori.library.fragments.base.PagerAdapterFragment;
import org.shikimori.library.interfaces.UpdateCommentsListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Владимир on 31.03.2015.
 */
public class AnimeDetailsActivity extends DrawerActivity implements UpdateCommentsListener {


    private List<Fragment> pageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // данные об anime (id)
        Bundle b = getIntent().getExtras();
        // список фрагментов
        pageList = new ArrayList<>();
        pageList.add(AnimeDeatailsFragment.newInstance(b));
        pageList.add(AnimeDiscusionFragment.newInstance(b));
        // view pager
        loadPage(PagerAdapterFragment.newInstance(
            pageList,
            getString(R.string.anime),
            getString(R.string.Discusion)
        ));
    }

    @Override
    public void initDrawer() {
        super.initDrawer();
        mDrawerAdapter.setSelected(-1);
    }

    @Override
    public void startLoadComments(String treadId) {
        pageList.get(1);
        AnimeDiscusionFragment frag = (AnimeDiscusionFragment) pageList.get(1);
        frag.startLoadComments(treadId);
    }
}
