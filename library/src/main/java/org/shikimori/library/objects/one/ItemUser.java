package org.shikimori.library.objects.one;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemUser extends JsonParseable<ItemUser> {
    public String id, nickname, img64, img148, avatar, last_online_at;

    public static ItemUser create(JSONObject json){
        return new ItemUser(json);
    }

    public ItemUser(JSONObject json){
        if(json == null)
            return;
        id = HelperObj.getString(json, "id");
        nickname = HelperObj.getString(json, "nickname");

        JSONObject image = json.optJSONObject("image");
        if(image == null)
            return;

        img64 = HelperObj.getString(image, "x64");
        img148 = HelperObj.getString(image, "x148");

        avatar = HelperObj.getString(image,"avatar");
        last_online_at = HelperObj.getString(image,"last_online_at");
    }
}
