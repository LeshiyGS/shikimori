package ru.altarix.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;


/**
 * Created by Владимир on 01.07.2014.
 */
public class CustomViewBase extends LinearLayout {
    String mHint = "";
    protected String mText;
    protected Context mContext;
    protected int guLayout;
    protected TextView tvLabel;
    protected TypedArray typedArray;
    protected OnClickListener clickListener;
    private int mTypeImage;
    protected ImageView ivImage;
    private int guVisibleLabel = INVISIBLE;
    private int mLabel;
    protected boolean mHideLabelIfEmpty;

    public CustomViewBase(Context context) {
        this(context, null);
    }

    public CustomViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomViewBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     *
     * @param attrs
     * @param baseLayout may be override in xml
     */
    protected void init(AttributeSet attrs, @LayoutRes int baseLayout) {
        mContext = getContext();
        checkThemeParent();
        setOrientation(VERTICAL);
        if(attrs != null){
            typedArray = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.AltarixUiDesclareStyle, 0, 0);

            try { //FIXME попробовать убрать try-finally
                int resHint = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "hint",0);
                mHint      = resHint==0? null : getContext().getString(resHint);
                int resText = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "text",0);
                if(resText != 0)
                    mText      = mContext.getString(resText);
                // нужно в случае когда лабел отличаеться от hint
                mLabel     = typedArray.getResourceId(R.styleable.AltarixUiDesclareStyle_uiLabel, 0);
                mTypeImage = typedArray.getResourceId(R.styleable.AltarixUiDesclareStyle_uiIcon, 0);
                guLayout   = typedArray.getResourceId(R.styleable.AltarixUiDesclareStyle_uiLayout, guLayout);
                guVisibleLabel   = typedArray.getInt(R.styleable.AltarixUiDesclareStyle_uiLabelVisible, INVISIBLE);
                // если текст пустой то обычно лейбел скрываетсья
                mHideLabelIfEmpty = typedArray.getBoolean(R.styleable.AltarixUiDesclareStyle_uiHideLabelIfEmpty, true);
            } finally {
            }
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (guLayout == 0)
            guLayout = baseLayout;

        if (guLayout == 0)
            return;

        View v = inflater.inflate(guLayout, null);
        tvLabel = (TextView) v.findViewById(R.id.tvLabel);
        ivImage = (ImageView) v.findViewById(R.id.ivImage);

        if(tvLabel!=null){
            if(mLabel==0)
                tvLabel.setText(mHint);
            else
                tvLabel.setText(mLabel);
            tvLabel.setVisibility(tvLabel.length()==0 ? guVisibleLabel:VISIBLE);
        }

        if(ivImage != null && mTypeImage!=0){
            ivImage.setVisibility(VISIBLE);
            ivImage.setImageResource(mTypeImage);
        }

        addView(v);
    }

    public void setHint(String hint){
        setLabel(hint);
    }

    public String getHint(){
        return mHint;
    }

    public void setLabel(String label){
        if(tvLabel !=null){
            if(TextUtils.isEmpty(label))
                tvLabel.setVisibility(GONE);
            else{
                tvLabel.setVisibility(VISIBLE);
                tvLabel.setText(label);
            }
        }
    }
    public void setLabel(int label){
        setLabel(mContext.getString(label));
    }

    public void setIcon(int icon_resorce){
        ivImage.setImageResource(icon_resorce);
        if(icon_resorce == 0){
            if(ivImage.getVisibility() != GONE)
                ivImage.setVisibility(GONE);
            return;
        }

        if(ivImage.getVisibility() != VISIBLE)
            ivImage.setVisibility(VISIBLE);
        invalidateLayout();
    }

    public void setIcon(String url){
        if(url == null){
            if(ivImage.getVisibility() != GONE)
                ivImage.setVisibility(GONE);
            return;
        }
        getImageLoader(mContext)
             .displayImage(url, ivImage);

        if(ivImage.getVisibility() != VISIBLE)
            ivImage.setVisibility(VISIBLE);
        invalidateLayout();
    }

    protected void invalidateLayout(){
        invalidate();
        requestLayout();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if(visibility == VISIBLE){
            this.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(this.getContext(), R.anim.abc_fade_in);
            this.startAnimation(animation);
        }
    }

    public TextView getLabelTextView(){
        return tvLabel;
    }

    public ImageView getIcon(){
        return ivImage;
    }

    void checkThemeParent(){
        TypedValue outValue = new TypedValue();
        boolean itsOk = mContext.getTheme().resolveAttribute(R.attr.altarixUiAttrTextColor, outValue, false);
        if(!itsOk){
            throw new IllegalStateException("You must set parent theme 'AltarixUiTeme'");
        }
    }

    /*******************************************************
     * Working to image load
     ******************************************************/
    private static ImageLoader getImageLoader(Context c){
        if(ImageLoader.getInstance().isInited())
            return ImageLoader.getInstance();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(c)
                .defaultDisplayImageOptions(getImageLoaderOptions().build())
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // 2 Mb
                .build();

        ImageLoader.getInstance().init(config);
        return ImageLoader.getInstance();
    }

    private static DisplayImageOptions.Builder getImageLoaderOptions(){
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .displayer(new FadeInBitmapDisplayer(400, true, true, false));
    }

}
