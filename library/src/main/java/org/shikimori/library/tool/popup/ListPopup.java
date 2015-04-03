package org.shikimori.library.tool.popup;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Феофилактов on 28.03.2015.
 */
public class ListPopup extends BasePopup {

    private List<String> selectList;
    private BaseAdapter adapter;
    public ListPopup(Activity mContext) {
        super(mContext);
    }

    public void setList(List<String> list){
        adapter = null;
        selectList = list;
    }

    public void setAdapter(BaseAdapter adapter){
        selectList = null;
        this.adapter = adapter;
    }

    @Override
    public void show() {
        if(selectList!=null)
            showList();
        else if (adapter!=null)
            showAdapter();
    }

    private void showAdapter() {
        for (int i = 0; i < adapter.getCount(); i++) {
            View v = adapter.getView(i, null, null);
            v.setOnClickListener(click);
            addViewToBody(v);
        }
        showPopup();
    }

    private void showList() {
        int padd = dpToPx(16);
        int padd2 = dpToPx(16);
        for (int i = 0; i < selectList.size(); i++) {
            String s = selectList.get(i);
            TextView text = new TextView(mContext);
            text.setPadding(padd, padd2, padd, padd2);
            text.setText(s);
            text.setTag(i);
            text.setOnClickListener(click);
            text.setBackgroundResource(h.getAttributeResourceId(mContext, R.attr.selectableItemBackground));
            addViewToBody(text);
        }
        showPopup();
    }

    View.OnClickListener click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int position = getPositionView(v);
            if(lister!=null)
                lister.onItemClick(null,v,position,position);
            hide();
        }
    };

}
