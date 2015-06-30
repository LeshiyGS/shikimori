package org.shikimori.library.objects.one;

import org.json.JSONObject;
import org.shikimori.library.objects.abs.HelperObj;

/**
 * Created by Владимир on 30.06.2015.
 */
public class RatesStatusesStats {
    public String name;
    public int value;
    public int procents;
    public RatesStatusesStats(JSONObject object){
        if(object==null)
            return;
        name = HelperObj.getString(object, "name");
        value = object.optInt("value");
    }
}
