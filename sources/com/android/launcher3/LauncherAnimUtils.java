package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.View;
import android.view.ViewTreeObserver;
import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;

public class LauncherAnimUtils {
    public static final int ALL_APPS_TRANSITION_MS = 320;
    public static final Property<Drawable, Integer> DRAWABLE_ALPHA = new Property<Drawable, Integer>(Integer.TYPE, "drawableAlpha") {
        public Integer get(Drawable drawable) {
            return Integer.valueOf(drawable.getAlpha());
        }

        public void set(Drawable drawable, Integer alpha) {
            drawable.setAlpha(alpha.intValue());
        }
    };
    public static final float MIN_PROGRESS_TO_ALL_APPS = 0.5f;
    public static final int OVERVIEW_TRANSITION_MS = 250;
    public static final Property<View, Float> SCALE_PROPERTY = new Property<View, Float>(Float.class, "scale") {
        public Float get(View view) {
            return Float.valueOf(view.getScaleX());
        }

        public void set(View view, Float scale) {
            view.setScaleX(scale.floatValue());
            view.setScaleY(scale.floatValue());
        }
    };
    public static final int SPRING_LOADED_EXIT_DELAY = 500;
    public static final int SPRING_LOADED_TRANSITION_MS = 150;
    static WeakHashMap<Animator, Object> sAnimators = new WeakHashMap<>();
    static Animator.AnimatorListener sEndAnimListener = new Animator.AnimatorListener() {
        public void onAnimationStart(Animator animation) {
            LauncherAnimUtils.sAnimators.put(animation, (Object) null);
        }

        public void onAnimationRepeat(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            LauncherAnimUtils.sAnimators.remove(animation);
        }

        public void onAnimationCancel(Animator animation) {
            LauncherAnimUtils.sAnimators.remove(animation);
        }
    };

    public static void cancelOnDestroyActivity(Animator a) {
        a.addListener(sEndAnimListener);
    }

    public static void startAnimationAfterNextDraw(final Animator animator, final View view) {
        view.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            private boolean mStarted = false;

            public void onDraw() {
                if (!this.mStarted) {
                    this.mStarted = true;
                    if (animator.getDuration() != 0) {
                        animator.start();
                        view.post(new Runnable() {
                            public void run() {
                                view.getViewTreeObserver().removeOnDrawListener(this);
                            }
                        });
                    }
                }
            }
        });
    }

    public static void onDestroyActivity() {
        Iterator<Animator> it = new HashSet<>(sAnimators.keySet()).iterator();
        while (it.hasNext()) {
            Animator a = it.next();
            if (a.isRunning()) {
                a.cancel();
            }
            sAnimators.remove(a);
        }
    }

    public static AnimatorSet createAnimatorSet() {
        AnimatorSet anim = new AnimatorSet();
        cancelOnDestroyActivity(anim);
        return anim;
    }

    public static ValueAnimator ofFloat(float... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setFloatValues(values);
        cancelOnDestroyActivity(anim);
        return anim;
    }

    public static ObjectAnimator ofFloat(View target, Property<View, Float> property, float... values) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(target, property, values);
        cancelOnDestroyActivity(anim);
        new FirstFrameAnimatorHelper((ValueAnimator) anim, target);
        return anim;
    }

    public static ObjectAnimator ofViewAlphaAndScale(View target, float alpha, float scaleX, float scaleY) {
        return ofPropertyValuesHolder(target, PropertyValuesHolder.ofFloat(View.ALPHA, new float[]{alpha}), PropertyValuesHolder.ofFloat(View.SCALE_X, new float[]{scaleX}), PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[]{scaleY}));
    }

    public static ObjectAnimator ofPropertyValuesHolder(View target, PropertyValuesHolder... values) {
        return ofPropertyValuesHolder(target, target, values);
    }

    public static ObjectAnimator ofPropertyValuesHolder(Object target, View view, PropertyValuesHolder... values) {
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(target, values);
        cancelOnDestroyActivity(anim);
        new FirstFrameAnimatorHelper((ValueAnimator) anim, view);
        return anim;
    }

    public static int blockedFlingDurationFactor(float velocity) {
        return (int) Utilities.boundToRange(Math.abs(velocity) / 2.0f, 2.0f, 6.0f);
    }
}
