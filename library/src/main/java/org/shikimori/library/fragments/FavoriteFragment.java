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
public class FavoriteFragment extends PagerAdapterFragment implements Query.OnQuerySuccessListener {

    List<List<AMShiki>> pages = new ArrayList<>();
    String[] titles;

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
        loadData();
    }

    private void loadData() {
        activity.getLoaderController().show();
        query.init(ShikiApi.getUrl(ShikiPath.FAVOURITES, getUserId()))
            .setCache(true, Query.DAY)
            .getResult(this);
    }

    @Override
    protected FragmentPageAdapter getPagerAdapter() {
        return new FragmentPageAdapter(activity.getSupportFragmentManager()){

            @Override
            public Fragment getItem(int position) {
                return FavoriteListFragment.newInstance(pages.get(position));
            }

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        };
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        if(activity == null)
            return;
        activity.getLoaderController().hide();
        JSONObject data = res.getResultObject();
        if(data == null)
            return;

        titles = new String[]{
            activity.getString(R.string.anime),
            activity.getString(R.string.manga),
            activity.getString(R.string.characters),
            activity.getString(R.string.people),
            activity.getString(R.string.mangakas),
            activity.getString(R.string.seyu),
            activity.getString(R.string.producers),
        };

        ObjectBuilder<AMShiki> builder = new ObjectBuilder<>(data.optJSONArray("animes"), AMShiki.class);
        pages.add(builder.getDataList());
        pages.add(builder.getList(data.optJSONArray("mangas"), AMShiki.class));
        pages.add(builder.getList(data.optJSONArray("characters"), AMShiki.class));
        pages.add(builder.getList(data.optJSONArray("people"), AMShiki.class));
        pages.add(builder.getList(data.optJSONArray("mangakas"), AMShiki.class));
        pages.add(builder.getList(data.optJSONArray("seyu"), AMShiki.class));
        pages.add(builder.getList(data.optJSONArray("producers"), AMShiki.class));
        buildPages();
    }
}
