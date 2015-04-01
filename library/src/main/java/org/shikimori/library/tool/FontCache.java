package org.shikimori.library.tool;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

public class FontCache {

    public static enum FONT {
        ROBOTO,
        ROBOTO_BOLD,
        STYLO,
    }

    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    public static Typeface get(FONT font, Context context) {
        String name = "";
        if(font == FONT.ROBOTO)
            name = "roboto.ttf";
        else if (font == FONT.ROBOTO_BOLD)
            name = "roboto_bold.ttf";
        else if (font == FONT.STYLO)
            name = "stylo-bold.ttf";
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }

    public static Typeface get(Context context) {
        return get(FONT.ROBOTO, context);
    }
}