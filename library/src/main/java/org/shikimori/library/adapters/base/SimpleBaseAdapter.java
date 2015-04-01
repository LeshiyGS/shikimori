package org.shikimori.library.adapters.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.List;

/**
 * Created by Владимир on 01.04.2015.
 */
public abstract class SimpleBaseAdapter<T,H> extends ArrayAdapter<T> {
    protected final LayoutInflater inflater;
    private int layout;

    public SimpleBaseAdapter(Context context, List<T> list, @LayoutRes int layout) {
        super(context, 0, list);
        this.layout = layout;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        H viewHolder;
        View v = convertView;

        if (convertView == null) {
            v = inflater.inflate(layout, parent, false);
            viewHolder = getViewHolder(v);
            setListeners(viewHolder);
            v.setTag(viewHolder);
        } else {
            viewHolder = (H) v.getTag();
        }

        T item = getItem(position);
        setValues(viewHolder, item);

        return v;
    }

    public abstract void setValues(H holder, T item);
    public abstract H getViewHolder(View v);
    public void setListeners(H holder){}
}
