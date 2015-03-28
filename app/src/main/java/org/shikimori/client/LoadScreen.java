package org.shikimori.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class LoadScreen extends FragmentActivity {

    private long splashTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_shiki_splash_screen);

        pauseBeforeLoad();
    }

    void startApp(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    void pauseBeforeLoad(){
        Thread mythread = new Thread() {
            public void run() {
                try {
                    sleep(splashTime);
                } catch (Exception e) {}

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startApp();
                    }
                });
            }
        };
        mythread.start();
    }

}
