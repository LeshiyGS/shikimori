package org.shikimori.library.tool.popup;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Феофилактов on 28.03.2015.
 */
public class ListPopup extends BasePopup {

    private List<String> selectList;
    private BaseAdapter adapter;
    private AdapterView.OnItemClickListener lister;

    public ListPopup(Activity mContext) {
        super(mContext);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener lister){
        this.lister = lister;
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

    }

    private void showList() {
        LinearLayout body = getLinearView();
        int padd = dpToPx(16);
        int padd2 = dpToPx(8);
        for (int i = 0; i < selectList.size(); i++) {
            String s = selectList.get(i);
            TextView text = new TextView(mContext);
            text.setPadding(padd, padd2, padd, padd2);
            text.setText(s);
            text.setTag(i);
            text.setOnClickListener(click);
            body.addView(text);
        }
        showPopup(body);
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
