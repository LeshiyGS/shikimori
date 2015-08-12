package org.shikimori.library.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.tool.hs;

import ru.altarix.ui.CustomTextView;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class CustomPosterView extends CustomTextView {
    private TextView tvCount;

    public CustomPosterView(Context context) {
        this(context, R.layout.custom_poster_view);
    }

    public CustomPosterView(Context context, int layout) {
        super(context, layout);
    }

    public CustomPosterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        clickListener = l;
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);

        getIcon().setOnClickListener(this);
        getIcon().setOnTouchListener(hs.getImageHighlight);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivImage){
            if(clickListener!=null)
                clickListener.onClick(this);
            return;
        }
        super.onClick(v);
    }
}
