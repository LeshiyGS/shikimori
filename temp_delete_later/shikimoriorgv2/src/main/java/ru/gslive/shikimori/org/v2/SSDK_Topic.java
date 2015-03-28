package ru.gslive.shikimori.org.v2;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;

public class SSDK_Topic implements Serializable {
	private static final long serialVersionUID = 1L;
	public String id;
	public String title;
	public String body;		
    public String body_html;
    public String body_clean;
    public Boolean viewed;
    public Boolean last_comment_viewed;
    public String date;
    public String comments_count;
    public String user_id;
    public String user_avatar;
    public String user_name;
    public String linked_type;
    public String linked_image;
    public String linked_id;
    public String linked_name;
    public String linked_russian;
    public String linked_status;
    public String linked_episodes;
    public String linked_review_id;
    public String linked_review_type;
    public String linked_html_body;
    public String linked_body_clean;
    public int linked_overall;
    public int linked_storyline;
    public int linked_music;
    public int linked_characters;
    public int linked_animation;
    ArrayList<String> spoiler_names = new ArrayList<>();
    ArrayList<String> spoiler_names_linked = new ArrayList<>();

    public static SSDK_Topic parse(JSONObject o, Context context) throws NumberFormatException, JSONException{
        SSDK_Topic topic = new SSDK_Topic();

        int iii = 0;
        int mmm = 0;
        int liii = 0;
        int lmmm = 0;
        int iiii = iii;
        int liiii = liii;

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
            topic.spoiler_names.add(element.child(0).select("label").text());
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

		topic.title = o.getString("title");
        topic.viewed = o.getBoolean("viewed");
        if (!o.isNull("last_comment_viewed")){
            topic.last_comment_viewed = o.getBoolean("last_comment_viewed");
        }else{
            topic.last_comment_viewed = true;
        }

        topic.date = Functions.in_time(o.getString("created_at"), true);
        topic.body = o.getString("body");
        topic.body_html = o.getString("html_body");
        topic.body_clean = json.html();
        topic.user_id = o.getJSONObject("user").getString("id");
        topic.id = o.getString("id");
        topic.user_name = o.getJSONObject("user").getString("nickname");
        topic.comments_count = o.getString("comments_count");
			
		if (o.getJSONObject("user").getJSONObject("image").getString("x148").contains("http")){
			topic.user_avatar = o.getJSONObject("user").getJSONObject("image").getString("x148");
		}else {
			topic.user_avatar = "http://shikimori.org" + o.getJSONObject("user").getJSONObject("image").getString("x148");
		} 

		topic.linked_type = o.getString("linked_type");
		if (!o.isNull("linked")){
			//Получаем ID прикрепленного
			topic.linked_id = o.getJSONObject("linked").getString("id");
			//Если обсуждение аниме
			if (topic.linked_type.equals("Anime")){
				topic.linked_image = "http://shikimori.org" + o.getJSONObject("linked").getJSONObject("image").getString("original");
				topic.linked_name = o.getJSONObject("linked").getString("name");
				topic.linked_russian = o.getJSONObject("linked").getString("russian");
				if (o.getJSONObject("linked").getBoolean("anons")){
					topic.linked_status = "Статус: Анонс";
				}else{
					if (o.getJSONObject("linked").getBoolean("ongoing")){
						topic.linked_status = "Статус: Выходит";
					}else{
						topic.linked_status = "Статус: Вышло";
					}
				}
				if (o.getJSONObject("linked").getString("episodes").equals("0")){
					topic.linked_episodes = "Эпизодов: ?";
				}else{
					topic.linked_episodes = "Эпизодов: " + o.getJSONObject("linked").getString("episodes");
				}
			//Если обсуждение манги
			}else if (topic.linked_type.equals("Manga")){
				topic.linked_image = "http://shikimori.org" + o.getJSONObject("linked").getJSONObject("image").getString("original");
				topic.linked_name = o.getJSONObject("linked").getString("name");
				topic.linked_russian = o.getJSONObject("linked").getString("russian");
				if (o.getJSONObject("linked").getBoolean("anons")){
					topic.linked_status = "Статус: Анонс";
				}else{
					if (o.getJSONObject("linked").getBoolean("ongoing")){
						topic.linked_status = "Статус: Выходит";
					}else{
						topic.linked_status = "Статус: Вышло";
					}
				}
				if (o.getJSONObject("linked").getString("chapters").equals("0")){
					topic.linked_episodes = "Глав: ?";
				}else{
					topic.linked_episodes = "Глав: " + o.getJSONObject("linked").getString("chapters");
				}
			//Если обсуждение группы
			}else if(topic.linked_type.equals("Group")){
				topic.linked_image = "http://shikimori.org" + o.getJSONObject("linked").getString("logo");
			//Если обсуждение персонажа
			}else if(topic.linked_type.equals("Character")){
				topic.linked_image = "http://shikimori.org" + o.getJSONObject("linked").getJSONObject("image").getString("original");
			//Если обсуждение обзора
			}else if (topic.linked_type.equals("Review")){
				//Получаем содержание обзора
				
				topic.linked_html_body = o.getJSONObject("linked").getString("html_body");

                Document json2 = Jsoup.parse(o.getJSONObject("linked").getString("html_body"));

                for(Element element : json2.select("a[class^=c-video b-video]")){
                    element.after(" <a href="+element.attr("href")+"a>Ссылка на видео</a>");
                }

                for(Element element : json2.select("img")){
                    if (!element.attr("src").contains("http")) 	element.attr("src", Constants.SERVER + element.attr("src"));
                }

                for(Element element : json2.select("a")){
                    if (!element.attr("href").contains("http")) element.attr("href", Constants.SERVER + element.attr("href"));
                }

                for(Element element : json2.select("div[class=after]")){
                    element.before("[SEP]SP:false:" + liii + "!");
                    element.remove();
                    liii++;
                }

                for(Element element : json2.select("div[class=b-spoiler unprocessed]")){
                    topic.spoiler_names_linked.add(element.child(0).select("label").text());
                    element.child(0).select("label").remove();
                }

                for(Element element : json2.select("div[class=before")){
                    element.after("[SEP]SP:true:" + liiii + "!");
                    element.remove();
                    liiii++;
                }

                for(Element element : json2.select("del")){
                    element.remove();
                }

                for(Element element : json2.select("img")){
                    if (!element.attr("src").contains("/images/user/") && !element.attr("src").contains("/images/smileys/")){
                        element.before("[SEP]CI:" + element.attr("src")+"!");
                        element.remove();
                        lmmm++;
                    }
                }

		       	topic.linked_body_clean = json2.html();
				
				//Получаем оценки автора обзора
				if (!o.getJSONObject("linked").isNull("overall")) topic.linked_overall = o.getJSONObject("linked").getInt("overall"); else topic.linked_overall = 0;
				if (!o.getJSONObject("linked").isNull("storyline")) topic.linked_storyline = o.getJSONObject("linked").getInt("storyline"); else topic.linked_storyline = 0;
				if (!o.getJSONObject("linked").isNull("music")) topic.linked_music = o.getJSONObject("linked").getInt("music"); else topic.linked_music = 0;
				if (!o.getJSONObject("linked").isNull("characters")) topic.linked_characters = o.getJSONObject("linked").getInt("characters"); else topic.linked_characters = 0;
				if (!o.getJSONObject("linked").isNull("animation")) topic.linked_animation = o.getJSONObject("linked").getInt("animation"); else topic.linked_animation = 0;
				
				//Получаем название обозреваемого тайтла
				topic.linked_review_id = o.getJSONObject("linked").getJSONObject("target").getString("id");
				topic.linked_name = o.getJSONObject("linked").getJSONObject("target").getString("name");
				topic.linked_russian = o.getJSONObject("linked").getJSONObject("target").getString("russian");
				
				//Получаем обложку тайтла
				topic.linked_image = "http://shikimori.org" + o.getJSONObject("linked").getJSONObject("target").getJSONObject("image").getString("original");
			
				//Получаем статус тайтла
				if (o.getJSONObject("linked").getJSONObject("target").getBoolean("anons")){
					topic.linked_status = "Статус: Анонс";
				}else{
					if (o.getJSONObject("linked").getJSONObject("target").getBoolean("ongoing")){
						topic.linked_status = "Статус: Выходит";
					}else{
						topic.linked_status = "Статус: Вышло";
					}
				}
				//Получаем эпизоды или главы тайтла
				if (o.getJSONObject("linked").getJSONObject("target").has("episodes")){
					topic.linked_episodes = "Эпизодов: " + o.getJSONObject("linked").getJSONObject("target").getString("episodes");
					topic.linked_review_type = "anime";
				}else{
					topic.linked_episodes = "Глав: " + o.getJSONObject("linked").getJSONObject("target").getString("chapters");
					topic.linked_review_type = "manga";
				}
			}
		}else{
			topic.linked_image = "";
			topic.linked_id = "";
			topic.linked_name = "";
			topic.linked_russian = "";
		}
		        
        Functions.saveFile(Functions.preference.cache_dir + "/shikimori/offline/topic/", topic.id + ".json", o.toString(), context);
        
        return topic;
    }
}