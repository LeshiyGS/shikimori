package org.shikimori.library.objects.one;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by Владимир on 07.07.2015.
 */
public class UserRate extends JsonParseable<UserRate> {
    public String id, text, chapters, volumes, textHtml;
    public int score, episodes, rewatches,statusInt;
    public Status status = Status.NONE;

    @Override
    public UserRate createFromJson(JSONObject json) {
        if(json!=null){
            id = HelperObj.getString(json, "id");
            text = HelperObj.getString(json, "text");
            chapters = HelperObj.getString(json, "chapters");
            volumes = HelperObj.getString(json, "volumes");
            textHtml = HelperObj.getString(json, "text_html");

            score = json.optInt("score");
            episodes = json.optInt("episodes");
            rewatches = json.optInt("rewatches");
            statusInt = json.optInt("status");

            status = Status.fromInt(json.optInt("status"));
        }
        return this;
    }

    public enum Status {
        NONE,
        WATCHING,
        PLANNED,
        COMPLETED,
        REWATCHING,
        ON_HOLD,
        DROPPED;

        public static Status fromInt(int status) {
            switch (status) {
                case 1: return WATCHING;
                case 2: return PLANNED;
                case 3: return COMPLETED;
                case 4: return REWATCHING;
                case 5: return ON_HOLD;
                case 6: return DROPPED;
                default: return NONE;
            }
        }
    }

}
