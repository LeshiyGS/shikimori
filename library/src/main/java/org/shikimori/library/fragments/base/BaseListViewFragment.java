package org.shikimori.library.fragments.base;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.paging.listview.PagingListView;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.custom.PaggingGridView2;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.h;

/**
 * Created by Владимир on 31.03.2015.
 */
public abstract class BaseListViewFragment extends BaseListFragment<BaseActivity> {
    private PagingListView lvList;
    private Parcelable state;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_list, null);
        lvList = (PagingListView) v.findViewById(R.id.lvList);
        lvList.setHasMoreItems(true);
        lvList.setOnItemClickListener(this);
        lvList.setPagingableListener(endListListener);
        lvList.setOnScrollListener(pauseImageLoading);
        return v;
    }

    /**
     * Достигаем последнего элемента, грузим новые данные
     */
    PagingListView.Pagingable endListListener = new PagingListView.Pagingable() {
        @Override
        public void onLoadMoreItems() {
            page++;
            loadData();
        }
    };

    /**
     * View id на которую привязываем pull to refresh
     * @return
     */
    @Override
    public int pullableViewId() {
        return R.id.lvList;
    }

    public void setAdapter(BaseAdapter adapter) {
        // save position
        if(page != DEFAULT_FIRST_PAGE)
            state = lvList.onSaveInstanceState();
        // set data
        lvList.setAdapter(adapter);
        // restore position
        if(state!=null)
            lvList.onRestoreInstanceState(state);
        state = null;
    }

    /**
     * Показываем лоадер если есть еще что подгружать
     * @param more
     */
    public void hasMoreItems(boolean more){
        try {
            lvList.onFinishLoading(more, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
