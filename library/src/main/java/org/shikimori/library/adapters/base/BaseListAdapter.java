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
public class BaseListAdapter<T, H extends BaseHolder> extends SimpleBaseAdapter<T, H>{

    private Class<H> classHolder;

    public BaseListAdapter(Context context, List<T> list, @LayoutRes int layout) {
        super(context, list, layout);
        this.classHolder = (Class<H>) BaseHolder.class;
    }

    public BaseListAdapter(Context context, List<T> list, @LayoutRes int layout, Class<H> classHolder) {
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
            holder.ivUser = (ImageView) v.findViewById(R.id.ivUser);
            holder.tvDate = (TextView) v.findViewById(R.id.tvCommentsDate);
            holder.tvText = (TextView) v.findViewById(R.id.tvCommentsText);
            holder.tvName = (TextView) v.findViewById(R.id.tvUserName);
            return holder;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
