package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.android.launcher3.Utilities;

public abstract class RevealOutlineAnimation extends ViewOutlineProvider {
    protected Rect mOutline = new Rect();
    protected float mOutlineRadius;

    /* access modifiers changed from: package-private */
    public abstract void setProgress(float f);

    /* access modifiers changed from: package-private */
    public abstract boolean shouldRemoveElevationDuringAnimation();

    public ValueAnimator createRevealAnimator(final View revealView, boolean isReversed) {
        float[] fArr;
        if (isReversed) {
            fArr = new float[]{1.0f, 0.0f};
        } else {
            fArr = new float[]{0.0f, 1.0f};
        }
        ValueAnimator va = ValueAnimator.ofFloat(fArr);
        final float elevation = revealView.getElevation();
        va.addListener(new AnimatorListenerAdapter() {
            private boolean mIsClippedToOutline;
            private ViewOutlineProvider mOldOutlineProvider;

            public void onAnimationStart(Animator animation) {
                this.mIsClippedToOutline = revealView.getClipToOutline();
                this.mOldOutlineProvider = revealView.getOutlineProvider();
                revealView.setOutlineProvider(RevealOutlineAnimation.this);
                revealView.setClipToOutline(true);
                if (RevealOutlineAnimation.this.shouldRemoveElevationDuringAnimation()) {
                    revealView.setTranslationZ(-elevation);
                }
            }

            public void onAnimationEnd(Animator animation) {
                revealView.setOutlineProvider(this.mOldOutlineProvider);
                revealView.setClipToOutline(this.mIsClippedToOutline);
                if (RevealOutlineAnimation.this.shouldRemoveElevationDuringAnimation()) {
                    revealView.setTranslationZ(0.0f);
                }
            }
        });
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator arg0) {
                RevealOutlineAnimation.this.setProgress(((Float) arg0.getAnimatedValue()).floatValue());
                revealView.invalidateOutline();
                if (!Utilities.ATLEAST_LOLLIPOP_MR1) {
                    revealView.invalidate();
                }
            }
        });
        return va;
    }

    public void getOutline(View v, Outline outline) {
        outline.setRoundRect(this.mOutline, this.mOutlineRadius);
    }

    public float getRadius() {
        return this.mOutlineRadius;
    }

    public void getOutline(Rect out) {
        out.set(this.mOutline);
    }
}
