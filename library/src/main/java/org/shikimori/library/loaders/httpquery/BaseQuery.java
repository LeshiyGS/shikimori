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

import cz.msebera.android.httpclient.Header;
import ru.altarix.basekit.library.tools.LoaderController;

/**
 * Created by Феофилактов on 30.10.2014.
 */
public abstract class BaseQuery<T extends BaseQuery> extends QueryTool{

    private static boolean isDebug = false;

    public static final long HALFHOUR = 1800000L; // 30 минут
    public static final long HOUR = 3600000L;
    public static final long DAY = 86400000L;
    public static final long FIVE_MIN = 300000L;
    protected static final String TAG = "httpquery";

    static AsyncHttpClient client;
    AsyncHttpClient clientSync;
    RequestParams params;
    protected Context context;
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
    private SwipeRefreshLayout loaderSwipe;

    public enum METHOD {
        GET, POST, DELETE, PUT
    }

    public static void setIsDebug(boolean debug){
        isDebug = debug;
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

    public T addHeader(String key, String header) {
        if(client!=null)
            client.addHeader(key, header);
        if(clientSync!=null)
            clientSync.addHeader(key, header);
        return (T) this;
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

    public BaseQuery(Context context) {
        initConstructor(context);
    }

    public BaseQuery(Context context, Boolean async) {
        if(async) {
            initConstructor(context);
            return;
        }
        this.context = context;
        if (clientSync == null)
            clientSync = new SyncHttpClient();
    }

    protected abstract T preinit(String url);

    /**
     * Call before make request
     * @return
     */
    public T init(String url) {
        this.prefix = url;
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
        preinit(url);
        params = new RequestParams();
        return (T) this;
    }

    /**
     * Call before make request
     *
     * @param prefix
     * @param type
     * @return
     */
    public T init(String prefix, StatusResult.TYPE type) {
        init(prefix);
        this.type = type;
        return (T) this;
    }

    public T setMethod(METHOD method) {
        this.method = method;
        return (T) this;
    }

    public T setCache(boolean cache) {
        this.cache = cache;
        return (T) this;
    }

    public T setCache(boolean cache, long time) {
        this.cache = cache;
        this.timeCache = time;
        return (T) this;
    }

    public RequestParams getParams() {
        if (params == null)
            params = new RequestParams();
        return params;
    }

    public T setProgressDialog(ProgressDialog pd) {
        this.pd = pd;
        return (T) this;
    }

    public T setLoader(LoaderController loaderView) {
        this.loaderController = loaderView;
        return (T) this;
    }
    public T setSwipeLoader(SwipeRefreshLayout loaderSwipe) {
        this.loaderSwipe = loaderSwipe;
        return (T) this;
    }

    public T setParams(RequestParams params) {
        this.params = params;
        return (T) this;
    }

    public T addParam(String name, Object value) {
        params.put(name, value);
        return (T) this;
    }

    public Context getContext() {
        return context;
    }

    public T setErrorListener(OnQueryErrorListener errorListener) {
        this.errorListener = errorListener;
        return (T) this;
    }

    private boolean getCache(OnQuerySuccessListener successListener) {
        if (cache) {
            Cursor cur = getDbCache().getData(prefix + params.toString());
            if (cur.moveToFirst()) {
                String data = DbCache.getValue(cur, DbCache.QUERY_DATA);
                data = data.replace("__|__", "'");
                if (isDebug)
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

    public abstract void rezultDebug(OnQuerySuccessListener successListener);

    public void getResult(final OnQuerySuccessListener successListener) {
        if (isDebug) {
            String p = params.toString();
            if (method == METHOD.GET) {
                Log.d(TAG, "request: " + prefix + "?" + p);
            } else {
                Log.d(TAG, "request: " + prefix);
                Log.d(TAG, "params: " + p);
            }
            rezultDebug(successListener);
        }
        if (getCache(successListener))
            return;

        if (!getConnection(context)) {
            StatusResult res = new StatusResult();
            res.setMsg(context.getString(getConnectErrorMessage()));
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
        } else if (method == METHOD.PUT){
            tempClient.put(prefix, params, getSuccessListener(successListener));
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
    }

    public abstract boolean fail(StatusResult stat, String data);

    void failResult(byte[] bytes) {
        StatusResult stat = new StatusResult();
        String dataString = null;
        try {
            dataString = new String(bytes);
            if(fail(stat, dataString))
                return;
        } catch (Exception e) {
            stat.setServerError();
            e.printStackTrace();
        }
        showError(stat);
        if (isDebug)
            Log.d(TAG, "server not response: " + dataString);
    }

    /**
     * Remove cache by url like (http://shikimori/api/calendar)
     *
     * @param prefix
     * @return
     */
    public T invalidateCache(String prefix) {
        getDbCache().invalidateCache(prefix);
        return (T) this;
    }

    public T invalidateCache(String prefix, ContentValues cv) {
        getDbCache().invalidateCache(prefix, cv);
        return (T) this;
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

    public abstract int getConnectErrorMessage();
    public abstract int getServerErrorMessage();

    boolean showError(StatusResult res) {
        if (context == null)
            return true;
        if (!res.isSuccess()) {
            hideLoaders();
            if (TextUtils.isEmpty(res.getMsg())) {
                if (!getConnection(context))
                    res.setMsg(context.getString(getConnectErrorMessage()));
                else
                    res.setMsg(context.getString(getServerErrorMessage()));
            }
            if (errorListener != null)
                errorListener.onQueryError(res);
            else {
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
        showMsg(context, res.getMsg());
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
                if (isDebug)
                    Log.d(TAG, "response success but context is null (no ui return)");
                return;
            }
            String data = bytes == null ? null : new String(bytes);
            if (isDebug)
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
