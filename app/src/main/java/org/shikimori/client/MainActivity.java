package org.shikimori.client;

import android.os.Bundle;

import org.shikimori.client.activity.DrawerActivity;
import org.shikimori.client.adapters.DrawerAdapter;


/**
 * Created by Владимир on 20.06.2014.
 */
public class MainActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage(DrawerAdapter.DRAWER_MENU_PROFILE_ID);
        setDowbleBackPressetToExit(true);
    }

    @Override
    public void initDrawer() {
        super.initDrawer();
        mDrawerAdapter.setSelected(0);
    }
}
