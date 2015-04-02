package org.shikimori.library.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.interfaces.UserDataChangeListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.AnimeManga;
import org.shikimori.library.objects.UserDetails;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.constpack.AnimeStatuses;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Владимир on 30.03.2015.
 */
public class ProfileShikiFragment extends PullableFragment<BaseActivity> implements Query.OnQuerySuccessListener, View.OnClickListener {

    public static final String USER_ID = "user_id";

    String userId;
    private ImageView avatar;
    private TextView tvUserName;
    private UserDetails userDetails;
    private TextView tvMiniDetails,tvAnimeProgress,tvMangaProgress, tvLastOnline;
    private SeekBar sbAnimeProgress, sbMangaProgress;
    private View llBody, ivWebShow;

    public static ProfileShikiFragment newInstance() {
        return new ProfileShikiFragment();
    }

    public static ProfileShikiFragment newInstance(String userId) {
        Bundle b = new Bundle();
        b.putString(USER_ID, userId);

        ProfileShikiFragment frag = new ProfileShikiFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shiki_profile, null);
        llBody = v.findViewById(R.id.llBody);
        ivWebShow = v.findViewById(R.id.ivWebShow);
        avatar = (ImageView) v.findViewById(R.id.ava);
        tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        tvLastOnline = (TextView) v.findViewById(R.id.tvLastOnline);
        tvMiniDetails = (TextView) v.findViewById(R.id.tvMiniDetails);
        tvAnimeProgress = (TextView) v.findViewById(R.id.tvAnimeProgress);
        tvMangaProgress = (TextView) v.findViewById(R.id.tvMangaProgress);
        sbAnimeProgress = (SeekBar) v.findViewById(R.id.sbAnimeProgress);
        sbMangaProgress = (SeekBar) v.findViewById(R.id.sbMangaProgress);
        h.setVisible(llBody, false);

        ivWebShow.setOnClickListener(this);
        sbAnimeProgress.setOnTouchListener(disableScrolling);
        sbMangaProgress.setOnTouchListener(disableScrolling);

        return v;
    }

    View.OnTouchListener disableScrolling = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    };

    @Override
    public int pullableViewId() {
        return R.id.bodyScroll;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        showRefreshLoader();
        loadDataFromServer();
    }

    @Override
    public void onStartRefresh() {
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.GET_USER_DETAILS) + userId);
        loadDataFromServer();
    }

    private void initData() {
        Bundle b = getArguments();
        if(b != null)
            userId = b.getString(USER_ID);

        if(userId == null)
            userId    = activity.getShikiUser().getId();
    }

    void loadDataFromServer(){
        query.init(ShikiApi.getUrl(ShikiPath.GET_USER_DETAILS)+userId)
             .setCache(true, Query.HOUR)
             .getResult(this);

    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        h.setVisible(llBody, true);
        YoYo.with(Techniques.FadeIn)
            .playOn(llBody);

        stopRefresh();
        userDetails = UserDetails.create(res.getResultObject());
        if(activity.getShikiUser().getId().equalsIgnoreCase(userDetails.id)){
            activity.getShikiUser().setData(res.getResultObject());
            if(activity instanceof UserDataChangeListener)
                ((UserDataChangeListener) activity).updateUserUI();

        }

        fillUi();

    }

    private void fillUi() {
        if(userDetails == null || userDetails.id == null)
            return;

        if(userDetails.avatar!=null)
            ImageLoader.getInstance().displayImage(userDetails.avatar, avatar);
        tvUserName.setText(userDetails.nickname);

        setSexYearLocation();

        // когда был на сайте
        tvLastOnline.setText(userDetails.lastOnline);
        // web site
        setWebSite();
        // anime / manga progress
        setProgress();
    }

    private void setProgress() {
        ProgressData progress;
        if(userDetails.fullStatuses!=null){
            // set anime progress
            progress = getProgress(userDetails.fullStatuses.animes);
            sbAnimeProgress.setProgress(progress.percentage1);
            sbAnimeProgress.setSecondaryProgress(progress.percentage2);
            tvAnimeProgress.setText(String.format(
                    activity.getString(R.string.seeing),
                    progress.firstProgress,
                    progress.fullProgress
            ));
            // set manga progress
            progress = getProgress(userDetails.fullStatuses.manga);
            sbMangaProgress.setProgress(progress.percentage1);
            sbMangaProgress.setSecondaryProgress(progress.percentage2);
            tvMangaProgress.setText(String.format(
                    activity.getString(R.string.reading),
                    progress.firstProgress,
                    progress.fullProgress
            ));
        }
    }

    /**
     * Показываем иконку web странички
     */
    private void setWebSite() {
        if(!TextUtils.isEmpty(userDetails.website))
            h.setVisible(ivWebShow, true);
        else
            h.setVisibleGone(ivWebShow);
    }

    private void setSexYearLocation() {
        StringBuffer str = new StringBuffer();
        // пол
        if(!TextUtils.isEmpty(userDetails.sex)){
            switch (userDetails.sex){
                case "male": str.append(activity.getString(R.string.male)); break;
                case "female": str.append(activity.getString(R.string.female)); break;
            }
        }
        // сколько лет
        if(str.length() > 0 && !TextUtils.isEmpty(userDetails.fullYears)){
            str.append(" / ")
               .append(activity.getString(R.string.years))
               .append(" ")
               .append(userDetails.fullYears);
        }
        // где живем
        if(!TextUtils.isEmpty(userDetails.location))
            str.append("\n").append(userDetails.location);

        tvMiniDetails.setText(str.toString());
    }

    /**
     * Расчет прочитанного из аниме и манги
     * progress 1 = просмотренно
       progress 2 = запланированно + смотрю + просмотренно + отложено
       max size = запланированно + смотрю + просмотренно + отложено + брошено
     */
    private ProgressData getProgress(List<AnimeManga> list) {
        ProgressData progr = new ProgressData();
        for (AnimeManga animeManga : list) {
            progr.fullProgress += animeManga.counted;
            if(AnimeStatuses.COMPLETED.equals(animeManga.name))
                progr.firstProgress += animeManga.counted;
            if (!AnimeStatuses.DROPPED.equals(animeManga.name)
                    && !AnimeStatuses.REWATCHING.equals(animeManga.name))
                progr.secondProgress += animeManga.counted;
        }

        progr.percentage1 = progr.firstProgress * 100 / progr.fullProgress;
        progr.percentage2 = progr.secondProgress * 100 / progr.fullProgress;

        return progr;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivWebShow){
            h.launchUrlLink(activity, userDetails.website);
        }
    }

    class ProgressData{
        int firstProgress, secondProgress, fullProgress, percentage1, percentage2;
    }

//    public class URLDrawable extends BitmapDrawable {
//        // the drawable that you need to set, you could set the initial drawing
//        // with the loading image if you need to
//        protected Drawable drawable;
//
//        @Override
//        public void draw(Canvas canvas) {
//            // override the draw to facilitate refresh function later
//            if(drawable != null) {
//                drawable.draw(canvas);
//            }
//        }
//    }
//
//    Html.ImageGetter imgGetter3 = new Html.ImageGetter() {
//
//        public Drawable getDrawable(String source) {
//            final URLDrawable urlDrawable = new URLDrawable();
//            if (source.contains("missing_logo")){
//                source = ShikiApi.HTTP_SERVER + "/assets/globals/missing_original.jpg";
//            }
//            if (!source.contains("http")){
//                source = ShikiApi.HTTP_SERVER + source;
//            }
//
//            ImageLoader.getInstance().loadImage(source, new ImageLoadingListener() {
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    Drawable d = new BitmapDrawable(getResources(),loadedImage);
//                    urlDrawable.drawable = d;
//                    urlDrawable.drawable.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//                    ViewGroup parent = (ViewGroup) tvAbout.getParent();
//                    parent.invalidate();
//                    tvAbout.append("");
//                    tvAbout.invalidate();
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//
//                }
//            });
//
//            return urlDrawable;
//        }
//    };

}
