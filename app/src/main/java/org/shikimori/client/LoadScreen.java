package org.shikimori.client;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.shikimori.client.tool.AuthMasterShiki;
import org.shikimori.library.tool.FontCache;
import org.shikimori.library.tool.hs;

/**
 * Created by Феофилактов on 29.03.2015.
 */
public class LoadScreen extends FragmentActivity {

    private long splashTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_shiki_splash_screen);

        hs.setFont(LoadScreen.this, findViewById(R.id.tvLogo), FontCache.FONT.STYLO);

        animateLogo();
        pauseBeforeLoad();
    }

    private void animateLogo() {
        final View logo = findViewById(R.id.tvLogo);
        logo.post(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.FadeInUp)
                        .duration(1000)
                        .playOn(logo);
            }
        });
    }

    void startApp(){

        new AuthMasterShiki(this)
                .openPage(MainActivity.class);

//        if(TextUtils.isEmpty(ShikiUser.getToken())
//                || TextUtils.isEmpty(ShikiUser.USER_NAME) || TextUtils.isEmpty(ShikiUser.USER_ID)){
//            startActivity(new Intent(this, AuthActivity.class));
//        } else {
//            startActivity(new Intent(this, MainActivity.class));
//        }

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
