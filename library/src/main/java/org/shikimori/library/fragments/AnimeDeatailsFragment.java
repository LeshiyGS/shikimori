package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.adapters.StudiosAdapter;
import org.shikimori.library.fragments.base.AMDeatailsFragment;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.interfaces.OnNewMenuListener;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemAnimeDetails;
import org.shikimori.library.objects.one.Studio;
import org.shikimori.library.tool.h;

import java.util.List;

import static org.shikimori.library.tool.ProjectTool.TYPE.ANIME;


/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class AnimeDeatailsFragment extends AMDeatailsFragment {

    private ItemAnimeDetails details;

    public static AnimeDeatailsFragment newInstance(Bundle b) {
        AnimeDeatailsFragment frag = new AnimeDeatailsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        details = new ItemAnimeDetails().createFromJson(res.getResultObject());
        prepareData();
    }

    private void prepareData() {

        if (details.id == null)
            return;

        activity.setTitle(details.name);
        // название аниме в карточке
        setTitleElement(details.russianName, details.name);
        // description
        h.setTextViewHTML(activity, tvReview, details.description_html);
        // rating
        h.setTextViewHTML(activity, tvScore, activity.getString(R.string.rating) + ": " + details.score);
        rbTitle.setRating(Float.parseFloat(details.score) / 2);
        // info

        addInfo(R.string.type, details.kind);
        addInfo(R.string.episodes, details.episodesAired + " / " + details.episodes);
        addInfo(R.string.title_time, details.duration + " " + activity.getString(R.string.min));


        addInfo(R.string.title_rating, details.rating);
        addInfo(R.string.title_genres, TextUtils.join(", ", details.genres));
        //addInfo(R.string.title_publishers, TextUtils.join(", ", details.studios));

        setStudios(R.string.title_studio, details.studios);
        buildStateWanted(details.ratesStatusesStats);

        setAddListName(details.userRate, ANIME);

        h.setVisible(llWrapAddList, true);

        if (activity instanceof ExtraLoadInterface)
            ((ExtraLoadInterface) activity).extraLoad(details.thread_id);

        // poster
        if(getView()!=null)
            getView().post(new Runnable() {
                @Override
                public void run() {

                    ImageLoader.getInstance().displayImage(details.image.original, ivPoster, addBlurToTitle);
                    setStatus(details.anons, details.ongoing);
                    svMain.scrollTo(0,0);
                }
            });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.ivPoster && details.image!=null)
            activity.getThumbToImage().zoom(ivPoster, details.image.original);
        else if(v.getId() == R.id.bAddToList){
            addToListPopup(v, R.menu.add_to_list_anime_menu, details.userRate, new OnNewMenuListener() {

                @Override
                public boolean onMenuItemClick(PopupMenu menu, MenuItem menuItem) {
                    if(menuItem.getItemId() == R.id.delete){
                        deleteRate(details.userRate.id, details.userRate, ANIME);
                        return true;
                    }
                    int position = getPosition(menuItem) + 1;
                    setRate(position, details.id, ANIME, details.userRate);

                    return false;
                }
            });
        } else if(v.getId() == R.id.bListSettings){

        }
    }

    /**
     * Set studios
     */
    protected void setStudios(int title, List<Studio> studions) {
        if(studions.size() > 0){
            tvMenuStudios.setText(title);
            h.setVisible(tvMenuStudios, true);
            h.setVisible(llStudios, true);
            llStudios.setAdapter(new StudiosAdapter(activity, studions));
            llStudios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    h.showMsg(activity, "test");
                }
            });
        } else {
            h.setVisibleGone(tvMenuStudios);
            h.setVisibleGone(llStudios);
        }
    }
}
