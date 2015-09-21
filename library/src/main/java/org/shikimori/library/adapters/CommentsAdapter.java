package org.shikimori.library.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.SettingsHolder;
import org.shikimori.library.objects.ActionQuote;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.hs;

import java.util.Date;
import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class CommentsAdapter extends BaseListAdapter<ItemCommentsShiki, SettingsHolder> implements View.OnClickListener {

    private View.OnClickListener clickListener;

    public CommentsAdapter(Context context, List<ItemCommentsShiki> list) {
        super(context, list, R.layout.item_shiki_comments_list, SettingsHolder.class);
    }

    String formatDate(long date, String format) {
        return hs.getStringDate(format, new Date(date));
    }

    @Override
    public void setListeners(SettingsHolder holder) {
        super.setListeners(holder);
        if (clickListener != null)
            holder.ivSettings.setOnClickListener(clickListener);
        holder.ivPoster.setOnClickListener(this);
    }

    @Override
    public SettingsHolder getViewHolder(View v) {
        SettingsHolder hol = super.getViewHolder(v);
        hol.ivSettings = find(v, R.id.icSettings);
        return hol;
    }

    public void setOnSettingsListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void setValues(SettingsHolder holder, ItemCommentsShiki item, int position) {
        holder.tvName.setText(item.nickname);
        Date date = hs.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.created_at);
        String sdate = formatDate(date.getTime(), "dd MMMM yyyy HH:mm");
        holder.tvDate.setText(sdate);
//        HtmlText text = new HtmlText(getContext(), false);
//        text.setText(item.html_body, holder.tvText);
        holder.llBodyHtml.removeAllViews();
        holder.llBodyHtml.setTag(R.id.icQuote, new ActionQuote(item.user_id, item.nickname, item.id));
//        initDescription(item, holder.llBodyHtml);
        if (item.parsedContent.getParent() != null)
            ((ViewGroup) item.parsedContent.getParent()).removeAllViews();
        holder.llBodyHtml.addView(item.parsedContent);
        holder.ivSettings.setTag(position);

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivPoster.setImageDrawable(null);
        holder.ivPoster.setTag(position);
        ImageLoader.getInstance().displayImage(item.image_x160, holder.ivPoster);
    }

    @Override
    public void onClick(View v) {
        // this is user
        if (v.getId() == R.id.ivPoster) {
            ItemCommentsShiki item = getItem((int) v.getTag());
            ProjectTool.goToUser(getContext(), item.user_id);
        }
    }

    public ItemCommentsShiki getItemById(String id){
        for (int i = 0; i < getCount(); i++) {
            ItemCommentsShiki item = getItem(i);
            if(item.id.equals(id))
                return item;
        }
        return null;
    }


}
