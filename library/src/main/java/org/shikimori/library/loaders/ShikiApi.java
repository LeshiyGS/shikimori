package org.shikimori.library.loaders;

/**
 * Created by Феофилактов on 28.03.2015.
 */
public class ShikiApi {
    public static final String HTTP_SERVER = "https://shikimori.org";
    public static boolean isDebug;

    public static String getUrl(String aniApi) {
        return HTTP_SERVER + "/" + aniApi;
    }

    public static String getUrlPrefix(String aniApi, String preffix) {
        return HTTP_SERVER + "/" + aniApi + "/"+preffix;
    }

    public static String getUrl(String aniApi, String userId) {
        return getUrl(aniApi, userId, null);
    }

    public static String getUrl(String aniApi, String userId, String preffix) {
        String url = getUrl(aniApi).replace(ShikiPath.ID, userId == null ? "0": userId);
        return preffix == null ? url : url+"/"+preffix;
    }

    public static void setIsDebug(boolean isDebug) {
        ShikiApi.isDebug = isDebug;
    }
}
