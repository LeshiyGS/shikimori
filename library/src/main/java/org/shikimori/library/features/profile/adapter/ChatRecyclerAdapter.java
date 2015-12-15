package org.shikimori.library.features.profile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.SettingsHolder;
import org.shikimori.library.adapters.holder.SettingsRecycleHolder;
import org.shikimori.library.fragments.base.abstracts.recycleview.ListRecycleAdapter;
import org.shikimori.library.fragments.base.abstracts.recycleview.OnItemClickRecycleListener;
import org.shikimori.library.objects.ActionQuote;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ReadMessageController;
import org.shikimori.library.tool.hs;

import java.util.Date;
import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class ChatRecyclerAdapter extends ListRecycleAdapter<ItemNewsUserShiki, SettingsRecycleHolder> implements View.OnClickListener {

    private View.OnClickListener clickListener;

    public ChatRecyclerAdapter(Context context, List<ItemNewsUserShiki> list) {
        super(context, list, R.layout.item_shiki_comments_list);
    }

    String formatDate(long date, String format) {
        return hs.getStringDate(format, new Date(date));
    }

    @Override
    public void setListeners(SettingsRecycleHolder holder) {
        if (clickListener != null)
            holder.ivSettings.setOnClickListener(clickListener);
        holder.ivPoster.setOnClickListener(this);
        holder.tvRead.setOnClickListener(this);
    }

    @Override
    public SettingsRecycleHolder getViewHolder(View v) {
        return new SettingsRecycleHolder(v);
    }

    public void setOnSettingsListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void setValues(SettingsRecycleHolder holder, ItemNewsUserShiki item, int position) {
        holder.tvName.setText(item.from.nickname);
//        Date date = hs.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.createdAt);
//        String sdate = formatDate(date.getTime(), "dd MMMM yyyy HH:mm");
        holder.tvDate.setText(hs.getTimeAgo(item.createdAt));
//        HtmlText text = new HtmlText(getContext(), false);
//        text.setText(item.html_body, holder.tvText);
        holder.llBodyHtml.removeAllViews();
        holder.llBodyHtml.setTag(R.id.icQuote, new ActionQuote(item.from.id, item.from.nickname, item.id));
        if (item.parsedContent.getParent() != null)
            ((ViewGroup) item.parsedContent.getParent()).removeAllViews();
        holder.llBodyHtml.addView(item.parsedContent);

        holder.ivSettings.setTag(position);
//        h.setTextViewHTML(getContext(),holder.tvText,item.html_body.toString());

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivPoster.setImageDrawable(null);
        holder.ivPoster.setTag(position);
        ImageLoader.getInstance().displayImage(item.from.img148, holder.ivPoster);

        holder.tvRead.setTag(position);
        hs.setVisible(!ShikiUser.USER_ID.equals(item.from.id), holder.tvRead);
        ProjectTool.setReadOpasity(holder.tvRead, item.read);
    }

    @Override
    public void onClick(View v) {
        // this is user
        ItemNewsUserShiki item = getItem((int) v.getTag());
        if (v.getId() == R.id.ivPoster) {
            Intent intent = new Intent(getContext(), ShowPageActivity.class);
            intent.putExtra(Constants.USER_ID, item.from.id);
            intent.putExtra(Constants.DISSCUSION_TYPE, Constants.TYPE_USER);
            intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.USER_PROFILE);
            getContext().startActivity(intent);
        } else if (v.getId() == R.id.tvRead) {
            item.read = ReadMessageController.getInstance().setRead(v, item.read, item.id);
        }
    }

    public ItemNewsUserShiki getItemById(String id){
        for (int i = 0; i < getItemCount(); i++) {
            ItemNewsUserShiki item = getItem(i);
            if(item.id.equals(id))
                return item;
        }
        return null;
    }
}
