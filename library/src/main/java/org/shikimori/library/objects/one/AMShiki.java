package org.shikimori.library.objects.one;


import org.json.JSONObject;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by LeshiyGS on 30.08.2014.
 */
public class AMShiki extends JsonParseable implements JsonParseable.Creator<AMShiki> {
    public String id, name,russianName, nextEpisodeAt, imgOriginal, imgPreview,img_x96,img_x64, url ,episodes, episodesAired;
    public boolean ongoing, anons;
    protected JSONObject allData;
    protected HelperObj helper;

    @Override
    public String toString() {
        if (allData != null)
            return allData.toString();
        return "";
    }

    public static AMShiki create(JSONObject json) {
        return new AMShiki().createFromJson(json);
    }

    @Override
    public AMShiki createFromJson(JSONObject json) {
        allData = json;
        if (json == null)
            return null;
        helper = new HelperObj(json);
        id = helper.addString("id");
        name = helper.addString("name");
        url = helper.addString("url");
        russianName = helper.addString("russian");
        episodes = helper.addString("episodes");
        episodesAired = helper.addString("episodes_aired");
        nextEpisodeAt = helper.addString("next_episode_at");

        ongoing = json.optBoolean("ongoing");
        anons = json.optBoolean("anons");
        episodesAired = episodes;

        JSONObject image = json.optJSONObject("image");
        if(image!=null){
            imgOriginal = getImageUrl(image.optString("original"));
            imgPreview = getImageUrl(image.optString("preview"));
            img_x96 = getImageUrl(image.optString("x96"));
            img_x64 = getImageUrl(image.optString("x64"));
        }

        return this;
    }

    String getImageUrl(String url){
        if(url.startsWith("/"))
            return ShikiApi.HTTP_SERVER + url;
        return url;
    }
}
