package org.shikimori.client.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import org.shikimori.client.DrawerTool;
import org.shikimori.client.R;
import org.shikimori.client.adapters.DrawerAdapter;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.fragments.AnimeDeatailsFragment;
import org.shikimori.library.fragments.DiscusionFragment;
import org.shikimori.library.interfaces.UserDataChangeListener;

/**
 * Created by Владимир on 16.04.2015.
 */
public class DrawerShowPageActivity extends ShowPageActivity implements UserDataChangeListener {

    private DrawerTool drawerTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDrawer();
        drawerTool.setSelected(DrawerAdapter.NON_SELECTED);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void chosePage() {
        if(page == TOPIC_PAGE){
            addPageFragment(AnimeDeatailsFragment.newInstance(params), R.string.anime);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.discusion);
            showPages();
            return;
        }
        super.chosePage();
    }

    /**
     * Левая менюшка
     */
    public void initDrawer() {
        drawerTool = new DrawerTool(this);
        drawerTool.setUserDrawerData();
    }

    public void setUserDrawerData() {
        drawerTool.setUserDrawerData();
    }

    @Override
    public void loadPage(int id) {
        drawerTool.loadPage(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (drawerTool.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerTool.hideDrawer())
            return;

        super.onBackPressed();
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerTool.getDrawerToggle();
    }

    @Override
    public void setHomeArrow(boolean arrow) {
        getDrawerToggle().setDrawerIndicatorEnabled(!arrow);
    }


    @Override
    public void updateUserUI() {
        setUserDrawerData();
    }


}
