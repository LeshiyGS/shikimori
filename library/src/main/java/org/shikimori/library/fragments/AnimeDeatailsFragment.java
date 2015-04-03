package org.shikimori.library.fragments;

import android.os.Bundle;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.AMDeatailsFragment;
import org.shikimori.library.interfaces.UpdateCommentsListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemAnimeDetails;
import org.shikimori.library.objects.abstracts.AMDetails;
import org.shikimori.library.tool.h;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class AnimeDeatailsFragment extends AMDeatailsFragment{

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
        if(activity == null)
            return;
        animeDetails = ItemAnimeDetails.create(res.getResultObject());
        prepareData();
    }

    private void prepareData() {

        if(animeDetails.id == null)
            return;
        // название аниме в карточке
        setTitleElement(animeDetails.russianName, animeDetails.name);
        // poster
        ImageLoader.getInstance().displayImage(ShikiApi.HTTP_SERVER + animeDetails.imgOriginal, ivPoster);
        // description
        h.setTextViewHTML(activity, tvReview, animeDetails.description_html);
        // rating
        h.setTextViewHTML(activity, tvScore, activity.getString(R.string.rating)+ ": " + animeDetails.score);
        rbTitle.setRating(Float.parseFloat(animeDetails.score) / 2);
        // info

        StringBuilder builder = new StringBuilder();
        builder.append("<b>").append(activity.getString(R.string.type)).append("</b> ")
                .append(animeDetails.kind).append("<br/>")
                .append("<b>").append(activity.getString(R.string.episodes)).append("</b> ")
                .append(animeDetails.episodesAired).append(" / ").append(animeDetails.episodes).append("<br/>")
                .append("<b>").append(activity.getString(R.string.title_time)).append("</b> ")
                .append(animeDetails.duration).append(" ").append(activity.getString(R.string.min)).append("<br/>")
                .append("<b>").append(activity.getString(R.string.title_status)).append("</b> ")
                .append(getStatus(animeDetails.anons, animeDetails.anons)).append("<br/>")
                .append("<b>").append(activity.getString(R.string.title_rating)).append("</b> ")
                .append(animeDetails.rating).append("<br/>")
                .append("<b>").append(activity.getString(R.string.title_genres)).append("</b> ")
                .append(TextUtils.join(", ", animeDetails.genres)).append("<br/>")
                .append("<b>").append(activity.getString(R.string.title_publishers)).append("</b> ")
                .append(TextUtils.join(", ", animeDetails.studios));

        h.setTextViewHTML(activity, tvInfo, builder.toString());

        if(activity instanceof UpdateCommentsListener)
            ((UpdateCommentsListener) activity).startLoadComments(animeDetails.thread_id);

    }
}
