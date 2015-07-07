package org.shikimori.library.objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.shikimori.library.objects.abstracts.AMDetails;
import org.shikimori.library.objects.one.RatesStatusesStats;
import org.shikimori.library.objects.one.Studio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 03.04.2015.
 */
public class ItemAnimeDetails extends AMDetails {

    public List<Studio> studios;

    @Override
    public ItemAnimeDetails createFromJson(JSONObject json) {
        super.createFromJson(json);
        JSONArray arrayStudios = json.optJSONArray("studios");
        studios = new ArrayList<>();
        if(arrayStudios!=null){
            for (int i = 0; i < arrayStudios.length(); i++) {
                JSONObject item = arrayStudios.optJSONObject(i);
                if(item.optBoolean("real"))
                    studios.add(new Studio(item));
            }
        }

        return this;
    }

}
