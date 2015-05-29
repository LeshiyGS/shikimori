package org.shikimori.library.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.BaseHolder;
import org.shikimori.library.adapters.holder.SettingsHolder;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.elements.HtmlText;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.Date;
import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class CommentsAdapter extends BaseListAdapter<ItemCommentsShiki, SettingsHolder> implements View.OnClickListener {

    private final BodyBuild bodyBuilder;
    private View.OnClickListener clickListener;

    public CommentsAdapter(Context context, BodyBuild bodyBuilder, List<ItemCommentsShiki> list) {
        super(context, list, R.layout.item_shiki_comments_list, SettingsHolder.class);
        this.bodyBuilder = bodyBuilder;
    }

    String formatDate(long date, String format) {
        return h.getStringDate(format, new Date(date));
    }

    @Override
    public void setListeners(SettingsHolder holder) {
        super.setListeners(holder);
        if(clickListener!=null)
            holder.ivSettings.setOnClickListener(clickListener);
        holder.ivPoster.setOnClickListener(this);
    }

    @Override
    public SettingsHolder getViewHolder(View v) {
        SettingsHolder hol = super.getViewHolder(v);
        hol.ivSettings = get(v, R.id.icSettings);
        return hol;
    }

    public void setOnSettingsListener(View.OnClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public void setValues(SettingsHolder holder, ItemCommentsShiki item, int position) {
        holder.tvName.setText(item.nickname);
        Date date = h.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.created_at);
        String sdate = formatDate(date.getTime(), "dd MMMM yyyy HH:mm");
        holder.tvDate.setText(sdate);
//        HtmlText text = new HtmlText(getContext(), false);
//        text.setText(item.html_body, holder.tvText);
        holder.llBodyHtml.removeAllViews();
        initDescription(item, holder.llBodyHtml);

        holder.ivSettings.setTag(position);
//        h.setTextViewHTML(getContext(),holder.tvText,item.html_body.toString());

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivPoster.setImageDrawable(null);
        holder.ivPoster.setTag(position);
        ImageLoader.getInstance().displayImage(item.image_x160, holder.ivPoster);
    }

    private void initDescription(final ItemCommentsShiki item, final ViewGroup llBodyHtml) {
        // Если уже распарсили то берем высоту вьюхи иначе прыгать начинает
        if(item.parsedContent != null){
            llBodyHtml.setMinimumHeight(item.parsedContent.getHeight());
        } else
            llBodyHtml.setMinimumHeight(0);

        // Парсим ассинхронно каждую, так как при парсинге всех, после прокрутки списка вылетает
        bodyBuilder.parceAsync(item.getHtml(), new BodyBuild.ParceDoneListener() {
            @Override
            public void done(ViewGroup view) {
                item.parsedContent = view;
                llBodyHtml.addView(item.parsedContent);
                YoYo.with(Techniques.FadeIn)
                    .duration(300)
                        .playOn(item.parsedContent);
                bodyBuilder.loadPreparedImages();
            }
        });
    }

    @Override
    public void onClick(View v) {
        // this is user
        if(v.getId() == R.id.ivPoster){
            ItemCommentsShiki item = getItem((int) v.getTag());
            ProjectTool.goToUser(getContext(), item.user_id);
        }
    }
}
