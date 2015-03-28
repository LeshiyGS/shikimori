package ru.altarix.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.altarix.ui.R;

import ru.altarix.ui.tool.h;


/**
 * Created by Владимир on 26.06.2014.
 */
public class CustomCheckBox extends CustomClickableBase {
    private TextView tvText;
    private boolean mChecked;
    private ImageView cbImage;
    private boolean isChecked;

    public CustomCheckBox(Context context) {
        super(context);
        init(null);
    }

    public CustomCheckBox(Context context, int layout) {
        super(context);
        guLayout = layout;
        init(null);
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        super.init(attrs, R.layout.altarix_ui_custom_checkbox_in_group);
        if(typedArray!=null){
            try {
                mChecked   = typedArray.getBoolean(R.styleable.AltarixUiDesclareStyle_uiChecked, false);
            } finally {
                typedArray.recycle();
            }
        }
//
        if(mText == null)
            mText = "";

        tvText = (TextView)this.findViewById(R.id.tvText);
        cbImage = (ImageView)this.findViewById(R.id.ivImage);

        tvText.setText(mText);

        setChecked(mChecked);

        h.setFont(mContext, this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public void setText(int text){
        setText(mContext.getString(text));
    }

    public void setText(String text){
        mText = text.replace("null", "");
        tvText.setText(mText);
    }

    public void setChecked(boolean checked){
        if(isChecked == checked)
            return;

        isChecked = checked;
        if(checked){
            cbImage.setVisibility(VISIBLE);
        } else
            cbImage.setVisibility(INVISIBLE);
    }

    public TextView getTextView(){
        return tvText;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void toggle(){
        setChecked(!isChecked);
    }
}