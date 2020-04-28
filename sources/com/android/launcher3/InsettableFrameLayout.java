package com.android.launcher3;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class InsettableFrameLayout extends FrameLayout implements Insettable {
    @ViewDebug.ExportedProperty(category = "launcher")
    protected Rect mInsets = new Rect();

    public Rect getInsets() {
        return this.mInsets;
    }

    public InsettableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFrameLayoutChildInsets(View child, Rect newInsets, Rect oldInsets) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (child instanceof Insettable) {
            ((Insettable) child).setInsets(newInsets);
        } else if (!lp.ignoreInsets) {
            lp.topMargin += newInsets.top - oldInsets.top;
            lp.leftMargin += newInsets.left - oldInsets.left;
            lp.rightMargin += newInsets.right - oldInsets.right;
            lp.bottomMargin += newInsets.bottom - oldInsets.bottom;
        }
        child.setLayoutParams(lp);
    }

    public void setInsets(Rect insets) {
        int n = getChildCount();
        for (int i = 0; i < n; i++) {
            setFrameLayoutChildInsets(getChildAt(i), insets, this.mInsets);
        }
        this.mInsets.set(insets);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        boolean ignoreInsets = false;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.InsettableFrameLayout_Layout);
            this.ignoreInsets = a.getBoolean(0, false);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams lp) {
            super(lp);
        }
    }

    public void onViewAdded(View child) {
        super.onViewAdded(child);
        setFrameLayoutChildInsets(child, this.mInsets, new Rect());
    }

    public static void dispatchInsets(ViewGroup parent, Rect insets) {
        int n = parent.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = parent.getChildAt(i);
            if (child instanceof Insettable) {
                ((Insettable) child).setInsets(insets);
            }
        }
    }
}
