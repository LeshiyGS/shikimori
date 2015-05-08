package org.shikimori.library.adapters;

import android.content.Context;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.MessageHolder;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.baselisteners.BaseAnimationListener;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class NewsUserAdapter extends BaseListAdapter<ItemNewsUserShiki, MessageHolder> implements View.OnClickListener {

    private final Query query;
    private String type;

    public NewsUserAdapter(Context context, Query query, List list) {
        super(context, list, R.layout.item_shiki_message_list, MessageHolder.class);
        this.query = query;
    }

    @Override
    public void setListeners(MessageHolder holder) {
        super.setListeners(holder);
        holder.ivPoster.setOnTouchListener(h.getImageHighlight);
        holder.tvRead.setOnClickListener(this);
    }

    @Override
    public MessageHolder getViewHolder(View v) {
        MessageHolder holder = super.getViewHolder(v);
        holder.ivPoster = get(v, R.id.ivPoster);
        holder.tvRead = get(v, R.id.tvRead);
        return holder;
    }

    @Override
    public void setValues(MessageHolder holder, ItemNewsUserShiki item, int position) {
        if (item.from != null)
            holder.tvName.setText(item.from.nickname);

        holder.tvDate.setText(ProjectTool.formatDatePost(item.createdAt));
        h.setTextViewHTML(getContext(), holder.tvText, item.htmlBody);

        ShikiImage.show(item.from.img148, holder.ivUser, true);
        if (item.linked != null && item.linked.image != null)
            ShikiImage.show(item.linked.image.x96, holder.ivPoster, true);
        else
            h.setVisibleGone(holder.ivPoster);

        holder.tvRead.setTag(position);
        if (item.read)
            h.setVisibleGone(holder.tvRead);
        else
            h.setVisible(holder.tvRead, true);
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.tvRead) {
            int position = (int) v.getTag();
            final ItemNewsUserShiki item = getItem(position);
            query.init(ShikiApi.getUrl(ShikiPath.READ_MESSAGE))
                    .setMethod(Query.METHOD.POST)
                    .addParam("ids", getMessageIds(item.id))
                    .getResult(new Query.OnQuerySuccessListener() {
                        @Override
                        public void onQuerySuccess(StatusResult res) {
                        }
                    });
            item.read = true;
            YoYo.with(Techniques.FadeOut)
                    .duration(300)
                    .withListener(new BaseAnimationListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            h.setVisibleGone(v);
                        }
                    }).playOn(v);
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    String getMessageIds(String id) {
        switch (type) {
            case "inbox":
                return id;
            case "news":
                return "message-" + id;
            case "notifications":
                return id;
        }
        return id;
    }
}
