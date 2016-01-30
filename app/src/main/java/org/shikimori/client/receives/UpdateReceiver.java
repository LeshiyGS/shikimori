package org.shikimori.client.receives;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.shikimori.client.tool.PushHelperShiki;
import org.shikimori.client.tool.controllers.ApiShikiVersionController;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;

import ru.altarix.basekit.library.tools.h;

public class UpdateReceiver extends BroadcastReceiver implements QueryShiki.OnQuerySuccessListener<ShikiStatusResult> {

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
        new ApiShikiVersionController(new QueryShiki(mContext))
                .checkNewVersion(this);
    }

    @Override
    public void onQuerySuccess(ShikiStatusResult res) {
        if(mContext!=null)
            return;
        if (res.getParametrBool("thisisnew")) {
            new PushHelperShiki(mContext)
                    .sendNewVersion(res.getParameter("version_name"));
        }
    }
}
