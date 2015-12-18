package org.shikimori.library.tool.controllers.api;

import android.view.View;

import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.controllers.BaseApiController;

/**
 * Created by Владимир on 03.07.2015.
 */
public class ApiMessageController extends BaseApiController<ApiMessageController> {
    public ApiMessageController(QueryShiki query) {
        super(query);
    }

    public void updatePrivateMessage(String id, String text, QueryShiki.OnQuerySuccessListener listener){
        query.init(ShikiApi.getUrl(ShikiPath.MESSAGESPRIVATE_ID, id))
                .setMethod(QueryShiki.METHOD.PUT)
                .addParam("message[body]", text);

        send(listener);
    }

    public void updateComment(String id, String text, QueryShiki.OnQuerySuccessListener listener){
        query.init(ShikiApi.getUrl(ShikiPath.COMMENTS_ID, id))
                .setMethod(QueryShiki.METHOD.PUT)
                .addParam("comment[body]", text);

        send(listener);
    }

    public void sendPrivateMessage(String fromUserId, String toUserId, String text, QueryShiki.OnQuerySuccessListener listener){
        query.init(ShikiApi.getUrl(ShikiPath.MESSAGESPRIVATE))
                .setMethod(QueryShiki.METHOD.POST)
                .addParam("message[kind]", "Private")
                .addParam("message[from_id]", fromUserId)
                .addParam("message[to_id]", toUserId)
                .addParam("message[body]", text);

        send(listener);
    }

    public void deleteMessage(){

    }

    public void sendComment(String treadId, String userId, String mesType, String text, QueryShiki.OnQuerySuccessListener listener){
        query.init(ShikiApi.getUrl(ShikiPath.COMMENTS))
                .setMethod(QueryShiki.METHOD.POST)
                .addParam("comment[commentable_id]", treadId)
//                .addParam("comment[commentable_type]", "Entry")
                .addParam("comment[commentable_type]", mesType)
                .addParam("comment[user_id]", userId)
                .addParam("comment[body]", text);

        send(listener);
    }

    public boolean setRead(View v, boolean read, String id){
        query.init(ShikiApi.getUrl(ShikiPath.READ_MESSAGE))
                .setMethod(QueryShiki.METHOD.POST)
                .addParam("is_read", read ? 0 : 1)
                .addParam("ids", id);

        send(null);
        read = !read;
        ProjectTool.setReadOpasity(v, read);
        return read;
    }

    public void getLastDialog(QueryShiki.OnQuerySuccessListener listener){
        query.init(ShikiApi.getUrl(ShikiPath.DIALOGS), ShikiStatusResult.TYPE.ARRAY)
                .addParam("limit", "1")
                .addParam("page", "1");

        send(listener);
    }

}
