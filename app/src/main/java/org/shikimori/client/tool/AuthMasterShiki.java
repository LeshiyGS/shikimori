package org.shikimori.client.tool;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import org.shikimori.client.activity.AuthActivity;
import org.shikimori.client.fragments.AuthFragment;
import org.shikimori.library.tool.ShikiUser;

import ru.altarix.basekit.library.tools.BaseAuthMaster;

/**
 * Created by Владимир on 29.07.2015.
 */
public class AuthMasterShiki extends BaseAuthMaster<AuthMasterShiki> {

    public AuthMasterShiki(Context context) {
        super(context);
        authActivityClass = AuthActivity.class;
    }

    public AuthMasterShiki(Context context, int rootId, FragmentManager frmanager) {
        super(context, rootId, frmanager);
        authFragmentClass = AuthFragment.class;
    }

    @Override
    public boolean isAuthorized() {
        return ShikiUser.isAuthorized();
    }
}
