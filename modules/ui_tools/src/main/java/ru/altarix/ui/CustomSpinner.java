package ru.altarix.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.List;

import ru.altarix.ui.adapter.CustomArrayAdapter;
import ru.altarix.ui.tool.h;


/**
 * Created by Владимир on 26.06.2014.
 */
public class CustomSpinner extends CustomViewBase {
    private int mPosition;
    private CustomSpinnerClickItem sSpinner;
    private int mArray;
    private View pbLoader;

    public CustomSpinner(Context context) {
        super(context);
        init(null);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        super.init(attrs, R.layout.altarix_ui_custom_spinner);
        if (isInEditMode()) return;

        if(typedArray !=null){
            mPosition = typedArray.getInt(R.styleable.AltarixUiDesclareStyle_uiDefaultPosition, 0);
            mArray = typedArray.getResourceId(R.styleable.AltarixUiDesclareStyle_uiArray, 0);
            typedArray.recycle();
        }
        sSpinner = (CustomSpinnerClickItem) this.findViewById(R.id.sSpinner);

        if (mArray > 0) {
            CustomArrayAdapter<String> spinnerAdapter = new CustomArrayAdapter<>(mContext, R.layout.altarix_ui_item_spinner, getResources().getStringArray(mArray));
            spinnerAdapter.setDropDownViewResource(R.layout.altarix_ui_item_spinner);
            sSpinner.setAdapter(spinnerAdapter);
            sSpinner.setSelection(mPosition);
        }
        h.setFont(mContext, this);

        // add loader
        ViewGroup body = (ViewGroup) findViewById(R.id.lbase);
        if(body!=null) {
            body.addView(LayoutInflater.from(getContext()).inflate(R.layout.ui_custom_loader, body, false));
            pbLoader = findViewById(R.id.pbLoader);
        }

    }

    public void showLoader(boolean show){
        h.setVisible(pbLoader, show);
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        sSpinner.setOnItemSelectedListener(listener);
    }

    public void setAdapter(SpinnerAdapter adapter) {
        sSpinner.setAdapter(adapter);
    }

    public <T> void setList(List<T> list){
        CustomArrayAdapter<T> spinnerAdapter = new CustomArrayAdapter<>(mContext, R.layout.altarix_ui_item_spinner, R.id.tvName, list);
        spinnerAdapter.setDropDownViewResource(R.layout.altarix_ui_item_spinner);
        sSpinner.setAdapter(spinnerAdapter);
        sSpinner.setSelection(mPosition);
    }

    @SafeVarargs
    public final <T> void setList(T... list){
        CustomArrayAdapter<T> spinnerAdapter = new CustomArrayAdapter<>(mContext, R.layout.altarix_ui_item_spinner, R.id.tvName, list);
        spinnerAdapter.setDropDownViewResource(R.layout.altarix_ui_item_spinner);
        sSpinner.setAdapter(spinnerAdapter);
        sSpinner.setSelection(mPosition);
    }

    public int getSelectedItemPosition() {
        return sSpinner.getSelectedItemPosition();
    }

    public Object getSelectedItem() {
        return sSpinner.getSelectedItem();
    }

    public Spinner getSpinner()
    {
        return sSpinner;
    }

    public void setSelection(int position) {
        this.sSpinner.setSelection(position);
    }
}
