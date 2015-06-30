package org.shikimori.library.tool;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.custom.yoyoanimation.OpacityInAnimator;
import org.shikimori.library.custom.yoyoanimation.OpacityOutAnimator;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.constpack.AnimeStatuses;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.parser.elements.PostImage;
import org.shikimori.library.tool.parser.jsop.BodyBuild;
import org.shikimori.library.tool.pmc.PopupMenuCompat;

import ru.altarix.ui.tool.TextStyling;

/**
 * Created by Владимир on 07.04.2015.
 */
public class ProjectTool {

    public enum TYPE{
        ANIME, MANGA, OFFTOP, CHARACTER
    }

    public static String getStatus(Context context, boolean anons, boolean ongoing){
        if (!anons && !ongoing){
            return context.getString(R.string.incoming);
        }else if(anons){
            return context.getString(R.string.anons);
        }else if(ongoing){
            return context.getString(R.string.ongoing);
        }
        return "";
    }

    public static void setStatusColor(Context context, View v, boolean anons, boolean ongoing){
        if (!anons && !ongoing){
            v.setBackgroundColor(context.getResources().getColor(R.color.done));
        }else if(anons){
            v.setBackgroundColor(context.getResources().getColor(R.color.anons));
        }else if(ongoing){
            v.setBackgroundColor(context.getResources().getColor(R.color.ongoing));
        }
    }

    public static void setTypeColor(Context context, View v, TYPE type){
        if (type == TYPE.MANGA){
            v.setBackgroundColor(context.getResources().getColor(R.color.lightPink));
        }else if(type == TYPE.ANIME){
            v.setBackgroundColor(context.getResources().getColor(R.color.darkBlue));
        }else if(type == TYPE.OFFTOP){
            v.setBackgroundColor(context.getResources().getColor(R.color.anons));
        } else
            v.setBackgroundColor(context.getResources().getColor(R.color.done));
    }
    
    public static void setTypeColor(Context context, View v, String type){
        int color = R.color.done;
        if(type != null){
            switch (type.toLowerCase()){
                case Constants.ANIME: color = R.color.darkBlue; break;
                case Constants.MANGA: color = R.color.lightPink; break;
                case Constants.TOPIC: color = R.color.anons; break;
            }
        }
        v.setBackgroundColor(context.getResources().getColor(color));
    }

    public static String getListStatusName(Context context, String statusName, TYPE type){
        switch (statusName){
            case AnimeStatuses.COMPLETED:
                return type == TYPE.ANIME ? context.getString(R.string.completed) : context.getString(R.string.completedmanga);
            case AnimeStatuses.DROPPED:
                return context.getString(R.string.dropped);
            case AnimeStatuses.ON_HOLD:
                return context.getString(R.string.on_hold);
            case AnimeStatuses.PLANNED:
                return context.getString(R.string.planned);
            case AnimeStatuses.WATCHING:
                return type == TYPE.ANIME ? context.getString(R.string.watching) : context.getString(R.string.watchingmanga);
            case AnimeStatuses.REWATCHING:
                return type == TYPE.ANIME ? context.getString(R.string.rewatching) : context.getString(R.string.rewatchingmanga);
        }
        return null;
    }

    public static String formatDatePost(String cteatedAt){
        return h.getStringDate("dd MMMM yyyy HH:mm",
                h.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", cteatedAt));
    }

    public static String fixUrl(String url){
        if(url == null)
            return url;

        if(!url.startsWith("http"))
            url = ShikiApi.HTTP_SERVER + url;
        return url;
    }

    public static Spannable getTitleElement(String rusname, String engName){
        return getTitleElement(rusname, engName, "66ffffff");
    }
    public static Spannable getTitleElement(String rusname, String engName, String color){
        TextStyling styling = new TextStyling()
                .addGlobalStyle(TextStyling.TextStyle.COLOR, color);

        if(rusname!=null){
            return styling.formatString(rusname, engName + "\n" + rusname);
        } else
            return new SpannableString(engName);
    }

    public static String getTypeFromUrl(Context c, String url){
        if(url.contains("anime"))
            return c.getString(R.string.anime);
        else if(url.contains("manga"))
            return c.getString(R.string.manga);
        else if(url.contains("characters"))
            return c.getString(R.string.character);
        return "";
    }

    public static TYPE getTypeFromUrl(String url){
        if(url.contains("anime"))
            return TYPE.ANIME;
        else if(url.contains("manga"))
            return TYPE.MANGA;
        else if(url.contains("characters"))
            return TYPE.CHARACTER;
        return null;
    }

    public static Intent getSimpleIntentDetails(Context context, String type){
        int page = -1;
        switch (type.toLowerCase()) {
            case Constants.ANIME:
                page = ShowPageActivity.ANIME_PAGE; break;
            case Constants.MANGA:
                page = ShowPageActivity.MANGA_PAGE; break;
            case Constants.CHARACTER:
                page = ShowPageActivity.CHARACTER_PAGE; break;
        }

        if(page > -1){
            Intent intent = new Intent(context, ShowPageActivity.class);
            intent.putExtra(Constants.PAGE_FRAGMENT, page);
            return intent;
        }
        return null;

    }

    public static void ShowSimplePage(Context context, String type, String itemId){
        Intent i = getSimpleIntentDetails(context, type);
        i.putExtra(Constants.ITEM_ID, itemId);
        context.startActivity(i);
    }

    public static void goToUser(Context context, String userId){
        Intent intent = new Intent(context, ShowPageActivity.class);
        intent.putExtra(Constants.USER_ID, userId);
        intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.USER_PROFILE);
        context.startActivity(intent);
    }

    public static void setReadOpasity(View v, boolean read) {
        BaseViewAnimator animator = read ?
                new OpacityOutAnimator() : new OpacityInAnimator();
        YoYo.with(animator)
            .duration(300)
            .playOn(v);
    }

    public static BodyBuild getBodyPuilder(final BaseActivity activity, BodyBuild.CLICKABLETYPE type){
        BodyBuild bodyBuilder = new BodyBuild(activity);
        bodyBuilder.setOnImageClickListener(new BodyBuild.ImageClickListener() {
            @Override
            public void imageClick(PostImage image) {
                activity.getThumbToImage().zoom(image.getImage(), image.getImageData().getOriginal());
            }
        });
        bodyBuilder.setClickType(type);
        bodyBuilder.setUrlTextListener(new BodyBuild.UrlTextListener() {
            @Override
            public void textLink(String url) {
                LinkHelper.goToUrl(activity, url);
            }
        });

        return bodyBuilder;
    }
}
