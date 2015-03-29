package org.shikimori.client.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.shikimori.client.R;

import ru.altarix.ui.tool.h;

public class DrawerAdapter extends ArrayAdapter<DrawerAdapter.Item> {
    public static final int DRAWER_MENU_MAIN_ID = 4555;
    public static final int DRAWER_MENU_PROFILE_ID = 4556;
    public static final int DRAWER_MENU_ANIME_ID = 4557;
    public static final int DRAWER_MENU_MANGA_ID = 4559;
    public static final int DRAWER_MENU_CALENDAR_ID = 4560;
    public static final int DRAWER_MENU_NEWS_ID = 4561;
    public static final int DRAWER_MENU_COMUNITY_ID = 4562;
    public static final int DRAWER_MENU_SETTINGS_ID = 4563;
    public static final int DRAWER_MENU_ABOUT_ID = 4564;

    private Context mContext;
    private LayoutInflater inflater;
    private int selectedPos = 0;

    @SuppressWarnings("deprecation")
    public DrawerAdapter(Context context) {
        super(context, 0);
        mContext = context;
        initData();
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item elem = getItem(position);
        View view = convertView;
        ViewHolder holder;
        if (convertView == null) {
            if (position != 0) {
                view = inflater.inflate(R.layout.item_shiki_drawer_menu, parent, false);
            } else {
                view = inflater.inflate(R.layout.item_shiki_drawer_menu_profile, null);
            }
            holder = getViewHolder(view);
            view.setTag(holder);
            h.setFont(mContext, view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        view.setId(elem.id);
        // set menu name
        holder.tvTitle.setText(elem.title);
        // set icon
        if (elem.icon != 0)
            holder.icon.setImageResource(elem.icon);

        // set selection item
        if(position==selectedPos){
            view.setBackgroundColor(mContext.getResources().getColor(R.color.greenColor));
            //holder.tvTitle.setTextColor(Color.WHITE);
        } else {
            view.setBackgroundColor(0);
            //holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.altarixUiTextColor));
        }

        return view;
    }

    public void setSelected(int position){
        if(position<0 || position>=getCount()) return;

        selectedPos = position;
        notifyDataSetChanged();
    }

    /**
     * Генерим holder
     * @param view
     * @return
     */
    private ViewHolder getViewHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        holder.icon = (ImageView) view.findViewById(R.id.icon);
        return holder;
    }

    public int getSelectedPosition() {
        return selectedPos;
    }

    static class ViewHolder {
        TextView tvTitle;
        ImageView icon;
    }

    /**
     * Пунк меню
     */
    public static class Item{
        int icon,id,title;

        public Item(int id, int title, int icon){
            this.id    = id;
            this.title = title;
            this.icon  = icon;
        }
    }

    /**
     * Заполняем список менюшками
     */
    private void initData(){
        add(new Item(DRAWER_MENU_PROFILE_ID, R.string.user_name, R.mipmap.ic_no_avatar));
//        add(new Item(DRAWER_MENU_PROFILE_ID, R.string.profile, R.drawable.ic_drawer_profile));
        add(new Item(DRAWER_MENU_ANIME_ID, R.string.anime, R.drawable.ic_drawer_news));
        add(new Item(DRAWER_MENU_MANGA_ID, R.string.manga, R.drawable.ic_drawer_news));
        add(new Item(DRAWER_MENU_CALENDAR_ID, R.string.calendar, R.drawable.ic_drawer_calendar));
        add(new Item(DRAWER_MENU_NEWS_ID, R.string.newsanddiscus, R.drawable.ic_drawer_news));
        add(new Item(DRAWER_MENU_COMUNITY_ID, R.string.community, R.drawable.ic_drawer_comunity));
        add(new Item(DRAWER_MENU_SETTINGS_ID, R.string.settings, R.drawable.ic_drawer_settings));
        add(new Item(DRAWER_MENU_ABOUT_ID, R.string.about, R.drawable.ic_drawer_about));
    }

}
