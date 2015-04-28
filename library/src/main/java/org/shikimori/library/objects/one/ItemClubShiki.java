package org.shikimori.library.objects.one;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;
import org.shikimori.library.tool.ProjectTool;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemClubShiki extends JsonParseable implements JsonParseable.Creator<ItemClubShiki> {
    public String id, name, original, main;
    protected JSONObject allData;
    protected HelperObj helper;

    public static ItemClubShiki create(JSONObject json) {
        return new ItemClubShiki().createFromJson(json);
    }

    @Override
    public ItemClubShiki createFromJson(JSONObject json) {
        allData = json;
        if (json == null)
            return this;
        id = HelperObj.getString(json, "id");
        name = HelperObj.getString(json, "name");

        JSONObject image = json.optJSONObject("logo");
        if (image != null) {
            original = ProjectTool.fixUrl(image.optString("original"));
            main = ProjectTool.fixUrl(image.optString("main"));
        }

        return this;
    }
}