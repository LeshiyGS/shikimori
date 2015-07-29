package org.shikimori.library.objects.one;

import org.json.JSONObject;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;

/**
 * Created by Феофилактов on 29.06.2015.
 */
public class Studio {
    public String id, name, image;
    public boolean real;
    public Studio(JSONObject object){
        if(object == null)
            return;

        id = HelperObj.getString(object, "id");
        name = HelperObj.getString(object, "name");
        image = HelperObj.getString(object, "image");
        real = object.optBoolean("real");
    }
}
