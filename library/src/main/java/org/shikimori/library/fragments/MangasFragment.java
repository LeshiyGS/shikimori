package org.shikimori.library.fragments;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.AMBaseListFragment;
import org.shikimori.library.loaders.ShikiPath;

/**
 * Created by Владимир on 27.03.2015.
 */
public class MangasFragment extends AMBaseListFragment {

    @Override
    public int getActionBarTitle() {
        return R.string.manga;
    }

    @Override
    protected String getLoadPath() {
        return ShikiPath.MANGAS;
    }

}
