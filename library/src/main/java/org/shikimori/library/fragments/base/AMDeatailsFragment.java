package org.shikimori.library.fragments.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.ThumbToImage;
import org.shikimori.library.tool.baselisteners.BaseAnimationListener;
import org.shikimori.library.tool.Blur;
import org.shikimori.library.tool.FixPauseAnimate;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.baselisteners.BaseImageLoadListener;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.h;

import ru.altarix.ui.CustomTextView;
import ru.altarix.ui.tool.TextStyling;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public abstract class AMDeatailsFragment extends PullableFragment<BaseActivity> implements Query.OnQuerySuccessListener, View.OnClickListener {

    private String itemId;
    protected ScrollView svMain;
    protected TextView tvTitle, tvScore, tvReview,tvStatus;
    protected ImageView ivPoster;
    protected RatingBar rbTitle;
    protected ViewGroup llInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_deatales, null);
        svMain = (ScrollView) v.findViewById(R.id.svMain);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        llInfo = (ViewGroup) v.findViewById(R.id.llInfo);
        tvScore = (TextView) v.findViewById(R.id.tvMenuScore);
        tvReview = (TextView) v.findViewById(R.id.tvReview);
        ivPoster = (ImageView) v.findViewById(R.id.ivPoster);
        rbTitle = (RatingBar) v.findViewById(R.id.rbTitle);
        tvStatus = (TextView) v.findViewById(R.id.tvStatus);

        ivPoster.setOnTouchListener(h.getImageHighlight);
        return v;
    }

    @Override
    public int pullableViewId() {
        return R.id.svMain;
    }

    public abstract String getPatch();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArgiments();
        if(itemId == null)
            return;
        showRefreshLoader();
        loadDataFromServer();

        ivPoster.setOnClickListener(this);
    }

    @Override
    public void onStartRefresh() {
        query.invalidateCache(ShikiApi.getUrl(getPatch() + itemId));
        loadDataFromServer();
    }

    void loadDataFromServer(){
        query.init(ShikiApi.getUrl(getPatch()+itemId))
                .setCache(true, Query.HOUR)
                .getResult(this);
    }

    private void initArgiments() {
        Bundle b = getArguments();
        if(b == null)
            return;

        itemId = getArguments().getString(Constants.ITEM_ID);
    }

    public void setStatus(boolean anons, boolean ongoing){
        tvStatus.setText(getStatus(anons, ongoing));
        ProjectTool.setStatusColor(activity, tvStatus, anons, ongoing);
        YoYo.AnimationComposer composer = YoYo.with(Techniques.BounceInRight)
                .withListener(new BaseAnimationListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        h.setVisible(tvStatus, true);
                    }
                });
        FixPauseAnimate.play(composer, tvStatus, 700);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
    }

    protected void setTitleElement(String rusname, String engName){
        TextStyling styling = new TextStyling()
                .addGlobalStyle(TextStyling.TextStyle.COLOR, "66ffffff");

        if(rusname!=null){
            Spannable text = styling.formatString(rusname, engName + "\n" + rusname);
            tvTitle.setText(text);
        } else
            tvTitle.setText(engName);
    }

    protected String getStatus(boolean anons, boolean ongoing){
        return ProjectTool.getStatus(activity, anons, ongoing);
    }

    protected void addInfo(int label,  String text) {
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

    }
}
