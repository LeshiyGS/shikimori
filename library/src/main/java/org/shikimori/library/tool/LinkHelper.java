package org.shikimori.library.tool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.shikimori.library.tool.constpack.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Владимир on 18.06.2015.
 */
public class LinkHelper {
    public static void goToUrl(Context context, String url){

        if(url.contains("/animes")){
            String id = getItemId("animes", url);
            if(id!=null){
                ProjectTool.ShowSimplePage(context, Constants.ANIME, id);
                return;
            }
        } else if(url.contains("/mangas")){
            String id = getItemId("mangas", url);
            if(id!=null){
                ProjectTool.ShowSimplePage(context, Constants.MANGA, id);
                return;
            }
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    static String getItemId(String pattern, String url){
        Pattern p = Pattern.compile("\\/"+pattern+"\\/([0-9]+)");
        Matcher matcher = p.matcher(url);
        if (matcher.find()){
            return matcher.group(1);
        }
        return null;
    }
}
