package org.shikimori.client.activity;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.shikimori.client.R;
import org.shikimori.client.adapters.DrawerAdapter;
import org.shikimori.library.fragments.CalendarFragment;
import org.shikimori.library.fragments.ProfileShikiFragment;
import org.shikimori.library.interfaces.UserDataChangeListener;
import org.shikimori.library.tool.h;


/**
 * Created by Владимир on 20.06.2014.
 */
public class DrawerActivity extends ProjectActivity implements UserDataChangeListener {

    protected DrawerLayout mDrawerLayout;
    protected ListView mDrawerList;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected DrawerAdapter mDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDrawer();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     * Левая менюшка
     */
    public void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
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
                h.hideKeyboard(DrawerActivity.this, drawerView);
            }
        };

        h.postIfOutside(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerAdapter = new DrawerAdapter(this);
        // Set the adapter for the list view
        mDrawerList.setAdapter(mDrawerAdapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(drawerClickItemListener);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        /**
         * Кнопка нажималась слева сверху
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setUserDrawerData();
    }

    public void setUserDrawerData(){
        mDrawerAdapter.setUserData(getShikiUser());
    }

    /**
     * Listener click drawer
     */
    AdapterView.OnItemClickListener drawerClickItemListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == mDrawerAdapter.getSelectedPosition()) {
                mDrawerLayout.closeDrawers();
                return;
            }

            chooseItem(view.getId());
            mDrawerAdapter.setSelected(position);
        }
    };

    private int launchId = -1;

    public void chooseItem(int id) {
        launchId = id;
//        if (id == DrawerAdapter.DRAWER_MENU_MAP_ID)
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawers();
//            else
//                loadPage();
//        else
//            loadPage();
    }

    private void loadPage() {
        if (launchId == -1) return;
        loadPage(launchId);
        launchId = -1;
    }


    /**
     * Открытие корневой страницы. Бекстек очищается
     */
    public void loadPage(int pageId) {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawers();

        fragmentBackListener = null;
        frag = null;
        // Услуги (Сервисы)
        if (pageId == DrawerAdapter.DRAWER_MENU_CALENDAR_ID) {
            frag = CalendarFragment.newInstance();
        } else if (pageId == DrawerAdapter.DRAWER_MENU_PROFILE_ID) {
            frag = ProfileShikiFragment.newInstance();
        }

        clearBackStack();
        loadPage(frag);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (hideDrawer())
            return;

        super.onBackPressed();
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

    @Override
    public void setHomeArrow(boolean arrow) {
        mDrawerToggle.setDrawerIndicatorEnabled(!arrow);
    }


    @Override
    public void updateUserUI() {
        setUserDrawerData();
    }
}
