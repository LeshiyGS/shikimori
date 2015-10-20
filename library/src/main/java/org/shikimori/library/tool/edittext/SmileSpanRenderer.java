package org.shikimori.library.tool.edittext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmpergar.awesometext.AwesomeTextHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.custom.emoji.EmojiView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Владимир on 10.06.2015.
 */
public class SmileSpanRenderer implements AwesomeTextHandler.ViewSpanRenderer {
    @Override
    public View getView(String text, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View hashtagView = inflater.inflate(R.layout.item_shiki_edittext_smile, null);
        ImageView ivSmile = (ImageView) hashtagView.findViewById(R.id.ivSmile);
        if (EmojiView.cash.containsKey(text))
            ImageLoader.getInstance().displayImage(EmojiView.cash.get(text), ivSmile);
        return hashtagView;
    }
}