package org.shikimori.library.objects;

import android.view.ViewGroup;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.shikimori.library.interfaces.OnViewBuildLister;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.objects.one.ItemUser;
import org.shikimori.library.objects.one.Section;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemTopicsShiki extends ItemNewsUserShiki implements OnViewBuildLister {

    public String title, type, linkedType, linkedId;
    public int commentsCount;
    public Section section;
    public ItemUser user;
    public ViewGroup parsedContent;
    public Document doc;

    public static ItemTopicsShiki create(JSONObject json){
        return new ItemTopicsShiki().createFromJson(json);
    }

    @Override
    public ItemTopicsShiki createFromJson(JSONObject json) {
        if(json == null)
            return this;
        super.createFromJson(json);
        commentsCount = json.optInt("comments_count");
        section = new Section(json.optJSONObject("section"));
        user = new ItemUser(json.optJSONObject("user"));

        title = HelperObj.getString(json, "title");
        type = HelperObj.getString(json, "type");
        linkedType = HelperObj.getString(json, "linked_type");
        linkedId = HelperObj.getString(json, "linked_id");

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
