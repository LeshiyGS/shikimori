package org.shikimori.library.objects.one;

import org.json.JSONObject;

import ru.altarix.basekit.library.tools.objBuilder.HelperObj;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;

/**
 * Created by Владимир on 14.09.2015.
 */
public class Relation implements JsonParseable<Relation> {

    String relation;
    String relation_russian;
    AMShiki anime, manga;

    @Override
    public Relation create(JSONObject jsonObject) {
        if (jsonObject != null) {
            relation = HelperObj.getString(jsonObject,"relation");
            relation_russian = HelperObj.getString(jsonObject,"relation_russian");
            anime = new AMShiki().create(jsonObject.optJSONObject("anime"));
            manga = new AMShiki().create(jsonObject.optJSONObject("manga"));
        }
        return this;
    }

    public String getRelation() {
        return relation;
    }

    public String getRelationRussian() {
        return relation_russian;
    }

    public AMShiki getAnime() {
        return anime;
    }

    public AMShiki getManga() {
        return manga;
    }

}
