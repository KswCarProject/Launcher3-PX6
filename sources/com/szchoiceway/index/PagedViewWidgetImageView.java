package com.szchoiceway.index;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

class PagedViewWidgetImageView extends ImageView {
    public boolean mAllowRequestLayout = true;

    public PagedViewWidgetImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void requestLayout() {
        if (this.mAllowRequestLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.save();
        canvas.clipRect(getScrollX() + getPaddingLeft(), getScrollY() + getPaddingTop(), ((getScrollX() + getRight()) - getLeft()) - getPaddingRight(), ((getScrollY() + getBottom()) - getTop()) - getPaddingBottom());
        super.onDraw(canvas);
        canvas.restore();
    }
}
