package org.shikimori.library.objects.one;

import android.view.ViewGroup;

import org.json.JSONObject;
import org.shikimori.library.interfaces.OnViewBuildLister;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemDialogs extends JsonParseable implements OnViewBuildLister {

    public ItemUser user;
    public ItemNewsUserShiki message;

    @Override
    public ItemDialogs createFromJson(JSONObject json) {
        if(json == null)
            return this;

        user = new ItemUser().createFromJson(json.optJSONObject("target_user"));
        message = new ItemNewsUserShiki().createFromJson(json.optJSONObject("message"));
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
