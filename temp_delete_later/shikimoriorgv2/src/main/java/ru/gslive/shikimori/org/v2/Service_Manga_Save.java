package ru.gslive.shikimori.org.v2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Service_Manga_Save extends Service {
		
	String links;
	String[] list_link;
	int pb_sel = 0;
	boolean stop_save = false;
	
	Intent notificationIntent;
    PendingIntent contentIntent;
    String ns;
    NotificationManager notificationManager;
    final int JOBFINISH_ID = 1;
    final int JOBFINISH_ID_2 = 2;
    Notification noti;
    int NOTI_ID = 11111;


		@Override
		public IBinder onBind(Intent intent) {
		return null;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onCreate() {
		Toast.makeText(this, "Загрузка манги начата", Toast.LENGTH_SHORT).show();
		CharSequence contentTitle = "Загрузка манги";
        long when = System.currentTimeMillis();
		noti = new Notification(android.R.drawable.stat_sys_download, contentTitle, when);
		}

		@Override
		public void onDestroy() {
		//Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();

		}
					
		@Override
		public void onStart(Intent intent, int startid) {
			Functions.getPreference(getBaseContext());

			notificationIntent = new Intent(this, Activity_MainProfile.class);
		    contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		    ns = Context.NOTIFICATION_SERVICE;
		    notificationManager = (NotificationManager) getSystemService(ns);
			
			links = intent.getExtras().getString("links");
		
			list_link = links.split("GLS");

			stop_save = false;
			
			for(String src : list_link){
				new ImageDownloadAsyncTask(src).execute();
			}
			
			noti.flags |= Notification.FLAG_AUTO_CANCEL;
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notify);
            contentView.setTextViewText(R.id.status_text, "Загрузка манги");
            contentView.setTextViewText(R.id.tv_file, "Загружено " + pb_sel + " из " + list_link.length);
            contentView.setProgressBar(R.id.status_progress, list_link.length, pb_sel, false);
            noti.contentView = contentView;
            noti.contentIntent = contentIntent;
            notificationManager.notify(NOTI_ID, noti);

		}
		
		@SuppressWarnings("deprecation")
		public void stop(){
			CharSequence contentTitle = "Загрузка завершена";
	        long when = System.currentTimeMillis();
			noti = new Notification(android.R.drawable.stat_sys_download_done, contentTitle, when);
			noti.flags |= Notification.FLAG_AUTO_CANCEL;
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notify);
            contentView.setTextViewText(R.id.status_text, "Загрузка манги завершена");
            contentView.setTextViewText(R.id.tv_file,  "Загружено " + list_link.length + " из " + list_link.length);
            contentView.setProgressBar(R.id.status_progress, list_link.length, list_link.length, false);
            noti.contentView = contentView;
            noti.contentIntent = contentIntent;
            notificationManager.notify(NOTI_ID, noti);

			this.stopSelf(); 
		}
		
		class ImageDownloadAsyncTask extends AsyncTask<Void, Void, Void> {
			private String source;


		    public ImageDownloadAsyncTask(String source) {
		        this.source = source;

		    }

		    @Override
			  protected void onPreExecute() {
				 
		    }
		    
		    @Override
		    protected Void doInBackground(Void... params) {
		    	@SuppressWarnings("unused")
				String pic_link ="";
		    	String pic_html ="";
		    	ArrayList<String> page_link = new ArrayList<String>();


				Document doc = null;
				try {
					doc = Jsoup
						    .connect(source)
						    .userAgent("Mozilla")
						    .referrer("http://shikimori.org/")
						    .get();
					if (doc.select("div[class^=pageBlock reader]").size()>0){
						pic_html = doc.select("div[class^=pageBlock reader]").first().html();
						pic_html = pic_html.substring(pic_html.indexOf("["),pic_html.indexOf("]"));
						pic_html = pic_html.replaceAll("\",w", "#");
						for (String src : pic_html.split("#")){
							if (src.contains("http")){
								page_link.add(src.substring(src.indexOf("http"), src.length()));
						
							}
						}
						Elements img = doc.select("img[id=mangaPicture]");
						for (Element src : img){
							pic_link = src.attr("abs:src");
						}
					}else{
						doc = Jsoup
							    .connect(source+"?mature=1")
							    .userAgent("Mozilla")
							    .referrer("http://shikimori.org/")
							    .get();
						pic_html = doc.select("div[class^=pageBlock reader]").first().html();
						pic_html = pic_html.substring(pic_html.indexOf("["),pic_html.indexOf("]"));
						pic_html = pic_html.replaceAll("\",w", "#");
						for (String src : pic_html.split("#")){
							if (src.contains("http")){
								page_link.add(src.substring(src.indexOf("http"), src.length()));
						
							}
						}
						Elements img = doc.select("img[id=mangaPicture]");
						for (Element src : img){
							pic_link = src.attr("abs:src");
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				int i = 1;
				for(String src : page_link){
					String temp_dir = source.split("http://")[1];
					temp_dir = temp_dir.substring(temp_dir.indexOf("/"), temp_dir.length());
					//Log.d("Saving ", "Saving page " + src + " on " + temp_dir);
					if (!stop_save){
						Functions.download(getBaseContext(), i+".jpg", Functions.preference.cache_dir + "/shikimori/manga/" + temp_dir, src);
					}else{
						break;
					}
					i++;
				}

				pb_sel++;
		    	return null;
		    }

		    @Override
		    protected void onPostExecute(Void result) {
		    	
		    	noti.flags |= Notification.FLAG_AUTO_CANCEL;
	            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notify);
	            contentView.setTextViewText(R.id.status_text, "Загрузка манги");
	            contentView.setTextViewText(R.id.tv_file, "Загружено " + pb_sel + " из " + list_link.length);
	            contentView.setProgressBar(R.id.status_progress, list_link.length, pb_sel, false);
	            noti.contentView = contentView;
	            noti.contentIntent = contentIntent;
	            notificationManager.notify(NOTI_ID, noti);
			    
			    if (pb_sel == list_link.length){
			    	stop();
			    }

		    }
		    
		}

}