package org.shikimori.library.fragments.base.abstracts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

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
    /**
     * Нужно для поиска вьюшек в ней через метод find
     */
    private View baseView;
    private Bundle params;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        params = getArguments();
    }

    public int getActionBarTitle(){
        if(params!=null)
           return params.getInt(Constants.ACTION_BAR_TITLE);
        return 0;
    }

    /**
     * Set action bar title
     * @return
     */
    public String getActionBarTitleString(){
        if(params!=null)
            return params.getString(Constants.ACTION_BAR_TITLE);
        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(baseView!=null)
            baseView = getView();

        initData();

        this.activity = (T) getActivity();
        query = ((Queryable)activity).prepareQuery(false);

        setActionBarTitle();
    }

    private void setActionBarTitle(){
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar!=null){
            String title;
            int titleInt;
            // set int title
            if((titleInt = getActionBarTitle())!=0)
                actionBar.setTitle(titleInt);
            // set String title
            else if ((title = getActionBarTitleString())!=null)
                actionBar.setTitle(title);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onDestroyView() {
        if(activity!=null)
            h.hideKeyboard(activity, activity.getWindow().getDecorView());
        if(query!=null)
            query.onStop();
        super.onDestroyView();
    }

    public Query getQuery() {
        return query;
    }


    private void initData() {
        if(params != null)
            userId = params.getString(Constants.USER_ID);

        if(userId == null)
            userId = ShikiUser.USER_ID;
    }


    public String getUserId(){
        return userId;
    }

    protected void setBaseView(View v){
        baseView = v;
    }

    protected <C extends View> C find(View view, int id) {
        return h.get(view, id);
    }

    protected <C extends View> C find(int id) {
        return h.get(baseView, id);
    }

    protected <C extends Object> C getParam(String name){
        if(params!=null)
            return (C) params.get(name);
        return null;
    }

    protected boolean getParamBoolean(String name){
        if(params!=null)
            return params.getBoolean(name);
        return false;
    }
}
