package ru.altarix.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import ru.altarix.ui.tool.h;


/**
 * Created by Владимир on 26.06.2014.
 */
public class CustomTabView extends CustomViewBase {
    private View back;
    private View rlContainerBackAnimated;
    private int mSelectedImage;
    private boolean visible;
    private TextView tvText;

    public CustomTabView(Context context) {
        super(context);
        this.init(null);
    }

    public CustomTabView(Context context, int layout) {
        super(context);
        guLayout = layout;
        this.init(null);
    }

    public CustomTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initParams();
    }

    private void initParams() {
        ViewGroup.LayoutParams params = back.getLayoutParams();
        params.height = rlContainerBackAnimated.getHeight();
        back.setLayoutParams(params);
    }

    protected void init(AttributeSet attrs) {
        super.init(attrs,guLayout != 0 ? guLayout : R.layout.altarix_ui_custom_text_tab);

        if(attrs != null){
            typedArray = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.AltarixUiDesclareStyle, 0, 0);

            try {
                mSelectedImage = typedArray.getResourceId(R.styleable.AltarixUiDesclareStyle_uiSelectedImage, 0);
                typedArray.recycle();
            } finally {
            }
        }

        back = findViewById(R.id.backViewPressed);
        rlContainerBackAnimated = findViewById(R.id.rlContainerBackAnimated);

        if(back!=null && rlContainerBackAnimated!=null){
            if(mSelectedImage!=0)
                back.setBackgroundResource(mSelectedImage);
        }

        tvText = (TextView)this.findViewById(R.id.tvText);
        tvText.setText(getHint());

        h.setFont(mContext, this);
    }

    @Override
    public void setHint(String hint) {
        super.setHint(hint);
        if(tvText!=null && hint!=null)
            tvText.setText(hint);
    }

    public void setSelected(boolean selected){
        if(selected)
            fadeIn();
        else fadeOut();
    }

    public void setBackgroundColorResorce(int resorce){
        back.setBackgroundResource(resorce);
    }

    private void fadeOut() {
        if(!visible)
            return;
        visible = false;
        Animation out = AnimationUtils.loadAnimation(mContext,
                android.R.anim.fade_out);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!visible)
                    back.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        back.clearAnimation();
        back.startAnimation(out);
    }

    private void fadeIn() {
        if(visible)
            return;
        visible = true;
        Animation out = AnimationUtils.loadAnimation(mContext,
                android.R.anim.fade_in);
        back.setVisibility(VISIBLE);
        back.clearAnimation();
        back.startAnimation(out);
    }

}
