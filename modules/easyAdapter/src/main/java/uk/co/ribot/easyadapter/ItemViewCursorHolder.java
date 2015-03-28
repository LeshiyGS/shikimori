package uk.co.ribot.easyadapter;

import android.database.Cursor;
import android.view.View;

/**
 * Created by Феофилактов on 24.02.2015.
 */
public abstract class ItemViewCursorHolder extends ItemViewHolder<Cursor> {

    public ItemViewCursorHolder(View view) {
        super(view);
    }

    public String getStringValue(String column){
        return mItem.getString(mItem.getColumnIndex(column));
    }

    public int getIntValue(String column){
        return mItem.getInt(mItem.getColumnIndex(column));
    }

    public float getFloatValue(String column){
        return mItem.getFloat(mItem.getColumnIndex(column));
    }

    public byte[] getBlobValue(String column){
        return mItem.getBlob(mItem.getColumnIndex(column));
    }

    public long getLongValue(String column){
        return mItem.getLong(mItem.getColumnIndex(column));
    }
    public double getDoubleValue(String column){
        return mItem.getDouble(mItem.getColumnIndex(column));
    }

}
