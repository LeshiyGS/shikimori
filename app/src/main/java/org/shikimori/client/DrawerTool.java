package org.shikimori.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.shikimori.client.activity.DrawerActivity;
import org.shikimori.client.activity.PreferencesShikiActivity;
import org.shikimori.client.adapters.DrawerAdapter;
import org.shikimori.client.fragments.AboutFragment;
import org.shikimori.client.fragments.AnimesShikiFragment;
import org.shikimori.client.fragments.CalendarShikiFragment;
import org.shikimori.client.fragments.MangasShikiFragment;
import org.shikimori.library.features.comminity.CommunityClubsFragment;
import org.shikimori.library.features.comminity.CommunityUsersFragment;
import org.shikimori.library.fragments.DiscusionFragment;
import org.shikimori.library.features.profile.ProfileShikiFragment;
import org.shikimori.library.features.topic.TopicsFragment;
import org.shikimori.library.fragments.base.PagerAdapterFragment;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.constpack.Constants;

import java.util.ArrayList;

import ru.altarix.basekit.library.tools.drawer.BaseDrawerAdapter;
import ru.altarix.basekit.library.tools.drawer.DrawerToolHelper;

/**
 * Created by Владимир on 16.04.2015.
 */
public class DrawerTool extends DrawerToolHelper<DrawerActivity>{
    DrawerAdapter mDrawerAdapter;
    public DrawerTool(DrawerActivity context){
        super(context);
        setUserDrawerData();
    }

    @Override
    public BaseDrawerAdapter getDrawerAdapter() {
        mDrawerAdapter = new DrawerAdapter(activity);
        return mDrawerAdapter;
    }

    public void setUserDrawerData() {
        mDrawerAdapter.setUserData(activity.getAC().getShikiUser());
    }

    /**
     * Открытие корневой страницы. Бекстек очищается
     */
    @Override
    public Fragment getFragment(int pageId) {
        if (pageId == DrawerAdapter.DRAWER_MENU_CALENDAR_ID) {
            return CalendarShikiFragment.newInstance();
        } else if (pageId == DrawerAdapter.DRAWER_MENU_PROFILE_ID) {
            Bundle b = new Bundle();
            b.putString(Constants.TREAD_ID, ShikiUser.USER_ID);
            b.putString(Constants.DISSCUSION_TYPE, Constants.TYPE_USER);

            String[] titles = new String[]{ activity.getString(R.string.info), activity.getString(R.string.lenta)};
            ArrayList<Fragment> pageList = new ArrayList<>();
            pageList.add(ProfileShikiFragment.newInstance());
            pageList.add(DiscusionFragment.newInstance(b));
            return PagerAdapterFragment.newInstance(
                    activity.getString(R.string.profile),
                    pageList,
                    titles
            );
            //frag = ProfileShikiFragment.newInstance();
        } else if (pageId == DrawerAdapter.DRAWER_MENU_ANIME_ID) {
            return AnimesShikiFragment.newInstance();
        } else if (pageId == DrawerAdapter.DRAWER_MENU_MANGA_ID) {
            return MangasShikiFragment.newInstance();
        } else if (pageId == DrawerAdapter.DRAWER_MENU_NEWS_ID) {
            return TopicsFragment.newInstance();
        } else if (pageId == DrawerAdapter.DRAWER_MENU_SETTINGS_ID) {
//            if (Build.VERSION.SDK_INT > 10) {
//                activity.removeCurrentFragment();
//                activity.getFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.content_frame, PreferenceShikiPragment.newInstance())
//                        .commit();
//            }else
                activity.startActivity(new Intent(activity, PreferencesShikiActivity.class));
            return null;
        } else if (pageId == DrawerAdapter.DRAWER_MENU_COMUNITY_ID) {
            String[] titles = new String[]{ activity.getString(R.string.users), activity.getString(R.string.clubs)};
            ArrayList<Fragment> pageList = new ArrayList<>();
            pageList.add(CommunityUsersFragment.newInstance());
            pageList.add(CommunityClubsFragment.newInstance());
            return PagerAdapterFragment.newInstance(
                    activity.getString(R.string.community),
                    pageList,
                    titles
            );
        } else if (pageId == DrawerAdapter.DRAWER_MENU_ABOUT_ID){
            return AboutFragment.newInstance();
        }

        return null;
    }
}
