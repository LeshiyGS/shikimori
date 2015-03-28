package ru.gslive.shikimori.org.v2;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class Service_Notification extends Service {

    Notification notification;
	NotificationManager notificationManager;
	
	public String[] unread = new String[]{"0","0","0"};
	
	private static final int NOTIFY_ID = 0x1001;

	//Асинхронная загрузка
    AsyncTask<Void, Void, Void> loader;
	
		Handler handler = new Handler();
	 
		class Update extends TimerTask {
			@Override
			public void run() {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						if (Functions.preference.kawai != null) {
							if (Functions.isNetwork(getBaseContext())) loader = new AsyncLogin().execute();
        				}
					}
				});
			}
		}
	
		@Override
		public IBinder onBind(Intent intent) {
		return null;
		}

		@Override
		public void onCreate() {
					}

		@Override
		public void onDestroy() {

		}

		@SuppressWarnings("deprecation")
		@Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
			Functions.getPreference(getBaseContext());
            notification = new Notification(R.drawable.ic_launcher, getBaseContext().getString(R.string.app_name), System.currentTimeMillis());
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Timer timer1;
			timer1 = new Timer();
			timer1.schedule(new Update(),Functions.preference.notify_time,Functions.preference.notify_time);
            return START_STICKY;
	    }

		//Асинхронная загрузка (Логинимся и получаем информацию о пользователе)
		private class AsyncLogin extends AsyncTask<Void, Void, Void> {
		  
		  	@Override
		  	protected void onPreExecute() {
		  		unread = new String[]{"0","0","0"};	
			}
			  
		  	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            protected void onPostExecute(Void result1) {
		  		String[] unread_temp = new String[]{"0","0","0"};
                Context context = getApplicationContext();

                Functions.getPreference(context);

		  		unread_temp[0] = Functions.preference.unread_temp.split(";")[0];
		  		unread_temp[1] = Functions.preference.unread_temp.split(";")[1];
		  		unread_temp[2] = Functions.preference.unread_temp.split(";")[2];

		  		if (!(unread[0].equals("0") && unread[1].equals("0") && unread[2].equals("0"))){
		  			if (!(unread[0].equals(unread_temp[0]) && unread[1].equals(unread_temp[1]) && unread[2].equals(unread_temp[2]))){

                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
                            Intent notificationIntent = new Intent(context, Activity_MainProfile.class);
                            PendingIntent contentIntent = PendingIntent.getActivity(context,
                                    0, notificationIntent,
                                    PendingIntent.FLAG_CANCEL_CURRENT);

                            Notification.Builder builder = new Notification.Builder(context);

                            builder.setContentIntent(contentIntent)
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setTicker(context.getString(R.string.app_name))
                                    .setWhen(System.currentTimeMillis())
                                    .setAutoCancel(true)
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                                    .setContentTitle(context.getString(R.string.app_name))
                                    .setContentText("Новости: " +  unread[0] + ";   Письма: " +  unread[1] + ";   Уведомления: " + unread[2] + ";"); // Текст уведомленимя


                            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN) {
                                notification = builder.build();
                            }else{
                                notification = builder.getNotification();
                            }

                            NotificationManager notificationManager = (NotificationManager) context
                                    .getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(NOTIFY_ID, notification);
                        }else {
                            Intent notificationIntent = new Intent(Service_Notification.this, Activity_MainProfile.class);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                                    notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

                            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.unread_notify);
                            contentView.setTextViewText(R.id.tv_title, context.getString(R.string.app_name));
                            contentView.setTextViewText(R.id.tv_message, "Новости: " +  unread[0] + ";   Письма: " +  unread[1] + ";   Уведомления: " + unread[2] + ";");
                            notification.contentView = contentView;
                            notification.contentIntent = pendingIntent;
                            notification.flags |= Notification.FLAG_AUTO_CANCEL;
                            notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
                            notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
                            notification.defaults |= Notification.DEFAULT_SOUND;
                            notificationManager.notify(NOTIFY_ID, notification);
                        }

		  				SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
						Editor editor = mSettings.edit();
                        editor.putString(Constants.APP_PREFERENCES_UNREAD, unread[0]+";"+unread[1]+";"+unread[2]);
                        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
							editor.apply();
						}else {
							editor.commit();
						}
		  				
		  			}
		  		}
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				if (Functions.isNetwork(getBaseContext())) unread = SSDK_API.getUnread(Functions.preference.kawai);
				return null;
			}

			
	  }
	}