package org.shikimori.library.objects;


import org.json.JSONObject;
import org.shikimori.library.objects.abs.AbstractHelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by LeshiyGS on 30.08.2014.
 */
public class ItemAnimesShiki extends JsonParseable implements JsonParseable.Creator<ItemAnimesShiki> {
    public String id, name,russian, original,preview,x96,x64,url,episodes,episodes_aired;
    public boolean ongoing,anons;
    private JSONObject allData;

    @Override
    public String toString() {
        if (allData != null)
            return allData.toString();
        return "";
    }

    public static ItemAnimesShiki create(JSONObject json) {
        return new ItemAnimesShiki().createFromJson(json);
    }

    @Override
    public ItemAnimesShiki createFromJson(JSONObject json) {
        allData = json;
        if (json == null)
            return null;
        AbstractHelperObj helper = new AbstractHelperObj(json);
        id = helper.addString("id");
        name = helper.addString("name");
        russian = helper.addString("russian");
        episodes = helper.addString("episodes");
        episodes_aired = helper.addString("episodes_aired");

        JSONObject status = json;

        ongoing = status.optBoolean("ongoing");
        anons = status.optBoolean("anons");

        JSONObject image = json.optJSONObject("image");
        if(image!=null){
            original = image.optString("original");
            preview = image.optString("preview");
            x96 = image.optString("x96");
            x64 = image.optString("x64");
        }

        return this;
    }
}
