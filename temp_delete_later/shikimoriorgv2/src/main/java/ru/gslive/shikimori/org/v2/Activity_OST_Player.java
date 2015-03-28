package ru.gslive.shikimori.org.v2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.actionbarsherlock.view.Window;
import com.koushikdutta.ion.Ion;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_OST_Player extends ShikiSherlockActivity {
	
	String album_link;
	int play_position = 0;
	String status = "stop";
	String ost_title = "";
	String cart="";
	boolean repiate = false;
	boolean random = false;
	boolean service = false;
	boolean ui=false;
	
	ListView lv_track;
	TextView lbl_track;
	ImageView iv_cart;
	ImageButton btn_play_pause, btn_repiate, btn_random;
	SeekBar pb_track;
	
	private AsyncTrackList loader;
	
	TrackAdapter adapter;
	
	Intent notificationIntent;
    PendingIntent contentIntent;
	String ns;
	NotificationManager notificationManager;
	Notification noti;

	String[] list;
	String track_name = "";
	ArrayList<String> track_link = new ArrayList<String>();
	ArrayList<String> track = new ArrayList<String>();
	ArrayList<Boolean> track_download = new ArrayList<Boolean>();
		
	private class DownloadReceiver extends ResultReceiver{
	    public DownloadReceiver(Handler handler) {
	        super(handler);
	    }

	    @Override
	    protected void onReceiveResult(int resultCode, Bundle resultData) {
	        super.onReceiveResult(resultCode, resultData);
	        if (resultCode == Service_Download_OST.UPDATE_PROGRESS) {
	            int progress = resultData.getInt("progress");
	            int note_id = resultData.getInt("note_id");
	            String note_name = resultData.getString("note_name");
	            
	            noti.flags |= Notification.FLAG_AUTO_CANCEL;
	            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notify);
	            contentView.setTextViewText(R.id.status_text, "Трек: " + note_name);
	            contentView.setTextViewText(R.id.tv_file, "Загружено " + progress + "%");
	            contentView.setProgressBar(R.id.status_progress, 100, progress, false);
	            noti.contentView = contentView;
	            noti.contentIntent = null;
	            notificationManager.notify(note_id, noti);
        		if (progress == 100){
        			startLoadingTrackList();
        		}
	        }
	    }
	}
	
	private class PosReceiver extends ResultReceiver{
	    public PosReceiver(Handler handler) {
	        super(handler);
	    }

	    @Override
	    protected void onReceiveResult(int resultCode, Bundle resultData) {
	        super.onReceiveResult(resultCode, resultData);
	        if (resultCode == Service_Player.UPDATE_STATUS) {
	        	pb_track.setProgress(resultData.getInt("pos"));
	        	pb_track.setMax(resultData.getInt("count"));
	        }
	    }
	}
	
	private class StatusReceiver extends ResultReceiver{
	    public StatusReceiver(Handler handler) {
	        super(handler);
	    }

	    @Override
	    protected void onReceiveResult(int resultCode, Bundle resultData) {
	        super.onReceiveResult(resultCode, resultData);
	        if (resultCode == Service_Player.UPDATE_STATUS) {
	            String status = resultData.getString("status");
	            if (status == null) status = "stop";
	           
	            track_name = resultData.getString("track");
	            if (track_name == null) track_name = "";
	            boolean repiate = resultData.getBoolean("repiate", false);
	            boolean shaffle = resultData.getBoolean("shaffle", false);
	            play_position = resultData.getInt("track_id", 0);
	            service = resultData.getBoolean("service", true);
	           
	            if (status.equals("play")){
	            	btn_play_pause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
	            }else{
	            	btn_play_pause.setImageDrawable(getResources().getDrawable(R.drawable.play));
	            }
	            if (!shaffle){
	    			btn_random.setImageDrawable(getResources().getDrawable(R.drawable.shuffle));
	    		}else{
	    			btn_random.setImageDrawable(getResources().getDrawable(R.drawable.shuffle));
	    		}
	            if (!repiate){
	    			btn_repiate.setImageDrawable(getResources().getDrawable(R.drawable.repeat));
	    		}else{
	    			btn_repiate.setImageDrawable(getResources().getDrawable(R.drawable.repeat));
	    		}
	            lv_track.smoothScrollToPosition(play_position);
	            lbl_track.setText(track_name);
	            adapter.notifyDataSetChanged();
	            if (service == false) finish();
	        }
	    }
	}
	
	//
	public class TrackAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public TrackAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}

		@Override
		public int getCount() {
			return track.size(); 
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;
			 
			if (convertView == null) {
		    	  holder = new Functions.ViewHolder();
		    	  convertView = mLayoutInflater.inflate(R.layout.item_ost_player, null);
		    	  holder.textView = (TextView) convertView.findViewById(R.id.lbl_track_name);
				  holder.imageView = (ImageView) convertView.findViewById(R.id.ib_track_save);
			      convertView.setTag(holder);
			} else {
				holder = (Functions.ViewHolder) convertView.getTag();
			}

			convertView.findViewById(R.id.ib_track_save).setOnClickListener(new OnClickListener() {    	
				
	        	@Override
	            public void onClick(View v) {
	        		if (!track_download.get(position)){
		        		final Random rand = new Random();
		            	int diceRoll = rand.nextInt(100000);
		                Intent intent = new Intent(Activity_OST_Player.this, Service_Download_OST.class);
		                intent.putExtra("url", track_link.get(position));
		                intent.putExtra("type", "ost/"+ost_title);
		                intent.putExtra("name", track.get(position));
		                intent.putExtra("receiver", new DownloadReceiver(new Handler()));
		                intent.putExtra("note_id", diceRoll);
		                startService(intent);
	        		}
	            }
	        	});
				    
		    if (play_position == position && track_name.equals(track.get(position))){
		    	holder.textView.setText(Html.fromHtml("<b>► " + track.get(position)+"</b>"));
		    }else{
		    	holder.textView.setText(track.get(position));
		    }
		    if (track_download.get(position)){
				holder.imageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_save));
		    }else{
		    	holder.imageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
		    }
			return convertView;
		}
	}
	
	public void loadlist(){
		try {
			Document doc = Jsoup
				    .connect(album_link)
				    .userAgent("Mozilla")
				    .get();
			Elements title = doc.select("div[class=content-block-title]");
			for (Element src : title){
				ost_title = src.text().trim().replaceAll("/", "|");
			}
			Elements img = doc.select("img[height=300]");
			for (Element src : img){
				cart = src.attr("abs:src");
			}
			Elements table = doc.select("table[class=tracklist]");
			for (Element src_table : table){
				Elements row = src_table.select("tr");
				for (Element src_row : row){
					Elements link = src_row.select("a[href]");
					for (Element src : link){
						if (!src.attr("href").equals("#")){
							track_link.add(src.attr("abs:href"));
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Functions.getPreference(getBaseContext());

		setTheme(R.style.AppTheme_ActionBarStyle_BackHome);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setSubtitle("OST");
		
		super.onCreate(savedInstanceState);
		int rotate = getWindowManager().getDefaultDisplay().getRotation();
		if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
			setContentView(R.layout.ost_tablet_player);
			iv_cart = (ImageView) findViewById(R.id.iv_cart);
		}else{
			setContentView(R.layout.ost_phone_player);
		}
		
	    ns = Context.NOTIFICATION_SERVICE;
	    notificationManager = (NotificationManager) getSystemService(ns);
	    CharSequence contentTitle = "Загрузка начата";
        long when = System.currentTimeMillis();
	    noti = new Notification(android.R.drawable.stat_sys_download_done, contentTitle, when);
		
		album_link = getIntent().getExtras().getString("album_link");

		btn_play_pause = (ImageButton) findViewById(R.id.btn_play_pause);
		btn_repiate = (ImageButton) findViewById(R.id.btn_repiate);
		btn_random = (ImageButton) findViewById(R.id.btn_random);
		pb_track = (SeekBar) findViewById(R.id.pb_track);
		lbl_track = (TextView) findViewById(R.id.lbl_track);
		
		lv_track = (ListView) findViewById(R.id.lv_track);
		adapter = new TrackAdapter(this);
	    lv_track.setAdapter(adapter);
	    
	    startLoadingTrackList();
		
	    lv_track.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  play_position = position;
		    	  status = "play";
		    	  if (service){
		    		  startService(new Intent(Service_Player.ACTION_PLAY)
		    		  	.putExtra("position", play_position)
		    		  	.putExtra("album_link", album_link)
		    		  	.putExtra("list", list));
		    	  }else{
		    		  startService(new Intent(Service_Player.ACTION_CREATE)
						.putExtra("album_link", album_link)
						.putExtra("receiver", new StatusReceiver(new Handler()))
						.putExtra("receiver_pos", new PosReceiver(new Handler())));
		    		  startService(new Intent(Service_Player.ACTION_PLAY)
		    		  	.putExtra("position", play_position)
		    		  	.putExtra("album_link", album_link)
		    		  	.putExtra("list", list));
		    	  }
		      }
		    });
	    
	    pb_track.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	    	@Override       
	        public void onStopTrackingTouch(SeekBar seekBar) {      
	    		   
	        }       

	        @Override       
	        public void onStartTrackingTouch(SeekBar seekBar) {     
	            // TODO Auto-generated method stub      
	        }       

	        @Override       
	        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) { 
	        	if (fromUser){
	        		if (service) {
	        			startService(new Intent(Service_Player.ACTION_SEEK_TO).putExtra("pos_to_seek", progress));
	        		}else{
	        			
	        		}
	        	}
	        }
	    });
	}

	@Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
		}
		return super.onOptionsItemSelected(item);
    }
		
	public void onPlayPause(View view){
		if (service) {
			startService(new Intent(Service_Player.ACTION_PLAY_PAUSE));
		}else{
			startService(new Intent(Service_Player.ACTION_CREATE)
				.putExtra("list", list)
				.putExtra("receiver", new StatusReceiver(new Handler()))
				.putExtra("receiver_pos", new PosReceiver(new Handler())));
		}
	}
		
	public void onRandom(View view){
		if (random){
			random = false;
			btn_random.setImageDrawable(getResources().getDrawable(R.drawable.shuffle));
		}else{
			random = true;
			btn_random.setImageDrawable(getResources().getDrawable(R.drawable.shuffle));
		}
		if (service) {
			startService(new Intent(Service_Player.ACTION_SHAFFLE));
		}else{
			startService(new Intent(Service_Player.ACTION_CREATE)
				.putExtra("list", list)
				.putExtra("receiver", new StatusReceiver(new Handler()))
				.putExtra("receiver_pos", new PosReceiver(new Handler())));
		}
	}
	
	public void onDestroy(View view){
		if (service) {
			startService(new Intent(Service_Player.ACTION_DESTROY));
		}else{

		}
	}
	
	public void onRepiate(View view){
		if (repiate){
			repiate = false;
			btn_repiate.setImageDrawable(getResources().getDrawable(R.drawable.repeat));
		}else{
			repiate = true;
			btn_repiate.setImageDrawable(getResources().getDrawable(R.drawable.repeat));
		}
		if (service) {
			startService(new Intent(Service_Player.ACTION_REPIATE));
		}else{
			startService(new Intent(Service_Player.ACTION_CREATE)
				.putExtra("list", list)
				.putExtra("receiver", new StatusReceiver(new Handler()))
				.putExtra("receiver_pos", new PosReceiver(new Handler())));
		}
	}
	
	public void nextAudio(View view){
		if (service) {
			startService(new Intent(Service_Player.ACTION_NEXT));
		}else{
			startService(new Intent(Service_Player.ACTION_CREATE)
				.putExtra("list", list)
				.putExtra("receiver", new StatusReceiver(new Handler()))
				.putExtra("receiver_pos", new PosReceiver(new Handler())));
		}
	}
	
	public void prewAudio(View view){
		if (service) {
			startService(new Intent(Service_Player.ACTION_PREVIOUS));
		}else{
			startService(new Intent(Service_Player.ACTION_CREATE)
				.putExtra("list", list)
				.putExtra("receiver", new StatusReceiver(new Handler()))
				.putExtra("receiver_pos", new PosReceiver(new Handler())));
		}
	}
	
	public void startLoadingTrackList() {
    	if (loader == null || loader.getStatus().equals(AsyncTask.Status.FINISHED)) {
    		loader = new AsyncTrackList();
    		loader.execute();
    	} else {
    		Toast.makeText(Activity_OST_Player.this, "Р–РґРёС‚Рµ Р·Р°РІРµСЂС€РµРЅРёСЏ Р·Р°РіСЂСѓР·РєРё", Toast.LENGTH_SHORT)
			.show();
    	}
    }
  
    private class AsyncTrackList extends AsyncTask<Void, Void, Void> {
	  
	  @Override
	  protected void onPreExecute() {
		  setSupportProgressBarIndeterminateVisibility(true);
		  track_link.clear();
		  track_download.clear();
		  track.clear();
		  lv_track.setEnabled(false);
	  }
	  
	  protected void onPostExecute(Void result1) {
		  list = new String[ track_link.size() ];
		  File cachedir;
		  for (int i=0;i<track_link.size();i++){
			  String temp = track_link.get(i).split("/")[track_link.get(i).split("/").length-1];
			  track.add(temp);
			  cachedir = new File(Functions.preference.cache_dir + "/shikimori/ost/"+ost_title+"/"+temp);
			  //Log.d("dir", cash_dir + "/shikimori/ost/"+ost_title+"/"+temp);
			  if(cachedir.exists()){
				  track_download.add(true);
				  track_link.set(i, Functions.preference.cache_dir + "/shikimori/ost/"+ost_title+"/"+temp);
			  }else{
				  track_download.add(false);  
			  }
		  }
		  track_link.toArray(list);
		  
		  int rotate = getWindowManager().getDefaultDisplay().getRotation();
		  if (Functions.isTablet(getBaseContext()) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
		      //aq.id(iv_cart).image(cart, true, true);
              Ion.with(iv_cart)
                      .animateLoad(R.anim.spin_animation)
                      .error(R.drawable.missing_preview)
                      .load(cart);
		  }

		  setSupportProgressBarIndeterminateVisibility(false);
		  adapter.notifyDataSetChanged();
		  lv_track.setEnabled(true);


		  startService(new Intent(Service_Player.ACTION_CREATE)
		  							.putExtra("receiver", new StatusReceiver(new Handler()))
		  							.putExtra("receiver_pos", new PosReceiver(new Handler())));
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			loadlist();
			return null;
		}

		
  }
    
}
