package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Relation implements Serializable {
	private static final long serialVersionUID = 1L;
	String type;
	String relation_type;
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
	
    public static SSDK_Relation parse(JSONObject o, Boolean adaptation) throws NumberFormatException, JSONException{
        SSDK_Relation relations = new SSDK_Relation();
        if (adaptation){
        	if (o.getString("relation").equals("Adaptation")){
        		relations.relation_type = o.getString("relation_russian");
                if (!o.isNull("anime")){
                	relations.relation_type = "anime";
        	        relations.id = o.getJSONObject("anime").getString("id");
        	        relations.name = o.getJSONObject("anime").getString("name");
        	        relations.russian = o.getJSONObject("anime").getString("russian");
        	        relations.image_original = "http://shikimori.org" + o.getJSONObject("anime").getJSONObject("image").getString("original");
        	        relations.image_preview = "http://shikimori.org" + o.getJSONObject("anime").getJSONObject("image").getString("preview");
        	        relations.image_x96 = "http://shikimori.org" + o.getJSONObject("anime").getJSONObject("image").getString("x96");
        	        relations.image_x64 = "http://shikimori.org" + o.getJSONObject("anime").getJSONObject("image").getString("x64");
        	        relations.url = o.getJSONObject("anime").getString("url");
        	        relations.anons = o.getJSONObject("anime").getBoolean("anons");
        	        relations.ongoing = o.getJSONObject("anime").getBoolean("ongoing");
        	        relations.episodes = o.getJSONObject("anime").getString("episodes");
        	        relations.episodes_aired = o.getJSONObject("anime").getString("episodes_aired");
        	        if (!relations.ongoing && !relations.anons){
        	        	relations.episodes_aired = relations.episodes;
        	        }
                }
                if (!o.isNull("manga")){
                	relations.relation_type = "manga";
        	        relations.id = o.getJSONObject("manga").getString("id");
        	        relations.name = o.getJSONObject("manga").getString("name");
        	        relations.russian = o.getJSONObject("manga").getString("russian");
        	        relations.image_original = "http://shikimori.org" + o.getJSONObject("manga").getJSONObject("image").getString("original");
        	        relations.image_preview = "http://shikimori.org" + o.getJSONObject("manga").getJSONObject("image").getString("preview");
        	        relations.image_x96 = "http://shikimori.org" + o.getJSONObject("manga").getJSONObject("image").getString("x96");
        	        relations.image_x64 = "http://shikimori.org" + o.getJSONObject("manga").getJSONObject("image").getString("x64");
        	        relations.url = o.getJSONObject("manga").getString("url");
        	        relations.anons = o.getJSONObject("manga").getBoolean("anons");
        	        relations.ongoing = o.getJSONObject("manga").getBoolean("ongoing");
        	        relations.episodes = o.getJSONObject("manga").getString("volumes");
        	        relations.episodes_aired = o.getJSONObject("manga").getString("chapters");
                }
        	}else{
        		return null;
        	}
        }else{
        	if (!o.getString("relation").equals("Adaptation")){
        		relations.relation_type = o.getString("relation_russian");
        		if (!o.isNull("anime")){
        			relations.relation_type = "anime";
        			relations.id = o.getJSONObject("anime").getString("id");
        			relations.name = o.getJSONObject("anime").getString("name");
        			relations.russian = o.getJSONObject("anime").getString("russian");
        			relations.image_original = "http://shikimori.org" + o.getJSONObject("anime").getJSONObject("image").getString("original");
        			relations.image_preview = "http://shikimori.org" + o.getJSONObject("anime").getJSONObject("image").getString("preview");
        			relations.image_x96 = "http://shikimori.org" + o.getJSONObject("anime").getJSONObject("image").getString("x96");
        			relations.image_x64 = "http://shikimori.org" + o.getJSONObject("anime").getJSONObject("image").getString("x64");
        			relations.url = o.getJSONObject("anime").getString("url");
        			relations.anons = o.getJSONObject("anime").getBoolean("anons");
        			relations.ongoing = o.getJSONObject("anime").getBoolean("ongoing");
        			relations.episodes = o.getJSONObject("anime").getString("episodes");
        			relations.episodes_aired = o.getJSONObject("anime").getString("episodes_aired");
        			if (!relations.ongoing && !relations.anons){
        				relations.episodes_aired = relations.episodes;
        			}
        		}
        		if (!o.isNull("manga")){
        			relations.relation_type = "manga";
        			relations.id = o.getJSONObject("manga").getString("id");
        			relations.name = o.getJSONObject("manga").getString("name");
        			relations.russian = o.getJSONObject("manga").getString("russian");
        			relations.image_original = "http://shikimori.org" + o.getJSONObject("manga").getJSONObject("image").getString("original");
        			relations.image_preview = "http://shikimori.org" + o.getJSONObject("manga").getJSONObject("image").getString("preview");
        			relations.image_x96 = "http://shikimori.org" + o.getJSONObject("manga").getJSONObject("image").getString("x96");
        			relations.image_x64 = "http://shikimori.org" + o.getJSONObject("manga").getJSONObject("image").getString("x64");
        			relations.url = o.getJSONObject("manga").getString("url");
        			relations.anons = o.getJSONObject("manga").getBoolean("anons");
        			relations.ongoing = o.getJSONObject("manga").getBoolean("ongoing");
        			relations.episodes = o.getJSONObject("manga").getString("volumes");
        			relations.episodes_aired = o.getJSONObject("manga").getString("chapters");
        		}
        	}else{
        		return null;
        	}
        }
        
    	return relations;
    }
}