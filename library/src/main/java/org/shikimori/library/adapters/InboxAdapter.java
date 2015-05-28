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
import org.shikimori.library.adapters.holder.SettingsHolder;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.ItemDialogs;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.Date;
import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class InboxAdapter extends BaseListAdapter<ItemDialogs, SettingsHolder> implements View.OnClickListener {

    private final BodyBuild bodyBuilder;
    private final Query query;
    private View.OnClickListener clickListener;

    public InboxAdapter(Context context, Query query, List<ItemDialogs> list) {
        super(context, list, R.layout.item_shiki_comments_list, SettingsHolder.class);
        this.query = query;
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

    public void setOnSettingsListener(View.OnClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public void setValues(SettingsHolder holder, ItemDialogs item, int position) {
        holder.tvName.setText(item.message.from.nickname);
        Date date = h.getDateFromString("yyyy-MM-dd'T'HH:mm:ss.SSSZ", item.message.createdAt);
        String sdate = formatDate(date.getTime(), "dd MMMM yyyy HH:mm");
        holder.tvDate.setText(sdate);
        // delete content before
        holder.llBodyHtml.removeAllViews();
        // create html content
        initDescription(item, holder.llBodyHtml);
        // settings
        holder.ivSettings.setTag(position);

        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivPoster.setImageDrawable(null);
        holder.ivPoster.setTag(position);
        ImageLoader.getInstance().displayImage(item.message.from.img148, holder.ivPoster);

        holder.tvRead.setTag(position);
        h.setVisible(holder.tvRead, true);
        ProjectTool.setReadOpasity(holder.tvRead, item.message.read);
    }

    private void initDescription(final ItemDialogs item, final ViewGroup llBodyHtml) {
        // Если уже распарсили то берем высоту вьюхи иначе прыгать начинает
        if(item.message.parsedContent != null){
            llBodyHtml.setMinimumHeight(item.message.parsedContent.getHeight());
        } else
            llBodyHtml.setMinimumHeight(0);

        // Парсим ассинхронно каждую, так как при парсинге всех, после прокрутки списка вылетает
        bodyBuilder.parceAsync(item.message.htmlBody, new BodyBuild.ParceDoneListener() {
            @Override
            public void done(ViewGroup view) {
                item.message.parsedContent = view;
                llBodyHtml.addView(item.message.parsedContent);
                YoYo.with(Techniques.FadeIn)
                    .duration(300)
                        .playOn(item.message.parsedContent);
                bodyBuilder.loadPreparedImages();
            }
        });
    }

    @Override
    public void onClick(View v) {
        // this is user
        ItemDialogs item = getItem((int) v.getTag());
        if(v.getId() == R.id.ivPoster){
            Intent intent = new Intent(getContext(), ShowPageActivity.class);
            intent.putExtra(Constants.USER_ID, item.message.from.id);
            intent.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.USER_PROFILE);
            getContext().startActivity(intent);
        } else if (v.getId() == R.id.tvRead) {
            query.init(ShikiApi.getUrl(ShikiPath.READ_MESSAGE))
                    .setMethod(Query.METHOD.POST)
                    .addParam("is_read", item.message.read ? 0 : 1)
                    .addParam("ids", item.message.id)
                    .getResult(new Query.OnQuerySuccessListener() {
                        @Override
                        public void onQuerySuccess(StatusResult res) {
                        }
                    });
            item.message.read = !item.message.read;
            ProjectTool.setReadOpasity(v, item.message.read);
        }
    }
}
