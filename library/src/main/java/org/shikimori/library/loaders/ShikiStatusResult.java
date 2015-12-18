package org.shikimori.library.loaders;

import com.gars.querybuilder.StatusResult;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by gars on 3/20/14.
 */
public class ShikiStatusResult extends StatusResult {

    private JSONObject jsonObject;
    private JSONArray jsonArray;

    public ShikiStatusResult() {}

    public ShikiStatusResult(String data) {
        this(data, TYPE.OBJECT);
    }

    public ShikiStatusResult(String data, TYPE type){
        super(data, type);
    }

    @Override
    public void init(String data, TYPE type) {
        super.init(data, type);
        if(type == TYPE.ARRAY)
            jsonArray = super.getResultArray();
        else
            jsonObject = super.getResultObject();
    }

    public JSONArray getResultArray() {
        return jsonArray;
    }

    public JSONObject getResultObject() {
        return jsonObject;
    }

    public JSONObject getJsonObject(String name) {
        return jsonObject.optJSONObject(name);
    }

    public JSONArray getJsonArray(String name) {
        return jsonObject.optJSONArray(name);
    }

    public String getParameter(String papam) {
        if (jsonObject != null && jsonObject.has(papam)){
            String p = jsonObject.optString(papam);
            if(p.equalsIgnoreCase("null"))
                return null;
            return p;
        }
        return null;
    }

    public <C extends Object> C getValue(String key){
        if (jsonObject != null && jsonObject.has(key)){
            return (C) jsonObject.opt(key);
        }
        return null;
    }

    public boolean getParametrBool(String key){
        Object val = getValue(key);
        return val != null && (boolean) val;
    }

    public int getParameterInt(String papam) {
        if (jsonObject != null){
            return jsonObject.optInt(papam);
        }
        return 0;
    }
}
