package org.shikimori.library.objects.one;


import android.view.ViewGroup;

import org.json.JSONObject;
import org.shikimori.library.interfaces.OnViewBuildLister;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class ItemCommentsShiki extends JsonParseable implements JsonParseable.Creator<ItemCommentsShiki>, OnViewBuildLister {
    public String id, user_id, commentable_id, commentable_type, body, html_body, created_at, updated_at;
    public String nickname, avatar, image_x160, image_x148, last_online_at;
    public boolean offtopic, review, viewed,  can_be_edited;
    private JSONObject allData;
    public ViewGroup parsedContent;

    @Override
    public String toString() {
        if (allData != null)
            return allData.toString();
        return "";
    }

    public static ItemCommentsShiki create(JSONObject json) {
        return new ItemCommentsShiki().createFromJson(json);
    }

    @Override
    public ItemCommentsShiki createFromJson(JSONObject json) {
        allData = json;
        if (json == null)
            return null;
        HelperObj helper = new HelperObj(json);
        id = helper.addString("id");
        user_id = helper.addString("user_id");
        commentable_id = helper.addString("commentable_id");
        commentable_type = helper.addString("commentable_type");
        body = helper.addString("body");
        html_body = helper.addString("html_body");
        created_at = helper.addString("created_at");
        updated_at = helper.addString("updated_at");

        offtopic = json.optBoolean("offtopic");
        review = json.optBoolean("review");
        viewed = json.optBoolean("viewed");
        can_be_edited = json.optBoolean("can_be_edited");

        JSONObject user = json.optJSONObject("user");
        if(user!=null){
            nickname = user.optString("nickname");
            avatar = user.optString("avatar");
            image_x160 = user.optString("x160");
            image_x148 = user.optString("x148");
            last_online_at = user.optString("last_online_at");
        }

        return this;
    }

    @Override
    public void setBuildView(ViewGroup view) {
        parsedContent = view;
    }

    @Override
    public String getHtml() {
        return html_body;
    }
}
