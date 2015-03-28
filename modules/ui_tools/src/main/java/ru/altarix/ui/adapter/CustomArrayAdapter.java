package ru.altarix.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import ru.altarix.ui.tool.h;

/**
 * Created by Владимир on 25.08.2014.
 */
public class CustomArrayAdapter<E> extends ArrayAdapter<E> {

    public CustomArrayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CustomArrayAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public CustomArrayAdapter(Context mContext, int item_spinner, E[] stringArray) {
        super(mContext, item_spinner, stringArray);
    }

    public CustomArrayAdapter(Context mContext, int item_spinner, int resourceTextId, E[] stringArray) {
        super(mContext, item_spinner, resourceTextId, stringArray);
    }

    public CustomArrayAdapter(Context mContext, int item_spinner, List list) {
        super(mContext, item_spinner, list);
    }

    public CustomArrayAdapter(Context mContext, int item_spinner, int resourceTextId, List list) {
        super(mContext, item_spinner, resourceTextId, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        h.setFont(getContext(), v);
        return v;
    }
}
