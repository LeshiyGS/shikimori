package org.shikimori.library.tool.controllers;

import android.text.TextUtils;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.BaseQuery;
import org.shikimori.library.loaders.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.ShikiUser;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class AuthShikiController {
    private final Query query;
    private final ShikiUser user;
    private String login;
    private String password;
    private Query.OnQuerySuccessListener listener;
    Query.METHOD method = Query.METHOD.POST;


    public AuthShikiController(Query query, ShikiUser user){
        this.query = query;
        this.user = user;
    }

    public void shikiAuth(String login, String password, Query.OnQuerySuccessListener listener){
        this.login = login;
        this.password = password;
        this.listener = listener;
        auth();
    }

    void auth() {
        query.init(ShikiApi.getUrl(ShikiPath.AUTH))
                .setMethod(BaseQuery.METHOD.GET)
                .addParam("nickname", login)
                .addParam("password", password)
                .getResult(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {

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
        query.init(ShikiApi.getUrl(ShikiPath.GET_USER_DATA))
            .getResult(new Query.OnQuerySuccessListener() {
                @Override
                public void onQuerySuccess(StatusResult res) {
                    user.setData(res.getResultObject());
                    user.initStaticParams();
                    listener.onQuerySuccess(res);
                }
            });
    }
}
