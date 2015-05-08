package org.shikimori.library.tool.imagetool;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.shikimori.library.R;
import org.shikimori.library.activity.ImageShowActivity;
import org.shikimori.library.tool.RelevalCircular;
import org.shikimori.library.tool.baselisteners.BaseAnimationListener;
import org.shikimori.library.tool.h;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by Владимир on 21.04.2015.
 */
public class ThumbToImage {
    private final ImageLoader iMageLoader;
    private Activity mContext;
    private AnimatorSet mCurrentAnimator;
    boolean isLoad = false;
    private ViewGroup root;
    private View back;
    private ImageView expandedImage;
    private ProgressBar pbLoaderExpanded;
    private Rect startBounds;
    private Rect finalBounds;
    private Point globalOffset;
    float startScale;
    RelevalCircular rev;
    private View wraper;
    PhotoViewAttacher mAttacher;
    private ViewPager viewPager;
    private ImageView thumbView;
    private List<Thumb> list;

    public ThumbToImage(Activity mContext) {
        this.mContext = mContext;
        iMageLoader = ImageLoader.getInstance();
        rev = new RelevalCircular(mContext);
        initRootView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void zoom(final ImageView thumbView, int selectedPosition, List<Thumb> list){
        this.thumbView = thumbView;
        this.list = list;

        h.setVisible(wraper, true);
        h.setVisible(back, true);
        YoYo.with(Techniques.FadeIn)
                .duration(300)
                .playOn(back);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        if(Build.VERSION.SDK_INT > 10){
            expandedImage.setImageDrawable(thumbView.getDrawable());
            initBounds();
            animateIn(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    viewPager.setVisibility(View.VISIBLE);
                    expandedImage.setVisibility(View.GONE);
                }
            });
        } else
            viewPager.setVisibility(View.VISIBLE);

        List<View> views = new ArrayList<>();

        for (Thumb thumb : list) {
            ImageView image = new ImageView(thumbView.getContext());

            image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            final PhotoViewAttacher atacher = new PhotoViewAttacher(image);
            image.setImageDrawable(thumbView.getDrawable());
            mAttacher.update();
            iMageLoader.displayImage(thumb.origin, image, new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    atacher.update();
                }
            });
            views.add(image);
        }
        ThumbPagerAdapter adapter = new ThumbPagerAdapter(views);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectedPosition);

    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void zoom(final ImageView thumbView, String url) {

        if(url == null)
            return;

        isLoad = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            showInActivity(url);
            return;
        }

        h.setVisible(wraper, true);
        h.setVisible(back, true);
        YoYo.with(Techniques.FadeIn)
                .duration(300)
                .playOn(back);
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        expandedImage.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void onClick(View view) {
                closeImage();
            }
        });

        if (pbLoaderExpanded != null)
            pbLoaderExpanded.setProgress(0);

        expandedImage.setImageDrawable(thumbView.getDrawable());
        mAttacher.update();
        if (Build.VERSION.SDK_INT > 20) {
            rev.setType(RelevalCircular.TYPE.VIEW);
//            expandedImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            rev.setCustomRadius(expandedImage.getRootView().getWidth(),
//                    expandedImage.getRootView().getHeight());
            rev.startProgress(expandedImage, null);
            rev.setProgress(20);
        }

        if (h.getConnection(mContext)) {
            DisplayImageOptions.Builder options = h.getImageLoaderOptionsBuilder();
            options.displayer(new SimpleBitmapDisplayer());
            options.resetViewBeforeLoading(false);

            iMageLoader.displayImage(url, expandedImage, options.build(), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            if (Build.VERSION.SDK_INT > 20)
                                rev.setProgress(100);
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            isLoad = true;
                            if (Build.VERSION.SDK_INT > 20)
                                rev.setProgress(100);
                            else if (pbLoaderExpanded.getVisibility() == View.VISIBLE)
                                YoYo.with(Techniques.SlideOutDown)
                                    .duration(300)
                                    .withListener(new BaseAnimationListener() {
                                        @Override
                                        public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                                            super.onAnimationEnd(animation);
                                            h.setVisibleGone(pbLoaderExpanded);
                                        }
                                    });
                            mAttacher.update();
                        }
                    },
                    new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String url, View view, int count, int all_count) {
                            int progress = count * 100 / all_count;
                            if (Build.VERSION.SDK_INT > 20) {
                                if (progress < 100)
                                    rev.setProgress(progress);
                            } else
                                pbLoaderExpanded.setProgress(count * 100 / all_count);
                        }
                    }
            );

        } else {
            if (Build.VERSION.SDK_INT > 20)
                rev.setProgress(100);
        }

        if (Build.VERSION.SDK_INT > 20) {
            return;
        }

        //iMageLoader.displayImage(imageResId, expandedImageView);

        initBounds();
        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        animateIn(null);
    }

    void initBounds(){
        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        startBounds = new Rect();
        finalBounds = new Rect();
        globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        thumbView.getRootView()
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -(globalOffset.y));
        finalBounds.offset(-globalOffset.x, -globalOffset.y);


        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }


        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        //thumbView.setAlpha(0f);

        //expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImage.setPivotX(0f);
        expandedImage.setPivotY(0f);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    void animateIn(final AnimatorListenerAdapter listener) {
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImage, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImage, View.Y,
                        startBounds.top - h.pxToDp(64, mContext), finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImage,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
                if (isLoad)
                    return;
                h.setVisible(pbLoaderExpanded, true);
                YoYo.with(Techniques.SlideInDown)
                        .playOn(pbLoaderExpanded);
                if(listener!= null)
                    listener.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                h.setVisibleGone(pbLoaderExpanded);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }

    void clearViewPager(){
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean closeImage() {
        if (wraper.getVisibility() != View.VISIBLE)
            return false;

        viewPager.setVisibility(View.GONE);
        if(Build.VERSION.SDK_INT < 11){
            wraper.setVisibility(View.GONE);
            viewPager.setAdapter(null);
            return true;
        }

        boolean isViewPagerOpen = viewPager.getChildCount() > 0;

        if(isViewPagerOpen){
            ImageView img = (ImageView) viewPager.getChildAt(viewPager.getCurrentItem());
            expandedImage.setImageDrawable(img.getDrawable());
        }

        YoYo.with(Techniques.FadeOut)
            .duration(200)
            .withListener(new BaseAnimationListener() {
                @Override
                public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                    super.onAnimationEnd(animation);
                    h.setVisibleGone(back);
                }
            })
            .playOn(back);
        h.setVisibleGone(pbLoaderExpanded);
        h.setVisible(expandedImage, true);


        if (!isViewPagerOpen && Build.VERSION.SDK_INT > 20) {
            rev.closeReleval(new RelevalCircular.OnCircleEndAnimation() {
                @Override
                public void animateEnd() {
                    expandedImage.setImageDrawable(null);
                    h.setVisible(wraper, false);
                    viewPager.setAdapter(null);
//                    mAttacher.cleanup();
                }
            });
            return true;
        }

        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        iMageLoader.stop();
        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
            .ofFloat(expandedImage, View.X, startBounds.left))
            .with(ObjectAnimator
                    .ofFloat(expandedImage,
                            View.Y, startBounds.top - h.pxToDp(64, mContext)))
            .with(ObjectAnimator
                    .ofFloat(expandedImage,
                            View.SCALE_X, startScale))
            .with(ObjectAnimator
                    .ofFloat(expandedImage,
                            View.SCALE_Y, startScale));
        set.setDuration(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //thumbView.setAlpha(1f);
                //back.setVisibility(View.GONE);
                expandedImage.setImageDrawable(null);
                viewPager.setAdapter(null);
                h.setVisible(wraper, false);
//                mAttacher.cleanup();
                //expandedImageView.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }
        });
        set.start();
        mCurrentAnimator = set;

        return true;
    }

    private void showInActivity(String url) {
        Intent intent = new Intent(mContext, ImageShowActivity.class);
        intent.putExtra(ImageShowActivity.IMAGE_URL, url);
        mContext.startActivity(intent);
    }

    void initRootView() {
        ViewGroup decorView = (ViewGroup) mContext.findViewById(android.R.id.content);
        if (decorView != null) {
            View firstChaild = decorView.getChildAt(0);

            if (!(firstChaild instanceof RelativeLayout) && !(firstChaild instanceof FrameLayout)) {
                FrameLayout rootViewNew = new FrameLayout(mContext);
                rootViewNew.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                decorView.removeView(firstChaild);
                rootViewNew.addView(firstChaild);
                decorView.addView(rootViewNew);
                root = rootViewNew;
            } else {
                root = (ViewGroup) firstChaild;
            }

            back = root.findViewById(R.id.llbackExpandedImage);
            if (back == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                wraper = inflater.inflate(R.layout.view_thumb_to_image, root, false);
                root.addView(wraper);
                back = root.findViewById(R.id.llbackExpandedImage);
            } else {
                wraper = (View) back.getParent();
            }
            expandedImage = (ImageView) root.findViewById(R.id.expanded_image);
            mAttacher = new PhotoViewAttacher(expandedImage);
            pbLoaderExpanded = (ProgressBar) root.findViewById(R.id.pbLoaderExpanded);
            viewPager = (ViewPager) root.findViewById(R.id.viewPager);
        }
    }

    public static class Thumb{
        String thumb;
        String origin;
        public Thumb(String thumb, String origin){
            this.thumb = thumb;
            this.origin = origin;
        }
        public Thumb(){}

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }
    }
}