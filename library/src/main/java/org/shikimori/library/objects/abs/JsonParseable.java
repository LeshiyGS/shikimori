package org.shikimori.library.objects.abs;

import org.json.JSONObject;

/**
 * Created by Александр Свиридов on 15.09.2014.
 */
public abstract class JsonParseable<T> {
    public static <T extends JsonParseable> T create(Class<T> tClass, JSONObject json) throws IllegalAccessException, InstantiationException {
        return (T) tClass.newInstance().createFromJson(json);
    }

    public T create(JSONObject json){
        return createFromJson(json);
    }

    public abstract T createFromJson(JSONObject json);
}

