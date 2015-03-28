package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_History implements Serializable {
	private static final long serialVersionUID = 1L;
	public String date;
    public String info;
    public String type;
    public String id;
    public String img;
    public String name;
    public String name_russian;


    public static SSDK_History parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_History history = new SSDK_History();
        
        String target = o.getString("target");
        history.date = Functions.in_time(o.getString("created_at"), true);
        history.info = o.getString("description");
		if (target.equals("null")){
			history.name = "";
			history.name_russian = "null";
			history.id = "";
			history.img = "";
			history.type = "";
		}else{
			history.name = o.getJSONObject("target").getString("name").replaceAll("&quot;", "\"");
			history.name_russian = o.getJSONObject("target").getString("russian").replaceAll("&quot;", "\"");
			history.id = o.getJSONObject("target").getString("id");
			history.img = "http://shikimori.org"+o.getJSONObject("target").getJSONObject("image").getString("preview");
			if (o.getJSONObject("target").getString("url").contains("/animes/")){
				history.type = "anime";
			}else if (o.getJSONObject("target").getString("url").contains("/mangas/")){
				history.type = "manga";
			}
			
		}
        
        return history;
    }
}