package org.shikimori.library.tool.parser.elements;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

/**
 * Created by Владимир on 09.04.2015.
 */
public class Spoiler {
    private final ViewGroup spoiler;
    private final LinearLayout spoilerBody;
    private final TextView spoilerName;
    private Context context;

    public Spoiler(Context context){
        this.context = context;
        spoiler = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_shiki_spoiler, null);
        spoilerBody = (LinearLayout) spoiler.findViewById(R.id.llSpoilerBody);
        spoilerName = (TextView) spoiler.findViewById(R.id.tvTitle);
//        spoiler = new LinearLayout(context);
//        spoilerBody = new LinearLayout(context);
//        spoilerName = new TextView(context);
//        spoiler.addView(spoilerName);
//        spoiler.addView(spoilerBody);
        initSpoiler();
    }

    public void setOpened(boolean opened){
        if(opened && spoiler.getVisibility() != View.VISIBLE)
            spoilerBody.setVisibility(View.VISIBLE);
        else if(!opened && spoiler.getVisibility() != View.GONE)
            spoilerBody.setVisibility(View.GONE);

    }

    private void initSpoiler() {
//        int padding = (int) context.getResources().getDimension(R.dimen.defaultPaddingMini);
//        spoilerName.setPadding(padding, padding, padding, padding);
//        spoilerName.setTypeface(null, Typeface.BOLD);
//        spoilerBody.setVisibility(View.GONE);
//        spoiler.setOrientation(LinearLayout.VERTICAL);
//        spoiler.setPadding(padding, padding, padding, padding);
//        spoilerBody.setOrientation(LinearLayout.VERTICAL);
//        spoilerBody.setPadding(padding, padding, padding, padding);
//        spoilerBody.setBackgroundColor(Color.GRAY);
        //Обработчик нажатия на название спойлера
        spoilerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spoilerBody.getVisibility() == View.VISIBLE) {
                    spoilerBody.setVisibility(View.GONE);
                } else {
                    openSpoiler();
                }

            }
        });
    }

    /**
     * Растягиваем изображения по ширине при открытии спойлера
     */
    void openSpoiler(){
        spoilerBody.setVisibility(View.VISIBLE);
        int viewscount = spoilerBody.getChildCount();
        for (int i = 0; i < viewscount; i++) {
            // находим галлерею
            if(spoilerBody.getChildAt(i) instanceof GridLayout){
                GridLayout grid = (GridLayout) spoilerBody.getChildAt(i);
                int viewCount = grid.getChildCount();
                // не более 3 на строку
                int column = viewCount > 3 ? 3 : viewCount;
                for (int g = 0; g < viewCount; g++) {
                    View v = grid.getChildAt(g);
                    GridLayout.LayoutParams itemParams = (GridLayout.LayoutParams) v.getLayoutParams();
                    itemParams.width = (grid.getWidth()/column) - itemParams.rightMargin - itemParams.leftMargin;
                    v.setLayoutParams(itemParams);
                }
            }
        }
    }


    public ViewGroup getContent(){
        return spoilerBody;
    }

    public ViewGroup getSpoiler() {
        return spoiler;
    }

    public TextView getTitle(){
        return spoilerName;
    }

    public void setTitle(String title){
        spoilerName.setText(title);
    }
}
