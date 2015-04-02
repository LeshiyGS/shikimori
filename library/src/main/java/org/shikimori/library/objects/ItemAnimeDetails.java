package org.shikimori.library.objects;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.AbstractHelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class ItemAnimeDetails extends ItemAnimesShiki {

    public String rating, kind, aired_on, released_on, duration, score, description, description_html, thread_id,
            world_art_id, myanimelist_id, ani_db_id, user_rate;
    public Boolean favoured;

    //english,
    //japanese,
    //synonyms,
    //genres,
    //studios,


    public static ItemAnimeDetails create(JSONObject json) {
        return new ItemAnimeDetails().createFromJson(json);
    }

    @Override
    public ItemAnimeDetails createFromJson(JSONObject json) {
        if(json == null)
            return this;

        super.createFromJson(json);

        AbstractHelperObj helper = new AbstractHelperObj(json);
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
        return this;
    }


}
