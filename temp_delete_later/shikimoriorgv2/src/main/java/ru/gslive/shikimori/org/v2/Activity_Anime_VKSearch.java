package ru.gslive.shikimori.org.v2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.perm.kate.api.Api;
import com.perm.kate.api.Video;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
//import android.os.Handler;
//import android.os.ResultReceiver;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
//import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Activity_Anime_VKSearch extends ShikiSherlockActivity {

	int page_no = 0;
	Boolean stop_update = true;
	
	private final int REQUEST_LOGIN=1;
	String theme = "0";
	String search = "";
    String title = "";
    String id ="";
	String episodes,select_video;
	Boolean all_ok = true;

	TraningAdapter adapter;
	
	final int JOBFINISH_ID = 1;
    int temp_progress = 0;
    
	ListView lv_main;
	TextView tv_load;
	
	VK_Account account = new VK_Account();
    Api api;
    
    Notification noti;
    SearchView searchView;
	
    AsyncTask<Void, Void, Void> al;
    AsyncTask<Void, Void, Void> al2;
	
    private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
    
	private final int DIALOG_LOAD_KEY = 2; //
	private final int DIALOG_VIDEO_QUALITY = 3; //
	
	private final String API_ID = "4018924";
	
	public static ActionBar actionbar;
	
	Intent notificationIntent;
    PendingIntent contentIntent;
    String ns;
    NotificationManager notificationManager;
	
	ArrayList<String> video_quality = new ArrayList<String>();
	ArrayList<String> video_quality_title = new ArrayList<String>();
	
	ArrayList<String> res_title = new ArrayList<String>();
	ArrayList<String> res_prev = new ArrayList<String>();
	ArrayList<String> res_link = new ArrayList<String>();
	
	ArrayList<String> t_res_title = new ArrayList<String>();
	ArrayList<String> t_res_prev = new ArrayList<String>();
	ArrayList<String> t_res_link = new ArrayList<String>();
		
	//Создание контекстного меню
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		menu.setHeaderTitle("Выберите:");
		menu.add(0, 1000, 0, "Скопировать URL видео");
        menu.add(0, 1001, 0, "Добавить видео на сайт");
		super.onCreateContextMenu(menu, v, menuInfo);
	}
		
	//Обработка выбора контекстного меню
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
	    case 1000:
	    	//Копируем в буфер
	    	int sdk_Version = android.os.Build.VERSION.SDK_INT;
	        if(sdk_Version < android.os.Build.VERSION_CODES.HONEYCOMB) {
	            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
	            clipboard.setText(res_link.get(info.position));   // Assuming that you are copying the text from a TextView
	            Toast.makeText(getApplicationContext(), "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
	        }
	        else { 
	            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE); 
	            android.content.ClipData clip = android.content.ClipData.newPlainText("Video URL", res_link.get(info.position));
	            clipboard.setPrimaryClip(clip);
	            Toast.makeText(getApplicationContext(), "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
	        } 
	        return true;
        case 1001:
            Intent intent_com = new Intent(Activity_Anime_VKSearch.this, Activity_Add_Video.class);
            intent_com.putExtra("id", id);
            intent_com.putExtra("title", title);
            intent_com.putExtra("episod", String.valueOf(Integer.parseInt(episodes)+1));
            intent_com.putExtra("url", res_link.get(info.position));
            startActivity(intent_com);
             return true;
	    default:
	        return false;
	    }
	}
		
	
	public class TraningAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;
		 
		public TraningAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return res_title.size(); // длина массива
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return res_title.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;
			if (Functions.preference.theme.equals("ligth")){
				convertView = mLayoutInflater.inflate(R.layout.w_item_vk_search, null);
			}else{
				convertView = mLayoutInflater.inflate(R.layout.d_item_vk_search, null);
			}
			
			
			holder = new Functions.ViewHolder();
    		holder.textView = (TextView) convertView.findViewById(R.id.lbl_title);
    		holder.imageView = (ImageView) convertView.findViewById(R.id.img_prev);
    		convertView.setTag(holder);

			holder.textView.setText(res_title.get(position));
			ImageLoader.getInstance().displayImage(res_prev.get(position), holder.imageView);
			
			return convertView;
		}
	}
	

	/*private class DownloadReceiver extends ResultReceiver{
	    public DownloadReceiver(Handler handler) {
	        super(handler);
	    }

	    @Override
	    protected void onReceiveResult(int resultCode, Bundle resultData) {
	        super.onReceiveResult(resultCode, resultData);
	        if (resultCode == Service_Download.UPDATE_PROGRESS) {
	        	int progress = resultData.getInt("progress");
	            int note_id = resultData.getInt("note_id");
	            String note_name = resultData.getString("note_name");
	            
				CharSequence contentTitle;
	            CharSequence contentText;
	            if (progress == 100){
		            contentTitle = "Скачивание завершено";
		            contentText = "Скачивание завершено";
		            long when = System.currentTimeMillis();
		            noti = new Notification(android.R.drawable.stat_sys_download_done, contentTitle, when);
	            }else{
		            contentTitle = "Скачивание началось";
		            contentText = note_name;
	            }
	            
	            noti.flags |= Notification.FLAG_AUTO_CANCEL;
	            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notify);
	            contentView.setTextViewText(R.id.status_text, contentText);
	            contentView.setTextViewText(R.id.tv_file, resultData.getString("title"));
	            contentView.setProgressBar(R.id.status_progress, 100, progress, false);
	            noti.contentView = contentView;
	            noti.contentIntent = contentIntent;
	            notificationManager.notify(note_id, noti);
	        }
	    }
	}*/

	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Чтение настроек
		Functions.getPreference(getBaseContext());

		setTheme(R.style.AppTheme_ActionBarStyle_BackHome);
			
		actionbar = getSupportActionBar();
			
		SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
	    s.setSpan(new Functions.TypefaceSpan(getBaseContext(), getString(R.string.shiki_font)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
	    actionbar.setTitle(s);
		actionbar.setSubtitle("Поиск на VK");
		actionbar.setDisplayHomeAsUpEnabled(true);

		super.onCreate(savedInstanceState);
		if (Functions.preference.theme.equals("ligth")){
			setContentView(R.layout.w_activity_vk_list);
		}else{
			setContentView(R.layout.d_activity_vk_list);
		}
		
		
		search = getIntent().getExtras().getString("title");
        id = getIntent().getExtras().getString("id");
        title = getIntent().getExtras().getString("title");
		episodes = getIntent().getExtras().getString("episodes");
		search = search + " " + (Integer.parseInt(episodes)+1);
		 
		tv_load = (TextView) findViewById(R.id.tv_load);
		
		lv_main = (ListView) findViewById(R.id.lv_main);
		adapter = new TraningAdapter(Activity_Anime_VKSearch.this);
		lv_main.setAdapter(adapter);
		registerForContextMenu(lv_main);
		
		//Восстановление сохранённой сессии
        account.restore(this);
        
        al = (AsyncLoader) getLastNonConfigurationInstance();
	    if (al == null) {
	      al = new AsyncLoader();
	    }
                
        lv_main.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	  al2 = new seriesLoader(res_link.get(position)).execute();
		      }
		    });
      
        lv_main.setOnScrollListener(new OnScrollListener() {
	        public void onScrollStateChanged(AbsListView view, int scrollState) {

	        }

	        public void onScroll(AbsListView view, int firstVisibleItem,
	            int visibleItemCount, int totalItemCount) {
	        	boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

	    		if (loadMore && al.getStatus() == AsyncTask.Status.FINISHED && !stop_update) {
		        	page_no++;
		        	al = new AsyncLoader().execute();
	    		}
	    		if (stop_update){

	    		}
	        	
	        }
	      });
        
        //Если сессия есть создаём API для обращения к серверу
        if(account.access_token!=null){
            api = new Api(account.access_token, API_ID);
            al = new AsyncLoader().execute();
        }else{
        	Intent intent = new Intent();
            intent.setClass(this, Activity_Login_VK.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        }
	}

	//Сохранение значения Асинхронной задачи
  	public Object onRetainNonConfigurationInstance() {
  		return al;
  	}
	
  	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
				
	    searchView = new SearchView(getSherlock().getActionBar().getThemedContext());
	    
	    searchView.setQueryHint("Search");
	    searchView.setIconified(false);
	    searchView.setQuery(search,false);
	    searchView.setWeightSum(1);
	    searchView.setFocusable(false);
	    
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {

	        @Override
	        public boolean onQueryTextSubmit(String query) {
	            onSearchRequested();
	    		page_no = 0;
	    		res_title.clear();
	    		res_prev.clear();
	    		res_link.clear();
	    		t_res_title.clear();
	    		t_res_prev.clear();
	    		t_res_link.clear();
	    		adapter.notifyDataSetChanged();
				search = query;
				al = new AsyncLoader().execute();;
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
	            return false;
	        }

	        @Override
	        public boolean onQueryTextChange(String newText) {
	            // TODO Auto-generated method stub
	            return false;
	        }
	    });	
		
	    menu.add("Search")
        .setIcon(R.drawable.abs__ic_search)
        .setActionView(searchView)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
	}
	
	//Actions меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		switch (item.getItemId()) {
          	case android.R.id.home:
          		finish();
          		break;
  		}
  		return super.onOptionsItemSelected(item);        
    }
	
  	
  	@Override
    protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final CharSequence[] temp;
		int ids;
		switch (id) {
        case DIALOG_LOAD_KEY:
        	ProgressDialog dialog = new ProgressDialog(this);
        	dialog.setMessage("Загрузка, подождите пожалуйста...");
        	dialog.setCancelable(false);
        	return dialog;
        case DIALOG_VIDEO_QUALITY:
        	temp = new String[ video_quality.size() ];
        	//series.toArray(temp);
        	for (int i=0;i<video_quality.size();i++){
        		temp[i] = video_quality_title.get(i);
        	}
            builder = new AlertDialog.Builder(this);
			ids = 0;
			select_video = video_quality.get(0);
			if (video_quality.size() != 0){
			  builder.setTitle("Выбирите качество видео")
				.setCancelable(false)
				.setSingleChoiceItems(temp, ids, new DialogInterface.OnClickListener() {
	            
					@Override
					public void onClick(DialogInterface dialog, int which) {
						select_video = video_quality.get(which);
					}
					
				})
				.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int id) {
                        removeDialog(DIALOG_VIDEO_QUALITY);
                    }
				})
				.setNeutralButton("Скачать", new DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int id) {
                    	//final Random rand = new Random();
                    	//int diceRoll = rand.nextInt(100000);
                    	String temp_name = "";
                    	for (int ii=0; ii < ILLEGAL_CHARACTERS.length; ii++){
			 				temp_name = search.replace(ILLEGAL_CHARACTERS[ii], '_');
			 			}
                    	temp_name += " " + select_video.substring(select_video.indexOf(".mp4")-3, select_video.indexOf(".mp4")) +"p";
			 			if (temp_name.substring(temp_name.lastIndexOf("/")+1).length() >= 150){
			 				temp_name = temp_name.substring(0, 149);
			 			}
                    	removeDialog(DIALOG_VIDEO_QUALITY);
                    	
                    	
                    	File cachedir = new File(Functions.preference.cache_dir + "/shikimori/video/");
                        if(cachedir.exists()==false) {
                             cachedir.mkdirs();
                        }
                    	DownloadManager.Request r = new DownloadManager.Request(Uri.parse(select_video));
                    	r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, temp_name + ".mp4");
                    	r.allowScanningByMediaScanner();
                    	r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    	DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    	dm.enqueue(r);
                    	
                    	/*
                        Intent intent = new Intent(Activity_Anime_VKSearch.this, Service_Download.class);
                        intent.putExtra("url", select_video);
                        intent.putExtra("type", "video");
                        intent.putExtra("title", select_video);
                        intent.putExtra("name", temp_name);
                        //intent.putExtra("name", select_video.substring(select_video.lastIndexOf("/")+1));
                        intent.putExtra("receiver", new DownloadReceiver(new Handler()));
                        intent.putExtra("note_id", diceRoll);
                        startService(intent);
                        
                        notificationIntent = new Intent(Activity_Anime_VKSearch.this, Activity_MainProfile.class);
                	    contentIntent = PendingIntent.getActivity(Activity_Anime_VKSearch.this, 0, notificationIntent, 0);
                	    ns = Context.NOTIFICATION_SERVICE;
                	    notificationManager = (NotificationManager) getSystemService(ns);
                		
                	    CharSequence contentTitle = "Загрузка видео";
        	            CharSequence contentText = "Начинаем загрузку";
        		        long when = System.currentTimeMillis();
        		        
        	            noti = new Notification(android.R.drawable.stat_sys_download, contentTitle, when);
        	            noti.flags |= Notification.FLAG_AUTO_CANCEL;
        	            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.download_notify);
        	            contentView.setTextViewText(R.id.status_text, contentText);
        	            contentView.setProgressBar(R.id.status_progress, 100, 0, false);
        	            noti.contentView = contentView;
        	            noti.contentIntent = contentIntent;
        	            notificationManager.notify(diceRoll, noti);
						*/
                    }
				})
                .setPositiveButton("Смотреть", new DialogInterface.OnClickListener() {
	                @SuppressWarnings("deprecation")
					public void onClick(DialogInterface dialog, int id) {
	                    removeDialog(DIALOG_VIDEO_QUALITY);
	                    Intent intent = new Intent(Intent.ACTION_VIEW); 
						intent.setDataAndType(Uri.parse(select_video), "video/mp4");
						startActivity(intent);
	                }
                });
			}else{
				builder.setMessage("К сожалению видео с данных серверов не может быть получено, получение видео с сервисов помимо VK, будет добавлено в следующих версиях.")
	            .setCancelable(false)
	            .setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int id) {
	                            //removeDialog(IDD_SERIES);
	                        }
	                    });
			}
			return builder.create();
        default:
            return null;
        }
    }
	
  	
  	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                //авторизовались успешно 
                account.access_token=data.getStringExtra("token");
                account.save(Activity_Anime_VKSearch.this);
                api=new Api(account.access_token, API_ID);
                al = new AsyncLoader().execute();
            }else{
            	finish();
            }
        }
        
    }
	
  	
	private class AsyncLoader extends AsyncTask<Void, Void, Void> {
		  
		 @Override
		 protected void onPreExecute() {
			 tv_load.setVisibility(View.VISIBLE);
		 }
		  
		 protected void onPostExecute(Void result1) {
			 if (!all_ok){
				 Intent intent = new Intent(Activity_Anime_VKSearch.this, Activity_Login_VK.class);
				 startActivityForResult(intent, REQUEST_LOGIN);
				 api = new Api(account.access_token, API_ID);
				 al = new AsyncLoader().execute();
			 }else{
				 res_title = t_res_title;
				 res_prev = t_res_prev;
				 res_link = t_res_link;
				 
				 adapter.notifyDataSetChanged();
			 }
			 tv_load.setVisibility(View.GONE);
			 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			 imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		 }

		 @Override
		 protected Void doInBackground(Void... arg0) {
			 	boolean ubdate_avalible = false;
			 	
			 	try {
                    ArrayList<Video> temps = api.searchVideo(search, "0", "0", "20", String.valueOf(page_no*20));
                    
                    for(int i=0;i<temps.size();i++){
                    	t_res_title.add(temps.get(i).title);
                    	t_res_prev.add(temps.get(i).image_big);
                    	t_res_link.add(temps.get(i).player);
                    	ubdate_avalible = true;
                    }
                    all_ok = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    all_ok = false;
                }
			 	if (ubdate_avalible){
					////Log.d("Что то добавилось","fdbfdf");
					stop_update = false;
				}else{
					////Log.d("Нет обновлений","fdbfdf");
					stop_update = true;
				}
				return null;
			}

			
	  }
	
	
	private class seriesLoader extends AsyncTask<Void, Void, Void> {
		private String url;  
		
		public seriesLoader(String url) {
	        this.url = url;
	    }
		
		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_LOAD_KEY);
			
			video_quality.clear();
			video_quality_title.clear();
		}
		  
		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Void result1) {
			removeDialog(DIALOG_LOAD_KEY);
			showDialog(DIALOG_VIDEO_QUALITY);
			
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Document res = Jsoup
					    .connect(url)
					    .get();
				
				Elements vk = res.select("source[type*=video]");
				for (Element src : vk){
					video_quality.add(src.attr("src"));
					video_quality_title.add(src.attr("src").substring(src.attr("src").indexOf(".mp4")-3, src.attr("src").indexOf(".mp4")));
				}
				select_video = vk.first().attr("src");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

			
	}

}
