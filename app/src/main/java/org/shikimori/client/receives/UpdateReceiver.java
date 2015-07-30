package org.shikimori.client.receives;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.shikimori.client.tool.PushHelperShiki;
import org.shikimori.client.tool.controllers.ApiShikiVersionController;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;

import ru.altarix.basekit.library.tools.h;

public class UpdateReceiver extends BroadcastReceiver implements Query.OnQuerySuccessListener {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            if (intent.getExtras() == null)
                return;

            if (h.isInternetConnectionAvailable(context)) {
                loadVersion();
            }
        }
    }

    private void loadVersion() {
        new ApiShikiVersionController(new Query(mContext))
                .checkNewVersion(this);
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        if (res.getParametrBool("thisisnew")) {
            new PushHelperShiki(mContext)
                    .sendNewVersion(res.getParameter("version_name"));
        }
    }
}
