package org.shikimori.library.tool.actionmode;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.shikimori.library.R;

public class ActionSelectCallBack extends DestroyActionCallback {
    private static final int SELECT_ALL_ID = 0;

    private SelectItemsAdapter adapter;
    private boolean allSelected = false;
    private Context context;
    private ListAdapter itemAdapter;
    private ListView listview;
    private ActionDescription[] actionsDesc;

    public ActionSelectCallBack(Context context, ListView listview, ListAdapter itemAdapter, DestroyAction destroyAction, ActionDescription... actionsDesc) {
        super(destroyAction);

        this.context = context;
        this.itemAdapter = itemAdapter;
        this.listview = listview;
        this.actionsDesc = (actionsDesc == null) ? new ActionDescription[0] : actionsDesc;
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        adapter = new SelectItemsAdapter(context, itemAdapter, mode);
        listview.setAdapter(adapter);

        mode.setTitle(context.getString(R.string.elk_checked, 0));
        MenuItem item = menu.add(Menu.NONE, SELECT_ALL_ID, SELECT_ALL_ID, R.string.elk_mark_all);
        item.setIcon(R.drawable.elk_select_all);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        for (ActionDescription desc : actionsDesc) {
            item = menu.add(Menu.NONE, desc.getId(), desc.getOrder(), desc.getTitleRes());
            item.setIcon(desc.getIconId());
            MenuItemCompat.setShowAsAction(item, desc.getActionEnum());
        }

        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == SELECT_ALL_ID) {
            allSelected = !allSelected;
            adapter.setAllChecked(allSelected);
            return true;
        }

        for (ActionDescription desc : actionsDesc)
            if (desc.getId() == itemId) {
                int[] selected = adapter.getChecked();
                mode.finish();
                if (selected != null && selected.length > 0)
                    desc.act(selected);

                return true;
            }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        super.onDestroyActionMode(mode);

        listview.setAdapter(itemAdapter);
        adapter = null;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

}
