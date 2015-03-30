package org.shikimori.library.objects.abs;

import org.json.JSONObject;

/**
 * Created by Александр Свиридов on 15.09.2014.
 */
public abstract class JsonParseable<T> {

    public interface Creator<T> {
        public T createFromJson(JSONObject json);
    }


}

