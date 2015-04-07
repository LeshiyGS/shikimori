package org.shikimori.library.fragments.base.abstracts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import org.shikimori.library.loaders.Queryable;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.h;

/**
 * Created by Владимир on 02.07.2014.
 */
public class BaseFragment<T extends ActionBarActivity> extends Fragment {

    /**
     * Root activity
     */
    protected T activity;
    /**
     * get data from server or cache
     */
    protected Query query;
    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public int getActionBarTitle(){
        return 0;
    }

    /**
     * Set action bar title
     * @return
     */
    public String getActionBarTitleString(){
        Bundle b = getArguments();
        if(b!=null)
            return b.getString(Constants.ACTION_BAR_TITLE);
        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        this.activity = (T) getActivity();
        query = ((Queryable)activity).prepareQuery(false);

        setActionBarTitle();
    }

    private void setActionBarTitle(){
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar!=null){
            String title;
            // set int title
            if(getActionBarTitle()!=0)
                actionBar.setTitle(getActionBarTitle());
            // set String title
            else if ((title = getActionBarTitleString())!=null)
                actionBar.setTitle(title);
        }
    }


    @Override
    public void onDetach() {
        if(query!=null)
            query.onStop();
        super.onDetach();
        activity = null;
    }

    @Override
    public void onDestroyView() {
        if(activity!=null)
            h.hideKeyboard(activity, activity.getWindow().getDecorView());
        super.onDestroyView();
    }

    public Query getQuery() {
        return query;
    }


    private void initData() {
        Bundle b = getArguments();
        if(b != null)
            userId = b.getString(Constants.USER_ID);

        if(userId == null)
            userId    = ShikiUser.USER_ID;
    }

    public String getUserId(){
        return userId;
    }

}
