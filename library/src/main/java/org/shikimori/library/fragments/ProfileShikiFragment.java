package org.shikimori.library.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.adapters.ProfileMangaAnnimeNameAdapter;
import org.shikimori.library.interfaces.UserDataChangeListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.AnimeManga;
import org.shikimori.library.objects.one.UserDetails;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.constpack.AnimeStatuses;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.NotifyProfileController;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.jsop.BodyBuild;
import org.shikimori.library.tool.popup.ListPopup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Владимир on 30.03.2015.
 */
public class ProfileShikiFragment extends PullableFragment<BaseActivity> implements Query.OnQuerySuccessListener, View.OnClickListener, BaseActivity.OnFragmentBackListener {

    private ImageView avatar;
    private TextView tvUserName;
    private UserDetails userDetails;
    private TextView tvMiniDetails,tvAnimeProgress,tvMangaProgress, tvLastOnline;
    private SeekBar sbAnimeProgress, sbMangaProgress;
    private View llBody, ivWebShow;
    private ListPopup pop;
    private ViewGroup llBodyProfile;
    private NotifyProfileController notifyController;

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

    @Override
    public int getActionBarTitle() {
        return R.string.profile;
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
        llBodyProfile = (ViewGroup) v.findViewById(R.id.llBodyProfile);
        h.setVisible(llBody, false);

        v.findViewById(R.id.ivAnimeListShow).setOnClickListener(this);
        v.findViewById(R.id.ivMangaListShow).setOnClickListener(this);
        ivWebShow.setOnClickListener(this);
        sbAnimeProgress.setOnTouchListener(disableScrolling);
        sbMangaProgress.setOnTouchListener(disableScrolling);

        return v;
    }

    View.OnTouchListener disableScrolling = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    };

    @Override
    public int pullableViewId() {
        return R.id.bodyScroll;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // load unread messages
        if(getView()!=null)
            getView().post(new Runnable() {
                @Override
                public void run() {
                    notifyController = new NotifyProfileController(activity,
                            query, activity.getShikiUser(), llBodyProfile);
                    showRefreshLoader();
                    loadDataFromServer();
                    activity.setOnFragmentBackListener(ProfileShikiFragment.this);
                }
            });

        testHtml();
    }

    @Override
    public void onStartRefresh() {
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.GET_USER_DETAILS) + getUserId());
        loadDataFromServer();
    }

    void loadDataFromServer(){
        query.init(ShikiApi.getUrl(ShikiPath.GET_USER_DETAILS)+getUserId())
             .setCache(true, Query.HOUR)
             .getResult(this);

    }

    @Override
    public void onQuerySuccess(StatusResult res) {

        if(activity == null)
            return;

        h.setVisible(llBody, true);
        YoYo.with(Techniques.FadeIn)
            .playOn(llBody);

        stopRefresh();
        userDetails = UserDetails.create(res.getResultObject());

        fillUi();
        if(activity.getShikiUser().getId().equalsIgnoreCase(userDetails.id))
            activity.getShikiUser().setData(res.getResultObject());
    }

    void updateUserUI(){
        if(getUserId()!=null && getUserId().equalsIgnoreCase(ShikiUser.USER_ID)){
            if(activity instanceof UserDataChangeListener)
                ((UserDataChangeListener) activity).updateUserUI();
        }
    }

    private void fillUi() {
        if(userDetails == null || userDetails.id == null)
            return;

        if(userDetails.avatar!=null)
            ImageLoader.getInstance().displayImage(userDetails.avatar, avatar);
        tvUserName.setText(userDetails.nickname);

        setSexYearLocation();

        // когда был на сайте
        tvLastOnline.setText(userDetails.lastOnline);
        // web site
        setWebSite();
        // anime / manga progress
        setProgress();

        // profile data
        buildProfile();
    }

    private void buildProfile() {
        if(getUserId().equals(ShikiUser.USER_ID)){
            notifyController.load(new Query.OnQuerySuccessListener() {
                @Override
                public void onQuerySuccess(StatusResult res) {
                    updateUserUI();
                }
            });

        }
    }

    private void setProgress() {
        ProgressData progress;
        if(userDetails.fullStatuses!=null){
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
        if(!TextUtils.isEmpty(userDetails.website))
            h.setVisible(ivWebShow, true);
        else
            h.setVisibleGone(ivWebShow);
    }

    private void setSexYearLocation() {
        StringBuilder str = new StringBuilder();
        // пол
        if(!TextUtils.isEmpty(userDetails.sex)){
            switch (userDetails.sex){
                case "male": str.append(activity.getString(R.string.male)); break;
                case "female": str.append(activity.getString(R.string.female)); break;
            }
        }
        // сколько лет
        if(str.length() > 0 && !TextUtils.isEmpty(userDetails.fullYears)){
            str.append(" / ")
               .append(activity.getString(R.string.years))
               .append(" ")
               .append(userDetails.fullYears);
        }
        // где живем
        if(!TextUtils.isEmpty(userDetails.location))
            str.append("\n").append(userDetails.location);

        tvMiniDetails.setText(str.toString());
    }

    /**
     * Расчет прочитанного из аниме и манги
     * progress 1 = просмотренно
       progress 2 = запланированно + смотрю + просмотренно + отложено
       max size = запланированно + смотрю + просмотренно + отложено + брошено
     */
    private ProgressData getProgress(List<AnimeManga> list) {
        ProgressData progr = new ProgressData();
        for (AnimeManga animeManga : list) {
            progr.fullProgress += animeManga.counted;
            if(AnimeStatuses.COMPLETED.equals(animeManga.name))
                progr.firstProgress += animeManga.counted;
            if (!AnimeStatuses.DROPPED.equals(animeManga.name)
                    && !AnimeStatuses.REWATCHING.equals(animeManga.name))
                progr.secondProgress += animeManga.counted;
        }

        progr.percentage1 = progr.firstProgress * 100 / progr.fullProgress;
        progr.percentage2 = progr.secondProgress * 100 / progr.fullProgress;

        return progr;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivWebShow){
            h.launchUrlLink(activity, userDetails.website);
        } else if (v.getId() == R.id.ivAnimeListShow){
            pop = new ListPopup(activity);
            pop.setOnItemClickListener(animePopupListener);
            pop.setAdapter(new ProfileMangaAnnimeNameAdapter(activity,
                    userDetails.fullStatuses.animes, ProjectTool.TYPE.ANIME));
//            pop.setList(getListNames(userDetails.fullStatuses.animes, anime));
            pop.setTitle(R.string.lists_anime);
            pop.show();
        } else if (v.getId() == R.id.ivMangaListShow){
            pop = new ListPopup(activity);
            pop.setOnItemClickListener(mangaPopupListener);
            pop.setAdapter(new ProfileMangaAnnimeNameAdapter(activity,
                    userDetails.fullStatuses.manga, ProjectTool.TYPE.MANGA));
//            pop.setList(getListNames(userDetails.fullStatuses.manga, manga));
            pop.setTitle(R.string.lists_manga);
            pop.show();
        }
    }

    AdapterView.OnItemClickListener animePopupListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            AnimeManga item = userDetails.fullStatuses.animes.get(position);
            Bundle b = new Bundle();
            // TODO SET DATA
            b.putString(Constants.LIST_ID, item.id);
            b.putString(Constants.ACTION_BAR_TITLE, ProjectTool.getListStatusName(activity, item.name, ProjectTool.TYPE.ANIME));
            b.putString(Constants.USER_ID, getUserId());
            activity.loadPage(AnimeUserListFragment.newInstance(b));
        }
    };

    AdapterView.OnItemClickListener mangaPopupListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AnimeManga item = userDetails.fullStatuses.manga.get(position);
            Bundle b = new Bundle();
            // TODO SET DATA
            b.putString(Constants.LIST_ID, item.id);
            b.putString(Constants.ACTION_BAR_TITLE, ProjectTool.getListStatusName(activity, item.name, ProjectTool.TYPE.MANGA));
            b.putString(Constants.USER_ID, getUserId());
            activity.loadPage(AnimeUserListFragment.newInstance(b));
        }
    };

    /**
     * Получаем список аниме или манги статусов
     */
    List<String> getListNames(List<AnimeManga> array, ProjectTool.TYPE type){
        List<String> list = new ArrayList<>();
        if(array!=null){
            for (AnimeManga animeManga : array) {
                String name = ProjectTool.getListStatusName(activity, animeManga.name, type);
                if(name!=null)
                    list.add(name + " / " + animeManga.counted);
            }
        }
        return list;
    }

    String getTextStatus(int resigd, int count){
        return activity.getString(resigd) + " (" + count +")";
    }

    @Override
    public boolean onBackPressed() {
        if(pop!=null && pop.hide())
            return true;
        return false;
    }

    class ProgressData{
        int firstProgress, secondProgress, fullProgress, percentage1, percentage2;
    }




    String text = "<center><span style=\"font-size: 20px;\"><strong>Итак, дорогие любители моэ, сегодня я расскажу вам, как сделать свой моэ-шики:</strong></span><br><a href=\"http://s27.postimg.org/n6u767kj7/Untitled.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s27.postimg.org/n6u767kj7/Untitled.jpg\" class=\"\" width=\"200\"></a> <a href=\"http://s18.postimg.org/f9qvvqtfd/image.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s18.postimg.org/f9qvvqtfd/image.jpg\" class=\"\" width=\"200\"></a> <br><br><span style=\"font-size: 16px;\"><strong>Для этого нам понадобится картинка с моэ-моэ-няшей, желательно в темных тонах.<br>Let's go:</strong></span><br><br><ul><li><div class=\"b-spoiler unprocessed\"><label>Открываем Google</label><div class=\"content\"><div class=\"before\"></div><div class=\"inner\">Открываем Google<br><a href=\"http://s27.postimg.org/wxmxiftmb/image_2.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s27.postimg.org/wxmxiftmb/image_2.jpg\" class=\"\" width=\"200\"></a><br></div><div class=\"after\"></div></div></div><br></li><li><div class=\"b-spoiler unprocessed\"><label>Ищем нужную няшу</label><div class=\"content\"><div class=\"before\"></div><div class=\"inner\">Ищем нужную няшу<br><a href=\"http://s27.postimg.org/flr6aqpir/image_3.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s27.postimg.org/flr6aqpir/image_3.jpg\" class=\"\" width=\"200\"></a><br></div><div class=\"after\"></div></div></div><br></li><li><div class=\"b-spoiler unprocessed\"><label>Желательно в параметрах поиска указать коричневый цвет</label><div class=\"content\"><div class=\"before\"></div><div class=\"inner\">Желательно в параметрах поиска указать коричневый цвет<br><a href=\"http://s27.postimg.org/4aoimdinn/image_4.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s27.postimg.org/4aoimdinn/image_4.jpg\" class=\"\" width=\"200\"></a><br></div><div class=\"after\"></div></div></div><br></li><li><div class=\"b-spoiler unprocessed\"><label>Скаченную картинку желательно немного затемнить в редакторе</label><div class=\"content\"><div class=\"before\"></div><div class=\"inner\">Скаченную картинку желательно немного затемнить в редакторе<br><a href=\"http://s27.postimg.org/vu2t6h8z7/image.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s27.postimg.org/vu2t6h8z7/image.jpg\" class=\"\" width=\"200\"></a><br></div><div class=\"after\"></div></div></div><br></li><li><div class=\"b-spoiler unprocessed\"><label>Заливаем полученную няшу на хороший фото-хостинг</label><div class=\"content\"><div class=\"before\"></div><div class=\"inner\">Заливаем полученную няшу на хороший фото-хостинг<br><a href=\"http://s27.postimg.org/rv5lnnkc3/image_1.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s27.postimg.org/rv5lnnkc3/image_1.jpg\" class=\"\" width=\"200\"></a><br></div><div class=\"after\"></div></div></div><br></li><li><div class=\"b-spoiler unprocessed\"><label>Переходим к <a href=\"https://userstyles.org/styles/110342/transparent-shiki-theme\">по ссылке к теме</a> на Stylish. Вставляем ссылку и применяем тему (для этого установится расширение для Google Chrome). Не забываем выбирать, сворачивать длинные посты (выбор по-умолчанию, как задумано на самом сайте) или показывать полностью.</label><div class=\"content\"><div class=\"before\"></div><div class=\"inner\">Переходим к <a href=\"https://userstyles.org/styles/110342/transparent-shiki-theme\">по ссылке к теме</a> на Stylish. Вставляем ссылку и применяем тему (для этого установится расширение для Google Chrome). Не забываем выбирать, сворачивать длинные посты (выбор по-умолчанию, как задумано на самом сайте) или показывать полностью.<br><a href=\"http://s11.postimg.org/6wlim3p4j/Untitled.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s11.postimg.org/6wlim3p4j/Untitled.jpg\" class=\"\" width=\"200\"></a><br></div><div class=\"after\"></div></div></div><br></li><li><div class=\"b-spoiler unprocessed\"><label>Получаем MOE-MOE-KYUN!</label><div class=\"content\"><div class=\"before\"></div><div class=\"inner\">Получаем MOE-MOE-KYUN!<br><a href=\"http://s15.postimg.org/g7nycs56j/image.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s15.postimg.org/g7nycs56j/image.jpg\" class=\"\" width=\"200\"></a> <a href=\"http://s27.postimg.org/em0vf1acz/Untitled_1.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s27.postimg.org/em0vf1acz/Untitled_1.jpg\" class=\"\" width=\"200\"></a><br></div><div class=\"after\"></div></div></div><br></li><li><div class=\"b-spoiler unprocessed\"><label>Ну или как-то так</label><div class=\"content\"><div class=\"before\"></div><div class=\"inner\">Ну или как-то так<br><a href=\"http://s15.postimg.org/qka8yuypn/image.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s15.postimg.org/qka8yuypn/image.jpg\" class=\"\" width=\"200\"></a> <a href=\"http://s15.postimg.org/g7nycs56j/image.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s15.postimg.org/g7nycs56j/image.jpg\" class=\"\" width=\"200\"></a> <a href=\"http://s15.postimg.org/or7cajdiz/image.jpg\" rel=\"745539841\" class=\"b-image unprocessed\"><img src=\"http://s15.postimg.org/or7cajdiz/image.jpg\" class=\"\" width=\"200\"></a><br></div><div class=\"after\"></div></div></div><br></li></ul><br><span style=\"font-size: 16px;\"><strong>Таким образом вы покажете элите <em>полное отсутсвие вкуса</em>, но кого это волнует, когда на фоне стоит Богиня :3</strong></span><br>Может кому-то даже понравится.<br></center>";
    public void testHtml(){
        new BodyBuild(activity).parce(text, (ViewGroup) llBody);


    }


//    public class URLDrawable extends BitmapDrawable {
//        // the drawable that you need to set, you could set the initial drawing
//        // with the loading image if you need to
//        protected Drawable drawable;
//
//        @Override
//        public void draw(Canvas canvas) {
//            // override the draw to facilitate refresh function later
//            if(drawable != null) {
//                drawable.draw(canvas);
//            }
//        }
//    }
//
//    Html.ImageGetter imgGetter3 = new Html.ImageGetter() {
//
//        public Drawable getDrawable(String source) {
//            final URLDrawable urlDrawable = new URLDrawable();
//            if (source.contains("missing_logo")){
//                source = ShikiApi.HTTP_SERVER + "/assets/globals/missing_original.jpg";
//            }
//            if (!source.contains("http")){
//                source = ShikiApi.HTTP_SERVER + source;
//            }
//
//            ImageLoader.getInstance().loadImage(source, new ImageLoadingListener() {
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    Drawable d = new BitmapDrawable(getResources(),loadedImage);
//                    urlDrawable.drawable = d;
//                    urlDrawable.drawable.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//                    ViewGroup parent = (ViewGroup) tvAbout.getParent();
//                    parent.invalidate();
//                    tvAbout.append("");
//                    tvAbout.invalidate();
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//
//                }
//            });
//
//            return urlDrawable;
//        }
//    };

}
