package org.shikimori.library.fragments.base.abstracts.recycleview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.paging.listview.PagingListView;

import org.shikimori.library.R;
import org.shikimori.library.pull.PullableFragment;

import java.util.ArrayList;
import java.util.List;

import ru.altarix.basekit.library.activity.BaseKitActivity;

/**
 * Created by Владимир on 22.09.2015.
 */
public abstract class BaseRecycleViewFragment extends PullableFragment<BaseKitActivity> {
    protected LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private boolean hasMoreItems;
    private boolean isLoading;
    private ListRecycleAdapter adapter;
    List<Object> allList = new ArrayList<>();

    public static final int DEFAULT_FIRST_PAGE = 1;
    public static final int LIMIT = 20;
    protected int page = DEFAULT_FIRST_PAGE;

    public abstract void loadData();

    public int getLayoutId() {
        return R.layout.basekit_fragment_base_recycle_view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutId(), null);
        mRecyclerView = find(v, R.id.recycleView);
        return v;
    }

    @Override
    public int pullableViewId() {
        return 0;
    }

    @Override
    public int getWrapperId() {
        return R.id.swipeLayout;
    }

    public LinearLayoutManager getLayoutManager(){
        return new LinearLayoutManager(activity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = getLayoutManager();
        mRecyclerView.setLayoutManager(mLayoutManager);
        initLoading();
    }

    private void initLoading() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                int lastVisibleItem = visibleItemCount + pastVisiblesItems;
                if (!isLoading && hasMoreItems) {
//                    if(PagingListView.this.isHeaderLoader) {
//                        if(pastVisiblesItems != 0) {
//                            return;
//                        }
//                    } else
                    if (lastVisibleItem != totalItemCount) {
                        return;
                    }

                    page++;
                    isLoading = true;
                    loadData();
                }


//                if (loading) {
//                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                        loading = false;
//                        Log.v("...", "Last Item Wow !");
//                    }
//                }
            }
        });
    }

    public void hasMoreItems(boolean b) {
        isLoading = false;
        adapter.showLoader(b);
    }

    public abstract ListRecycleAdapter getAdapter(List list);

    @Override
    public void onStartRefresh() {
        page = DEFAULT_FIRST_PAGE;
    }

    protected void prepareData(List<?> list, boolean removeLastItem, boolean limitOver) {

        if (activity == null)
            return;

        if (page == DEFAULT_FIRST_PAGE)
            allList.clear();

        for (int i = 0; i < list.size(); i++)
            allList.add(list.get(i));

        if (adapter == null) {
            adapter = getAdapter(allList);
            setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        int size = list.size();
        // повышать на +1 или нет
        int limit = limitOver ? (LIMIT + 1) : LIMIT;
        // если предыдущее количество кратно limit+1
        // значит есть еще данные
        if (size != 0 && size % (limit) == 0) {
            hasMoreItems(true);
            // удаляем последний элемент
            if (removeLastItem)
                list.remove(size - 1);
        } else
            hasMoreItems(false);
    }

    protected void removeItem(int position) {
        if (allList != null && allList.size() > position) {
            allList.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }

    public void setAdapter(ListRecycleAdapter adapter){
        mRecyclerView.setAdapter(adapter);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}
