package org.shikimori.library.features.anime;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.adapters.StudiosAdapter;
import org.shikimori.library.fragments.ScreenShootsFragment;
import org.shikimori.library.fragments.base.AMDeatailsFragment;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.features.anime.model.ItemAnimeDetails;
import org.shikimori.library.objects.one.Studio;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.UpdateApp;
import org.shikimori.library.tool.hs;

import java.util.List;

import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.basekit.library.tools.DialogCompat;
import ru.altarix.basekit.library.tools.h;

import static org.shikimori.library.tool.ProjectTool.TYPE.ANIME;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class AnimeDeatailsFragment extends AMDeatailsFragment implements BaseKitActivity.OnFragmentBackListener {

    private ItemAnimeDetails details;
    public static boolean UPDATE_AUTO_SERIES = true;

    public static AnimeDeatailsFragment newInstance(Bundle b) {
        AnimeDeatailsFragment frag = new AnimeDeatailsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public String getPatch() {
        return ShikiPath.ANIMES_ID;
    }


    @Override
    public ProjectTool.TYPE getType() {
        return ANIME;
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        super.onQuerySuccess(res);
        if (activity == null)
            return;
        details = new ItemAnimeDetails().createFromJson(res.getResultObject());
        prepareData();
    }

    @Override
    protected void beforeLoadDate() {
        super.beforeLoadDate();
        imageFactory.setZoom(true);
        imageFactory.setRightOffset(.3f);
        imageFactory.setVisibilityPagging(false);
        InitScreenShootMoreBtn();
        activity.setOnFragmentBackListener(this);
    }

    private void prepareData() {

        if (details.id == null)
            return;

        hs.setVisible(!details.anons, fbPlay);

        if (ProjectTool.isFullVersion()) {
            hs.setVisible(fbPlay);
        }

        activity.setTitle(details.name);
        // название аниме в карточке
        setTitleElement(details.russianName, details.name);
        // description
        hs.setTextViewHTML(activity, tvReview, details.description_html, true);
        // rating
//        h.setTextViewHTML(activity, tvScore, activity.getString(R.string.rating) + ": " + details.score);
//        rbTitle.setRating(Float.parseFloat(details.score) / 2);
        rbTitle.setRating(Double.valueOf(details.score), details.scoreStats);
        // info

        addInfo(R.string.type, getTypeTranslate(details.kind));
        addInfo(R.string.episodes, details.episodesAired + " / " + details.episodes);
        addInfo(R.string.title_time, details.duration + " " + activity.getString(R.string.min));


        addInfo(R.string.title_rating, getRaitingTranslate(details.rating));
        addInfo(R.string.title_genres, TextUtils.join(", ", details.genres));
        //addInfo(R.string.title_publishers, TextUtils.join(", ", details.studios));

        setStudios(details.studios);
        buildStateWanted(details.ratesStatusesStats);

        hs.setVisible(llWrapAddList);
        llWrapAddList.setRate(details.id, details.userRate, ANIME);
        llWrapAddList.setEpisodes(details.episodes);

        setImages();

        if (activity instanceof ExtraLoadInterface)
            ((ExtraLoadInterface) activity).extraLoad(details.thread_id, null);

        // poster
        if (getView() != null)
            getView().post(new Runnable() {
                @Override
                public void run() {

                    ImageLoader.getInstance().displayImage(details.image.original, ivPoster, addBlurToTitle);
                    setStatus(details.anons, details.ongoing);
                    svMain.scrollTo(0, 0);
                }
            });
    }

    private void setImages() {
        if (details.screenshots != null && details.screenshots.size() > 0) {
            imageFactory.setList(details.screenshots);
        } else
            h.setVisibleGone(imageFactory);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.ivPoster && details.image != null) {
            activity.getAC().getThumbToImage().zoom(ivPoster, ProjectTool.fixUrl(details.image.original));
        } else if (v.getId() == R.id.fbPlay) {
            if (ProjectTool.isFullVersion()) {
                if (!hs.appInstalledOrNot(activity, "com.videogars.anime")) {
                    instalAnibreakDialog(R.string.downloadanibreak);
                } else {
                    int versionAniBreak = hs.appVersionCode(activity, "com.videogars.anime");
                    if (versionAniBreak < 402159) {
                        instalAnibreakDialog(R.string.updateanibreak);
                        return;
                    }

                    Intent intent = new Intent();
                    intent.setData(Uri.parse("anibreakUrl://video?anime_shiki_id=" + itemId));
                    intent.putExtra("shiki_user_name", activity.getAC().getShikiUser().getNickname());
                    intent.putExtra("shiki_user_token", ShikiUser.getToken());
                    intent.putExtra("serie_name", String.valueOf(llWrapAddList.getRateUser().episodes));
                    intent.putExtra("shiki_anime_name", details.name);
                    intent.putExtra("shiki_anime_name_rus", details.russianName);
                    if (UPDATE_AUTO_SERIES)
                        intent.putExtra("shiki_anime_rate_id", details.userRate.id);

                    try {
                        startActivityForResult(intent, 777);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private void InitScreenShootMoreBtn() {
        View v = activity.getLayoutInflater().inflate(R.layout.item_more_btn, null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getPageController()
                        .startActivity(ScreenShootsFragment.class, itemId);
            }
        });
        imageFactory.setEndView(v);
    }

    void instalAnibreakDialog(int str) {
        new DialogCompat(activity)
                .setPositiveListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startLoadAniBreak();
                    }
                })
                .showConfirm(activity.getString(str));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 777) {
            Log.d("shikiresult", "777");
            startRefresh();
        }
    }

    private void startLoadAniBreak() {
        activity.getAC().getLoaderController().show();
        new UpdateApp(activity)
                .setProgresListener(new UpdateApp.UpdateApkProgressListener() {
                    @Override
                    public void update(int progress) {

                    }

                    @Override
                    public void finish(String patch) {
                        activity.getAC().getLoaderController().hide();
                    }
                }).startLoad(activity.getString(R.string.anibreak_url) + "uploads/app-release.apk");
    }

    /**
     * Set studios
     */
    protected void setStudios(List<Studio> studions) {
        if (studions.size() > 0) {
            hs.setVisible(llStudios);
            llStudios.setAdapter(new StudiosAdapter(activity, studions));
            llStudios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    h.showMsg(activity, "test");
                }
            });
        } else {
            hs.setVisibleGone(llStudios);
        }
    }

    private String getTypeTranslate(String type) {
        if (type == null)
            return activity.getString(R.string.unknow);
        switch (type) {
            case "tv":
                return activity.getString(R.string.tv);
            case "movie":
                return activity.getString(R.string.movies);
            case "special":
                return activity.getString(R.string.special);
            case "music":
                return activity.getString(R.string.music);
            default:
                return type;
        }
    }

    private String getRaitingTranslate(String type) {
        if(type == null)
            return activity.getString(R.string.rating_unknown);
        switch (type) {
            case "g":
                return activity.getString(R.string.rating_g);
            case "pg":
                return activity.getString(R.string.rating_pg);
            case "pg_13":
                return activity.getString(R.string.rating_pg_13);
            case "r":
                return activity.getString(R.string.rating_r);
            case "r_plus":
                return activity.getString(R.string.rating_r_plus);
            case "rx":
                return activity.getString(R.string.rating_rx);
            default:
                return type;
        }
    }

    @Override
    public boolean onBackPressed() {
        return imageFactory.closeImage();
    }
}
