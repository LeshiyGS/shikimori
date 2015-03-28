/*
 * Copyright (C) 2014 Ribot Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.ribot.easyadapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Easier alternative to implementing your own {@link android.widget.BaseAdapter}. Extend this class as it's explained below and then use {@link android.widget.AdapterView}
 * to link it to your {@link android.widget.AdapterView} </p>
 * <ol>
 *     <li>Annotate the subclass using {@link uk.co.ribot.easyadapter.annotations.LayoutId}, i.e @LayoutId(R.id.item_layout). It will link the ItemViewHolder to the item's layout.</li>
 *     <li>Annotate the view fields with {@link uk.co.ribot.easyadapter.annotations.ViewId}, i.e @ViewId(R.id.textView1). It will look for the viewId's in the item's layout and will assign the views to the annotated fields of the subclass.</li>
 *     <li>Implement {@link #onSetValues(Object, PositionInfo)} and populate the views using the data in the item object</li>
 *     <li>Optionally, implement {@link #onSetListeners()} to add listeners to the views.</li>
 * </ol>
 */
public abstract class ItemViewHolder<T> extends ViewHolder {

    protected T mItem;
    private Object mListener;
    private Context context;

    /**
     * Constructs an item view holder with the item view
     *
     * @param view the parent view where the held views reside.
     */
    public ItemViewHolder(View view) {
        super(view);
    }

    /**
     * Constructs an item view holder with the item view and the item data
     *
     * @param view the parent view where the held views reside.
     * @param item the data item that is used to populate the held views.
     */
    public ItemViewHolder(View view, T item) {
        super(view);
        setItem(item);
    }

    /**
     * Gets the item data
     *
     * @return the data item that is used to populate the held views.
     */
    public T getItem() {
        return mItem;
    }

    /**
     * Sets the item data
     *
     * @param item the data item that is used to populate the held views.
     */
    public void setItem(T item) {
        mItem = item;
    }

    /**
     * Must implement this method to populate the views with the data in the item object.
     *
     * @param item         the data item that is used to populate the held views.
     * @param positionInfo information about the position of the item on the list.
     */
    public abstract void onSetValues(T item, PositionInfo positionInfo);

    /**
     * Implement this method to add listeners to the views. This method is only called once when
     * the Adapter is created. Note that at this point calling {@link #getItem()} will return null, however
     * you can still call {@link #getItem()} from inside your listener object implementation, e.g from inside onClick().
     */
    public void onSetListeners() {
    }

    /**
     * Gets the listener object that was passed into the Adapter through its constructor.
     *
     * @return a generic listener object that can be casted and used as a callback
     * or null if not listener was set into the Adapter.
     */
    @Nullable
    public Object getListener() {
        return mListener;
    }

    /**
     * Gets the listener object that was passed into the Adapter through its constructor and cast
     * it to a given type.
     *
     * @param type the type of the listener
     * @return the listener casted to the given type or null if not listener was set into the Adapter.
     */
    @Nullable
    public <P> P getListener(Class<P> type) {
        if (mListener != null) {
            return type.cast(mListener);
        }
        return null;
    }

    protected void setListener(Object listener) {
        mListener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext(){
        return context;
    }

}
