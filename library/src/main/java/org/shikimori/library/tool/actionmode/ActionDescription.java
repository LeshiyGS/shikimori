package org.shikimori.library.tool.actionmode;


import android.support.v4.view.MenuItemCompat;
import android.view.Menu;

public abstract class ActionDescription {
    private int id = Menu.NONE;
    private int iconId;
    private int order = Menu.NONE;
    private int actionEnum = MenuItemCompat.SHOW_AS_ACTION_ALWAYS;
    private int titleRes = -1;
    private String title = null;

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int imgId) {
        this.iconId = imgId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getActionEnum() {
        return actionEnum;
    }

    public void setActionEnum(int actionEnum) {
        this.actionEnum = actionEnum;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public void setTitle(int titleRes) {
        this.titleRes = titleRes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract void act(int[] selectedItems);

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
