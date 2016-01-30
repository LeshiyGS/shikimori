package org.shikimori.library.objects.one;

import com.mcgars.imagefactory.objects.Thumb;

import org.json.JSONObject;

import ru.altarix.basekit.library.tools.objBuilder.HelperObj;

/**
 * Created by Феофилактов on 06.01.2016.
 */
public class Video extends Thumb{
    String id, imageUrl, playerUrl, hosting, kind;

    public Video(JSONObject object){
        if(object!=null){
            id = HelperObj.getString(object, "id");
            imageUrl = HelperObj.getString(object, "image_url");
            playerUrl = HelperObj.getString(object, "player_url");
            setThumb(imageUrl);
            setOrigin(imageUrl);
            hosting = HelperObj.getString(object, "hosting");
            kind = HelperObj.getString(object, "kind");
        }
    }
}
