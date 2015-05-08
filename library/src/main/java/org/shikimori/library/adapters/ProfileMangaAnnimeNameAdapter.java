package org.shikimori.library.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.adapters.base.BaseListAdapter;
import org.shikimori.library.adapters.base.SimpleBaseAdapter;
import org.shikimori.library.adapters.holder.AnimeMangaNamesHolder;
import org.shikimori.library.adapters.holder.MessageHolder;
import org.shikimori.library.objects.one.AnimeManga;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.h;

import java.util.Date;
import java.util.List;

/**
 * Created by Феофилактов on 04.04.2015.
 */
public class ProfileMangaAnnimeNameAdapter extends SimpleBaseAdapter<AnimeManga, AnimeMangaNamesHolder> {

    private ProjectTool.TYPE type;

    public ProfileMangaAnnimeNameAdapter(Context context, List list, ProjectTool.TYPE type) {
        super(context, list, R.layout.item_shiki_profile_anime_manga_list);
        this.type = type;
    }

    @Override
    public AnimeMangaNamesHolder getViewHolder(View v) {
        AnimeMangaNamesHolder holder = new AnimeMangaNamesHolder();
        holder.tvName = (TextView) v.findViewById(R.id.tvName);
        holder.tvCount = (TextView) v.findViewById(R.id.tvCount);
        return holder;
    }

    @Override
    public void setValues(AnimeMangaNamesHolder holder, AnimeManga item, int position) {
        holder.tvCount.setText(String.valueOf(item.counted));
        holder.tvName.setText(ProjectTool.getListStatusName(
                getContext(), item.name, type));
    }
}
