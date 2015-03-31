package org.shikimori.library.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.fragments.base.BaseFragment;

/**
 * Created by Владимир on 31.03.2015.
 */
public class AnimeDiscusionFragment extends BaseFragment<BaseActivity> {


    public static AnimeDiscusionFragment newInstance(Bundle b) {
        AnimeDiscusionFragment frag = new AnimeDiscusionFragment();
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
    }
}
