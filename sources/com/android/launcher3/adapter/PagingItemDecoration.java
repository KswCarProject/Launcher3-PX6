package com.android.launcher3.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PagingItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = {16843284};
    private Drawable mDivider;
    PageDecorationLastJudge mPageDecorationLastJudge;

    public PagingItemDecoration(Context context, PageDecorationLastJudge pageDecorationLastJudge) {
        if (pageDecorationLastJudge != null) {
            this.mPageDecorationLastJudge = pageDecorationLastJudge;
            TypedArray a = context.obtainStyledAttributes(ATTRS);
            this.mDivider = a.getDrawable(0);
            a.recycle();
            return;
        }
        throw new IllegalArgumentException("pageDecorationLastJudge must be no null");
    }

    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getLeft() - params.leftMargin;
            int right = child.getRight() + params.rightMargin + this.mDivider.getIntrinsicWidth();
            int top = child.getBottom() + params.bottomMargin;
            this.mDivider.setBounds(left, top, right, this.mDivider.getIntrinsicHeight() + top);
            this.mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getTop() - params.topMargin;
            int bottom = child.getBottom() + params.bottomMargin;
            int left = child.getRight() + params.rightMargin;
            this.mDivider.setBounds(left, top, this.mDivider.getIntrinsicWidth() + left, bottom);
            this.mDivider.draw(c);
        }
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemPosition = parent.getChildAdapterPosition(view);
        if (this.mPageDecorationLastJudge.isPageLast(itemPosition)) {
            outRect.set(0, 0, 0, 0);
        } else if (this.mPageDecorationLastJudge.isLastRow(itemPosition)) {
            outRect.set(0, 0, this.mDivider.getIntrinsicWidth(), 0);
        } else if (this.mPageDecorationLastJudge.isLastColumn(itemPosition)) {
            outRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, this.mDivider.getIntrinsicWidth(), this.mDivider.getIntrinsicHeight());
        }
    }
}
