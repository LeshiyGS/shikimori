package org.shikimori.library.objects;

import org.json.JSONObject;
import org.shikimori.library.tool.ProjectTool;

import ru.altarix.basekit.library.tools.objBuilder.HelperObj;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;

/**
 * Created by Владимир on 06.08.2015.
 */
public class ItemScreenShot implements JsonParseable<ItemScreenShot>{

    String original, preview;

    @Override
    public ItemScreenShot create(JSONObject json) {
        if(json != null){
            original = ProjectTool.fixUrl(HelperObj.getString(json, "original"));
            preview = ProjectTool.fixUrl(HelperObj.getString(json, "preview"));
        }
        return this;
    }

    public String getPreview() {
        return preview;
    }

    public String getOriginal() {
        return original;
    }
}
