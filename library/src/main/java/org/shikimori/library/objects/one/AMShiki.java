package org.shikimori.library.objects.one;


import org.json.JSONObject;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;
import org.shikimori.library.tool.ProjectTool;

/**
 * Created by LeshiyGS on 30.08.2014.
 */
public class AMShiki extends JsonParseable implements JsonParseable.Creator<AMShiki> {
    public String id, name,russianName, nextEpisodeAt, url ,episodes, episodesAired;
    public boolean ongoing, anons;
    protected JSONObject allData;
    protected HelperObj helper;
    public ItemImage image;
    public String poster;

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
            return this;
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

        image = new ItemImage(json.optJSONObject("image"));
        // use image field instead of this
        poster = ProjectTool.fixUrl(json.optString("image"));
        return this;
    }

    String getImageUrl(String url){
        if(url.startsWith("/"))
            return ShikiApi.HTTP_SERVER + url;
        return url;
    }
}
