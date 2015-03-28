package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Images implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String original;
	String main;
	String preview;
	
    public static SSDK_Images parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Images images = new SSDK_Images();
        images.id = o.getString("id");
        images.original = "http://shikimori.org" + o.getString("original");
        images.preview = "http://shikimori.org" + o.getString("preview");
        images.main = "http://shikimori.org" + o.getString("main");
        
        return images;
    }
}