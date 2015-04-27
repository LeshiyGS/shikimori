package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.AMBaseListFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by Владимир on 27.03.2015.
 */
public class InfoMediaFragment extends AMBaseListFragment {

    private String type;
    private int title;

    public static Fragment newInstance(String type) {
        return newInstance(type, null);
    }
    public static Fragment newInstance(String type, String userId) {
        Bundle b = new Bundle();
        b.putString(Constants.TYPE, type);
        b.putString(Constants.USER_ID, userId);
        InfoMediaFragment frag = new InfoMediaFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getActionBarTitle() {
        switch (type){
            case Constants.ANIME: title = R.string.anime; break;
            case Constants.MANGA : title = R.string.manga; break;
        }

        return title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams();
    }

    private void initParams() {
        Bundle b = getArguments();
        if(b == null)
            return;

        type = b.getString(Constants.TYPE);
    }

    @Override
    protected String getLoadPath() {
        String url = null;
        switch (type){
            case Constants.ANIME: url = ShikiApi.getUrl(ShikiPath.ANIMES); break;
            case Constants.MANGA : url = ShikiApi.getUrl(ShikiPath.MANGAS); break;
        }

        return url;
    }
}
