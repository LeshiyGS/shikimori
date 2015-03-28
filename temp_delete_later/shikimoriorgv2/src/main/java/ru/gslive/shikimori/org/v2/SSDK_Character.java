package ru.gslive.shikimori.org.v2;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class SSDK_Character implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String name;
	String russian;
	String altname;
	String japanese;
	String image_original;
	String image_preview;
	String image_x96;
	String image_x64;
	String url;
	String description;
	String description_html;
	String description_clean;
	Boolean favoured;
	String thread_id;
	ArrayList<String> seyu = new ArrayList<>();
	ArrayList<String> anime = new ArrayList<>();
	ArrayList<String> manga = new ArrayList<>();
	ArrayList<String> spoiler_names = new ArrayList<>();


    public static SSDK_Character parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Character character = new SSDK_Character();
        int iii = 0;
        int mmm = 0;
        int iiii = iii;

        Document json = Jsoup.parse(o.getString("description_html"));

        for(Element element : json.select("a[class^=c-video b-video]")){
            //
            Document img_json = Jsoup.parse(element.html());
            String img_link="";
            for(Element img : img_json.select("img")){
                img_link = img.attr("src");
            }
            element.before("[SEP]VI:" + element.attr("href")+"" + img_link + "!");
            element.remove();
            //element.after(" <a href="+element.attr("href")+"a>Ссылка на видео</a>");
        }

        for(Element element : json.select("img")){
            if (!element.attr("src").contains("http")) 	element.attr("src", Constants.SERVER + element.attr("src"));
        }

        for(Element element : json.select("a")){
            if (!element.attr("href").contains("http")) element.attr("href", Constants.SERVER + element.attr("href"));
        }

        for(Element element : json.select("div[class=after]")){
            element.before("[SEP]SP:false:" + iii + "!");
            element.remove();
            iii++;
        }

        for(Element element : json.select("div[class=b-spoiler unprocessed]")){
            character.spoiler_names.add(element.child(0).select("label").text());
            element.child(0).select("label").remove();
        }

        for(Element element : json.select("div[class=before")){
            element.after("[SEP]SP:true:" + iiii + "!");
            element.remove();
            iiii++;
        }

        for(Element element : json.select("del")){
            element.remove();
        }

        for(Element element : json.select("img")){
            if (!element.attr("src").contains("/images/user/") && !element.attr("src").contains("/images/smileys/")){
                element.before("[SEP]CI:" + element.attr("src")+"!");
                element.remove();
                mmm++;
            }
        }
        
		character.description_clean = json.html();
        
        character.id = o.getString("id");
        character.name = o.getString("name");
        character.russian = o.getString("russian");
        character.altname = o.getString("altname");
        character.japanese = o.getString("japanese");
        character.image_original = o.getJSONObject("image").getString("original");
        character.image_preview = o.getJSONObject("image").getString("preview");
        character.image_x96 = o.getJSONObject("image").getString("x96");
        character.image_x64 = o.getJSONObject("image").getString("x64");
    	character.url = o.getString("url");
    	character.description = o.getString("description");
    	character.description_html = o.getString("description_html");
    	character.favoured = o.getBoolean("favoured");
    	character.thread_id = o.getString("thread_id");
    	JSONArray a_seyu = o.getJSONArray("seyu");
    	JSONArray a_anime = o.getJSONArray("animes");
    	JSONArray a_manga = o.getJSONArray("mangas");
    	for (int i=0; i < a_seyu.length();i++){
    		/*Log.d("id", a_seyu.getJSONObject(i).getString("id"));
    		Log.d("name", a_seyu.getJSONObject(i).getString("name"));
    		Log.d("original", a_seyu.getJSONObject(i).getJSONObject("image").getString("original"));*/
    		character.seyu.add(a_seyu.getJSONObject(i).getString("id")+";"
    				+a_seyu.getJSONObject(i).getString("name")+ ";"
    				  +a_seyu.getJSONObject(i).getJSONObject("image").getString("original"));
    	}
    	for (int i=0; i < a_anime.length();i++){
    		character.anime.add(a_anime.getJSONObject(i).getString("id")+";"
    				+a_anime.getJSONObject(i).getString("name")+ ";"
    				  +a_anime.getJSONObject(i).getJSONObject("image").getString("original"));
    	}
    	for (int i=0; i < a_manga.length();i++){
    		character.manga.add(a_manga.getJSONObject(i).getString("id")+";"
    				+a_manga.getJSONObject(i).getString("name")+ ";"
    				  +a_manga.getJSONObject(i).getJSONObject("image").getString("original"));
    	}
        
        return character;
    }
}