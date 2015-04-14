package org.shikimori.library.tool.popup;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Феофилактов on 14.04.2015.
 */
public class TextPopup extends BasePopup {
    private final int padd;

    public TextPopup(Activity mContext) {
        super(mContext);
        padd = dpToPx(16);
        getBody().setPadding(padd,0,padd,padd);
    }

    @Override
    public void show() {
        showPopup();
    }

    public void show(String text){
        TextView tv = new TextView(mContext);
        tv.setPadding(padd, padd, padd, padd);
        tv.setText(text);
        clearBody();
        addViewToBody(tv);
        showPopup();
    }

    public void setBody(ViewGroup view) {
        clearBody();
        addViewToBody(view);
    }
}
