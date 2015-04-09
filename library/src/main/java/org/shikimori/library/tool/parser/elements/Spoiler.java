package org.shikimori.library.tool.parser.elements;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.shikimori.library.R;

/**
 * Created by Владимир on 09.04.2015.
 */
public class Spoiler {
    private final LinearLayout spoiler;
    private final TextView spoilerName;
    private final TextView text;
    private Context context;

    public Spoiler(Context context){
        this.context = context;
        spoiler = new LinearLayout(context);
        spoilerName = new TextView(context);
        text = new TextView(context);
        initSpoiler();
    }

    private void initSpoiler() {
        int padding = (int) context.getResources().getDimension(R.dimen.defaultPaddingMini);
        spoilerName.setPadding(padding, padding, padding, padding);
        spoilerName.setTypeface(null, Typeface.BOLD);
        spoiler.setVisibility(View.GONE);
        //Обработчик нажатия на название спойлера
        spoilerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spoiler.getVisibility() == View.VISIBLE) {
                    spoiler.setVisibility(View.GONE);
                } else {
                    spoiler.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    public void setTitle(String title){
        spoilerName.setText(title);
    }
}
