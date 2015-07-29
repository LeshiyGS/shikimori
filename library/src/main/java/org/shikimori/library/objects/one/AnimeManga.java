package org.shikimori.library.objects.one;

import org.json.JSONObject;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;

/**
 * Created by Владимир on 30.03.2015.
 */
public class AnimeManga extends JsonParseable<AnimeManga> {
    public String id, groupedId, name;
    public int counted;

    @Override
    public AnimeManga createFromJson(JSONObject json) {
        if (json == null)
            return this;

        HelperObj helper = new HelperObj(json);
        id = helper.addString("id");
        groupedId = helper.addString("grouped_id");
        name = helper.addString("name");
        counted = helper.addInt("size");
        return this;
    }

}
