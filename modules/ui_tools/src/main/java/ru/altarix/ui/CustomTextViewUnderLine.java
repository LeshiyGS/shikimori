package ru.altarix.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.View;

import ru.altarix.ui.tool.h;


/**
 * Created by Владимир on 26.06.2014.
 */
public class CustomTextViewUnderLine extends CustomTextView {

    public CustomTextViewUnderLine(Context context) {
        this(context, null);
    }

    public CustomTextViewUnderLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.altarix_ui_custom_text_view_underline;
    }

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);
        if(Build.VERSION.SDK_INT < 21){
            tvLabel.setPadding(0, 0, 0, 0);
            post(new Runnable() {
                @Override
                public void run() {
                    View llUnderLine = findViewById(R.id.llUnderLine);
                    final int colorAccent = h.getAttributeResourceId(getContext(), R.attr.colorAccent);
                    llUnderLine.setBackgroundDrawable(new SelectorDrawable(getContext(), colorAccent));
                }
            });
        }
    }

    public class SelectorDrawable extends StateListDrawable {

        private final Context c;
        private int colorAccent;
        int defColor = 0xFF666666;

        public SelectorDrawable(Context c, int colorAccent) {
            super();
            this.c = c;
            int padd = (int) c.getResources().getDimension(R.dimen.defaultPadding4);
            setPadding(padd, padd, padd, padd);
            defColor = c.getResources().getColor(h.getAttributeResourceId(c, R.attr.colorControlNormal));
            this.colorAccent = c.getResources().getColor(colorAccent);

            Drawable defaimg = c.getResources().getDrawable(R.drawable.altarix_ui_spiner_material_default);
            Drawable pressed = c.getResources().getDrawable(R.drawable.altarix_ui_spiner_material_pressed);
            addState(new int[]{android.R.attr.state_pressed}, pressed);
            addState(StateSet.WILD_CARD, defaimg);
        }

        @Override
        protected boolean onStateChange(int[] states) {
            boolean isClicked = false;
            for (int state : states) {
                if (state == android.R.attr.state_pressed) {
                    isClicked = true;
                }
            }

            if (isClicked)
                setColorFilter(colorAccent, PorterDuff.Mode.SRC_ATOP);
            else
                setColorFilter(defColor, PorterDuff.Mode.SRC_ATOP);

            return super.onStateChange(states);
        }
    }
}
