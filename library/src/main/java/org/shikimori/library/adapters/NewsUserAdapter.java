package org.shikimori.library.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.animation.Animation;

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
import org.shikimori.library.tool.baselisteners.BaseAnimationListenerAndroid;
import org.shikimori.library.tool.constpack.Constants;
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
        holder.ivUser.setOnClickListener(this);
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

        if(item.kind.toLowerCase().equals(Constants.SITENEWS)){
            holder.tvText.setTypeface(null, Typeface.ITALIC);
            holder.tvText.setText(R.string.open_details);
        } else {
            holder.tvText.setTypeface(null, Typeface.NORMAL);
            h.setTextViewHTML(getContext(), holder.tvText, item.htmlBody);
        }

        holder.ivUser.setTag(item);
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
            h.startAnimation(v, R.anim.abc_fade_out, new BaseAnimationListenerAndroid() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    h.setVisibleGone(v);
                }
            });
        } else if (v.getId() == R.id.ivUser){
            if(type.equals(Constants.INBOX)){
                ItemNewsUserShiki item = (ItemNewsUserShiki) v.getTag();
                ProjectTool.goToUser(getContext(), item.from.id);
            }
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    String getMessageIds(String id) {
        switch (type) {
            case Constants.INBOX:
                return id;
            case Constants.NEWS:
                return "message-" + id;
            case Constants.NOTIFYING:
                return id;
        }
        return id;
    }
}
