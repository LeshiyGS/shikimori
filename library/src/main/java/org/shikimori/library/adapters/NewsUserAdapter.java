package org.shikimori.library.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.MessageHolder;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.h;

import java.util.Date;
import java.util.List;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class NewsUserAdapter extends BaseListAdapter<ItemNewsUserShiki, MessageHolder> {

    public NewsUserAdapter(Context context, List list) {
        super(context, list, R.layout.item_shiki_message_list, MessageHolder.class);
    }

    String formatDate(long date, String format) {
        return h.getStringDate(format, new Date(date));
    }


    @Override
    public void setListeners(MessageHolder holder) {
        super.setListeners(holder);
        holder.ivPoster.setOnTouchListener(h.getImageHighlight);
    }

    @Override
    public MessageHolder getViewHolder(View v) {
        MessageHolder holder = super.getViewHolder(v);
        holder.ivPoster = (ImageView) v.findViewById(R.id.ivPoster);
        return holder;
    }

    @Override
    public void setValues(MessageHolder holder, ItemNewsUserShiki item) {
        if(item.from!=null)
            holder.tvName.setText(item.from.nickname);
        Date date = h.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.createdAt);
        String sdate = formatDate(date.getTime(), "dd MMMM yyyy HH:mm");

        holder.tvDate.setText(sdate);
        h.setTextViewHTML(getContext(), holder.tvText, item.htmlBody);

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivUser.setImageDrawable(null);
        holder.ivPoster.setImageDrawable(null);
        ShikiImage.show(item.from.img148, holder.ivUser);
        if(item.linked!=null && item.linked.image!=null)
            ShikiImage.show(item.linked.image.x96, holder.ivPoster);

    }
}
