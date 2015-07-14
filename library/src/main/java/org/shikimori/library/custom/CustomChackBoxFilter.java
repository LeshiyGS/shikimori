package org.shikimori.library.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 14.07.2015.
 */
public class CustomChackBoxFilter extends FrameLayout {

    ViewGroup llCheckBoxList;
    TextView tvLabel;
    List<Box> list = new ArrayList<>();

    public CustomChackBoxFilter(Context context) {
        this(context, null);
    }

    public CustomChackBoxFilter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomChackBoxFilter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View v = inflate(getContext(), R.layout.altarix_ui_custom_group_checkbox, null);
        addView(v);

        llCheckBoxList = h.get(v, R.id.llCheckBoxList);
        tvLabel = h.get(v, R.id.tvLabel);
    }

    public void setTitle(int title){
        tvLabel.setText(title);
    }

    public void setList(List<Box> list){
        this.list = list;
        buildViews();
    }

    private void buildViews() {
        llCheckBoxList.removeAllViews();
        for (Box box : list) {

        }
    }


    public static class Box{
        String title, type;
        // 0,1,2
        int status;

    }
}
