package org.shikimori.library.tool;

import android.content.Context;
import android.view.View;

import org.shikimori.library.R;

/**
 * Created by Владимир on 07.04.2015.
 */
public class ProjectTool {
    public static String getStatus(Context context, boolean anons, boolean ongoing){
        if (!anons && !ongoing){
            return context.getString(R.string.incoming);
        }else if(anons){
            return context.getString(R.string.anons);
        }else if(ongoing){
            return context.getString(R.string.ongoing);
        }
        return "";
    }

    public static void setStatusColor(Context context, View v, boolean anons, boolean ongoing){
        if (!anons && !ongoing){
            v.setBackgroundColor(context.getResources().getColor(R.color.done));
        }else if(anons){
            v.setBackgroundColor(context.getResources().getColor(R.color.anons));
        }else if(ongoing){
            v.setBackgroundColor(context.getResources().getColor(R.color.ongoing));
        }
    }
}
