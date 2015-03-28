package ru.altarix.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Simple layout which shows only first its view.
 *
 * @author cab404
 */
public class CollapsibleLayout extends LinearLayout {

    /**
     * If view is expanded
     */
    protected boolean isExpanded = false;

    /**
     * Used by click recognizer: if header was touched
     */
    protected boolean headerWasTouched = false;

    /**
     * Listener
     */
    protected ExpandListener expandListener = null;

    public CollapsibleLayout(Context context) {
        super(context);
    }

    public CollapsibleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected boolean checkIfInHeader(MotionEvent event) {
        View header = getChildAt(0);
        return event.getX() > header.getLeft() && event.getX() < header.getRight() && event.getY() > header.getTop() && event.getY() < header.getBottom();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (checkIfInHeader(event)) {
            getChildAt(0).onTouchEvent(event);
            /* If header was pressed...*/
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                /* ...when changing pressed state. */
                headerWasTouched = true;

            /* If header was released and pressed before...*/
            if (event.getAction() == MotionEvent.ACTION_UP && headerWasTouched)
                /* ...when triggering event. */
                triggerTopViewClicked();

            return true;

        }
        return super.onTouchEvent(event);
    }

    /**
     * Triggered when click is recognized on header
     */
    protected void triggerTopViewClicked() {
        setExpanded(!isExpanded);
    }

    /**
     * Sets expanded state
     */
    public void setExpanded(boolean state) {
        isExpanded = state;

        if (isInEditMode()) return;

        if (isExpanded) {
            /* Showing all of them! */
            for (int $i = 1; $i < getChildCount(); $i++) {
                getChildAt($i).setVisibility(VISIBLE);
            }
        } else {
            /* Hiding all of them! */
            for (int $i = 1; $i < getChildCount(); $i++) {
                getChildAt($i).setVisibility(GONE);
            }
        }

        if (expandListener != null)
            expandListener.onStateChanged(isExpanded);

        requestLayout();
    }

    /**
     * Tells you if collapsible part is expanded
     */
    public boolean isExpanded() {
        return !isExpanded;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        /**
         *  Syncing state of the view with our params
         */
        if (getChildCount() != 0)
            child.setVisibility(isExpanded ? VISIBLE : GONE);
        super.addView(child, index, params);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /* We cannot really work if there's nothing to work with. */
        if (getChildCount() == 0)
            throw new RuntimeException("Cannot layout - should be at least one view inside.");

        super.onLayout(changed, l, t, r, b);

    }

    public ExpandListener getExpandListener() {
        return expandListener;
    }

    public void setExpandListener(ExpandListener expandListener) {
        this.expandListener = expandListener;
    }

    public interface ExpandListener {
        public void onStateChanged(boolean expanded);
    }
}
