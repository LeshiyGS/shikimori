package org.shikimori.library.fragments.base.abstracts.recycleview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

import org.shikimori.library.fragments.base.abstracts.OnBaseListListener;

/**
 * Created by Владимир on 22.09.2015.
 */
public abstract class BaseRecycleViewGridFragment extends BaseRecycleViewFragment {

    public int getColumnCount(){
        return 2;
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        GridLayoutManager ml = new GridLayoutManager(activity, getColumnCount());
        ml.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(getAdapter().getItemViewType(position)){
                    case HeaderRecyclerAdapter.TYPE_HEADER:
                    case HeaderRecyclerAdapter.TYPE_FOOTER:
                        return 1;
                    case HeaderRecyclerAdapter.TYPE_ITEM:
                        return getColumnCount(); //number of columns of the grid
                    default:
                        return -1;
                }
            }
        });
        return ml;
    }
}
