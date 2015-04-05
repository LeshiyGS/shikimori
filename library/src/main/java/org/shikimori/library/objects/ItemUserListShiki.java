package org.shikimori.library.objects;

/**
 * Created by LeshiyGS on 04.04.2015.
 */
import org.json.JSONObject;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by LeshiyGS on 30.08.2014.
 */
public class ItemUserListShiki extends JsonParseable implements JsonParseable.Creator<ItemUserListShiki> {
    public String id, score, status, status_name, text, episodes, chapters, volumes, text_html, rewatches;
    public String tId, tName, tRussian, tpreview,tx96, tx64, tUrl, tOngoing, tAnons, tEpisodes, tEpisodesAired, tVolumes, tChapters;
    protected JSONObject allData;

    @Override
    public String toString() {
        if (allData != null)
            return allData.toString();
        return "";
    }

    public static ItemUserListShiki create(JSONObject json) {
        return new ItemUserListShiki().createFromJson(json);
    }

    @Override
    public ItemUserListShiki createFromJson(JSONObject json) {
        allData = json;
        if (json == null)
            return null;
        HelperObj helper = new HelperObj(json);
        id = helper.addString("id");
        score = helper.addString("score");
        status = helper.addString("status");
        status_name = helper.addString("status_name");
        text = helper.addString("text");
        episodes = helper.addString("episodes");
        chapters = helper.addString("chapters");
        volumes = helper.addString("volumes");
        text_html = helper.addString("text_html");
        rewatches = helper.addString("rewatches");


        JSONObject anime = json.optJSONObject("anime");
        if(anime!=null){
            tId = anime.optString("id");
            tName = anime.optString("name");
            tRussian = anime.optString("russian");
            tOngoing = anime.optString("ongoing");
            tAnons = anime.optString("anons");
            tEpisodes = anime.optString("episodes");
            tEpisodesAired = anime.optString("episodes_aired");
            tUrl = anime.optString("url");

            JSONObject imgAnime = anime.optJSONObject("image");
            tpreview = ShikiApi.getUrl(imgAnime.optString("preview"));
            tx96 = ShikiApi.getUrl(imgAnime.optString("x96"));
            tx64 = ShikiApi.getUrl(imgAnime.optString("x64"));
        }

        JSONObject manga = json.optJSONObject("manga");
        if(manga!=null){
            tId = manga.optString("id");
            tName = manga.optString("name");
            tRussian = manga.optString("russian");
            tOngoing = manga.optString("ongoing");
            tAnons = manga.optString("anons");
            tVolumes = manga.optString("volumes");
            tChapters = manga.optString("chapters");
            tUrl = manga.optString("url");

            JSONObject imgManga = manga.optJSONObject("image");
            tpreview = ShikiApi.getUrl(imgManga.optString("preview"));
            tx96 = ShikiApi.getUrl(imgManga.optString("x96"));
            tx64 = ShikiApi.getUrl(imgManga.optString("x64"));
        }

        return this;
    }
}