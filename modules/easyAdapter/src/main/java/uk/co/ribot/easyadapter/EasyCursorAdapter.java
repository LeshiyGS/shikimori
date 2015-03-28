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
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension of {@link BaseEasyAdapter} that uses a List as data structure and provide methods to set a new list of items or add them to the existing List.
 * Don't worry about implementing your own Adapter, the EasyAdapter will <b>do the tedious work for you.</b>
 * You only have to implement an {@link ItemViewHolder} and pass it into the constructor of this class.
 */
public class EasyCursorAdapter extends BaseEasyCursorAdapter {

    /**
     * Constructs and EasyAdapter with a Context, an {@link ItemViewHolder} class, and list of items.
     *
     * @param context             a valid Context
     * @param itemViewHolderClass your {@link ItemViewHolder} implementation class
     */
    public EasyCursorAdapter(Context context, Class<? extends ItemViewHolder> itemViewHolderClass, Cursor cur) {
        super(context, cur, itemViewHolderClass);
    }

    /**
     * Constructs and EasyAdapter with a Context, an {@link ItemViewHolder} class, a list of items
     * and a generic listener.
     *
     * @param context             a valid Context
     * @param itemViewHolderClass your {@link uk.co.ribot.easyadapter.ItemViewHolder} implementation class
     * @param listener            a generic object that you can access from your {@link uk.co.ribot.easyadapter.ItemViewHolder} by calling
     *                            {@link uk.co.ribot.easyadapter.ItemViewHolder#getListener()}, This can be used to pass a listener to the view holder that then you
     *                            can cast and use as a callback.
     */
    public EasyCursorAdapter(Context context, Class<? extends ItemViewHolder> itemViewHolderClass, Cursor cur, Object listener) {
        super(context, cur, itemViewHolderClass, listener);
    }

    /**
     * Sets a new list of items into the Adapter
     *
     * @param cur new list of items
     */
    public void setItems(Cursor cur) {
        changeCursor(cur);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Cursor getItem(int position) {
        mCursor.moveToPosition(position);
        return mCursor;
    }
}
