package org.shikimori.library.tool.parser.htmlutil;

import android.text.TextUtils;

/**
 * Created by Владимир on 14.04.2015.
 */
public class TextHtmlUtils {
    public static StringBuilder getStyledText(String style, StringBuilder builder, String text){
        if (!TextUtils.isEmpty(style)) {
            String[] styles = style.split(";");
            builder.append("<font ");
            for (String s : styles) {
                String[] params = s.split(":");
                if (params.length < 2)
                    continue;
                builder.append(params[0])
                        .append("='")
                        .append(params[1].trim())
                        .append("' ");
            }

            builder.append(">")
                    .append(clearText(text))
                    .append("</font>");
        } else {
            builder.append(clearText(text));
        }
        return builder;
    }

    public static String clearText(String text){
        text = text.replaceAll("\\n", "");
//        if(text.startsWith("<br>"))
//            return text.replaceFirst("<br>", "");
        return text;
    }

//    public static String clearText(String text){
//        return text.replaceAll("\\n", "");
////        return text.replace("\n", "");
//    }
}
