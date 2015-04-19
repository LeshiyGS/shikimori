package org.shikimori.library.objects.one;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.tool.ProjectTool;

public class ItemImage {
        public String original, preview, x96, x64;
        public ItemImage(JSONObject object){
            if(object == null)
                return;

            original = ProjectTool.fixUrl(HelperObj.getString(object, "original"));
            preview = ProjectTool.fixUrl(HelperObj.getString(object, "preview"));
            x96 = ProjectTool.fixUrl(HelperObj.getString(object, "x96"));
            x64 = ProjectTool.fixUrl(HelperObj.getString(object, "x64"));
        }
    }