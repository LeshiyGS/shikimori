package ru.gslive.shikimori.org.v2;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.annotation.SuppressLint;

public class SSDK_Inbox implements Serializable {
	private static final long serialVersionUID = 1L;
	public String id;
	public String kind;
	public Boolean read;
	public String body;
	public String body_clean;
	public String html_body;
	public String created_at;
	public String from_id;
    public String from_nickname;
    public String from_avatar;
    public String to_id;
    public String to_nickname;
    public String to_avatar;
    ArrayList<String> spoiler_names= new ArrayList<>();

    @SuppressLint("DefaultLocale")
	public static SSDK_Inbox parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Inbox inbox = new SSDK_Inbox();

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
            inbox.spoiler_names.add(element.child(0).select("label").text());
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
        
        inbox.id = o.getString("id");
        inbox.kind = o.getString("kind");
        inbox.read = o.getBoolean("read");
        inbox.body = o.getString("body"); 
        inbox.html_body = o.getString("html_body");
		inbox.body_clean = json.html();
		inbox.created_at = Functions.in_time(o.getString("created_at"), true);
        
		inbox.from_id = o.getJSONObject("from").getString("id");
		inbox.from_nickname = o.getJSONObject("from").getString("nickname");
		if (o.getJSONObject("from").getString("avatar").contains("http")){
			inbox.from_avatar = o.getJSONObject("from").getString("avatar");
		}else{
			inbox.from_avatar = "http://shikimori.org" + o.getJSONObject("from").getString("avatar");
		}
        
		inbox.to_id = o.getJSONObject("to").getString("id");
		inbox.to_nickname = o.getJSONObject("to").getString("nickname");
		if (o.getJSONObject("to").getString("avatar").contains("http")){
			inbox.to_avatar = o.getJSONObject("to").getString("avatar");
		}else{
			inbox.to_avatar = "http://shikimori.org" + o.getJSONObject("to").getString("avatar");
		}
        return inbox;
    }
}