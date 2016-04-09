package org.shikimori.client.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.shikimori.client.tool.GetMessageLastForPush;
import org.shikimori.client.tool.PushHelperShiki;
import org.shikimori.library.features.profile.model.ItemDialogs;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.push.PushHelper;
import org.shikimori.library.tool.push.PushHelperReceiver;

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

        Bundle b = intent.getExtras();
        if(b!=null){
            String message = b.getString("message");
            if(!TextUtils.isEmpty(message)){

                try {
                    JSONObject obj = new JSONObject(message);
                    ItemNewsUserShiki msg = new ItemNewsUserShiki().create(obj.optJSONObject("params"));
                    msg.body = obj.optString(PushHelperReceiver.MSG_BODY);
                    GetMessageLastForPush.notifyFrom(this, msg.from.nickname, msg.body, msg.from.img148);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        QueryShiki query = new QueryShiki(this, false);
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.UNREAD_MESSAGES, ShikiUser.USER_ID));
        GetMessageLastForPush.notifyMessage(query);
    }
}
