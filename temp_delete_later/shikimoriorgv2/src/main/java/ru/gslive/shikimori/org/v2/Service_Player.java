package ru.gslive.shikimori.org.v2;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Service_Player extends Service{

	static final String ACTION_PLAY = "ru.gslive.shikimori.org.v2.PLAY";
	static final String ACTION_CREATE = "ru.gslive.shikimori.org.v2.CREATE";
	static final String ACTION_DESTROY = "ru.gslive.shikimori.org.v2.DESTROY";
	static final String ACTION_PAUSE = "ru.gslive.shikimori.org.v2.PAUSE";
	static final String ACTION_STOP = "ru.gslive.shikimori.org.v2.STOP";
	static final String ACTION_NEXT = "ru.gslive.shikimori.org.v2.NEXT";
	static final String ACTION_PREVIOUS = "ru.gslive.shikimori.org.v2.PREVIOUS";
	static final String ACTION_PLAY_PAUSE = "ru.gslive.shikimori.org.v2.PLAY_PAUSE";
	static final String ACTION_REPIATE = "ru.gslive.shikimori.org.v2.REPIATE";
	static final String ACTION_SHAFFLE = "ru.gslive.shikimori.org.v2.SHAFFLE";
	static final String ACTION_SEEK_TO = "ru.gslive.shikimori.org.v2.SEEK_TO";
	
	MediaPlayer mMediaPlayer = null;
	ArrayList<String> TrackList = new ArrayList<String>();
	
	NotificationManager notificationManager;
	Builder jobFinishNotification;
	Notification notification;
	int Pos = 0;
	String album_link="";
	ResultReceiver receiver, receiver_pos;
	Boolean shaffle = false;
	Boolean repiate = false;
	String track ="";
	Boolean ready = true;
	PendingIntent pendingIntent;
	
	Intent preintent;
	PendingIntent preIntent;
	Intent pintent;
	PendingIntent pIntent;
	Intent nintent;
	PendingIntent nIntent;
	Intent dintent;
	PendingIntent dIntent;
	
	public static final int UPDATE_STATUS = 8345;
	public static final int NOTIFY_ID = 11111;
	public static final int SHOW_PROGRESS = 10101;
	public static final int RUN_PROGRESS = 10102;
	public static final int DESTROY_PROGRESS = 10103;
	
	protected Handler mHandler = new Handler(){
	    @Override
	    public void handleMessage(Message msg){
	    	int pos;
	    	int count;
	    	switch (msg.what){
	            case SHOW_PROGRESS:
	            	pos = mMediaPlayer.getCurrentPosition();
	            	count = mMediaPlayer.getDuration();
                    msg = obtainMessage(SHOW_PROGRESS);
                    //Log.d("position", "-> "+pos + " - " + count);
                    Bundle resultData = new Bundle();
        		    resultData.putInt("pos", pos);
        		    resultData.putInt("count", count);
        	        receiver_pos.send(UPDATE_STATUS, resultData);
	                sendMessageDelayed(msg, 1000 - (pos % 1000));
	                break;

	        }
	    }
	};
	
	@SuppressLint("InlinedApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
	    if (action.equals(ACTION_PLAY)){
	    	Pos = intent.getIntExtra("position", 0);
		    //Получаем заново список треков, если изменился альбом
	    	String[] list = intent.getStringArrayExtra("list");
			TrackList.clear();
			//Если нажали плей, значит и альбом мог поменяться..
			album_link = intent.getStringExtra("album_link");
			Intent notificationIntent = new Intent(Service_Player.this, Activity_OST_Player.class).putExtra("album_link", album_link);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			for (int i=0;i<list.length;i++){
				TrackList.add(list[i]);
			}
			if (ready) mpPlay(Pos);
	    }
	    if (action.equals(ACTION_STOP))
	        Log.d("Music Player", "Stop");
	    
	    if (action.equals(ACTION_PAUSE)){
	    	mpPause();
	    }
	    if (action.equals(ACTION_CREATE)){
	    	mpCreate((ResultReceiver) intent.getParcelableExtra("receiver"), (ResultReceiver) intent.getParcelableExtra("receiver_pos"));
	    }
	    if (action.equals(ACTION_NEXT)){
	    	mpNext();
	    }
	    if (action.equals(ACTION_PREVIOUS)){
	    	mpPrevious();
	    }
	    if (action.equals(ACTION_PLAY_PAUSE)){
	    	mpPlayPause();
	    }
	    if (action.equals(ACTION_DESTROY)){
	    	mpDestroy();
	    }
	    if (action.equals(ACTION_SEEK_TO)){
	    	mpSeekTo(intent.getIntExtra("pos_to_seek", 0));
	    }
	    if (action.equals(ACTION_REPIATE)){
	    	if (repiate){
	    		repiate = false;
	    	}else{
	    		repiate = true;
	    	}
	    	Bundle resultData = new Bundle();
	    	if (mMediaPlayer.isPlaying()){
	    		resultData.putString("status" , "play");
	    	}else{
	    		resultData.putString("status" , "pause");
	    	}
	        resultData.putBoolean("repiate" , repiate);
		    resultData.putBoolean("shaffle" , shaffle);
		    resultData.putString("track", track);
		    resultData.putInt("track_id", Pos);
	        receiver.send(UPDATE_STATUS, resultData);
	    }
	    if (action.equals(ACTION_SHAFFLE)){
	    	if (shaffle){
	    		shaffle = false;
	    	}else{
	    		shaffle = true;
	    	}
	    	Bundle resultData = new Bundle();
	    	if (mMediaPlayer.isPlaying()){
	    		resultData.putString("status" , "play");
	    	}else{
	    		resultData.putString("status" , "pause");
	    	}
	        resultData.putBoolean("repiate" , repiate);
		    resultData.putBoolean("shaffle" , shaffle);
		    resultData.putString("track", track);
		    resultData.putInt("track_id", Pos);
	        receiver.send(UPDATE_STATUS, resultData);
	    }
		return START_STICKY;
	  }
	
	public void mpSeekTo(int pos){
		mMediaPlayer.seekTo(pos);
	}
	
	public void mpDestroy(){
		mHandler.removeMessages(SHOW_PROGRESS);
		Bundle resultData = new Bundle();
	    resultData.putBoolean("service" , false);
        receiver.send(UPDATE_STATUS, resultData);
        mMediaPlayer.release();
		stopSelf();
	}
	
	public void mpPlayPause(){
		if (ready){
		if (mMediaPlayer.isPlaying()){
			mpPause();
		}else{
			mMediaPlayer.start();
			
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_music_layout);
			contentView.setImageViewResource(R.id.notifiation_image, R.drawable.ic_hardware_headphones);
			contentView.setImageViewResource(R.id.ib_play_pause, R.drawable.pause);
			contentView.setTextViewText(R.id.notification_title, track);
			contentView.setOnClickPendingIntent(R.id.ib_prev, preIntent);
			contentView.setOnClickPendingIntent(R.id.ib_play_pause, pIntent);
			contentView.setOnClickPendingIntent(R.id.ib_next, nIntent);
			contentView.setOnClickPendingIntent(R.id.ib_destroy, dIntent);
			notification.contentIntent = pendingIntent;
			notification.contentView = contentView;
			notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
			notificationManager.notify(NOTIFY_ID, notification);
			//mHandler.sendEmptyMessage(SHOW_PROGRESS);
			Bundle resultData = new Bundle();
	        resultData.putString("status" , "play");
	        resultData.putBoolean("repiate" , repiate);
		    resultData.putBoolean("shaffle" , shaffle);
		    resultData.putString("track", track);
		    resultData.putInt("track_id", Pos);
	        receiver.send(UPDATE_STATUS, resultData);
		}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void mpCreate(ResultReceiver receiver_temp, ResultReceiver receiver_pos_temp){
		 Log.d("Music Player", "Create");
		 
		 receiver = receiver_temp;
		 receiver_pos = receiver_pos_temp;     
	 
		//Отправка состояния в UI плеера
		 Bundle resultData = new Bundle();
		 if (mMediaPlayer.isPlaying()){
			 resultData.putString("status" , "play");
		 }else{
			 resultData.putString("status" , "pause");
		 }
		 resultData.putBoolean("repiate" , repiate);
		 resultData.putBoolean("shaffle" , shaffle);
         resultData.putBoolean("service" , true);
		 resultData.putString("track", track);
		 resultData.putInt("track_id", Pos);
	     receiver.send(UPDATE_STATUS, resultData);
		 
		 //Создаем Нотификацию с кнопками и action для них
		 String ns = Context.NOTIFICATION_SERVICE;
 	     notificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.ic_hardware_headphones;
		long when = System.currentTimeMillis();
		notification = new Notification(icon, "OST Player", when);
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_music_layout);
		contentView.setImageViewResource(R.id.notifiation_image, R.drawable.ic_hardware_headphones);
		contentView.setTextViewText(R.id.notification_title, "My custom notification title");
		contentView.setOnClickPendingIntent(R.id.ib_prev, preIntent);
		contentView.setOnClickPendingIntent(R.id.ib_play_pause, pIntent);
		contentView.setOnClickPendingIntent(R.id.ib_next, nIntent);
		contentView.setOnClickPendingIntent(R.id.ib_destroy, dIntent);
		notification.contentView = contentView;
		notification.contentIntent = pendingIntent;
		notification.contentView = contentView;
		notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
		//notificationManager.notify(NOTIFICATION_ID, notification);
			 
	}
	
	public void mpPause(){
		Log.d("Music Player", "Pause");
		
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_music_layout);
		contentView.setImageViewResource(R.id.notifiation_image, R.drawable.ic_hardware_headphones);
		contentView.setImageViewResource(R.id.ib_play_pause, R.drawable.play);
		contentView.setTextViewText(R.id.notification_title, track);
		contentView.setOnClickPendingIntent(R.id.ib_prev, preIntent);
		contentView.setOnClickPendingIntent(R.id.ib_play_pause, pIntent);
		contentView.setOnClickPendingIntent(R.id.ib_next, nIntent);
		contentView.setOnClickPendingIntent(R.id.ib_destroy, dIntent);
		notification.contentIntent = pendingIntent;
		notification.contentView = contentView;
		notification.contentView = contentView;
		notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
		notificationManager.notify(NOTIFY_ID, notification);
		
		mMediaPlayer.pause();
		Bundle resultData = new Bundle();
        resultData.putString("status" , "pause");
        resultData.putBoolean("repiate" , repiate);
	    resultData.putBoolean("shaffle" , shaffle);
	    resultData.putString("track", track);
	    resultData.putInt("track_id", Pos);
        receiver.send(UPDATE_STATUS, resultData);
	}
	
	public void mpPlay(int position){
		Log.d("Music Player", "Play position " + position);

		if (!mMediaPlayer.isPlaying()){
			try {
				ready = false;
				mMediaPlayer = null;
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setDataSource(TrackList.get(position));
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mMediaPlayer.prepareAsync();
				mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				    public void onPrepared(MediaPlayer mp) {
				            mp.start();
				            ready = true;
				            mHandler.sendEmptyMessage(SHOW_PROGRESS);
				        }
				    });
				mMediaPlayer.setOnCompletionListener(new OnCompletionListener(){
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						Log.d("Media Player", "Finish"); 
						mHandler.removeMessages(SHOW_PROGRESS);
						mpNext();
					}        
				});
				
				track = TrackList.get(position).substring(TrackList.get(position).lastIndexOf("/")+1);
				
				RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_music_layout);
				contentView.setImageViewResource(R.id.notifiation_image, R.drawable.ic_hardware_headphones);
				contentView.setImageViewResource(R.id.ib_play_pause, R.drawable.pause);
				contentView.setTextViewText(R.id.notification_title, track);
				contentView.setOnClickPendingIntent(R.id.ib_prev, preIntent);
				contentView.setOnClickPendingIntent(R.id.ib_play_pause, pIntent);
				contentView.setOnClickPendingIntent(R.id.ib_next, nIntent);
				contentView.setOnClickPendingIntent(R.id.ib_destroy, dIntent);
				notification.contentIntent = pendingIntent;
				notification.contentView = contentView;
				notification.contentView = contentView;
				notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
				notificationManager.notify(NOTIFY_ID, notification);
				//
				Bundle resultData = new Bundle();
		        resultData.putString("status" , "play");
		        resultData.putBoolean("repiate" , repiate);
			    resultData.putBoolean("shaffle" , shaffle);
			    resultData.putString("track", track);
			    resultData.putInt("track_id", position);
		        receiver.send(UPDATE_STATUS, resultData);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			mMediaPlayer.stop();
			mHandler.removeMessages(SHOW_PROGRESS);
			mpPlay(position);
		}
		
	}

	public void mpNext(){
		Log.d("Music Player", "Next");
		
		if (ready){
			mHandler.removeMessages(SHOW_PROGRESS);
		if (shaffle){
			final Random myRandom = new Random();
			mpPlay(myRandom.nextInt(TrackList.size()-1));
		}else{
			if (Pos < TrackList.size()-1){
				Pos++;
				mMediaPlayer.stop();
				mpPlay(Pos);
			}else{
				if (repiate){
					Pos = 0;
					mMediaPlayer.stop();
					mpPlay(Pos);
				}else{
					Toast.makeText(Service_Player.this, "Достигнут конец списка.", Toast.LENGTH_SHORT).show();
				}
			}
		}
		}
		
	}
	
	public void mpPrevious(){
		Log.d("Music Player", "Previous");
		if (ready){
		mHandler.removeMessages(SHOW_PROGRESS);
		if (shaffle){
			final Random myRandom = new Random();
			mpPlay(myRandom.nextInt(TrackList.size()-1));
		}else{
			if (Pos > 0){
				Pos--;
				mMediaPlayer.stop();
				mpPlay(Pos);
			}else{
				Toast.makeText(Service_Player.this, "Достигнуто начало списка.", Toast.LENGTH_SHORT).show();
			}
		}
		}
	}
	
	@Override
	public void onCreate(){
		mMediaPlayer = new MediaPlayer();
		preintent = new Intent(Service_Player.ACTION_PREVIOUS);
		preIntent = PendingIntent.getService(this, 0, preintent, 0);
		pintent = new Intent(Service_Player.ACTION_PLAY_PAUSE);
		pIntent = PendingIntent.getService(this, 0, pintent, 0);
		nintent = new Intent(Service_Player.ACTION_NEXT);
		nIntent = PendingIntent.getService(this, 0, nintent, 0);
		dintent = new Intent(Service_Player.ACTION_DESTROY);
		dIntent = PendingIntent.getService(this, 0, dintent, 0);
	}
	
	@Override
	public void onDestroy() {
		mHandler.removeMessages(SHOW_PROGRESS);
	    mMediaPlayer.release();
	    notificationManager.cancel(NOTIFY_ID);
	    Log.d("Media Player Service", "onDestroy");
		super.onDestroy();
	  }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


}
