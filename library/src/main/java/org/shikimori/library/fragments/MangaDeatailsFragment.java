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
import org.shikimori.library.objects.ItemMangaDetails;
import org.shikimori.library.objects.abstracts.AMDetails;
import org.shikimori.library.tool.h;

import ru.altarix.ui.tool.TextStyling;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class MangaDeatailsFragment extends AMDeatailsFragment{

    private ItemMangaDetails details;

    public static MangaDeatailsFragment newInstance(Bundle b) {
        MangaDeatailsFragment frag = new MangaDeatailsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public String getPatch() {
        return ShikiPath.MANGAS_ID;
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        super.onQuerySuccess(res);
        if(activity == null)
            return;
        details = ItemMangaDetails.create(res.getResultObject());
        prepareData();
    }

    private void prepareData() {

        if(details.id == null)
            return;
        // название аниме в карточке
        setTitleElement(details.russianName, details.name);
        ImageLoader.getInstance().displayImage(ShikiApi.HTTP_SERVER + details.imgOriginal, ivPoster);
        h.setTextViewHTML(activity, tvReview, details.description_html);
        h.setTextViewHTML(activity, tvScore, activity.getString(R.string.rating) + ": " + details.score);
        rbTitle.setRating(Float.parseFloat(details.score) / 2);


        addInfo(R.string.type, details.kind);
        addInfo(R.string.volumes, details.volumes);
        addInfo(R.string.chapters, details.chapters);
        addInfo(R.string.title_status, getStatus(details.anons, details.ongoing));
        addInfo(R.string.title_genres, TextUtils.join(", ", details.genres));
        addInfo(R.string.title_publishers, TextUtils.join(", ", details.publishers));

        if(activity instanceof UpdateCommentsListener)
            ((UpdateCommentsListener) activity).startLoadComments(details.thread_id);

    }
}