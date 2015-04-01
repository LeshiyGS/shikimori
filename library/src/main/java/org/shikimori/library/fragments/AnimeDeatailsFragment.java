package org.shikimori.library.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.interfaces.UpdateCommentsListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.AnimeDetails;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.h;


/**
 * Created by Владимир on 31.03.2015.
 */
public class AnimeDeatailsFragment extends PullableFragment<BaseActivity> implements Query.OnQuerySuccessListener{

    private String animeId;
    ScrollView svMain;
    TextView tvTitle, tvInfo, tvScore, tvReview;
    ImageView ivPoster;
    RatingBar rbTitle;

    private AnimeDetails animeDetails;

    private ObjectBuilder builder;

    public static AnimeDeatailsFragment newInstance(Bundle b) {
        AnimeDeatailsFragment frag = new AnimeDeatailsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_anime_deatales, null);
        svMain = (ScrollView) v.findViewById(R.id.svMain);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvInfo = (TextView) v.findViewById(R.id.tvInfo);
        tvScore = (TextView) v.findViewById(R.id.tvMenuScore);
        tvReview = (TextView) v.findViewById(R.id.tvReview);
        ivPoster = (ImageView) v.findViewById(R.id.ivPoster);
        rbTitle = (RatingBar) v.findViewById(R.id.rbTitle);
        return v;
    }

    @Override
    public int pullableViewId() {
        return R.id.svMain;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArgiments();
        showRefreshLoader();
        loadAnimeInfo();
    }

    @Override
    public void onStartRefresh() {
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.ANIMES_ID + animeId));
        loadAnimeInfo();
    }

    private void loadAnimeInfo() {
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.ANIMES_ID) + animeId);
        loadDataFromServer();
    }

    void loadDataFromServer(){
        query.init(ShikiApi.getUrl(ShikiPath.ANIMES_ID)+animeId)
                .setCache(true, Query.HOUR)
                .getResult(this);

    }

    private void initArgiments() {
        Bundle b = getArguments();
        if(b == null)
            return;

        animeId = getArguments().getString(Constants.ANIME_ID);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        if(activity == null)
            return;
        stopRefresh();
        animeDetails = AnimeDetails.create(res.getResultObject());
        prepareData();
    }


    private void prepareData() {
        tvTitle.setText(animeDetails.name.toString() + " / " + animeDetails.russian);
        ImageLoader.getInstance().displayImage(ShikiApi.HTTP_SERVER + animeDetails.img_original, ivPoster);
        h.setTextViewHTML(getActivity(), tvInfo, "<b>Тип: </b>" + animeDetails.kind + "<br>" +
                "<b>Эпизоды: </b>" + animeDetails.episodes + "<br>" +
                "<b>Длительность эпизода: </b>" + animeDetails.duration + " минуты<br>" +
                "<b>Статус: </b>" + animeDetails.aired_on + "<br>" +
                "<b>Рейтинг: </b>" + animeDetails.rating + "<br>" +
                "<b>Жанры: </b>" + animeDetails.aired_on + "<br>" +
                "<b>Студии: </b>" + animeDetails.aired_on);
        h.setTextViewHTML(getActivity(), tvReview, animeDetails.description_html);
        h.setTextViewHTML(getActivity(), tvScore, "Оценка: " + animeDetails.score);
        rbTitle.setRating(Float.parseFloat(animeDetails.score) / 2);
        // exemple
        String.format(activity.getString(R.string.episodes), animeDetails.episodes + "\n");


        if(activity instanceof UpdateCommentsListener)
            ((UpdateCommentsListener) activity).startLoadComments(animeDetails.thread_id);

    }
}
