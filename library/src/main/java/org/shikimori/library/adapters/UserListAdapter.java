package org.shikimori.library.adapters;

/**
 * Created by LeshiyGS on 04.04.2015.
 */
import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.adapters.base.BaseCommentsListAdapter;
import org.shikimori.library.adapters.base.BaseUserListAdapter;
import org.shikimori.library.objects.ItemUserListShiki;
import org.shikimori.library.tool.h;

import java.util.Date;
import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class UserListAdapter extends BaseUserListAdapter<ItemUserListShiki> {

    public UserListAdapter(Context context, List<ItemUserListShiki> list) {
        super(context, list);
    }

    String formatDate(long date, String format) {
        return h.getStringDate(format, new Date(date));
    }

    @Override
    public void setValues(ViewHolder holder, ItemUserListShiki item) {
        holder.tvUserName.setText(item.tName);
        holder.tvCommentsDate.setText(item.tRussian);
        //h.setTextViewHTML(getContext(),holder.tvCommentsText,item.text_html.toString());

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivImage.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(item.tpreview, holder.ivImage);
    }

}