package org.shikimori.library.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import org.shikimori.library.R;
import org.shikimori.library.objects.ItemScreenShot;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.hs;

import java.util.List;

import ru.altarix.basekit.library.activities.BaseKitActivity;
import ru.altarix.basekit.library.tools.SimpleBaseAdapter;

/**
 * Created by Владимир on 06.08.2015.
 */
public class ScreenShotAdapter extends SimpleBaseAdapter<ItemScreenShot, ScreenShotAdapter.Holder> {

    public ScreenShotAdapter(Context context, List<ItemScreenShot> list) {
        super(context, list, R.layout.view_shiki_screenshot);
    }

    @Override
    public void setListeners(Holder holder) {
        super.setListeners(holder);
        holder.img.setOnTouchListener(hs.getImageHighlight);
    }

    @Override
    public void setValues(Holder holder, ItemScreenShot itemScreenShot, int i) {
        holder.img.setTag(i);
        ShikiImage.show(itemScreenShot.getPreview(), holder.img);
    }

    @Override
    public Holder getViewHolder(View view) {
        Holder holder = new Holder();
        holder.img = find(view, R.id.ivBigImage);
        return holder;
    }

    public class Holder {
        ImageView img;
    }

}
