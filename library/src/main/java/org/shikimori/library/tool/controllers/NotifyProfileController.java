package org.shikimori.library.tool.controllers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;
import org.shikimori.library.R;
import org.shikimori.library.custom.CustomProfileTextView;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.h;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class NotifyProfileController {
    public static final int NEWS = 0;
    public static final int MESSAGES = 1;
    public static final int NOTIFYING = 2;
    public static final int HISTORY = 3;
    public static final int FORUMS = 4;
    public static final int FAVORITE = 5;
    public static final int FRIENDS = 6;

    private final Context mContext;
    private Query query;
    private String userId;
    private final ViewGroup body;
    private List<Item> menu = new ArrayList<>();

    public NotifyProfileController(Context mContext, Query query, String userId, ViewGroup body){
        this.mContext = mContext;
        this.query = query;
        this.userId = userId;
        this.body = body;
        initList();
    }

    private void initList() {
        menu.add(new Item(NEWS, mContext.getString(R.string.news)));
        menu.add(new Item(MESSAGES, mContext.getString(R.string.messages)));
        menu.add(new Item(NOTIFYING, mContext.getString(R.string.notifying)));
        menu.add(new Item(HISTORY, mContext.getString(R.string.history)));
        menu.add(new Item(FORUMS, mContext.getString(R.string.forums)));
        menu.add(new Item(FAVORITE, mContext.getString(R.string.favorite)));
        menu.add(new Item(FRIENDS, mContext.getString(R.string.friends)));

        for (Item item : menu) {
            CustomProfileTextView row = new CustomProfileTextView(mContext);
            row.setText(item.name);
            row.setTag(item.id);
            row.setOnClickListener(loadGroup);
            body.addView(row);
        }
    }

    public void load(){
        query.init(ShikiApi.getUrl(ShikiPath.UNREAD_MESSAGES.replace(":id:", userId)))
             .setCache(true)
             .getResult(new Query.OnQuerySuccessListener() {
                 @Override
                 public void onQuerySuccess(StatusResult res) {
                     load(res.getResultObject());
                 }
             });
    }

    public void load(JSONObject dataFromServer){
        if(dataFromServer == null)
            return;
        int messages = dataFromServer.optInt("messages");
        int news = dataFromServer.optInt("news");
        int notifications = dataFromServer.optInt("notifications");
        for (int i = 0, size = body.getChildCount(); i < size; i++) {
            CustomProfileTextView row = (CustomProfileTextView) body.getChildAt(i);
            int id = (int) row.getTag();
            if(messages > 0 && id == MESSAGES)
                row.setCount(messages);
            else if(news > 0 && id == NEWS)
                row.setCount(news);
            else if(notifications > 0 && id == NOTIFYING)
                row.setCount(notifications);
        }
    }

    View.OnClickListener loadGroup = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = (int) v.getTag();
            h.showMsg(mContext, "click");
        }
    };

    private class Item{
        public String name;
        public int id, count;
        Item(int id, String name){
            this.id = id;
            this.name = name;
        }
    }
}
