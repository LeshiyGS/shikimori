package org.shikimori.client.activity;

import android.support.v4.content.ContextCompat;

import org.shikimori.client.DrawerTool;
import org.shikimori.client.MainActivity;
import org.shikimori.client.R;
import org.shikimori.client.tool.AuthMasterShiki;
import org.shikimori.library.interfaces.LogouUserListener;
import org.shikimori.library.interfaces.UserDataChangeListener;
import org.shikimori.library.tool.controllers.ShikiAC;

import ru.altarix.basekit.library.activities.BaseKitDrawerActivity;
import ru.altarix.basekit.library.tools.drawer.DrawerToolHelper;
import ru.altarix.basekit.library.tools.h;
import ru.altarix.ui.ExSlidingTabLayout;


/**
 * Created by Владимир on 20.06.2014.
 */
public class DrawerActivity extends BaseKitDrawerActivity<ShikiAC> implements UserDataChangeListener, LogouUserListener {

    @Override
    public ShikiAC initActivityController() {
        return new ShikiAC(this);
    }

    @Override
    public DrawerToolHelper getDrawerTool() {
        return new DrawerTool(this, getToolbar());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public void setUserDrawerData() {
        ((DrawerTool) drawerTool).setUserDrawerData();
    }

    @Override
    public void updateUserUI() {
        setUserDrawerData();
    }


    @Override
    public void logoutTrigger() {
        getActivityController().getShikiUser().logout();
        new AuthMasterShiki(this)
                .openPage(MainActivity.class);
    }
}
