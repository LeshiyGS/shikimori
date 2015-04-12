package org.shikimori.library.tool.parser.elements;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
    private final LinearLayout spoiler;
    private final LinearLayout spoilerBody;
    private final TextView spoilerName;
    private Context context;

    public Spoiler(Context context){
        this.context = context;
        spoiler = new LinearLayout(context);
        spoilerBody = new LinearLayout(context);
        spoilerName = new TextView(context);
        spoiler.addView(spoilerName);
        spoiler.addView(spoilerBody);
        initSpoiler();
    }

    public void setOpened(boolean opened){
        if(opened && spoiler.getVisibility() != View.VISIBLE)
            spoilerBody.setVisibility(View.VISIBLE);
        else if(!opened && spoiler.getVisibility() != View.GONE)
            spoilerBody.setVisibility(View.GONE);

    }

    private void initSpoiler() {
        int padding = (int) context.getResources().getDimension(R.dimen.defaultPaddingMini);
        spoilerName.setPadding(padding, padding, padding, padding);
        spoilerName.setTypeface(null, Typeface.BOLD);
        spoilerBody.setVisibility(View.GONE);
        spoiler.setOrientation(LinearLayout.VERTICAL);
        spoiler.setPadding(padding, padding, padding, padding);
        spoilerBody.setOrientation(LinearLayout.VERTICAL);
        spoilerBody.setPadding(padding, padding, padding, padding);
        spoilerBody.setBackgroundColor(Color.GRAY);
        //Обработчик нажатия на название спойлера
        spoilerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                h.showMsg(context, "click");

                if (spoilerBody.getVisibility() == View.VISIBLE) {
                    spoilerBody.setVisibility(View.GONE);
                } else {
                    spoilerBody.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    public ViewGroup getContent(){
        return spoilerBody;
    }

    public LinearLayout getSpoiler() {
        return spoiler;
    }

    public TextView getTitle(){
        return spoilerName;
    }

    public void setTitle(String title){
        spoilerName.setText(title);
    }
}
