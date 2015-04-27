package org.shikimori.library.objects;

import org.json.JSONObject;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.objects.abs.HelperObj;
import org.shikimori.library.objects.abs.JsonParseable;

/**
 * Created by Феофилактов on 04.04.2015.
 */
    public class ItemClubShiki extends JsonParseable implements JsonParseable.Creator<ItemClubShiki> {
        public String id, name, original, main;
        protected JSONObject allData;
        protected HelperObj helper;

        @Override
        public String toString() {
            if (allData != null)
                return allData.toString();
            return "";
        }

        public static ItemClubShiki create(JSONObject json) {
            return new ItemClubShiki().createFromJson(json);
        }

        @Override
        public ItemClubShiki createFromJson(JSONObject json) {
            allData = json;
            if (json == null)
                return null;
            helper = new HelperObj(json);
            id = helper.addString("id");
            name = helper.addString("name");

            JSONObject image = json.optJSONObject("logo");
            if(image!=null){
                original = getImageUrl(image.optString("original"));
                main = getImageUrl(image.optString("main"));
            }

            return this;
        }

        String getImageUrl(String url){
            if(url.startsWith("/"))
                return ShikiApi.HTTP_SERVER + url;
            return url;
        }
    }