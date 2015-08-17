package org.shikimori.library.adapters;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.MessageHolder;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ReadMessageController;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.List;

import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.basekit.library.tools.h;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class NewsUserAdapter extends BaseListAdapter<ItemNewsUserShiki, MessageHolder> implements View.OnClickListener {

    private final BodyBuild bodyBuild;
    private String type;
    private Query query;
    private View.OnClickListener settingsClickListener;

    public NewsUserAdapter(BaseKitActivity<ShikiAC> context, Query query, List list) {
        this(context, R.layout.item_shiki_message_list, query, list);
    }

    public NewsUserAdapter(BaseKitActivity<ShikiAC> context, int layout,  Query query, List list) {
        super(context, list, layout, MessageHolder.class);
        this.query = query;
        bodyBuild = ProjectTool.getBodyBuilder(context, BodyBuild.CLICKABLETYPE.NOT);
    }

    @Override
    public void setListeners(MessageHolder holder) {
        super.setListeners(holder);
        holder.ivPoster.setOnTouchListener(hs.getImageHighlight);
        holder.tvRead.setOnClickListener(this);
        holder.ivUser.setOnClickListener(this);
        if(holder.bGoTo!=null){
            holder.bGoTo.setOnClickListener(this);
            holder.bComment.setOnClickListener(this);
        }
        if(holder.icSettings!=null && settingsClickListener!=null)
            holder.icSettings.setOnClickListener(settingsClickListener);
    }

    @Override
    public MessageHolder getViewHolder(View v) {
        MessageHolder holder = super.getViewHolder(v);
        holder.ivPoster = find(v, R.id.ivPoster);
        holder.tvRead = find(v, R.id.tvRead);
        holder.bGoTo = find(v, R.id.bGoTo);
        holder.bComment = find(v, R.id.bComment);
        holder.tvStatus = find(v, R.id.tvSection);
        holder.icSettings = find(v, R.id.icSettings);
        return holder;
    }

    @Override
    public void setValues(MessageHolder holder, ItemNewsUserShiki item, int position) {
        holder.tvDate.setText(ProjectTool.formatDatePost(item.createdAt));
        holder.llBodyHtml.removeAllViews();
//        initDescription(item, holder.llBodyHtml);
        if (item.parsedContent.getParent() != null)
            ((ViewGroup) item.parsedContent.getParent()).removeAllViews();

        holder.llBodyHtml.addView(item.parsedContent);

        if(type.equals(Constants.NEWS)){
            h.setVisible(holder.tvStatus);
            holder.tvStatus.setText(ProjectTool.getStatus(getContext(), item.kind));
            ProjectTool.setTypeColorFromKind(getContext(), holder.tvStatus, item.kind);
        } else
            h.setVisibleGone(holder.tvStatus);

        if(item.linked != null && item.linked.type !=null){
            showUser(item, holder);
        } else {
            ShikiImage.show(item.from.img148, holder.ivUser, true);
        }
        holder.tvName.setText(getTitle(item));
        holder.ivUser.setTag(item);
        holder.tvRead.setTag(position);
        if(holder.bGoTo!=null){
            holder.bComment.setTag(position);
            holder.bGoTo.setTag(position);
        }
        if(holder.icSettings!=null)
            holder.icSettings.setTag(position);

        hs.setVisible(holder.tvRead);
        ProjectTool.setReadOpasity(holder.tvRead, item.read);
    }

    private String getTitle(ItemNewsUserShiki item){
        if(item.linked!=null && (Constants.ANIME.equalsIgnoreCase(item.linked.type) || Constants.MANGA.equalsIgnoreCase(item.linked.type)))
            return item.linked.name;
        else if (item.from!=null)
            return item.from.nickname;
        return null;
    }

    private void showUser(ItemNewsUserShiki item, MessageHolder holder){
        if(Constants.ANIME.equalsIgnoreCase(item.linked.type) || Constants.MANGA.equalsIgnoreCase(item.linked.type)){
            h.setVisibleGone(holder.ivUser);
            if(item.linked.image != null)
                ShikiImage.show(item.linked.image.x96, holder.ivPoster, true);
        } else {
            ShikiImage.show(item.from.img148, holder.ivUser, true);
            h.setVisibleGone(holder.ivPoster);
        }
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
            i.putExtra(Constants.TREAD_ID, item.linked.threadId);
            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.DISCUSSION);
            i.putExtra(Constants.ACTION_BAR_TITLE, getContext().getString(R.string.comments));
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

    public void setOnSettingsListener(View.OnClickListener clickListener) {
        this.settingsClickListener = clickListener;
    }
}
