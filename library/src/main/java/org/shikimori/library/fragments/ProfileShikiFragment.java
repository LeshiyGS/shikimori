package org.shikimori.library.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.interfaces.UserDataChangeListener;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.UserDetails;
import org.shikimori.library.pull.PullableFragment;

/**
 * Created by Владимир on 30.03.2015.
 */
public class ProfileShikiFragment extends PullableFragment<BaseActivity> implements Query.OnQuerySuccessListener {

    public static final String USER_ID = "user_id";

    String userId;
    private ImageView avatar;
    private TextView tvUserName;
    private UserDetails userDetails;

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
    }
}
