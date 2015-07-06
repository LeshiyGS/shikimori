package ru.altarix.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import ru.altarix.ui.R;

import java.util.ArrayList;
import java.util.List;

import ru.altarix.ui.tool.h;

/**
 * Created by Владимир on 26.06.2014.
 */
public class CustomGroupCheckBox extends CustomViewBase {
    private int mArray;
    private String mSelected;
    private ViewGroup llCheckBoxList;
    private boolean mMultiChose;
    List<CustomCheckBox> listCheckboxes = new ArrayList<>();
    private OnCustomCheckboxClickListener customListener;
    private int colorLine;
    private int defaultCheckBoxLayout;
    private int dividerHeight;

    public CustomGroupCheckBox(Context context) {
        this(context, null);
    }

    public CustomGroupCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public interface OnCustomCheckboxClickListener{
        public void click(CustomCheckBox v, int position);
    }

    protected void init(AttributeSet attrs) {
        super.init(attrs, R.layout.altarix_ui_custom_group_checkbox);
        try {
            mArray = typedArray.getResourceId(R.styleable.AltarixUiDesclareStyle_uiArray, 0);
            colorLine = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "divider", 0);
            dividerHeight = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "dividerHeight", 0);
            defaultCheckBoxLayout = typedArray.getResourceId(R.styleable.AltarixUiDesclareStyle_uiLayoutCheckBoxInGroup, 0);
            mSelected = typedArray.getString(R.styleable.AltarixUiDesclareStyle_uiSelectedPosition);
            mMultiChose = typedArray.getBoolean(R.styleable.AltarixUiDesclareStyle_uiMultiChose, false);
        } finally {
            typedArray.recycle();
        }
        if(colorLine == 0)
            colorLine = h.getAttributeResourceId(mContext, R.attr.altarixUiAttrDividerColor);

        if(dividerHeight == 0)
            dividerHeight = getContext().getResources().getDimensionPixelSize(R.dimen.devider_height);

        llCheckBoxList = (ViewGroup) findViewById(R.id.llCheckBoxList);

        // prepare to checked checkboxes
        // format 1,2,5,6
        String[] selected = new String[0];
        if(mSelected!=null)
            selected = mSelected.split(",");
        // build rows
        if (mArray > 0) {
            String[] data = getResources().getStringArray(mArray);
            // for multi selected
            boolean is_setted_box = false;

            for (int i = 0; i < data.length; i++){
                CustomCheckBox item = buildItem(data[i], null, i, data.length, 0);

                // if not multi and one is checked move to next
                if(!mMultiChose && is_setted_box)
                    continue;

                // select checkbox
                for (String _sel : selected)
                    if(i == Integer.valueOf(_sel)){
                        item.setChecked(true);
                        is_setted_box = true;
                    }
            }
        }
        h.setFont(mContext, this);
    }

    public void setColorLine(int color){
        colorLine = color;
    }

    CustomCheckBox buildItem(String text, String label, int i, int length, @IdRes int layout){
        CustomCheckBox item = layout>0? new CustomCheckBox(getContext(),layout): new CustomCheckBox(getContext());
        item.setTag(i); //FIXME плохое решение делать позиционирование через дефолтные теги. Надо хотя бы делать теги с ключом
        item.setOnClickListener(clickListener);
        if(Build.VERSION.SDK_INT <21)
            item.setClickable(true);
        item.setText(text);
        item.setLabel(label);
        listCheckboxes.add(item);
        llCheckBoxList.addView(item);
        if(i!= length-1){
            if(colorLine!=0){
                View line = new View(getContext());
                line.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight));
                line.setBackgroundColor(getContext().getResources().getColor(colorLine));
                llCheckBoxList.addView(line);
            }
        }
        return item;
    }

    /**
     * Set list of checkboxes
     * @param texts
     */
    public void setList(String[] texts){
        llCheckBoxList.removeAllViews();
        listCheckboxes.clear();
        for (int i = 0; i < texts.length; i++){
            buildItem(texts[i], null,i, texts.length, defaultCheckBoxLayout);
        }
    }

    public interface Labeled{
        String getLabel();
        String toString();
    }

    public void setList(Labeled... data){
        setList(defaultCheckBoxLayout, data);
    }

    /** передача кастомного лейаута. Labeled нужны для установки text и label*/
    public void setList(int guLayout, Labeled... data){
        llCheckBoxList.removeAllViews();
        listCheckboxes.clear();
        for (int i = 0; i < data.length; i++){
            buildItem(data[i].toString(), data[i].getLabel(), i, data.length, guLayout);
        }
    }

    public <T> void setList(List<? extends Labeled> data){
        setList(defaultCheckBoxLayout, data);
    }

    public <T> void setList(int guLayout, List<? extends Labeled> data){
        llCheckBoxList.removeAllViews();
        listCheckboxes.clear();
        for (int i = 0,length=data.size(); i < length; i++){
            Labeled item = data.get(i);
            buildItem(item.toString(), item.getLabel(), i, length, guLayout);
        }
    }

    public void setSelected(int position){
        if(!mMultiChose){
            for (CustomCheckBox item : listCheckboxes){
                if(item.isChecked())
                    item.setChecked(false);
            }
        }

        if(position == listCheckboxes.size())
            position = 0;

        listCheckboxes.get(position).setChecked(true);

    }

    public void setOnCustomCheckboxClickListener(OnCustomCheckboxClickListener l){
        customListener = l;
    }

    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

//            if(Build.VERSION.SDK_INT >=21){
//                v = (View) v.getParent();
//            }

            int position = (Integer) v.getTag();
            setSelected(position);
            if(customListener!=null)
                customListener.click((CustomCheckBox) v, position);
        }
    };

    /**
     * get list of positions selected checkboxes
     * @return
     */
    public List<Integer> getSelected(){
        ArrayList<Integer> _selected_list = new ArrayList<Integer>();
        int count = listCheckboxes.size();
        for(int i = 0; i < count; i++){
            if(listCheckboxes.get(i).isChecked())
                _selected_list.add(i);
        }
        return _selected_list;
    }

}
