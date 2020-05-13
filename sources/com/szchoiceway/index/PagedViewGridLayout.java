package com.szchoiceway.index;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;

public class PagedViewGridLayout extends GridLayout implements Page {
    static final String TAG = "PagedViewGridLayout";
    private int mCellCountX;
    private int mCellCountY;
    private Runnable mOnLayoutListener;

    public PagedViewGridLayout(Context context, int cellCountX, int cellCountY) {
        super(context, (AttributeSet) null, 0);
        this.mCellCountX = cellCountX;
        this.mCellCountY = cellCountY;
    }

    /* access modifiers changed from: package-private */
    public int getCellCountX() {
        return this.mCellCountX;
    }

    /* access modifiers changed from: package-private */
    public int getCellCountY() {
        return this.mCellCountY;
    }

    public void resetChildrenOnKeyListeners() {
        int childCount = getChildCount();
        for (int j = 0; j < childCount; j++) {
            getChildAt(j).setOnKeyListener((View.OnKeyListener) null);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(getSuggestedMinimumWidth(), View.MeasureSpec.getSize(widthMeasureSpec)), 1073741824), heightMeasureSpec);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mOnLayoutListener = null;
    }

    public void setOnLayoutListener(Runnable r) {
        this.mOnLayoutListener = r;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mOnLayoutListener != null) {
            this.mOnLayoutListener.run();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        int count = getPageChildCount();
        if (count <= 0) {
            return result;
        }
        return result || event.getY() < ((float) getChildOnPageAt(count + -1).getBottom());
    }

    public void removeAllViewsOnPage() {
        removeAllViews();
        this.mOnLayoutListener = null;
        setLayerType(0, (Paint) null);
    }

    public void removeViewOnPageAt(int index) {
        removeViewAt(index);
    }

    public int getPageChildCount() {
        return getChildCount();
    }

    public View getChildOnPageAt(int i) {
        return getChildAt(i);
    }

    public int indexOfChildOnPage(View v) {
        return indexOfChild(v);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}
