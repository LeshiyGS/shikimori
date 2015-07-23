package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.custom.ExpandableHeightGridView;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.ItemClubDescriptionShiki;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

/**
 * Created by Владимир on 17.04.2015.
 */
public class ClubDetailsFragment extends PullableFragment<BaseActivity> implements Query.OnQuerySuccessListener, View.OnClickListener {

    TextView tvTitle;
    ImageView ivPoster;
    ViewGroup llInfo,tvReview;
    View tvAnimes, tvMangas;
    ScrollView svMain;
    ExpandableHeightGridView pageAnime, pageManga;
    private String itemId;
    private ItemClubDescriptionShiki item;
    private BodyBuild bodyBuilder;
    private View iLoader;

    public static ClubDetailsFragment newInstance(Bundle b) {
        ClubDetailsFragment frag = new ClubDetailsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_deatales_club, null);
        setBaseView(v);
        svMain   = find(R.id.svMain);
        tvTitle   = find(R.id.tvTitle);
        tvReview  = find(R.id.llReview);
        ivPoster  = find(R.id.ivPoster);
        iLoader  = find(R.id.iLoader);

        ivPoster.setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArgiments();
        showRefreshLoader();
        bodyBuilder = ProjectTool.getBodyBuilder(activity, BodyBuild.CLICKABLETYPE.INTEXT);
        loadData();
    }

    String getUrl(){
        return ShikiApi.getUrl(ShikiPath.CLUB_ID, itemId);
    }

    @Override
    public void onStartRefresh() {
        query.invalidateCache(getUrl());
        h.setVisible(iLoader);
        loadData();
    }

    void loadData(){
        query.init(getUrl())
                .setCache(true, Query.HOUR)
                .getResult(this);
    }

    private void initArgiments() {
        itemId = getParam(Constants.ITEM_ID);
    }

    @Override
    public int pullableViewId() {
        return R.id.svMain;
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        item = new ItemClubDescriptionShiki().create(res.getResultObject());

        activity.setTitle(item.name);

        bodyBuilder.parceAsync(item.descriptionHtml, new BodyBuild.ParceDoneListener() {
            @Override
            public void done(ViewGroup view) {
                if (activity == null || getView() == null)
                    return;
                h.setVisibleGone(iLoader);
                tvReview.removeAllViews();
                tvReview.addView(view);
                bodyBuilder.loadPreparedImages();
                svMain.scrollTo(0,0);
            }
        });

        if(item.original!=null)
            ShikiImage.show(item.original, ivPoster);

        if (activity instanceof ExtraLoadInterface)
            ((ExtraLoadInterface) activity).extraLoad(item.threadId);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivPoster){
            if(item.original!=null)
                activity.getThumbToImage().zoom(ivPoster, ProjectTool.fixUrl(item.original));
        }
    }
}
