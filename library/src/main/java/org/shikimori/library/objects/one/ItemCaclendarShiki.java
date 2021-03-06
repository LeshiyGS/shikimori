package org.shikimori.library.objects.one;


import org.json.JSONObject;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;

/**
 * Created by Владимир on 27.08.2014.
 */
public class ItemCaclendarShiki implements JsonParseable<ItemCaclendarShiki> {
    public String id, name, day, russianName, nextEpisode, episodesIired, nextEpisodeAt, imgPreview, imgOrigin;
    public boolean ongoing;
    private JSONObject allData;
    public Long order;
    public boolean isDayHeader;

    @Override
    public String toString() {
        if (allData != null)
            return allData.toString();
        return "";
    }

    @Override
    public ItemCaclendarShiki create(JSONObject json) {
        allData = json;
        if (json == null)
            return null;
        HelperObj helper = new HelperObj(json);
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
