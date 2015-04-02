package org.shikimori.library.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.AdapterView;


/**
 * Created by Феофилактов on 26.01.2015.
 */
public class PaggingGridView2 extends GridViewWithHeaderAndFooter {
    private AbsListView.OnScrollListener onScrollListener;
    private OnEndListListener listener;
    private boolean hasMore;

    public PaggingGridView2(Context context) {
        super(context);
    }

    public PaggingGridView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PaggingGridView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
        super.setOnScrollListener(onScrollLoadMore);
    }

    public interface OnEndListListener {
        public void endReach();
    }

    public void setOnEndListListener(OnEndListListener listener) {
        this.listener = listener;
    }

    public void setHasMoreItems(boolean hasMore) {
        this.hasMore = hasMore;
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
    }

    AbsListView.OnScrollListener onScrollLoadMore = new AbsListView.OnScrollListener() {

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (onScrollListener != null)
                onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            if (totalItemCount > 0) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                if (hasMore && (lastVisibleItem == totalItemCount)) {
                    if (listener != null) {
                        hasMore = false;
                        listener.endReach();
                    }

                }
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (onScrollListener != null)
                onScrollListener.onScrollStateChanged(view, scrollState);
        }
    };
}
