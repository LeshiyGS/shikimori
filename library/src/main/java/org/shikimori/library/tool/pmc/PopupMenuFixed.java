package org.shikimori.library.tool.pmc;

import android.content.Context;
import android.view.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Владимир on 12.02.2015.
 */
public class PopupMenuFixed {

    private final Context mContext;
    private View v;
    private OnItemClick l;
    private int menu;
    private int[] ids;
    private List<MenuItemM> listItems = new ArrayList<>();

    private class MenuItemM{
        public int id, text;
        private MenuItemM(int id, int text) {
            this.id = id;
            this.text = text;
        }

    }

    public void setTitle(int id, int text) {
        listItems.add(new MenuItemM(id, text));
    }

    public interface OnItemClick{
        public boolean onItemClick(int id);
    }

    public void menuInflate(int menu){
        this.menu = menu;
    }

    public PopupMenuFixed(Context mContext, View v){
        this.mContext = mContext;
        this.v = v;
    }

    public void setOnMenuItemClickListener(OnItemClick l){
        this.l = l;
    }

    public void show(){
        initDialog();
    }

    private void initDialog() {
        PopupMenuCompat popMenu = PopupMenuCompat.newInstance(mContext, v);
        popMenu.inflate(menu);
        for (MenuItemM t : listItems)
            popMenu.getMenu().findItem(t.id).setTitle(t.text);
        if(ids!=null)
            for (int in : ids)
                popMenu.getMenu().findItem(in).setVisible(false);
        popMenu.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(l!=null)
                    return l.onItemClick(item.getItemId());
                return false;
            }
        });
        popMenu.show();
    }

    public void hideMenu(int ... ids){
        this.ids = ids;
    }
}
