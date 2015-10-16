package org.shikimori.library.features.profile;

import android.os.Bundle;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.PagerAdapterFragment;
import org.shikimori.library.tool.constpack.Constants;

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
        return newInstance(b);
    }

    public static FavoriteFragment newInstance(Bundle params) {
        FavoriteFragment frag = new FavoriteFragment();
        frag.setArguments(params);
        return frag;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addPage(FavoriteListFragment.newInstance(0, getArguments()),  activity.getString(R.string.anime));
        addPage(FavoriteListFragment.newInstance(1, getArguments()),  activity.getString(R.string.manga));
        addPage(FavoriteListFragment.newInstance(2, getArguments()),  activity.getString(R.string.characters));
        addPage(FavoriteListFragment.newInstance(3, getArguments()),  activity.getString(R.string.people));
        addPage(FavoriteListFragment.newInstance(4, getArguments()),  activity.getString(R.string.mangakas));
        addPage(FavoriteListFragment.newInstance(5, getArguments()),  activity.getString(R.string.seyu));
        addPage(FavoriteListFragment.newInstance(6, getArguments()), activity.getString(R.string.producers));

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
