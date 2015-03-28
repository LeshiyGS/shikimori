package ru.altarix.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import ru.altarix.ui.R;

import ru.altarix.ui.tool.h;


/**
 * Created by Владимир on 26.06.2014.
 */
public class CustomTextView extends CustomClickableBase {
    private TextView tvText;

    public CustomTextView(Context context) {
        this(context, null);
    }

    public CustomTextView(Context context, int layout) {
        super(context);
        guLayout = layout;
        init(null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        super.init(attrs, R.layout.altarix_ui_custom_text_view);
        if (isInEditMode()) return;

        if(typedArray!=null){
            try {
                typedArray.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//
        tvText = (TextView)this.findViewById(R.id.tvText);

        tvText.setText(mText);

        h.setFont(mContext, this);
    }

    public void setText(int text){
        setText(mContext.getString(text));
    }

    public void setText(String text){
        mText = text==null ? "" : text;
        tvText.setText(mText);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public TextView getTextView(){
        return tvText;
    }
}
