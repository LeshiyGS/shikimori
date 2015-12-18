package org.shikimori.library.tool.controllers;

import android.text.TextUtils;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.tool.ShikiUser;

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
