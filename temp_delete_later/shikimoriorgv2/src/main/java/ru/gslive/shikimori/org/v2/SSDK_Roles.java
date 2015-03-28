package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Roles implements Serializable {
	private static final long serialVersionUID = 1L;
	String roles_russian;
	String id;
	String name;
	String russian;
	String image_original;
	String image_preview;
	String image_x96;
	String image_x64;
	String url;
	
    public static SSDK_Roles parse(JSONObject o, Boolean character) throws NumberFormatException, JSONException{
        SSDK_Roles roles = new SSDK_Roles();
        if (character){
        	if (!o.isNull("character")){
        		roles.roles_russian = o.getString("roles_russian");
       	        roles.id = o.getJSONObject("character").getString("id");
        	    roles.name = o.getJSONObject("character").getString("name");
        	    roles.russian = o.getJSONObject("character").getString("russian");
        	    roles.image_original = "http://shikimori.org" + o.getJSONObject("character").getJSONObject("image").getString("original");
        	    roles.image_preview = "http://shikimori.org" + o.getJSONObject("character").getJSONObject("image").getString("preview");
        	    roles.image_x96 = "http://shikimori.org" + o.getJSONObject("character").getJSONObject("image").getString("x96");
        	    roles.image_x64 = "http://shikimori.org" + o.getJSONObject("character").getJSONObject("image").getString("x64");
        	    roles.url = o.getJSONObject("character").getString("url");
        	}else{
        		return null;
        	}
        }else{
        	if (!o.isNull("person")){
        		roles.roles_russian = o.getString("roles_russian");
       	        roles.id = o.getJSONObject("person").getString("id");
        	    roles.name = o.getJSONObject("person").getString("name");
        	    roles.russian = "";
        	    roles.image_original = "http://shikimori.org" + o.getJSONObject("person").getJSONObject("image").getString("original");
        	    roles.image_preview = "http://shikimori.org" + o.getJSONObject("person").getJSONObject("image").getString("preview");
        	    roles.image_x96 = null;
        	    roles.image_x64 = "http://shikimori.org" + o.getJSONObject("person").getJSONObject("image").getString("x64");
        	    roles.url = o.getJSONObject("person").getString("url");
        	}else{
        		return null;
        	}
        }
        
    	return roles;
    }
}