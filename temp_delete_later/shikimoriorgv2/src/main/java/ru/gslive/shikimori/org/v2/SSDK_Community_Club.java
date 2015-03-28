package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Community_Club implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String name;
	String logo;


    public static SSDK_Community_Club parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Community_Club club = new SSDK_Community_Club();
        club.id = o.getString("id");
        club.name = o.getString("name");
        club.logo = Constants.SERVER + o.getJSONObject("logo").getString("original");

        return club;
    }
}