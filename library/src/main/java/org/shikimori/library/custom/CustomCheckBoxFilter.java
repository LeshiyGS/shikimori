package org.shikimori.library.custom;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 14.07.2015.
 */
public class CustomCheckBoxFilter extends FrameLayout implements View.OnClickListener {

    ViewGroup llCheckBoxList;
    TextView tvLabel,tvCount,tvCountNegative;
    List<Box> list = new ArrayList<>();
    int titleCount, titleCountNegative;
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

    /**
     * Подготовка вьюшек
     */
    private void init() {
        View v = inflate(getContext(), R.layout.custom_group_checkbox, null);
        addView(v);

        llCheckBoxList = h.get(v, R.id.llCheckBoxList);
        tvLabel = h.get(v, R.id.tvLabel);
        tvCount = h.get(v, R.id.tvCount);
        tvCountNegative = h.get(v, R.id.tvCountNegative);
        View llLabel = h.get(v, R.id.llLabel);
        // click on title
        llLabel.setOnClickListener(this);
    }

    public void setTitle(int title){
        tvLabel.setText(title);
    }

    public void setList(List<Box> list){
        this.list = list;
        buildViews();
    }

    public void setArray(List<String> array){
        this.list.clear();
        for (String item : array) {
            this.list.add(new Box(item));
        }
        buildViews();
    }

    /**
     * Строим список
     */
    private void buildViews() {
        llCheckBoxList.removeAllViews();
        for (Box box : list) {
            View v = inflate(getContext(), R.layout.custom_checkbox_filter, null);
            TextView tvTitle = h.get(v, R.id.tvTitle);
            ImageSwitcher isImage = h.get(v, R.id.isImage);

            v.setOnClickListener(imageSwitch);
            isImage.setFactory(factory);
            setAnimation(isImage);
            tvTitle.setText(box.title);
            llCheckBoxList.addView(v);

            prepareCounts(box);
        }
        invalidateCount();
    }

    /**
     * Показываем цифирки выбранных элементов
     */
    void invalidateCount(){
        if(titleCount > 0)
            tvCount.setText(String.valueOf(titleCount));
        if(titleCountNegative > 0)
            tvCountNegative.setText(String.valueOf(titleCountNegative));

        h.setVisibleGone(titleCount == 0, tvCount);
        h.setVisibleGone(titleCountNegative == 0, tvCountNegative);
    }

    /**
     * Пре первой инициализации просто считаем значеня статусов
     * @param box
     */
    void prepareCounts(Box box){
        if(box.status == 1)
            titleCount++;
        else if (box.status == 2){
            titleCountNegative++;
        }
    }

    /**
     * При нажатии на итем выставляем в + или в - выбранных элементов
     * @param box
     */
    private void upCount(Box box) {
        if(box.status == 1)
            titleCount++;
        else if (box.status == 2){
            titleCount--;
            titleCountNegative++;
        } else {
            titleCountNegative--;
        }

        if(titleCount<0)
            titleCount = 0;
        if(titleCountNegative<0)
            titleCountNegative = 0;
    }

    /**
     *
     */
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

    /**
     * Анимация смены плюсика на минус и наоборот
     * @param isImage
     */
    void setAnimation(ImageSwitcher isImage){
        if(Build.VERSION.SDK_INT > 10){
            Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.checkbox_animation_in);
            Animation out = AnimationUtils.loadAnimation(getContext(), R.anim.checkbox_animation_out);
            isImage.setInAnimation(in);
            isImage.setOutAnimation(out);
        }
    }

    /**
     * Клик по элементу
     */
    View.OnClickListener imageSwitch = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            ImageSwitcher isImage = h.get(v, R.id.isImage);
            int position = llCheckBoxList.indexOfChild(v);
            Box box = list.get(position);

            box.status++;
            if(box.status > 2)
                box.status = 0;

            upCount(box);
            invalidateCount();
            isImage.setImageResource(images[box.status]);
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.llLabel){
            h.setVisibleGone(llCheckBoxList.getVisibility() == VISIBLE, llCheckBoxList);
        }
    }


    public static class Box{
        String title;
        String type;
        String value;
        // not selected-0,selected-1,minus-2
        int status;

        public Box(){}
        public Box(String title){
            this.title = title;
        }

        public Box(String title, String type, String value){
            this.title = title;
            this.type = type;
            this.value = value;
        }

    }
}
