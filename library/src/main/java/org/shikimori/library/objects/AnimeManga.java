package org.shikimori.library.objects;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.AbstractHelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by Владимир on 30.03.2015.
 */
public class AnimeManga extends JsonParseable implements JsonParseable.Creator<AnimeManga> {
    public String id, groupedId, name;
    public int size;

    public static AnimeManga create(JSONObject json) {
        return new AnimeManga().createFromJson(json);
    }

    @Override
    public AnimeManga createFromJson(JSONObject json) {
        if (json == null)
            return this;

        AbstractHelperObj helper = new AbstractHelperObj(json);
        id = helper.addString("id");
        groupedId = helper.addString("grouped_id");
        name = helper.addString("name");
        size = helper.addInt("size");
        return null;
    }

}
