package org.shikimori.library.objects.one;

import org.json.JSONObject;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;

/**
 * Created by Владимир on 07.07.2015.
 */
public class UserRate extends JsonParseable<UserRate> {
    public String id, text, chapters, volumes, textHtml;
    public int score, rewatches,statusInt,episodes;
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
                case 0: return PLANNED;
                case 1: return WATCHING;
                case 2: return COMPLETED;
                case 3: return ON_HOLD;
                case 4: return DROPPED;
                case 9: return REWATCHING;
                default: return NONE;
            }
        }

        public static int fromStatus(Status status) {
            switch (status) {
                case PLANNED: return 0;
                case WATCHING: return 1;
                case COMPLETED: return 2;
                case ON_HOLD: return 3;
                case DROPPED: return 4;
                case REWATCHING: return 9;
                default: return 0;
            }
        }
    }

}
