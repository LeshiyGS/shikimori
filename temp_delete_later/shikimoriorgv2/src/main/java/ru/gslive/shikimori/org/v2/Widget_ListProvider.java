package ru.gslive.shikimori.org.v2;

import java.util.ArrayList;


import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Widget_ListProvider implements RemoteViewsFactory {
	private ArrayList<Widget_ListItem> listItemList = new ArrayList<Widget_ListItem>();
	private Context context = null;
	@SuppressWarnings("unused")
	private int appWidgetId;

	public Widget_ListProvider(Context context, Intent intent) {
		this.context = context;
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		populateListItem();
	}

	private void populateListItem() {
		for (int i = 0; i < 10; i++) {
			Widget_ListItem listItem = new Widget_ListItem();
			listItem.heading = "Heading" + i;
			listItem.content = i
					+ " This is the content of the app widget listview.Nice content though";
			listItemList.add(listItem);
		}

	}

	@Override
	public int getCount() {
		return listItemList.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 *Similar to getView of Adapter where instead of View
	 *we return RemoteViews 
	 * 
	 */
	@Override
	public RemoteViews getViewAt(int position) {
		final RemoteViews remoteView = new RemoteViews(
				context.getPackageName(), R.layout.list_row);
		Widget_ListItem listItem = listItemList.get(position);
		remoteView.setTextViewText(R.id.heading, listItem.heading);
		remoteView.setTextViewText(R.id.content, listItem.content);

		return remoteView;
	}


	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
	}

	@Override
	public void onDestroy() {
	}

}