package org.shikimori.library.objects;


import org.json.JSONObject;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;
import org.shikimori.library.objects.one.AMShiki;

/**
 * Created by Владимир on 27.08.2014.
 */
public class ItemMangaShiki extends AMShiki {
    public String nextEpisode,volumes,chapters;

    @Override
    public ItemMangaShiki createFromJson(JSONObject json) {
        super.createFromJson(json);
        if (json == null)
            return null;
        HelperObj helper = new HelperObj(json);
        nextEpisode = helper.addString("next_episode");

        volumes = helper.addString("volumes");
        chapters = helper.addString("chapters");

        return this;
    }
}
