package com.szchoiceway.index;

import android.animation.ValueAnimator;

abstract class LauncherAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    /* access modifiers changed from: package-private */
    public abstract void onAnimationUpdate(float f, float f2);

    LauncherAnimatorUpdateListener() {
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        float b = ((Float) animation.getAnimatedValue()).floatValue();
        onAnimationUpdate(1.0f - b, b);
    }
}
