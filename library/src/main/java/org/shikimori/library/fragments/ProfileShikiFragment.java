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
import org.shikimori.library.fragments.base.BaseFragment;

/**
 * Created by Владимир on 30.03.2015.
 */
public class ProfileShikiFragment extends BaseFragment<BaseActivity> {

    public static final String USER_ID = "user_id";

    String userId;
    String avatarUrl;
    String userName;
    private ImageView avatar;
    private TextView tvUserName;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        preFillUi();
    }

    private void initData() {
        Bundle b = getArguments();
        if(b != null)
            userId = b.getString(USER_ID);

        if(userId == null){
            userId    = activity.getShikiUser().getId();
            avatarUrl = activity.getShikiUser().getAvatar();
            userName  = activity.getShikiUser().getNickname();
        }
    }

    /**
     * Проверяем свой ли профиль или смотрим чужой
     */
    private void preFillUi() {
        if(userName == null){
            // TODO load from server user data
        } else {
            fillUi();
        }
    }

    private void fillUi() {
        if(avatarUrl!=null)
            ImageLoader.getInstance().displayImage(avatarUrl, avatar);
        tvUserName.setText(userName);
    }
}
