package org.shikimori.library.tool.controllers;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.json.JSONObject;
import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.NotifyProfileAdapter;
import org.shikimori.library.custom.ExpandableHeightGridView;
import org.shikimori.library.fragments.UserHistoryFragment;
import org.shikimori.library.fragments.UserNewsFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.Notification;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.constpack.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class NotifyProfileController implements AdapterView.OnItemClickListener {
    public static final int NEWS = 0;
    public static final int INBOX = 1;
    public static final int NOTIFYING = 2;
    public static final int HISTORY = 3;
    public static final int FORUMS = 4;
    public static final int FAVORITE = 5;
    public static final int FRIENDS = 6;

    private final BaseActivity mContext;
    private Query query;
    private ShikiUser user;
    private final ViewGroup body;
    private List<Item> menu = new ArrayList<>();
    private NotifyProfileAdapter notifyAdapter;

    public NotifyProfileController(BaseActivity mContext, Query query, ShikiUser user, ViewGroup body) {
        this.mContext = mContext;
        this.query = query;
        this.user = user;
        this.body = body;
        initList();
    }

    private void initList() {
        menu.add(new Item(NEWS, mContext.getString(R.string.news)));
        menu.add(new Item(INBOX, mContext.getString(R.string.messages)));
        menu.add(new Item(NOTIFYING, mContext.getString(R.string.notifying)));
        menu.add(new Item(HISTORY, mContext.getString(R.string.history)));
        menu.add(new Item(FORUMS, mContext.getString(R.string.forums)));
        menu.add(new Item(FAVORITE, mContext.getString(R.string.favorite)));
        menu.add(new Item(FRIENDS, mContext.getString(R.string.friends)));

        notifyAdapter = new NotifyProfileAdapter(mContext, menu);
        ((ExpandableHeightGridView) body).setAdapter(notifyAdapter);
        ((ExpandableHeightGridView) body).setOnItemClickListener(this);

    }

    public void load(final Query.OnQuerySuccessListener listener) {
        query.init(ShikiApi.getUrl(ShikiPath.UNREAD_MESSAGES, ShikiUser.USER_ID))
                .setCache(true, Query.FIVE_MIN)
                .getResult(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {
                        load(res.getResultObject());
                        listener.onQuerySuccess(res);
                    }
                });
    }

    public void load(JSONObject dataFromServer) {
        if (dataFromServer == null)
            return;
        Notification notify = new Notification(dataFromServer);
        user.setNotification(notify);

        for (Item item : menu) {
            if (item.id == INBOX)
                item.count = notify.messages;
            else if (item.id == NEWS)
                item.count = notify.news;
            else if (item.id == NOTIFYING)
                item.count = notify.notifications;
        }
        notifyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = (Item) parent.getAdapter().getItem(position);
        if (item.id == NEWS) {
            mContext.loadPage(UserNewsFragment.newInstance(Constants.NEWS));
        } else if (item.id == NOTIFYING)
            mContext.loadPage(UserNewsFragment.newInstance(Constants.NOTIFYING));
        else if (item.id == INBOX)
            mContext.loadPage(UserNewsFragment.newInstance(Constants.INBOX));
        else if (id == HISTORY)
            mContext.loadPage(UserHistoryFragment.newInstance());
        else if (id == FAVORITE) {
            Intent i = new Intent(mContext, ShowPageActivity.class);
            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.FAVORITES_PAGE);
            mContext.startActivity(i);
        }
    }

    public class Item {
        public String name;
        public int id, count;

        Item(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
