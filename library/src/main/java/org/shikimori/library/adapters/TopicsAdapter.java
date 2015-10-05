package org.shikimori.library.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.TopicHolder;
import org.shikimori.library.objects.ItemTopicsShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.hs;

import java.util.List;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class TopicsAdapter extends BaseListAdapter<ItemTopicsShiki, TopicHolder> {


    public TopicsAdapter(Context context, List list) {
        super(context, list, R.layout.item_shiki_topic_list, TopicHolder.class);
    }

    @Override
    public TopicHolder getViewHolder(View v) {
        TopicHolder holder = super.getViewHolder(v);
        holder.ivPoster = (ImageView) v.findViewById(R.id.ivPoster);
        holder.tvCountComments = (TextView) v.findViewById(R.id.tvCountComments);
        holder.tvSection = (TextView) v.findViewById(R.id.tvSection);
        holder.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        holder.tvText = (TextView) v.findViewById(R.id.tvText);
        holder.llBodyHtml = (ViewGroup) v.findViewById(R.id.llBodyHtml);
        return holder;
    }

    @Override
    public void setValues(TopicHolder holder, ItemTopicsShiki item, int position) {
        if (item.user != null)
            holder.tvName.setText(item.user.nickname);
        // count comments
        holder.tvCountComments.setText(String.valueOf(item.commentsCount));
        // date
        holder.tvDate.setText(ProjectTool.formatDatePost(item.createdAt));
        // text

        holder.tvTitle.setText(item.title);
//        holder.tvText.setText(item.body);

        holder.llBodyHtml.removeAllViews();
        if (item.parsedContent != null) {
            if (item.parsedContent.getParent() != null)
                ((ViewGroup) item.parsedContent.getParent()).removeAllViews();
            hs.setVisible(holder.llBodyHtml, true);
            hs.setVisibleGone(holder.tvText);
            holder.llBodyHtml.addView(item.parsedContent);
        } else {
            hs.setVisibleGone(holder.llBodyHtml);
            hs.setVisible(holder.tvText, true);
        }


        // htmlBuilder.parce(item.htmlBody, holder.llBodyHtml);

//        HtmlText text = new HtmlText(getContext(), false);
//        text.setText(item.htmlBody, holder.tvText);

        //h.setTextViewHTML(getContext(), holder.tvText, item.htmlBody);
        // images
        ShikiImage.show(item.user.img148, holder.ivUser, true);
        if (item.linked != null && item.linked.image != null)
            ShikiImage.show(item.linked.image.x96, holder.ivPoster, true);
        else
            hs.setVisibleGone(holder.ivPoster);

        holder.tvSection.setText(item.section.name);
        ProjectTool.setTypeColor(getContext(), holder.tvSection, item.linkedType);
    }

}
