package org.shikimori.library.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mcgars.imagefactory.objects.Thumb;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.custom.CustomGridlayout;
import org.shikimori.library.custom.yoyoanimation.OpacityInAnimator;
import org.shikimori.library.custom.yoyoanimation.OpacityOutAnimator;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.BaseQuery;
import org.shikimori.library.loaders.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.objects.one.UserRate.Status;
import org.shikimori.library.tool.baselisteners.BaseAnimationListener;
import org.shikimori.library.tool.constpack.AnimeStatuses;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.parser.elements.PostImage;
import org.shikimori.library.tool.parser.jsop.BodyBuild;
import org.shikimori.library.tool.popup.TextPopup;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.ui.tool.TextStyling;

import static org.shikimori.library.objects.one.UserRate.Status.*;

/**
 * Created by Владимир on 07.04.2015.
 */
public class ProjectTool {

    public static String buildType = "";

    public enum TYPE{
        ANIME, MANGA, OFFTOP, CHARACTER,
        ALL,NEWS,SITE,GROUP,
        REVIEWS, POLLS;

        public String toUrl(){
            switch (this){
                case ANIME: return "a";
                case MANGA: return "m";
                case OFFTOP: return "o";
                case CHARACTER: return "c";
                case NEWS: return "news";
                case SITE: return "s";
                case GROUP: return "g";
                case REVIEWS: return "reviews";
                case POLLS: return "v";
                default: return "all";
            }
        }
    }

    public static String getStatus(Context context, boolean anons, boolean ongoing){
        if(context==null)
            return "";
        if (!anons && !ongoing){
            return context.getString(R.string.incoming);
        }else if(anons){
            return context.getString(R.string.anons);
        }else if(ongoing){
            return context.getString(R.string.ongoing);
        }
        return "";
    }

    public static String getStatus(Context context, String status){
        switch (status){
            case "released": return context.getString(R.string.relize);
            case "SiteNews": return context.getString(R.string.site);
            case "episode":
            case "ongoing": return context.getString(R.string.ongoing);
            default: return status;
        }
    }

    public static void setTypeColorFromKind(Context context, View v, String type){
        int color = R.color.done;
        if(type != null){
            switch (type.toLowerCase()){
                case "released": color = R.color.black_owerlay_40; break;
                case "SiteNews": color = R.color.darkBlue; break;
                case "episode":
                case "ongoing": color = R.color.greenColor; break;
            }
        }
        v.setBackgroundColor(context.getResources().getColor(color));
    }

    public static void setStatusColor(Context context, View v, boolean anons, boolean ongoing){
        if(context == null)
            return;
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

    public static String getListStatusName(Context context, Status statusName, TYPE type){
        switch (statusName){
            case COMPLETED:
                return type == TYPE.ANIME ? context.getString(R.string.completed) : context.getString(R.string.completedmanga);
            case DROPPED:
                return context.getString(R.string.dropped);
            case ON_HOLD:
                return context.getString(R.string.on_hold);
            case PLANNED:
                return context.getString(R.string.planned);
            case WATCHING:
                return type == TYPE.ANIME ? context.getString(R.string.watching) : context.getString(R.string.watchingmanga);
            case REWATCHING:
                return type == TYPE.ANIME ? context.getString(R.string.rewatching) : context.getString(R.string.rewatchingmanga);
        }
        return null;
    }

    public static Status getListStatusValue(int id){
        if(id == R.id.watching)
            return WATCHING;
        else if(id == R.id.planned)
            return PLANNED;
        else if(id == R.id.completed)
            return COMPLETED;
        else if(id == R.id.on_hold)
            return ON_HOLD;
        else if(id == R.id.dropped)
            return DROPPED;
        else if(id == R.id.rewatching)
            return REWATCHING;
        else return NONE;
    }
    public static int getListPositionFromStatus(Status status){
        switch (status){
            case WATCHING: return 0;
            case PLANNED: return 1;
            case COMPLETED: return 2;
            case REWATCHING: return 3;
            case ON_HOLD: return 4;
            case DROPPED: return 5;
            default: return 0;
        }
    }
    public static Status getListStatusFromPosition(int position){
        switch (position){
            case 0: return WATCHING;
            case 1: return PLANNED;
            case 2: return COMPLETED;
            case 3: return REWATCHING;
            case 4: return ON_HOLD;
            case 5: return DROPPED;
            default: return NONE;
        }
    }

    public static int getItemIdFromStatus(Status status){
        switch (status){
            case WATCHING: return R.id.watching;
            case PLANNED: return R.id.planned;
            case COMPLETED: return R.id.completed;
            case ON_HOLD: return R.id.on_hold;
            case DROPPED: return R.id.dropped;
            case REWATCHING: return R.id.rewatching;
            default: return 0;
        }
    }

    public static String formatDatePost(String cteatedAt){
        return hs.getStringDate("dd MMMM yyyy HH:mm",
                hs.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", cteatedAt));
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
        if(url != null){
            if(url.contains("anime"))
                return c.getString(R.string.anime);
            else if(url.contains("manga"))
                return c.getString(R.string.manga);
            else if(url.contains("characters"))
                return c.getString(R.string.character);
        }
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

    public static String getStringFromType(TYPE type){
        if(type == TYPE.ANIME)
            return "Anime";
        else if (type == TYPE.MANGA)
            return "Manga";
        else if (type == TYPE.CHARACTER)
            return "Character";
        return "";
    }

    public static Intent getSimpleIntentDetails(Context context, String type){
        int page = -1;
        if(type != null){
            switch (type.toLowerCase()) {
                case Constants.ANIME:
                    page = ShowPageActivity.ANIME_PAGE; break;
                case Constants.MANGA:
                    page = ShowPageActivity.MANGA_PAGE; break;
                case Constants.CHARACTER:
                    page = ShowPageActivity.CHARACTER_PAGE; break;
                case Constants.CLUBS:
                    page = ShowPageActivity.CLUB_PAGE; break;
            }
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
        if(i == null)
            return;
        i.putExtra(Constants.ITEM_ID, itemId);
        context.startActivity(i);
    }

    public static void goToUser(Context context, String userId){
        Intent intent = new Intent(context, ShowPageActivity.class);
        intent.putExtra(Constants.USER_ID, userId);
        intent.putExtra(Constants.DISSCUSION_TYPE, Constants.TYPE_USER);
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

    public static BodyBuild getBodyBuilder(final BaseKitActivity<ShikiAC> activity, BodyBuild.CLICKABLETYPE type){
        final BodyBuild bodyBuilder = new BodyBuild(activity);
        bodyBuilder.setOnImageClickListener(new BodyBuild.ImageClickListener() {
            @Override
            public void imageClick(PostImage image) {
                ViewGroup parent = (ViewGroup) image.getImage().getParent();
                if (parent instanceof CustomGridlayout) {
                    int count = parent.getChildCount();
                    if (count > 1) {
                        List<Thumb> list = new ArrayList<>();
                        int selected = 0;
                        for (int i = 0; i < count; i++) {
                            PostImage img = (PostImage) parent.getChildAt(i).getTag();
                            if (image.equals(img))
                                selected = i;
                            String url = ProjectTool.fixUrl(img.getImageData().getOriginal());
                            list.add(new Thumb(url, url));
                        }
                        activity.getAC().getThumbToImage()
                                .zoom(image.getImage(), selected, list);
                        return;
                    }
                }
                activity.getAC().getThumbToImage().zoom(image.getImage(), ProjectTool.fixUrl(image.getImageData().getOriginal()));
            }
        });
        bodyBuilder.setClickType(type);
        return bodyBuilder;
    }

    public static void showComment(Query query, String id, final BodyBuild bodyBuild){
        Crouton.cancelAllCroutons();
        final TextPopup popup = new TextPopup((Activity) query.getContext());
        popup.showLoader();
        popup.setTitle(R.string.comment);
        query.init(ShikiApi.getUrl(ShikiPath.COMMENTS_ID, id))
                .getResultObject(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {
                        ItemCommentsShiki comment = new ItemCommentsShiki().create(res.getResultObject());
                        bodyBuild.parceAsync(comment.html_body, new BodyBuild.ParceDoneListener() {
                            @Override
                            public void done(ViewGroup view) {
                                popup.hideLoader();
                                popup.setBody(view);
                            }
                        });
                    }
                });
        popup.show();
    }

    public static boolean isFullVersion(){
        return buildType.equals("debug") || buildType.equals("full");
    }


    public static void deleteItem(final BaseKitActivity<ShikiAC> activity, String url){
        deleteItem(activity, url, null, null);
    }

    public static void deleteItem(BaseKitActivity<ShikiAC> activity, String url, final View animated, final BaseAnimationListener listener) {
        activity.getAC().getQuery().init(url)
                .setMethod(Query.METHOD.DELETE)
                .setErrorListener(new BaseQuery.OnQueryErrorListener() {
                    @Override
                    public void onQueryError(StatusResult res) {

                    }
                })
                .getResult(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {
                        if(animated!=null){
                            YoYo.with(Techniques.FadeOutUp)
                                    .withListener(listener)
                                    .duration(300)
                                    .playOn(animated);
                        }
                    }
                });
    }
}
