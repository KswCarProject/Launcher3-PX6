package com.szchoiceway.index;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HolographicLinearLayout extends LinearLayout {
    private final HolographicViewHelper mHolographicHelper;
    private ImageView mImageView;
    private int mImageViewId;

    public HolographicLinearLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public HolographicLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HolographicLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HolographicLinearLayout, defStyle, 0);
        this.mImageViewId = a.getResourceId(0, -1);
        a.recycle();
        setWillNotDraw(false);
        this.mHolographicHelper = new HolographicViewHelper(context);
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mImageView != null) {
            Drawable d = this.mImageView.getDrawable();
            if (d instanceof StateListDrawable) {
                ((StateListDrawable) d).setState(getDrawableState());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void invalidatePressedFocusedStates() {
        this.mHolographicHelper.invalidatePressedFocusedStates(this.mImageView);
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mImageView == null) {
            this.mImageView = (ImageView) findViewById(this.mImageViewId);
        }
        this.mHolographicHelper.generatePressedFocusedStates(this.mImageView);
    }
}
