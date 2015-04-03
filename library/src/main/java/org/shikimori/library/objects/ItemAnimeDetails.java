package org.shikimori.library.objects;

import org.json.JSONObject;
import org.shikimori.library.objects.abstracts.AMDetails;

import java.util.List;

/**
 * Created by Феофилактов on 03.04.2015.
 */
public class ItemAnimeDetails extends AMDetails {

    public List<String> studios;

    public static ItemAnimeDetails create(JSONObject json) {
        return new ItemAnimeDetails().createFromJson(json);
    }

    @Override
    public ItemAnimeDetails createFromJson(JSONObject json) {
        super.createFromJson(json);
        studios = getList(json.optJSONArray("studios"), "name");
        return this;
    }


}
