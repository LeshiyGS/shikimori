package org.shikimori.library.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.MessageHolder;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.ItemClubShiki;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ReadMessageController;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.jsop.BodyBuild;
import org.shikimori.library.tool.popup.TextPopup;

import java.util.List;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class NewsUserAdapter extends BaseListAdapter<ItemNewsUserShiki, MessageHolder> implements View.OnClickListener {

    private final BodyBuild bodyBuild;
    private String type;
    private Query query;

    public NewsUserAdapter(Context context, Query query, List list) {
        super(context, list, R.layout.item_shiki_message_list, MessageHolder.class);
        this.query = query;
        bodyBuild = ProjectTool.getBodyBuilder((BaseActivity) context, BodyBuild.CLICKABLETYPE.NOT);
    }

    @Override
    public void setListeners(MessageHolder holder) {
        super.setListeners(holder);
        holder.ivPoster.setOnTouchListener(h.getImageHighlight);
        holder.tvRead.setOnClickListener(this);
        holder.ivUser.setOnClickListener(this);
        holder.bGoTo.setOnClickListener(this);
        holder.bComment.setOnClickListener(this);
    }

    @Override
    public MessageHolder getViewHolder(View v) {
        MessageHolder holder = super.getViewHolder(v);
        holder.ivPoster = get(v, R.id.ivPoster);
        holder.tvRead = get(v, R.id.tvRead);
        holder.bGoTo = get(v, R.id.bGoTo);
        holder.bComment = get(v, R.id.bComment);
        return holder;
    }

    @Override
    public void setValues(MessageHolder holder, ItemNewsUserShiki item, int position) {
        if (item.from != null)
            holder.tvName.setText(item.from.nickname);

        holder.tvDate.setText(ProjectTool.formatDatePost(item.createdAt));

        holder.llBodyHtml.removeAllViews();
//        initDescription(item, holder.llBodyHtml);
        if (item.parsedContent.getParent() != null)
            ((ViewGroup) item.parsedContent.getParent()).removeAllViews();

        holder.llBodyHtml.addView(item.parsedContent);

        holder.ivUser.setTag(item);
        ShikiImage.show(item.from.img148, holder.ivUser, true);
        if (item.linked != null && item.linked.image != null)
            ShikiImage.show(item.linked.image.x96, holder.ivPoster, true);
        else
            h.setVisibleGone(holder.ivPoster);

        holder.tvRead.setTag(position);
        holder.bComment.setTag(position);
        holder.bGoTo.setTag(position);
        h.setVisible(holder.tvRead);
        ProjectTool.setReadOpasity(holder.tvRead, item.read);
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.tvRead) {
            int position = (int) v.getTag();
            ItemNewsUserShiki item = getItem(position);
            item.read = ReadMessageController.getInstance().setRead(v, item.read, item.id);
        } else if (v.getId() == R.id.ivUser) {
            if (type.equals(Constants.INBOX) || type.equals(Constants.NOTIFYING)) {
                ItemNewsUserShiki item = (ItemNewsUserShiki) v.getTag();
                ProjectTool.goToUser(getContext(), item.from.id);
            }
        } else if (v.getId() == R.id.bComment) {
            showCooment(getItem((int) v.getTag()));
        } else if (v.getId() == R.id.bGoTo) {
            ItemNewsUserShiki item = getItem((int) v.getTag());
            Intent i = new Intent(getContext(), ShowPageActivity.class);
            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.DISCUSSION);
            i.putExtra(Constants.TREAD_ID, item.linked.threadId);
//            i.putExtra(Constants.ACTION_BAR_TITLE, item.id);
            getContext().startActivity(i);
        }
    }

    private void showCooment(ItemNewsUserShiki item){
        ProjectTool.showComment(query, item.linked.id, bodyBuild);
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
