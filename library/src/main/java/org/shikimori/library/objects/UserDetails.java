package org.shikimori.library.objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.shikimori.library.objects.abs.AbstractHelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Владимир on 30.03.2015.
 */
public class UserDetails extends JsonParseable implements JsonParseable.Creator<UserDetails> {

    public String id, nickname, avatar, lastOnlineAt, name, sex, fullYears, lastOnline,
            website, location, about, aboutHtml, inFriends;
    public List<String> commonInfo;
    public Statuses statuses;
    public Statuses fullStatuses;
    public boolean banned, showComments, hasAnime, hasManga;

    public static UserDetails create(JSONObject json) {
        return new UserDetails().createFromJson(json);
    }

    @Override
    public UserDetails createFromJson(JSONObject json) {
        AbstractHelperObj helper = new AbstractHelperObj(json);
        id = helper.addString("id");
        nickname = helper.addString("nickname");
        avatar = helper.addString("avatar");
        if(avatar!=null)
            avatar = avatar.replace("x48", "x80");
        lastOnlineAt = helper.addString("last_online_at");
        name = helper.addString("name");
        sex = helper.addString("sex");
        fullYears = helper.addString("full_years");
        lastOnline = helper.addString("last_online");
        website = helper.addString("website");
        location = helper.addString("location");
        about = helper.addString("about");
        aboutHtml = helper.addString("about_html");
        inFriends = helper.addString("in_friends");
        banned = json.optBoolean("banned");
        showComments = json.optBoolean("show_comments");
        hasAnime = json.optBoolean("has_anime?");
        hasManga = json.optBoolean("has_manga?");

        JSONObject stats = json.optJSONObject("stats");
        if(stats!=null){
            statuses = new Statuses(stats.optJSONObject("statuses"));
            fullStatuses = new Statuses(stats.optJSONObject("full_statuses"));
        }

        JSONArray coomInf = json.optJSONArray("common_info");
        if(coomInf!=null){
            commonInfo = new ArrayList<>();
            for (int i = 0; i < coomInf.length(); i++) {
                commonInfo.add(coomInf.optString(i));
            }
        }

        return this;
    }

    public static class Statuses{
        public List<AnimeManga> animes = new ArrayList<>();
        public List<AnimeManga> manga = new ArrayList<>();
        public Statuses(JSONObject obj){
            if(obj == null)
                return;
            animes = getAnimeMangaList(obj, "anime");
            manga = getAnimeMangaList(obj, "manga");
        }
    }

    static List<AnimeManga> getAnimeMangaList(JSONObject obj, String name){
        List<AnimeManga> list = new ArrayList<>();
        JSONArray array = obj.optJSONArray(name);
        if(array!=null){
            for (int i = 0; i < array.length(); i++) {
                list.add(AnimeManga.create(array.optJSONObject(i)));
            }
        }
        return list;
    }

}
