package org.shikimori.library.loaders.httpquery;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gars on 3/20/14.
 */
public class StatusResult {

    private String bodyText;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private String msg;
    private Header[] headers;

    public enum TYPE{
        OBJECT, ARRAY
    }
    public enum STATUS{
        SUCCESS, ERROR, SERVERERROR
    }
    private STATUS status = STATUS.SUCCESS;

    public StatusResult() {}

    public StatusResult(String data) {
        if (data == null)
            return;
        bodyText = data;
        try {
            jsonObject = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public StatusResult(String data, TYPE type){
        if (data == null)
            return;
        bodyText = data;
        try {
            if(type == TYPE.ARRAY)
                jsonArray = new JSONArray(data);
            else
                jsonObject = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getResultArray() {
        return jsonArray;
    }

    public JSONObject getResultObject() {
        return jsonObject;
    }

    public String getHtml() {
        return bodyText == null ? "" : bodyText;
    }

    public boolean isSuccess() {
        return status == STATUS.SUCCESS;
    }

    public boolean isError() {
        return status == STATUS.ERROR;
    }

    public boolean isServerError() {
        return status == STATUS.SERVERERROR;
    }

    public void setSuccess(){
        status = STATUS.SUCCESS;
    }
    public void setError(){
        status = STATUS.ERROR;
    }

    public void setServerError() {
        status = STATUS.SERVERERROR;
    }

    public String getMsg() {
        return msg != null ? msg : "";
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public String getHeader(String key){
        getHeader(key, null);
        return null;
    }

    public String getHeader(String key, String searchParam){
        if(headers == null)
            return null;
        for(Header head : headers){
            if(head.getName().equalsIgnoreCase(key)){
                if(searchParam == null)
                    return head.getValue();
                else if (head.getValue().contains(searchParam))
                    return head.getValue();
            }
        }

        return null;
    }

}
