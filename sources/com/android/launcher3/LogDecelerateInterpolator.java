package com.android.launcher3;

import android.animation.TimeInterpolator;

public class LogDecelerateInterpolator implements TimeInterpolator {
    int mBase;
    int mDrift;
    final float mLogScale = (1.0f / computeLog(1.0f, this.mBase, this.mDrift));

    public LogDecelerateInterpolator(int base, int drift) {
        this.mBase = base;
        this.mDrift = drift;
    }

    static float computeLog(float t, int base, int drift) {
        return ((float) (-Math.pow((double) base, (double) (-t)))) + 1.0f + (((float) drift) * t);
    }

    public float getInterpolation(float t) {
        if (Float.compare(t, 1.0f) == 0) {
            return 1.0f;
        }
        return computeLog(t, this.mBase, this.mDrift) * this.mLogScale;
    }
}
