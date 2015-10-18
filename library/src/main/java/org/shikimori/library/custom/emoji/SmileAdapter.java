package org.shikimori.library.custom.emoji;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.shikimori.library.R;

import java.util.List;

import ru.altarix.basekit.library.tools.SimpleBaseAdapter;

/**
 * Created by Феофилактов on 18.10.2015.
 */
public class SmileAdapter extends SimpleBaseAdapter<SmileItem, SmileAdapter.Holder> {


    public SmileAdapter(Context context, List<SmileItem> list) {
        super(context, list, R.layout.item_smiley);
    }

    @Override
    public void setValues(Holder holder, SmileItem smileItem, int i) {
        holder.ivImage.setImageResource(0);
        ImageLoader.getInstance().displayImage(smileItem.path, holder.ivImage);
    }

    @Override
    public Holder getViewHolder(View view) {
        Holder holder = new Holder();
        holder.ivImage = find(view, R.id.ivImage);
        return holder;
    }

    class Holder {
        ImageView ivImage;
    }

}
