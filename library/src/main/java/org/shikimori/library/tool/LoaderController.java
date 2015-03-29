package org.shikimori.library.tool;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.shikimori.library.R;


/**
 * Created by Владимир on 25.06.2014.
 */
public class LoaderController {

    private ViewGroup root;
    private Activity activity;
    private View loader;
    private boolean isShown;
    private int layout;

    public LoaderController(Activity activity) {
        this.activity  = activity;
        ViewGroup decorView = (ViewGroup) activity.findViewById(android.R.id.content);
        if(decorView!=null){
            View firstChaild = decorView.getChildAt(0);

            if(firstChaild instanceof RelativeLayout == false && firstChaild instanceof FrameLayout == false){
                FrameLayout rootViewNew = new FrameLayout(activity);
                rootViewNew.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                decorView.removeView(firstChaild);
                rootViewNew.addView(firstChaild);
                decorView.addView(rootViewNew);
                root = rootViewNew;
            } else {
                root = (ViewGroup) firstChaild;
            }
        }
    }

    public LoaderController(Activity activity, ViewGroup root) {
        this.activity  = activity;
        this.root = root;
    }

    private void init() {
        loader = activity.getLayoutInflater().inflate(layout != 0 ? layout : R.layout.view_shiki_custom_loader, root, false);
        if(root!=null)
            root.addView(loader);
    }

    public View getLoader(){
        return loader;
    }

    public void show(){
        if(isShown)
            return;

        if(loader == null)
            init();

        isShown = true;
        if(loader.getVisibility()!=View.VISIBLE)
            loader.setVisibility(View.VISIBLE);
        loader.startAnimation(getAnimation(R.anim.ug_fadein));
    }

    public void hide(){
        if(!isShown || loader == null)
            return;

        isShown = false;
        Animation animation = getAnimation(R.anim.ug_fadeout);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(loader.getVisibility()!=View.GONE)
                    loader.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        loader.startAnimation(animation);
    }

    public boolean isShown() {
        return isShown;
    }

    private Animation getAnimation(int anim){
        if(loader.getAnimation()!=null)
            loader.clearAnimation();

        return AnimationUtils.loadAnimation(activity, anim);
    }

    public void setLoaderView(@LayoutRes int layout) {
        this.layout = layout;
    }
}
