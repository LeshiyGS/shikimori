package org.shikimori.library.tool.controllers;

import android.text.TextUtils;
import android.util.Log;

import com.gars.querybuilder.BaseQuery;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.tool.ShikiUser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpException;
import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.HttpRequestInterceptor;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class AuthShikiController {
    private final QueryShiki query;
    private final ShikiUser user;
    private String login;
    private String password;
    private QueryShiki.OnQuerySuccessListener listener;
    QueryShiki.METHOD method = QueryShiki.METHOD.POST;


    public AuthShikiController(QueryShiki query, ShikiUser user){
        this.query = query;
        this.user = user;
    }

    public void shikiAuth(String login, String password, QueryShiki.OnQuerySuccessListener listener){
        this.login = login;
        this.password = password;
        this.listener = listener;
        auth();
    }

    void auth() {
        query.init(ShikiApi.getUrl(ShikiPath.AUTH))
                .setMethod(QueryShiki.METHOD.GET)
                .setEncodeUrl(false)
                .addParam("nickname", login)
                .addParam("password", password)
                .getResult(new QueryShiki.OnQuerySuccessListener<ShikiStatusResult>() {
                    @Override
                    public void onQuerySuccess(ShikiStatusResult res) {

                        String token = res.getParameter("api_access_token");
                        if(!TextUtils.isEmpty(token)){
                            user.setToken(token);
                            user.setName(login);
                            user.initStaticParams();
                            getUserData();
                        } else {
                            res.setError();
                            res.setMsg(query.getContext().getString(R.string.error_auth));
                            listener.onQuerySuccess(res);
                        }
                    }
                });
    }

    /**
     * Get id, avatar
     */
    void getUserData(){

//        AsyncHttpClient client = new AsyncHttpClient();
//        ((DefaultHttpClient)client.getHttpClient()).addRequestInterceptor(new HttpRequestInterceptor() {
//            @Override
//            public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
//                for (Header header : request.getAllHeaders()) {
//                    Log.d("header", "process: " + header.getName() + ": " + header.getValue());
//                }
//            }
//        });
//
//        try {
//            client.addHeader("X-User-Nickname", URLEncoder.encode(ShikiUser.USER_NAME, "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        client.addHeader("X-User-Api-Access-Token", ShikiUser.getToken());
//        client.get(ShikiApi.getUrl(ShikiPath.GET_USER_DATA), new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                Log.d("header", "onSuccess: " + new String(responseBody));
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Log.d("header", "onFailure: " + new String(responseBody));
//            }
//        });
        query.init(ShikiApi.getUrl(ShikiPath.GET_USER_DATA))
            .getResult(new QueryShiki.OnQuerySuccessListener<ShikiStatusResult>() {
                @Override
                public void onQuerySuccess(ShikiStatusResult res) {
                    user.setData(res.getResultObject());
                    user.initStaticParams();
                    listener.onQuerySuccess(res);
                }
            });
    }
}
