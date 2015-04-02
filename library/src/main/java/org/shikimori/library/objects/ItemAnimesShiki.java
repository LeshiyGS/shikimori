package org.shikimori.library.objects;


import org.json.JSONObject;
import org.shikimori.library.objects.abs.AbstractHelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by LeshiyGS on 30.08.2014.
 */
public class ItemAnimesShiki extends JsonParseable implements JsonParseable.Creator<ItemAnimesShiki> {
    public String id, name,russianName, nextEpisodeAt, imgOriginal, imgPreview,img_x96,img_x64, url ,episodes, episodesAired, status;
    public boolean ongoing, anons;
    protected JSONObject allData;

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
        url = helper.addString("utl");
        russianName = helper.addString("russian");
        episodes = helper.addString("episodes");
        episodesAired = helper.addString("episodes_aired");
        nextEpisodeAt = helper.addString("next_episode_at");

        ongoing = json.optBoolean("ongoing");
        anons = json.optBoolean("anons");

        if (!anons && !ongoing){
            status = "Вышло";
            episodesAired = episodes;
        }else if(anons){
            status = "Анонс";
        }else if(ongoing){
            status = "Онгоинг";
        }

        JSONObject image = json.optJSONObject("image");
        if(image!=null){
            imgOriginal = image.optString("original");
            imgPreview = image.optString("preview");
            img_x96 = image.optString("x96");
            img_x64 = image.optString("x64");
        }

        return this;
    }
}
