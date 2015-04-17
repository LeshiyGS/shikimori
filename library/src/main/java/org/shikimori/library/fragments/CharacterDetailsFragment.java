package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by Владимир on 17.04.2015.
 */
public class CharacterDetailsFragment extends PullableFragment<BaseActivity> implements Query.OnQuerySuccessListener {

    TextView tvTitle, tvReview;
    ImageView ivPoster;
    ViewGroup llInfo;
    View tvAnimes, tvMangas;
    ViewPager pageAnime, pageManga;
    private String itemId;

    public static CharacterDetailsFragment newInstance(Bundle b) {
        CharacterDetailsFragment frag = new CharacterDetailsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_deatales_character, null);
        setBaseView(v);
        tvTitle   = find(R.id.tvTitle);
        tvReview  = find(R.id.tvReview);
        ivPoster  = find(R.id.ivPoster);
        llInfo    = find(R.id.llInfo);
        tvAnimes  = find(R.id.tvAnimes);
        tvMangas  = find(R.id.tvMangas);
        pageAnime = find(R.id.pageAnime);
        pageManga = find(R.id.pageManga);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArgiments();
        showRefreshLoader();
        loadData();
    }

    String getUrl(){
        return ShikiApi.getUrl(ShikiApi.getUrl(ShikiPath.CHARACTER_ID) + itemId);
    }

    @Override
    public void onStartRefresh() {
        query.invalidateCache(getUrl());
        loadData();
    }

    void loadData(){
        query.init(getUrl())
                .setCache(true, Query.HOUR)
                .getResult(this);
    }

    private void initArgiments() {
        Bundle b = getArguments();
        if(b == null)
            return;

        itemId = getArguments().getString(Constants.ITEM_ID);
    }

    @Override
    public int pullableViewId() {
        return R.id.svMain;
    }

    @Override
    public void onQuerySuccess(StatusResult res) {

//        if (activity instanceof ExtraLoadInterface)
//            ((ExtraLoadInterface) activity).extraLoad(animeDetails.thread_id);
    }
}
