package ru.altarix.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

/**
 * Created by Феофилактов on 27.10.2014.
 */
public class AnimateLabel implements TextWatcher {

    private final Context context;
    private final View label;
    private boolean mHideLabelIfEmpty;
    private EditText etText;
    int animatedLabel = 1;

    public AnimateLabel(Context context, View label, boolean mHideLabelIfEmpty, EditText etText){
        this.context = context;
        this.label = label;
        this.mHideLabelIfEmpty = mHideLabelIfEmpty;
        this.etText = etText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    public void setmHideLabelIfEmpty(boolean mHideLabelIfEmpty) {
        this.mHideLabelIfEmpty = mHideLabelIfEmpty;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(label == null)
            return;
        if(!mHideLabelIfEmpty){
            label.setVisibility(View.VISIBLE);
            return;
        } else {
            label.setVisibility(View.INVISIBLE);
        }

        String text = etText.getText().toString();

        if(text.length() == 0 && animatedLabel == 1)
            return;

        label.clearAnimation();
        label.setVisibility(text.length() == 0 ? View.INVISIBLE : View.VISIBLE);
        if(text.length() == 0 && animatedLabel != 1){
            animatedLabel = 1;
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.altarix_ui_fadein_to_bottom);
            label.startAnimation(animation);
        }else if (animatedLabel != 2){
            animatedLabel = 2;
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.altarix_ui_fadein_from_bottom);
            label.startAnimation(animation);
        }
    }
}
