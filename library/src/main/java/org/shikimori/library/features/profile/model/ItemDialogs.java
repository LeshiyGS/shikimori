package org.shikimori.library.features.profile.model;

import android.view.ViewGroup;

import org.json.JSONObject;
import org.shikimori.library.interfaces.OnViewBuildLister;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.objects.one.ItemUser;

import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemDialogs implements JsonParseable<ItemDialogs>, OnViewBuildLister {

    public ItemUser user;
    public ItemNewsUserShiki message;

    @Override
    public ItemDialogs create(JSONObject json) {
        if(json == null)
            return this;

        user = new ItemUser().create(json.optJSONObject("target_user"));
        message = new ItemNewsUserShiki().create(json.optJSONObject("message"));
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
