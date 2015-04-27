package org.shikimori.library.objects;

import android.view.ViewGroup;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.shikimori.library.interfaces.OnViewBuildLister;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.objects.one.ItemUser;
import org.shikimori.library.objects.one.Section;

import java.util.List;

/**
 * Created by Феофилактов on 04.04.2015.
 */
    public class ItemUserShiki extends JsonParseable implements JsonParseable.Creator<ItemUserShiki> {
        public String id, nickname, avatar, last_online_at, img_x160, img_x148;
        protected JSONObject allData;
        protected HelperObj helper;

        @Override
        public String toString() {
            if (allData != null)
                return allData.toString();
            return "";
        }

        public static ItemUserShiki create(JSONObject json) {
            return new ItemUserShiki().createFromJson(json);
        }

        @Override
        public ItemUserShiki createFromJson(JSONObject json) {
            allData = json;
            if (json == null)
                return null;
            helper = new HelperObj(json);
            id = helper.addString("id");
            nickname = helper.addString("nickname");
            avatar = helper.addString("avatar");
            last_online_at = helper.addString("last_online_at");

            JSONObject image = json.optJSONObject("image");
            if(image!=null){
                img_x160 = getImageUrl(image.optString("x160"));
                img_x148 = getImageUrl(image.optString("x148"));
            }

            return this;
        }

        String getImageUrl(String url){
            if(url.startsWith("/"))
                return ShikiApi.HTTP_SERVER + url;
            return url;
        }
    }