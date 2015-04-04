package org.shikimori.library.loaders;

/**
 * Created by Феофилактов on 28.03.2015.
 */
public class ShikiApi {
    public static final String HTTP_SERVER = "http://shikimori.org";
    public static boolean isDebug;

    public static String getUrl(String aniApi) {
        return HTTP_SERVER + "/" + aniApi;
    }

    public static String getUrl(String aniApi, String userId) {
        return getUrl(aniApi).replace(ShikiPath.ID, userId);
    }

    public static void setIsDebug(boolean isDebug) {
        ShikiApi.isDebug = isDebug;
    }
}
