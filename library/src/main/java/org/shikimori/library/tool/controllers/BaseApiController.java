package org.shikimori.library.tool.controllers;


import com.loopj.android.http.RequestParams;

import org.shikimori.library.loaders.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;

/**
 * Created by Владимир on 29.06.2015.
 */
public abstract class BaseApiController<T extends BaseApiController> {
    protected Query query;
    protected RequestParams params= new RequestParams();

    public BaseApiController(Query query) {
        this.query = query;
    }

    public void send(Query.OnQuerySuccessListener listener) {
        if (listener != null)
            query.getResult(listener);
        else
            query.getResult(new Query.OnQuerySuccessListener() {
                @Override
                public void onQuerySuccess(StatusResult res) {

                }
            });
    }

    public T init() {
        params = new RequestParams();
        return (T)this;
    }

    public void setErrorListener(Query.OnQueryErrorListener errorListener) {
        query.setErrorListener(errorListener);
    }
}