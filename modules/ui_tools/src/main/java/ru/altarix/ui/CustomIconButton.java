package ru.altarix.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.altarix.ui.tool.h;

/**
 * Created by Владимир on 01.07.2014.
 */
public class CustomIconButton extends LinearLayout implements View.OnClickListener {

    protected String mText;

    protected Context mContext;
    protected int guLayout;
    protected TypedArray typedArray;
    private int mTypeImage, mSelectedImage;
    private View ivImage;
    private OnClickListener clickListener;
    private Drawable icon;
    private View iconBack;
    private TextView tvText;

    public CustomIconButton(Context context) {
        super(context);
        init(null, R.layout.altarix_ui_custom_icon_button);
    }

    public CustomIconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, R.layout.altarix_ui_custom_icon_button);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomIconButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, R.layout.altarix_ui_custom_icon_button);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        clickListener = l;
        if (Build.VERSION.SDK_INT > 20) {
            getChildAt(0).setOnClickListener(this);
        } else
            super.setOnClickListener(this);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initParams();
    }

    public void initParams() {
        if (iconBack == null)
            return;
        ViewGroup.LayoutParams params = iconBack.getLayoutParams();
        params.height = ((View) iconBack.getParent()).getHeight();
        params.width = ((View) iconBack.getParent()).getWidth();
        iconBack.setLayoutParams(params);
    }


    /**
     * @param attrs
     * @param baseLayout may be override in xml
     */
    protected void init(AttributeSet attrs, @LayoutRes int baseLayout) {
        mContext = getContext();
        setOrientation(VERTICAL);
        if (attrs != null) {
            typedArray = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.AltarixUiDesclareStyle, 0, 0);

            try {
                int resText = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "text", 0);
                mText = resText == 0 ? null : getContext().getString(resText);
                mTypeImage = typedArray.getResourceId(R.styleable.AltarixUiDesclareStyle_uiIcon, 0);
                mSelectedImage = typedArray.getResourceId(R.styleable.AltarixUiDesclareStyle_uiSelectedImage, 0);
                guLayout = typedArray.getResourceId(R.styleable.AltarixUiDesclareStyle_uiLayout, 0);
            } finally {
            }
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (guLayout == 0)
            guLayout = baseLayout;

        if (guLayout == 0)
            return;


        View v = inflater.inflate(guLayout, this, false);
        ivImage = v.findViewById(R.id.ivImage);
        tvText = (TextView) v.findViewById(R.id.tvText);

        if (mText != null && tvText != null) {
            tvText.setText(mText);
            tvText.setVisibility(VISIBLE);
        }

        iconBack = v.findViewById(R.id.iconBack);
        if (iconBack != null && mSelectedImage != 0)
            iconBack.setBackgroundResource(mSelectedImage);

        addView(v);

        if (ivImage != null && mTypeImage != 0) {
            setImageResource(mTypeImage);
        }

        if (Build.VERSION.SDK_INT > 20)
            setElevation(3);

        //this.setOnClickListener(this);
    }


    public void setImageResource(int icon_resorce) {
        ((ImageView) ivImage).setImageResource(icon_resorce);
    }

    public void setImageDrawable(Drawable drawable) {
        ((ImageView) ivImage).setImageDrawable(drawable);
    }

    protected void invalidateLayout() {
        invalidate();
        requestLayout();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            this.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(this.getContext(), R.anim.abc_fade_in);
            this.startAnimation(animation);
        }
    }

    public void setText(String text) {
        tvText.setText(text);
        h.setVisible(tvText, true);
        h.setVisibleGone(ivImage);
    }

    public void setText(int text) {
        setText(mContext.getString(text));
    }

    private void playAnim() {
        if (iconBack == null)
            return;
        Animation out = AnimationUtils.loadAnimation(mContext,
                android.R.anim.fade_out);
        iconBack.clearAnimation();
        iconBack.startAnimation(out);
    }

    @Override
    public void onClick(View v) {
        playAnim();
        if (clickListener != null)
            clickListener.onClick(this);
    }
}
