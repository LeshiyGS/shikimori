package org.shikimori.library.activity;

import org.shikimori.library.R;
import org.shikimori.library.tool.controllers.ShikiAC;

import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.basekit.library.tools.LoaderController;

/**
 * Created by Владимир on 27.08.2014.
 */
public class BaseActivity extends BaseKitActivity<ShikiAC> {

    @Override
    public ShikiAC initActivityController() {
        return new ShikiAC(this);
    }

    /**
     * Custom loader
     *
     * @return
     */
    public LoaderController getLoaderController() {
        return getActivityController().getQuery().getLoader();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.basekit_simple_activity;
    }


    @Override
    public void onBackPressed() {
        if(getAC().getThumbToImage().closeImage())
            return;

        super.onBackPressed();

    }
}
