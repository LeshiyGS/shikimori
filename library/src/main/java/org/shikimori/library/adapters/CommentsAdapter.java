package org.shikimori.library.adapters;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.adapters.base.BaseAnimeGridAdapter;
import org.shikimori.library.adapters.base.BaseCommentsListAdapter;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.objects.ItemAnimesShiki;
import org.shikimori.library.objects.ItemCommentsShiki;
import org.shikimori.library.tool.h;

import java.util.Date;
import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class CommentsAdapter extends BaseCommentsListAdapter<ItemCommentsShiki> {

    public CommentsAdapter(Context context, List<ItemCommentsShiki> list) {
        super(context, list);
    }

    String formatDate(long date, String format) {
        return h.getStringDate(format, new Date(date));
    }

    @Override
    public void setValues(ViewHolder holder, ItemCommentsShiki item) {
        holder.tvUserName.setText(item.nickname);
        Date date = h.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.created_at);
        String sdate = formatDate(date.getTime(), "dd MMMM yyyy HH:mm");
        holder.tvCommentsDate.setText(sdate);
        h.setTextViewHTML(getContext(),holder.tvCommentsText,item.html_body.toString());

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivImage.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(item.avatar, holder.ivImage);
    }
}
