package org.shikimori.library.adapters;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.adapters.base.BaseCardGridAdapter;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.objects.one.Relation;

import java.util.List;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class RelationAdapter extends BaseCardGridAdapter<Relation> {

    public RelationAdapter(Context context, List<Relation> list) {
        super(context, list);
    }

    @Override
    public void setValues(ViewHolder holder, Relation item, int position) {

        holder.tvEpisode.setText(item.getRelationRussian());
        holder.tvTitle.setText(item.getAnime().id != null ? item.getAnime().name : item.getManga().name);
        holder.tvTitleRus.setText(item.getAnime().id != null ? item.getAnime().russianName : item.getManga().russianName);


        // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
        holder.ivImage.setImageDrawable(null);
        String imgUrl;
        if(item.getAnime().id != null)
            imgUrl = getImageUrl(item.getAnime());
        else
            imgUrl = getImageUrl(item.getManga());

        ImageLoader.getInstance().displayImage(imgUrl, holder.ivImage);
    }

    private String getImageUrl(AMShiki item){
        if(item.image == null)
            return item.poster;
        return item.image.original;
    }

}