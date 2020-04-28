package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.BaseRecyclerView;
import com.android.launcher3.R;

public class WidgetsRecyclerView extends BaseRecyclerView implements RecyclerView.OnItemTouchListener {
    private WidgetsListAdapter mAdapter;
    private final Point mFastScrollerOffset;
    private final int mScrollbarTop;
    private boolean mTouchDownOnScroller;

    public WidgetsRecyclerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public WidgetsRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetsRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mFastScrollerOffset = new Point();
        this.mScrollbarTop = getResources().getDimensionPixelSize(R.dimen.dynamic_grid_edge_margin);
        addOnItemTouchListener(this);
    }

    public WidgetsRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        this.mAdapter = (WidgetsListAdapter) adapter;
    }

    public String scrollToPositionAtProgress(float touchFraction) {
        if (isModelNotReady()) {
            return "";
        }
        stopScroll();
        float pos = ((float) this.mAdapter.getItemCount()) * touchFraction;
        ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(0, (int) (-(((float) getAvailableScrollHeight()) * touchFraction)));
        return this.mAdapter.getSectionName((int) (touchFraction == 1.0f ? pos - 1.0f : pos));
    }

    public void onUpdateScrollbar(int dy) {
        if (!isModelNotReady()) {
            int scrollY = getCurrentScrollY();
            if (scrollY < 0) {
                this.mScrollbar.setThumbOffsetY(-1);
            } else {
                synchronizeScrollBarThumbOffsetToViewScroll(scrollY, getAvailableScrollHeight());
            }
        }
    }

    public int getCurrentScrollY() {
        if (isModelNotReady() || getChildCount() == 0) {
            return -1;
        }
        View child = getChildAt(0);
        int rowIndex = getChildPosition(child);
        return (getPaddingTop() + (child.getMeasuredHeight() * rowIndex)) - getLayoutManager().getDecoratedTop(child);
    }

    /* access modifiers changed from: protected */
    public int getAvailableScrollHeight() {
        return ((getChildAt(0).getMeasuredHeight() * this.mAdapter.getItemCount()) - getScrollbarTrackHeight()) - this.mScrollbarTop;
    }

    private boolean isModelNotReady() {
        return this.mAdapter.getItemCount() == 0;
    }

    public int getScrollBarTop() {
        return this.mScrollbarTop;
    }

    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (e.getAction() == 0) {
            this.mTouchDownOnScroller = this.mScrollbar.isHitInParent(e.getX(), e.getY(), this.mFastScrollerOffset);
        }
        if (this.mTouchDownOnScroller) {
            return this.mScrollbar.handleTouchEvent(e, this.mFastScrollerOffset);
        }
        return false;
    }

    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        if (this.mTouchDownOnScroller) {
            this.mScrollbar.handleTouchEvent(e, this.mFastScrollerOffset);
        }
    }

    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
