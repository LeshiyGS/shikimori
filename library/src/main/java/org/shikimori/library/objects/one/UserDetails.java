package org.shikimori.library.objects.one;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Владимир on 30.03.2015.
 */
public class UserDetails extends JsonParseable<UserDetails> {

    public String name, sex, fullYears, lastOnline,
            website, location, about, aboutHtml;
    public ItemUser user;
    public List<String> commonInfo;
    public Statuses statuses;
    public Statuses fullStatuses;
    public boolean banned, isIgnored, showComments, hasAnime, hasManga, inFriends;

    @Override
    public UserDetails createFromJson(JSONObject json) {
        if(json == null)
            return this;

        HelperObj helper = new HelperObj(json);
        user = new ItemUser().create(json);
        if(user.avatar!=null)
            user.avatar = user.avatar.replace("x48", "x80");
        name = helper.addString("name");
        sex = helper.addString("sex");
        fullYears = helper.addString("full_years");
        lastOnline = helper.addString("last_online");
        website = helper.addString("website");
        location = helper.addString("location");
        about = helper.addString("about");
        aboutHtml = helper.addString("about_html");
        inFriends = json.optBoolean("in_friends");
        banned = json.optBoolean("banned");
        banned = json.optBoolean("banned");
        showComments = json.optBoolean("show_comments");
        isIgnored = json.optBoolean("is_ignored");
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
                list.add(new AnimeManga().create(array.optJSONObject(i)));
            }
        }
        return list;
    }

}
