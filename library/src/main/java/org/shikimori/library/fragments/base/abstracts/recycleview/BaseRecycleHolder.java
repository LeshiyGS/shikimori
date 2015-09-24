package org.shikimori.library.fragments.base.abstracts.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Владимир on 24.09.2015.
 */
public class BaseRecycleHolder extends RecyclerView.ViewHolder {
    public BaseRecycleHolder(View itemView) {
        super(itemView);
    }

    public <C extends View> C find(int id) {
        View childView = itemView.findViewById(id);
        return (C)childView;
    }
}
