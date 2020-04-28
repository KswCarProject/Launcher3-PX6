package com.android.launcher3.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class WorkFooterContainer extends RelativeLayout {
    public WorkFooterContainer(Context context) {
        super(context);
    }

    public WorkFooterContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorkFooterContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        updateTranslation();
    }

    public void offsetTopAndBottom(int offset) {
        super.offsetTopAndBottom(offset);
        updateTranslation();
    }

    private void updateTranslation() {
        if (getParent() instanceof View) {
            View parent = (View) getParent();
            setTranslationY((float) Math.max(0, (parent.getHeight() - parent.getPaddingBottom()) - getBottom()));
        }
    }
}
