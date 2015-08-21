package org.shikimori.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.shikimori.client.activity.DrawerActivity;
import org.shikimori.client.adapters.DrawerAdapter;
import org.shikimori.client.tool.PushHelperShiki;
import org.shikimori.library.fragments.InboxFragment;
import org.shikimori.library.fragments.UserNewsFragment;
import org.shikimori.library.tool.constpack.Constants;


/**
 * Created by Владимир on 20.06.2014.
 */
public class MainActivity extends DrawerActivity {

    private boolean drawerNoSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment anotherFrag = getExtraPage();
        if(anotherFrag !=null){
            drawerNoSelect =true;
            loadPage(anotherFrag);
            drawerTool.setSelected(DrawerAdapter.NON_SELECTED);
            return;
        }
        loadPage(DrawerAdapter.DRAWER_MENU_PROFILE_ID);
        drawerTool.setSelected(DrawerAdapter.DRAWER_MENU_PROFILE_ID);
        setDowbleBackPressetToExit(true);
    }

    public Fragment getExtraPage() {
        Bundle params = getIntent().getExtras();
        if(params != null){
            int idExtraPge = params.getInt(ShikiApplikation.OPEN_PAGE);
            if(idExtraPge == 0)
                return null;
            switch (idExtraPge){
                case ShikiApplikation.MESSAGES_ID: return InboxFragment.newInstance();
                case ShikiApplikation.NEWS_ID: return UserNewsFragment.newInstance(Constants.NEWS);
                case ShikiApplikation.NOTIFY_ID: return UserNewsFragment.newInstance(Constants.NOTIFYING);
            }
        }
        return null;
    }
}
