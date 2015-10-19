package org.shikimori.library.features.manga.model;

import org.json.JSONObject;
import org.shikimori.library.objects.abstracts.AMDetails;

import java.util.List;

/**
 * Created by Феофилактов on 03.04.2015.
 */
public class ItemMangaDetails extends AMDetails {

    public List<String> publishers;
    public String volumes, chapters;

    @Override
    public ItemMangaDetails create(JSONObject json) {
        super.create(json);
        helper.setData(json);
        volumes = helper.addString("volumes");
        chapters = helper.addString("chapters");

        publishers = getList(json.optJSONArray("publishers"), "name");
        return this;
    }


}
