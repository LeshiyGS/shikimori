package org.shikimori.library.objects;

import org.json.JSONObject;
import org.shikimori.library.objects.abstracts.AMDetails;

import java.util.List;

/**
 * Created by Владимир on 06.04.2015.
 */
public class ItemAMDetails extends AMDetails {
    public List<String> publishers;
    public List<String> studios;
    public String volumes, chapters;

    @Override
    public ItemAMDetails createFromJson(JSONObject json) {
        super.createFromJson(json);
        helper.setData(json);
        volumes = helper.addString("volumes");
        chapters = helper.addString("chapters");

        publishers = getList(json.optJSONArray("publishers"), "name");
        studios = getList(json.optJSONArray("studios"), "name");
        return this;
    }
}
