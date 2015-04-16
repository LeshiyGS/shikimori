package org.shikimori.library.tool.popup;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

import org.shikimori.library.R;
import org.shikimori.library.tool.FixPauseAnimate;
import org.shikimori.library.tool.baselisteners.BaseAnimationListener;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Феофилактов on 28.03.2015.
 */
public class ListPopup extends BasePopup {

    private List<String> selectList;
    private BaseAdapter adapter;
    private Techniques typeAnimate;

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
            playAnimate(v, i);
        }
        showPopup();
    }

    private void playAnimate(final View v, int position) {
        if(typeAnimate!=null){
            v.setVisibility(View.INVISIBLE);
            YoYo.AnimationComposer anim =
                    YoYo.with(typeAnimate).withListener(new BaseAnimationListener(){
                        @Override
                        public void onAnimationStart(Animator animation) {
                            v.setVisibility(View.VISIBLE);
                        }
                    });

            FixPauseAnimate.play(anim, v, 100 * position);
        }
    }

    public void setAnimate(Techniques typeAnimate){
        this.typeAnimate = typeAnimate;
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
            playAnimate(text, i);
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
