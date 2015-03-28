package org.shikimori.client.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.shikimori.client.loaders.Queryable;
import org.shikimori.client.loaders.httpquery.Query;
import org.shikimori.client.tool.h;

/**
 * Created by Владимир on 02.07.2014.
 */
public class BaseFragment<T extends Activity> extends Fragment {

    /**
     * Root activity
     */
    protected T activity;
    /**
     * get data from server or cache
     */
    protected Query query;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.activity = (T) getActivity();
        query = ((Queryable)activity).prepareQuery(false);
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
}
