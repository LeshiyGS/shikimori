package org.shikimori.client.objects.abs;

import org.json.JSONObject;

/**
 * Created by Александр Свиридов on 15.09.2014.
 */
public interface JsonParseable {

    public interface Creator<T> {
        public T createFromJson(JSONObject json);
    }

}

