package org.shikimori.client.activity.base;

import android.os.Bundle;
import android.text.TextUtils;

import org.shikimori.client.activity.DrawerActivity;
import org.shikimori.client.adapters.DrawerAdapter;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by Владимир on 31.03.2015.
 */
public abstract class BaseDetailsActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimeTitle();
    }

    private void setAnimeTitle() {
        Bundle b = getIntent().getExtras();
        if(b==null)
            return;
        String title = b.getString(Constants.ITEM_NAME);
        if(!TextUtils.isEmpty(title))
            setTitle(title);
    }

    @Override
    public void initDrawer() {
        super.initDrawer();
        mDrawerAdapter.setSelected(DrawerAdapter.NON_SELECTED);
    }
}
