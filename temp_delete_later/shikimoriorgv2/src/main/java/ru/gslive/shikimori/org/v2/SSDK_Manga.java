package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Manga implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String name;
	String russian;
	String image_original;
	String image_preview;
	String image_x96;
	String image_x64;
	String url;
	String english;
	String kind;
	String aired_at;
	String released_at;
	String volumes;
	String chapters;
	String score;
	String description;
	String description_html;
	Boolean favoured;
	Boolean anons;
	Boolean ongoing;
	String thread_id;
	String genres;
	String publishers;
	String user_rate;
	String user_score;
	String user_rate_id;
	int user_list;
	String user_chapters;
	String read_manga;
	String rewatches;
	String text;


    public static SSDK_Manga parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Manga manga = new SSDK_Manga();
        manga.id = o.getString("id");
        manga.name = o.getString("name").replaceAll("&quot;", "\"");
        manga.russian = o.getString("russian").replaceAll("&quot;", "\"");
        manga.image_original = "http://shikimori.org" + o.getJSONObject("image").getString("original");
        manga.image_preview = "http://shikimori.org" + o.getJSONObject("image").getString("preview");
        manga.image_x96 = "http://shikimori.org" + o.getJSONObject("image").getString("x96");
        manga.image_x64 = "http://shikimori.org" + o.getJSONObject("image").getString("x64");
        manga.url = o.getString("url");
        manga.english = o.getString("english");
        manga.kind = o.getString("kind");
        manga.aired_at = o.getString("aired_on");
        manga.released_at = o.getString("released_on");
        manga.volumes = o.getString("volumes");
        if (manga.volumes.equals("0")){
        	manga.volumes = "?";
        }
        manga.chapters = o.getString("chapters");
        if (manga.chapters.equals("0")){
        	manga.chapters = "?";
        }
        manga.score = o.getString("score");
        manga.description = o.getString("description");
        if(!o.isNull("description_html")){
        	manga.description_html = o.getString("description_html");
        }else{
        	manga.description_html = manga.description;
        }
        manga.favoured = o.getBoolean("favoured");
        manga.anons = o.getBoolean("anons");
        manga.ongoing = o.getBoolean("ongoing");
        manga.thread_id = o.getString("thread_id");
        manga.user_rate = o.getString("user_rate");
        
        if (!o.getString("user_rate").equals("null")){
        	manga.user_rate_id = o.getJSONObject("user_rate").getString("id");
        	manga.user_score = o.getJSONObject("user_rate").getString("score");
        	manga.user_list = o.getJSONObject("user_rate").getInt("status");
        	manga.user_chapters = o.getJSONObject("user_rate").getString("chapters");
        	manga.rewatches = o.getJSONObject("user_rate").getString("rewatches");
	        if (!o.getJSONObject("user_rate").getString("text").equals("null"))
	        	manga.text = o.getJSONObject("user_rate").getString("text");
	        else
	        	manga.text = "";
        }else{
        	manga.user_score = "0";
        	manga.user_list = -1;
        	manga.user_chapters = "0";
        	manga.rewatches = "0";
        	manga.text = "";
        }
                
        JSONArray a_genres = o.getJSONArray("genres");
        manga.genres = "";
        for (int i=0; i < a_genres.length();i++){
        	manga.genres += a_genres.getJSONObject(i).getString("russian")+ ", ";
    	}

        JSONArray a_publishers = o.getJSONArray("publishers");
        manga.publishers = "";
        for (int i=0; i < a_publishers.length();i++){
        	manga.publishers += a_publishers.getJSONObject(i).getString("name")+ ", ";
    	}

        if (o.has("read_manga_id")){
	        if (o.getString("read_manga_id").startsWith("rm_")){
	        	manga.read_manga = "http://readmanga.ru/"+o.getString("read_manga_id").substring(3);
	        }else if(o.getString("read_manga_id").startsWith("am_")){
	        	manga.read_manga = "http://adultmanga.ru/"+o.getString("read_manga_id").substring(3);
	        }else{
	        	manga.read_manga = "null";
	        }
        }else{
        	manga.read_manga = "null";
        }
        
        return manga;
    }
}