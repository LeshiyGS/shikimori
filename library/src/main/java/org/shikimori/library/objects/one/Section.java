package org.shikimori.library.objects.one;

import org.json.JSONObject;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class Section {
    public String id, position, name, permalink, url;
    public Section(JSONObject json){
        if(json == null)
            return;
        id = HelperObj.getString(json, "id");
        position = HelperObj.getString(json, "position");
        name = HelperObj.getString(json, "name");
        permalink = HelperObj.getString(json, "permalink");
        url = HelperObj.getString(json, "url");
    }
}
