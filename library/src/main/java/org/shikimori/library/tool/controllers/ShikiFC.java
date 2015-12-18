package org.shikimori.library.tool.controllers;

import android.os.Bundle;

import org.shikimori.library.fragments.base.abstracts.BaseFragment;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.constpack.Constants;

import ru.altarix.basekit.library.fragment.BaseKitFragment;
import ru.altarix.basekit.library.fragment.FragmentController;

/**
 * Created by Владимир on 29.07.2015.
 */
public class ShikiFC extends FragmentController<BaseKitFragment> {
    private ShikiAC shikiAC;
    private QueryShiki query;
    private String userId;

    public ShikiFC(BaseFragment tBaseFragment, ShikiAC shikiAC) {
        super(tBaseFragment);
        this.shikiAC = shikiAC;
    }

    @Override
    public void onCreateActivity(Bundle savedInstanceState) {
        super.onCreateActivity(savedInstanceState);
        query = shikiAC.getQuery();
        userId = (String) fragment.getParam(Constants.USER_ID, ShikiUser.USER_ID);
    }

    public QueryShiki getQuery() {
        return query;
    }

    public void showLoader(){
        query.getLoader().show();
    }
    public void hideLoader(){
        query.getLoader().hide();
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (query != null)
            query.onStop();
    }
}
