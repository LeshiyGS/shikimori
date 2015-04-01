package org.shikimori.client.activity;

import android.content.Context;
import android.content.SharedPreferences;

import org.shikimori.client.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by me on 30.03.2015.
 */
public class ProjectActivity extends BaseActivity{

    @Override
    protected int getLayoutId() {
        return R.layout.view_simple_content;
    }

    @Override
    public SharedPreferences getSettings() {
        return getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
    }

    @Override
    public Query prepareQuery(boolean separate) {
        if(separate){
            Query q = new Query(this);
            q.setLoader(query.getLoader());
            return q;
        }

        return query;
    }
}
