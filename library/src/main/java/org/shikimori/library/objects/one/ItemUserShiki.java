package org.shikimori.library.objects.one;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;
import org.shikimori.library.tool.ProjectTool;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemUserShiki extends JsonParseable implements JsonParseable.Creator<ItemUserShiki> {
    public String id, nickname, avatar, last_online_at, img_x160, img_x148;
    protected JSONObject allData;
    protected HelperObj helper;

    public static ItemUserShiki create(JSONObject json) {
        return new ItemUserShiki().createFromJson(json);
    }

    @Override
    public ItemUserShiki createFromJson(JSONObject json) {
        allData = json;
        if (json == null)
            return this;
        helper = new HelperObj(json);
        id = helper.addString("id");
        nickname = helper.addString("nickname");
        avatar = helper.addString("avatar");
        last_online_at = helper.addString("last_online_at");

        JSONObject image = json.optJSONObject("image");
        if (image != null) {
            img_x160 = ProjectTool.fixUrl(image.optString("x160"));
            img_x148 = ProjectTool.fixUrl(image.optString("x148"));
        }

        return this;
    }

}