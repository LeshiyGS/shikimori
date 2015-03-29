package org.shikimori.library.objects;


import org.json.JSONObject;
import org.shikimori.library.objects.abs.AbstractHelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by Владимир on 27.08.2014.
 */
public class ItemCaclendarShiki implements JsonParseable, JsonParseable.Creator<ItemCaclendarShiki> {
    public String id, name, russianName, nextEpisode, episodesIired, nextEpisodeAt, imgPreview, imgOrigin;
    public boolean ongoing;
    private JSONObject allData;

    @Override
    public String toString() {
        if (allData != null)
            return allData.toString();
        return "";
    }

    public static ItemCaclendarShiki create(JSONObject json) {
        return new ItemCaclendarShiki().createFromJson(json);
    }

    @Override
    public ItemCaclendarShiki createFromJson(JSONObject json) {
        allData = json;
        if (json == null)
            return null;
        AbstractHelperObj helper = new AbstractHelperObj(json);
        nextEpisode = helper.addString("next_episode");
        nextEpisodeAt = helper.addString("next_episode_at");


        JSONObject anime = json.optJSONObject("anime");

        if(anime!=null){
            helper.setData(anime);
            id = helper.addString("id");
            name = helper.addString("name");
            russianName = helper.addString("russian");
            episodesIired = helper.addString("episodes_aired");
            ongoing = anime.optBoolean("ongoing");

            JSONObject img = anime.optJSONObject("image");
            if(img!=null){
                imgOrigin = img.optString("original");
                imgPreview = img.optString("preview");
            }
        }

        return this;
    }
}
