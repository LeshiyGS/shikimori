package ru.altarix.ui;

import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by Владимир on 07.10.2014.
 */
public class CustomClickableBase extends CustomViewBase implements View.OnClickListener {

    private int mSelectedImage;
    private View back;
    private View rlContainerBackAnimated;
    private CountDownTimer coldawn;

    public CustomClickableBase(Context context) {
        super(context);
    }

    public CustomClickableBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomClickableBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    protected void init(AttributeSet attrs, int resId) {
        super.init(attrs, resId);

        if(typedArray!=null){
            try {
                mSelectedImage = typedArray.getResourceId(R.styleable.AltarixUiDesclareStyle_uiSelectedImage, 0);
            } finally {
            }
        }

        back = findViewById(R.id.backViewPressed);
        rlContainerBackAnimated = findViewById(R.id.rlContainerBackAnimated);

        if(back!=null && rlContainerBackAnimated!=null){
            if(mSelectedImage!=0)
                back.setBackgroundResource(mSelectedImage);
        }

        if(back !=null)
            initParams();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initParams();
    }

    private void initParams() {
        if (back == null || rlContainerBackAnimated == null)
            return;
        ViewGroup.LayoutParams params = back.getLayoutParams();
        params.height = rlContainerBackAnimated.getHeight();
        back.setLayoutParams(params);
    }

    @Override
    public void onClick(final View v) {
        playAnim();
        if((back == null || v instanceof CustomCheckBox) && Build.VERSION.SDK_INT < 21){
            if(clickListener!=null)
                clickListener.onClick(v);
        } else {
            if(coldawn!=null)
                coldawn.cancel();
            // ставим секунду
            coldawn = new CountDownTimer(200, 200) {

                public void onTick(long millisUntilFinished) {
                    //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                // разрешаем делать запрос на сервер
                public void onFinish() {
                    if(clickListener!=null)
                        clickListener.onClick(Build.VERSION.SDK_INT < 21 ? v : CustomClickableBase.this);
                }
            }.start();
        }
    }

    private void playAnim() {
        if(back == null || Build.VERSION.SDK_INT >=21) return;
        Animation out = AnimationUtils.loadAnimation(mContext,
                android.R.anim.fade_out);
        back.clearAnimation();
        back.startAnimation(out);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if(Build.VERSION.SDK_INT >=21){
            getChildAt(0).setOnClickListener(this);
        } else {
            super.setOnClickListener(this);
        }
        clickListener = l;
    }
}