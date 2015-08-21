package org.shikimori.library.fragments.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gars.verticalratingbar.VerticalRatingBar;
import com.mcgars.imagefactory.cutomviews.ImageFactoryView;
import com.nineoldandroids.animation.Animator;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.shikimori.library.R;
import org.shikimori.library.custom.CustomAddRateView;
import org.shikimori.library.custom.ExpandableHeightGridView;
import org.shikimori.library.fragments.SimilarFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.Blur;
import org.shikimori.library.tool.FixPauseAnimate;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.baselisteners.BaseAnimationListener;
import org.shikimori.library.tool.baselisteners.BaseImageLoadListener;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.api.ApiRatesController;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;

import java.util.List;

import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.ui.CustomTextView;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public abstract class AMDeatailsFragment extends PullableFragment<BaseKitActivity<ShikiAC>>
        implements Query.OnQuerySuccessListener, View.OnClickListener {

    protected String itemId;
    protected ScrollView svMain;
    protected TextView tvTitle, tvReview, tvStatus;
    protected ImageView ivPoster;
    protected VerticalRatingBar rbTitle;
    protected ViewGroup llInfo, llWanted;
    protected ExpandableHeightGridView llStudios;
    protected ApiRatesController apiRateController;
    protected CustomAddRateView llWrapAddList;
    protected ImageFactoryView imageFactory;
    protected View bSimilar;
    protected FloatingActionButton fbPlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_detales, null);
        setBaseView(v);
        svMain = find(R.id.svMain);
        tvTitle = find(R.id.tvTitle);
        llInfo = find(R.id.llInfo);
        tvReview = find(R.id.llReview);
        ivPoster = find(R.id.ivPoster);
        rbTitle = find(R.id.vRatingBar);
        tvStatus = find(R.id.tvStatus);
        llStudios = find(R.id.llStudios);
        llWanted = find(R.id.llWanted);
        llWrapAddList = find(R.id.llWrapAddList);
        bSimilar = find(R.id.bSimilar);
        fbPlay = find(R.id.fbPlay);
        imageFactory = find(R.id.imageFactory);

        ivPoster.setOnTouchListener(hs.getImageHighlight);
        ivPoster.setOnClickListener(this);
        bSimilar.setOnClickListener(this);
        fbPlay.setOnClickListener(this);
        hs.setVisible(false, svMain);
        return v;
    }

    @Override
    public int pullableViewId() {
        return R.id.svMain;
    }

    public abstract String getPatch();

    public abstract ProjectTool.TYPE getType();

    protected void beforeLoadDate(){

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rbTitle.setSelectColor(activity.getResources().getColor(R.color.colorAccent));
        llWrapAddList.initParams(activity, getFC().getUserId());

        initArgiments();
        if (itemId == null)
            return;
        beforeLoadDate();
        showRefreshLoader();
        loadDataFromServer();

        apiRateController = new ApiRatesController(getFC().getQuery());
    }

    @Override
    public void onStartRefresh() {
        invalidate();
        loadDataFromServer();
    }

    protected void invalidate() {
        getFC().getQuery().invalidateCache(ShikiApi.getUrl(getPatch() + itemId));
    }

    void loadDataFromServer() {
        getFC().getQuery().init(ShikiApi.getUrl(getPatch() + itemId))
                .setCache(true, Query.HOUR)
                .getResult(this);
    }

    private void initArgiments() {
        Bundle b = getArguments();
        if (b == null)
            return;

        itemId = getArguments().getString(Constants.ITEM_ID);
    }

    public void setStatus(boolean anons, boolean ongoing) {
        tvStatus.setText(getStatus(anons, ongoing));
        ProjectTool.setStatusColor(activity, tvStatus, anons, ongoing);
        YoYo.AnimationComposer composer = YoYo.with(Techniques.BounceInRight)
                .withListener(new BaseAnimationListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        hs.setVisible(tvStatus);
                    }
                });
        FixPauseAnimate.play(composer, tvStatus, 700);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        hs.setVisible(svMain);
        stopRefresh();
        llInfo.removeAllViews();
    }

    protected void setTitleElement(String rusname, String engName) {
        tvTitle.setText(ProjectTool.getTitleElement(rusname, engName));
    }

    protected String getStatus(boolean anons, boolean ongoing) {
        return ProjectTool.getStatus(activity, anons, ongoing);
    }

    /**
     * Set wanted from people
     */
    protected void buildStateWanted(List<VerticalRatingBar.Rates> ratesStatusesStats) {
        llWanted.removeAllViews();
        LayoutInflater inflater = activity.getLayoutInflater();
        if (ratesStatusesStats != null) {
            for (VerticalRatingBar.Rates ratesStatusesStat : ratesStatusesStats) {
                View v = inflater.inflate(R.layout.item_shiki_progress, null);
                SeekBar sbProgress = hs.get(v, R.id.sbProgress);
                sbProgress.setEnabled(false);
                TextView tvProgress = hs.get(v, R.id.tvProgress);
                sbProgress.setProgress(ratesStatusesStat.getProcents());
                tvProgress.setText(ratesStatusesStat.getTitle() + " / " + ratesStatusesStat.getValue());
                llWanted.addView(v);
            }
        }
    }

    protected void addInfo(int label, String text) {
        CustomTextView row = new CustomTextView(activity);
        row.setLabel(label);
        row.setText(text);
        llInfo.addView(row);
    }

    protected ImageLoadingListener addBlurToTitle = new BaseImageLoadListener() {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            Blur.apply(activity, ivPoster, tvTitle);
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bSimilar) {
            activity.getPageController()
                    .startActivity(SimilarFragment.class,
                            getType() == ProjectTool.TYPE.ANIME ? Constants.ANIME : Constants.MANGA,
                            itemId);
        }
    }
}
