package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Members implements Serializable {
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
	
    public static SSDK_Members parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Members members = new SSDK_Members();
        members.id = o.getString("id");
        members.nickname = o.getString("nickname");
        members.avatar = o.getString("avatar");
        if (o.getJSONObject("image").getString("x160").contains("http:"))
        	members.image_x160 = o.getJSONObject("image").getString("x160");
        else
        	members.image_x160 = "http://shikimori.org" + o.getJSONObject("image").getString("x160");
        
        if (o.getJSONObject("image").getString("x148").contains("http:"))
        	members.image_x148 = o.getJSONObject("image").getString("x148");
        else
        	members.image_x148 = "http://shikimori.org" + o.getJSONObject("image").getString("x148");
        	
        if (o.getJSONObject("image").getString("x80").contains("http:"))
        	members.image_x80 = o.getJSONObject("image").getString("x80");
        else
        	members.image_x80 = "http://shikimori.org" + o.getJSONObject("image").getString("x80");
        	
        if (o.getJSONObject("image").getString("x64").contains("http:"))
        	members.image_x64 = o.getJSONObject("image").getString("x64");
        else
        	members.image_x64 = "http://shikimori.org" + o.getJSONObject("image").getString("x64");
        	
        if (o.getJSONObject("image").getString("x48").contains("http:"))
        	members.image_x48 = o.getJSONObject("image").getString("x48");
        else
        	members.image_x48 = "http://shikimori.org" + o.getJSONObject("image").getString("x48");
        	
        if (o.getJSONObject("image").getString("x32").contains("http:"))
        	members.image_x32 = o.getJSONObject("image").getString("x32");
        else
        	members.image_x32 = "http://shikimori.org" + o.getJSONObject("image").getString("x32");
        	
        if (o.getJSONObject("image").getString("x16").contains("http:"))
        	members.image_x16 = o.getJSONObject("image").getString("x16");
        else
        	members.image_x16 = "http://shikimori.org" + o.getJSONObject("image").getString("x16");
        return members;
    }
}