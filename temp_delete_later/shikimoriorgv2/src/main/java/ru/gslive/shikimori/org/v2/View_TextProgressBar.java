package ru.gslive.shikimori.org.v2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class View_TextProgressBar extends ProgressBar {
    private String text;
    private Paint textPaint;
    Typeface tf = Typeface.create("Sans",Typeface.BOLD);
 
    public View_TextProgressBar(Context context) {
        super(context);
        text = "0/100";
        textPaint = new Paint();
        final float scale = getContext().getResources().getDisplayMetrics().density;
        textPaint.setTextSize((int) (14 * scale));
        textPaint.setTypeface(tf);
        textPaint.setColor(Color.BLACK);
    }
 
    public View_TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        text = "0/100";
        textPaint = new Paint();
        final float scale = getContext().getResources().getDisplayMetrics().density;
        textPaint.setTextSize((int) (14 * scale));
        textPaint.setTypeface(tf);
        textPaint.setColor(Color.WHITE);
    }
 
    public View_TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        text = "0/100";
        textPaint = new Paint();
        final float scale = getContext().getResources().getDisplayMetrics().density;
        textPaint.setTextSize((int) (14 * scale));
        textPaint.setTypeface(tf);
        textPaint.setColor(Color.WHITE);
    }
 
    @SuppressLint("DrawAllocation")
	@Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        int x = getWidth() / 2 - bounds.centerX();
        int y = getHeight() / 2 - bounds.centerY();
        canvas.drawText(text, x, y, textPaint);
    }
 
    public synchronized void setText(String text) {
        this.text = text;
        drawableStateChanged();
    }
 
    public void setTextColor(int color) {
        textPaint.setColor(color);
        drawableStateChanged();
    }
}