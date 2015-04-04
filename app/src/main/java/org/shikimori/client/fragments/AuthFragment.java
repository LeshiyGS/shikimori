package org.shikimori.client.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.client.MainActivity;
import org.shikimori.client.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.tool.controllers.AuthShikiController;
import org.shikimori.library.fragments.base.abstracts.BaseFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.h;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import ru.altarix.ui.CustomEditText;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class AuthFragment extends BaseFragment<BaseActivity> implements View.OnClickListener, Query.OnQuerySuccessListener {

    private CustomEditText cetLogin;
    private CustomEditText cetPassword;

    public static AuthFragment newInstance() {
        return new AuthFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity.getSupportActionBar().setSubtitle(R.string.auth);

        if(ShikiApi.isDebug){
            cetLogin.setText("LeshiyGS");
            cetPassword.setText("339953");
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvLoginButton) {

            String login = cetLogin.getText();
            String pass  = cetPassword.getText();

            // show error if user not set all params
            if (TextUtils.isEmpty(login) || TextUtils.isEmpty(pass)) {
                h.showMsg(activity, R.string.set_login_and_pass);
                return;
            }

            // show loader
            activity.getLoaderController().show();
            // start auth
            AuthShikiController authController = new AuthShikiController(query, activity.getShikiUser());
            authController.shikiAuth(login, pass, this);
        }
    }

    /**
     * Ответ от сервера
     * @param res
     */
    @Override
    public void onQuerySuccess(StatusResult res) {
        activity.getLoaderController().hide();
        // success auth
        if(res.isSuccess() && activity.getShikiUser().getId()!=null){
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        // error auth
        } else if(res.isError() && !TextUtils.isEmpty(res.getMsg())){
            Crouton.cancelAllCroutons();
            Crouton.makeText(activity, res.getMsg(), Style.ALERT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_forgot_pass){

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.auth_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
