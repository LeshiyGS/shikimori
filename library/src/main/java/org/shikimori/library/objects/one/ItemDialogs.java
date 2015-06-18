package org.shikimori.library.objects.one;

import android.view.ViewGroup;

import org.json.JSONObject;
import org.shikimori.library.interfaces.OnViewBuildLister;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemDialogs extends JsonParseable implements JsonParseable.Creator<ItemDialogs>, OnViewBuildLister {

    public ItemUser user;
    public ItemNewsUserShiki message;

    public static ItemDialogs create(JSONObject json){
        return new ItemDialogs().createFromJson(json);
    }

    @Override
    public ItemDialogs createFromJson(JSONObject json) {
        if(json == null)
            return this;

        user = new ItemUser(json.optJSONObject("target_user"));
        message = ItemNewsUserShiki.create(json.optJSONObject("message"));
        return this;
    }

    @Override
    public void setBuildView(ViewGroup view) {
        message.parsedContent = view;
    }

    @Override
    public String getHtml() {
        return message.htmlBody;
    }
}
