package org.shikimori.library.adapters;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.BaseHolder;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.elements.HtmlText;

import java.util.Date;
import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class CommentsAdapter extends BaseListAdapter<ItemCommentsShiki, BaseHolder> {

    public CommentsAdapter(Context context, List<ItemCommentsShiki> list) {
        super(context, list, R.layout.item_shiki_comments_list);
    }

    String formatDate(long date, String format) {
        return h.getStringDate(format, new Date(date));
    }

    @Override
    public void setValues(BaseHolder holder, ItemCommentsShiki item) {
        holder.tvName.setText(item.nickname);
        Date date = h.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.created_at);
        String sdate = formatDate(date.getTime(), "dd MMMM yyyy HH:mm");
        holder.tvDate.setText(sdate);
        HtmlText text = new HtmlText(getContext(), false);
        text.setText(item.html_body, holder.tvText);
//        h.setTextViewHTML(getContext(),holder.tvText,item.html_body.toString());

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivPoster.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(item.avatar, holder.ivPoster);
    }
}
