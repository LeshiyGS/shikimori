package org.shikimori.library.objects.one;

import org.json.JSONObject;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemUser extends JsonParseable<ItemUser> {
    public String id, nickname, img64, img148, avatar, lastOnlineAt;

    @Override
    public ItemUser createFromJson(JSONObject json) {
        if(json == null)
            return this;
        id = HelperObj.getString(json, "id");
        nickname = HelperObj.getString(json, "nickname");
        avatar = HelperObj.getString(json,"avatar");
        lastOnlineAt = HelperObj.getString(json,"last_online_at");

        JSONObject image = json.optJSONObject("image");
        if(image == null)
            return this;

        img64 = HelperObj.getString(image, "x64");
        img148 = HelperObj.getString(image, "x148");
        return this;
    }
}
