package org.shikimori.library.tool.controllers.api;

import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.objects.one.UserRate;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.controllers.BaseApiController;

/**
 * Created by Владимир on 06.07.2015.
 */
public class ApiRatesController extends BaseApiController<ApiRatesController> {

    public ApiRatesController(Query query) {
        super(query);
    }


    public ApiRatesController setScore(String score){
        params.put("user_rate[score]", score);
        return this;
    }

    public ApiRatesController setStatus(int status){
        params.put("user_rate[status]", status);
        return this;
    }

    public ApiRatesController setEpisodes(String episodes){
        params.put("user_rate[episodes]", episodes);
        return this;
    }
    public ApiRatesController setVolumes(String volumes){
        params.put("user_rate[volumes]", volumes);
        return this;
    }
    public ApiRatesController setText(String text){
        params.put("user_rate[text]", text);
        return this;
    }

    public ApiRatesController setRewatches(String rewatches){
        params.put("user_rate[rewatches]", rewatches);
        return this;
    }

    public ApiRatesController setChapters(String chapters){
        params.put("user_rate[chapters]", chapters);
        return this;
    }

    public void createRate(String userId, String targetId, ProjectTool.TYPE targetType){
        createRate(userId, targetId, targetType, null);
    }

    public ApiRatesController setUserRate(UserRate rate){
        params.put("user_rate[score]", rate.score);
        params.put("user_rate[episodes]", rate.episodes);
        params.put("user_rate[volumes]", rate.volumes);
        params.put("user_rate[chapters]", rate.chapters);
        params.put("user_rate[text]", rate.text);
        params.put("user_rate[rewatches]", rate.rewatches);
        params.put("user_rate[status]", UserRate.Status.fromStatus(rate.status));
        return this;
    }

    public void createRate(String userId, String targetId, ProjectTool.TYPE targetType, Query.OnQuerySuccessListener listener){
        query.init(ShikiApi.getUrl(ShikiPath.SET_USER_RATE));
        query.setMethod(Query.METHOD.POST);
        params.put("user_rate[user_id]", userId);
        params.put("user_rate[target_id]", targetId);
        params.put("user_rate[target_type]", ProjectTool.getStringFromType(targetType));
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
