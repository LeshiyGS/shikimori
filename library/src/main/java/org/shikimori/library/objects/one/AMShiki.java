package org.shikimori.library.objects.one;


import org.json.JSONObject;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.tool.ProjectTool;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;
/**
 * Created by LeshiyGS on 30.08.2014.
 */
public class AMShiki implements JsonParseable<AMShiki> {
    public String id, name,russianName, nextEpisodeAt, url ,episodes, episodesAired;
    public boolean ongoing, anons;
    protected JSONObject allData;
    protected HelperObj helper;
    public ItemImage image;
    public String poster;
    public UserRate userRate;

    @Override
    public String toString() {
        if (allData != null)
            return allData.toString();
        return "";
    }

    @Override
    public AMShiki create(JSONObject json) {
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


        if (episodes!=null) {
            //приводим нули в нужный вид
            if (episodes.equals("0")) episodes = "?";
            //если тайтл вышел и кол-во вышедших серий ноль серий ноль
            if (!anons && !ongoing) episodesAired = episodes;
        }

        image = new ItemImage(json.optJSONObject("image"));
        // use image field instead of this
        poster = ProjectTool.fixUrl(json.optString("image"));

        userRate = new UserRate().create(json.optJSONObject("user_rate"));

        return this;
    }

    String getImageUrl(String url){
        if(url.startsWith("/"))
            return ShikiApi.HTTP_SERVER + url;
        return url;
    }
}
