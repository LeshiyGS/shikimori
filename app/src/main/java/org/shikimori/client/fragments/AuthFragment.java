package org.shikimori.client.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.client.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.fragments.base.BaseFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.h;

import ru.altarix.ui.CustomEditText;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class AuthFragment extends BaseFragment<BaseActivity> implements View.OnClickListener {

    private CustomEditText cetLogin;
    private CustomEditText cetPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shiki_auth, null);
        cetLogin = (CustomEditText) v.findViewById(R.id.cetLogin);
        cetPassword = (CustomEditText) v.findViewById(R.id.cetPassword);
        View tvLoginButton = v.findViewById(R.id.tvLoginButton);
        tvLoginButton.setOnClickListener(this);


        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvLoginButton) {

            if (TextUtils.isEmpty(cetLogin.getText()) || TextUtils.isEmpty(cetPassword.getText())) {
                h.showMsg(activity, R.string.set_login_and_pass);
                return;
            }

            getAuthThoken();

        }
    }

    void getAuthThoken() {
        query.init(ShikiApi.getUrl(ShikiPath.GET_AUTH_THOKEN))
                .getResult(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {
                        auth(res.getParameter("authenticity_token"));
                    }
                });
    }

    void auth(String thoken) {
        query.init(ShikiApi.getUrl(ShikiPath.AUTH))
             .addParam("user[nickname]", cetLogin.getText())
             .addParam("user[password]", cetPassword.getText())
             .addParam("authenticity_token", thoken)
            .getResult(new Query.OnQuerySuccessListener() {
                @Override
                public void onQuerySuccess(StatusResult res) {
                    String cookie = res.getHeader("cookies");
                    // TODO доделать авторизацию
                }
            });
    }
}
