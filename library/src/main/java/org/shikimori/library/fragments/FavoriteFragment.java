package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.json.JSONObject;
import org.shikimori.library.R;
import org.shikimori.library.adapters.FragmentPageAdapter;
import org.shikimori.library.fragments.base.PagerAdapterFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.tool.constpack.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Владимир on 27.04.2015.
 */
public class FavoriteFragment extends PagerAdapterFragment {

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    public static FavoriteFragment newInstance(String userId) {
        Bundle b = new Bundle();
        b.putString(Constants.USER_ID, userId);
        FavoriteFragment frag = new FavoriteFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addPage(FavoriteListFragment.newInstance(0),  activity.getString(R.string.anime));
        addPage(FavoriteListFragment.newInstance(1),  activity.getString(R.string.manga));
        addPage(FavoriteListFragment.newInstance(2),  activity.getString(R.string.chapters));
        addPage(FavoriteListFragment.newInstance(3),  activity.getString(R.string.people));
        addPage(FavoriteListFragment.newInstance(4),  activity.getString(R.string.mangakas));
        addPage(FavoriteListFragment.newInstance(5),  activity.getString(R.string.seyu));
        addPage(FavoriteListFragment.newInstance(6),  activity.getString(R.string.producers));

        showPages();

    }

//
//    @Override
//    protected FragmentPageAdapter getPagerAdapter() {
//        return new FragmentPageAdapter(activity.getSupportFragmentManager()){
//
//            @Override
//            public Fragment getItem(int position) {
//                return FavoriteListFragment.newInstance(pages.get(position));
//            }
//
//            @Override
//            public int getCount() {
//                return titles.length;
//            }
//
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return titles[position];
//            }
//        };
//    }

}
