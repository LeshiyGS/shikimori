package org.shikimori.library.tool.actionmode;

import android.content.Context;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;

import org.shikimori.library.R;

public class SelectItemsAdapter extends BaseAdapter {
    private final Context context;
    private LayoutInflater inflater;
    private ListAdapter itemAdapter;
    private SparseBooleanArray checked;
    private int checkedCount;
    private ActionMode actionMode;

    public SelectItemsAdapter(Context context, ListAdapter itemAdapter, ActionMode actionMode) {
        this.context = context;
        this.actionMode = actionMode;
        this.inflater = LayoutInflater.from(context);
        this.itemAdapter = itemAdapter;
        this.checked = new SparseBooleanArray(itemAdapter.getCount());
        this.checkedCount = 0; 
    }

    @Override
    public int getCount() {
        return itemAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return itemAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return itemAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SelectItemHolder holder;
        if (convertView == null) {
            View view = inflater.inflate(R.layout.shiki_item_select, parent, false);
            holder = new SelectItemHolder();
            holder.checkBox = (CheckBox) view.findViewById(R.id.cbDelete);
            holder.itemView = (ViewGroup) view.findViewById(R.id.flItem);
            view.setTag(holder);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean isChecked = ((CheckBox) v).isChecked();
                    checked.put((Integer) holder.checkBox.getTag(), isChecked);
                    checkedCount += (isChecked) ? 1 : -1;
                    actionMode.setTitle(context.getString(R.string.elk_checked,checkedCount));
                }
            });

            convertView = view;
        } else {
            holder = (SelectItemHolder) convertView.getTag();
        }

        holder.itemView.removeAllViews();
        holder.itemView.addView(itemAdapter.getView(position, holder.itemView.getChildAt(0), holder.itemView), 0);
        holder.checkBox.setTag(position);
        holder.checkBox.setChecked(checked.get(position));

        return convertView;
    }

    public class SelectItemHolder {
        CheckBox checkBox;
        ViewGroup itemView;
    }

    public void setAllChecked(boolean value) {
        int size = itemAdapter.getCount();
        for (int i = 0; i < size; i++)
            checked.put(i, value);
        checkedCount = (value) ? size : 0;
        actionMode.setTitle(context.getString(R.string.elk_checked,checkedCount));
        notifyDataSetChanged();
    }


    public int[] getChecked() {
        int size = checked.size();
        int[] checkedPos = new int[checkedCount];
        int k = 0;
        for (int i = 0; i < size; i++) {
            int pos = checked.keyAt(i);
            if (checked.get(pos))
                checkedPos[k++] = pos;

            if (k == checkedCount) break;
        }

        return checkedPos;
    }

}
