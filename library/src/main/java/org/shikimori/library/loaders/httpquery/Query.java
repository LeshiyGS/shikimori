package org.shikimori.library.loaders.httpquery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.json.JSONObject;
import org.shikimori.library.R;
import org.shikimori.library.interfaces.LogouUserListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.hs;
import ru.altarix.basekit.library.tools.LoaderController;
/**
 * Created by Феофилактов on 30.10.2014.
 */
public class Query extends BaseQuery<Query>{

    public Query(Context context) {
        super(context);
    }

    public Query(Context context, Boolean async) {
        super(context, async);
    }

    public Query in(String path){
        init(ShikiApi.getUrl(path));
        return this;
    }

    public Query in(String path, String id){
        init(ShikiApi.getUrl(path, id));
        return this;
    }

    @Override
    protected Query preinit(String url) {
        if (ShikiUser.getToken() != null) {
            addHeader("X-User-Nickname", ShikiUser.USER_NAME);
            addHeader("X-User-Api-Access-Token", ShikiUser.getToken());
        }
        return this;
    }
}
