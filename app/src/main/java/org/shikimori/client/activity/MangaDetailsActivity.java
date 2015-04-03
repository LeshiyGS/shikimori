package org.shikimori.client.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.shikimori.client.R;
import org.shikimori.client.activity.base.BaseDetailsActivity;
import org.shikimori.library.fragments.AnimeDeatailsFragment;
import org.shikimori.library.fragments.DiscusionFragment;
import org.shikimori.library.fragments.MangaDeatailsFragment;
import org.shikimori.library.fragments.base.PagerAdapterFragment;
import org.shikimori.library.interfaces.UpdateCommentsListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Владимир on 31.03.2015.
 */
public class MangaDetailsActivity extends BaseDetailsActivity implements UpdateCommentsListener {

    private List<Fragment> pageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // данные об anime (id)
        Bundle b = getIntent().getExtras();
        // список фрагментов
        pageList = new ArrayList<>();
        pageList.add(MangaDeatailsFragment.newInstance(b));
        pageList.add(DiscusionFragment.newInstance(b));
        // view pager
        loadPage(PagerAdapterFragment.newInstance(
                pageList,
                getString(R.string.manga),
                getString(R.string.Discusion)
        ));
    }

    @Override
    public void startLoadComments(String treadId) {
        pageList.get(1);
        DiscusionFragment frag = (DiscusionFragment) pageList.get(1);
        frag.startLoadComments(treadId);
    }
}
