package org.shikimori.library.objects.one;

import org.json.JSONObject;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;

/**
 * Created by Феофилактов on 17.04.2015.
 */
public class Seyu {
    public String id, name;
    public ItemImage image;
    public Seyu(JSONObject json){
        if (json == null)
            return;

        id = HelperObj.getString(json, "id");
        name = HelperObj.getString(json, "name");
        image = new ItemImage(json.optJSONObject("image"));

    }
}
