package org.shikimori.library.tool;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.tool.constpack.AnimeStatuses;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by Владимир on 07.04.2015.
 */
public class ProjectTool {

    public enum TYPE{
        ANIME, MANGA, OFFTOP
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

    public static void setTextViewHTML(Context activity, TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(activity, strBuilder, span);
        }
        text.setMovementMethod(LinkMovementMethod.getInstance());
        text.setText(strBuilder);
    }

    private static void makeLinkClickable(final Context activity, SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
//                Intent intent = new Intent(activity, WebViewActivity.class);
//                intent.putExtra(WebviewFragment.URL_NAME, span.getURL());
//                activity.startActivity(intent);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }
}
