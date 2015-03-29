package org.shikimori.library.loaders.httpquery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.shikimori.library.R;
import org.shikimori.library.tool.LoaderController;
import org.shikimori.library.tool.h;

/**
 * Created by Феофилактов on 30.10.2014.
 */
public class Query {

    public static final long HALFHOUR = 1800000L; // 30 минут
    public static final long HOUR = 3600000L;
    public static final long DAY = 86400000L;

    static AsyncHttpClient library;
    RequestParams params;
    private Context context;
    private OnQueryErrorListener errorListener;
    private String prefix;
    private ProgressDialog pd;
    private LoaderController loaderController;
    StatusResult.TYPE type = StatusResult.TYPE.OBJECT;
    METHOD method = METHOD.GET;
    private boolean cache = false;
    private long timeCache = HALFHOUR; // 30 минут
    private DbCache dbCache;

    public void onStop() {
        library.cancelAllRequests(true);
    }

    public LoaderController getLoader() {
        return loaderController;
    }

    public enum METHOD{
        GET, POST
    }

    public interface OnQueryErrorListener {
        public void onQueryError(StatusResult res);
    }

    public interface OnQuerySuccessListener {
        public void onQuerySuccess(StatusResult res);
    }

    public Query(Context context) {
        this.context = context;
        if (library == null)
            library = new AsyncHttpClient();
    }

    public Query init(String prefix) {
        this.prefix = prefix;
        cache = false;
        this.method = METHOD.GET;
        this.type = StatusResult.TYPE.OBJECT;
        params = new RequestParams();
        return this;
    }

    public Query init(String prefix, StatusResult.TYPE type){
        init(prefix);
        this.type = type;
        return this;
    }

    public Query setMethod(METHOD method){
        this.method = method;
        return this;
    }

    public Query setCache(boolean cache){
        this.cache = cache;
        return this;
    }
    public Query setCache(boolean cache, long time){
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

    public Query setParams(RequestParams params) {
        this.params = params;
        return this;
    }

    public Query addParam(String name, Object value) {
        params.put(name, value);
        return this;
    }

    public Query setErrorListener(OnQueryErrorListener errorListener) {
        this.errorListener = errorListener;
        return this;
    }

    private boolean getCache(OnQuerySuccessListener successListener){
        if(cache){
            Cursor cur = getDbCache().getData(prefix+params.toString());
            if(cur.moveToFirst()){
                StatusResult res = new StatusResult(DbCache.getValue(cur, DbCache.QUERY_DATA), type);
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

    public void getResult(final OnQuerySuccessListener successListener) {
        if (getCache(successListener))
            return;
        if (!h.getConnection(context)) {
            h.showMsg(context, R.string.error_connection);
            hideLoaders();
            return;
        }
        if(method == METHOD.POST)
            library.post(prefix, params, getSuccessListener(successListener));
        else if (method == METHOD.GET)
            library.get(prefix, params, getSuccessListener(successListener));
    }

    RequestData getRequestData(){
        RequestData reqData = new RequestData();
        reqData.cache = cache;
        reqData.method = method;
        reqData.timeCache = timeCache;
        reqData.type = type;
        reqData.requestRow = prefix+params.toString();
        return reqData;
    }

    public AsyncHttpResponseHandler getSuccessListener(final OnQuerySuccessListener successListener) {
        return new AsyncHttpResponseHandler() {
            RequestData reqData;
            @Override
            public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                super.onPreProcessResponse(instance, response);
                reqData = getRequestData();
            }

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (context == null)
                    return;
                String data = new String(bytes);
                StatusResult res = new StatusResult(data, reqData.type);
                res.setHeaders(headers);
                res.setSuccess();
                if (showError(res))
                    return;

                reqData.requestData = data;
                saveCache(reqData);

                if (successListener != null)
                    successListener.onQuerySuccess(res);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                StatusResult stat = new StatusResult();
                stat.setServerError();
                showError(stat);
            }
        };
    }

    public Query invalidateCache(String prefix){
        getDbCache().invalidateCache(prefix);
        return this;
    }

    private DbCache getDbCache(){
        if(dbCache == null)
            dbCache = new DbCache(context);
        return dbCache;
    }

    private void saveCache(RequestData reqData) {
        if(reqData.cache){
            getDbCache().setData(reqData.requestRow, reqData.requestData, reqData.timeCache);
        }
    }

    private void hideLoaders() {
        if (pd != null)
            pd.dismiss();
        if (loaderController != null)
            loaderController.hide();
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

    public void showStandartError(StatusResult res) {
        if (context == null)
            return;
        if ((context instanceof ActionBarActivity)) {
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

    static class RequestData{
        public METHOD method;
        public boolean cache;
        public long timeCache;
        public StatusResult.TYPE type;
        public String requestRow;
        public String requestData;
    }
}
