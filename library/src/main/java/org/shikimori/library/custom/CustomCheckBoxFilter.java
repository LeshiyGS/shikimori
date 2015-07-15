package org.shikimori.library.custom;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 14.07.2015.
 */
public class CustomCheckBoxFilter extends FrameLayout {

    ViewGroup llCheckBoxList;
    TextView tvLabel;
    List<Box> list = new ArrayList<>();
    int[] images = {
        0, R.drawable.ic_action_plus, R.drawable.ic_action_minus
    };

    public CustomCheckBoxFilter(Context context) {
        this(context, null);
    }

    public CustomCheckBoxFilter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCheckBoxFilter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View v = inflate(getContext(), R.layout.altarix_ui_custom_group_checkbox, null);
        addView(v);

        llCheckBoxList = h.get(v, R.id.llCheckBoxList);
        tvLabel = h.get(v, R.id.tvLabel);
    }

    public void setTitle(int title){
        tvLabel.setText(title);
    }

    public void setList(List<Box> list){
        this.list = list;
        buildViews();
    }

    private void buildViews() {
        llCheckBoxList.removeAllViews();
        for (Box box : list) {
            View v = inflate(getContext(), R.layout.custom_checkbox_filter, null);
            TextView tvTitle = h.get(v, R.id.tvTitle);
            ImageSwitcher isImage = h.get(v, R.id.tvTitle);
            isImage.setFactory(factory);
            setAnimation(isImage);
            tvTitle.setText(box.title);

            v.setOnClickListener(imageSwitch);
            addView(v);
        }
    }

    ViewSwitcher.ViewFactory factory = new ViewSwitcher.ViewFactory() {

        @Override
        public View makeView() {

            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            LayoutParams params = new ImageSwitcher.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            imageView.setLayoutParams(params);
            return imageView;

        }
    };

    void setAnimation(ImageSwitcher isImage){
        if(Build.VERSION.SDK_INT > 10){
            Animation in = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
            isImage.setInAnimation(in);
            isImage.setOutAnimation(in);
        }
    }

    View.OnClickListener imageSwitch = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            ImageSwitcher isImage = h.get(v, R.id.tvTitle);
            int positon = indexOfChild(v);
            Box box = list.get(positon);
            box.status++;
            if(box.status > 2)
                box.status = 0;

            isImage.setImageResource(images[box.status]);
        }
    };


    public static class Box{
        String title, type;
        // not selected-0,selected-1,minus-2
        int status;

    }
}
