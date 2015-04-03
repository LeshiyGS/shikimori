package org.shikimori.library.fragments;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.AMBaseListFragment;
import org.shikimori.library.loaders.ShikiPath;

import java.util.List;

/**
 * Created by Владимир on 27.03.2015.
 */
public class AnimesFragment extends AMBaseListFragment {

    public static AnimesFragment newInstance() {
        return new AnimesFragment();
    }

    @Override
    public int getActionBarTitle() {
        return R.string.anime;
    }

    @Override
    protected String getLoadPath() {
        return ShikiPath.ANIMES;
    }
}
