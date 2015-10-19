package org.shikimori.library.features.manga;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.features.manga.model.ItemMangaDetails;
import org.shikimori.library.fragments.base.AMDeatailsFragment;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.hs;

import ru.altarix.basekit.library.tools.h;

import static org.shikimori.library.tool.ProjectTool.TYPE.MANGA;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class MangaDeatailsFragment extends AMDeatailsFragment implements ExtraLoadInterface {

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
        if (activity == null)
            return;
        details = new ItemMangaDetails().create(res.getResultObject());
        prepareData();
    }

    @Override
    public ProjectTool.TYPE getType() {
        return MANGA;
    }

    private void prepareData() {

        h.setVisibleGone(imageFactory);
        if (details.id == null)
            return;
        // название аниме в карточке
        setTitleElement(details.russianName, details.name);
        // poster
        ImageLoader.getInstance().displayImage(details.image.original, ivPoster, addBlurToTitle);
        // description
        hs.setTextViewHTML(activity, tvReview, details.description_html, true);
        // rating
//        h.setTextViewHTML(activity, tvScore, activity.getString(R.string.rating) + ": " + details.score);
//        rbTitle.setRating(Float.parseFloat(details.score) / 2);
        rbTitle.setRating(Double.valueOf(details.score), details.scoreStats);

        // info list
        addInfo(R.string.type, getTypeTranslate(details.kind));
        addInfo(R.string.volumes, details.volumes);
        addInfo(R.string.chapters, details.chapters);
        addInfo(R.string.title_genres, TextUtils.join(", ", details.genres));
        addInfo(R.string.title_publishers, TextUtils.join(", ", details.publishers));

        // чего хотят пользователи
        buildStateWanted(details.ratesStatusesStats);

        llWrapAddList.setRate(details.id, details.userRate, MANGA);

        hs.setVisible(llWrapAddList);
        // status color and animation
        setStatus(details.anons, details.ongoing);
        // load comments
        if (activity instanceof ExtraLoadInterface)
            ((ExtraLoadInterface) activity).extraLoad(details.thread_id, null);

        getView().post(new Runnable() {
            @Override
            public void run() {
                svMain.scrollTo(0, 0);
            }
        });
    }

    @Override
    public void extraLoad(String itemId, Bundle params) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.ivPoster && details.image != null)
            activity.getAC().getThumbToImage().zoom(ivPoster, ProjectTool.fixUrl(details.image.original));
    }

    String getTypeTranslate(String type){
        switch (type){
            case "doujin": return activity.getString(R.string.doujin);
            case "manga": return activity.getString(R.string.manga);
            case "manhua": return activity.getString(R.string.manhua);
            case "manhwa": return activity.getString(R.string.manhwa);
            case "novel": return activity.getString(R.string.novel);
            case "one_shot": return activity.getString(R.string.one_shot);
            default: return type;
        }
    }

}
