package org.shikimori.library.tool.controllers;

import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.imagetool.ThumbToImage;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import ru.altarix.basekit.library.activity.ActivityController;
import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.basekit.library.tools.LoaderController;

/**
 * Created by Владимир on 29.07.2015.
 */
public class ShikiAC<T extends BaseKitActivity> extends ActivityController<T> {

    private Query query;
    private ShikiUser shikiUser;
    private ThumbToImage thumbToImage;
    private BodyBuild boddyBuild;

    public ShikiAC(T activity) {
        super(activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        query = new Query(activity);
        query.setLoader(new LoaderController(activity));
        shikiUser = new ShikiUser(activity);
        thumbToImage = new ThumbToImage(activity);
    }

    public Query getQuery() {
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
