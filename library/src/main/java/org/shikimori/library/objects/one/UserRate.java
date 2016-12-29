package org.shikimori.library.objects.one;

import org.json.JSONObject;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;

/**
 * Created by Владимир on 07.07.2015.
 */
public class UserRate implements JsonParseable<UserRate> {
    public String id, text, volumes, textHtml;
    public int score, rewatches,episodes,chapters;
    public Status status = Status.NONE;

    @Override
    public UserRate create(JSONObject json) {
        if(json!=null){
            id = HelperObj.getString(json, "id");
            text = HelperObj.getString(json, "text");
            volumes = HelperObj.getString(json, "volumes");
            textHtml = HelperObj.getString(json, "text_html");

            score = json.optInt("score");
            episodes = json.optInt("episodes");
            rewatches = json.optInt("rewatches");
            chapters = json.optInt("chapters");

            if(json.has("status_name"))
                status = Status.from(json.optString("status_name"));
            else
                status = Status.from(json.optString("status"));
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

        public static Status from(String status) {
            if(status == null)
                return NONE;
            switch (status.toLowerCase()) {
                case "planned": return PLANNED;
                case "watching": return WATCHING;
                case "completed": return COMPLETED;
                case "on_hold": return ON_HOLD;
                case "dropped": return DROPPED;
                case "rewatching": return REWATCHING;
                default: return NONE;
            }
        }

        @Override
        public String toString() {
            if(this == NONE)
                return "";
            return super.toString().toLowerCase();
        }

        //        public static Status fromInt(int status) {
//            switch (status) {
//                case 0: return PLANNED;
//                case 1: return WATCHING;
//                case 2: return COMPLETED;
//                case 3: return ON_HOLD;
//                case 4: return DROPPED;
//                case 9: return REWATCHING;
//                default: return NONE;
//            }
//        }
//
//        public static int fromStatus(Status status) {
//            switch (status) {
//                case PLANNED: return 0;
//                case WATCHING: return 1;
//                case COMPLETED: return 2;
//                case ON_HOLD: return 3;
//                case DROPPED: return 4;
//                case REWATCHING: return 9;
//                default: return 0;
//            }
//        }
    }

}
