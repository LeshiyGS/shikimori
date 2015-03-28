package ru.gslive.shikimori.org.v2;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class SSDK_News implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String kind;
	Boolean read;
	String body;
	String body_clean;
	String html_body;
	String created_at;
	String from_id;
    String from_nickname;
    String from_avatar;
    String to_id;
    String to_nickname;
    String to_avatar;
    String l_id;
    String l_topic;
    String l_type;
    String l_name;
    String l_russian;
    String l_image;
    String linked_type;
    String linked_topic_url;
    ArrayList<String> spoiler_names= new ArrayList<>();


    @SuppressLint("DefaultLocale")
	public static SSDK_News parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_News news = new SSDK_News();

        int iii = 0;
        int mmm = 0;
        int iiii = iii;

        Document json = Jsoup.parse(o.getString("html_body"));

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
            news.spoiler_names.add(element.child(0).select("label").text());
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
        
        news.id = o.getString("id");
        news.kind = o.getString("kind");
        news.read = o.getBoolean("read");
        news.body = o.getString("body"); 
        news.html_body = o.getString("html_body");
		news.body_clean = json.html();
		news.created_at = Functions.in_time(o.getString("created_at"), true);
        
		news.from_id = o.getJSONObject("from").getString("id");
		news.from_nickname = o.getJSONObject("from").getString("nickname");
		if (o.getJSONObject("from").getString("avatar").contains("http")){
			news.from_avatar = o.getJSONObject("from").getString("avatar");
		}else{
			news.from_avatar = "http://shikimori.org" + o.getJSONObject("from").getString("avatar");
		}
        
		news.to_id = o.getJSONObject("to").getString("id");
		news.to_nickname = o.getJSONObject("to").getString("nickname");
		if (o.getJSONObject("to").getString("avatar").contains("http")){
			news.to_avatar = o.getJSONObject("to").getString("avatar");
		}else{
			news.to_avatar = "http://shikimori.org" + o.getJSONObject("to").getString("avatar");
		}
        
        String linked = o.getJSONObject("linked").getString("type");
		if (linked.equals("Topic")){
			news.l_id = o.getJSONObject("linked").getString("id");
			news.l_topic = o.getJSONObject("linked").getString("topic_url").toLowerCase();
			news.l_type = "topic";
			news.l_name = "";
			news.l_russian = "";
			news.l_image = "";
		}else{
			news.l_id = o.getJSONObject("linked").getString("id");
			news.l_topic = o.getJSONObject("linked").getString("topic_url");
			news.l_type = o.getJSONObject("linked").getString("type").toLowerCase();
			news.l_name = o.getJSONObject("linked").getString("name").replaceAll("&quot;", "\"");
			news.l_russian = o.getJSONObject("linked").getString("russian").replaceAll("&quot;", "\"");
			news.l_image = "http://shikimori.org" + o.getJSONObject("linked").getJSONObject("image").getString("preview");
		}
		if (!o.isNull("linked")){
			news.linked_topic_url = o.getJSONObject("linked").getString("thread_id");
			news.linked_type = "topic";
			if (news.linked_topic_url.equals(news.from_id)){
				news.linked_type = "wall";
			}
		}else{
			news.linked_topic_url = null;
			news.linked_type = null;
		}
        return news;
    }
}