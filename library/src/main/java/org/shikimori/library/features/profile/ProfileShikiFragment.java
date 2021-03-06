package org.shikimori.library.features.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;
import org.shikimori.library.R;
import org.shikimori.library.adapters.ProfileMangaAnimeNameAdapter;
import org.shikimori.library.features.anime.AnimeUserListFragment;
import org.shikimori.library.features.manga.MangaUserListFragment;
import org.shikimori.library.interfaces.LogouUserListener;
import org.shikimori.library.interfaces.UserDataChangeListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.objects.one.AnimeManga;
import org.shikimori.library.features.profile.model.UserDetails;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.LinkHelper;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.constpack.AnimeStatuses;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.features.profile.controllers.NotifyProfileController;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.parser.elements.PostImage;
import org.shikimori.library.tool.parser.jsop.BodyBuild;
import org.shikimori.library.tool.popup.ListPopup;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import ru.altarix.basekit.library.activities.BaseKitActivity;
import ru.altarix.basekit.library.tools.DialogCompat;

/**
 * Created by Владимир on 30.03.2015.
 */
public class ProfileShikiFragment extends PullableFragment<BaseKitActivity<ShikiAC>> implements QueryShiki.OnQuerySuccessListener<ShikiStatusResult>, View.OnClickListener, BaseKitActivity.OnFragmentBackListener {

    private ImageView avatar;
    private TextView tvUserName;
    private UserDetails userDetails;
    private TextView tvMiniDetails, tvAnimeProgress, tvMangaProgress, tvLastOnline;
    private SeekBar sbAnimeProgress, sbMangaProgress;
    private View llBody, ivWebShow;
    private ListPopup pop;
    private NotifyProfileController notifyController;
    private GridView gvBodyProfile;
    private Menu actionMenu;

    public static ProfileShikiFragment newInstance() {
        return new ProfileShikiFragment();
    }

    public static ProfileShikiFragment newInstance(String userId) {
        Bundle b = new Bundle();
        b.putString(Constants.USER_ID, userId);

        ProfileShikiFragment frag = new ProfileShikiFragment();
        frag.setArguments(b);
        return frag;
    }

    public static ProfileShikiFragment newInstance(Bundle b) {
        ProfileShikiFragment frag = new ProfileShikiFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shiki_profile, null);
        llBody = v.findViewById(R.id.llBody);
        ivWebShow = v.findViewById(R.id.ivWebShow);
        avatar = (ImageView) v.findViewById(R.id.ava);
        tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        tvLastOnline = (TextView) v.findViewById(R.id.tvLastOnline);
        tvMiniDetails = (TextView) v.findViewById(R.id.tvMiniDetails);
        tvAnimeProgress = (TextView) v.findViewById(R.id.tvAnimeProgress);
        tvMangaProgress = (TextView) v.findViewById(R.id.tvMangaProgress);
        sbAnimeProgress = (SeekBar) v.findViewById(R.id.sbAnimeProgress);
        sbMangaProgress = (SeekBar) v.findViewById(R.id.sbMangaProgress);
        gvBodyProfile = (GridView) v.findViewById(R.id.gvBodyProfile);

        v.findViewById(R.id.ivAnimeListShow).setOnClickListener(this);
        v.findViewById(R.id.ivMangaListShow).setOnClickListener(this);
        v.findViewById(R.id.bAboutMe).setOnClickListener(this);
        ivWebShow.setOnClickListener(this);
        sbAnimeProgress.setOnTouchListener(disableScrolling);
        sbMangaProgress.setOnTouchListener(disableScrolling);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.user_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        actionMenu = menu;
        checkUserMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(userDetails == null){
            Crouton.makeText(activity, R.string.wait_load_user_data, Style.ALERT).show();
            return true;
        }
        if (item.getItemId() == R.id.ic_logout) {
            new DialogCompat(activity)
                    .setNegativeListener(null)
                    .setPositiveListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((LogouUserListener) activity).logoutTrigger();
                        }
                    }).showConfirm(activity.getString(R.string.logout));
            return true;
        } else if (item.getItemId() == R.id.ic_send_message) {
            activity.loadPage(ChatFragment.newInstance(userDetails.user.nickname, getFC().getUserId()));
            return true;
        } else if (item.getItemId() == R.id.ic_add_friend){
            userDetails.inFriends = !userDetails.inFriends;
            checkUserFriend();
            sendFriendToServer(userDetails.inFriends);
            invalidateData();
            return true;
        } else if (item.getItemId() == R.id.ic_ignore){
            if(userDetails.showComments){
                new AlertDialog.Builder(activity)
                    .setTitle(R.string.disable_user_messages)
                    .setMessage(R.string.disable_user_messages_text)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ignoreUser();
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            } else {
                ignoreUser();
            }
            return true;
        }
        return false;
    }

    void ignoreUser(){
        userDetails.showComments = !userDetails.showComments;
        checkUserFriend();
        sendIgnoreToServer(userDetails.showComments);
        invalidateData();
    }

    private void sendFriendToServer(boolean inFriends) {

        getFC().getQuery().init(ShikiApi.getUrl(ShikiPath.SET_FRIEND, getFC().getUserId()))
             .setMethod(inFriends ? QueryShiki.METHOD.POST : QueryShiki.METHOD.DELETE)
              .getResult(new QueryShiki.OnQuerySuccessListener<ShikiStatusResult>() {
                  @Override
                  public void onQuerySuccess(ShikiStatusResult res) {
                      if(activity == null)
                          return;
                      Crouton.makeText(activity, res.getParameter("notice"), Style.CONFIRM).show();
                  }
              });
    }
    private void sendIgnoreToServer(boolean showComments) {

        getFC().getQuery().init(ShikiApi.getUrl(ShikiPath.SET_IGNORES, getFC().getUserId()))
             .setMethod(!showComments ? QueryShiki.METHOD.POST : QueryShiki.METHOD.DELETE)
                .getResult(new QueryShiki.OnQuerySuccessListener<ShikiStatusResult>() {
                    @Override
                    public void onQuerySuccess(ShikiStatusResult res) {
                        if(activity == null)
                            return;
                        Crouton.makeText(activity, res.getParameter("notice"), Style.CONFIRM).show();
                    }
                });
    }

    void checkUserFriend(){
        if(getView()!=null)
            getView().post(new Runnable() {
                @Override
                public void run() {
                    if(actionMenu!=null && !isSelfProfile()){
                        if(actionMenu == null)
                            return;
                        MenuItem item = actionMenu.findItem(R.id.ic_add_friend);
                        MenuItem item_ignore = actionMenu.findItem(R.id.ic_ignore);

                        if(item_ignore ==null || item == null)
                            return;

                        if(!userDetails.inFriends){
                            item.setIcon(R.drawable.ic_action_favorite_white);
                        } else
                            item.setIcon(R.drawable.ic_action_favorite_blue);

                        if(userDetails.isIgnored){
                            item_ignore.setIcon(R.drawable.ic_action_bell_off);
                        } else
                            item_ignore.setIcon(R.drawable.ic_action_bell_on);
                    }
                }
            });
    }

    void checkUserMenu() {
        if (activity != null) {
            if (actionMenu != null) {
                if (isSelfProfile())
                    actionMenu.setGroupVisible(R.id.userGroup, false);
                else {
                    actionMenu.setGroupVisible(R.id.selfUserGroup, false);
                }
            }
        }
    }

    boolean isSelfProfile(){
        try {
            return getFC().getUserId().equals(activity.getAC().getShikiUser().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    View.OnTouchListener disableScrolling = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    };

    @Override
    public int pullableViewId() {
        return 0;
    }

    @Override
    public int getWrapperId() {
        return R.id.swipeLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // load unread messages
        showRefreshLoader();

        checkUserMenu();
        notifyController = new NotifyProfileController(activity,
                getFC().getQuery(), activity.getAC().getShikiUser(), getFC().getUserId(), gvBodyProfile);

        loadDataFromServer();
    }

    @Override
    public void onStartRefresh() {
        invalidateData();
        loadDataFromServer();
    }

    void invalidateData(){
        getFC().getQuery().invalidateCache(ShikiApi.getUrl(ShikiPath.GET_USER_DETAILS) + getFC().getUserId());
        if(notifyController!=null)
            notifyController.invalidate();
    }

    void loadDataFromServer() {
        getFC().getQuery().init(ShikiApi.getUrl(ShikiPath.GET_USER_DETAILS) + getFC().getUserId())
                .setCache(true, QueryShiki.HOUR)
                .getResult(this);

    }

    @Override
    public void onQuerySuccess(ShikiStatusResult res) {

        if (activity == null)
            return;

        hs.setVisible(llBody);

        stopRefresh();
        userDetails = new UserDetails().create(res.getResultObject());

        fillUi(res.getResultObject());

        buildProfile();

        checkUserFriend();
    }

    void updateUserUI() {
        if (getFC().getUserId() != null && getFC().getUserId().equalsIgnoreCase(ShikiUser.USER_ID)) {
            if (activity instanceof UserDataChangeListener)
                ((UserDataChangeListener) activity).updateUserUI();
        }
    }

    private void fillUi(JSONObject resultObject) {
        if (userDetails == null || userDetails.user == null)
            return;

        if (activity.getAC().getShikiUser().getId().equalsIgnoreCase(userDetails.user.id))
            activity.getAC().getShikiUser().setData(resultObject);

        if (userDetails.user.img148 != null)
            ImageLoader.getInstance().displayImage(userDetails.user.img148, avatar);
        tvUserName.setText(userDetails.user.nickname);

        setSexYearLocation();

        // когда был на сайте
        tvLastOnline.setText(userDetails.lastOnline);
        // web site
        setWebSite();
        // anime / manga progress
        setProgress();

    }

    @Override
    public void onResume() {
        super.onResume();
        if(notifyController!=null){
            if(NotifyProfileController.isNeedRefresh()){
                notifyController.invalidate();
                buildProfile();
            } else {
                notifyController.updateLocalData(activity.getAC().getShikiUser().getNotification());
            }
        }
    }

    private void buildProfile() {
        if (notifyController != null){
            notifyController.updateLocalData(activity.getAC().getShikiUser().getNotification());
            notifyController.load(new QueryShiki.OnQuerySuccessListener<ShikiStatusResult>() {
                @Override
                public void onQuerySuccess(ShikiStatusResult res) {
                    if(activity == null)
                        return;
                    updateUserUI();
                }
            });
        }
    }

    private void setProgress() {
        ProgressData progress;
        if (userDetails.fullStatuses != null) {
            // set anime progress
            progress = getProgress(userDetails.fullStatuses.animes);
            sbAnimeProgress.setProgress(progress.percentage1);
            sbAnimeProgress.setSecondaryProgress(progress.percentage2);
            tvAnimeProgress.setText(String.format(
                    activity.getString(R.string.seeing),
                    progress.firstProgress,
                    progress.fullProgress
            ));
            // set manga progress
            progress = getProgress(userDetails.fullStatuses.manga);
            sbMangaProgress.setProgress(progress.percentage1);
            sbMangaProgress.setSecondaryProgress(progress.percentage2);
            tvMangaProgress.setText(String.format(
                    activity.getString(R.string.reading),
                    progress.firstProgress,
                    progress.fullProgress
            ));
        }
    }

    /**
     * Показываем иконку web странички
     */
    private void setWebSite() {
        hs.setVisibleGone(TextUtils.isEmpty(userDetails.website), ivWebShow);
    }

    private void setSexYearLocation() {
        StringBuilder str = new StringBuilder();
        // пол
        if (!TextUtils.isEmpty(userDetails.sex)) {
            switch (userDetails.sex) {
                case "male":
                    str.append(activity.getString(R.string.male));
                    break;
                case "female":
                    str.append(activity.getString(R.string.female));
                    break;
            }
        }
        // сколько лет
        if (str.length() > 0 && !TextUtils.isEmpty(userDetails.fullYears)) {
            str.append(" / ")
                    .append(activity.getString(R.string.years))
                    .append(" ")
                    .append(userDetails.fullYears);
        }
        // где живем
        if (!TextUtils.isEmpty(userDetails.location))
            str.append("\n").append(userDetails.location);

        tvMiniDetails.setText(str.toString());
    }

    /**
     * Расчет прочитанного из аниме и манги
     * progress 1 = просмотренно
     * progress 2 = запланированно + смотрю + просмотренно + отложено
     * max size = запланированно + смотрю + просмотренно + отложено + брошено
     */
    private ProgressData getProgress(List<AnimeManga> list) {
        ProgressData progr = new ProgressData();
        for (AnimeManga animeManga : list) {
            progr.fullProgress += animeManga.counted;
            if (AnimeStatuses.COMPLETED.equals(animeManga.name))
                progr.firstProgress += animeManga.counted;
            if (!AnimeStatuses.DROPPED.equals(animeManga.name)
                    && !AnimeStatuses.REWATCHING.equals(animeManga.name))
                progr.secondProgress += animeManga.counted;
        }

        int fullProgress = progr.fullProgress == 0 ? 1 : progr.fullProgress;

        progr.percentage1 = progr.firstProgress * 100 / fullProgress;
        progr.percentage2 = progr.secondProgress * 100 / fullProgress;

        return progr;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivWebShow) {
            LinkHelper.goToUrl(activity, userDetails.website);
        } else if (v.getId() == R.id.ivAnimeListShow) {
            showPopup(animePopupListener, ProjectTool.TYPE.ANIME, R.string.lists_anime);
        } else if (v.getId() == R.id.ivMangaListShow) {
            showPopup(mangaPopupListener, ProjectTool.TYPE.MANGA, R.string.lists_manga);
        } else if (v.getId() == R.id.bAboutMe) {
            activity.getPageController()
                    .startActivity(ProfileAboutFragment.class, userDetails.aboutHtml);
        }
    }

    protected void showPopup(AdapterView.OnItemClickListener listener, ProjectTool.TYPE type, int title){
        if(userDetails == null || userDetails.fullStatuses == null)
            return;
        pop = new ListPopup(activity);
        pop.setAnimate(Techniques.Pulse);
        pop.setOnItemClickListener(listener);
        pop.setAdapter(new ProfileMangaAnimeNameAdapter(activity,
                type == ProjectTool.TYPE.MANGA ? userDetails.fullStatuses.manga :
                        userDetails.fullStatuses.animes, type));
        pop.setTitle(title);
        pop.show();
    }

    AdapterView.OnItemClickListener animePopupListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AnimeManga item = userDetails.fullStatuses.animes.get(position);
            goToAnimeManga(item.id, item.name, ProjectTool.TYPE.ANIME);
        }
    };

    AdapterView.OnItemClickListener mangaPopupListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AnimeManga item = userDetails.fullStatuses.manga.get(position);
            goToAnimeManga(item.id, item.name, ProjectTool.TYPE.MANGA);
        }
    };

    void goToAnimeManga(String id, String name, ProjectTool.TYPE type) {
        Bundle b = new Bundle();
        // TODO SET DATA
        b.putString(Constants.LIST_ID, id);
        b.putString(Constants.ACTION_BAR_TITLE, ProjectTool.getListStatusName(activity, name, type));
        b.putString(Constants.USER_ID, getFC().getUserId());
        if (type == ProjectTool.TYPE.ANIME)
            activity.loadPage(AnimeUserListFragment.newInstance(b));
        else
            activity.loadPage(MangaUserListFragment.newInstance(b));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onBackPressed() {
        if (pop != null && pop.hide())
            return true;
        return false;
    }

    class ProgressData {
        int firstProgress, secondProgress, fullProgress, percentage1, percentage2;
    }

}
