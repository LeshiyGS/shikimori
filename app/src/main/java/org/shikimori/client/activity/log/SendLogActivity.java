package org.shikimori.client.activity.log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;

/**
 * Created by Феофилактов on 02.12.2014.
 */
public class SendLogActivity extends Activity {

    public static final String MSG = "msg";
    public static final String MSG_CODE = "msg_code";
    private String device;
    private String androidVersion;
    private String appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // make a dialog without a titlebar

        extractLogToFile();

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                this);
        builderSingle.setTitle("Выбор");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Скопировать в буфер");
        arrayAdapter.add("Показать на экране");
        builderSingle.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            confirm(true);
                        } else if (which == 1){
                            Intent i = new Intent(SendLogActivity.this, ShowErrorLogActivity.class);
                            i.putExtra("error", getErrorText());
                            startActivity(i);
                        }
                        finish();
                    }
                });
        builderSingle.show();

    }

    private String extractLogToFile() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;

        device = model;
        androidVersion = Build.VERSION.SDK_INT + "";
        appName = info == null ? "(null)" : info.versionName;

        StringBuffer str = new StringBuffer();
        str.append("Android version: " + androidVersion + "\n");
        str.append("Device: " + model + "\n");
        str.append("App version: " + appName + "\n");
        //str.append(getIntent().getStringExtra(MSG));

        return str.toString();
    }

    private String getErrorText(){
        StringBuffer str = new StringBuffer();
        str.append("Android version: " + androidVersion + "\n");
        str.append("Device: " + device + "\n");
        str.append("App version: " + appName + "\n");
        str.append("error: " + getIntent().getStringExtra(MSG) + "\n");
        return str.toString();
    }

    public void confirm(boolean confirm) {
        if (confirm) {
            // Creates a new text clip to put on the clipboard
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(getErrorText());
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", getErrorText());
                clipboard.setPrimaryClip(clip);
            }
        }
        finish();
    }
}
