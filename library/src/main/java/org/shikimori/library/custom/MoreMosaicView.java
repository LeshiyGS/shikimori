package org.shikimori.library.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcgars.imagefactory.objects.Thumb;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.shikimori.library.R;
import org.shikimori.library.objects.one.Video;
import org.shikimori.library.tool.hs;

import java.util.List;

import ru.altarix.basekit.library.tools.h;

/**
 * Created by Феофилактов on 06.01.2016.
 */
public class MoreMosaicView extends MosaicView {

    private int width,height,moreSize;
    private LayoutInflater inflater;
    private OnClickListener onItemClickListener;
    private OnClickListener onMoreClickListener;
    private boolean more = true;

    public MoreMosaicView(Context context) {
        this(context, null);
    }

    public MoreMosaicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoreMosaicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        width = h.pxToDp(200,getContext());
        height = h.pxToDp(113,getContext());
        moreSize = h.pxToDp(84,getContext());
        inflater = LayoutInflater.from(getContext());
    }

    public void setOnItemClickListener(OnClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnMoreClickListener(OnClickListener onMoreClickListener){
        this.onMoreClickListener = onMoreClickListener;
    }

    public void setList(List<? extends Thumb> list, boolean isvideo){
        removeAllViews();
        for (Thumb image : list) {
            View iv = isvideo ? getVideoView(image) : getMosaicView(image);
            iv.setTag(image);
            iv.setOnClickListener(onItemClickListener);
//            addView(iv);
        }

        if(more)
            addView(getMoreMosaic());
        rebuildViews();
    }

    private View getMoreMosaic(){
        TextView v = new TextView(getContext());
        v.setLayoutParams(new ViewGroup.LayoutParams(moreSize, moreSize));
        v.setText(R.string.more);
        v.setGravity(Gravity.CENTER);
        v.setTextColor(Color.WHITE);
        v.setBackgroundColor(Color.DKGRAY);
        v.setOnTouchListener(hs.getImageHighlight);
        v.setOnClickListener(onMoreClickListener);
        return v;
    }

    private ImageView getMosaicView(Thumb thumb){
        ImageView iv = new ImageView(getContext());
        iv.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setBackgroundColor(Color.DKGRAY);
        iv.setOnTouchListener(hs.getImageHighlight);
        addView(iv);
        ImageLoader.getInstance().displayImage(thumb.getThumb(), iv, loadComplete);
        return iv;
    }

    SimpleImageLoadingListener loadComplete = new SimpleImageLoadingListener(){
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            rebuildViews();
        }
    };

    private View getVideoView(Thumb thumb){
        Video video = (Video) thumb;
        View v = inflater.inflate(R.layout.item_shiki_video, this, false);

        ViewGroup.LayoutParams params = v.getLayoutParams();
        params.height = height;
        params.width = width;
        v.setLayoutParams(params);

        ImageView iv = (ImageView) v.findViewById(R.id.ivImage);
        TextView tvType = (TextView) v.findViewById(R.id.tvType);
        tvType.setText(video.getKind());
//        v.setLayoutParams(new ViewGroup.LayoutParams(width, height));
//        ViewGroup.LayoutParams ivParams = iv.getLayoutParams();
//        ivParams.height = height;
//        ivParams.width = width;
//        iv.setLayoutParams(ivParams);

        iv.setOnTouchListener(hs.getImageHighlight);
        addView(v);
        ImageLoader.getInstance().displayImage(thumb.getThumb(), iv);
        return v;
    }

    public void setHasMore(boolean more) {
        this.more = more;
    }
}
