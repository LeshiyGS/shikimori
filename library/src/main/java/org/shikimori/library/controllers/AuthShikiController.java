package org.shikimori.library.controllers;

import android.content.Context;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.h;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class AuthShikiController {
    private Context mContext;
    private final Query query;
    private final ShikiUser user;
    private String login;
    private String password;
    private Query.OnQuerySuccessListener listener;

    public AuthShikiController(Context mContext, Query query, ShikiUser user){
        this.mContext = mContext;
        this.query = query;
        this.user = user;
    }

    public void shikiAuth(String login, String password, Query.OnQuerySuccessListener listener){
        this.login = login;
        this.password = password;
        this.listener = listener;
        getAuthThoken();
    }

    void getAuthThoken() {
        query.init(ShikiApi.getUrl(ShikiPath.GET_AUTH_THOKEN))
                .getResult(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {
                        auth(res.getParameter("authenticity_token"));
                    }
                });
    }

    void auth(String thoken) {
        query.init(ShikiApi.getUrl(ShikiPath.AUTH))
                .setMethod(Query.METHOD.POST)
                .addParam("user[nickname]", login)
                .addParam("user[password]", password)
                .addParam("authenticity_token", thoken)
                .getResult(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {

                        if (h.match("\\/users\\/sign_in", res.getHtml())) {
                            res.setError();
                            res.setMsg(mContext.getString(R.string.error_auth));
                            listener.onQuerySuccess(res);
                            return;
                        }

                        String cookie = res.getHeader("Set-Cookie");
                        user.setCookie(cookie);
                        getUserData();
                    }
                });
    }

    /**
     * Получаем инфу о пользователе
     */
    private void getUserData() {
        query.init(ShikiApi.getUrl(ShikiPath.GET_USER_DATA))
                .addHeader("Set-Cookie", user.getCookie())
                .getResult(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {
                        user.setData(res.getResultObject());
                        listener.onQuerySuccess(res);
                    }
                });
    }

}
