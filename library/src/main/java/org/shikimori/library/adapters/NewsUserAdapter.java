package org.shikimori.library.adapters;

import android.content.Context;

import org.shikimori.library.adapters.base.BaseMessageListAdapter;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.h;

import java.util.Date;
import java.util.List;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class NewsUserAdapter extends BaseMessageListAdapter<ItemNewsUserShiki> {

    public NewsUserAdapter(Context context, List list) {
        super(context, list);
    }

    String formatDate(long date, String format) {
        return h.getStringDate(format, new Date(date));
    }

    @Override
    public void setValues(ViewHolder holder, ItemNewsUserShiki item) {
        if(item.from!=null)
            holder.tvUserName.setText(item.from.nickname);
        Date date = h.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.createdAt);
        String sdate = formatDate(date.getTime(), "dd MMMM yyyy HH:mm");

        holder.tvCommentsDate.setText(sdate);
        h.setTextViewHTML(getContext(), holder.tvCommentsText, item.htmlBody);

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivUser.setImageDrawable(null);
        holder.ivPoster.setImageDrawable(null);
        ShikiImage.show(item.from.img148, holder.ivUser);
        if(item.linked!=null)
            ShikiImage.show(item.linked.image.x96, holder.ivPoster);

    }
}
