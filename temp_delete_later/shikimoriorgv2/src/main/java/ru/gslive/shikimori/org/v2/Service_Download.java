package ru.gslive.shikimori.org.v2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class Service_Download extends IntentService {
    
	Boolean start = false;
	
	public static final int UPDATE_PROGRESS = 8344;

	public Service_Download() {
        super("DownloadService");
    }
	
	public void onCreate() {
	    super.onCreate();
	}

    @Override
    protected void onHandleIntent(Intent intent) {
    	
    	Functions.getPreference(getBaseContext());
    	
		File cachedir = new File(Functions.preference.cache_dir + "/shikimori/"+intent.getStringExtra("type"));
        if(cachedir.exists()==false) {
             cachedir.mkdirs();
        }
        String urlToDownload = intent.getStringExtra("url");
        String file_name;
        if (intent.getStringExtra("type").equals("video")){
        	file_name = cachedir + "/" + intent.getStringExtra("name").split(".mp4")[0] + ".mp4";
        }else{
        	file_name = cachedir + "/" + intent.getStringExtra("name");
        }
        String title = intent.getStringExtra("title");
        int note_id = intent.getIntExtra("note_id", 10001);
        ResultReceiver receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        
        OnNewSensorData(urlToDownload, file_name, title, note_id, receiver);
        
    }
    
    public void OnNewSensorData(final String urlToDownload, final String file_name, final String title, final int note_id,final ResultReceiver receiver) {
    	Thread thread = new Thread(new Runnable() {
            public void run() {
            	try {
                    URL url = new URL(urlToDownload);
                    URLConnection connection = url.openConnection();
                    connection.setReadTimeout(3000);
                    connection.connect();
                    // this will be useful so that you can show a typical 0-100% progress bar
                    int fileLength = connection.getContentLength();

                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(file_name);

                    byte data[] = new byte[2048];
                    long total = 0;
                    int count;
                    int temp_count=0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        if (temp_count < (int) (total * 100 / fileLength)){
                        	temp_count = (int) (total * 100 / fileLength);
	                        // publishing the progress....
	                        Bundle resultData = new Bundle();
	                        resultData.putInt("progress" ,(int) (total * 100 / fileLength));
	                        resultData.putInt("note_id" , note_id);
	                        resultData.putString("title" , title);
	                        resultData.putString("note_name" , file_name.substring(file_name.lastIndexOf("/")+1));
	                        receiver.send(UPDATE_PROGRESS, resultData);
                        }
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
      
            }
        });
    	
    	thread.start();
    }
   
    public void onDestroy() {
        super.onDestroy();
    }
}
