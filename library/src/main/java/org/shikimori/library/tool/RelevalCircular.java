package org.shikimori.library.tool;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by Феофилактов on 15.01.2015.
 */
public class RelevalCircular {

    private static int ID = 0x12321;

    private Activity activity;
    private View circView;
    private ViewGroup rootView;
    int lastCentrX, lastCentrY;
    OnCircleEndAnimation callback;
    private int finalRadius;
    boolean animating;
    private int color;

    private Enum type = TYPE.GLOBAL;
    private View targetView;
    private int progress;
    private int customWidth;
    private int customHeight;

    public static enum TYPE {
        GLOBAL,
        VIEW
    }

    public RelevalCircular(Activity activity) {
        this.activity = activity;
    }

    public void setType(Enum type) {
        this.type = type;
    }

    public void setCustomRadius(int width, int height){
        customWidth = width;
        customHeight = height;
    }

    public void setColorBackground(int color) {
        this.color = color;
    }

    private boolean prepare() {
        if (animating)
            return true;
        animating = true;
        if (Build.VERSION.SDK_INT < 21) {
            if (callback != null)
                callback.animateEnd();
            animating = false;
            return true;
        }

        ViewGroup decorView = (ViewGroup) activity.findViewById(android.R.id.content);

        if(decorView!=null){
            rootView = (ViewGroup) decorView.getChildAt(0);
            if(rootView instanceof RelativeLayout == false && rootView instanceof FrameLayout == false){
                FrameLayout rootViewNew = new FrameLayout(activity);
                rootViewNew.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                decorView.removeView(rootView);
                rootViewNew.addView(rootView);
                decorView.addView(rootViewNew);
                rootView = rootViewNew;
            }
        } else {
            if (callback != null)
                callback.animateEnd();
            animating = false;
            return true;
        }

        return false;
    }

    public void startReleval(View v, OnCircleEndAnimation l) {
        targetView = v;

        progress = 20;

        callback = l;

        if(circView!=null)
            circView.setVisibility(View.VISIBLE);

        if (prepare())
            return;

        createView();

        initPositions();

        startAnimation(circView, lastCentrX, lastCentrY, 0, finalRadius, new AnimateListener(new AnimationEnd() {
            @Override
            public void end() {
                endAnimate();
                if (type == TYPE.GLOBAL)
                    closeReleval();
            }
        }));

    }

    public void startProgress(View v, OnCircleEndAnimation l) {
        targetView = v;

        progress = 20;

        if(circView!=null)
            circView.setVisibility(View.VISIBLE);

        callback = l;

        if (prepare())
            return;

        createView();

        initPositions();

        startAnimation(circView, lastCentrX, lastCentrY, 0, progress, new AnimateListener(new AnimationEnd() {
            @Override
            public void end() {

            }
        }));
    }

    public void setProgress(int progres) {
        if (progres >= 100) {
            if (callback != null)
                callback.animateEnd();
        }
        int size = finalRadius * progres / 100;
        if (size <= progress)
            size = progress;

        startAnimation(circView, lastCentrX, lastCentrY, progress, size, new AnimateListener(new AnimationEnd() {
            @Override
            public void end() {

            }
        }));

        progress = size;
    }

    private void initPositions() {

        if(customWidth > 0){
            finalRadius = Math.max(customWidth, customHeight);
            lastCentrX = customWidth / 2;
            lastCentrY = customHeight / 2;
            return;
        }

        int[] targetPosition = new int[2];
        targetView.getLocationOnScreen(targetPosition);
        // get the center for the clipping circle
        lastCentrX = (circView.getLeft() + circView.getRight()) / 2;
        lastCentrY = (circView.getTop() + circView.getBottom()) / 2;

        if (type == TYPE.GLOBAL) {
            // get the final radius for the clipping circle
            Point size = getWindowSize();
            finalRadius = Math.max(size.x, size.y);
        } else {
            finalRadius = Math.max(circView.getWidth(), circView.getHeight());
        }
    }

    public void closeReleval() {
        closeReleval(null);
    }

    public void closeReleval(final OnCircleEndAnimation l) {
        if (circView != null) {
            circView.setVisibility(View.VISIBLE);
            startAnimation(circView, lastCentrX, lastCentrY, finalRadius, 0, new AnimateListener(new AnimationEnd() {

                @Override
                public void end() {
                    circView.setVisibility(View.GONE);
                    if(l!=null)
                        l.animateEnd();
                }
            }));
        } else if(l!=null)
                l.animateEnd();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private Point getWindowSize() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private void createView() {
        if (type == TYPE.GLOBAL) {
//            circView = rootView.findViewById(R.id.viewGlobalReleval);
//        } else {
            circView = rootView.findViewById(ID);
            if (circView == null) {
                circView = new View(activity);
                circView.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                if (color != 0)
                    circView.setBackgroundColor(activity.getResources().getColor(color));
                else
                    circView.setBackgroundColor(Color.WHITE);
                circView.setId(ID);
                rootView.addView(circView);
            }
        } else if (type == TYPE.VIEW) {
            circView = targetView;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startAnimation(View v, int startX, int startY, float startRadius, float endRadius, AnimateListener listener) {
        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(v, startX, startY, startRadius, endRadius);
        anim.addListener(listener);
        anim.start();
    }

    private void endAnimate() {
        if (callback != null)
            callback.animateEnd();
        animating = false;
//        if(!global)
//            rootView.removeView(circView);
    }

    public interface OnCircleEndAnimation {
        public void animateEnd();
    }

    public void onPause() {
        closeReleval();
    }

    private interface AnimationEnd {
        public void end();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class AnimateListener implements Animator.AnimatorListener {
        private AnimationEnd animationEnd;

        public AnimateListener(AnimationEnd animationEnd) {
            this.animationEnd = animationEnd;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            animationEnd.end();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            animationEnd.end();
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

}