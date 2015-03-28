package ru.altarix.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Spinner;

/**
 * Created by Владимир on 04.08.2014.
 */
public class CustomSpinnerClickItem extends Spinner{
    int prevPosition = -1;
    public CustomSpinnerClickItem(Context context) {
        super(context);
    }

    public CustomSpinnerClickItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinnerClickItem(Context context, AttributeSet attrs, int defStyle)
    { super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int position){
        super.setSelection(position);
        if (prevPosition == position) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            if(getOnItemSelectedListener()!=null)
                getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
        prevPosition = position;
    }
}
