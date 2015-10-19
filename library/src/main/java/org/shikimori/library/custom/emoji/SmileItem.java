package org.shikimori.library.custom.emoji;

import org.json.JSONObject;
import org.shikimori.library.tool.ProjectTool;

import ru.altarix.basekit.library.tools.objBuilder.HelperObj;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;

/**
 * Created by Феофилактов on 18.10.2015.
 */
public class SmileItem implements JsonParseable<SmileItem> {
    public String path, bbcode;

    @Override
    public SmileItem create(JSONObject jsonObject) {
        if(jsonObject!=null){
            path = ProjectTool.fixUrl(HelperObj.getString(jsonObject, "path"));
            bbcode = HelperObj.getString(jsonObject, "bbcode");
        }
        return this;
    }
}
