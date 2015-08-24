package org.shikimori.library.fragments.base.abstracts;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.shikimori.library.R;
import org.shikimori.library.custom.PaggingGridView2;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;

import java.util.List;

import ru.altarix.basekit.library.activity.BaseKitActivity;

/**
 * Created by Владимир on 31.03.2015.
 */
public abstract class BaseGridViewFragment extends BaseListFragment<BaseKitActivity<ShikiAC>> {
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
            if(adapter == null || adapter.getCount() == 0)
                return;
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
        setAdapter(adapter, false);
    }

    @Override
    public void setAdapter(BaseAdapter adapter, boolean saveState) {
        // save position
        if (saveState || page != DEFAULT_FIRST_PAGE)
            state = gvList.onSaveInstanceState();
        // set data
        gvList.setAdapter(adapter);
        // restore position
        if (state != null)
            gvList.onRestoreInstanceState(state);
        state = null;
    }

    public PaggingGridView2 getGridView(){
        return gvList;
    }

    /**
     * Показываем лоадер если есть еще что подгружать
     *
     * @param more
     */
    @Override
    public void hasMoreItems(boolean more) {
        hs.setVisibleGone(!more, footerGridLoading);
        gvList.setHasMoreItems(more);
    }

    @Override
    protected void prepareData(List<?> list, boolean removeLastItem, boolean limitOver) {
        super.prepareData(list, removeLastItem, limitOver);
        if(page == DEFAULT_FIRST_PAGE)
            gvList.scrollTo(0,0);
    }
}
