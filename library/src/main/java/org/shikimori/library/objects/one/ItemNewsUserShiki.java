package org.shikimori.library.objects.one;

import android.view.ViewGroup;

import org.json.JSONObject;
import org.shikimori.library.interfaces.OnViewBuildLister;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemNewsUserShiki extends JsonParseable<ItemNewsUserShiki> implements OnViewBuildLister {

    public String id, kind, body, htmlBody, createdAt;
    public Linked linked;
    public ItemUser from, to;
    public boolean read;
    public ViewGroup parsedContent;

    @Override
    public ItemNewsUserShiki createFromJson(JSONObject json) {
        if(json == null)
            return this;

        id = HelperObj.getString(json, "id");
        kind = HelperObj.getString(json, "kind");
        read = json.optBoolean("read");
        body = HelperObj.getString(json, "body");
        htmlBody = HelperObj.getString(json, "html_body");
        createdAt = HelperObj.getString(json, "created_at");
        linked = new Linked(json.optJSONObject("linked"));
        from = new ItemUser().create(json.optJSONObject("from"));
        to = new ItemUser().create(json.optJSONObject("to"));

        return this;
    }

    @Override
    public void setBuildView(ViewGroup view) {
        parsedContent = view;
    }

    @Override
    public String getHtml() {
        return htmlBody;
    }
}
