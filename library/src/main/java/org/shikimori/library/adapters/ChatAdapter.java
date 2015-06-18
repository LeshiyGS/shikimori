package org.shikimori.library.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.SettingsHolder;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ReadMessageController;
import org.shikimori.library.tool.h;

import java.util.Date;
import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class ChatAdapter extends BaseListAdapter<ItemNewsUserShiki, SettingsHolder> implements View.OnClickListener {

    private View.OnClickListener clickListener;

    public ChatAdapter(Context context, List<ItemNewsUserShiki> list) {
        super(context, list, R.layout.item_shiki_comments_list, SettingsHolder.class);
    }

    String formatDate(long date, String format) {
        return h.getStringDate(format, new Date(date));
    }

    @Override
    public void setListeners(SettingsHolder holder) {
        super.setListeners(holder);
        if (clickListener != null)
            holder.ivSettings.setOnClickListener(clickListener);
        holder.ivPoster.setOnClickListener(this);
        holder.tvRead.setOnClickListener(this);
    }

    @Override
    public SettingsHolder getViewHolder(View v) {
        SettingsHolder hol = super.getViewHolder(v);
        hol.ivSettings = get(v, R.id.icSettings);
        hol.tvRead = get(v, R.id.tvRead);
        return hol;
    }

    public void setOnSettingsListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void setValues(SettingsHolder holder, ItemNewsUserShiki item, int position) {
        holder.tvName.setText(item.from.nickname);
        Date date = h.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.createdAt);
        String sdate = formatDate(date.getTime(), "dd MMMM yyyy HH:mm");
        holder.tvDate.setText(sdate);
//        HtmlText text = new HtmlText(getContext(), false);
//        text.setText(item.html_body, holder.tvText);
        holder.llBodyHtml.removeAllViews();
        if (item.parsedContent.getParent() != null)
            ((ViewGroup) item.parsedContent.getParent()).removeAllViews();

        holder.ivSettings.setTag(position);
//        h.setTextViewHTML(getContext(),holder.tvText,item.html_body.toString());

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivPoster.setImageDrawable(null);
        holder.ivPoster.setTag(position);
        ImageLoader.getInstance().displayImage(item.from.img148, holder.ivPoster);

        holder.tvRead.setTag(position);
        if (ShikiUser.USER_ID.equals(item.from.id))
            h.setVisible(holder.tvRead, false);
        else
            h.setVisible(holder.tvRead, true);
        ProjectTool.setReadOpasity(holder.tvRead, item.read);
    }

    @Override
    public void onClick(View v) {
        // this is user
        ItemNewsUserShiki item = getItem((int) v.getTag());
        if (v.getId() == R.id.ivPoster) {
            Intent intent = new Intent(getContext(), ShowPageActivity.class);
            intent.putExtra(Constants.USER_ID, item.from.id);
            intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.USER_PROFILE);
            getContext().startActivity(intent);
        } else if (v.getId() == R.id.tvRead) {
            item.read = ReadMessageController.getInstance().setRead(v, item.read, item.id);
        }
    }
}
