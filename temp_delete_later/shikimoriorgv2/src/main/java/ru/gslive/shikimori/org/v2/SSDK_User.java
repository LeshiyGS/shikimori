package ru.gslive.shikimori.org.v2;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SSDK_User implements Serializable {
	private static final long serialVersionUID = 1L;
	String id;
	String nickname;
	String avatar;
	String image_x160;
	String image_x148;
	String image_x80;
	String image_x64;
	String image_x48;
	String image_x32;
	String image_x16;
	String name;
	String sex;
	String full_years;
	String location;
	String website;
	String start_date;
	String last_online;
	Boolean is_friend;
	String[] user_manga = new String[]{"0","0","0","0","0","0"};
	String[] user_anime = new String[]{"0","0","0","0","0","0"};
	String[] user_manga_id = new String[]{"","","","","",""};
	String[] user_anime_id = new String[]{"","","","","",""};
	String[] user_manga_name = new String[]{"","","","","",""};
	String[] user_anime_name = new String[]{"","","","","",""};

	int see = 0;
	int adrop = 0;
	int total_see = 0;
	int read = 0;
	int mdrop = 0;
	int total_read = 0;
	
	Boolean hasAnime = false;
	Boolean hasManga = false;
	
	String agenres = "";
	String mgenres = "";
	String studios = "";
	String publishers = "";
	
	ArrayList<String> anime_scores_name = new ArrayList<String>();
	ArrayList<Integer> anime_scores = new ArrayList<Integer>();
	ArrayList<String> manga_scores_name = new ArrayList<String>();
	ArrayList<Integer> manga_scores = new ArrayList<Integer>();
	
	ArrayList<String> anime_types_name = new ArrayList<String>();
	ArrayList<Integer> anime_types = new ArrayList<Integer>();
	ArrayList<String> manga_types_name = new ArrayList<String>();
	ArrayList<Integer> manga_types = new ArrayList<Integer>();
	
	ArrayList<String> anime_ratings_name = new ArrayList<String>();
	ArrayList<Integer> anime_ratings = new ArrayList<Integer>();
	ArrayList<String> manga_ratings_name = new ArrayList<String>();
	ArrayList<Integer> manga_ratings = new ArrayList<Integer>();

	ArrayList<Integer> activity = new ArrayList<Integer>();
	
    public static SSDK_User parse(JSONObject o) throws NumberFormatException, JSONException{
        SSDK_User user = new SSDK_User();
                
        user.id = o.getString("id");
        user.nickname = o.getString("nickname");
        user.is_friend = o.getBoolean("in_friends");

        if (o.getString("avatar").contains("http:"))
        	user.avatar = o.getString("avatar");
        else
        	user.avatar = "http://shikimori.org" + o.getString("avatar");
        
        user.name = o.getString("name");
        user.sex = o.getString("sex");
        user.full_years = o.getString("full_years");
        user.location = o.getString("location");
        user.website = o.getString("website");
        user.last_online = o.getString("last_online");
        JSONArray jArray_info = o.getJSONArray("common_info");
        int ti = jArray_info.length();
        user.start_date = jArray_info.getString(ti-1);
        if (o.getJSONObject("image").getString("x160").contains("http:"))
        	user.image_x160 = o.getJSONObject("image").getString("x160");
        else
        	user.image_x160 = "http://shikimori.org" + o.getJSONObject("image").getString("x160");
        
        if (o.getJSONObject("image").getString("x148").contains("http:"))
        	user.image_x148 = o.getJSONObject("image").getString("x148");
        else
        	user.image_x148 = "http://shikimori.org" + o.getJSONObject("image").getString("x148");
        	
        if (o.getJSONObject("image").getString("x80").contains("http:"))
        	user.image_x80 = o.getJSONObject("image").getString("x80");
        else
        	user.image_x80 = "http://shikimori.org" + o.getJSONObject("image").getString("x80");
        	
        if (o.getJSONObject("image").getString("x64").contains("http:"))
        	user.image_x64 = o.getJSONObject("image").getString("x64");
        else
        	user.image_x64 = "http://shikimori.org" + o.getJSONObject("image").getString("x64");
        	
        if (o.getJSONObject("image").getString("x48").contains("http:"))
        	user.image_x48 = o.getJSONObject("image").getString("x48");
        else
        	user.image_x48 = "http://shikimori.org" + o.getJSONObject("image").getString("x48");
        	
        if (o.getJSONObject("image").getString("x32").contains("http:"))
        	user.image_x32 = o.getJSONObject("image").getString("x32");
        else
        	user.image_x32 = "http://shikimori.org" + o.getJSONObject("image").getString("x32");
        	
        if (o.getJSONObject("image").getString("x16").contains("http:"))
        	user.image_x16 = o.getJSONObject("image").getString("x16");
        else
        	user.image_x16 = "http://shikimori.org" + o.getJSONObject("image").getString("x16");
        
        JSONArray jArray_anime = o.getJSONObject("stats").getJSONObject("full_statuses").getJSONArray("anime");
		for (int i=0; i < jArray_anime.length(); i++){
			user.user_anime[i] = jArray_anime.getJSONObject(i).getString("size");
			user.user_anime_id[i] = jArray_anime.getJSONObject(i).getString("id");
			switch (jArray_anime.getJSONObject(i).getString("name")) {
          	case "planned":
          		user.user_anime_name[i] = "Запланировано";
          		break;
          	case "watching":
          		user.user_anime_name[i] = "Смотрю";
          		break;
          	case "completed":
          		user.user_anime_name[i] = "Просмотрено";
          		break;
          	case "on_hold":
          		user.user_anime_name[i] = "Отложено";
          		break;
          	case "rewatching":
          		user.user_anime_name[i] = "Пересматриваю";
          		break;
          	case "dropped":
          		user.user_anime_name[i] = "Брошено";
          		break;
			}
			//user.user_anime_name[i] = jArray_anime.getJSONObject(i).getString("name");
			user.total_see += Integer.parseInt(jArray_anime.getJSONObject(i).getString("size"));
  			if (i == 2)	user.see = Integer.parseInt(jArray_anime.getJSONObject(i).getString("size"));
  			if (i == 4)	user.adrop = Integer.parseInt(jArray_anime.getJSONObject(i).getString("size"));
  		}
		
		JSONArray jArray_manga = o.getJSONObject("stats").getJSONObject("full_statuses").getJSONArray("manga");
		for (int i=0; i < jArray_manga.length(); i++){
			user.user_manga[i] = jArray_manga.getJSONObject(i).getString("size");
			user.user_manga_id[i] = jArray_manga.getJSONObject(i).getString("id");
			switch (jArray_manga.getJSONObject(i).getString("name")) {
          	case "planned":
          		user.user_manga_name[i] = "Запланировано";
          		break;
          	case "watching":
          		user.user_manga_name[i] = "Читаю";
          		break;
          	case "completed":
          		user.user_manga_name[i] = "Прочитано";
          		break;
          	case "on_hold":
          		user.user_manga_name[i] = "Отложено";
          		break;
          	case "rewatching":
          		user.user_manga_name[i] = "Перечитываю";
          		break;
          	case "dropped":
          		user.user_manga_name[i] = "Брошено";
          		break;
			}
			//user.user_manga_name[i] = jArray_manga.getJSONObject(i).getString("name");
			user.total_read += Integer.parseInt(jArray_manga.getJSONObject(i).getString("size"));
  			if (i == 2)	user.read = Integer.parseInt(jArray_manga.getJSONObject(i).getString("size"));
  			if (i == 4)	user.mdrop = Integer.parseInt(jArray_manga.getJSONObject(i).getString("size"));
  		}
		
		user.hasAnime = o.getJSONObject("stats").getBoolean("has_anime?");
		user.hasManga = o.getJSONObject("stats").getBoolean("has_manga?");
        
        if (user.hasAnime){
    		//Оценки
    		JSONArray jScore_anime = o.getJSONObject("stats").getJSONObject("scores").getJSONArray("anime");
    		for (int i=0; i < jScore_anime.length(); i++){
    			user.anime_scores.add(jScore_anime.getJSONObject(i).getInt("value"));
    			user.anime_scores_name.add(jScore_anime.getJSONObject(i).getString("name"));
      		}
    		
    		//Типы
    		JSONArray jTypes_anime = o.getJSONObject("stats").getJSONObject("types").getJSONArray("anime");
    		for (int i=0; i < jTypes_anime.length(); i++){
    			user.anime_types.add(jTypes_anime.getJSONObject(i).getInt("value"));
    			user.anime_types_name.add(jTypes_anime.getJSONObject(i).getString("name"));
    		}
        	
    		//Рэйтинги
    		JSONArray jRatings_anime = o.getJSONObject("stats").getJSONObject("ratings").getJSONArray("anime");
    		for (int i=0; i < jRatings_anime.length(); i++){
    			user.anime_ratings.add(jRatings_anime.getJSONObject(i).getInt("value"));
    			user.anime_ratings_name.add(jRatings_anime.getJSONObject(i).getString("name"));
    		}
    		
    		JSONArray jGenres_anime = o.getJSONObject("stats").getJSONObject("genres").getJSONArray("anime");
    		for (int i=0; i < jGenres_anime.length(); i++){
    			switch (jGenres_anime.getJSONObject(i).getInt("scale")){
    			case 0:
    				user.agenres = user.agenres + "<small>" + jGenres_anime.getJSONObject(i).getJSONObject("category").getString("russian") + "</small>, ";
    				break;
    			case 4:
    				user.agenres = user.agenres + "<big>" + jGenres_anime.getJSONObject(i).getJSONObject("category").getString("russian") + "</big>, ";
    				break;
    			default:
    				user.agenres = user.agenres + jGenres_anime.getJSONObject(i).getJSONObject("category").getString("russian") + ", ";
    				break;
    			}
    		}
    		if (user.agenres.equals("")) user.agenres = "Недостаточно данных для формирования статистики.";
    		
    		JSONArray jStudios_anime = o.getJSONObject("stats").getJSONObject("studios").getJSONArray("anime");
    		for (int i=0; i < jStudios_anime.length(); i++){
    			switch (jStudios_anime.getJSONObject(i).getInt("scale")){
    			case 0:
    				user.studios = user.studios + "<small>" + jStudios_anime.getJSONObject(i).getJSONObject("category").getString("name") + "</small>, ";
    				break;
    			case 4:
    				user.studios = user.studios + "<big>" + jStudios_anime.getJSONObject(i).getJSONObject("category").getString("name") + "</big>, ";
    				break;
    			default:
    				user.studios = user.studios + jStudios_anime.getJSONObject(i).getJSONObject("category").getString("name") + ", ";
    				break;
    			}
    		}
    		if (user.studios.equals("")) user.studios = "Недостаточно данных для формирования статистики.";
    		
    		//activity
    		if (!o.getJSONObject("stats").getString("activity").equals("{}")){
	    		JSONArray jActivity = o.getJSONObject("stats").getJSONArray("activity");
	    		for (int i=0; i < jActivity.length(); i++){
	    			user.activity.add(jActivity.getJSONObject(i).getInt("value"));
	    		}
    		}
        }else{
        	user.agenres = "Недостаточно данных для формирования статистики.";
        	user.studios = "Недостаточно данных для формирования статистики.";
        }
        
        if (user.hasManga){
        	
    		
    		JSONArray jScore_manga = o.getJSONObject("stats").getJSONObject("scores").getJSONArray("manga");
    		for (int i=0; i < jScore_manga.length(); i++){
    			user.manga_scores.add(jScore_manga.getJSONObject(i).getInt("value"));
    			user.manga_scores_name.add(jScore_manga.getJSONObject(i).getString("name"));
      		}

    		JSONArray jTypes_manga = o.getJSONObject("stats").getJSONObject("types").getJSONArray("manga");
    		for (int i=0; i < jTypes_manga.length(); i++){
    			user.manga_types.add(jTypes_manga.getJSONObject(i).getInt("value"));
    			user.manga_types_name.add(jTypes_manga.getJSONObject(i).getString("name"));
    		}
    		
    		//Рэйтинги
    		JSONArray jRatings_manga = o.getJSONObject("stats").getJSONObject("ratings").getJSONArray("manga");
    		for (int i=0; i < jRatings_manga.length(); i++){
    			user.manga_ratings.add(jRatings_manga.getJSONObject(i).getInt("value"));
    			user.manga_ratings_name.add(jRatings_manga.getJSONObject(i).getString("name"));
    		}
    		
    		JSONArray jGenres_manga = o.getJSONObject("stats").getJSONObject("genres").getJSONArray("manga");
    		for (int i=0; i < jGenres_manga.length(); i++){
    			switch (jGenres_manga.getJSONObject(i).getInt("scale")){
    			case 0:
    				user.mgenres = user.mgenres + "<small>" + jGenres_manga.getJSONObject(i).getJSONObject("category").getString("russian") + "</small>, ";
    				break;
    			case 4:
    				user.mgenres = user.mgenres + "<big>" + jGenres_manga.getJSONObject(i).getJSONObject("category").getString("russian") + "</big>, ";
    				break;
    			default:
    				user.mgenres = user.mgenres + jGenres_manga.getJSONObject(i).getJSONObject("category").getString("russian") + ", ";
    				break;
    			}
    		}
    		if (user.mgenres.equals("")) user.mgenres = "Недостаточно данных для формирования статистики.";
    		
    		JSONArray jPublishers_manga = o.getJSONObject("stats").getJSONObject("publishers").getJSONArray("manga");
    		for (int i=0; i < jPublishers_manga.length(); i++){
    			switch (jPublishers_manga.getJSONObject(i).getInt("scale")){
    			case 0:
    				user.publishers = user.publishers + "<small>" + jPublishers_manga.getJSONObject(i).getJSONObject("category").getString("name") + "</small>, ";
    				break;
    			case 4:
    				user.publishers = user.publishers + "<big>" + jPublishers_manga.getJSONObject(i).getJSONObject("category").getString("name") + "</big>, ";
    				break;
    			default:
    				user.publishers = user.publishers + jPublishers_manga.getJSONObject(i).getJSONObject("category").getString("name") + ", ";
    				break;
    			}
    		}
    		if (user.publishers.equals("")) user.publishers = "Недостаточно данных для формирования статистики.";
        }else{
        	user.mgenres = "Недостаточно данных для формирования статистики.";
        	user.publishers = "Недостаточно данных для формирования статистики.";
        }
        return user;
    }
}