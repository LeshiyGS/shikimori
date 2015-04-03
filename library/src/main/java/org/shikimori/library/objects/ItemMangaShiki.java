package org.shikimori.library.objects;


import org.json.JSONObject;
import org.shikimori.library.objects.abs.AbstractHelperObj;

/**
 * Created by Владимир on 27.08.2014.
 */
public class ItemMangaShiki extends AMShiki {
    public String nextEpisode,volumes,chapters;

    public static ItemMangaShiki create(JSONObject json) {
        return new ItemMangaShiki().createFromJson(json);
    }

    @Override
    public ItemMangaShiki createFromJson(JSONObject json) {
        super.createFromJson(json);
        if (json == null)
            return null;
        AbstractHelperObj helper = new AbstractHelperObj(json);
        nextEpisode = helper.addString("next_episode");

        volumes = helper.addString("volumes");
        chapters = helper.addString("chapters");

        return this;
    }
}
