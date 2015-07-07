package org.shikimori.library.objects.one;

import org.json.JSONArray;
import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 17.04.2015.
 */
public class ItemCharacter extends JsonParseable<ItemCharacter> {

    public String id, name, russian, altname, japanese, descriptionHtml, threadId;
    public ItemImage image;
    public boolean favoured;
    public Seyu seyu;
    public List<AMShiki> animes = new ArrayList<>();
    public List<AMShiki> mangas = new ArrayList<>();

    @Override
    public ItemCharacter createFromJson(JSONObject json) {
        if(json == null)
            return this;

        id = HelperObj.getString(json, "id");
        name = HelperObj.getString(json, "name");
        russian = HelperObj.getString(json, "russian");
        altname = HelperObj.getString(json, "altname");
        japanese = HelperObj.getString(json, "japanese");
        descriptionHtml = HelperObj.getString(json, "description_html");
        threadId = HelperObj.getString(json, "thread_id");

        image = new ItemImage(json.optJSONObject("image"));

        seyu = new Seyu(json.optJSONObject("seyu"));
        favoured = json.optBoolean("favoured");

        JSONArray array = json.optJSONArray("animes");
        if(array!=null){
            for (int i = 0; i < array.length(); i++) {
                animes.add(new AMShiki().create(array.optJSONObject(i)));
            }
        }
        array = json.optJSONArray("mangas");
        if(array!=null){
            for (int i = 0; i < array.length(); i++) {
                mangas.add(new AMShiki().create(array.optJSONObject(i)));
            }
        }

        return this;
    }
}
