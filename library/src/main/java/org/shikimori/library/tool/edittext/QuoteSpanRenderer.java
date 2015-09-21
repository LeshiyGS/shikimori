package org.shikimori.library.tool.edittext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jmpergar.awesometext.AwesomeTextHandler;

import org.shikimori.library.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Владимир on 10.06.2015.
 */
public class QuoteSpanRenderer implements AwesomeTextHandler.ViewSpanRenderer {
    private static int LIMIT = 20;
    @Override
    public View getView(String text, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View hashtagView = inflater.inflate(R.layout.item_shiki_edittext_quote, null);
        TextView textView = (TextView) hashtagView.findViewById(R.id.tvTitle);
        textView.setText(getName(text));
        return hashtagView;
    }

    String getName(String text){
        Pattern p = Pattern.compile("\\[quote=(.+?);(.+?);(.+?)\\](.+?)\\[");
        Matcher matcher = p.matcher(text);

        while (matcher.find()) {
            String userName = matcher.group(3);
            String userText = matcher.group(4);
            String newText;
            if(userText.length() > (2 * LIMIT)){
                StringBuilder cutText = new StringBuilder(userText);
                cutText.delete(LIMIT, userText.length() - LIMIT);
                cutText.insert(LIMIT, "...");
                newText = cutText.toString();
            } else {
                newText = userText;
            }

            return userName+": "+newText;
        }
        return text;
    }
}
