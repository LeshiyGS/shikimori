package ru.gslive.shikimori.org.v2;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class SSDK_Club implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String name;
	String description;
	String description_clean;
	String description_html;
	String image_original;
	String image_main;
	String image_x96;
	String image_x73;
	String image_x48;
	String thread_id;
	Boolean inGroup;
	ArrayList<String> spoiler_names = new ArrayList<String>();


    public static SSDK_Club parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Club club = new SSDK_Club();
        int iii=0;
        int iiii = iii;
        String temp = o.getString("description_html");

        Document json = Jsoup.parse(temp);

        for(Element element : json.select("a[class^=c-video b-video]")){
            element.after(" <a href="+element.attr("href")+"a>Ссылка на видео</a>");
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
            //element.before("[STARTSPNAME:" + element.child(0).select("label").text() + "ENDSPNAME]");
            club.spoiler_names.add(element.child(0).select("label").text());
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

        int mmm = 0;

        for(Element element : json.select("img")){
            if (!element.attr("src").contains("/images/user/") && !element.attr("src").contains("/images/smileys/")){
                element.before("[SEP]CI:" + element.attr("src")+"!");
                element.remove();
                mmm++;
            }
        }
        
		club.description_clean = json.html();
        club.id = o.getString("id");
        club.name = o.getString("name");
        club.description = o.getString("description");
        club.description_html = o.getString("description_html");
        club.image_original = Constants.SERVER+o.getJSONObject("logo").getString("original");
        club.image_main = Constants.SERVER +o.getJSONObject("logo").getString("main");
        club.image_x96 = Constants.SERVER+o.getJSONObject("logo").getString("x96");
        club.image_x73 = Constants.SERVER+o.getJSONObject("logo").getString("x73");
        club.image_x48 = Constants.SERVER+o.getJSONObject("logo").getString("x48");
        club.thread_id = o.getString("thread_id");
        if (o.isNull("user_role")){
        	club.inGroup = false;
        }else{
        	club.inGroup = true;
        }
       
        return club;
    }
}