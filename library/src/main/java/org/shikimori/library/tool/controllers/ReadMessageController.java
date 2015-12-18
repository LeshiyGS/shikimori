package org.shikimori.library.tool.controllers;

import android.view.View;

import org.shikimori.library.features.profile.controllers.NotifyProfileController;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.tool.ProjectTool;

/**
 * Created by Владимир on 29.05.2015.
 */
public class ReadMessageController {

    public static ReadMessageController instance;
    private QueryShiki query;

    public ReadMessageController(QueryShiki query) {
        this.query = query;
    }

    public static ReadMessageController newInstance(QueryShiki query){
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
                .setMethod(QueryShiki.METHOD.POST)
                .addParam("is_read", read ? 0 : 1)
                .addParam("ids", id)
                .getResult(new QueryShiki.OnQuerySuccessListener<ShikiStatusResult>() {
                    @Override
                    public void onQuerySuccess(ShikiStatusResult res) {
                    }
                });
        read = !read;
        ProjectTool.setReadOpasity(v, read);
        return read;
    }

    public interface OnUpdateReadListener{
        public void updateRead();
    }

}
