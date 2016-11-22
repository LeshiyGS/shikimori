package org.shikimori.library.loaders;

import android.content.Context;
import android.util.Log;

import com.gars.querybuilder.BaseQuery;
import com.gars.querybuilder.StatusResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shikimori.library.R;
import org.shikimori.library.interfaces.LogouUserListener;
import org.shikimori.library.tool.ShikiUser;
import android.support.v4.widget.SwipeRefreshLayout;
import ru.altarix.basekit.library.tools.LoaderController;

/**
 * Created by Феофилактов on 30.10.2014.
 */
public class QueryShiki extends BaseQuery<QueryShiki, ShikiStatusResult> {

    private LoaderController loaderController;
    private SwipeRefreshLayout loaderSwipe;

    public QueryShiki(Context context) {
        this(context, true);
    }

    public QueryShiki(Context context, boolean async) {
        super(context, async);
    }

    @Override
    public Class<? extends StatusResult> getResultClass() {
        return ShikiStatusResult.class;
    }

    public QueryShiki in(String path) {
        init(ShikiApi.getUrl(path));
        return this;
    }

    public QueryShiki inp(String path, String prefix) {
        init(ShikiApi.getUrlPrefix(path, prefix));
        return this;
    }

    public QueryShiki in(String path, String id) {
        init(ShikiApi.getUrl(path, id));
        return this;
    }

    public QueryShiki in(String path, String id, String prefix) {
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
    protected QueryShiki preinit(String url) {
        if (ShikiUser.getToken() != null) {
            addHeader("X-User-Nickname", ShikiUser.USER_NAME);
            addHeader("X-User-Api-Access-Token", ShikiUser.getToken());
        }
        return this;
    }

    public QueryShiki setSwipeLoader(SwipeRefreshLayout loaderSwipe) {
        this.loaderSwipe = loaderSwipe;
        return this;
    }

    public QueryShiki setLoader(LoaderController loaderView) {
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
    public boolean fail(ShikiStatusResult stat, String dataString) {
        if(isDebug)
            Log.d("httpquery", "fail: " + dataString);
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
                    } else if (data.has("errors")){
                        JSONArray errorMessage = data.optJSONArray("errors");
                        if(errorMessage!=null){
                            StringBuilder str = new StringBuilder();
                            for (int i = 0; i < errorMessage.length(); i++) {
                                str.append(errorMessage.getString(i)).append("\n");
                            }
                            stat.setError();
                            stat.setMsg(str.toString());
                        }
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
