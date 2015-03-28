package ru.gslive.shikimori.org.v2;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_MangaList implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String score;
	int score_int;
	int status;
	int volumes;
	String chapters;
	String status_name;
	String manga_id;
	String manga_name;
	String manga_russian;
	String manga_image_original;
	String manga_image_preview;
	String manga_image_x96;
	String manga_image_x64;
	String manga_url;
	Boolean manga_ongoing;
	Boolean manga_anons;
	String manga_volumes;
	String manga_chapters;
	String manga_user_text;
	
    public static SSDK_MangaList parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_MangaList mangaList = new SSDK_MangaList();
        mangaList.id = o.getString("id");
        if (o.isNull("score") || o.getString("score").equals("0")){
        	mangaList.score = "-";
        	mangaList.score_int = 0;
        }else{
        	mangaList.score = o.getString("score");
        	mangaList.score_int = o.getInt("score");
        }
        mangaList.status = o.getInt("status");
        mangaList.volumes = o.getInt("volumes");
        mangaList.chapters = o.getString("chapters");
        //mangaList.status_name = o.getString("status_name");
        mangaList.manga_id = o.getJSONObject("manga").getString("id");
        mangaList.manga_name = o.getJSONObject("manga").getString("name").replaceAll("&quot;", "\"");
        mangaList.manga_russian = o.getJSONObject("manga").getString("russian").replaceAll("&quot;", "\"");
        mangaList.manga_image_original = "http://shikimori.org" + o.getJSONObject("manga").getJSONObject("image").getString("original");
        mangaList.manga_image_preview = "http://shikimori.org" + o.getJSONObject("manga").getJSONObject("image").getString("preview");
        mangaList.manga_image_x96 = "http://shikimori.org" + o.getJSONObject("manga").getJSONObject("image").getString("x96");
        mangaList.manga_image_x64 = "http://shikimori.org" + o.getJSONObject("manga").getJSONObject("image").getString("x64");
        mangaList.manga_url = o.getJSONObject("manga").getString("url");
        mangaList.manga_ongoing = o.getJSONObject("manga").getBoolean("ongoing");
        mangaList.manga_anons = o.getJSONObject("manga").getBoolean("anons");
        mangaList.manga_volumes = o.getJSONObject("manga").getString("volumes");
        mangaList.manga_chapters = o.getJSONObject("manga").getString("chapters");
        if (mangaList.manga_chapters.equals("0")){
        	mangaList.manga_chapters = "?";
        }
        if (o.isNull("text")){
        	mangaList.manga_user_text = "";
        }else{
        	mangaList.manga_user_text = o.getString("text");
        }
        
        if (mangaList.manga_ongoing){
        	mangaList.status_name = "Онгоинг";
        }else{
        	if(mangaList.manga_anons){
        		mangaList.status_name = "Анонс";
        	}else{
        		mangaList.status_name = "Вышло";
        	}
        }
        
        return mangaList;
    }
}