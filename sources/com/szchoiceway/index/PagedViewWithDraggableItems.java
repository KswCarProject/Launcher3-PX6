package com.szchoiceway.index;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public abstract class PagedViewWithDraggableItems extends PagedView implements View.OnLongClickListener, View.OnTouchListener {
    private float mDragSlopeThreshold;
    private boolean mIsDragEnabled;
    private boolean mIsDragging;
    private View mLastTouchedItem;
    private Launcher mLauncher;

    public PagedViewWithDraggableItems(Context context) {
        this(context, (AttributeSet) null);
    }

    public PagedViewWithDraggableItems(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedViewWithDraggableItems(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLauncher = (Launcher) context;
    }

    /* access modifiers changed from: protected */
    public boolean beginDragging(View v) {
        boolean wasDragging = this.mIsDragging;
        this.mIsDragging = true;
        if (!wasDragging) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void cancelDragging() {
        this.mIsDragging = false;
        this.mLastTouchedItem = null;
        this.mIsDragEnabled = false;
    }

    private void handleTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & 255) {
            case 0:
                cancelDragging();
                this.mIsDragEnabled = true;
                return;
            case 2:
                if (this.mTouchState != 1 && !this.mIsDragging && this.mIsDragEnabled) {
                    determineDraggingStart(ev);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        handleTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        handleTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    public boolean onTouch(View v, MotionEvent event) {
        this.mLastTouchedItem = v;
        this.mIsDragEnabled = true;
        return false;
    }

    public boolean onLongClick(View v) {
        if (v.isInTouchMode() && this.mNextPage == -1 && this.mLauncher.isAllAppsVisible() && !this.mLauncher.getWorkspace().isSwitchingState() && this.mLauncher.isDraggingEnabled()) {
            return beginDragging(v);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent ev) {
        if (!this.mIsDragging) {
            super.determineScrollingStart(ev);
        }
    }

    /* access modifiers changed from: protected */
    public void determineDraggingStart(MotionEvent ev) {
        boolean yMoved;
        boolean isUpwardMotion;
        int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
        float x = ev.getX(pointerIndex);
        float y = ev.getY(pointerIndex);
        int xDiff = (int) Math.abs(x - this.mLastMotionX);
        int yDiff = (int) Math.abs(y - this.mLastMotionY);
        if (yDiff > this.mTouchSlop) {
            yMoved = true;
        } else {
            yMoved = false;
        }
        if (((float) yDiff) / ((float) xDiff) > this.mDragSlopeThreshold) {
            isUpwardMotion = true;
        } else {
            isUpwardMotion = false;
        }
        if (isUpwardMotion && yMoved && this.mLastTouchedItem != null) {
            beginDragging(this.mLastTouchedItem);
            if (this.mAllowLongPress) {
                this.mAllowLongPress = false;
                View currentPage = getPageAt(this.mCurrentPage);
                if (currentPage != null) {
                    currentPage.cancelLongPress();
                }
            }
        }
    }

    public void setDragSlopeThreshold(float dragSlopeThreshold) {
        this.mDragSlopeThreshold = dragSlopeThreshold;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        cancelDragging();
        super.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onPageBeginMoving() {
        showScrollingIndicator(false);
    }

    /* access modifiers changed from: protected */
    public void onPageEndMoving() {
        hideScrollingIndicator(false);
    }
}
