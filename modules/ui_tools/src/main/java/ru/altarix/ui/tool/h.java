package ru.altarix.ui.tool;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Феофилактов on 05.12.2014.
 */
public class h {
    public static void setFont(final Context context, final View v) {
        setFont(context, v, FontCache.FONT.ROBOTO);
    }

    public static void setFont(final Context context, final View v, FontCache.FONT font) {

        Typeface typeFace = FontCache.get(font, context);

        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    setFont(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(typeFace);
            } else if (v instanceof EditText){
                ((EditText) v).setTypeface(typeFace);
            }
        } catch (Exception e) {
        }
    }



    public static void setVisible(View v, boolean visible) {
        if(v==null)
            return;
        if (visible) {
            if (v.getVisibility() != View.VISIBLE)
                v.setVisibility(View.VISIBLE);
        } else {
            if (v.getVisibility() != View.INVISIBLE)
                v.setVisibility(View.INVISIBLE);
        }
    }

    public static void setVisibleGone(View v) {
        if (v!= null && v.getVisibility() != View.GONE)
            v.setVisibility(View.GONE);
    }

    public static int getAttributeResourceId(Context context, int attr){
        try {
            TypedValue typedValue = new TypedValue();
            int[] resIdAttr = new int[] { attr };
            TypedArray a = context.obtainStyledAttributes(typedValue.data, resIdAttr);
            int resId = a.getResourceId(0, 0);
            a.recycle();
            return resId;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean match(String needle_regexp, String string) {
        Pattern p = Pattern.compile(needle_regexp);
        Matcher matcher = p.matcher(string);
        if (matcher.find())
            return true;
        return false;
    }

}
