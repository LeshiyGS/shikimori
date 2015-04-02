package org.shikimori.library.tool.popup;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by Феофилактов on 28.03.2015.
 */
public abstract class BasePopup {
    protected Activity mContext;
    private Crouton crouton;
    protected AdapterView.OnItemClickListener lister;

    public BasePopup (Activity mContext){
        this.mContext = mContext;
    }

    public abstract void show();

    public boolean hide(){
        if(crouton!=null){
            crouton.hide();
            crouton = null;
            return true;
        }
        return false;
    }


    public void setOnItemClickListener(AdapterView.OnItemClickListener lister){
        this.lister = lister;
    }

    protected void showPopup(View body) {
        hide();
        crouton = Crouton.make(mContext, body);
        Configuration croutonConfiguration = new Configuration.Builder()
                .setDuration(Configuration.DURATION_INFINITE).build();
        crouton.setConfiguration(croutonConfiguration);
        crouton.show();
    }

    protected LinearLayout getLinearView(){
        LinearLayout body = new LinearLayout(mContext);
        body.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        body.setOrientation(LinearLayout.VERTICAL);
        body.setBackgroundColor(Color.WHITE);
        return body;
    }

    int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                r.getDisplayMetrics());
    }

}
