package org.shikimori.library.features.profile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.abstracts.recycleview.ListRecycleAdapter;
import org.shikimori.library.features.profile.model.ItemDialogs;
import org.shikimori.library.tool.InvalidateTool;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.controllers.ReadMessageController;
import org.shikimori.library.tool.hs;

import java.util.Date;
import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class InboxRecycleAdapter extends ListRecycleAdapter<ItemDialogs, InboxRecycleHolder> implements View.OnClickListener {

    private View.OnClickListener clickListener;
    private ReadMessageController.OnUpdateReadListener updateListener;

    public InboxRecycleAdapter(Context context, List<ItemDialogs> list) {
        super(context, list, R.layout.item_shiki_inbox_list);
    }

    String formatDate(long date, String format) {
        return hs.getStringDate(format, new Date(date));
    }

    @Override
    public void setListeners(InboxRecycleHolder holder) {
        if (clickListener != null)
            holder.ivSettings.setOnClickListener(clickListener);
        holder.ivPoster.setOnClickListener(this);
        holder.tvRead.setOnClickListener(this);
    }

    @Override
    public InboxRecycleHolder getViewHolder(View v) {
        return new InboxRecycleHolder(v);
    }

    public void setOnSettingsListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void setValues(InboxRecycleHolder holder, ItemDialogs item, int position) {

        if(item.message == null || item.user == null)
            return;

        boolean selfUser = item.message.from.id.equals(item.user.id);
        hs.setVisibleGone(selfUser, holder.llFromUserName);

        if(!selfUser){
            holder.tvFromUser.setText(item.message.from.nickname);
            ImageLoader.getInstance().displayImage(item.message.from.img148, holder.ivFromUser);
        }

        holder.tvName.setText(item.user.nickname);
        Date date = hs.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.message.createdAt);
        String sdate = formatDate(date.getTime(), "dd MMMM yyyy HH:mm");
        holder.tvDate.setText(sdate);
        // delete content before
        holder.llBodyHtml.removeAllViews();
        // create html content
        if (item.message.parsedContent.getParent() != null)
            ((ViewGroup) item.message.parsedContent.getParent()).removeAllViews();
        holder.llBodyHtml.addView(item.message.parsedContent);
        // settings
        holder.ivSettings.setTag(position);

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivPoster.setImageDrawable(null);
        holder.ivPoster.setTag(position);
        ImageLoader.getInstance().displayImage(item.user.img148, holder.ivPoster);

        holder.tvRead.setTag(position);
        selfUser = ShikiUser.USER_ID.equals(item.message.from.id);
        hs.setVisible(!selfUser, holder.tvRead);
        if (!selfUser)
            ProjectTool.setReadOpasity(holder.tvRead, item.message.read);
    }

    @Override
    public void onClick(View v) {
        // this is user
        ItemDialogs item = getItem((int) v.getTag());
        if (v.getId() == R.id.ivPoster) {
            ProjectTool.goToUser(getContext(), item.message.from.id);
        } else if (v.getId() == R.id.tvRead) {
            item.message.read = ReadMessageController.getInstance().setRead(v, item.message.read, item.message.id);
            InvalidateTool.invalidateMessages(InvalidateTool.getQuery(getContext()));
        }
    }

}
