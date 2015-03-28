package ru.gslive.shikimori.org.v2;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import android.util.Log;

public class SSDK_People implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String name;
	String japanese;
	String image_original;
	String image_preview;
	String image_x96;
	String image_x64;
	String url;
	String job_title;
	String birthday;
	String website;
	String groupped_roles;
	Boolean producer;
	Boolean mangaka;
	Boolean seyu;
	Boolean owner = false;
	Boolean person_favoured;
	Boolean producer_favoured;
	Boolean mangaka_favoured;
	Boolean seyu_favoured;
	Boolean favoured;
	String thread_id;
	ArrayList<String> anime = new ArrayList<String>();
	ArrayList<String> manga = new ArrayList<String>();
	
    public static SSDK_People parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_People people = new SSDK_People();
        people.id = o.getString("id");
        people.name = o.getString("name");
        if (o.getString("japanese").equals("null")){
        	people.japanese = "-";
        }else{
        	people.japanese = o.getString("japanese");
        }
        people.thread_id = o.getString("thread_id");
        people.image_original = "http://shikimori.org" + o.getJSONObject("image").getString("original");
        people.image_preview = "http://shikimori.org" + o.getJSONObject("image").getString("preview");
        //people.image_x96 = "http://shikimori.org" + o.getJSONObject("image").getString("x96");
        people.image_x64 = "http://shikimori.org" + o.getJSONObject("image").getString("x64");
        people.url = o.getString("url");
        people.job_title = o.getString("job_title");
        people.groupped_roles = o.getString("groupped_roles").replaceAll("\\],\\[", "\n").replaceAll(",", ": ").replaceAll("\"", "")
        		.replaceAll("\\[", "").replaceAll("\\]", "");
        if (o.isNull("birthday")){
        	people.birthday = "-";
        }else{
        	people.birthday = o.getString("birthday");
        }
        if(o.isNull("website")){
        	people.website = "-";
        }else{
        	people.website = o.getString("website");
        }
        
        people.producer = o.getBoolean("producer");
    	people.mangaka = o.getBoolean("mangaka");
    	people.seyu = o.getBoolean("seyu");
    	people.person_favoured = o.getBoolean("person_favoured");
    	people.producer_favoured = o.getBoolean("producer_favoured");
    	people.mangaka_favoured = o.getBoolean("mangaka_favoured");
    	people.seyu_favoured = o.getBoolean("seyu_favoured");
    	if (!(people.producer || people.mangaka || people.seyu)){
    		people.owner = true;
    	}
    	if(people.person_favoured || people.producer_favoured || people.mangaka_favoured || people.seyu_favoured){
    		people.favoured = true;
    	}else{
    		people.favoured = false;
    	}
    	
    	//Log.d("temp", "-> " + o.getString("birthday"));
    	
    	JSONArray temp = o.getJSONArray("roles");
    	for(int i=0; i < temp.length();i++){
    		JSONObject temp2 = (JSONObject) temp.get(i);
    		if (temp2.getJSONArray("animes").length() > 0){
    			JSONObject temp3 = (JSONObject) temp2.getJSONArray("animes").get(0);
        		//Log.d(Constants.LogTag, "-> " + temp2.getJSONArray("animes").get(0).toString());
        		people.anime.add(temp3.getString("id")+";"+temp3.getString("name")+ ";"+temp3.getJSONObject("image").getString("original"));
        	
    		}
    	}
    	temp = o.getJSONArray("works");
    	for(int i=0; i < temp.length();i++){
    		JSONObject temp2 = (JSONObject) temp.get(i);
    		if (temp2.isNull("anime")){
    			people.manga.add(temp2.getJSONObject("manga").getString("id")+";"+temp2.getJSONObject("manga").getString("name")+ ";"+temp2.getJSONObject("manga").getJSONObject("image").getString("original"));
    		}else{
    			people.anime.add(temp2.getJSONObject("anime").getString("id")+";"+temp2.getJSONObject("anime").getString("name")+ ";"+temp2.getJSONObject("anime").getJSONObject("image").getString("original"));
    		}
    	}
    	
    	/*//Log.d("temp", "-> " + o.toString());
    	//if (o.getJSONObject("roles").has("animes")){
    		a_anime = o.getJSONArray("roles").getJSONObject("animes").optJSONArray("animes");
    		//Log.d("temp", "-> " + o.getJSONArray("animes").toString());
    		for (int i=0; i < a_anime.length();i++){
        		people.anime.add(a_anime.getJSONObject(i).getString("id")+";"
        				+a_anime.getJSONObject(i).getString("name")+ ";"
        				  +a_anime.getJSONObject(i).getJSONObject("image").getString("original"));
        		Log.d("temp", "-> " + a_anime.getJSONObject(i).getString("id")+";"
        				+a_anime.getJSONObject(i).getString("name")+ ";"
      				  +a_anime.getJSONObject(i).getJSONObject("image").getString("original"));
        	}
    	//}
    	//if(o.getJSONObject("roles").has("mangas")){
    		a_manga = o.getJSONArray("roles").getJSONArray("mangas");
    		for (int i=0; i < a_manga.length();i++){
        		people.manga.add(a_manga.getJSONObject(i).getString("id")+";"
        				+a_manga.getJSONObject(i).getString("name")+ ";"
        				  +a_manga.getJSONObject(i).getJSONObject("image").getString("original"));
        	}
        		
    	//}*/
    	
    	

    	
    	
        return people;
    }
}