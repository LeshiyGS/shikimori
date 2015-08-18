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

import org.shikimori.library.R;
import org.shikimori.library.custom.ExpandableHeightGridView;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
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

/**
 * Created by Владимир on 17.04.2015.
 */
public class ClubDetailsFragment extends PullableFragment<BaseKitActivity<ShikiAC>> implements Query.OnQuerySuccessListener, View.OnClickListener {

    TextView tvTitle;
    ImageView ivPoster;
    ViewGroup llInfo,tvReview;
    View tvAnimes, tvMangas;
    ScrollView svMain;
    ExpandableHeightGridView pageAnime, pageManga;
    private String itemId;
    private ItemClubDescriptionShiki item;
    private BodyBuild bodyBuilder;
    private View iLoader, bImages;
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
        tvReview  = find(R.id.llReview);
        ivPoster  = find(R.id.ivPoster);
        iLoader  = find(R.id.iLoader);
        bImages  = find(R.id.bImages);
        webView  = find(R.id.wvWeb);

        ivPoster.setOnClickListener(this);
        bImages.setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArgiments();
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

        activity.setTitle(item.name);

//        bodyBuilder.parceAsync(item.descriptionHtml, new BodyBuild.ParceDoneListener() {
//            @Override
//            public void done(ViewGroup view) {
//                if (activity == null || getView() == null)
//                    return;
//                hs.setVisibleGone(iLoader);
//                tvReview.removeAllViews();
//                tvReview.addView(view);
//                bodyBuilder.loadPreparedImages();
//                svMain.scrollTo(0,0);
//            }
//        });

        loadHtml(item.descriptionHtml);

        if(item.original!=null)
            ShikiImage.show(item.original, ivPoster);

        if (activity instanceof ExtraLoadInterface)
            ((ExtraLoadInterface) activity).extraLoad(item.threadId);
    }

    private void loadHtml(String html){
//        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
        }
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
//                webView.getSettings().setUseWideViewPort(true);  слишком большое растягивание
//        webView.getSettings().setSupportMultipleWindows(true);
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if (linksToBrowser && isUrlAllowedForLoad(url)) {
                Log.d("weblink", "" + url);
                if(url.contains("image=1")){
                    activity.getAC().getThumbToImage().showInActivity(url);
                    return true;
                }
                LinkHelper.goToUrl(activity, url);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(intent);
                return true;
//                }
//
//                if (pageListener != null)
//                    if (pageListener.onPageFinished(view, url))
//                        return true;
//                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (activity != null)
                    activity.getLoaderController().hide();
                hs.setVisibleGone(iLoader);

//                String javascript="javascript: document.getElementsByClassName('prgrph').innerHTML='Hello WORLD!';";
//                view.loadUrl(javascript);
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
        if(v.getId() == R.id.ivPoster){
            if(item.original!=null)
                activity.getAC().getThumbToImage().zoom(ivPoster, ProjectTool.fixUrl(item.original));
        } else if (v.getId() == R.id.bImages){
            activity.getPageController()
                    .addParam(Constants.CUSTOM_URL, ShikiApi.getUrl(ShikiPath.CLUB_IMAGES, itemId))
                    .startActivity(ScreenShootsFragment.class, itemId);
        }
    }
}
