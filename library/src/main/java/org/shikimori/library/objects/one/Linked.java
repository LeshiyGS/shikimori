package org.shikimori.library.objects.one;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class Linked {
    public String id, threadId, type, name, russianName;
    public ItemImage image;
    public int episodes, episodesAired;
    public boolean ongoing, anons;
    public Linked(JSONObject object){
        if(object == null)
            return;

        id = HelperObj.getString(object, "id");
        threadId = HelperObj.getString(object, "thread_id");
        type = HelperObj.getString(object, "type");
        russianName = HelperObj.getString(object, "russian_name");
        name = HelperObj.getString(object, "name");
        image = new ItemImage(object.optJSONObject("image"));
        ongoing = object.optBoolean("ongoing");
        anons = object.optBoolean("anons");
        episodes = object.optInt("episodes");
        episodesAired = object.optInt("episodes_aired");

    }
}
