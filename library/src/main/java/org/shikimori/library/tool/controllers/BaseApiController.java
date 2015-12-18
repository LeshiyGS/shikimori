package org.shikimori.library.tool.controllers;


import com.loopj.android.http.RequestParams;

import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;

/**
 * Created by Владимир on 29.06.2015.
 */
public abstract class BaseApiController<T extends BaseApiController> {
    protected QueryShiki query;
    protected RequestParams params= new RequestParams();

    public BaseApiController(QueryShiki query) {
        this.query = query;
    }

    public void send(QueryShiki.OnQuerySuccessListener listener) {
        if (listener != null)
            query.getResult(listener);
        else
            query.getResult(new QueryShiki.OnQuerySuccessListener<ShikiStatusResult>() {
                @Override
                public void onQuerySuccess(ShikiStatusResult res) {

                }
            });
    }

    public T init() {
        params = new RequestParams();
        return (T)this;
    }

    public void setErrorListener(QueryShiki.OnQueryErrorListener errorListener) {
        query.setErrorListener(errorListener);
    }
}