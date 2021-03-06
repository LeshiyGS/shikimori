package org.shikimori.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import org.shikimori.client.activity.DrawerActivity;
import org.shikimori.client.adapters.DrawerAdapter;
import org.shikimori.client.gsm.QuickstartPreferences;
import org.shikimori.library.features.profile.InboxFragment2;
import org.shikimori.library.fragments.UserNewsFragment;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

import ru.altarix.ui.ExSlidingTabLayout;


/**
 * Created by Владимир on 20.06.2014.
 */
public class MainActivity extends DrawerActivity {

    private int idExtraPge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment anotherFrag = getExtraPage();
        if(anotherFrag !=null){
            loadPage(anotherFrag);
            drawerTool.setSelected(DrawerAdapter.NON_SELECTED);
            return;
        }
        loadPage(DrawerAdapter.DRAWER_MENU_PROFILE_ID);
        drawerTool.setSelected(DrawerAdapter.DRAWER_MENU_PROFILE_ID);
        setDowbleBackPressetToExit(true);

        QuickstartPreferences.sendGsmToken(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle params =intent.getExtras();
        if(params!=null)
            idExtraPge = params.getInt(ShikiApplikation.OPEN_PAGE);
    }

    public Fragment getExtraPage() {
        if(idExtraPge == 0){
            Bundle params = getIntent().getExtras();
            if(params != null)
                idExtraPge = params.getInt(ShikiApplikation.OPEN_PAGE);
        }


            if(idExtraPge == 0)
                return null;
            switch (idExtraPge){
                case ShikiApplikation.PRIVATE_ID: return InboxFragment2.newInstance();
                case ShikiApplikation.NEWS_ID: return UserNewsFragment.newInstance(Constants.NEWS);
                case ShikiApplikation.NOTIFICATION_ID: return UserNewsFragment.newInstance(Constants.NOTIFYING);
            }
//        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List fragments = this.getSupportFragmentManager().getFragments();
        if(fragments != null) {
            for(int i = fragments.size() - 1; i >= 0; --i) {
                Fragment fragment = (Fragment)fragments.get(i);
                if(fragment != null && !fragment.isDetached()) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

}
