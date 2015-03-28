package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Anime implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String name;
	String russian;
	String image_original;
	String image_preview;
	String image_x96;
	String image_x64;
	String url;
	String rating;
	String english;
	String kind;
	String aired_at;
	String released_at;
	String episodes;
	String episodes_aired;
	String duration;
	String score;
	String description;
	String description_html;
	Boolean favoured;
	Boolean anons;
	Boolean ongoing;
	String user_rate_id;
	String thread_id;
	String genres;
	String studios;
	String user_rate;
	String user_score;
	int user_list;
	String user_episodes;
	String rewatches;
	String text;


    public static SSDK_Anime parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Anime anime = new SSDK_Anime();
        anime.id = o.getString("id");
        anime.name = o.getString("name").replaceAll("&quot;", "\"");
        anime.russian = o.getString("russian").replaceAll("&quot;", "\"");
        anime.image_original = "http://shikimori.org" + o.getJSONObject("image").getString("original");
        anime.image_preview = "http://shikimori.org" + o.getJSONObject("image").getString("preview");
        anime.image_x96 = "http://shikimori.org" + o.getJSONObject("image").getString("x96");
        anime.image_x64 = "http://shikimori.org" + o.getJSONObject("image").getString("x64");
        anime.url = o.getString("url");
        anime.rating = o.getString("rating");
        anime.english = o.getString("english");
        anime.kind = o.getString("kind");
        anime.aired_at = o.getString("aired_on");
        anime.released_at = o.getString("released_on");
        anime.episodes = o.getString("episodes");
        if (anime.episodes.equals("0")){
        	anime.episodes = "?";
        }
        anime.episodes_aired = o.getString("episodes_aired");
        anime.duration = o.getString("duration");
        anime.score = o.getString("score");
        anime.description = o.getString("description");
        if (o.isNull("description_html")){
        	anime.description_html = o.getString("description");
        }else{
        	anime.description_html = o.getString("description_html");
        }
        anime.favoured = o.getBoolean("favoured");
        anime.anons = o.getBoolean("anons");
        anime.ongoing = o.getBoolean("ongoing");
        anime.thread_id = o.getString("thread_id");
        anime.user_rate = o.getString("user_rate");
        
        if (!o.getString("user_rate").equals("null")){
        	anime.user_rate_id = o.getJSONObject("user_rate").getString("id");
        	anime.user_score = o.getJSONObject("user_rate").getString("score");
        	anime.user_list = o.getJSONObject("user_rate").getInt("status");
	        anime.user_episodes = o.getJSONObject("user_rate").getString("episodes");
	        anime.rewatches = o.getJSONObject("user_rate").getString("rewatches");
	        if (!o.getJSONObject("user_rate").getString("text").equals("null"))
	        	anime.text = o.getJSONObject("user_rate").getString("text");
	        else
	        	anime.text = "";
        }else{
        	anime.user_score = "0";
        	anime.user_list = -1;
        	anime.user_episodes = "0";
        	anime.rewatches = "0";
        	anime.text = "";
        }
        
        JSONArray a_genres = o.getJSONArray("genres");
        anime.genres = "";
        for (int i=0; i < a_genres.length();i++){
        	anime.genres += a_genres.getJSONObject(i).getString("russian")+ ", ";
    	}

        JSONArray a_studios = o.getJSONArray("studios");
        anime.studios = "";
        for (int i=0; i < a_studios.length();i++){
        	anime.studios += a_studios.getJSONObject(i).getString("name")+ ", ";
    	}

        return anime;
    }
}