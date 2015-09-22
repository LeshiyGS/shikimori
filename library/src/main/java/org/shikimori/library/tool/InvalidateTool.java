package org.shikimori.library.tool;

import android.content.ContentValues;
import android.content.Context;

import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ShikiAC;

import ru.altarix.basekit.library.activity.BaseKitActivity;

/**
 * Created by Владимир on 22.09.2015.
 */
public class InvalidateTool {

    public static Query getQuery(Context context){
        if(context  instanceof BaseKitActivity){
            ShikiAC ac = (ShikiAC) ((BaseKitActivity) context).getAC();
            return ac.getQuery();
        }
        return null;
    }

    public static void invalidateNotificationList(Query query, String type){
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.MESSAGES, ShikiUser.USER_ID), cv);
    }
    public static void invalidateNotificationList(Query query){
        invalidateNotificationList(query, Constants.NOTIFYING);
    }
    public static void invalidateNewsList(Query query){
        invalidateNotificationList(query, Constants.NEWS);
    }
    public static void invalidateMessages(Query query){
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.DIALOGS));
    }
}
