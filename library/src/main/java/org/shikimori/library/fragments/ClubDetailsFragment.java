package org.shikimori.library.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mcgars.imagefactory.cutomviews.ImageFactoryView;

import org.shikimori.library.R;
import org.shikimori.library.custom.ExpandableHeightGridView;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.BaseQuery;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.ItemClubDescriptionShiki;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.LinkHelper;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.basekit.library.tools.h;
import ru.altarix.basekit.library.tools.pagecontroller.PageController;

/**
 * Created by Владимир on 17.04.2015.
 */
public class ClubDetailsFragment extends PullableFragment<BaseKitActivity<ShikiAC>> implements Query.OnQuerySuccessListener, View.OnClickListener, BaseKitActivity.OnFragmentBackListener {

    TextView tvTitle,tvMenuImages;
    ImageView ivPoster;
    ViewGroup llInfo,tvReview;
    View bManga, bAnime, bCharacter;
    ScrollView svMain;
    ExpandableHeightGridView pageAnime, pageManga;
    private String itemId;
    private ItemClubDescriptionShiki item;
    private BodyBuild bodyBuilder;
    private View iLoader;
    ImageFactoryView imageFactory;
    private WebView webView;

    public static ClubDetailsFragment newInstance(Bundle b) {
        ClubDetailsFragment frag = new ClubDetailsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_deatales_club, null);
        setBaseView(v);
        svMain   = find(R.id.svMain);
        tvTitle   = find(R.id.tvTitle);
        tvMenuImages   = find(R.id.tvMenuImages);
        tvReview  = find(R.id.llReview);
        ivPoster  = find(R.id.ivPoster);
        iLoader  = find(R.id.iLoader);
        imageFactory  = find(R.id.imageFactory);
        webView  = find(R.id.wvWeb);
        bAnime  = find(R.id.bAnime);
        bManga  = find(R.id.bManga);
        bCharacter  = find(R.id.bCharacter);

        ivPoster.setOnClickListener(this);
        bAnime.setOnClickListener(this);
        bManga.setOnClickListener(this);
        bCharacter.setOnClickListener(this);
        return v;
    }

    private void initImageFactory(){
        imageFactory.setZoom(true);
        imageFactory.setRightOffset(.3f);
        imageFactory.setVisibilityPagging(false);
        InitScreenShootMoreBtn();
        activity.setOnFragmentBackListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArgiments();
        initImageFactory();
        showRefreshLoader();
        bodyBuilder = ProjectTool.getBodyBuilder(activity, BodyBuild.CLICKABLETYPE.INTEXT);
        loadData();
    }

    String getUrl(){
        return ShikiApi.getUrl(ShikiPath.CLUB_ID, itemId);
    }

    @Override
    public void onStartRefresh() {
        getFC().getQuery().invalidateCache(getUrl());
        hs.setVisible(iLoader);
        loadData();
    }

    void loadData(){
        getFC().getQuery().init(getUrl())
                .setCache(true, Query.HOUR)
                .getResult(this);
    }

    private void initArgiments() {
        itemId = getParam(Constants.ITEM_ID);
    }

    @Override
    public int pullableViewId() {
        return R.id.svMain;
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        item = new ItemClubDescriptionShiki().create(res.getResultObject());

        h.setVisibleGone(!item.isAnimeExist(), bAnime);
        h.setVisibleGone(!item.isMangaExist(), bManga);
        h.setVisibleGone(!item.isMangaExist(), bCharacter);

        setImages();
        activity.setTitle(item.name);
        loadHtml(item.descriptionHtml);
        if(item.original!=null)
            ShikiImage.show(item.original, ivPoster);

        if (activity instanceof ExtraLoadInterface){
            Bundle b = new Bundle();
            b.putString(Constants.ROLE_CLUB, item.user_role);
            ((ExtraLoadInterface) activity).extraLoad(item.threadId, b);
        }
    }

    private void setImages() {
        if (item.getImages() != null && item.getImages().size() > 0) {
            h.setVisible(imageFactory,tvMenuImages);
            imageFactory.setList(item.getImages());
        } else
            h.setVisibleGone(imageFactory,tvMenuImages);
    }

    private void InitScreenShootMoreBtn() {
        View v = activity.getLayoutInflater().inflate(R.layout.item_more_btn, null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getPageController()
                        .addParam(Constants.CUSTOM_URL, ShikiApi.getUrl(ShikiPath.CLUB_IMAGES, itemId))
                        .startActivity(ScreenShootsFragment.class, itemId);
            }
        });
        imageFactory.setEndView(v);
    }

    private void loadHtml(String html){
        webView.getSettings().setJavaScriptEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
        }
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("weblink", "" + url);
                if(url.contains("image=1")){
                    activity.getAC().getThumbToImage().showInActivity(url);
                    return true;
                }
                LinkHelper.goToUrl(activity, url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (activity != null)
                    activity.getLoaderController().hide();
                hs.setVisibleGone(iLoader);
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("<HTML><HEAD>");
        sb.append("<LINK href=\"file:///android_asset/application.css\" type=\"text/css\" rel=\"stylesheet\"/>");
        sb.append("<script src=\"file:///android_asset/jquery-2.1.4.min.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"file:///android_asset/shiki.js\" type=\"text/javascript\"></script>");
//        sb.append("<script src=\"http://shikimori.org/assets/core-e3c7f5a04d70fc73396d0a5804875f62.js\" type=\"text/javascript\"></script>");
//        sb.append("<script src=\"http://shikimori.org/assets/application-b86673bc249423153ae7c934f538e43a.js\" type=\"text/javascript\"></script>");
        sb.append("</HEAD><body>");
        sb.append(html);
        sb.append("</body></HTML>");

        webView.loadDataWithBaseURL(null,
                sb.toString(),
                "text/html", "utf-8", null);
    }


    @Override
    public void onClick(View v) {
        String customUrl = null;
        if(v.getId() == R.id.ivPoster){
            if(item.original!=null)
                activity.getAC().getThumbToImage().zoom(ivPoster, ProjectTool.fixUrl(item.original));
            return;
        }

        PageController pc = activity.getPageController();

        if (v.getId() == R.id.bAnime){
            pc.setTitle(R.string.anime);
            customUrl = ShikiPath.CLUB_ANIME;
        }else if (v.getId() == R.id.bManga){
            pc.setTitle(R.string.manga);
            customUrl = ShikiPath.CLUB_MANGA;
        } else if (v.getId() == R.id.bCharacter) {
            pc.setTitle(R.string.characters);
            customUrl = ShikiPath.CLUB_CHACTERS;
        }
        pc.startActivity(LinkedListClubFragment.class, ShikiApi.getUrl(customUrl, item.id));
    }

    @Override
    public boolean onBackPressed() {
        if(imageFactory.closeImage())
            return true;
        return false;
    }
}
