package org.shikimori.library.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.interfaces.UserDataChangeListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.UserDetails;
import org.shikimori.library.pull.PullableFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Владимир on 30.03.2015.
 */
public class ProfileShikiFragment extends PullableFragment<BaseActivity> implements Query.OnQuerySuccessListener {

    public static final String USER_ID = "user_id";

    String userId;
    private ImageView avatar;
    private TextView tvUserName;
    private UserDetails userDetails;
    private TextView tvAbout;
    private TextView tvMiniDetails;

    public static ProfileShikiFragment newInstance() {
        return new ProfileShikiFragment();
    }

    public static ProfileShikiFragment newInstance(String userId) {
        Bundle b = new Bundle();
        b.putString(USER_ID, userId);

        ProfileShikiFragment frag = new ProfileShikiFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shiki_profile, null);
        avatar = (ImageView) v.findViewById(R.id.ava);
        tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        tvAbout = (TextView) v.findViewById(R.id.tvAbout);
        tvMiniDetails = (TextView) v.findViewById(R.id.tvMiniDetails);
        return v;
    }

    @Override
    public int pullableViewId() {
        return R.id.bodyScroll;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        showRefreshLoader();
        loadDataFromServer();
    }

    @Override
    public void onStartRefresh() {
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.GET_USER_DETAILS) + userId);
        loadDataFromServer();
    }

    private void initData() {
        Bundle b = getArguments();
        if(b != null)
            userId = b.getString(USER_ID);

        if(userId == null)
            userId    = activity.getShikiUser().getId();
    }

    void loadDataFromServer(){
        query.init(ShikiApi.getUrl(ShikiPath.GET_USER_DETAILS)+userId)
             .setCache(true, Query.HOUR)
             .getResult(this);

    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        userDetails = UserDetails.create(res.getResultObject());
        if(activity.getShikiUser().getId().equalsIgnoreCase(userDetails.id)){
            activity.getShikiUser().setData(res.getResultObject());
            if(activity instanceof UserDataChangeListener)
                ((UserDataChangeListener) activity).updateUserUI();

        }

        fillUi();

    }

    private void fillUi() {
        if(userDetails == null)
            return;

        if(userDetails.avatar!=null)
            ImageLoader.getInstance().displayImage(userDetails.avatar, avatar);
        tvUserName.setText(userDetails.nickname);

        StringBuffer buf = new StringBuffer();
        if(userDetails.commonInfo !=null)
            for (String info : userDetails.commonInfo) {
                if(buf.length() != 0)
                    buf.append(" / ");
                buf.append(Html.fromHtml(info));
            }

        tvMiniDetails.setText(buf.toString());

        if(userDetails.aboutHtml != null)
            tvAbout.setText(Html.fromHtml(userDetails.aboutHtml, imgGetter3, null));
    }

    public class URLDrawable extends BitmapDrawable {
        // the drawable that you need to set, you could set the initial drawing
        // with the loading image if you need to
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if(drawable != null) {
                drawable.draw(canvas);
            }
        }
    }

    Html.ImageGetter imgGetter3 = new Html.ImageGetter() {

        public Drawable getDrawable(String source) {
            final URLDrawable urlDrawable = new URLDrawable();
            if (source.contains("missing_logo")){
                source = ShikiApi.HTTP_SERVER + "/assets/globals/missing_original.jpg";
            }
            if (!source.contains("http")){
                source = ShikiApi.HTTP_SERVER + source;
            }

            ImageLoader.getInstance().loadImage(source, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Drawable d = new BitmapDrawable(getResources(),loadedImage);
                    urlDrawable.drawable = d;
                    urlDrawable.drawable.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    ViewGroup parent = (ViewGroup) tvAbout.getParent();
                    parent.invalidate();
                    tvAbout.append("");
                    tvAbout.invalidate();
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            return urlDrawable;
        }
    };

}
