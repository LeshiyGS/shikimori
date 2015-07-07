package org.shikimori.library.objects.one;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;
import org.shikimori.library.tool.ProjectTool;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemClubDescriptionShiki extends JsonParseable<ItemClubDescriptionShiki> {
    public String description, descriptionHtml, threadId;
    public String id, name, original, main;
    protected JSONObject allData;
    protected HelperObj helper;

    @Override
    public ItemClubDescriptionShiki createFromJson(JSONObject json) {
        allData = json;
        if (json == null)
            return this;
        description = HelperObj.getString(json, "description");
        descriptionHtml = HelperObj.getString(json, "description_html");
        threadId = HelperObj.getString(json, "thread_id");

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