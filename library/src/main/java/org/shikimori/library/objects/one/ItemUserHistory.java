package org.shikimori.library.objects.one;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by Феофилактов on 25.04.2015.
 */
public class ItemUserHistory extends JsonParseable<ItemUserHistory> {
    public String id, createdAt, description;
    public AMShiki target;

    @Override
    public ItemUserHistory createFromJson(JSONObject json) {
        if(json == null)
            return this;

        id = HelperObj.getString(json, "id");
        createdAt = HelperObj.getString(json, "created_at");
        description = HelperObj.getString(json, "description");

        target = new AMShiki().create(json.optJSONObject("target"));
        return this;
    }
}
