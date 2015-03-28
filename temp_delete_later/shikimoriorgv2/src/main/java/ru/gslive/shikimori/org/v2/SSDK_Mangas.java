package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Mangas implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String name;
	String russian;
	String image_original;
	String image_preview;
	String image_x96;
	String image_x64;
	String url;
	String volumes;
	String chapters;
	
    public static SSDK_Mangas parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Mangas mangas = new SSDK_Mangas();
        mangas.id = o.getString("id");
        mangas.name = o.getString("name");
        mangas.russian = o.getString("russian");
        mangas.image_original = "http://shikimori.org" + o.getJSONObject("image").getString("original");
        mangas.image_preview = "http://shikimori.org" + o.getJSONObject("image").getString("preview");
        mangas.image_x96 = "http://shikimori.org" + o.getJSONObject("image").getString("x96");
        mangas.image_x64 = "http://shikimori.org" + o.getJSONObject("image").getString("x64");
        mangas.url = o.getString("url");
        mangas.volumes = o.getString("volumes");
        mangas.chapters = o.getString("chapters");
        return mangas;
    }
}