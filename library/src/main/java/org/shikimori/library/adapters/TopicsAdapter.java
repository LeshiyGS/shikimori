package org.shikimori.library.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.MessageHolder;
import org.shikimori.library.adapters.holder.TopicHolder;
import org.shikimori.library.objects.ItemTopicsShiki;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.h;

import java.util.List;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class TopicsAdapter extends BaseListAdapter<ItemTopicsShiki, TopicHolder> {

    private final String commentsText;

    public TopicsAdapter(Context context, List list) {
        super(context, list, R.layout.item_shiki_topic_list, TopicHolder.class);
        commentsText = context.getString(R.string.comments) + ": ";
    }

    @Override
    public void setListeners(TopicHolder holder) {
        super.setListeners(holder);
        holder.ivPoster.setOnTouchListener(h.getImageHighlight);
    }

    @Override
    public TopicHolder getViewHolder(View v) {
        TopicHolder holder = super.getViewHolder(v);
        holder.ivPoster = (ImageView) v.findViewById(R.id.ivPoster);
        holder.tvCountComments = (TextView) v.findViewById(R.id.tvCountComments);
        holder.tvSection = (TextView) v.findViewById(R.id.tvSection);
        return holder;
    }

    @Override
    public void setValues(TopicHolder holder, ItemTopicsShiki item) {
        if(item.user!=null)
            holder.tvName.setText(item.user.nickname);
        // count comments
        holder.tvCountComments.setText(commentsText + String.valueOf(item.commentsCount));
        // date
        holder.tvDate.setText(ProjectTool.formatDatePost(item.createdAt));
        // text
        h.setTextViewHTML(getContext(), holder.tvText, item.htmlBody);
        // images
        ShikiImage.show(item.user.img148, holder.ivUser, true);
        if(item.linked!=null && item.linked.image!=null)
            ShikiImage.show(item.linked.image.x96, holder.ivPoster, true);
        else
            h.setVisibleGone(holder.ivPoster);

        holder.tvSection.setText(item.section.name);
        ProjectTool.setTypeColor(getContext(), holder.tvSection, item.linkedType);
    }
}
