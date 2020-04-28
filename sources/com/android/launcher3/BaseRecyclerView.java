package com.android.launcher3;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.launcher3.views.RecyclerViewFastScroller;

public abstract class BaseRecyclerView extends RecyclerView {
    protected RecyclerViewFastScroller mScrollbar;

    /* access modifiers changed from: protected */
    public abstract int getAvailableScrollHeight();

    public abstract int getCurrentScrollY();

    public abstract void onUpdateScrollbar(int i);

    public abstract String scrollToPositionAtProgress(float f);

    public BaseRecyclerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        bindFastScrollbar();
    }

    public void bindFastScrollbar() {
        ViewGroup parent = (ViewGroup) getParent().getParent();
        this.mScrollbar = (RecyclerViewFastScroller) parent.findViewById(R.id.fast_scroller);
        this.mScrollbar.setRecyclerView(this, (TextView) parent.findViewById(R.id.fast_scroller_popup));
        onUpdateScrollbar(0);
    }

    public RecyclerViewFastScroller getScrollbar() {
        return this.mScrollbar;
    }

    public int getScrollBarTop() {
        return getPaddingTop();
    }

    public int getScrollbarTrackHeight() {
        return (this.mScrollbar.getHeight() - getScrollBarTop()) - getPaddingBottom();
    }

    /* access modifiers changed from: protected */
    public int getAvailableScrollBarHeight() {
        return getScrollbarTrackHeight() - this.mScrollbar.getThumbHeight();
    }

    /* access modifiers changed from: protected */
    public void synchronizeScrollBarThumbOffsetToViewScroll(int scrollY, int availableScrollHeight) {
        if (availableScrollHeight <= 0) {
            this.mScrollbar.setThumbOffsetY(-1);
            return;
        }
        this.mScrollbar.setThumbOffsetY((int) ((((float) scrollY) / ((float) availableScrollHeight)) * ((float) getAvailableScrollBarHeight())));
    }

    public boolean shouldContainerScroll(MotionEvent ev, View eventSource) {
        int[] point = {(int) ev.getX(), (int) ev.getY()};
        Utilities.mapCoordInSelfToDescendant(this.mScrollbar, eventSource, point);
        if (!this.mScrollbar.shouldBlockIntercept(point[0], point[1]) && getCurrentScrollY() == 0) {
            return true;
        }
        return false;
    }

    public boolean supportsFastScrolling() {
        return true;
    }

    public void onFastScrollCompleted() {
    }
}
