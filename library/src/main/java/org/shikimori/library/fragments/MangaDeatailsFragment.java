package org.shikimori.library.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.AMDeatailsFragment;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.interfaces.OnNewMenuListener;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemMangaDetails;
import org.shikimori.library.objects.one.UserRate;
import org.shikimori.library.tool.h;

import static org.shikimori.library.tool.ProjectTool.TYPE.ANIME;
import static org.shikimori.library.tool.ProjectTool.TYPE.MANGA;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class MangaDeatailsFragment extends AMDeatailsFragment implements ExtraLoadInterface, AddRateDialogFragment.ControllListenerRate {

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
        details = new ItemMangaDetails().createFromJson(res.getResultObject());
        prepareData();
    }

    private void prepareData() {

        if (details.id == null)
            return;
        // название аниме в карточке
        setTitleElement(details.russianName, details.name);
        // poster
        ImageLoader.getInstance().displayImage(details.image.original, ivPoster, addBlurToTitle);
        // description
        h.setTextViewHTML(activity, tvReview, details.description_html);
        // rating
        h.setTextViewHTML(activity, tvScore, activity.getString(R.string.rating) + ": " + details.score);
        rbTitle.setRating(Float.parseFloat(details.score) / 2);

        // info list
        addInfo(R.string.type, details.kind);
        addInfo(R.string.volumes, details.volumes);
        addInfo(R.string.chapters, details.chapters);
        addInfo(R.string.title_genres, TextUtils.join(", ", details.genres));
        addInfo(R.string.title_publishers, TextUtils.join(", ", details.publishers));

        // hide studio block
        h.setVisibleGone(tvMenuStudios);
        h.setVisibleGone(llStudios);
        // чего хотят пользователи
        buildStateWanted(details.ratesStatusesStats);

        setAddListName(details.userRate, MANGA);

        h.setVisible(llWrapAddList, true);
        // status color and animation
        setStatus(details.anons, details.ongoing);
        // load comments
        if (activity instanceof ExtraLoadInterface)
            ((ExtraLoadInterface) activity).extraLoad(details.thread_id);

        getView().post(new Runnable() {
            @Override
            public void run() {
                svMain.scrollTo(0, 0);
            }
        });
    }

    @Override
    public void extraLoad(String itemId) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.ivPoster && details.image != null)
            activity.getThumbToImage().zoom(ivPoster, details.image.original);
        else if (v.getId() == R.id.bAddToList) {
            addToListPopup(v, R.menu.add_to_list_manga_menu, details.userRate, new OnNewMenuListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    setRate(item.getItemId(), details.id, MANGA, details.userRate);
                    return true;
                }
            });
        } else if (v.getId() == R.id.bListSettings) {
            AddRateDialogFragment frag = AddRateDialogFragment.newInstance();
            frag.setUpdateListener(this);
            frag.setType(MANGA);
            frag.show(activity.getFragmentManagerLocal(), "");
        }
    }

    @Override
    public UserRate getRateUser() {
        return details.userRate;
    }

    @Override
    public void updateRateFromDialog() {
        setRate(details.id, MANGA, details.userRate);
        setAddListName(details.userRate, MANGA);
    }
}
