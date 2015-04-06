package org.shikimori.library.adapters;

/**
 * Created by LeshiyGS on 04.04.2015.
 */
import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.base.UserListBaseListAdapter;
import org.shikimori.library.adapters.holder.BaseHolder;
import org.shikimori.library.objects.ItemUserListShiki;

import java.util.List;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class UserListAdapter extends UserListBaseListAdapter<ItemUserListShiki, BaseHolder> {

    public UserListAdapter(Context context, List<ItemUserListShiki> list) {
        super(context, list, R.layout.item_shiki_user_list);
    }

    @Override
    public void setValues(BaseHolder holder, ItemUserListShiki item) {
        if(item.amDetails!=null){
            holder.tvName.setText(item.amDetails.name);
            holder.tvRusName.setText(item.amDetails.russianName);
            holder.tvStatus.setText(getContext().getString(R.string.status) +  getStatus(item));
            //h.setTextViewHTML(getContext(),holder.tvCommentsText,item.text_html.toString());

            // очищаем картинку перед загрузкой чтобы она при прокрутке не мигала
            holder.ivPoster.setImageDrawable(null);
            ImageLoader.getInstance().displayImage(item.amDetails.imgPreview, holder.ivPoster);
        }
    }

    public String getStatus(ItemUserListShiki item){
        String result;
        if (!item.amDetails.anons && !item.amDetails.ongoing) result = getContext().getString(R.string.incoming);
        else if(item.amDetails.anons) result = getContext().getString(R.string.anons);
        else if(item.amDetails.ongoing) result = getContext().getString(R.string.ongoing);
        else result = null;

        return result;
    }

}