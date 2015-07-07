package org.shikimori.library.tool.controllers;

import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.tool.ProjectTool;

/**
 * Created by Владимир on 06.07.2015.
 */
public class ApiRatesController extends BaseApiController<ApiRatesController> {

    public ApiRatesController(Query query) {
        super(query);
    }


    public ApiRatesController setScore(String score){
        params.put("score", score);
        return this;
    }

    public ApiRatesController setStatus(int status){
        params.put("status", status);
        return this;
    }

    public ApiRatesController setEpisodes(String episodes){
        params.put("episodes", episodes);
        return this;
    }
    public ApiRatesController setVolumes(String volumes){
        params.put("volumes", volumes);
        return this;
    }
    public ApiRatesController setText(String text){
        params.put("text", text);
        return this;
    }

    public ApiRatesController setRewatches(String rewatches){
        params.put("rewatches", rewatches);
        return this;
    }

    public ApiRatesController setChapters(String chapters){
        params.put("chapters", chapters);
        return this;
    }

    public void createRate(String userId, String targetId, ProjectTool.TYPE targetType){
        createRate(userId, targetId, targetType, null);
    }

    public void createRate(String userId, String targetId, ProjectTool.TYPE targetType, Query.OnQuerySuccessListener listener){
        query.init(ShikiApi.getUrl(ShikiPath.SET_USER_RATE));
        params.put("user_id", userId);
        params.put("target_id", targetId);
        params.put("target_type", ProjectTool.getStringFromType(targetType));
        query.setParams(params);
        send(listener);
    }

    public void updateRate(String id){
        query.init(ShikiApi.getUrl(ShikiPath.USER_RATE_ID, id));
        query.setMethod(Query.METHOD.PUT);
        query.setParams(params);
        send(null);
    }
    public void deleteRate(String id){
        query.init(ShikiApi.getUrl(ShikiPath.USER_RATE_ID, id));
        query.setMethod(Query.METHOD.DELETE);
        send(null);
    }
}
