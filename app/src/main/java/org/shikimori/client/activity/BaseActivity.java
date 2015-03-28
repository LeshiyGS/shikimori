package org.shikimori.client.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import org.shikimori.client.R;
import org.shikimori.client.loaders.Queryable;
import org.shikimori.client.loaders.httpquery.Query;
import org.shikimori.client.tool.LoaderController;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by Владимир on 27.08.2014.
 */
public abstract class BaseActivity extends ActionBarActivity implements Queryable {

    private static boolean CLOSE_APLICATION;
    private boolean homeButtonPressed;

    protected Fragment frag;
    protected OnFragmentBackListener fragmentBackListener; //TODO чем не устраивает стандартный механизм фрагмента?
    protected OnFragmentHomeListener fragmentHomeListener;
    protected Query query;
    protected FragmentManager fManager;
    private SharedPreferences mSettings;
    private boolean dowbleBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        fManager = getSupportFragmentManager();

        query = new Query(this);
        query.setLoader(new LoaderController(this));
    }

    /*
        body xml
         */
    protected abstract int getLayoutId();
    /**
     * get shared preference
     */
    public abstract SharedPreferences getSettings();

    /**
     * Remove all fragments from stack
     */
    public void clearBackStack() {
        // Clear all back stack.
        int backStackCount = fManager.getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {

            // Get the back stack fragment id.
            int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();
            fManager.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    public void loadPage(int id){

    }
    public void loadPage(Fragment _frag){
        loadPage(_frag, true);
    }

    public void loadPage(final Fragment _frag, final boolean backstack){
        if(_frag == null)
            return;
        // clear fragment back pressed
        fragmentBackListener = null;

        new Handler().post(new Runnable() {
            public void run() {
                FragmentTransaction fr = fManager.beginTransaction();

                if(!backstack)
                    removeCurrentFragment();

                fr.addToBackStack(_frag.getClass().getSimpleName());
                fr.replace(R.id.content_frame, _frag)
                  .commitAllowingStateLoss();
            }
        });
    }
    /**
     * If next fragment is not be in stack
     * remove current fragment from stack
     */
    public void removeCurrentFragment(){
        FragmentTransaction fr = fManager.beginTransaction();
        fr.remove(getVisibleFragment());
        fr.commitAllowingStateLoss();
        fManager.popBackStack();
    }

    @Override
    public void onBackPressed() {
        /**
         * Check if we can back pressed from fragment
         */
        if(fragmentBackListener!=null && fragmentBackListener.onBackPressed())
            return;

        /** Если в стеке 2 фрагмента на момент нажатия назад то выходим из приложения */
        if (fManager.getBackStackEntryCount() < 2) {
			if(dowbleBack && !dowbleBackPressed())
				return;
            finish();
            return;
        }
        homeButtonPressed = false;
        super.onBackPressed();
    }

    public void setDowbleBackPressetToExit(boolean dowbleBack){
        this.dowbleBack = dowbleBack;
    }
	
	private boolean isFirstBack = true;
    public boolean dowbleBackPressed() {
        if (isFirstBack) {
            isFirstBack = false;
            Toast.makeText(this, R.string.one_more_back_to_exit, Toast.LENGTH_SHORT).show();
            new Thread("BackThread") {
                @Override
                public void run() {
                    try {
                        sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isFirstBack = true;
                }
            }.start();
        } else {
            isFirstBack = true;
            return true;
        }

        return false;
    }

    /**
     * Находим фрагмент который сейчас виден
     * @return
     */
    public Fragment getVisibleFragment(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(fragments == null)
            return null;
        for (int i = fragments.size()-1; i >= 0; i--){
            Fragment fragment = fragments.get(i);
            if(fragment != null && fragment.isVisible()){
                return fragment;
            }
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            if(fragmentHomeListener!=null && fragmentHomeListener.onHomePressed())
                return true;
            homeButtonPressed = true;
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Custom loader
     * @return
     */
    public LoaderController getLoaderController(){
        return query.getLoader();
    }

    /**
     * back listener
     */
    public interface OnFragmentBackListener{
        public boolean onBackPressed();
    }

    /**
     * home button listener
     */
    public interface OnFragmentHomeListener{
        public boolean onHomePressed();
    }

    public void setOnFragmentBackListener(OnFragmentBackListener fragmentBackListener){
        this.fragmentBackListener = fragmentBackListener;
    }
    public void setOnFragmentHomeListener(OnFragmentHomeListener fragmentHomeListener){
        this.fragmentHomeListener = fragmentHomeListener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult", "base responce ");
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(fragments == null)
            return;
        for (int i = fragments.size()-1; i >= 0; i--){
            Fragment fragment = fragments.get(i);
            if(fragment != null && !fragment.isDetached()){
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(CLOSE_APLICATION){
            if(isTaskRoot())
                CLOSE_APLICATION = false;
            finish();
        }
    }

    public void setHomeArrow(boolean arrow) {
        ActionBar actionBar = getSupportActionBar();
        if (arrow) {
            // actionBar.setLogo(R.drawable.nav_icon_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        } else {
            //  actionBar.setLogo(R.drawable.ic_launcher);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    public FragmentManager getFragmentManagerLocal(){
        return fManager;
    }

    /**
     * true if click on home button
     * check it in onBackPressed
     * @return
     */
    public boolean isHomeButtonPressed(){
        return homeButtonPressed;
    }

    /**
     * Close all activity in stack
     */
    public void exitApplication(){
        CLOSE_APLICATION = true;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }
}