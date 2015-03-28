package org.shikimori.client.loaders;

/**
 * Created by Феофилактов on 28.03.2015.
 */
public class ShikiApi {
    public static final String HTTP_SERVER = "http://shikimori.org";
    public static String getUrl(String aniApi) {
        return HTTP_SERVER + "/" + aniApi;
    }
}
