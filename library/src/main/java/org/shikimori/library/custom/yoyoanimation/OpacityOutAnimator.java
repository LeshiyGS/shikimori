package org.shikimori.library.custom.yoyoanimation;

import android.view.View;
import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

public class OpacityOutAnimator extends BaseViewAnimator {
    public OpacityOutAnimator() {
    }

    public void prepare(View target) {
        this.getAnimatorAgent().playTogether(new Animator[]{ObjectAnimator.ofFloat(target, "alpha", new float[]{1.0F, 0.4F})});
    }
}