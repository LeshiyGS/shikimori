package org.shikimori.library.tool.edittext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmpergar.awesometext.AwesomeTextHandler;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Владимир on 10.06.2015.
 */
public class PostSpanRenderer implements AwesomeTextHandler.ViewSpanRenderer {
    @Override
    public View getView(String text, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View hashtagView = inflater.inflate(R.layout.item_shiki_edittext_quote, null);
        TextView textView = (TextView) hashtagView.findViewById(R.id.tvTitle);
        textView.setText(getName(text));
        return hashtagView;
    }

    String getName(String text){
        Pattern p = Pattern.compile("\\[message=([0-9]+)\\](.+?)\\[");
        Matcher matcher = p.matcher(text);

        while (matcher.find()) {
            return matcher.group(2)+": "+matcher.group(1);
        }
        return text;
    }
}
