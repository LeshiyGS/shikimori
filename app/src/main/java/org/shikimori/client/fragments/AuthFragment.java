package org.shikimori.client.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import org.shikimori.client.MainActivity;
import org.shikimori.client.R;
import org.shikimori.library.tool.controllers.AuthShikiController;
import org.shikimori.library.fragments.base.abstracts.BaseFragment;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import ru.altarix.basekit.library.activities.BaseKitActivity;
import ru.altarix.ui.CustomEditText;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class AuthFragment extends BaseFragment<BaseKitActivity<ShikiAC>> implements View.OnClickListener, QueryShiki.OnQuerySuccessListener<ShikiStatusResult>, TextView.OnEditorActionListener {

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
        cetPassword.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        cetPassword.getEditText().setOnEditorActionListener(this);
        View tvLoginButton = v.findViewById(R.id.tvLoginButton);
        tvLoginButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity.getSupportActionBar().setSubtitle(R.string.auth);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvLoginButton) {
            sendAuth();
        }
    }

    void sendAuth(){
        String login = cetLogin.getText();
        String pass  = cetPassword.getText();

        // show error if user not set all params
        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(pass)) {
            hs.showMsg(activity, R.string.set_login_and_pass);
            return;
        }

        // show loader
        activity.getAC().getLoaderController().show();
        // start auth
        AuthShikiController authController = new AuthShikiController(getFC().getQuery(), activity.getAC().getShikiUser());
        authController.shikiAuth(login, pass, this);
    }

    /**
     * Ответ от сервера
     * @param res
     */
    @Override
    public void onQuerySuccess(ShikiStatusResult res) {
        if(activity == null)
            return;
        activity.getAC().getLoaderController().hide();
        // success auth
        if(res.isSuccess() && activity.getAC().getShikiUser().getId()!=null){
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId==EditorInfo.IME_ACTION_DONE){
            sendAuth();
            return true;
        }
        return false;
    }
}
