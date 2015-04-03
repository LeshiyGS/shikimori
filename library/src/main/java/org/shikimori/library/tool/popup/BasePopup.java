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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.shikimori.library.R;

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
        crouton = Crouton.make(mContext, view);
        Configuration croutonConfiguration = new Configuration.Builder()
                .setDuration(Configuration.DURATION_INFINITE).build();
        crouton.setConfiguration(croutonConfiguration);
        crouton.show();
    }

    /**
     * Init popup view
     */
    private void buildBodyView(){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.popup_body, null);
        body = (ViewGroup) view.findViewById(R.id.llBody);
        tvTitle = (TextView)view.findViewById(R.id.title);
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
                return true;
            }
        }
        return false;
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

}
