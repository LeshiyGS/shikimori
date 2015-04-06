package org.shikimori.library.objects.one;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class Notification {
    public int messages, news, notifications;
    public JSONObject json;
    public Notification(JSONObject json){
        if(json == null)
            return;
        this.json = json;
        messages = json.optInt("messages");
        news = json.optInt("news");
        notifications = json.optInt("notifications");
    }
}
