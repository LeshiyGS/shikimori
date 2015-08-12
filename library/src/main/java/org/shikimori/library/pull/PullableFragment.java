package org.shikimori.library.pull;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.abstracts.BaseFragment;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;

import ru.altarix.basekit.library.tools.WrapperUiTool;
import ru.altarix.ui.tool.h;

/**
 * Created by Феофилактов on 12.12.2014.
 */
public abstract class PullableFragment<T extends AppCompatActivity> extends BaseFragment<T> implements SwipeRefreshLayout.OnRefreshListener, Query.OnQueryErrorListener {

    private SwipeRefreshLayout swipeLayout;
    private int color;

    public abstract @IdRes int pullableViewId();

    public int getWrapperId(){
        return 0;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getWrapperId() == 0 && pullableViewId()!=0){
            View v = getView().findViewById(pullableViewId());

            swipeLayout = new SwipeRefreshLayout(v.getContext());
            swipeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            WrapperUiTool wraper = new WrapperUiTool(v, swipeLayout);

            wraper.insert();
            swipeLayout = (SwipeRefreshLayout) wraper.getView();
        } else if(getWrapperId() != 0){
            swipeLayout = (SwipeRefreshLayout) view.findViewById(getWrapperId());
        }

        if (swipeLayout != null)
            swipeLayout.setOnRefreshListener(this);

    }

    /**
     * Ставим цвет лоадера
     * @param color
     */
    public void setColorResourse(int color){
        this.color = color;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        color = h.getAttributeResourceId(activity, R.attr.colorAccent);

        if (swipeLayout != null && color!=0){
            swipeLayout.setColorSchemeResources(color);
        }

        getFC().getQuery().setSwipeLoader(swipeLayout);
    }

    public SwipeRefreshLayout getPullToRefreshLayout() {
        return swipeLayout;
    }

    /**
     * Если используеться больше одного listView внутри wrapper
     * то нужно обернуть списки в viewGroup, после ViewGroup
     * оборачивать врапером
     * В onActivityCreate вызвать этот метод для кождого списка
     * @param listView
     */
    public void initMoreThanOneListView(final AbsListView listView){

        if(listView == null)
            return;

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition =
                        (listView == null || listView.getChildCount() == 0) ?
                                0 : listView.getChildAt(0).getTop();
                swipeLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });
    }

    /*
    Use startRefresh()
     */
    @Override
    public void onRefresh() {
        getFC().getQuery().setErrorListener(this);
        onStartRefresh();
    }


    public abstract void onStartRefresh();

    /**
     * Manualy start refreshing
     */
    public void startRefresh(){
        setRefreshing(true);
        onRefresh();
    }

    /**
     * Hide loader
     */
    public void stopRefresh(){
        setRefreshing(false);
    }

    public void showRefreshLoader(){
        setRefreshing(true);
    }

    public void setRefreshing(final boolean refreshing){
        if(swipeLayout!=null){
            swipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(refreshing);
                }
            });
        }
    }

    /**
     * Move loader position from top
     * @param top
     */
    public void setOffsetTopLoader(int top){
        swipeLayout.setProgressViewOffset(false, top, top * 2);
    }


    @Override
    public void onQueryError(StatusResult res) {
        stopRefresh();
        getFC().getQuery().showStandartError(res);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopRefresh();
    }
}
