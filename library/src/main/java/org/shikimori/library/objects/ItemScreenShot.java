package org.shikimori.library.objects;

import org.json.JSONObject;

import ru.altarix.basekit.library.tools.objBuilder.HelperObj;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;

/**
 * Created by Владимир on 06.08.2015.
 */
public class ItemScreenShot extends JsonParseable<ItemScreenShot>{

    String original, preview;

    @Override
    public ItemScreenShot createFromJson(JSONObject json) {
        if(json != null){
            original = HelperObj.getString(json, "original");
            preview = HelperObj.getString(json, "preview");
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
