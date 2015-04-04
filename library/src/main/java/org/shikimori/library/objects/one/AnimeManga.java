package org.shikimori.library.objects.one;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by Владимир on 30.03.2015.
 */
public class AnimeManga extends JsonParseable implements JsonParseable.Creator<AnimeManga> {
    public String id, groupedId, name;
    public int counted;

    public static AnimeManga create(JSONObject json) {
        return new AnimeManga().createFromJson(json);
    }

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
