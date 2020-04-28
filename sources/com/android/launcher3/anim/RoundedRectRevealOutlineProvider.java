package com.android.launcher3.anim;

import android.graphics.Rect;

public class RoundedRectRevealOutlineProvider extends RevealOutlineAnimation {
    private final float mEndRadius;
    private final Rect mEndRect;
    private final float mStartRadius;
    private final Rect mStartRect;

    public RoundedRectRevealOutlineProvider(float startRadius, float endRadius, Rect startRect, Rect endRect) {
        this.mStartRadius = startRadius;
        this.mEndRadius = endRadius;
        this.mStartRect = startRect;
        this.mEndRect = endRect;
    }

    public boolean shouldRemoveElevationDuringAnimation() {
        return false;
    }

    public void setProgress(float progress) {
        this.mOutlineRadius = ((1.0f - progress) * this.mStartRadius) + (this.mEndRadius * progress);
        this.mOutline.left = (int) (((1.0f - progress) * ((float) this.mStartRect.left)) + (((float) this.mEndRect.left) * progress));
        this.mOutline.top = (int) (((1.0f - progress) * ((float) this.mStartRect.top)) + (((float) this.mEndRect.top) * progress));
        this.mOutline.right = (int) (((1.0f - progress) * ((float) this.mStartRect.right)) + (((float) this.mEndRect.right) * progress));
        this.mOutline.bottom = (int) (((1.0f - progress) * ((float) this.mStartRect.bottom)) + (((float) this.mEndRect.bottom) * progress));
    }
}
