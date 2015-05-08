package org.shikimori.library.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.holder.BaseHolder;
import org.shikimori.library.adapters.holder.SettingsHolder;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.elements.HtmlText;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.Date;
import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class CommentsAdapter extends BaseListAdapter<ItemCommentsShiki, SettingsHolder> {

    private final BodyBuild bodyBuilder;
    private View.OnClickListener clickListener;

    public CommentsAdapter(Context context, List<ItemCommentsShiki> list) {
        super(context, list, R.layout.item_shiki_comments_list, SettingsHolder.class);
        bodyBuilder = new BodyBuild((Activity) context);
    }

    String formatDate(long date, String format) {
        return h.getStringDate(format, new Date(date));
    }

    @Override
    public void setListeners(SettingsHolder holder) {
        super.setListeners(holder);
        if(clickListener!=null)
            holder.ivSettings.setOnClickListener(clickListener);
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
        ImageLoader.getInstance().displayImage(item.image_x160, holder.ivPoster);
    }

    private void initDescription(final ItemCommentsShiki item, final ViewGroup llBodyHtml) {
        // Если уже распарсили то берем высоту вьюхи иначе прыгать начинает
        if(item.parsedContent != null){
//            ViewGroup.LayoutParams params = llBodyHtml.getLayoutParams();
//            params.height = item.parsedContent.getHeight();
            llBodyHtml.setMinimumHeight(item.parsedContent.getHeight());
//            llBodyHtml.setLayoutParams(params);
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
}
