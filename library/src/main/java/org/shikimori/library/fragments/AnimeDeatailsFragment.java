package org.shikimori.library.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.fragments.base.BaseFragment;
import org.shikimori.library.tool.Constants;

/**
 * Created by Владимир on 31.03.2015.
 */
public class AnimeDeatailsFragment extends BaseFragment<BaseActivity> {

    private String animeId;

    public static AnimeDeatailsFragment newInstance(Bundle b) {
        AnimeDeatailsFragment frag = new AnimeDeatailsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_shiki_grid, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initArgiments();

    }

    private void initArgiments() {
        Bundle b = getArguments();
        if(b == null)
            return;

        animeId = getArguments().getString(Constants.ANIME_ID);
    }
}
