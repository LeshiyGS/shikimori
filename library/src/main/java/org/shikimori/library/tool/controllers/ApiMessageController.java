package org.shikimori.library.tool.controllers;

import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;

/**
 * Created by Владимир on 03.07.2015.
 */
public class ApiMessageController extends BaseApiController<ApiMessageController> {
    public ApiMessageController(Query query) {
        super(query);
    }

    public void updatePrivateMessage(String id, String text, Query.OnQuerySuccessListener listener){
        query.init(ShikiApi.getUrl(ShikiPath.MESSAGESPRIVATE_ID, id))
                .setMethod(Query.METHOD.PUT)
                .addParam("message[body]", text);

        send(listener);
    }

    public void updateComment(String id, String text, Query.OnQuerySuccessListener listener){
        query.init(ShikiApi.getUrl(ShikiPath.COMMENTS_ID, id))
                .setMethod(Query.METHOD.PUT)
                .addParam("comment[body]", text);

        send(listener);
    }

    public void sendPrivateMessage(String fromUserId, String toUserId, String text, Query.OnQuerySuccessListener listener){
        query.init(ShikiApi.getUrl(ShikiPath.MESSAGESPRIVATE))
                .setMethod(Query.METHOD.POST)
                .addParam("message[kind]", "Private")
                .addParam("message[from_id]", fromUserId)
                .addParam("message[to_id]", toUserId)
                .addParam("message[body]", text);

        send(listener);
    }

    public void sendComment(String treadId, String userId, String text, Query.OnQuerySuccessListener listener){
        query.init(ShikiApi.getUrl(ShikiPath.COMMENTS))
                .setMethod(Query.METHOD.POST)
                .addParam("comment[commentable_id]", treadId)
                .addParam("comment[commentable_type]", "Entry")
                .addParam("comment[user_id]", userId)
                .addParam("comment[body]", text);

        send(listener);
    }

}
