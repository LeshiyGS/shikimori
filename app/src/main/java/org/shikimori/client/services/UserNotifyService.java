package org.shikimori.client.services;

import android.app.IntentService;
import android.content.Intent;

import org.shikimori.client.tool.GetMessageLastForPush;
import org.shikimori.library.loaders.Query;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.tool.ShikiUser;

/**
 * Created by Владимир on 26.11.2015.
 */
public class UserNotifyService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UserNotifyService(String name) {
        super(name);
    }

    public UserNotifyService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Query query = new Query(this, false);
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.UNREAD_MESSAGES, ShikiUser.USER_ID));
        GetMessageLastForPush.notifyMessage(query);
    }
}
