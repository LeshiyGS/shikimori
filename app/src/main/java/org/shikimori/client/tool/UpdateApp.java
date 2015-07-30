package org.shikimori.client.tool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import org.shikimori.client.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import ru.altarix.basekit.library.tools.h;

public class UpdateApp extends AsyncTask<String, Integer, String> {
    private Context context;
    private String fileName;
    private boolean loading = true;
    private UpdateApkProgressListener update_listener;

    public UpdateApp(Context contextf, String fileName) {
        context = contextf;
        this.fileName = fileName;
    }
    public UpdateApp(Context contextf) {
        context = contextf;
    }

    public void setContext(Context contextf) {
        context = contextf;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setProgresListener(UpdateApkProgressListener l) {
        this.update_listener = l;
    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String urlString = arg0[0];
            URL url = new URL(arg0[0]);
            URLConnection c = url.openConnection();
            //                HttpURLConnection c = (HttpURLConnection) url.openConnection();
            //                c.setRequestMethod("GET");
            //                c.setDoOutput(true);
            c.connect();

            String path;
            if (h.isExternalStorageWritable()) {
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            } else {
                path = context.getFilesDir().getAbsolutePath();
            }

            if(fileName == null){
                fileName = urlString.substring(urlString.lastIndexOf('/') + 1, urlString.length());
                path += "/" + fileName;
            } else {
                path += "/" + fileName+".apk";
            }

            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }

            int fileLength = c.getContentLength();

            FileOutputStream fos = new FileOutputStream(file);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            int total = 0;
            int procent = 0;
            while ((len1 = is.read(buffer)) != -1) {
                total += len1;
                if (procent != (total * 100 / fileLength))
                    publishProgress(total * 100 / fileLength);
                // h.log((total * 100 / fileLength));

                procent = total * 100 / fileLength;

                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();

            //log("UpdateAPP install "+path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (update_listener != null)
            update_listener.update(values[0]);
    }

    @Override
    protected void onPostExecute(String path) {
        super.onPostExecute(path);
        if (context == null)
            return;

        if (path == null) {
            h.showMsg(context, R.string.error_download_apk);
            return;
        }

        if (update_listener != null)
            update_listener.finish();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
        context.startActivity(intent);
        loading = false;
    }

    public interface UpdateApkProgressListener {
        public void update(int progress);

        public void finish();
    }
}