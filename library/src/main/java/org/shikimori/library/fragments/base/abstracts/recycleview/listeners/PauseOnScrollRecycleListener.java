package org.shikimori.library.fragments.base.abstracts.recycleview.listeners;

import android.support.v7.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class PauseOnScrollRecycleListener extends RecyclerView.OnScrollListener {

    private ImageLoader imageLoader;

    private final boolean pauseOnScroll;
    private final boolean pauseOnSettling;
    private final RecyclerView.OnScrollListener externalListener;

    private boolean stopped = false;

    public PauseOnScrollRecycleListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnSettling) {
        this(imageLoader, pauseOnScroll, pauseOnSettling, null);
    }

    public PauseOnScrollRecycleListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnSettling,
                                        RecyclerView.OnScrollListener customListener) {
        this.imageLoader = imageLoader;
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnSettling = pauseOnSettling;
        externalListener = customListener;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//    Log.v("SCR", "onScrollStateChanged");
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
//            Log.d("SCR", "onScrollStateChanged :  + SCROLL_STATE_IDLE");
                imageLoader.resume();
                stopped = false;
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
//            Log.d("SCR", "onScrollStateChanged :  + SCROLL_STATE_DRAGGING");
                if (pauseOnScroll) {
                    imageLoader.pause();
                    stopped = true;
                } else if (stopped) {
                    imageLoader.resume();
                    stopped = false;
                }
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
//            Log.d("SCR", "onScrollStateChanged :  + SCROLL_STATE_SETTLING");
                if (pauseOnSettling) {
                    imageLoader.pause();
                    stopped = true;
                } else if (stopped) {
                    imageLoader.resume();
                    stopped = false;
                }
                break;
        }
        if (externalListener != null) {
            externalListener.onScrollStateChanged(recyclerView, newState);
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//    Log.i("SCR", "onScrolled + dx:"+dx+"  dy:"+dy);
        if (externalListener != null) {
            externalListener.onScrolled(recyclerView, dx, dy);
        }
    }
}

