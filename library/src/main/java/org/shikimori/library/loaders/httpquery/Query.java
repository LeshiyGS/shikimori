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
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONObject;
import org.shikimori.library.R;
import org.shikimori.library.interfaces.LogouUserLister;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.tool.LoaderController;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.h;

/**
 * Created by Феофилактов on 30.10.2014.
 */
public class Query {

    public static final long HALFHOUR = 1800000L; // 30 минут
    public static final long HOUR = 3600000L;
    public static final long DAY = 86400000L;
    public static final long FIVE_MIN = 300000L;
    private static final String TAG = "httpquery";

    static AsyncHttpClient client;
    AsyncHttpClient clientSync;
    RequestParams params;
    private Context context;
    private OnQueryErrorListener errorListener;
    // url for request
    private String prefix;
    private ProgressDialog pd;
    // loader
    private LoaderController loaderController;
    // type return content
    StatusResult.TYPE type = StatusResult.TYPE.OBJECT;
    // request method
    METHOD method = METHOD.GET;
    private boolean cache = false;
    private long timeCache = HALFHOUR; // 30 минут
    private DbCache dbCache;
    private boolean useAutorization;
    private SwipeRefreshLayout loaderSwipe;

    public enum METHOD {
        GET, POST, DELETE
    }

    // cancell all request
    public void onStop() {
        if(client!=null)
            client.cancelAllRequests(true);
        if(clientSync!=null)
            clientSync.cancelAllRequests(true);
    }

    public LoaderController getLoader() {
        return loaderController;
    }

    public Query addHeader(String key, String header) {
        if(client!=null)
            client.addHeader(key, header);
        if(clientSync!=null)
            clientSync.addHeader(key, header);
        return this;
    }

    public interface OnQueryErrorListener {
        public void onQueryError(StatusResult res);
    }

    public interface OnQuerySuccessListener {
        public void onQuerySuccess(StatusResult res);
    }

    private void initConstructor(Context context){
        this.context = context;
        if (client == null)
            client = new AsyncHttpClient();
    }

    public Query(Context context) {
        initConstructor(context);
    }

    public Query(Context context, Boolean async) {
        if(async) {
            initConstructor(context);
            return;
        }
        this.context = context;
        if (clientSync == null)
            clientSync = new SyncHttpClient();
    }

    /**
     * Call before make request
     *
     * @param prefix
     * @return
     */
    public Query init(String prefix) {
        this.prefix = prefix;
        cache = false;
        // request method
        this.method = METHOD.GET;
        // type return content
        this.type = StatusResult.TYPE.OBJECT;
        if(client!=null)
            client.removeAllHeaders();
        if(clientSync!=null)
            clientSync.removeAllHeaders();
        // add user token

        if (ShikiUser.getToken() != null) {
            addHeader("X-User-Nickname", ShikiUser.USER_NAME);
            addHeader("X-User-Api-Access-Token", ShikiUser.getToken());
        }
        useAutorization = false;
        params = new RequestParams();
        return this;
    }

    /**
     * Call before make request
     *
     * @param prefix
     * @param type
     * @return
     */
    public Query init(String prefix, StatusResult.TYPE type) {
        init(prefix);
        this.type = type;
        return this;
    }

    public Query setMethod(METHOD method) {
        this.method = method;
        return this;
    }

    public Query setCache(boolean cache) {
        this.cache = cache;
        return this;
    }

    public Query setCache(boolean cache, long time) {
        this.cache = cache;
        this.timeCache = time;
        return this;
    }

    public RequestParams getParams() {
        if (params == null)
            params = new RequestParams();
        return params;
    }

    public Query setProgressDialog(ProgressDialog pd) {
        this.pd = pd;
        return this;
    }

    public Query setLoader(LoaderController loaderView) {
        this.loaderController = loaderView;
        return this;
    }
    public Query setSwipeLoader(SwipeRefreshLayout loaderSwipe) {
        this.loaderSwipe = loaderSwipe;
        return this;
    }

    public Query setParams(RequestParams params) {
        this.params = params;
        return this;
    }

    public Query addParam(String name, Object value) {
        params.put(name, value);
        return this;
    }

    public Context getContext() {
        return context;
    }

    public Query setErrorListener(OnQueryErrorListener errorListener) {
        this.errorListener = errorListener;
        return this;
    }

    private boolean getCache(OnQuerySuccessListener successListener) {
        if (cache) {
            Cursor cur = getDbCache().getData(prefix + params.toString());
            if (cur.moveToFirst()) {
                String data = DbCache.getValue(cur, DbCache.QUERY_DATA);
                data = data.replace("__|__", "'");
                if (ShikiApi.isDebug)
                    Log.d(TAG, "cache: " + data);
                StatusResult res = new StatusResult(data, type);
                res.setSuccess();
                if (successListener != null)
                    successListener.onQuerySuccess(res);
                cur.close();
                return true;
            }
            cur.close();
        }
        return false;
    }

    public void getResultObject(OnQuerySuccessListener successListener){
        type = StatusResult.TYPE.OBJECT;
        getResult(successListener);
    }

    public void getResultArray(OnQuerySuccessListener successListener){
        type = StatusResult.TYPE.ARRAY;
        getResult(successListener);
    }

    public void getResult(final OnQuerySuccessListener successListener) {
        if (ShikiApi.isDebug) {
            String p = params.toString();
            if (method == METHOD.GET) {
                Log.d(TAG, "request: " + prefix + "?" + p);
            } else {
                Log.d(TAG, "request: " + prefix);
                Log.d(TAG, "params: " + p);
            }
            Log.d(TAG, "X-User-Nickname: " + ShikiUser.USER_NAME);
            Log.d(TAG, "X-User-Api-Access-Token: " + ShikiUser.getToken());
        }
        if (getCache(successListener))
            return;

        if (!h.getConnection(context)) {
            StatusResult res = new StatusResult();
            res.setMsg(context.getString(R.string.error_connection));
            showError(res);
            hideLoaders();
            return;
        }

        requestToServer(successListener);
    }

    private void requestToServer(final OnQuerySuccessListener successListener) {
        AsyncHttpClient tempClient = clientSync != null ? clientSync : client;
        if (method == METHOD.POST)
            tempClient.post(prefix, params, getSuccessListener(successListener));
        else if (method == METHOD.GET)
            tempClient.get(prefix, params, getSuccessListener(successListener));
        else if (method == METHOD.DELETE){
            String _paramStr = params.toString();
            if(_paramStr.length() > 0)
                _paramStr = "?" + _paramStr;
            tempClient.delete(prefix + _paramStr, getSuccessListener(successListener));
        }
    }

    RequestData getRequestData() {
        RequestData reqData = new RequestData();
        reqData.cache = cache;
        reqData.method = method;
        reqData.timeCache = timeCache;
        reqData.type = type;
        reqData.requestRow = prefix + params.toString();
        return reqData;
    }

    public AsyncHttpResponseHandler getSuccessListener(final OnQuerySuccessListener successListener) {
        return new MyAsyncHandler(getRequestData(), successListener);
//        return new AsyncHttpResponseHandler() {
//            RequestData reqData;
//
//            @Override
//            public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
//                super.onPreProcessResponse(instance, response);
//                reqData = getRequestData();
//            }
//
//            @Override
//            public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                if (context == null) {
//                    if (ShikiApi.isDebug)
//                        Log.d(TAG, "response success but context is null (no ui return)");
//                    return;
//                }
//                String data = bytes == null ? null : new String(bytes);
//                if (ShikiApi.isDebug)
//                    Log.d(TAG, "response: " + data);
//
//                StatusResult res = new StatusResult(data, reqData.type);
//                res.setHeaders(headers);
//                // TODO Сделать обработчик ошибок от shikimori
//                res.setSuccess();
//                if (showError(res))
//                    return;
//
//                reqData.requestData = data;
//                if(data != null)
//                    saveCache(reqData);
//
//                if (successListener != null)
//                    successListener.onQuerySuccess(res);
//            }
//
//            @Override
//            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                failResult(bytes);
//            }
//        };
    }

    void failResult(byte[] bytes) {
        StatusResult stat = new StatusResult();
        String dataString = null;
        try {

            dataString = new String(bytes);
            JSONObject data = new JSONObject(dataString);
            if (data.has("error")) {
                String errorMessage = data.optString("error");
                if (errorMessage.contains("Вам необходимо войти в систему")) {
                    if ((context instanceof LogouUserLister)) {
                        ((LogouUserLister) context).logoutTrigger();
                        return;
                    }
                }
                stat.setError();
                stat.setMsg(data.optString("error"));
            }
        } catch (Exception e) {
            stat.setServerError();
            e.printStackTrace();
        }
        showError(stat);
        if (ShikiApi.isDebug)
            Log.d(TAG, "server not response: " + dataString);
    }

    /**
     * Remove cache by url like (http://shikimori/api/calendar)
     *
     * @param prefix
     * @return
     */
    public Query invalidateCache(String prefix) {
        getDbCache().invalidateCache(prefix);
        return this;
    }

    public Query invalidateCache(String prefix, ContentValues cv) {
        getDbCache().invalidateCache(prefix, cv);
        return this;
    }

    private DbCache getDbCache() {
        if (dbCache == null)
            dbCache = new DbCache(context);
        return dbCache;
    }

    private void saveCache(RequestData reqData) {
        if (reqData.cache) {
            getDbCache().setData(reqData.requestRow, reqData.requestData, reqData.timeCache);
        }
    }

    private void hideLoaders() {
        if (pd != null)
            pd.dismiss();
        if (loaderController != null)
            loaderController.hide();
        if(loaderSwipe!=null)
            loaderSwipe.setRefreshing(false);
    }

    boolean showError(StatusResult res) {
        if (context == null)
            return true;
        if (!res.isSuccess()) {
            if (TextUtils.isEmpty(res.getMsg())) {
                if (!h.getConnection(context))
                    res.setMsg(context.getString(R.string.error_connection));
                else
                    res.setMsg(context.getString(R.string.error_server));
            }
            if (errorListener != null)
                errorListener.onQueryError(res);
            else {
                hideLoaders();
                showStandartError(res);
            }
            return true;
        }
        return false;
    }

    /**
     * Show standart error dialog
     *
     * @param res
     */
    public void showStandartError(StatusResult res) {
        if (context == null)
            return;
        if ((context instanceof Activity)) {
            try {
                new AlertDialog.Builder(context)
                        .setMessage(res.getMsg())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                return;
            } catch (Exception e) {
                return;
            }
        }
        h.showMsg(context, res.getMsg());
    }


//    public Query useAutorisation(){
//        useAutorization = true;
//        return this;
//    }

    /**
     * Нужно чтобы следующий запрос не перетер данные предыдущего
     */
    static class RequestData {
        public METHOD method;
        public boolean cache;
        public long timeCache;
        public StatusResult.TYPE type;
        public String requestRow;
        public String requestData;
    }

    class MyAsyncHandler extends AsyncHttpResponseHandler{

        private RequestData reqData;
        private OnQuerySuccessListener successListener;

        public MyAsyncHandler(RequestData reqData, OnQuerySuccessListener successListener){
            this.reqData = reqData;
            this.successListener = successListener;
        }

        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            if (context == null) {
                if (ShikiApi.isDebug)
                    Log.d(TAG, "response success but context is null (no ui return)");
                return;
            }
            String data = bytes == null ? null : new String(bytes);
            if (ShikiApi.isDebug)
                Log.d(TAG, "response: " + data);

            StatusResult res = new StatusResult(data, reqData.type);
            res.setHeaders(headers);
            // TODO Сделать обработчик ошибок от shikimori
            res.setSuccess();
            if (showError(res))
                return;

            reqData.requestData = data;
            if(data != null)
                saveCache(reqData);

            if (successListener != null)
                successListener.onQuerySuccess(res);
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            failResult(bytes);
        }
    }
}
