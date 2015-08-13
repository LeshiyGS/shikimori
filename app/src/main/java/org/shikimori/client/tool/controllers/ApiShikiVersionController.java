package org.shikimori.client.tool.controllers;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.shikimori.client.BuildConfig;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.controllers.BaseApiController;

/**
 * Created by Феофилактов on 30.07.2015.
 */
public class ApiShikiVersionController extends BaseApiController<ApiShikiVersionController> {
    private final SharedPreferences mSettings;

    public ApiShikiVersionController(Query query) {
        super(query);
        mSettings = query.getContext().getSharedPreferences(query.getContext().getPackageName(), Context.MODE_PRIVATE);
    }

    public void checkNewVersion(final Query.OnQuerySuccessListener listener) {
        query.init("http://anibreak.ru/v.0.3/get/shiki/version")
                .setCache(true)
                .getResultObject(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {
                        int newVersion = res.getParameterInt("version");
                        int lastShowNewVersion = mSettings.getInt("lastShowNewVersion", 0);

                        if (newVersion > BuildConfig.VERSION_CODE
                                && lastShowNewVersion < newVersion) {
                            mSettings.edit().putInt("lastShowNewVersion", newVersion).apply();
                            JSONObject obj = res.getResultObject();
                            if(obj!=null){
                                try {
                                    obj.put("thisisnew", true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        listener.onQuerySuccess(res);
                    }
                });
    }
}
