package org.shikimori.library.objects.one;

import com.mcgars.imagefactory.objects.Thumb;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;
import org.shikimori.library.tool.ProjectTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ItemClubDescriptionShiki extends JsonParseable<ItemClubDescriptionShiki> {
    public String description, descriptionHtml, threadId;
    public String id, name, original, main, user_role;
    protected JSONObject allData;
    protected HelperObj helper;
    protected List<Thumb> imgList;

    @Override
    public ItemClubDescriptionShiki createFromJson(JSONObject json) {
        allData = json;
        if (json == null)
            return this;
        description = HelperObj.getString(json, "description");
        descriptionHtml = HelperObj.getString(json, "description_html");
        threadId = HelperObj.getString(json, "thread_id");
        user_role = HelperObj.getString(json, "user_role");

        id = HelperObj.getString(json, "id");
        name = HelperObj.getString(json, "name");

        JSONObject image = json.optJSONObject("logo");
        if (image != null) {
            original = ProjectTool.fixUrl(image.optString("original"));
            main = ProjectTool.fixUrl(image.optString("main"));
        }

        JSONArray images = json.optJSONArray("images");
        imgList = new ArrayList<>();
        if(images!=null){
            for (int i = 0; i < images.length(); i++) {
                if(i > 1)
                    break;
                JSONObject item = images.optJSONObject(i);
                imgList.add(new Thumb(
                        ProjectTool.fixUrl(item.optString("preview")),
                        ProjectTool.fixUrl(item.optString("original"))));
            }
        }

        return this;
    }

    public List<Thumb> getImages(){
        return imgList;
    }
}