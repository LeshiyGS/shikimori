package org.shikimori.client.activity.log;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Владимир on 30.04.2015.
 */
public class ShowErrorLogActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        TextView text = new TextView(this);

        scroll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        text.setSelectAllOnFocus(true);
        text.setPadding(16, 16, 16, 16);
        text.setBackgroundColor(Color.WHITE);
        text.setText(getIntent().getStringExtra("error"));
        text.setTextColor(Color.BLACK);
        text.setOnClickListener(this);
        scroll.addView(text);
        setContentView(scroll);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Скопировано в буфер", Toast.LENGTH_SHORT).show();
        // Creates a new text clip to put on the clipboard
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(getIntent().getStringExtra("error"));
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", getIntent().getStringExtra("error"));
            clipboard.setPrimaryClip(clip);
        }
    }
}
