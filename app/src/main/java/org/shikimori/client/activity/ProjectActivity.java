package org.shikimori.client.activity;

import org.shikimori.client.MainActivity;
import org.shikimori.client.R;
import org.shikimori.client.tool.AuthMasterShiki;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.interfaces.LogouUserListener;

/**
 * Created by me on 30.03.2015.
 */
public class ProjectActivity extends BaseActivity implements LogouUserListener {

    @Override
    protected int getLayoutId() {
        return R.layout.basekit_simple_activity;
    }

    @Override
    public void logoutTrigger() {
        getActivityController().getShikiUser().logout();
        new AuthMasterShiki(this)
                .openPage(MainActivity.class);
    }
}
