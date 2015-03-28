package ru.gslive.shikimori.org.v2;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_Calendar implements Serializable {
	private static final long serialVersionUID = 1L;
	public long next_time;
	public String next_episode;
	public String next_episode_at;
	public String id;
	public String anime_name;
	public String anime_russian;
	public String anime_image_original;
	public String anime_image_preview;
	public String anime_image_x96;
	public String anime_image_x64;
	public Boolean anime_ongoing;
	public Boolean anime_anons;
	public String anime_episodes;
	public String anime_episodes_aired;


    @SuppressLint({ "DefaultLocale", "SimpleDateFormat" })
	public static SSDK_Calendar parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_Calendar calendar = new SSDK_Calendar();

        String toParse = o.getString("next_episode_at").split(".000")[0].replace("T", " "); // Results in "2-5-2012 20:43"
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d H:m:s"); // I assume d-M, you may refer to M-d for month-day instead.
        Date date2 = null;
		try {
			date2 = formatter.parse(toParse);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // You will need try/catch around this
        long startTime = date2.getTime();
        
        calendar.next_time = startTime;
       
        calendar.next_episode = o.getString("next_episode");
        calendar.next_episode_at = o.getString("next_episode_at").split("T")[0];
       
        //Log.d(Constants.LogTag, "time " + startTime + " - " + calendar.next_episode_at);
        
        calendar.id = o.getJSONObject("anime").getString("id");
        calendar.anime_name = o.getJSONObject("anime").getString("name").replaceAll("&quot;", "\"");
        calendar.anime_russian = o.getJSONObject("anime").getString("russian").replaceAll("&quot;", "\"");
        calendar.anime_image_original = Constants.ShikiLink + o.getJSONObject("anime").getJSONObject("image").getString("original");
        calendar.anime_image_preview = Constants.ShikiLink + o.getJSONObject("anime").getJSONObject("image").getString("preview");
        calendar.anime_image_x96 = Constants.ShikiLink + o.getJSONObject("anime").getJSONObject("image").getString("x96");
        calendar.anime_image_x64 = Constants.ShikiLink + o.getJSONObject("anime").getJSONObject("image").getString("x64");
        calendar.anime_ongoing = o.getJSONObject("anime").getBoolean("ongoing");
        calendar.anime_anons = o.getJSONObject("anime").getBoolean("anons");
        calendar.anime_episodes = o.getJSONObject("anime").getString("episodes");
        calendar.anime_episodes_aired = o.getJSONObject("anime").getString("episodes_aired");     
       
        return calendar;
    }
}