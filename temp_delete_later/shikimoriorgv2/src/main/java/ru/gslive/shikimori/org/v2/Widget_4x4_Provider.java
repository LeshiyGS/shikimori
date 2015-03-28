
package ru.gslive.shikimori.org.v2;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.RemoteViews;

public class Widget_4x4_Provider extends AppWidgetProvider {
	static String[] unread = new String[]{"0","0","0"};

	/**
	 * Custom Intent name that is used by the AlarmManager to tell us to update the clock once per second.
	 */
	public static String WIDGET_4X4_UPDATE = "ru.gslive.shikimori.org.v2.W4X4_WIDGET_UPDATE";

	static RemoteViews views;
	 	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		if (WIDGET_4X4_UPDATE.equals(intent.getAction())) {
			ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
		    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		    int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
		    for (int appWidgetID: ids) {
		    	Functions.getPreference(context);
				if (Functions.preference.kawai != null) {
					if (Functions.isNetwork(context)) new AsyncLoading(context, appWidgetManager, appWidgetID).execute();    						
				}
		    }
		}
	}
	
	private PendingIntent createClockTickIntent(Context context) {
        Intent intent = new Intent(WIDGET_4X4_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
	}
	
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
    	AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));	
	}
	
	@Override 
	public void onEnabled(Context context) {
		super.onEnabled(context);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis()+1000, 15*60*1000, createClockTickIntent(context));
	}
	
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;

		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];

			// Create an Intent to launch ExampleActivity
			Intent intent = new Intent(context, Activity_MainProfile.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,	intent, 0);

			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			views = new RemoteViews(context.getPackageName(), R.layout.widget_shiki_4x4);
			views.setOnClickPendingIntent(R.id.iv_logo, pendingIntent);
			// Tell the AppWidgetManager to perform an update on the current app
			// widget
			
			
			//-------------------------------------------------------------------------------------------------------------
			
			// RemoteViews Service needed to provide adapter for ListView
		    Intent svcIntent = new Intent(context, WidgetService.class);
		    // passing app widget id to that RemoteViews Service
		    svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		    // setting a unique Uri to the intent
		    // don't know its purpose to me right now
		    svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
		    // setting adapter to listview of the widget
		    views.setRemoteAdapter(appWidgetId, R.id.lv_widget,
		            svcIntent);
		    // setting an empty view in case of no data
		    //views.setEmptyView(R.id.lv_widget, R.id.empty_view);

		    
		    appWidgetManager.updateAppWidget(appWidgetId, views);
			Functions.getPreference(context);
			if (Functions.preference.kawai != null) {
				if (Functions.isNetwork(context)) new AsyncLoading(context, appWidgetManager, appWidgetId).execute();    						
			}
		}
	}
	
	//
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	static class AsyncLoading extends AsyncTask<Void, Void, Void> {
		private Context context;	  
		private AppWidgetManager appWidgetManager;
		private int appWidgetId;
		private Bitmap avatar;
		
		public AsyncLoading(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
	        this.context = context;
	        this.appWidgetManager = appWidgetManager;
	        this.appWidgetId = appWidgetId;
	    }
		
		@Override
		protected void onPreExecute() {
			unread = new String[]{"0","0","0"};	
			Functions.getPreference(context);
		}
				  
		@SuppressWarnings("deprecation")
		protected void onPostExecute(Void result1) {
			String temp = String.valueOf(Integer.parseInt(unread[0]) + Integer.parseInt(unread[1]) + Integer.parseInt(unread[2]));
			views = new RemoteViews(context.getPackageName(), R.layout.widget_shiki_4x4);
			views.setTextViewText(R.id.tv_unread, temp);
			views.setTextViewText(R.id.tv_nickname, Functions.preference.login);
			views.setRemoteAdapter(appWidgetId, R.id.lv_widget, null);
			views.setImageViewBitmap(R.id.iv_logo, avatar);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			if (Functions.isNetwork(context)) unread = SSDK_API.getUnread(Functions.preference.kawai);
			try {
				avatar = BitmapFactory.decodeStream((InputStream)new URL(Functions.preference.avatar).getContent());	        
		    } catch (Exception e) {
		       
		    }
			return null;
		}	
	}
	
}
