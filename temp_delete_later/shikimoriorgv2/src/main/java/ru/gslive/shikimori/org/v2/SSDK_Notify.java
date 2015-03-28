package ru.gslive.shikimori.org.v2;

import android.annotation.SuppressLint;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Notify implements Serializable {
	private static final long serialVersionUID = 1L;
	public String id;
	public String kind;
	public Boolean read;
	public String body;
	public String html_body;
	public String created_at;
	public String from_id;
    public String from_nickname;
    public String from_avatar;
    public String to_id;
    public String to_nickname;
    public String to_avatar;
    public String linked_type;
    public String linked_topic_url;

    @SuppressLint("DefaultLocale")
	public static SSDK_Notify parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Notify notify = new SSDK_Notify();
               
        notify.id = o.getString("id");
        notify.kind = o.getString("kind");
        notify.read = o.getBoolean("read");
        notify.body = o.getString("body"); 
        notify.html_body = o.getString("html_body");
		notify.created_at = Functions.in_time(o.getString("created_at"), true);
        
		notify.from_id = o.getJSONObject("from").getString("id");
		notify.from_nickname = o.getJSONObject("from").getString("nickname");
		if (o.getJSONObject("from").getString("avatar").contains("http")){
			notify.from_avatar = o.getJSONObject("from").getString("avatar");
		}else{
			notify.from_avatar = "http://shikimori.org" + o.getJSONObject("from").getString("avatar");
		}
        
		notify.to_id = o.getJSONObject("to").getString("id");
		notify.to_nickname = o.getJSONObject("to").getString("nickname");
		if (o.getJSONObject("to").getString("avatar").contains("http")){
			notify.to_avatar = o.getJSONObject("to").getString("avatar");
		}else{
			notify.to_avatar = "http://shikimori.org" + o.getJSONObject("to").getString("avatar");
		}
		if (!o.isNull("linked")){
			notify.linked_topic_url = o.getJSONObject("linked").getString("thread_id");
			notify.linked_type = "topic";
			if (notify.linked_topic_url.equals(notify.from_id)){
				notify.linked_type = "wall";
			}
		}else{
			notify.linked_topic_url = null;
			notify.linked_type = null;
		}
		
        return notify;
    }
}