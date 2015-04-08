package org.shikimori.library.fragments;

import android.os.Bundle;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.AMDeatailsFragment;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemAnimeDetails;
import org.shikimori.library.tool.h;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class AnimeDeatailsFragment extends AMDeatailsFragment {

    private ItemAnimeDetails animeDetails;

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
    public void onQuerySuccess(StatusResult res) {
        super.onQuerySuccess(res);
        if (activity == null)
            return;
        animeDetails = ItemAnimeDetails.create(res.getResultObject());
        prepareData();
    }

    private void prepareData() {

        if (animeDetails.id == null)
            return;
        // название аниме в карточке
        setTitleElement(animeDetails.russianName, animeDetails.name);
        // description
        h.setTextViewHTML(activity, tvReview, animeDetails.description_html);
        // rating
        h.setTextViewHTML(activity, tvScore, activity.getString(R.string.rating) + ": " + animeDetails.score);
        rbTitle.setRating(Float.parseFloat(animeDetails.score) / 2);
        // info

        addInfo(R.string.type, animeDetails.kind);
        addInfo(R.string.episodes, animeDetails.episodesAired + " / " + animeDetails.episodes);
        addInfo(R.string.title_time, animeDetails.duration + " " + activity.getString(R.string.min));


        addInfo(R.string.title_rating, animeDetails.rating);
        addInfo(R.string.title_genres, TextUtils.join(", ", animeDetails.genres));
        addInfo(R.string.title_publishers, TextUtils.join(", ", animeDetails.studios));

        if (activity instanceof ExtraLoadInterface)
            ((ExtraLoadInterface) activity).extraLoad(animeDetails.thread_id);

        // poster
        getView().post(new Runnable() {
            @Override
            public void run() {
                ImageLoader.getInstance().displayImage(animeDetails.imgOriginal, ivPoster, addBlurToTitle);
                setStatus(animeDetails.anons, animeDetails.ongoing);
            }
        });
    }

}
