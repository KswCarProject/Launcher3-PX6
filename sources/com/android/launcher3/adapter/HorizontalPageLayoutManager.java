package com.android.launcher3.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

public class HorizontalPageLayoutManager extends RecyclerView.LayoutManager implements PageDecorationLastJudge {
    private SparseArray<Rect> allItemFrames = new SparseArray<>();
    int columns = 0;
    int itemHeight = 0;
    int itemHeightUsed;
    int itemWidth = 0;
    int itemWidthUsed;
    int offsetX = 0;
    int offsetY = 0;
    int onePageSize = 0;
    int pageSize = 0;
    int rows = 0;
    int totalHeight = 0;
    int totalWidth = 0;

    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }

    public HorizontalPageLayoutManager(int rows2, int columns2) {
        this.rows = rows2;
        this.columns = columns2;
        this.onePageSize = rows2 * columns2;
    }

    public boolean canScrollHorizontally() {
        return true;
    }

    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int newX = this.offsetX + dx;
        int result = dx;
        if (newX > this.totalWidth) {
            result = this.totalWidth - this.offsetX;
        } else if (newX < 0) {
            result = 0 - this.offsetX;
        }
        this.offsetX += result;
        offsetChildrenHorizontal(-result);
        recycleAndFillItems(recycler, state);
        return result;
    }

    private int getUsableWidth() {
        return (getWidth() - getPaddingLeft()) - getPaddingRight();
    }

    private int getUsableHeight() {
        return (getHeight() - getPaddingTop()) - getPaddingBottom();
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int p;
        if (getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
        } else if (!state.isPreLayout()) {
            this.itemWidth = getUsableWidth() / this.columns;
            this.itemHeight = getUsableHeight() / this.rows;
            this.itemWidthUsed = (this.columns - 1) * this.itemWidth;
            this.itemHeightUsed = (this.rows - 1) * this.itemHeight;
            computePageSize(state);
            Log.i("zzz", "itemCount=" + getItemCount() + " state itemCount=" + state.getItemCount() + " pageSize=" + this.pageSize);
            this.totalWidth = (this.pageSize + -1) * getWidth();
            detachAndScrapAttachedViews(recycler);
            int count = getItemCount();
            for (int p2 = 0; p2 < this.pageSize; p2 = p + 1) {
                p = p2;
                int r = 0;
                while (r < this.rows) {
                    int c = 0;
                    while (true) {
                        if (c >= this.columns) {
                            break;
                        }
                        int index = (this.onePageSize * p) + (this.columns * r) + c;
                        if (index == count) {
                            int c2 = this.columns;
                            r = this.rows;
                            p = this.pageSize;
                            break;
                        }
                        View view = recycler.getViewForPosition(index);
                        addView(view);
                        measureChildWithMargins(view, this.itemWidthUsed, this.itemHeightUsed);
                        int width = getDecoratedMeasuredWidth(view);
                        int height = getDecoratedMeasuredHeight(view);
                        Rect rect = this.allItemFrames.get(index);
                        if (rect == null) {
                            rect = new Rect();
                        }
                        int x = (getUsableWidth() * p) + (this.itemWidth * c);
                        int y = this.itemHeight * r;
                        rect.set(x, y, width + x, height + y);
                        this.allItemFrames.put(index, rect);
                        c++;
                    }
                    RecyclerView.Recycler recycler2 = recycler;
                    r++;
                }
                RecyclerView.Recycler recycler3 = recycler;
                removeAndRecycleAllViews(recycler);
            }
            RecyclerView.Recycler recycler4 = recycler;
            recycleAndFillItems(recycler, state);
        }
    }

    private void computePageSize(RecyclerView.State state) {
        this.pageSize = (state.getItemCount() / this.onePageSize) + (state.getItemCount() % this.onePageSize == 0 ? 0 : 1);
    }

    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        this.offsetX = 0;
        this.offsetY = 0;
    }

    private void recycleAndFillItems(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (!state.isPreLayout()) {
            Rect displayRect = new Rect(getPaddingLeft() + this.offsetX, getPaddingTop(), ((getWidth() - getPaddingLeft()) - getPaddingRight()) + this.offsetX, (getHeight() - getPaddingTop()) - getPaddingBottom());
            Rect childRect = new Rect();
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                childRect.left = getDecoratedLeft(child);
                childRect.top = getDecoratedTop(child);
                childRect.right = getDecoratedRight(child);
                childRect.bottom = getDecoratedBottom(child);
                if (!Rect.intersects(displayRect, childRect)) {
                    removeAndRecycleView(child, recycler);
                }
            }
            for (int i2 = 0; i2 < getItemCount(); i2++) {
                if (Rect.intersects(displayRect, this.allItemFrames.get(i2))) {
                    View view = recycler.getViewForPosition(i2);
                    addView(view);
                    measureChildWithMargins(view, this.itemWidthUsed, this.itemHeightUsed);
                    Rect rect = this.allItemFrames.get(i2);
                    layoutDecorated(view, rect.left - this.offsetX, rect.top, rect.right - this.offsetX, rect.bottom);
                }
            }
        }
    }

    public boolean isLastRow(int index) {
        int indexOfPage;
        if (index < 0 || index >= getItemCount() || (indexOfPage = (index % this.onePageSize) + 1) <= (this.rows - 1) * this.columns || indexOfPage > this.onePageSize) {
            return false;
        }
        return true;
    }

    public boolean isLastColumn(int position) {
        if (position < 0 || position >= getItemCount() || (position + 1) % this.columns != 0) {
            return false;
        }
        return true;
    }

    public boolean isPageLast(int position) {
        if ((position + 1) % this.onePageSize == 0) {
            return true;
        }
        return false;
    }

    public int computeHorizontalScrollRange(RecyclerView.State state) {
        computePageSize(state);
        return this.pageSize * getWidth();
    }

    public int computeHorizontalScrollOffset(RecyclerView.State state) {
        return this.offsetX;
    }

    public int computeHorizontalScrollExtent(RecyclerView.State state) {
        return getWidth();
    }
}
