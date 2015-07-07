package org.shikimori.library.interfaces;

import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;

public class OnNewMenuListener implements PopupMenu.OnMenuItemClickListener {
    private PopupMenu menu;

    public void setMenu(PopupMenu menu) {
        this.menu = menu;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return onMenuItemClick(menu, menuItem);
    }

    public int getPosition(MenuItem menuItem){
        Menu mMenu = menu.getMenu();
        int position = 0;
        for (int i = 0; i < mMenu.size(); i++) {
            if(mMenu.getItem(i).getItemId() == menuItem.getItemId()){
                position = i;
                break;
            }
        }
        return position;
    }

    public boolean onMenuItemClick(PopupMenu menu, MenuItem menuItem) {
        return false;
    }
}