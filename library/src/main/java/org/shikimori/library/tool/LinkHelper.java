package org.shikimori.library.tool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;

import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.altarix.basekit.library.activity.BaseKitActivity;

/**
 * Created by Владимир on 18.06.2015.
 */
public class LinkHelper {
    public static void goToUrl(BaseKitActivity<ShikiAC> activity, String url){
        goToUrl(activity, url, null);
    }
    public static void goToUrl(BaseKitActivity<ShikiAC> activity, String url, BodyBuild bodyBuild){
        if(TextUtils.isEmpty(url))
            return;

        if(url.contains("/animes")){
            if(goToPage(activity, "animes", url, Constants.ANIME))
                return;
        } else if(url.contains("/mangas")){
            if(goToPage(activity, "mangas", url, Constants.MANGA))
                return;
        } else if(url.contains("/characters")){
            if(goToPage(activity, "characters", url, Constants.CHARACTER))
                return;
        } else if(url.contains("/clubs")){
            if(goToPage(activity, "clubs", url, Constants.CLUBS))
                return;
        } else if(bodyBuild!=null && url.contains("/comments")){
            String id = getItemId("comments", url);
            ProjectTool.showComment(activity.getAC().getQuery(), id, bodyBuild);
            return;
        }

        url = ProjectTool.fixUrl(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(url));
        activity.startActivity(intent);
    }

    static boolean goToPage(Context contenx, String name, String url, String type){
        String id = getItemId(name, url);
        if(id!=null){
            ProjectTool.ShowSimplePage(contenx, type, id);
            return true;
        }
        return false;
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
