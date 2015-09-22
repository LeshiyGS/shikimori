package org.shikimori.library.fragments.base.abstracts.recycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.shikimori.library.R;

import java.util.List;

/**
 * Created by Владимир on 22.09.2015.
 */
public abstract class ListRecycleAdapter<T, H extends RecyclerView.ViewHolder> extends HeaderRecyclerAdapter<T,H> {

    private final View loaderView;
    public ListRecycleAdapter(Context context, List<T> items, int layout) {
        super(context, items, layout);
        loaderView = inflater.inflate(R.layout.loading_view, null);
        addFooter(loaderView);
    }

    public void showLoader(boolean loader){
        loaderView.setVisibility(loader? View.VISIBLE : View.GONE);
    }
}
