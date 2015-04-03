package org.shikimori.library.tool.popup;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Феофилактов on 02.04.2015.
 */
public class CheckBoxPopup extends BasePopup {

    private int[] viewId;
    private View.OnClickListener listener;
    private List<String> list;

    public CheckBoxPopup(Activity mContext) {
        super(mContext);
    }

    public void addInsideViewClick(View.OnClickListener listener, @IdRes int... viewIds){
        this.listener = listener;
        this.viewId = viewIds;
    }

    public void setList(List<String> list){
        this.list = list;
    }

    private void showList() {
        int padd = dpToPx(16);
        int padd2 = dpToPx(16);
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            TextView text = new TextView(mContext);
            text.setPadding(padd, padd2, padd, padd2);
            text.setText(s);
            text.setTag(i);
            text.setOnClickListener(click);
            addViewToBody(text);
        }
        showPopup();
    }

    @Override
    public void show() {
        if(list!=null)
            showList();
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if(lister!=null)
                lister.onItemClick(null,v,position,position);
            hide();
        }
    };
}
