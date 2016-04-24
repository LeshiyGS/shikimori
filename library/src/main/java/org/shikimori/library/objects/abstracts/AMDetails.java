package org.shikimori.library.objects.abstracts;

import com.gars.verticalratingbar.VerticalRatingBar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.shikimori.library.objects.one.AMShiki;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LeshiyGS on 31.03.2015.
 */
public class AMDetails extends AMShiki {

    public String rating, kind, aired_on, released_on, duration, score, description, description_html, topic_id,
            world_art_id, myanimelist_id, ani_db_id, read_manga_id;
    public Boolean favoured;
    public List<String>  english, japanese, synonyms, genres;
    public List<VerticalRatingBar.Rates> ratesStatusesStats;
    public List<VerticalRatingBar.Rates> scoreStats;

    @Override
    public AMDetails create(JSONObject json) {
        if(json == null)
            return this;
        super.create(json);

        HelperObj helper = new HelperObj(json);
        rating = helper.addString("rating");
        kind = helper.addString("kind");
        aired_on = helper.addString("aired_on");
        released_on = helper.addString("released_on");
        duration = helper.addString("duration");
        score = helper.addString("score");
        description = helper.addString("description");
        description_html = helper.addString("description_html");
        topic_id = helper.addString("topic_id");
        world_art_id = helper.addString("world_art_id");
        myanimelist_id = helper.addString("myanimelist_id");
        ani_db_id = helper.addString("ani_db_id");
        favoured = json.optBoolean("favoured");
        read_manga_id = json.optString("read_manga_id");

        genres  = getList(json.optJSONArray("genres"), "russian");
        // user stats
        ratesStatusesStats = buildStats(json.optJSONArray("rates_statuses_stats"));
        Collections.reverse(ratesStatusesStats);
        // scores
        scoreStats = buildStats(json.optJSONArray("rates_scores_stats"));
        Collections.reverse(scoreStats);
        return this;
    }

    private List<VerticalRatingBar.Rates> buildStats(JSONArray array){
        List<VerticalRatingBar.Rates> list = new ArrayList<>();
        int fullStateProgress = 0;
        if(array!=null){
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.optJSONObject(i);
                VerticalRatingBar.Rates item = new VerticalRatingBar.Rates(obj.optInt("value"));
                item.setTitle(HelperObj.getString(obj, "name"));
                list.add(item);
                fullStateProgress += item.getValue();
            }
        }

        if(fullStateProgress!=0){
            for (VerticalRatingBar.Rates rates : list) {
                rates.setProcents(rates.getValue() * 100 / fullStateProgress);
            }
        }

        if(list.size() > 5){
            List<VerticalRatingBar.Rates> newList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                newList.add(list.get(i));
            }
            list = newList;
        }

        return list;
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
