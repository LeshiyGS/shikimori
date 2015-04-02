package org.shikimori.library.fragments.base;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.custom.PaggingGridView2;
import org.shikimori.library.tool.h;

/**
 * Created by Владимир on 31.03.2015.
 */
public abstract class BaseGridViewFragment extends BaseListFragment<BaseActivity> {
    private PaggingGridView2 gvList;
    private Parcelable state;
    private View footerGridLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_grid, null);
        gvList = (PaggingGridView2) v.findViewById(R.id.gvList);
        gvList.setOnItemClickListener(this);
        gvList.setOnEndListListener(endGridListener);
        footerGridLoading = inflater.inflate(R.layout.loading_view, null);
        gvList.addFooterView(footerGridLoading);
        gvList.setOnScrollListener(pauseImageLoading);
        return v;
    }

    PaggingGridView2.OnEndListListener endGridListener = new PaggingGridView2.OnEndListListener() {
        @Override
        public void endReach() {
            page++;
            loadData();
        }
    };

    @Override
    public int pullableViewId() {
        return R.id.gvList;
    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        // save position
        if (page != DEFAULT_FIRST_PAGE)
            state = gvList.onSaveInstanceState();
        // set data
        gvList.setAdapter(adapter);
        // restore position
        if (state != null)
            gvList.onRestoreInstanceState(state);
        state = null;
    }

    /**
     * Показываем лоадер если есть еще что подгружать
     *
     * @param more
     */
    @Override
    public void hasMoreItems(boolean more) {
        if (!more)
            h.setVisibleGone(footerGridLoading);
        else
            h.setVisible(footerGridLoading, true);
        gvList.setHasMoreItems(more);
    }
}
