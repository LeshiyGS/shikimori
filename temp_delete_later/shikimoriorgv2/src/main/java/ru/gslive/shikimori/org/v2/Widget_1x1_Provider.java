
package ru.gslive.shikimori.org.v2;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.RemoteViews;

public class Widget_1x1_Provider extends AppWidgetProvider {

	Handler handler = new Handler();
	 	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		if (Constants.WIDGET_1X1_UPDATE.equals(intent.getAction())) {
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
        Intent intent = new Intent(Constants.WIDGET_1X1_UPDATE);
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
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_shiki_1x1);
			views.setOnClickPendingIntent(R.id.iv_logo, pendingIntent);
			// Tell the AppWidgetManager to perform an update on the current app
			// widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
			Functions.getPreference(context);

			if (Functions.preference.kawai != null) {
				if (Functions.isNetwork(context)) new AsyncLoading(context, appWidgetManager, appWidgetId).execute();    						
			}
		}
	}
	
	
	
	//
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
			Functions.getPreference(context);
		}
				  
		protected void onPostExecute(Void result1) {
			
			RemoteViews updateViews = new RemoteViews(context.getPackageName(),	R.layout.widget_shiki_1x1);
			updateViews.setTextViewText(R.id.tv_unread, String.valueOf(Functions.count_unread));
			if (Functions.count_unread == 0){
				updateViews.setViewVisibility(R.id.tv_unread, View.GONE);
			}else{
				updateViews.setViewVisibility(R.id.tv_unread, View.VISIBLE);
			}
			updateViews.setTextViewText(R.id.tv_nickname, Functions.preference.login);
			updateViews.setImageViewBitmap(R.id.iv_logo, avatar);
			appWidgetManager.updateAppWidget(appWidgetId, updateViews);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			if (Functions.isNetwork(context)) SSDK_API.getUnread(Functions.preference.kawai);
			try {
				avatar = BitmapFactory.decodeStream((InputStream)new URL(Functions.preference.avatar).getContent());	        
		    } catch (Exception e) {
		       
		    }
			return null;
		}	
	}
	
}
