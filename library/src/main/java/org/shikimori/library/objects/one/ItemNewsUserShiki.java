package org.shikimori.library.objects.one;

import android.view.ViewGroup;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemNewsUserShiki extends JsonParseable implements JsonParseable.Creator<ItemNewsUserShiki> {

    public String id, kind, body, htmlBody, createdAt;
    public Linked linked;
    public ItemUser from, to;
    public boolean read;
    public ViewGroup parsedContent;

    public static ItemNewsUserShiki create(JSONObject json){
        return new ItemNewsUserShiki().createFromJson(json);
    }

    @Override
    public ItemNewsUserShiki createFromJson(JSONObject json) {
        if(json == null)
            return this;

        id = HelperObj.getString(json, "id");
        kind = HelperObj.getString(json, "kind");
        read = json.optBoolean("read");
        body = HelperObj.getString(json, "body");
        htmlBody = HelperObj.getString(json, "html_body");
        createdAt = HelperObj.getString(json, "created_at");
        linked = new Linked(json.optJSONObject("linked"));
        from = new ItemUser(json.optJSONObject("from"));
        to = new ItemUser(json.optJSONObject("to"));

        return this;
    }

}
