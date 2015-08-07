package org.shikimori.library.tool.controllers;

import android.view.View;

import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.ProjectTool;

/**
 * Created by Владимир on 29.05.2015.
 */
public class ReadMessageController {

    public static ReadMessageController instance;
    private Query query;

    public ReadMessageController(Query query) {
        this.query = query;
    }

    public static ReadMessageController newInstance(Query query){
        if(instance!=null)
            return instance;
        instance = new ReadMessageController(query);
        return instance;
    }

    public static ReadMessageController getInstance(){
        return instance;
    }

    public boolean setRead(View v, boolean read, String id){
        NotifyProfileController.setNeedRefresh(true);
        query.init(ShikiApi.getUrl(ShikiPath.READ_MESSAGE))
                .setMethod(Query.METHOD.POST)
                .addParam("is_read", read ? 0 : 1)
                .addParam("ids", id)
                .getResult(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {
                    }
                });
        read = !read;
        ProjectTool.setReadOpasity(v, read);
        return read;
    }

}
