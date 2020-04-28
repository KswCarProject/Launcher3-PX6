package com.android.launcher3.touch;

public class OverScroll {
    private static final float OVERSCROLL_DAMP_FACTOR = 0.07f;

    private static float overScrollInfluenceCurve(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2) + 1.0f;
    }

    public static int dampedScroll(float amount, int max) {
        if (Float.compare(amount, 0.0f) == 0) {
            return 0;
        }
        float f = amount / ((float) max);
        float f2 = (f / Math.abs(f)) * overScrollInfluenceCurve(Math.abs(f));
        if (Math.abs(f2) >= 1.0f) {
            f2 /= Math.abs(f2);
        }
        return Math.round(OVERSCROLL_DAMP_FACTOR * f2 * ((float) max));
    }
}
