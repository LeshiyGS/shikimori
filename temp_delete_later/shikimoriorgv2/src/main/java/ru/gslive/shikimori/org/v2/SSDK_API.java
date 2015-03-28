package ru.gslive.shikimori.org.v2;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class SSDK_API {
	
	/***********************************************************************************************************
	 * Получаем краткую информацию о пользователе программы
	 * @param kawai - наша сессия
	 * @param context - контекст для записи данных в настройки
	 * @return true или false в зависимости от того есть ли пользователь у этой сессии
	 ***********************************************************************************************************/
	public static Boolean getWhoAmI(String kawai, Context context){
		try {
			Response doc_token = Jsoup
						.connect(Constants.SERVER + "/api/users/whoami")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
				
			try {
					JSONObject jaa = new JSONObject(doc_token.body());
					String id = jaa.getString("id");
					String login = jaa.getString("nickname");

					//Записываем в файл настроек
					SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context);
					Editor editor = mSettings.edit();
					editor.putString(Constants.APP_PREFERENCES_LOGIN, login);
					editor.putString(Constants.APP_PREFERENCES_USER_ID, id);
					if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
						editor.apply();
					}
                    else {
						editor.commit();
					}
					
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

    	return true;
    }

    /***********************************************************************************************************
     * Получаем token и сессию для запросов на сайт
     * @param kawai - сессия пользователя
     * @return возвращает массив из строк элемент 0 - token, элемент 1 - сессия для этого токена
     ***********************************************************************************************************/
    public static String[] getToken(String kawai){
    	String token[] =  new String[] {"",""};
		try {
			Response doc_token = Jsoup
						.connect(Constants.SERVER + "/api/authenticity_token")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			try {
					JSONObject jaa;
					jaa = new JSONObject(doc_token.body());
					token[0] = jaa.getString("authenticity_token");
					token[1] = doc_token.cookie("_kawai_session");
					
			} catch (JSONException e) {
					e.printStackTrace();
			}
		} catch (IOException e) {
				e.printStackTrace();
		}

    	return token;
    }
    
    /***********************************************************************************************************
     * Авторизуемся на сайте
     * @param nickname - логин пользователя
     * @param password - пароль пользователя
     * @return возвращает массив строк элемент 0 - сессия пользователя, 1 - ник на сайте, 2 - id пользователя
     ***********************************************************************************************************/
    public static String[] getSession(String nickname, String password){
    	String[] session = new String[] {"","",""};
    	String[] temp = getToken("");
		try {
			Response doc_session = Jsoup
						.connect(Constants.SERVER + "/api/sessions")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.data("user[nickname]", nickname)
						.data("user[password]", password)
						.data("authenticity_token", temp[0])
						.cookie("_kawai_session", temp[1])
						.method(Method.POST)
						.execute();
			session[0] = doc_session.cookie("_kawai_session");

			try {
				JSONObject jaa = new JSONObject(doc_session.body());
				session[1] = jaa.getString("nickname");
				session[2] = jaa.getString("id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
    	return session;
    }
    
    /***********************************************************************************************************
     * Получаем непрочитанные сообщения
     * @param kawai - сессия пользователя
     * @return возвращает массив строк 0 - личные сообщения, 1 - новости, 2 - уведобления
     ***********************************************************************************************************/
    public static String[] getUnread(String kawai){
    	String[] t_unread = new String[] {"0","0","0"};
    	Log.d(Constants.LogTag, "get unread");
		try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/users/"+Functions.preference.user_id+"/unread_messages.json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			try {
				JSONObject jaa = new JSONObject(doc.body());
				t_unread[1] = jaa.getString("messages");
				t_unread[0] = jaa.getString("news");
				t_unread[2] = jaa.getString("notifications");
				Functions.unread = t_unread;
				Functions.count_unread = Integer.parseInt(Functions.unread[0]) + Integer.parseInt(Functions.unread[1]) + Integer.parseInt(Functions.unread[2]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
		return t_unread;
    }
   
    /***********************************************************************************************************
     * Получаем список новостей класса sHistory
     * @param id - id пользователя
     * @param kawai - сессия пользователя
     * @param limit - размер страницы
     * @param page - номер страницы
     * @return Список класса sHistory
     ***********************************************************************************************************/
    public static ArrayList<SSDK_History> getHistory(String id, String kawai, int limit, int page) throws IOException, JSONException{
		String temp = "";
		try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/users/" + id + "/history.json?limit=" 
								+ limit + "&page=" + page)
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        return parseHistoryList(new JSONArray(temp));
    }
    
    /***********************************************************************************************************
     * Разбираем json массив на элементы класса sHistory
     * @param array - json массив
     * @return список класса sHistory
     ***********************************************************************************************************/
    private static ArrayList<SSDK_History> parseHistoryList(JSONArray array) throws JSONException {
        ArrayList<SSDK_History> history = new ArrayList<SSDK_History>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { //get(0) is integer, it is audio count
                JSONObject o = (JSONObject)array.get(i);
                history.add(SSDK_History.parse(o));
            }
        }
        return history;
    }
    
    /***********************************************************************************************************
     * Получаем список уведомлений
     * @param limit - кол-во записей на странице
     * @param page - номер получаемой страницы
     * @param kawai - сессия пользователя
     * @return возвращает список уведомлений класа sNotify
     ***********************************************************************************************************/
    public static ArrayList<SSDK_Notify> getNotify(int limit, int page, String kawai) throws IOException, JSONException{
		String temp = "";
		try {
			Response doc = Jsoup
						.connect(Constants.ShikiLink + "/api/users/" + Functions.preference.user_id + 
								"/messages.json?limit="+limit+"&page="+page+"&type=notifications")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return parseNotifyList(new JSONArray(temp));
    }
    
    /***********************************************************************************************************
     * Разбираем json массив на элементы класса sNotify
     * @param array - json массив
     * @return список класса sNotify
     ***********************************************************************************************************/
    private static ArrayList<SSDK_Notify> parseNotifyList(JSONArray array) throws JSONException {
        ArrayList<SSDK_Notify> notify = new ArrayList<SSDK_Notify>();
        if (array != null) {
        	int count;
        	if (array.length()>20) count = 20; else count = array.length();
            for(int i = 0; i<count; ++i) { 
                JSONObject o = (JSONObject)array.get(i);
                notify.add(SSDK_Notify.parse(o));
            }
        }
        return notify;
    }
    
    /***********************************************************************************************************
     * Получаем новости от Аки тян))
     * @param limit - кол-во записей на странице
     * @param page - номер получаемой страницы
     * @param kawai - сессия пользователя
     * @return возвращает список новостей класа sNews
     ***********************************************************************************************************/
    public static ArrayList<SSDK_News> getNews(int limit, int page, String kawai) throws IOException, JSONException{
		String temp = "";
		Boolean clean = false;
		try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/users/" + Functions.preference.user_id + "/messages.json?limit="
								+limit+"&page="+page+"&type=news")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        return parseNewsList(new JSONArray(temp));
    }
    
    /***********************************************************************************************************
     * Разбираем json массив на элементы класса sNews
     * @param array - json массив
     * @return список класса sNews
     ***********************************************************************************************************/
    private static ArrayList<SSDK_News> parseNewsList(JSONArray array) throws JSONException {
        ArrayList<SSDK_News> news = new ArrayList<>();
        if (array != null) {
        	int count;
        	if (array.length()>20) count = 20; else count = array.length();
            for(int i = 0; i<count; ++i) { 
                JSONObject o = (JSONObject)array.get(i);
               	news.add(SSDK_News.parse(o));

            }
        }
        return news;
    }
    
    /***********************************************************************************************************
     * Получаем входящие сообщения
     * @param limit - кол-во записей на странице
     * @param page - номер страницы
     * @param kawai - сесссия пользователя
     * @return список личных сообщений класса sInbox
     ***********************************************************************************************************/
    public static ArrayList<SSDK_Inbox> getInbox(int limit, int page, String kawai) throws IOException, JSONException{
		String temp = "";
		try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/users/" + Functions.preference.user_id + "/messages.json?limit="
								+ limit + "&page=" + page + "&type=inbox")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        return parseInboxList(new JSONArray(temp));
    }
    
    /***********************************************************************************************************
     * Разбираем json массив на элементы класса sInbox
     * @param array - json массив
     * @return список класса sInbox
     ***********************************************************************************************************/
    private static ArrayList<SSDK_Inbox> parseInboxList(JSONArray array) throws JSONException {
        ArrayList<SSDK_Inbox> inbox = new ArrayList<SSDK_Inbox>();
        if (array != null) {
        	int count;
        	if (array.length()>20) count = 20; else count = array.length();
            for(int i = 0; i<count; ++i) { 
                JSONObject o = (JSONObject)array.get(i);
               	inbox.add(SSDK_Inbox.parse(o));
            }
        }
        return inbox;
    }

    /***********************************************************************************************************
     * Получаем комментарии к сущности
     * @param com_obj_id - id комментируемой сущности
     * @param com_obj_type - тип комментируемой сущности (User, Entry..)
     * @param limit - кол-во комментариев на странице
     * @param page - номер страницы
     * @param kawai - сессия пользователя
     * @return список класса sComments
     ***********************************************************************************************************/
    public static ArrayList<SSDK_Comments> getComments_new(String com_obj_id, String com_obj_type, int limit, int page, String kawai) throws IOException, JSONException{
		String temp = "";
		try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/comments.json?commentable_id="+com_obj_id+"&commentable_type="+com_obj_type+"&desc=1&limit="+limit+"&page="+page)
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        return parseCommentsList_new(new JSONArray(temp));
    }
    
    /***********************************************************************************************************
     * Разбираем json массив на элементы класса sComments
     * @param array - json массив
     * @return список класса sComments
     ***********************************************************************************************************/
    private static ArrayList<SSDK_Comments> parseCommentsList_new(JSONArray array) throws JSONException {
        ArrayList<SSDK_Comments> comments = new ArrayList<>();
        if (array != null) {
        	int count;
        	if (array.length()>20) count = 20; else count = array.length();
            for(int i = 0; i<count; ++i) { //get(0) is integer, it is audio count
                JSONObject o = (JSONObject)array.get(i);
               	comments.add(SSDK_Comments.parse(o));
            }
        }
        return comments;
    }
    
    /***********************************************************************************************************
     * Получаем список топиков форума
     * @param section - группа топиков
     * @param limit - кол-во записей на странице
     * @param page - номер страницы
     * @param kawai - сессия пользователя
     * @return список класса sTopic
     ***********************************************************************************************************/
    public static ArrayList<SSDK_Topic> getTopics(String section, int limit, int page, String kawai, Context context) throws IOException, JSONException{
		String temp = "";
		try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/topics?section=" + section + "&limit=" + limit + "&page=" + page)
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        return parseTopicsList(new JSONArray(temp), context);
    }
    
    /***********************************************************************************************************
     * Разбираем json массив на элементы класса sTopic
     * @param array - json массив
     * @return список класса sTopic
     ***********************************************************************************************************/
    private static ArrayList<SSDK_Topic> parseTopicsList(JSONArray array, Context context) throws JSONException {
        ArrayList<SSDK_Topic> topic = new ArrayList<SSDK_Topic>();
        if (array != null) {
        	int count;
        	if (array.length()>20) count = 20; else count = array.length();
            for(int i = 0; i<count; ++i) { //get(0) is integer, it is audio count
                JSONObject o = (JSONObject)array.get(i);
               	topic.add(SSDK_Topic.parse(o, context));
            }
        }
        return topic;
    }
   
    /***********************************************************************************************************
     * Получаем информацию о Клубе
     * @param id - id клуба
     * @param kawai - сессия пользователя
     * @return объект класса sClub
     ***********************************************************************************************************/
    public static SSDK_Club getClub (String id, String kawai){
    	SSDK_Club club = new SSDK_Club();
		try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/clubs/"+id+".json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			try {
				JSONObject o = new JSONObject(doc.body());
				club = SSDK_Club.parse(o);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
    	return club;
    }
    
    /***********************************************************************************************************
     * Получаем информацию о Пользователе
     * @param id - id пользователя сайта
     * @param kawai - сессия пользователя
     * @return объект класса sUser
     ***********************************************************************************************************/
    public static SSDK_User getUser (String id, String kawai){
    	SSDK_User user = new SSDK_User();
		try {
			Log.d(Constants.LogTag,"ID - > "+id);
			Response doc = Jsoup
					.connect(Constants.SERVER + "/api/users/"+id+".json")
					.ignoreContentType(true)
					.timeout(60000)
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
					.cookie("_kawai_session", kawai)
					.method(Method.GET)
					.execute();
			try {
				JSONObject o = new JSONObject(doc.body());
				user = SSDK_User.parse(o);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
		
    	return user;
    }
    
    /***********************************************************************************************************
     * Получаем список связанных аниме
     * @param id - id аниме
     * @param adaptation - true если ищем адаптации
     * @param kawai - сессия пользователя
     * @return список класса sRelation
     ***********************************************************************************************************/
    public static ArrayList<SSDK_Relation> getRelationAnimes(String id, Boolean adaptation, String kawai) throws IOException, JSONException{
		String temp = "";
		try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/animes/"+id+"/related.json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        return parseRelationAnimesList(new JSONArray(temp), adaptation);
    }
    
    /***********************************************************************************************************
     * Разбираем json массив на элементы класса sRelation
     * @param array - json массив
     * @param adaptation - true если ищем адаптации
     * @return список класса sRelation
     ***********************************************************************************************************/
    private static ArrayList<SSDK_Relation> parseRelationAnimesList(JSONArray array, Boolean adaptation) throws JSONException {
        ArrayList<SSDK_Relation> animes = new ArrayList<SSDK_Relation>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { //get(0) is integer, it is audio count
                JSONObject o = (JSONObject)array.get(i);
                SSDK_Relation temp_rel = SSDK_Relation.parse(o, adaptation);
                if (temp_rel != null) animes.add(temp_rel);
            }
        }
        return animes;
    }
    
    /***********************************************************************************************************
     * Получаем список связанных манг
     * @param id - id манги
     * @param adaptation - true если ищем экранизацию
     * @param kawai - сессия пользователя
     * @return список класса sRelation
     ***********************************************************************************************************/
    public static ArrayList<SSDK_Relation> getRelationMangas(String id, Boolean adaptation, String kawai) throws IOException, JSONException{
		String temp = "";
		try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/mangas/"+id+"/related.json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        return parseRelationMangasList(new JSONArray(temp), adaptation);
    }
    
    /***********************************************************************************************************
     * Разбираем json массив на элементы класса sRelation
     * @param array - json массив
     * @param adaptation - true если ищем экранизацию
     * @return список класса sRelation
     ***********************************************************************************************************/
    private static ArrayList<SSDK_Relation> parseRelationMangasList(JSONArray array, Boolean adaptation) throws JSONException {
        ArrayList<SSDK_Relation> mangas = new ArrayList<SSDK_Relation>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { 
                JSONObject o = (JSONObject)array.get(i);
                SSDK_Relation temp_rel = SSDK_Relation.parse(o, adaptation);
                if (temp_rel != null) mangas.add(temp_rel);
            }
        }
        return mangas;
    }
    
    /***********************************************************************************************************
     * Получаем список героев или создателей
     * @param id - id манги или аниме
     * @param type - anime или manga
     * @param character - true если ищем героев
     * @param kawai - сессия пользователя
     * @return список класса sRoles
     ***********************************************************************************************************/
    public static ArrayList<SSDK_Roles> getRoles(String id, String type, Boolean character, String kawai) throws IOException, JSONException{
		String temp = "";
		try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/"+ type +"/"+id+"/roles.json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        return parseRolesList(new JSONArray(temp), character);
    }
    
    /***********************************************************************************************************
     * Разбираем json массив на элементы класса sRoles
     * @param array - json массив
     * @param character - true если ищем героев
     * @return список класса sRoles
     ***********************************************************************************************************/
    private static ArrayList<SSDK_Roles> parseRolesList(JSONArray array, Boolean character) throws JSONException {
        ArrayList<SSDK_Roles> roles = new ArrayList<SSDK_Roles>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) {
                JSONObject o = (JSONObject)array.get(i);
                SSDK_Roles temp_rel = SSDK_Roles.parse(o, character);
                if (temp_rel != null) roles.add(temp_rel);
            }
        }
        return roles;
    }

    /***********************************************************************************************************
     * Получаем список похожих аниме
     * @param id - id аниме
     * @param kawai - сессия пользователя
     * @return список класса sAnimes
     ***********************************************************************************************************/
    public static ArrayList<SSDK_Animes> getSimilarAnimes(String id, String kawai) throws IOException, JSONException{
		String temp = "";
		try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/animes/" + id + "/similar.json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        return parseAnimesList(new JSONArray(temp));
    }
    
    /**
     * Получаем список всех аниме по фильтру
     * @param search
     * @param status
     * @param type
     * @param season
     * @param order_by
     * @param duration
     * @param rating
     * @param mylist
     * @param genre
     * @param kawai
     * @param limit
     * @param page
     * @return
     */
    public static ArrayList<SSDK_Animes> getFiltredAnimes(String search, String status, String type, String season, String order_by, String duration, String rating, String mylist, String genre, String kawai, int limit, int page) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/animes.json?status="+status+"&type="+type+"&season="+season+
								"&order="+ order_by+"&duration="+duration+"&rating="+rating+"&mylist="+mylist+
								"&genre="+genre+"&limit="+limit+"&page="+page+"&search="+search)
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        JSONArray array = new JSONArray(temp);
        return parseAnimesList(array);
    }
    private static ArrayList<SSDK_Animes> parseAnimesList(JSONArray array) throws JSONException {
        ArrayList<SSDK_Animes> animes = new ArrayList<SSDK_Animes>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { //get(0) is integer, it is audio count
                JSONObject o = (JSONObject)array.get(i);
                animes.add(SSDK_Animes.parse(o));
            }
        }
        return animes;
    }
    
    /**
     * Получаем список похожих манги
     * @param id
     * @param kawai
     * @return
     */
    public static ArrayList<SSDK_Mangas> getSimilarMangas(String id, String kawai) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/mangas/"+id+"/similar.json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        JSONArray array = new JSONArray(temp);
        return parseMangasList(array);
    }
    
    /**
     * Получаем список всех манг по фильтру
     * @param search
     * @param status
     * @param type
     * @param season
     * @param order_by
     * @param mylist
     * @param genre
     * @param kawai
     * @param limit
     * @param page
     * @return
     */
    public static ArrayList<SSDK_Mangas> getFiltredMangas(String search, String status, String type, String season, String order_by, String mylist, String genre, String kawai, int limit, int page) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/mangas.json?status="+status+"&type="+type+"&season="+season+
								"&order="+ order_by+"&mylist="+mylist+
								"&genre="+genre+"&limit="+limit+"&page="+page+"&search="+search)
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        JSONArray array = new JSONArray(temp);
        return parseMangasList(array);
    }
    private static ArrayList<SSDK_Mangas> parseMangasList(JSONArray array) throws JSONException {
        ArrayList<SSDK_Mangas> mangas = new ArrayList<SSDK_Mangas>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { 
                JSONObject o = (JSONObject)array.get(i);
                mangas.add(SSDK_Mangas.parse(o));
            }
        }
        return mangas;
    }
    
    /**
     * Получаем список аниме пользователя
     * @param user_id
     * @param type
     * @param kawai
     * @return
     */
    public static ArrayList<SSDK_AnimeList> getAnimeList(String user_id, String limit, String page_no, String type, String kawai, Context context) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/users/"+user_id+"/anime_rates.json?limit="+limit+"&page="+page_no+"&status="+type)
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
			//Log.d(Constants.LogTag, "-> "  + Constants.SERVER + "/api/users/"+user_id+"/anime_rates.json?limit="+limit+"&page="+page_no+"&status="+type);
			//Log.d(Constants.LogTag, "-> "  + temp);
			//Functions.saveFile(Functions.preference.cache_dir + "/shikimori/offline/user/"+user_id+"/", "anime_list.json", doc.body(), context);
		} catch (IOException e) {
				e.printStackTrace();
		}
		if (temp.equals("null")){
	        return new ArrayList<SSDK_AnimeList>();
		}else{
			JSONArray array = new JSONArray(temp);
	        return parseAnimeList(array);
		}
        
    }
    private static ArrayList<SSDK_AnimeList> parseAnimeList(JSONArray array) throws JSONException {
        ArrayList<SSDK_AnimeList> animelist = new ArrayList<SSDK_AnimeList>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { //get(0) is integer, it is audio count
                JSONObject o = (JSONObject)array.get(i);
                animelist.add(SSDK_AnimeList.parse(o));
            }
        }
        return animelist;
    }
    
    /**
     * Получаем список манги пользователя
     * @param user_id
     * @param type
     * @param kawai
     * @return
     */
    public static ArrayList<SSDK_MangaList> getMangaList(String user_id, String limit, String page_no, String type, String kawai) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/users/"+user_id+"/manga_rates.json?limit="+limit+"&page="+page_no+"&status="+type)
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
		if (temp.equals("null")){
	        return new ArrayList<SSDK_MangaList>();
		}else{
			JSONArray array = new JSONArray(temp);
	        return parseMangaList(array);
		}
        
    }
    private static ArrayList<SSDK_MangaList> parseMangaList(JSONArray array) throws JSONException {
        ArrayList<SSDK_MangaList> mangalist = new ArrayList<SSDK_MangaList>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { //get(0) is integer, it is audio count
                JSONObject o = (JSONObject)array.get(i);
                mangalist.add(SSDK_MangaList.parse(o));
            }
        }
        return mangalist;
    }
        
    /**
     * Получаем список избранного пользователя
     * @param id
     * @param kawai
     * @return
     */
    public static String getUserFavourites(String id, String kawai){
   		Response doc = null;
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/users/"+id+"/favourites.json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
		} catch (IOException e) {
				e.printStackTrace();
		}
    	return doc.body();
    }
    
    /**
     * Получаем список клубов пользователя
     * @param id - id пользователя сайта
     * @param kawai - сессия пользователя
     * @return 
     */
    public static String getUserClubs(String id, String kawai){
   		Response doc = null;
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/users/"+ id +"/clubs.json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
		} catch (IOException e) {
				e.printStackTrace();
		}
    	return doc.body();
    }
    
    /**
     * Получаем список аниме клуба
     * @param club_id
     * @param kawai
     * @return
     */
    public static ArrayList<SSDK_Animes> getClubAnimes(String club_id, String kawai) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/clubs/"+club_id+"/animes.json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        JSONArray array = new JSONArray(temp);
        return parseAnimesList(array);
    }
    
    /**
     * Получаем список манги клуба
     * @param club_id
     * @param kawai
     * @return
     */
    public static ArrayList<SSDK_Mangas> getClubMangas(String club_id, String kawai) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/clubs/"+club_id+"/mangas.json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        JSONArray array = new JSONArray(temp);
        return parseMangasList(array);
    }
    
    /**
     * Получаем список Персонажей клуба
     * @param club_id
     * @param kawai
     * @return
     */
    public static ArrayList<SSDK_Characters> getClubCharacters(String club_id, String kawai) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/clubs/"+club_id+"/characters.json")
						.ignoreContentType(true)
						.cookie("_kawai_session", kawai)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        JSONArray array = new JSONArray(temp);
        return parseCharactersList(array);
    }
    
    private static ArrayList<SSDK_Characters> parseCharactersList(JSONArray array) throws JSONException {
        ArrayList<SSDK_Characters> character = new ArrayList<SSDK_Characters>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { 
                JSONObject o = (JSONObject)array.get(i);
                character.add(SSDK_Characters.parse(o));
            }
        }
        return character;
    }
    
    public static SSDK_People getPeople(String id, String kawai){
    	SSDK_People people = new SSDK_People();
    	try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/people/"+id+".json")
						.ignoreContentType(true)
						.timeout(60000)
						.cookie("_kawai_session", kawai)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.method(Method.GET)
						.execute();
			try {
				JSONObject o = new JSONObject(doc.body());
				people = SSDK_People.parse(o);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
    	return people;
    }

    
    /**
     * Получаем список частников клуба
     * @param club_id
     * @param kawai
     * @return
     */
    public static ArrayList<SSDK_Members> getClubMembers(String club_id, String kawai) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/clubs/"+club_id+"/members.json")
						.ignoreContentType(true)
						.cookie("_kawai_session", kawai)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        JSONArray array = new JSONArray(temp);
        return parseMembersList(array);
    }
    private static ArrayList<SSDK_Members> parseMembersList(JSONArray array) throws JSONException {
        ArrayList<SSDK_Members> members = new ArrayList<SSDK_Members>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { 
                JSONObject o = (JSONObject)array.get(i);
                members.add(SSDK_Members.parse(o));
            }
        }
        return members;
    }
    
    public static ArrayList<SSDK_Community_Club> getCommunityClub(int page,int count, String kawai) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/clubs.json?limit=" + count + "&page=" + page)
						.ignoreContentType(true)
						.cookie("_kawai_session", kawai)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        JSONArray array = new JSONArray(temp);
        return parseCommunityClubsList(array);
    }
    private static ArrayList<SSDK_Community_Club> parseCommunityClubsList(JSONArray array) throws JSONException {
        ArrayList<SSDK_Community_Club> clubs = new ArrayList<SSDK_Community_Club>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { 
                JSONObject o = (JSONObject)array.get(i);
                clubs.add(SSDK_Community_Club.parse(o));
            }
        }
        return clubs;
    }
    
    public static ArrayList<SSDK_Community_Users> getCommunityUser(int page,int count, Boolean is_search, String search, String kawai) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
			try {
				doc = Jsoup
							.connect(Constants.SERVER + "/api/users.json?limit=" + count + "&page=" + page + "&search=" + search)
							.ignoreContentType(true)
							.cookie("_kawai_session", kawai)
							.timeout(60000)
							.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
							.method(Method.GET)
							.execute();
				temp  = doc.body();
			} catch (IOException e) {
					e.printStackTrace();
			}
        JSONArray array = new JSONArray(temp);
        return parseCommunityUsersList(array);
    }
    private static ArrayList<SSDK_Community_Users> parseCommunityUsersList(JSONArray array) throws JSONException {
        ArrayList<SSDK_Community_Users> users = new ArrayList<SSDK_Community_Users>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { 
                JSONObject o = (JSONObject)array.get(i);
                users.add(SSDK_Community_Users.parse(o));
            }
        }
        return users;
    }
    
    /**
     * Получаем картинки клуба
     * @param club_id
     * @param kawai
     * @return
     */
    public static ArrayList<SSDK_Images> getClubImages(String club_id, String kawai) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/clubs/"+club_id+"/images.json")
						.ignoreContentType(true)
						.cookie("_kawai_session", kawai)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        JSONArray array = new JSONArray(temp);
        return parseClubImages(array);
    }
    private static ArrayList<SSDK_Images> parseClubImages(JSONArray array) throws JSONException {
        ArrayList<SSDK_Images> images = new ArrayList<SSDK_Images>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { //get(0) is integer, it is audio count
                JSONObject o = (JSONObject)array.get(i);
                images.add(SSDK_Images.parse(o));
            }
        }
        return images;
    }
    
    
    /**
     * Получаем список друзей пользователя
     * @param id - id пользователя сайта
     * @param kawai - сессия пользователя
     * @return
     */
    public static String getFriends(String id, String kawai){
   		Response doc = null;
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/users/"+id+"/friends.json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
		} catch (IOException e) {
				e.printStackTrace();
		}
    	return doc.body();
    }
    
    /***************************************************************************************************************
     * Получаем информацию об аниме
     * @param id - id аниме
     * @param kawai - сессия пользователя
     * @return объект класса sAnime
     ***************************************************************************************************************/
    public static SSDK_Anime getAnime(String id, String kawai){
    	SSDK_Anime anime = new SSDK_Anime();
    	try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/animes/"+id+".json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			try {
				JSONObject o = new JSONObject(doc.body());
				anime = SSDK_Anime.parse(o);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
    	return anime;
    }
    
    /***************************************************************************************************************
     * Получаем информацию о топике форума
     * @param id - id топика
     * @param kawai - сессия пользователя
     * @return объект класса sTopic
     ***************************************************************************************************************/
    public static SSDK_Topic getTopic(String id, String kawai, Context context){
    	SSDK_Topic topic = new SSDK_Topic();
    	try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/topics/"+id+".json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			try {
				JSONObject o = new JSONObject(doc.body());
				topic = SSDK_Topic.parse(o, context);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
    	return topic;
    }
        
    /***************************************************************************************************************
     * Получаем информацию о манге
     * @param id - id манги
     * @param kawai - сессия пользователя
     * @return объект класса sManga
     ***************************************************************************************************************/
    public static SSDK_Manga getManga(String id, String kawai){
    	SSDK_Manga manga = new SSDK_Manga();
		try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/mangas/"+id+".json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			try {
				JSONObject o = new JSONObject(doc.body());
				manga = SSDK_Manga.parse(o);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
    	return manga;
    }
    
    /***************************************************************************************************************
     * Получаем информацию о персонаже
     * @param id - id персонажа
     * @param kawai - сессия пользователя
     * @return объект класса sCharacter
     ***************************************************************************************************************/
    public static SSDK_Character getCharacter(String id, String kawai){
    	SSDK_Character character = new SSDK_Character();
    	try {
			Response doc = Jsoup
						.connect(Constants.SERVER + "/api/characters/"+id+".json")
						.ignoreContentType(true)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.cookie("_kawai_session", kawai)
						.method(Method.GET)
						.execute();
			try {
				JSONObject o = new JSONObject(doc.body());
				character = SSDK_Character.parse(o);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
    	return character;
    }
    
    /***************************************************************************************************************
     * Посылаем запрос на изменение статуса тайтла
     * @param type - тип который описывает сущность
     * @param id - id сущности статуса тайтла
     * @param status - номер списка пользователя
     * @param episodes - кол-во просмотренных эпизодов
     * @param score - оценка пользователя
     * @param rewatches - кол-во пересмотров
     * @param text - заметка пользователя
     ***************************************************************************************************************/
    @SuppressWarnings("unused")
	public static void setAnimeMangaRate (String type, String id, int status, String episodes, String score, String rewatches, String text){
    	String[] token = SSDK_API.getToken(Functions.preference.kawai);
    	try {
    		if (type.equals("Anime")){
    		Response res = Jsoup
					.connect(Constants.SERVER + "/api/user_rates/" + id) 
					.ignoreContentType(true)
					.header("X-CSRF-Token", token[0])
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
					.cookie("_kawai_session", token[1])
					.data("user_rate[status]",String.valueOf(status))
					.data("user_rate[episodes]",episodes)
					.data("user_rate[text]",text)
					.data("user_rate[score]",score)
					.data("user_rate[rewatches]",rewatches)
					.data("_method","patch")
					.method(Method.POST)
					.execute();
    		}else{
    			Response res = Jsoup
    					.connect(Constants.SERVER + "/api/user_rates/" + id) 
    					.ignoreContentType(true)
    					.header("X-CSRF-Token", token[0])
    					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
    					.cookie("_kawai_session", token[1])
    					.data("user_rate[status]",String.valueOf(status))
    					.data("user_rate[chapters]",episodes)
    					.data("user_rate[text]",text)
    					.data("user_rate[score]",score)
    					.data("user_rate[rewatches]",rewatches)
    					.data("_method","patch")
    					.method(Method.POST)
    					.execute();
    		}
		} catch (IOException e) {
				e.printStackTrace();
		}
    }
    
    /***************************************************************************************************************
     * Посылаем запрос на изменение статуса тайтла
     * @param type - тип который описывает сущность
     * @param id - id сущности статуса тайтла
     * @param episodes - кол-во просмотренных эпизодов
     ***************************************************************************************************************/
    @SuppressWarnings("unused")
	public static void setAnimeMangaEp (String type, String id, String episodes){
    	String[] token = SSDK_API.getToken(Functions.preference.kawai);
    	try {
    		if (type.equals("Anime")){
    		Response res = Jsoup
					.connect(Constants.SERVER + "/api/user_rates/" + id) 
					.ignoreContentType(true)
					.header("X-CSRF-Token", token[0])
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
					.cookie("_kawai_session", token[1])
					.data("user_rate[episodes]",episodes)
					.data("_method","patch")
					.method(Method.POST)
					.execute();
    		}else{
    			Response res = Jsoup
    					.connect(Constants.SERVER + "/api/user_rates/" + id) 
    					.ignoreContentType(true)
    					.header("X-CSRF-Token", token[0])
    					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
    					.cookie("_kawai_session", token[1])
    					.data("user_rate[chapters]",episodes)
    					.data("_method","patch")
    					.method(Method.POST)
    					.execute();
    		}
		} catch (IOException e) {
				e.printStackTrace();
		}
    }
    
    
    @SuppressWarnings("unused")
    public static void setAnimeMangaScore (String type, String id, String score){
    	String[] token = SSDK_API.getToken(Functions.preference.kawai);
    	try {
    		if (type.equals("Anime")){
			Response res = Jsoup
					.connect(Constants.SERVER + "/api/user_rates/" + id) 
					.ignoreContentType(true)
					.header("X-CSRF-Token", token[0])
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
					.cookie("_kawai_session", token[1])
					.data("user_rate[score]",score)
					.data("_method","patch")
					.method(Method.POST)
					.execute();
    		}else{
    			Response res = Jsoup
    					.connect(Constants.SERVER + "/api/user_rates/" + id) 
    					.ignoreContentType(true)
    					.header("X-CSRF-Token", token[0])
    					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
    					.cookie("_kawai_session", token[1])
    					.data("user_rate[score]",score)
    					.data("_method","patch")
    					.method(Method.POST)
    					.execute();
    		}
		} catch (IOException e) {
				e.printStackTrace();
		}
    }
    
    
    /***************************************************************************************************************
     * Добавляем аниме или мангу в список пользователя
     * @param target_id - ID аниме или манги
     * @param status - add in list
     * @param user_id - user id
     * @param target_type - "Anime" или "Manga"
     ***************************************************************************************************************/
    @SuppressWarnings("unused")
	public static void setAnimeMangaAdd (String target_id, String user_id, String target_type, int status){
    	String[] token = SSDK_API.getToken(Functions.preference.kawai);
    	try {
    		Response res = Jsoup
					.connect(Constants.SERVER + "/api/user_rates") 
					.ignoreContentType(true)
					.header("X-CSRF-Token", token[0])
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
					.cookie("_kawai_session", token[1])
					.data("user_rate[user_id]",user_id)
					.data("user_rate[target_id]",target_id)
					.data("user_rate[target_type]",target_type)
					.data("user_rate[status]",String.valueOf(status))
					.data("_method","post")
					.method(Method.POST)
					.execute();
		} catch (IOException e) {
				e.printStackTrace();
		}
    }
    
    @SuppressWarnings("unused")
	public static void AnimeMangaDelete (String user_rate){
    	String[] token = SSDK_API.getToken(Functions.preference.kawai);
    	try {
    		Response res = Jsoup
					.connect(Constants.SERVER + "/api/user_rates/" + user_rate) 
					.ignoreContentType(true)
					.header("X-CSRF-Token", token[0])
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
					.cookie("_kawai_session", token[1])
					.data("_method","DELETE")
					.method(Method.POST)
					.execute();
		} catch (IOException e) {
				e.printStackTrace();
		}
    }
    
    public static ArrayList<SSDK_Calendar> getCalendar(String kawai) throws IOException, JSONException{
    	Response doc = null;
		String temp = "";
		try {
			doc = Jsoup
						.connect(Constants.SERVER + "/api/calendar.json")
						.ignoreContentType(true)
						.cookie("_kawai_session", kawai)
						.timeout(60000)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.method(Method.GET)
						.execute();
			temp  = doc.body();
		} catch (IOException e) {
				e.printStackTrace();
		}
        JSONArray array = new JSONArray(temp);
        return parseCalendar(array);
    }
    private static ArrayList<SSDK_Calendar> parseCalendar(JSONArray array) throws JSONException {
        ArrayList<SSDK_Calendar> calendar = new ArrayList<SSDK_Calendar>();
        if (array != null) {
            for(int i = 0; i<array.length(); ++i) { //get(0) is integer, it is audio count
                JSONObject o = (JSONObject)array.get(i);
                calendar.add(SSDK_Calendar.parse(o));
            }
        }
        return calendar;
    }
    
    @SuppressWarnings("unused")
	public static void setGCMId (String user_id, String device_id, String kawai){
    	Log.d(Constants.LogTag, "user: " + user_id + " - token: " + device_id);
    	String[] token = SSDK_API.getToken(Functions.preference.kawai);
    	try {
    		Response res = Jsoup
					.connect(Constants.SERVER + "/api/devices") 
					.header("X-CSRF-Token", token[0])
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
					.cookie("_kawai_session", token[1])
					.data("device[platform]", "android")
					.data("device[token]",device_id)
					.data("device[user_id]",user_id)
					.data("device[name]", android.os.Build.MODEL)
					.data("_method","post")
					.method(Method.POST)
					.execute();
    		/*Response res2 = Jsoup
					.connect(Constants.SERVER + "/api/devices") 
					.header("X-CSRF-Token", token[0])
					.cookie("_kawai_session", token[1])
					.cookie("_kawai_session", kawai)
					.data("_method","GET")
					.method(Method.GET)
					.execute();
    		Log.d(Constants.LogTag, res2.body());*/
		} catch (IOException e) {
				e.printStackTrace();
		}
    }
    
    public static int sendMessage(String body, String commentable_id, String to_id){
    	try {
    		Log.d("mes", "-> " + to_id);
			String[] token = SSDK_API.getToken(Functions.preference.kawai);
			Response res = Jsoup
			    .connect(Constants.SERVER + "/messages.json")
			    .ignoreContentType(true)
			    .header("X-CSRF-Token", token[0])
			    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
			    .cookie("_kawai_session", token[1])
			    .data("link_value","")
			    .data("image_value","")
			    .data("image_value","")
			    .data("message[kind]", "Private")
			    .data("message[from_id]", Functions.preference.user_id)
			    .data("message[to_id]", to_id)
			    .data("message[body]", body)
			    //.data("comment[commentable_id]",  commentable_id)
			    //.data("comment[commentable_type]", "User")
			    .method(Method.POST)
			    .execute();
            Log.d(Constants.LogTag, "code -> " + res.statusCode());
			return res.statusCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
    }
    
    public static int sendUserPage(String body, String commentable_id){
    	try {
    		String[] token = SSDK_API.getToken(Functions.preference.kawai);
			Response res = Jsoup
			    .connect(Constants.SERVER + "/comments")
			    .ignoreContentType(true)
			    .header("X-CSRF-Token", token[0])
			    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
			    .cookie("_kawai_session", token[1])
			    .data("comment[body]", body)
			    .data("comment[commentable_id]", commentable_id)
			    .data("comment[commentable_type]", "User")
			    .data("comment[offtopic]", "0")
			    .data("comment[review]", "0")
			    .method(Method.POST)
			    .execute();
            Log.d(Constants.LogTag, "code -> " + res.statusCode());
			return res.statusCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
    }
    
    public static int sendComment(String body, String commentable_id){
    	try {
    		String[] token = SSDK_API.getToken(Functions.preference.kawai);
			Response res = Jsoup
			    .connect(Constants.SERVER + "/comments")
			    .ignoreContentType(true)
			    .header("X-CSRF-Token", token[0])
			    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
			    .cookie("_kawai_session", token[1])
			    .data("comment[body]", body)
			    .data("comment[commentable_id]",  commentable_id)
			    .data("comment[commentable_type]", "Entry")
			    .method(Method.POST)
			    .execute();
            Log.d(Constants.LogTag, "code -> " + res.statusCode());
			return res.statusCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
    }
    
    public static int editComment(String body, String comment_id, String offtop){
    	try {
    		String[] token = SSDK_API.getToken(Functions.preference.kawai);
			Response res = Jsoup
				    .connect(Constants.SERVER + "/comments/" + comment_id)
				    .ignoreContentType(true)
				    .header("X-CSRF-Token", token[0])
				    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
				    .cookie("_kawai_session", token[1])
				    .data("_method","put")
				    .data("comment[body]", body)
				    .data("comment[offtopic]", offtop)
				    .method(Method.POST)
				    .execute();
			return res.statusCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
    }
    
    public static int deleteComment(String comment_id){
		try {
			String[] token = SSDK_API.getToken(Functions.preference.kawai);
			Response res = Jsoup
					.connect(Constants.SERVER + "/comments/" + comment_id)
					.ignoreContentType(true)
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
					.header("X-CSRF-Token", token[0])
					.cookie("_kawai_session", token[1])
					.data("_method","delete")
					.method(Method.POST)
					.execute();
			return res.statusCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
    }
    
    public static int deleteMessages(String id){
    	try {
			String[] token = SSDK_API.getToken(Functions.preference.kawai);
			Response res = Jsoup.connect(Constants.SERVER + "/messages/" + id)
					.ignoreContentType(true)
					.header("X-CSRF-Token", token[0])
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
					.cookie("_kawai_session", token[1])
					.data("_method","delete")
					.method(Method.POST)
					.execute();
			return res.statusCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
    }
    
    public static void readMessages(String id){
    	try {
			String[] token = SSDK_API.getToken(Functions.preference.kawai);
			@SuppressWarnings("unused")
			Response res = Jsoup
				.connect(Constants.SERVER + "/messages/mark_read")
				.ignoreContentType(true)
				.header("X-CSRF-Token", token[0])
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
				.cookie("_kawai_session", token[1])
				.data("ids", id)
				.method(Method.POST)
				.execute();
			SSDK_API.getUnread(Functions.preference.kawai);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void readComment(String id){
    	try {
			String[] token = SSDK_API.getToken(Functions.preference.kawai);
			@SuppressWarnings("unused")
			Response res = Jsoup
				.connect(Constants.SERVER + "/appear/read")
				.ignoreContentType(true)
				.header("X-CSRF-Token", token[0])
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
				.cookie("_kawai_session", token[1])
				.data("ids", "comment-" + id)
				.method(Method.POST)
				.execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void addFriend(String id){
    	try {
			String[] token = SSDK_API.getToken(Functions.preference.kawai);
			@SuppressWarnings("unused")
			Response res = Jsoup
				.connect(Constants.SERVER + "/" + id +"/friend")
				.ignoreContentType(true)
				.header("X-CSRF-Token", token[0])
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
				.cookie("_kawai_session", token[1])
				.method(Method.POST)
				.execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void delFriend(String id){
    	try {
			String[] token = SSDK_API.getToken(Functions.preference.kawai);
			@SuppressWarnings("unused")
			Response res = Jsoup
				.connect(Constants.SERVER + "/" + id +"/friend")
				.ignoreContentType(true)
				.header("X-CSRF-Token", token[0])
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
				.data("_method","delete")
				.cookie("_kawai_session", token[1])
				.method(Method.POST)
				.execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static int creatComment(String body,String id, String type, Boolean offtopic, Boolean review){
    	int status = 0;
    	try {
			String[] token = SSDK_API.getToken(Functions.preference.kawai);
			Document res = Jsoup
				.connect(Constants.SERVER + "/api/comments.json")
				.ignoreContentType(true)
				//.ignoreHttpErrors(true)
				.header("X-CSRF-Token", token[0])
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
				.data("comment[body]",body)
				.data("comment[commentable_id]",id)
				.data("comment[commentable_type]",type)
				.data("comment[offtopic]", String.valueOf(offtopic))
				.data("comment[review]", String.valueOf(review))
				.cookie("_kawai_session", token[1])
				.post();
			//status = res..statusCode();
			Log.d("status", "-> " + res.html());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return status;
    }
    
    public static ArrayList<String> GoogleTranslate(String text){
    	ArrayList<String> result = new ArrayList<String>();
    	String[] pr = text.split("\\.");
    	Log.d("Всего предложений","-> " + pr.length);
    	for (int i=0; i < pr.length; i++){
	    	try {
	    		Log.d("Запрос","предложение: " + (i+1));
				Response res = Jsoup
					.connect("http://translate.google.ru/translate_a/t?client=x&text="+ URLEncoder.encode(pr[i], "UTF-8") + "&hl=en&sl=en&tl=ru")
					.ignoreContentType(true)
					.header("Accept-Language", "ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3")
            		.header("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.7")
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
					.method(Method.GET)
					.execute();
				try {
					JSONObject jaa = new JSONObject(res.body());
					JSONArray tr = jaa.getJSONArray("sentences");
					for(int ii=0; ii < tr.length();ii++){
						JSONObject temp = (JSONObject) tr.get(ii);
			    		result.add(temp.getString("trans")+".");
			    		Log.d("Запрос " + (i+1),"-> " + temp.getString("trans"));
			    	}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	return result;
    }
    	
}
