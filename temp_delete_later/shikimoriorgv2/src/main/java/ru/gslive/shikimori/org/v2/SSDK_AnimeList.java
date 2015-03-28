package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SSDK_AnimeList implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String score;
	int score_int;
	int status;
	String episodes;
	String status_name;
	String anime_id;
	String anime_name;
	String anime_russian;
	String anime_image_original;
	String anime_image_preview;
	String anime_image_x96;
	String anime_image_x64;
	String anime_url;
	Boolean anime_ongoing;
	Boolean anime_anons;
	String anime_episodes;
	String anime_episodes_aired;
	String anime_user_text;
	
    public static SSDK_AnimeList parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_AnimeList animelist = new SSDK_AnimeList();
        animelist.id = o.getString("id");
        if (o.isNull("score") || o.getString("score").equals("0")){
        	animelist.score = "-";
        	animelist.score_int = 0;
        }else{
        	animelist.score = o.getString("score");
        	animelist.score_int = o.getInt("score");
        }
        animelist.status = o.getInt("status");
        animelist.episodes = o.getString("episodes");
       
        animelist.anime_id = o.getJSONObject("anime").getString("id");
        animelist.anime_name = o.getJSONObject("anime").getString("name").replaceAll("&quot;", "\"");
        animelist.anime_russian = o.getJSONObject("anime").getString("russian").replaceAll("&quot;", "\"");
        animelist.anime_image_original = "http://shikimori.org" + o.getJSONObject("anime").getJSONObject("image").getString("original");
        animelist.anime_image_preview = "http://shikimori.org" + o.getJSONObject("anime").getJSONObject("image").getString("preview");
        animelist.anime_image_x96 = "http://shikimori.org" + o.getJSONObject("anime").getJSONObject("image").getString("x96");
        animelist.anime_image_x64 = "http://shikimori.org" + o.getJSONObject("anime").getJSONObject("image").getString("x64");
        animelist.anime_url = o.getJSONObject("anime").getString("url");
        animelist.anime_ongoing = o.getJSONObject("anime").getBoolean("ongoing");
        animelist.anime_anons = o.getJSONObject("anime").getBoolean("anons");
        animelist.anime_episodes = o.getJSONObject("anime").getString("episodes");
        animelist.anime_episodes_aired = o.getJSONObject("anime").getString("episodes_aired");
        if (!animelist.anime_ongoing && !animelist.anime_anons){
        	animelist.anime_episodes_aired = animelist.anime_episodes;
        }
        if (animelist.anime_episodes.equals("0")){
        	animelist.anime_episodes = "?";
        }
        if (o.isNull("text")){
        	animelist.anime_user_text = "";
        }else{
        	animelist.anime_user_text = o.getString("text");
        }
        
        if (animelist.anime_ongoing){
        	animelist.status_name = "Онгоинг";
        }else{
        	if(animelist.anime_anons){
        		animelist.status_name = "Анонс";
        	}else{
        		animelist.status_name = "Вышло";
        	}
        }
        //animelist.status_name = o.getString("status_name");
        Log.d("status_name","-> " + animelist.status_name);
        return animelist;
    }
}