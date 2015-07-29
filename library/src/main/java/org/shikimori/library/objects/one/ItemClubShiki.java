package org.shikimori.library.objects.one;

import org.json.JSONObject;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;
import org.shikimori.library.tool.ProjectTool;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemClubShiki extends JsonParseable<ItemClubShiki> {
    public String id, name, original, main;
    protected JSONObject allData;
    protected HelperObj helper;

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