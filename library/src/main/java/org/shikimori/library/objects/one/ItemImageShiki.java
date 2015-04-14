package org.shikimori.library.objects.one;

import android.view.View;

import org.shikimori.library.tool.ProjectTool;


/**
 * Created by Феофилактов on 12.04.2015.
 */
public class ItemImageShiki {
    private String thumb, original;
    private View.OnClickListener clickListener;

    public ItemImageShiki() {
    }

    public ItemImageShiki(String thumb, String original) {
        setThumb(thumb);
        setOriginal(original);
    }

    public String getThumb() {
        return thumb;
    }

    public String getOriginal() {
        return original == null ? thumb : original;
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setThumb(String thumb) {
        this.thumb = ProjectTool.fixUrl(thumb);
    }

    public void setOriginal(String original) {
        this.original = ProjectTool.fixUrl(original);
    }

    public View.OnClickListener getClickListener() {
        return clickListener;
    }
}
