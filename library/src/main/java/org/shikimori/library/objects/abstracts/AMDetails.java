package org.shikimori.library.objects.abstracts;

import org.json.JSONArray;
import org.json.JSONObject;
import org.shikimori.library.objects.AMShiki;
import org.shikimori.library.objects.abs.AbstractHelperObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class AMDetails extends AMShiki {

    public String rating, kind, aired_on, released_on, duration, score, description, description_html, thread_id,
            world_art_id, myanimelist_id, ani_db_id, user_rate;
    public Boolean favoured;
    public List<String>  english, japanese, synonyms, genres;


    public static AMDetails create(JSONObject json) {
        return new AMDetails().createFromJson(json);
    }

    @Override
    public AMDetails createFromJson(JSONObject json) {
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

        genres  = getList(json.optJSONArray("genres"), "russian");

        return this;
    }

    protected ArrayList<String> getList(JSONArray arr, String name){
        ArrayList<String> list = new ArrayList<>();
        if(arr == null)
            return list;
        for (int i = 0; i < arr.length(); i++) {
            JSONObject item = arr.optJSONObject(i);
            list.add(item.optString(name));
        }
        return list;
    }


}
