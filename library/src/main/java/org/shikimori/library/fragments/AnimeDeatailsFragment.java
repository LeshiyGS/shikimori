package org.shikimori.library.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.adapters.StudiosAdapter;
import org.shikimori.library.fragments.base.AMDeatailsFragment;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.ItemAnimeDetails;
import org.shikimori.library.objects.one.RatesStatusesStats;
import org.shikimori.library.objects.one.Studio;
import org.shikimori.library.tool.h;

import java.util.List;


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
        animeDetails = ItemAnimeDetails.create(res.getResultObject());
        prepareData();
    }

    private void prepareData() {

        if (animeDetails.id == null)
            return;

        activity.setTitle(animeDetails.name);
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
        //addInfo(R.string.title_publishers, TextUtils.join(", ", animeDetails.studios));

        setStudios(R.string.title_studio, animeDetails.studios);
        buildStateWanted(animeDetails.ratesStatusesStats);

        if (activity instanceof ExtraLoadInterface)
            ((ExtraLoadInterface) activity).extraLoad(animeDetails.thread_id);

        // poster
        if(getView()!=null)
            getView().post(new Runnable() {
                @Override
                public void run() {

                    ImageLoader.getInstance().displayImage(animeDetails.image.original, ivPoster, addBlurToTitle);
                    setStatus(animeDetails.anons, animeDetails.ongoing);
                    svMain.scrollTo(0,0);
                }
            });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.ivPoster && animeDetails.image!=null)
            activity.getThumbToImage().zoom(ivPoster, animeDetails.image.original);
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
