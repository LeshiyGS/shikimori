package ru.gslive.shikimori.org.v2;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Community_Users implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String nickname;
	String avatar;
	String image_x160;
	String image_x148;
	String image_x80;
	String image_x64;
	String image_x48;
	String image_x32;
	String image_x16;
	String last_online;
	
    public static SSDK_Community_Users parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Community_Users user = new SSDK_Community_Users();
                
        user.id = o.getString("id");
        user.nickname = o.getString("nickname");

        if (o.getString("avatar").contains("http:"))
        	user.avatar = o.getString("avatar");
        else
        	user.avatar = "http://shikimori.org" + o.getString("avatar");

        if (o.isNull("last_online_at")){
        	user.last_online = "0000-00-00T00:00:00.000+00:00";
        }else{
        	user.last_online = o.getString("last_online_at");
        }
        
        if (o.getJSONObject("image").getString("x160").contains("http:"))
        	user.image_x160 = o.getJSONObject("image").getString("x160");
        else
        	user.image_x160 = "http://shikimori.org" + o.getJSONObject("image").getString("x160");
        
        if (o.getJSONObject("image").getString("x148").contains("http:"))
        	user.image_x148 = o.getJSONObject("image").getString("x148");
        else
        	user.image_x148 = "http://shikimori.org" + o.getJSONObject("image").getString("x148");
        	
        if (o.getJSONObject("image").getString("x80").contains("http:"))
        	user.image_x80 = o.getJSONObject("image").getString("x80");
        else
        	user.image_x80 = "http://shikimori.org" + o.getJSONObject("image").getString("x80");
        	
        if (o.getJSONObject("image").getString("x64").contains("http:"))
        	user.image_x64 = o.getJSONObject("image").getString("x64");
        else
        	user.image_x64 = "http://shikimori.org" + o.getJSONObject("image").getString("x64");
        	
        if (o.getJSONObject("image").getString("x48").contains("http:"))
        	user.image_x48 = o.getJSONObject("image").getString("x48");
        else
        	user.image_x48 = "http://shikimori.org" + o.getJSONObject("image").getString("x48");
        	
        if (o.getJSONObject("image").getString("x32").contains("http:"))
        	user.image_x32 = o.getJSONObject("image").getString("x32");
        else
        	user.image_x32 = "http://shikimori.org" + o.getJSONObject("image").getString("x32");
        	
        if (o.getJSONObject("image").getString("x16").contains("http:"))
        	user.image_x16 = o.getJSONObject("image").getString("x16");
        else
        	user.image_x16 = "http://shikimori.org" + o.getJSONObject("image").getString("x16");
        
        return user;
    }
}