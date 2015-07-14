package org.shikimori.client;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.shikimori.client.activity.PreferencesShikiActivity;
import org.shikimori.client.adapters.DrawerAdapter;
import org.shikimori.client.fragments.AnimesShikiFragment;
import org.shikimori.client.fragments.CalendarShikiFragment;
import org.shikimori.client.fragments.MangasShikiFragment;
import org.shikimori.client.fragments.PreferenceShikiPragment;
import org.shikimori.client.fragments.TopicsShikiFragment;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.fragments.CalendarFragment;
import org.shikimori.library.fragments.CommunityClubsFragment;
import org.shikimori.library.fragments.CommunityUsersFragment;
import org.shikimori.library.fragments.DiscusionFragment;
import org.shikimori.library.fragments.ProfileShikiFragment;
import org.shikimori.library.fragments.base.PagerAdapterFragment;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.h;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Владимир on 16.04.2015.
 */
public class DrawerTool {
    private BaseActivity activity;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerAdapter mDrawerAdapter;
    private int launchId =-1;

    private Bundle b = new Bundle();

    public DrawerTool(BaseActivity context){
        this.activity = context;
        initDrawer();
    }

    /**
     * Левая менюшка
     */
    public void initDrawer() {
        mDrawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) activity.findViewById(R.id.left_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout,
                R.string.app_name, R.string.app_name) {
            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                loadPage();
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                h.hideKeyboard(activity, drawerView);
            }
        };

        h.postIfOutside(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerAdapter = new DrawerAdapter(activity);
        // Set the adapter for the list view
        mDrawerList.setAdapter(mDrawerAdapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(drawerClickItemListener);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        /**
         * Кнопка нажималась слева сверху
         */
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        setUserDrawerData();
    }

    /**
     * Listener click drawer
     */
    AdapterView.OnItemClickListener drawerClickItemListener = new AdapterView.OnItemClickListener() {


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int itemId = view.getId();
            if (itemId == mDrawerAdapter.getSelectedId()) {
                mDrawerLayout.closeDrawers();
                return;
            }

            chooseItem(itemId);
            mDrawerAdapter.setSelected(itemId, position);
        }
    };

    public void setUserDrawerData() {
        mDrawerAdapter.setUserData(activity.getShikiUser());
    }

    public void chooseItem(int id) {
        launchId = id;
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawers();
    }

    public void setSelected(int id){
        mDrawerAdapter.setSelected(id);
    }

    /**
     * Открытие корневой страницы. Бекстек очищается
     */
    public void loadPage(int pageId) {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawers();

        activity.setOnFragmentBackListener(null);
        Fragment frag = null;
        // Услуги (Сервисы)
        if (pageId == DrawerAdapter.DRAWER_MENU_CALENDAR_ID) {
            frag = CalendarShikiFragment.newInstance();
        } else if (pageId == DrawerAdapter.DRAWER_MENU_PROFILE_ID) {

            b.putString(Constants.TREAD_ID, activity.getShikiUser().getId());
            b.putBoolean(Constants.DISSCUSION_TYPE, true);

            String[] titles = new String[]{ activity.getString(R.string.info), activity.getString(R.string.lenta)};
            ArrayList<Fragment> pageList = new ArrayList<>();
            pageList.add(ProfileShikiFragment.newInstance());
            pageList.add(DiscusionFragment.newInstance(b));
            frag = PagerAdapterFragment.newInstance(
                    activity.getString(R.string.profile),
                    pageList,
                    titles
            );
            //frag = ProfileShikiFragment.newInstance();
        } else if (pageId == DrawerAdapter.DRAWER_MENU_ANIME_ID) {
            frag = AnimesShikiFragment.newInstance();
        } else if (pageId == DrawerAdapter.DRAWER_MENU_MANGA_ID) {
            frag = MangasShikiFragment.newInstance();
        } else if (pageId == DrawerAdapter.DRAWER_MENU_NEWS_ID) {
            frag = TopicsShikiFragment.newInstance();
        } else if (pageId == DrawerAdapter.DRAWER_MENU_SETTINGS_ID) {
            if (Build.VERSION.SDK_INT > 10) {
                activity.removeCurrentFragment();
                activity.getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, PreferenceShikiPragment.newInstance())
                        .commit();
            }else
                activity.startActivity(new Intent(activity, PreferencesShikiActivity.class));
            return;
        } else if (pageId == DrawerAdapter.DRAWER_MENU_COMUNITY_ID) {
            String[] titles = new String[]{ activity.getString(R.string.users), activity.getString(R.string.clubs)};
            ArrayList<Fragment> pageList = new ArrayList<>();
            pageList.add(CommunityUsersFragment.newInstance());
            pageList.add(CommunityClubsFragment.newInstance());
            frag = PagerAdapterFragment.newInstance(
                    activity.getString(R.string.community),
                    pageList,
                    titles
            );
        }

        activity.clearBackStack();
        activity.loadPage(frag);
    }

    private void loadPage() {
        if (launchId == -1) return;
        loadPage(launchId);
        launchId = -1;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    public boolean hideDrawer() {
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
            return true;
        }
        return false;
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }
}
