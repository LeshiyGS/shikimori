package ru.gslive.shikimori.org.v2;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class SSDK_Comments implements Serializable {
	private static final long serialVersionUID = 1L;
	String date;
	String body;
    String body_html;
    String body_clean;
    String id;
    String user_id;
    String user_avatar;
    String user_name;
    Boolean can_be_edited;
    Boolean review;
    Boolean viewed;
    Boolean offtopic;
    ArrayList<String> spoiler_names= new ArrayList<>();

    public static SSDK_Comments parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Comments comments = new SSDK_Comments();

        int iii = 0;
        int mmm = 0;
        int iiii = iii;

        Document json = Jsoup.parse(o.getString("html_body"));

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
            comments.spoiler_names.add(element.child(0).select("label").text());
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

        comments.viewed = o.getBoolean("viewed");
        comments.review = o.getBoolean("review");
        comments.offtopic = o.getBoolean("offtopic");
        comments.date = Functions.in_time(o.getString("created_at"), true);
        comments.body = o.getString("body");
		comments.body_html = o.getString("html_body");
		comments.body_clean = json.html();
        comments.user_id = o.getJSONObject("user").getString("id");
		comments.can_be_edited = o.getBoolean("can_be_edited");
		comments.id = o.getString("id");
		comments.user_name = o.getJSONObject("user").getString("nickname");
			
		if (o.getJSONObject("user").getJSONObject("image").getString("x148").contains("http")){
			comments.user_avatar = o.getJSONObject("user").getJSONObject("image").getString("x148");
		}else {
			comments.user_avatar = "http://shikimori.org" + o.getJSONObject("user").getJSONObject("image").getString("x148");
		} 
        
        return comments;
    }
}