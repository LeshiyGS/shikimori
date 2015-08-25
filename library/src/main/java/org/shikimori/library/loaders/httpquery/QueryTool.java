package org.shikimori.library.loaders.httpquery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Владимир on 25.08.2015.
 */
public class QueryTool {
    public boolean getConnection(Context c) {
        ConnectivityManager conMgr = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        boolean ret = true;
        if (i != null) {
            if (!i.isConnected())
                ret = false;
            if (!i.isAvailable())
                ret = false;
        }

        if (i == null)
            ret = false;

        return ret;
    }

    public static void showMsg(Context context, String msg) {
        if(context != null) {
            Toast m = Toast.makeText(context, msg, 0);
            m.setGravity(48, 0, 60);
            m.show();
        }
    }
}
