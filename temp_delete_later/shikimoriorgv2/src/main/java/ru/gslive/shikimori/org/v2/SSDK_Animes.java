package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Animes implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String name;
	String russian;
	String image_original;
	String image_preview;
	String image_x96;
	String image_x64;
	String url;
	Boolean anons;
	Boolean ongoing;
	String episodes;
	String episodes_aired;
	
    public static SSDK_Animes parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Animes animes = new SSDK_Animes();
        animes.id = o.getString("id");
        animes.name = o.getString("name");
        animes.russian = o.getString("russian");
        animes.image_original = "http://shikimori.org" + o.getJSONObject("image").getString("original");
        animes.image_preview = "http://shikimori.org" + o.getJSONObject("image").getString("preview");
        animes.image_x96 = "http://shikimori.org" + o.getJSONObject("image").getString("x96");
        animes.image_x64 = "http://shikimori.org" + o.getJSONObject("image").getString("x64");
        animes.url = o.getString("url");
        animes.anons = o.getBoolean("anons");
        animes.ongoing = o.getBoolean("ongoing");
        animes.episodes = o.getString("episodes");
        animes.episodes_aired = o.getString("episodes_aired");
        if (!animes.ongoing && !animes.anons){
        	animes.episodes_aired = animes.episodes;
        }
        return animes;
    }
}