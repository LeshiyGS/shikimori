package org.shikimori.library.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import org.shikimori.library.R;

import ru.altarix.ui.CustomTextView;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class CustomProfileTextView extends CustomTextView {
    private TextView tvCount;

    public CustomProfileTextView(Context context) {
        super(context, R.layout.custom_profile_text_view);
    }

    public CustomProfileTextView(Context context, int layout) {
        super(context, layout);
    }

    public CustomProfileTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);
        tvCount = (TextView)findViewById(R.id.tvCount);
    }

    public void setCount(int count){
        if(tvCount!=null){
            if(count>0){
                if (tvCount.getVisibility()!=VISIBLE)
                    tvCount.setVisibility(VISIBLE);
            } else {
                if (tvCount.getVisibility()!=GONE)
                    tvCount.setVisibility(GONE);
            }
            tvCount.setText(String.valueOf(count));
        }
    }
}
