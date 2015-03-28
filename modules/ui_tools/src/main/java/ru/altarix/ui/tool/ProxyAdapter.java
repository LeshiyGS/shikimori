package ru.altarix.ui.tool;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 19.12.2014.
 */
public class ProxyAdapter <E,T extends ListAdapter & Filterable> extends ArrayAdapter<Object>{

    private T adapter;
    private ArrayList<E> mOriginalValues;
    /**
     * Contains the list of objects that represent the data of this ArrayAdapter.
     * The content of this list is referred to as "the array" in the documentation.
     */
    private List<E> mObjects;
    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();
    private ArrayFilter mFilter;
    private ProxyFilter proxyFilter;

    public ProxyAdapter(Context context, T adapter){
        super(context, 0);
        this.adapter = adapter;
        mObjects = new ArrayList<E>();
        if(adapter == null)
            return;
        for (int i = 0; i < adapter .getCount(); i++)
            mObjects.add((E) adapter .getItem(i));
    }
    public void setAdapter(T adapter) {
        this.adapter = adapter;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = adapter.getView(position, convertView, parent);
        h.setFont(v.getContext(), v);
        return v;
    }
    @Override
    public boolean areAllItemsEnabled() {
        if(adapter == null) return false;
        return adapter.areAllItemsEnabled();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
        if(adapter != null)
            adapter.registerDataSetObserver(observer);
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);
        if(adapter != null)
            adapter.unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }
    @Override
    public E getItem(int position) {
        if(mObjects.size() <= position)
            position = mObjects.size() - 1;
        return mObjects.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        if(adapter == null) return false;
        return adapter.hasStableIds();
    }
    @Override
    public int getItemViewType(int position) {
        if(adapter == null) return 0;
        return adapter.getItemViewType(position);
    }
    @Override
    public int getViewTypeCount() {
        if(adapter == null) return 1;
        return adapter.getViewTypeCount();
    }
    @Override
    public boolean isEmpty() {
        if(adapter == null) return true;
        return adapter.isEmpty();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    /**
     * Ставим кастомный фильтр если сверяем объекты а не строки
     * @param proxyFilter
     */
    public void setProxyFilter(ProxyFilter proxyFilter){
        this.proxyFilter = proxyFilter;
    }

    public interface ProxyFilter<E>{
        public boolean filter(E item, String filter);
    }

    /**
     * <p>An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<E>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<E> list;
                synchronized (mLock) {
                    list = new ArrayList<E>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<E> values;
                synchronized (mLock) {
                    values = new ArrayList<E>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<E> newValues = new ArrayList<E>();

                for (int i = 0; i < count; i++) {
                    final E value = values.get(i);

                    if(proxyFilter!=null){
                        if(proxyFilter.filter(value, prefixString))
                            newValues.add(value);
                        continue;
                    }

                    final String valueText = value.toString().toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<E>) results.values;

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
