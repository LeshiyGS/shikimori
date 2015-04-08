package org.shikimori.library.tool;

import android.os.CountDownTimer;
import android.view.View;
import com.daimajia.androidanimations.library.YoYo;

/**
 * Created by Владимир on 08.04.2015.
 */
public class FixPauseAnimate {
    public static void play(final YoYo.AnimationComposer composer, final View v, int delay){

        new CountDownTimer(delay, delay){

            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                composer.playOn(v);
            }
        }.start();

    }
}
