package org.shikimori.library.loaders.httpquery;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.shikimori.library.R;
import org.shikimori.library.interfaces.LogouUserListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.tool.ShikiUser;

/**
 * Created by Феофилактов on 30.10.2014.
 */
public class Query extends BaseQuery<Query> {

    public Query(Context context) {
        super(context);
    }

    public Query(Context context, Boolean async) {
        super(context, async);
    }

    public Query in(String path) {
        init(ShikiApi.getUrl(path));
        return this;
    }

    public Query in(String path, String id) {
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

    @Override
    public void rezultDebug(OnQuerySuccessListener successListener) {
        Log.d(TAG, "X-User-Nickname: " + ShikiUser.USER_NAME);
        Log.d(TAG, "X-User-Api-Access-Token: " + ShikiUser.getToken());
    }

    @Override
    public boolean fail(StatusResult stat, String dataString) {
        try {
            JSONObject data = new JSONObject(dataString);
            if (data.has("error")) {
                String errorMessage = data.optString("error");
                if (errorMessage.contains("token") || errorMessage.contains("Вам необходимо войти в систему")) {
                    if ((context instanceof LogouUserListener)) {
                        ((LogouUserListener) context).logoutTrigger();
                        return true;
                    }
                }
                stat.setError();
                stat.setMsg(data.optString("error"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getConnectErrorMessage() {
        return R.string.error_connection;
    }

    @Override
    public int getServerErrorMessage() {
        return R.string.error_server;
    }
}
