package org.shikimori.library.adapters;

/**
 * Created by LeshiyGS on 04.04.2015.
 */
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.objects.one.Studio;
import org.shikimori.library.tool.ShikiImage;

import java.util.List;

import ru.altarix.basekit.library.tools.SimpleBaseAdapter;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class StudiosAdapter extends SimpleBaseAdapter<Studio, StudiosAdapter.Holder> {

    public StudiosAdapter(Context context, List<Studio> list) {
        super(context, list, R.layout.item_shiki_studio);
    }

    @Override
    public void setValues(StudiosAdapter.Holder holder, Studio item, int position) {
//        holder.tvTitle.setText(item.name);
        ShikiImage.show(item.image, holder.ivImage);
    }

    @Override
    public Holder getViewHolder(View v) {
        Holder holder = new Holder();
        holder.ivImage = find(v, R.id.ivImage);
        holder.tvTitle = find(v, R.id.tvTitle);
        return holder;
    }

    class Holder{
        ImageView ivImage;
        TextView tvTitle;
    }

}