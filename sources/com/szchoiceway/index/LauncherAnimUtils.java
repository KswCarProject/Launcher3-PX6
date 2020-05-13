package com.szchoiceway.index;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewTreeObserver;
import java.util.HashSet;
import java.util.Iterator;

public class LauncherAnimUtils {
    static HashSet<Animator> sAnimators = new HashSet<>();
    static Animator.AnimatorListener sEndAnimListener = new Animator.AnimatorListener() {
        public void onAnimationStart(Animator animation) {
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
        sAnimators.add(a);
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
        Iterator<Animator> it = new HashSet<>(sAnimators).iterator();
        while (it.hasNext()) {
            Animator a = it.next();
            if (a.isRunning()) {
                a.cancel();
            } else {
                sAnimators.remove(a);
            }
        }
    }

    public static AnimatorSet createAnimatorSet() {
        AnimatorSet anim = new AnimatorSet();
        cancelOnDestroyActivity(anim);
        return anim;
    }

    public static ValueAnimator ofFloat(View target, float... values) {
        ValueAnimator anim = new ValueAnimator();
        anim.setFloatValues(values);
        cancelOnDestroyActivity(anim);
        return anim;
    }

    public static ObjectAnimator ofFloat(View target, String propertyName, float... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(target);
        anim.setPropertyName(propertyName);
        anim.setFloatValues(values);
        cancelOnDestroyActivity(anim);
        new FirstFrameAnimatorHelper((ValueAnimator) anim, target);
        return anim;
    }

    public static ObjectAnimator ofPropertyValuesHolder(View target, PropertyValuesHolder... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(target);
        anim.setValues(values);
        cancelOnDestroyActivity(anim);
        new FirstFrameAnimatorHelper((ValueAnimator) anim, target);
        return anim;
    }

    public static ObjectAnimator ofPropertyValuesHolder(Object target, View view, PropertyValuesHolder... values) {
        ObjectAnimator anim = new ObjectAnimator();
        anim.setTarget(target);
        anim.setValues(values);
        cancelOnDestroyActivity(anim);
        new FirstFrameAnimatorHelper((ValueAnimator) anim, view);
        return anim;
    }
}
