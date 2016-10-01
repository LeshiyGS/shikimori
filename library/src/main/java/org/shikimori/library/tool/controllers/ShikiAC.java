package org.shikimori.library.tool.controllers;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;

import org.shikimori.library.R;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import com.mcgars.imagefactory.ThumbToImage;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import ru.altarix.basekit.library.activities.ActivityController;
import ru.altarix.basekit.library.activities.BaseKitActivity;
import ru.altarix.basekit.library.tools.LoaderController;
import ru.altarix.basekit.library.tools.h;

/**
 * Created by Владимир on 29.07.2015.
 */
public class ShikiAC<T extends BaseKitActivity> extends ActivityController<T> {

    private QueryShiki query;
    private ShikiUser shikiUser;
    private ThumbToImage thumbToImage;
    private BodyBuild boddyBuild;

    public ShikiAC(){

    }

    public ShikiAC(T activity) {
        super(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
//        if(toolbar!=null)
//            activity.setSupportActionBar(toolbar);



        query = new QueryShiki(activity);
        query.setLoader(new LoaderController(activity));
        shikiUser = new ShikiUser(activity);
        thumbToImage = new ThumbToImage(activity);
        stylingTabs();
    }

    private void stylingTabs() {
        int color = ContextCompat.getColor(activity, R.color.black_owerlay_80);
        int colorDef = ContextCompat.getColor(activity, R.color.black_owerlay_40);
        activity.getTabs().setTextColors(color, colorDef);
        activity.getTabs().setSelectedIndicatorColors(h.getColor(activity, R.attr.colorAccent));
    }

    public QueryShiki getQuery() {
        return query;
    }

    public ThumbToImage getThumbToImage() {
        return thumbToImage;
    }

    public ShikiUser getShikiUser() {
        return shikiUser;
    }

    /**
     * Custom loader
     *
     * @return
     */
    public LoaderController getLoaderController() {
        return query.getLoader();
    }

    @Override
    public boolean onBackPressed() {
        if (thumbToImage.closeImage())
            return true;
        return super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    public BodyBuild getBodyBuilder() {
        if (boddyBuild == null)
            boddyBuild = ProjectTool.getBodyBuilder(activity, BodyBuild.CLICKABLETYPE.INTEXT);
        return boddyBuild;
    }
}
