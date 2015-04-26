package org.shikimori.library.objects.one;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by Феофилактов on 25.04.2015.
 */
public class ItemUserHistory extends JsonParseable implements JsonParseable.Creator<ItemUserHistory> {
    public String id, createdAt, description;
    public AMShiki target;

    public static ItemUserHistory create(JSONObject json){
        return new ItemUserHistory().createFromJson(json);
    }

    @Override
    public ItemUserHistory createFromJson(JSONObject json) {
        if(json == null)
            return this;

        id = HelperObj.getString(json, "id");
        createdAt = HelperObj.getString(json, "created_at");
        description = HelperObj.getString(json, "description");

        target = AMShiki.create(json.optJSONObject("target"));
        return this;
    }
}
