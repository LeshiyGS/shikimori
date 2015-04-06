package org.shikimori.library.adapters.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.adapters.holder.BaseHolder;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Владимир on 06.04.2015.
 */
public class UserListBaseListAdapter<T, H extends BaseHolder> extends SimpleBaseAdapter<T, H>{

    private Class<H> classHolder;

    public UserListBaseListAdapter(Context context, List<T> list, @LayoutRes int layout) {
        super(context, list, layout);
        this.classHolder = (Class<H>) BaseHolder.class;
    }

    public UserListBaseListAdapter(Context context, List<T> list, @LayoutRes int layout, Class<H> classHolder) {
        super(context, list, layout);
        this.classHolder = classHolder;
    }

    @Override
    public void setListeners(H holder) {
        super.setListeners(holder);
        if(holder.ivUser!=null)
            holder.ivUser.setOnTouchListener(h.getImageHighlight);
    }

    @Override
    public void setValues(H holder, T item) {

    }

    @Override
    public H getViewHolder(View v) {
        try {
            H holder = classHolder.newInstance();
            holder.ivPoster = (ImageView) v.findViewById(R.id.ivPoster);
            holder.tvName = (TextView) v.findViewById(R.id.tvName);
            holder.tvRusName = (TextView) v.findViewById(R.id.tvRusName);
            holder.tvStatus = (TextView) v.findViewById(R.id.tvStatus);
            return holder;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
