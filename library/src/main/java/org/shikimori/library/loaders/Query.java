package org.shikimori.library.loaders;

import android.content.Context;
import android.util.Log;

import com.gars.querybuilder.BaseQuery;
import com.gars.querybuilder.StatusResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.shikimori.library.R;
import org.shikimori.library.interfaces.LogouUserListener;
import org.shikimori.library.loaders.httpquery.MyStatusResult;
import org.shikimori.library.tool.ShikiUser;
import android.support.v4.widget.SwipeRefreshLayout;
import ru.altarix.basekit.library.tools.LoaderController;

/**
 * Created by Феофилактов on 30.10.2014.
 */
public class Query extends BaseQuery<Query, MyStatusResult> {

    private LoaderController loaderController;
    private SwipeRefreshLayout loaderSwipe;

    public Query(Context context) {
        this(context, true);
    }

    public Query(Context context, boolean async) {
        super(context, async);
    }

    @Override
    public Class<? extends StatusResult> getResultClass() {
        return MyStatusResult.class;
    }

    public Query in(String path) {
        init(ShikiApi.getUrl(path));
        return this;
    }

    public Query inp(String path, String prefix) {
        init(ShikiApi.getUrlPrefix(path, prefix));
        return this;
    }

    public Query in(String path, String id) {
        init(ShikiApi.getUrl(path, id));
        return this;
    }

    public Query in(String path, String id, String prefix) {
        init(ShikiApi.getUrl(path, id, prefix));
        return this;
    }


    @Override
    public void hideLoaders() {
        super.hideLoaders();
        if (loaderController != null)
            loaderController.hide();
        if (loaderSwipe != null)
            loaderSwipe.setRefreshing(false);
    }


    @Override
    protected Query preinit(String url) {
        if (ShikiUser.getToken() != null) {
            addHeader("X-User-Nickname", ShikiUser.USER_NAME);
            addHeader("X-User-Api-Access-Token", ShikiUser.getToken());
        }
        return this;
    }

    public Query setSwipeLoader(SwipeRefreshLayout loaderSwipe) {
        this.loaderSwipe = loaderSwipe;
        return this;
    }

    public Query setLoader(LoaderController loaderView) {
        this.loaderController = loaderView;
        return this;
    }

    public LoaderController getLoader() {
        return loaderController;
    }

    @Override
    public void resultDebug(OnQuerySuccessListener successListener) {
        Log.d(TAG, "X-User-Nickname: " + ShikiUser.USER_NAME);
        Log.d(TAG, "X-User-Api-Access-Token: " + ShikiUser.getToken());
    }

    @Override
    public boolean fail(MyStatusResult stat, String dataString) {
        try {
            if(dataString != null){
                if(dataString.startsWith("{")){
                    JSONObject data = new JSONObject(dataString);
                    int code = data.optInt("code");
                    if(code > 0){
                        stat.setError();
                        stat.setMsg(data.optString("message"));
                    } else if (data.has("error")){
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
                }
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
