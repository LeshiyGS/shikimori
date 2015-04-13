package org.shikimori.library.tool.parser.elements;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Владимир on 09.04.2015.
 */
public class Quote {
    private final ViewGroup quote;
    private ImageView ivUser;
    private LinearLayout quoteBody;
    private final TextView userName;
    private Context context;
    private String userId;

    public Quote(Context context, boolean simple){
        this.context = context;
        if(simple){
            quote = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_shiki_quote_text, null);
        } else {
            quote = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_shiki_quote_named, null);
            quoteBody = (LinearLayout) quote.findViewById(R.id.llQuoteBody);
            ivUser = (ImageView) quote.findViewById(R.id.ivUser);
        }
        userName = (TextView) quote.findViewById(R.id.tvTitle);
    }


    public ViewGroup getContent(){
        return quoteBody == null ? quote : quoteBody;
    }

    public ViewGroup getQuote() {
        return quote;
    }

    public TextView getTitle(){
        return userName;
    }

    public void setUserName(String title){
        if (userName!=null)
            userName.setText(title);
    }
    public void setText(String title){
        if (userName!=null)
            userName.setText(title);
    }

    public void setUserIdFromImage(String html) {
        Pattern p = Pattern.compile("\\/([0-9]+)\\.png");
        Matcher matcher = p.matcher(html);
        if (matcher.find())
            userId = matcher.group(1);
    }

    public void setUserImage(String src) {
        if(ivUser!=null){
            String image = src.replace("/x16/", "/x64/");
            ImageLoader.getInstance().displayImage(image, ivUser);
        }
    }
}
