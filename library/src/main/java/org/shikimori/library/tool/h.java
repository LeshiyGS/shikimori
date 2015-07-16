package org.shikimori.library.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import com.nineoldandroids.animation.Animator;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidanimations.library.fading_entrances.FadeInAnimator;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.shikimori.library.R;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Владимир on 23.06.2014.
 */
public class h {

    public static final Locale RUSSIAN_LOCALE = new Locale("ru", "RU");
    public static Handler loop = new Handler(); //fixme это пиздец. Тут должна быть отложенная инициализация как минимум.

    public static void showMsg(Context context, int msg) {
        if (context == null)
            return;

        showMsg(context, context.getString(msg));
    }

    public static void showMsg(Context context, String msg) {
        if (context == null)
            return;
        Toast m = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        m.setGravity(Gravity.TOP, 0, 60);
        m.show();
    }

    @Deprecated
    public static void setVisible(View v, boolean visible) {
        setVisible(visible ? View.VISIBLE : View.INVISIBLE, v);
    }

    public static void setVisible(boolean visible, View ... views) {
        setVisible(visible ? View.VISIBLE : View.INVISIBLE, views);
    }

    public static void setVisibleGone(View ... v) {
        setVisible(View.GONE, v);
    }

    public static void setVisible(View ... v) {
        setVisible(View.VISIBLE, v);
    }

    public static void setVisibleGone(boolean gone, View ... views) {
        setVisible(gone ? View.GONE : View.VISIBLE, views);
    }

    private static void setVisible(int status, View ... views){
        if(views == null)
            return;
        for (View v : views) {
            if(v!=null){
                if (v.getVisibility() != status)
                    v.setVisibility(status);
            }
        }
    }

    public static int pxToDp(int px, Context contex) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                px, contex.getResources().getDisplayMetrics());
    }

    public int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /**
     * @param format
     * @return current formated date
     */
    public static String getStringDate(String format) {
        return getStringDate(format, Calendar.getInstance().getTime());
    }

    public static String getStringDate(String format, Date date) {
        SimpleDateFormat _format = new SimpleDateFormat(format);
        return _format.format(date);
    }

    /**
     * @param duration_seconds Duration in seconds
     * @return String representation of duration
     */
    public static String durationToString(long duration_seconds) {
        return getDuration((int) duration_seconds);
    }

    public static Date getDateFromString(String format, String date) {
        if (date != null && format != null) {
            SimpleDateFormat _format = new SimpleDateFormat(format);
            try {
                return _format.parse(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Date();
    }

    public static Animation startAnimation(View v, int anim) {
        return startAnimation(v, anim, null);
    }

    public static Animation startAnimation(View v, int anim, Animation.AnimationListener l) {
        v.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(v.getContext(), anim);
        if (l != null)
            animation.setAnimationListener(l);
        v.startAnimation(animation);
        return animation;
    }

    public static void visibility(final View view,
                                  final boolean visibility, int animation) {
        if (view == null)
            return;

        Animation fades = AnimationUtils.loadAnimation(view.getContext(), animation);
        fades.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!visibility) {
                    view.clearAnimation();
                    view.setVisibility(View.GONE);
                }
            }
        });
        if (visibility)
            view.setVisibility(View.VISIBLE);
        view.startAnimation(fades);
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void setFont(final Context context, final View v) {
        setFont(context, v, FontCache.FONT.ROBOTO);
    }

    public static void setFont(final Context context, final View v, FontCache.FONT font) {

        Typeface typeFace = FontCache.get(font, context);

        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    setFont(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(typeFace);
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(typeFace);
            }
        } catch (Exception e) {
        }
    }

    /**
     * @param secconds
     * @return
     */
    public static String getDuration(int secconds) {
        StringBuffer buf = new StringBuffer();
        int millis = secconds * 1000;
        int hours = millis / (1000 * 60 * 60);
        int minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

        if (hours > 0) {
            buf.append(String.format("%02d", hours))
                    .append(":");
        }
        buf.append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

//    /**
//     *
//     * @param context
//     * @return mdpi,hpi,xhpi, xxhdpi
//     */
//    public static String getScreenSize(Context context){
//
//        int _w = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
//
//        if (_w == Configuration.SCREENLAYOUT_SIZE_LARGE) {
//            return Configuration.SCREENLAYOUT_SIZE_LARGE;
//        }
//        else if (_w == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
//            return Configuration.SCREENLAYOUT_SIZE_XLARGE;
//        }
//        else if (_w == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
//            return "mdpi";
//        }
//        else if (_w == Configuration.SCREENLAYOUT_SIZE_SMALL) {
//            return "mdpi";
//        }
//        else {
//            return 0;
//        }
//    }

    public static Point getScreenSize(Activity context) {
        Display display = context.getWindowManager().getDefaultDisplay();

        Point screenSize = new Point();
        if (Build.VERSION.SDK_INT >= 13) {
            display.getSize(screenSize);
        } else {
            screenSize.set(display.getWidth(), display.getHeight());
        }
        return screenSize;
    }

    public static String getScreenDensyty(Context context) {
        int dpi = context.getResources().getDisplayMetrics().densityDpi;
        if (dpi <= DisplayMetrics.DENSITY_MEDIUM)
            return "mdpi";
        else if (dpi == DisplayMetrics.DENSITY_HIGH)
            return "hdpi";
        else if (dpi == DisplayMetrics.DENSITY_XHIGH)
            return "xhdpi";
        else if (dpi >= DisplayMetrics.DENSITY_XXHIGH)
            return "drawable-xxhdpi";

        return "mdpi";
    }

    public static String formatCurrency(double value) {
        NumberFormat currencyFormatter;
        currencyFormatter = NumberFormat.getCurrencyInstance(RUSSIAN_LOCALE);
        currencyFormatter.setGroupingUsed(true);
        currencyFormatter.setMinimumFractionDigits(0);
        return currencyFormatter.format(value);
    }

    public static boolean match(String needle_regexp, String string) {
        Pattern p = Pattern.compile(needle_regexp);
        Matcher matcher = p.matcher(string);
        if (matcher.find())
            return true;
        return false;
    }

    public static boolean hideKeyboard(Context context, View hostView) {
        if (hostView == null || hostView.getWindowToken() == null)
            return true;
        return ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(hostView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean showKeyboard(Context mContext, EditText etText) {
        if (etText == null || etText.getWindowToken() == null)
            return true;
        etText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.showSoftInput(etText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void toggleKeyboard(Context context) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static boolean getConnection(Context c) {
        ConnectivityManager conMgr = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        boolean ret = true;
        if (i != null) {
            if (!i.isConnected())
                ret = false;
            if (!i.isAvailable())
                ret = false;
        }

        if (i == null)
            ret = false;

        return ret;

    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static int getCameraPhotoOrientation(String imagePath) {
        int rotate = 0;
        try {
            //context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null && !b.isRecycled()) {
            Matrix m = new Matrix();

            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                throw ex;
            }
        }
        return b;
    }

    public static String getText(String text, String defText) {
        if (TextUtils.isEmpty(text) || text.equals("null"))
            return defText;
        return text;
    }

    public static void setLanguage(Context c, String lang) {
        Resources res = c.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang);
        res.updateConfiguration(conf, dm);
    }

    public static int getAttributeResourceId(Context context, int attr) {
        try {
            TypedValue typedValue = new TypedValue();
            int[] resIdAttr = new int[]{attr};
            TypedArray a = context.obtainStyledAttributes(typedValue.data, resIdAttr);
            int resId = a.getResourceId(0, 0);
            a.recycle();
            return resId;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Recolor text according to the tags.
     * <p/>
     * E.g "Something red is after that:&#F00; red" will be processed and word red will be wrapped into red ColorSpan.
     *
     * @author cab404
     */
    public static Spanned colorize(CharSequence str) {
        SpannableStringBuilder b = new SpannableStringBuilder(str);
        Object appliedSpan = null;

        int last_start = 0;

        int start = 0;

        while ((start = indexOf(b, 0, '&')) != -1) {

            int end;

            if ((end = indexOf(b, 0, ';')) == -1) {
                b.delete(start, start + 1);
                continue;
            }

            if (appliedSpan != null)
                b.setSpan(appliedSpan, last_start, start, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            String node = b.subSequence(start + 1, end).toString();

            b.delete(start, end + 1);

            last_start = start;

            try {
                System.out.println("node = " + node);
                int color = Color.parseColor(node);
                appliedSpan = new ForegroundColorSpan(color);
            } catch (IllegalArgumentException e) {
                appliedSpan = null;
            }

        }

        if (appliedSpan != null) {
            b.setSpan(appliedSpan, last_start, b.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            System.out.println("last");
            System.out.println("b = " + b);
            System.out.println("last_start = " + last_start);
            System.out.println("b.length() = " + b.length());
        }

        return b;
    }

    public static int indexOf(CharSequence seq, int start, char ch) {
        for (int i = start; i < seq.length(); i++)
            if (ch == seq.charAt(i)) return i;
        return -1;
    }


    public static int indexOf(CharSequence seq, char ch) {
        return indexOf(seq, 0, ch);
    }

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isInsideLoop() {
        return loop.getLooper().getThread() == Thread.currentThread();
    }

    /**
     * Постит задание, если указатель выполнения не в потоке прорисовки, иначе сразу выполняет.
     */
    public static void postIfOutside(Runnable run) {
        if (loop.getLooper().getThread() == Thread.currentThread()) {
            run.run();
        } else {
            loop.post(run);
        }
    }

    public static void setTextViewHTML(Context activity, TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(activity, strBuilder, span);
        }
//        text.setMovementMethod(LinkMovementMethod.getInstance());
        text.setText(strBuilder);
    }

    private static void makeLinkClickable(final Context activity, SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(span.getURL()));
                activity.startActivity(i);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public static View.OnTouchListener getImageHighlight = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v == null)
                return false;
            try {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        YoYo.with(new FadeInAnimator() {
                            public void prepare(View target) {
                                this.getAnimatorAgent().playTogether(new Animator[]{ObjectAnimator.ofFloat(target, "alpha", new float[]{0.5F, 1.0F})});
                            }
                        }).duration(200)
                                .playOn(v);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };


    public static void launchUrlLink(Context mContext, String url) {
        if (url == null)
            return;
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mContext.startActivity(browserIntent);
    }

    public static ViewGroup.LayoutParams getDefaultParams() {
        return new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public enum ORIENTATION {
        PORT, LAND
    }

    public static ORIENTATION getOrientation(Activity mContext) {
        final int orientation = mContext.getResources().getConfiguration().orientation;
        final int rotation = mContext.getWindowManager().getDefaultDisplay().getOrientation();

        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                return ORIENTATION.PORT;
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return ORIENTATION.LAND;
            }
        } else if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_270) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                return ORIENTATION.PORT;
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return ORIENTATION.LAND;
            }
        }
        return ORIENTATION.LAND;
    }

    public static <C extends View> C get(View view, int id) {
        View childView = view.findViewById(id);
        return (C) childView;
    }

    public static ImageLoader initImageLoader(Context c) {
        if (ImageLoader.getInstance().isInited())
            return ImageLoader.getInstance();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(c)
                .defaultDisplayImageOptions(getImageLoaderOptions())
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // 2 Mb
                .build();

        ImageLoader.getInstance().init(config);
        return ImageLoader.getInstance();
    }

    public static DisplayImageOptions getImageLoaderOptions() {
        return getImageLoaderOptionsBuilder().build();
    }

    public static DisplayImageOptions.Builder getImageLoaderOptionsBuilder() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageOnFail(R.drawable.missing_preview)
                .resetViewBeforeLoading(false)
                .displayer(new FadeInBitmapDisplayer(400, true, true, false));
    }

    public static String getTimeAgo(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        df.setTimeZone(TimeZone.getDefault());
        try {
            Date d = df.parse(date);

            long epoch = d.getTime();

            return (String) DateUtils.getRelativeTimeSpanString(epoch, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return getStringDate("dd/MM/yyyy", new Date());
    }

    public static void insertTextEditText(EditText et, String text) {
        int start = Math.max(et.getSelectionStart(), 0);
        int end = Math.max(et.getSelectionEnd(), 0);
        et.getText().replace(Math.min(start, end), Math.max(start, end),
                text, 0, text.length());
    }

}
