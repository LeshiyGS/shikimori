package org.shikimori.library.tool.popup;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by Феофилактов on 28.03.2015.
 */
public abstract class BasePopup {
    protected Activity mContext;
    private Crouton crouton;
    protected AdapterView.OnItemClickListener lister;
    private ViewGroup body;
    private View view;
    private TextView tvTitle;
    private boolean hideOnClickItem = true;
    View backView;
    private View pbLoader;

    public BasePopup (Activity mContext){
        this.mContext = mContext;
        buildBodyView();
    }

    public abstract void show();

    /**
     * Title
     * @param title
     */
    public void setTitle(int title){
        setTitle(mContext.getString(title));
    }
    /**
     * Title
     * @param title
     */
    public void setTitle(String title){
        if(title == null && tvTitle.getVisibility()!=View.GONE)
            tvTitle.setVisibility(View.GONE);
        else if(tvTitle.getVisibility()!=View.VISIBLE)
            tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
    }

    /**
     * Click on item
     * @param lister
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener lister){
        this.lister = lister;
    }

    /**
     * Remove all view from body
     */
    public void clearBody(){
        body.removeAllViews();
    }

    /**
     * Add view
     * @param v
     */
    public void addViewToBody(View v){
        body.addView(v);
    }

    public ViewGroup getBody() {
        return body;
    }

    /**
     * Get position in body
     * @param v
     * @return
     */
    protected int getPositionView(View v){
        return body.indexOfChild(v);
    }

    /**
     * show popup from top
     */
    protected void showPopup() {
        hide();
        initBackPopup();
        h.setFont(mContext, view);
        crouton = Crouton.make(mContext, view);
        Configuration croutonConfiguration = new Configuration.Builder()
                .setDuration(Configuration.DURATION_INFINITE).build();
        crouton.setConfiguration(croutonConfiguration);
        crouton.show();
        showBackPopup();
    }

    private void showBackPopup() {
        YoYo.with(Techniques.FadeIn)
            .withListener(new AnimateBackListener(backView, true))
            .duration(600)
            .playOn(backView);
    }

    private void hideBackPopup(){
        if(backView.getVisibility()!=View.VISIBLE)
            backView.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeOut)
                .duration(200)
                .withListener(new AnimateBackListener(backView, false))
            .playOn(backView);
    }

    /**
     * Init popup view
     */
    private void buildBodyView(){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.popup_body, null);
        body = (ViewGroup) view.findViewById(R.id.llBody);
        tvTitle = (TextView)view.findViewById(R.id.title);
        pbLoader = view.findViewById(R.id.pbLoader);
        view.findViewById(R.id.ivClose).setOnClickListener(closeToClick);
    }

    int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                r.getDisplayMetrics());
    }

    public boolean hide(){
        return hide(hideOnClickItem);
    }

    /**
     * Close popup naw
     * @param immediately
     * @return
     */
    public boolean hide(boolean immediately){
        if(immediately){
            if(crouton!=null){
                crouton.hide();
                crouton = null;
                hideBackPopup();
                return true;
            }
        }
        return false;
    }

    public void showLoader(){
        if(pbLoader.getVisibility()!=View.VISIBLE){
            pbLoader.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.Bounce)
                .delay(300)
                .playOn(pbLoader);
        }
    }

    public void hideLoader(){
        if(pbLoader.getVisibility()!=View.GONE){
            pbLoader.setVisibility(View.GONE);
        }
    }

    /**
     * Hide popup if click outside
     */
    View.OnClickListener closeToClick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            hide(true);
        }
    };

    private void initBackPopup(){

        if(backView !=null)
            return;

        ViewGroup decorView = (ViewGroup) mContext.findViewById(android.R.id.content);
        if(decorView!=null){

            backView = decorView.findViewById(R.id.back_popup_view);
            if(backView!=null)
                return;

            View firstChaild = decorView.getChildAt(0);

            ViewGroup root;
            if(firstChaild instanceof RelativeLayout == false && firstChaild instanceof FrameLayout == false){
                FrameLayout rootViewNew = new FrameLayout(mContext);
                rootViewNew.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                decorView.removeView(firstChaild);
                rootViewNew.addView(firstChaild);
                decorView.addView(rootViewNew);
                root = rootViewNew;
            } else {
                root = (ViewGroup) firstChaild;
            }

            if(root!=null){

                backView = new View(mContext);
                backView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                backView.setBackgroundColor(mContext.getResources().getColor(R.color.black_owerlay_60));
                backView.setOnClickListener(closeToClick);

                root.addView(backView);
            }
        }
    }

    private static class AnimateBackListener implements Animator.AnimatorListener {

        private View view;
        private boolean visibility;

        public AnimateBackListener(View view, boolean visibility){
            this.view = view;
            this.visibility = visibility;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            if(visibility && view.getVisibility()!=View.VISIBLE)
                view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if(!visibility && view.getVisibility()!=View.GONE)
                view.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

}
