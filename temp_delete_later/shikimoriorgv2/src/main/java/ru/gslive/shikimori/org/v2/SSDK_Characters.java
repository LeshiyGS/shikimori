package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Characters implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String name;
	String russian;
	String image_original;
	String image_preview;
	String image_x96;
	String image_x64;
	String url;
	
    public static SSDK_Characters parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Characters characters = new SSDK_Characters();
        characters.id = o.getString("id");
        characters.name = o.getString("name");
        characters.russian = o.getString("russian");
        characters.image_original = "http://shikimori.org" + o.getJSONObject("image").getString("original");
        characters.image_preview = "http://shikimori.org" + o.getJSONObject("image").getString("preview");
        characters.image_x96 = "http://shikimori.org" + o.getJSONObject("image").getString("x96");
        characters.image_x64 = "http://shikimori.org" + o.getJSONObject("image").getString("x64");
        characters.url = o.getString("url");
        
        return characters;
    }
}