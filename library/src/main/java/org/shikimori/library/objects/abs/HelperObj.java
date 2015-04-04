package org.shikimori.library.objects.abs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gars on 5/30/14.
 */
public class HelperObj {

    private JSONObject object;

    public HelperObj(JSONObject object) {
        this.object = object;
    }


    public void setData(JSONObject object) {
        this.object = object;
    }

    // проверка наличия значения в json
    public String addString(String name) {
        if (object != null && object.has(name))
            try {
                if (object.has(name) && !object.getString(name).equals("null"))
                    return object.getString(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return null;
    }

    // проверка наличия значения в json
    public int addInt(String name) {
        if (object != null && object.has(name))
            try {
                return object.getInt(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return 0;
    }

    // проверка наличия значения в json
    public JSONObject addJsonObject(String name) {
        if (object != null && object.has(name))
            try {
                return object.getJSONObject(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    // проверка наличия значения в json
    public JSONArray addJsonArray(String name) {
        if (object != null && object.has(name))
            try {
                return object.getJSONArray(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    // проверка наличия значения в json
    public long addLong(String name) {
        if (object != null && object.has(name))
            try {
                return object.getLong(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return 0;
    }

    public JSONObject getObject() {
        return object;
    }

    public static String getString(JSONObject object, String name){
        if (object != null && object.has(name)){
            String n = object.optString(name);

            if (n!=null && !n.equals("null"))
                return n;
        }
        return null;
    }

}
