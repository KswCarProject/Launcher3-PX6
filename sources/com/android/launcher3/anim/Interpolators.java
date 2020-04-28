package com.android.launcher3.anim;

import android.graphics.Path;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;

public class Interpolators {
    public static final Interpolator ACCEL = new AccelerateInterpolator();
    public static final Interpolator ACCEL_1_5 = new AccelerateInterpolator(1.5f);
    public static final Interpolator ACCEL_2 = new AccelerateInterpolator(2.0f);
    public static final Interpolator ACCEL_DEACCEL = new AccelerateDecelerateInterpolator();
    public static final Interpolator AGGRESSIVE_EASE = new PathInterpolator(0.2f, 0.0f, 0.0f, 1.0f);
    public static final Interpolator AGGRESSIVE_EASE_IN_OUT = new PathInterpolator(0.6f, 0.0f, 0.4f, 1.0f);
    public static final Interpolator DEACCEL = new DecelerateInterpolator();
    public static final Interpolator DEACCEL_1_5 = new DecelerateInterpolator(1.5f);
    public static final Interpolator DEACCEL_1_7 = new DecelerateInterpolator(1.7f);
    public static final Interpolator DEACCEL_2 = new DecelerateInterpolator(2.0f);
    public static final Interpolator DEACCEL_2_5 = new DecelerateInterpolator(2.5f);
    public static final Interpolator DEACCEL_3 = new DecelerateInterpolator(3.0f);
    public static final Interpolator EXAGGERATED_EASE;
    private static final float FAST_FLING_PX_MS = 10.0f;
    public static final Interpolator FAST_OUT_SLOW_IN = new PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    public static final Interpolator LINEAR = new LinearInterpolator();
    private static final int MIN_SETTLE_DURATION = 200;
    public static final Interpolator OVERSHOOT_1_2 = new OvershootInterpolator(1.2f);
    private static final float OVERSHOOT_FACTOR = 0.9f;
    public static final Interpolator SCROLL = new Interpolator() {
        public float getInterpolation(float t) {
            float t2 = t - 1.0f;
            return (t2 * t2 * t2 * t2 * t2) + 1.0f;
        }
    };
    public static final Interpolator SCROLL_CUBIC = new Interpolator() {
        public float getInterpolation(float t) {
            float t2 = t - 1.0f;
            return (t2 * t2 * t2) + 1.0f;
        }
    };
    public static final Interpolator TOUCH_RESPONSE_INTERPOLATOR = new PathInterpolator(0.3f, 0.0f, 0.1f, 1.0f);
    public static final Interpolator ZOOM_IN = new Interpolator() {
        public float getInterpolation(float v) {
            return Interpolators.DEACCEL_3.getInterpolation(1.0f - Interpolators.ZOOM_OUT.getInterpolation(1.0f - v));
        }
    };
    public static final Interpolator ZOOM_OUT = new Interpolator() {
        private static final float FOCAL_LENGTH = 0.35f;

        public float getInterpolation(float v) {
            return zInterpolate(v);
        }

        private float zInterpolate(float input) {
            return (1.0f - (FOCAL_LENGTH / (input + FOCAL_LENGTH))) / 0.7407408f;
        }
    };

    static {
        Path exaggeratedEase = new Path();
        exaggeratedEase.moveTo(0.0f, 0.0f);
        Path path = exaggeratedEase;
        path.cubicTo(0.05f, 0.0f, 0.133333f, 0.08f, 0.166666f, 0.4f);
        path.cubicTo(0.225f, 0.94f, 0.5f, 1.0f, 1.0f, 1.0f);
        EXAGGERATED_EASE = new PathInterpolator(exaggeratedEase);
    }

    public static Interpolator scrollInterpolatorForVelocity(float velocity) {
        return Math.abs(velocity) > FAST_FLING_PX_MS ? SCROLL : SCROLL_CUBIC;
    }

    public static Interpolator overshootInterpolatorForVelocity(float velocity) {
        return new OvershootInterpolator(Math.min(Math.abs(velocity), 3.0f));
    }

    public static Interpolator clampToProgress(Interpolator interpolator, float lowerBound, float upperBound) {
        if (upperBound > lowerBound) {
            return new Interpolator(lowerBound, upperBound, interpolator) {
                private final /* synthetic */ float f$0;
                private final /* synthetic */ float f$1;
                private final /* synthetic */ Interpolator f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final float getInterpolation(float f) {
                    return Interpolators.lambda$clampToProgress$0(this.f$0, this.f$1, this.f$2, f);
                }
            };
        }
        throw new IllegalArgumentException("lowerBound must be less than upperBound");
    }

    static /* synthetic */ float lambda$clampToProgress$0(float lowerBound, float upperBound, Interpolator interpolator, float t) {
        if (t < lowerBound) {
            return 0.0f;
        }
        if (t > upperBound) {
            return 1.0f;
        }
        return interpolator.getInterpolation((t - lowerBound) / (upperBound - lowerBound));
    }

    public static Interpolator mapToProgress(Interpolator interpolator, float lowerBound, float upperBound) {
        return new Interpolator(interpolator, lowerBound, upperBound) {
            private final /* synthetic */ Interpolator f$0;
            private final /* synthetic */ float f$1;
            private final /* synthetic */ float f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final float getInterpolation(float f) {
                return Utilities.mapRange(this.f$0.getInterpolation(f), this.f$1, this.f$2);
            }
        };
    }

    public static class OvershootParams {
        public long duration;
        public float end;
        public Interpolator interpolator;
        public float start;

        public OvershootParams(float startProgress, float overshootPastProgress, float endProgress, float velocityPxPerMs, int totalDistancePx) {
            int i = totalDistancePx;
            float velocityPxPerMs2 = Math.abs(velocityPxPerMs);
            this.start = startProgress;
            this.end = overshootPastProgress + Utilities.boundToRange((((Interpolators.OVERSHOOT_FACTOR * velocityPxPerMs2) * 16.0f) / ((float) i)) / 2.0f, 0.02f, 0.15f);
            float decelerationPxPerMs = (velocityPxPerMs2 * velocityPxPerMs2) / ((float) ((((int) (this.end * ((float) i))) - ((int) (this.start * ((float) i)))) * 2));
            this.duration = (long) (velocityPxPerMs2 / decelerationPxPerMs);
            float f = velocityPxPerMs2;
            long settleDuration = Math.max(200, ((long) Math.sqrt((double) (((float) ((int) (((float) i) * (this.end - endProgress)))) / decelerationPxPerMs))) * 4);
            float overshootFraction = ((float) this.duration) / ((float) (this.duration + settleDuration));
            this.duration += settleDuration;
            long j = settleDuration;
            this.interpolator = new Interpolator(overshootFraction, Interpolators.clampToProgress(Interpolators.DEACCEL, 0.0f, overshootFraction), Interpolators.clampToProgress(Interpolators.mapToProgress(Interpolators.ACCEL_DEACCEL, 1.0f, (endProgress - this.start) / (this.end - this.start)), overshootFraction, 1.0f)) {
                private final /* synthetic */ float f$0;
                private final /* synthetic */ Interpolator f$1;
                private final /* synthetic */ Interpolator f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final float getInterpolation(float f) {
                    return Interpolators.OvershootParams.lambda$new$0(this.f$0, this.f$1, this.f$2, f);
                }
            };
        }

        static /* synthetic */ float lambda$new$0(float overshootFraction, Interpolator overshoot, Interpolator settle, float t) {
            if (t <= overshootFraction) {
                return overshoot.getInterpolation(t);
            }
            return settle.getInterpolation(t);
        }
    }
}
