package org.shikimori.client;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.ImageLoader;

import io.fabric.sdk.android.Fabric;

import org.shikimori.client.activity.AboutActivity;
import org.shikimori.client.services.UserNotifyService;
import org.shikimori.client.tool.PreferenceHelper;
import org.shikimori.library.features.anime.AnimeDeatailsFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.push.PushHelperReceiver;

import ru.altarix.basekit.library.tools.pagecontroller.PageController;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class ShikiApplikation extends Application {
    /**
     * Private = 'Private'
     * # уведомление
     * Notification = 'Notification'
     * # Название аниме
     * Anons = AnimeHistoryAction::Anons
     * # Название аниме
     * Ongoing = AnimeHistoryAction::Ongoing
     * # Название аниме
     * Released = AnimeHistoryAction::Released
     * # Название аниме и эпизод
     * Episode = AnimeHistoryAction::Episode
     * # Ник пользователя кто запрашивает
     * FriendRequest = 'FriendRequest'
     * # Название клуба
     * GroupRequest = 'GroupRequest'
     * # новость сайта
     * SiteNews = 'SiteNews'
     * # прокомментирован профиль
     * ProfileCommented = 'ProfileCommented'
     * # пользователь процитирован кем-то где-то
     * Ник пользователя кто процетировал
     * QuotedByUser = 'QuotedByUser'
     * # комментарий в подписанной сущности
     * название сущности
     * SubscriptionCommented = 'SubscriptionCommented'
     * # уведомление о смене ника
     * Ник пользователя кто сменил на что сменил
     * NicknameChanged = 'NicknameChanged'
     * # уведомление о бане
     * Banned = 'Banned'
     * # уведомление о предупреждении
     * Warned = 'Warned'
     * # уведомление о принятии/отказе правки
     * Название топика где произошло
     * VersionAccepted = 'VersionAccepted'
     * VersionRejected = 'VersionRejected'
     * # уведомление о завершении опроса
     * название опроса
     * ContestFinished = 'ContestFinished'
     */


    public static final String OPEN_PAGE = "open_launch_page";
    public static final String PRIVATE = "private";
    public static final String NEW_NEWS = "new_news";
    public static final String NOTIFICATION = "notification";

    public static final String ANONS = "anons";
    public static final String BANNED = "banned";
    public static final String CLUB_REQUEST = "club_request";
    public static final String CONTEST_FINISHED = "contest_finished";
    public static final String EPISODE = "episode";
    public static final String FRIEND_REQUEST = "friend_request";
    public static final String NICKNAME_CHANGED = "nickname_changed";
    public static final String ONGOING = "ongoing";
    public static final String PROFILE_COMMENTED = "profile_commented";
    public static final String QUOTED_BY_USER = "quoted_by_user";
    public static final String RELEASED = "released";
    public static final String SITE_NEWS = "site_news";
    public static final String VERSION_ACCEPTED = "version_accepted";
    public static final String VERSION_REJECTED = "version_rejected";
    public static final String WARNED = "warned";
    public static final String SUBSCRIPTION_COMMENTED = "subscription_commented";


    public static final String NEW_VERSION = "new_version";


    public static final int PRIVATE_ID = 3;
    public static final int NEWS_ID = 4;
    public static final int NOTIFICATION_ID = 5;
    public static final int VERSION_ID = 6;
    public static final int UNDEFINE_ID = 7;
//    private GoogleAnalytics analytics;
//    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            final Fabric fabric = new Fabric.Builder(this)
                    .kits(new Crashlytics())
                    .debuggable(ProjectTool.isFullVersion())
                    .build();
            Fabric.with(fabric);
        }

        try {
            Class.forName("android.os.AsyncTask");
            Class.forName("android.support.v7.internal.view.menu.MenuBuilder");
        } catch (Throwable ignore) {
            // ignored
        }

//        analytics = GoogleAnalytics.getInstance(this);
//        analytics.setLocalDispatchPeriod(1800);
//
//        tracker = analytics.newTracker("UA-39194517-2"); // Replace with actual tracker/property Id
//        tracker.enableExceptionReporting(true);
////        tracker.enableAdvertisingIdCollection(true);
//        tracker.enableAutoActivityTracking(true);

        // Setup handler for uncaught exceptions.
//        if (!BuildConfig.DEBUG) {
//            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//                @Override
//                public void uncaughtException(Thread thread, Throwable e) {
//                    handleUncaughtException(thread, e);
//                }
//            });
//        }

        new ShikiUser(this);
        ShikiApi.setIsDebug(BuildConfig.DEBUG);
        QueryShiki.setIsDebug(BuildConfig.DEBUG);
        initImageLoader(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                WebView.setWebContentsDebuggingEnabled(ShikiApi.isDebug);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        PushHelperReceiver.addAction(PRIVATE, getMessgesPushAction(PRIVATE_ID));

        PushHelperReceiver.PushAction newsAction = getMessgesPushAction(NEWS_ID);
        PushHelperReceiver.PushAction notifyAction = getMessgesPushAction(NOTIFICATION_ID);

        PushHelperReceiver.addAction(NEW_NEWS, newsAction);
        PushHelperReceiver.addAction(ONGOING, newsAction);
        PushHelperReceiver.addAction(ANONS, newsAction);
        PushHelperReceiver.addAction(RELEASED, newsAction);
        PushHelperReceiver.addAction(EPISODE, newsAction);
        PushHelperReceiver.addAction(SITE_NEWS, newsAction);
        PushHelperReceiver.addAction(NOTIFICATION, notifyAction);
        PushHelperReceiver.addAction(CLUB_REQUEST, notifyAction);
        PushHelperReceiver.addAction(CONTEST_FINISHED, notifyAction);
        PushHelperReceiver.addAction(FRIEND_REQUEST, notifyAction);
        PushHelperReceiver.addAction(NICKNAME_CHANGED, notifyAction);
        PushHelperReceiver.addAction(PROFILE_COMMENTED, notifyAction);
        PushHelperReceiver.addAction(QUOTED_BY_USER, notifyAction);
        PushHelperReceiver.addAction(VERSION_ACCEPTED, notifyAction);
        PushHelperReceiver.addAction(VERSION_REJECTED, notifyAction);
        PushHelperReceiver.addAction(WARNED, notifyAction);
        PushHelperReceiver.addAction(SUBSCRIPTION_COMMENTED, notifyAction);


        PushHelperReceiver.addAction(NEW_VERSION, getMessgesPushAction(VERSION_ID));


//        PushHelperReceiver.setNonEmpAction(getNonUndefineAction());

        PageController.baseActivityController = ShikiAC.class;
        ProjectTool.buildType = BuildConfig.BUILD_TYPE;
        AnimeDeatailsFragment.UPDATE_AUTO_SERIES = PreferenceHelper.isUpdateSeries(this);
    }

//    public void handleUncaughtException(Thread thread, Throwable e) {
//        e.printStackTrace(); // not all Android versions will print the stack trace automatically
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        e.printStackTrace(pw);
//
//        Intent intent = new Intent();
//        intent.putExtra(SendLogActivity.MSG, sw.toString());
//        intent.setAction("ru.altarix.mos.shikimory.SEND_LOG"); // see step 5.
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
//        startActivity(intent);
//
//        System.exit(1); // kill off the crashed app
//    }

    public static ImageLoader initImageLoader(Context c) {
        return hs.initImageLoader(c);
    }

    public PushHelperReceiver.PushAction getMessgesPushAction(final int id) {
        return new PushHelperReceiver.PushActionSimple() {

            @Override
            public int getNotifyId() {
                return super.getNotifyId() + id;
            }

            @Override
            public Intent getNotifyIntent() {
                Intent newIntent;
                if (id == VERSION_ID) {
                    newIntent = new Intent(ShikiApplikation.this, AboutActivity.class);
                } else {
                    newIntent = new Intent(ShikiApplikation.this, MainActivity.class);
                    newIntent.putExtra(OPEN_PAGE, id);
                }
                newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                return newIntent;
            }

            @Override
            public void onPushRecived(Bundle bundle) {
                QueryShiki query = new QueryShiki(ShikiApplikation.this);
                query.invalidateCache(ShikiApi.getUrl(ShikiPath.UNREAD_MESSAGES, ShikiUser.USER_ID));
                if (id == PRIVATE_ID) {
                    Intent intent = new Intent(ShikiApplikation.this, UserNotifyService.class);
                    intent.putExtras(bundle);
                    startService(intent);
                }

                bundle.putString(PushHelperReceiver.MSG_TITLE,
                        getTitleFromAction(bundle.getString(PushHelperReceiver.ACTION)));

            }

            @Override
            public boolean isNoNotification() {
                return id == PRIVATE_ID;
            }

            @Override
            public boolean isAuthRequired() {
                return ShikiUser.USER_ID != null;
            }

            @Override
            public boolean isSound() {
                return PreferenceHelper.getNotifySound(getApplicationContext());
            }
        };
    }

    public PushHelperReceiver.PushAction getNonUndefineAction() {
        return new PushHelperReceiver.PushActionSimple() {

            @Override
            public int getNotifyId() {
                return super.getNotifyId() + UNDEFINE_ID;
            }

            @Override
            public void onPushRecived(Bundle bundle) {
                if (isAuthRequired()) {
                    PushHelperReceiver.notifyUser(ShikiApplikation.this, this, bundle);
                }
            }

            @Override
            public Intent getNotifyIntent() {
                Intent newIntent;
                newIntent = new Intent(ShikiApplikation.this, AboutActivity.class);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return newIntent;
            }

            @Override
            public boolean isAuthRequired() {
                return ShikiUser.USER_ID != null;
            }

            @Override
            public boolean isSound() {
                return PreferenceHelper.getNotifySound(getApplicationContext());
            }
        };
    }

    public static void runService(Context context) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(context.getPackageName() + ".LAUNCH_FROM_APP");
        context.sendBroadcast(broadcastIntent);
    }

    public String getTitleFromAction(String string) {
        switch (string) {
            case ANONS:
                return getString(R.string.anons);
            case BANNED:
                return getString(R.string.banned);
            case CLUB_REQUEST:
                return getString(R.string.club_request);
            case CONTEST_FINISHED:
                return getString(R.string.contest_finished);
            case EPISODE:
                return getString(R.string.new_episode);
            case FRIEND_REQUEST:
                return getString(R.string.friend_request);
            case NICKNAME_CHANGED:
                return getString(R.string.nickname_changed);
            case ONGOING:
                return getString(R.string.ongoing);
            case PROFILE_COMMENTED:
                return getString(R.string.profile_commented);
            case QUOTED_BY_USER:
                return getString(R.string.quoted_by_user);
            case RELEASED:
                return getString(R.string.relize);
            case SITE_NEWS:
                return getString(R.string.news);
            case VERSION_ACCEPTED:
                return getString(R.string.version_accepted);
            case VERSION_REJECTED:
                return getString(R.string.version_rejected);
            case WARNED:
                return getString(R.string.warned);
            case SUBSCRIPTION_COMMENTED:
                return getString(R.string.comment);
        }
        return null;
    }
}
