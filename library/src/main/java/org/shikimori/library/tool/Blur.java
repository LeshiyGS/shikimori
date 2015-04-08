package org.shikimori.library.tool;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.fading_entrances.FadeInAnimator;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by Владимир on 08.04.2015.
 */
public class Blur {
    public static void apply(final Context context, final ImageView image, final View blured){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            return;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                image.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        image.getViewTreeObserver().removeOnPreDrawListener(this);
                        image.buildDrawingCache();

                        Bitmap bmp = image.getDrawingCache();
                        blur(context, bmp, blured);
                        return true;
                    }
                });
            }
        }, 300);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void blur(Context context, Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 8;
        float radius = 2;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth()/scaleFactor),
                (int) (view.getMeasuredHeight()/scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        ColorFilter filter = new LightingColorFilter(Color.parseColor("#666666"), 1);
//        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        paint.setColorFilter(filter);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int)radius, true);
        view.setBackground(new BitmapDrawable(context.getResources(), overlay));

        playFade(view);

        Log.d("blur time", "" + (System.currentTimeMillis() - startMs) + "ms");
    }

    private static void playFade(View view){
        int timer = 100;

        YoYo.with(new FadeInAnimator(){
            @Override
            public void prepare(View target) {
                this.getAnimatorAgent().playTogether(
                        new Animator[]{ObjectAnimator.ofFloat(target, "alpha", new float[]{1.0F, 0.8F})});
            }
        })
        .duration(timer)
        .playOn(view);

        YoYo.with(new FadeInAnimator(){
            @Override
            public void prepare(View target) {
                this.getAnimatorAgent().playTogether(
                        new Animator[]{ObjectAnimator.ofFloat(target, "alpha", new float[]{0.8F, 1.0F})});
            }
        })
        .delay(timer)
        .duration(timer*20)
        .playOn(view);
    }
}
