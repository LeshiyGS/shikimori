package org.shikimori.library.objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.shikimori.library.objects.abs.AbstractHelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class AnimeDetails extends JsonParseable implements JsonParseable.Creator<AnimeDetails> {

    public String id, name, russian, img_original, img_preview, img_x96, img_x64, url, ongoing,
            episodes, episodes_aired, rating, kind, aired_on, released_on, duration, score, description, description_html, thread_id,
            world_art_id, myanimelist_id, ani_db_id, user_rate;
    public Boolean favoured, anons;

    //english,
    //japanese,
    //synonyms,
    //genres,
    //studios,


    public static AnimeDetails create(JSONObject json) {
        return new AnimeDetails().createFromJson(json);
    }

    @Override
    public AnimeDetails createFromJson(JSONObject json) {
        if(json == null)
            return this;

        AbstractHelperObj helper = new AbstractHelperObj(json);
        id = helper.addString("id");
        name = helper.addString("name");
        russian = helper.addString("russian");
        url = helper.addString("url");
        ongoing = helper.addString("ongoing");
        episodes = helper.addString("episodes");
        episodes_aired = helper.addString("episodes_aired");
        rating = helper.addString("rating");
        kind = helper.addString("kind");
        aired_on = helper.addString("aired_on");
        released_on = helper.addString("released_on");
        duration = helper.addString("duration");
        score = helper.addString("score");
        description = helper.addString("description");
        description_html = helper.addString("description_html");
        thread_id = helper.addString("thread_id");
        world_art_id = helper.addString("world_art_id");
        myanimelist_id = helper.addString("myanimelist_id");
        ani_db_id = helper.addString("ani_db_id");
        user_rate  = helper.addString("user_rate");

        favoured = json.optBoolean("favoured");
        anons = json.optBoolean("anons");

        JSONObject image = json.optJSONObject("image");
        if(image!=null){
            img_original = image.optString("original");
            img_preview = image.optString("preview");
            img_x96 = image.optString("x96");
            img_x64 = image.optString("x64");
        }

//        JSONArray coomInf = json.optJSONArray("common_info");
//        if(coomInf!=null){
//            commonInfo = new ArrayList<>();
//            for (int i = 0; i < coomInf.length(); i++) {
//                commonInfo.add(coomInf.optString(i));
//            }
//        }

        return this;
    }


}
