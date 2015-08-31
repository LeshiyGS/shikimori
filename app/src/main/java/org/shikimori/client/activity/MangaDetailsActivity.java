package org.shikimori.client.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.shikimori.client.R;
import org.shikimori.client.activity.base.BaseDetailsActivity;
import org.shikimori.library.fragments.DiscusionFragment;
import org.shikimori.library.fragments.MangaDeatailsFragment;
import org.shikimori.library.fragments.base.PagerAdapterFragment;
import org.shikimori.library.interfaces.ExtraLoadInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Владимир on 31.03.2015.
 */
public class MangaDetailsActivity extends BaseDetailsActivity implements ExtraLoadInterface {

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
                getString(R.string.discusion)
        ));
    }

    @Override
    public void extraLoad(String itemId, Bundle params) {
        pageList.get(1);
        ExtraLoadInterface frag = (ExtraLoadInterface) pageList.get(1);
        frag.extraLoad(itemId,params);
    }
}
