package com.szchoiceway.index;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class HolographicImageView extends ImageView {
    private final HolographicViewHelper mHolographicHelper;

    public HolographicImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public HolographicImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HolographicImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHolographicHelper = new HolographicViewHelper(context);
    }

    /* access modifiers changed from: package-private */
    public void invalidatePressedFocusedStates() {
        this.mHolographicHelper.invalidatePressedFocusedStates(this);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mHolographicHelper.generatePressedFocusedStates(this);
    }
}
