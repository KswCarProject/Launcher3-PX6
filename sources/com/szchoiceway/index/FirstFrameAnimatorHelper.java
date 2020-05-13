package com.szchoiceway.index;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;

public class FirstFrameAnimatorHelper extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {
    private static final boolean DEBUG = false;
    private static final int IDEAL_FRAME_DURATION = 16;
    private static final int MAX_DELAY = 1000;
    private static ViewTreeObserver.OnDrawListener sGlobalDrawListener;
    private static long sGlobalFrameCounter;
    private static boolean sVisible;
    private boolean mAdjustedSecondFrameTime;
    private boolean mHandlingOnAnimationUpdate;
    private long mStartFrame;
    private long mStartTime = -1;
    private View mTarget;

    static /* synthetic */ long access$008() {
        long j = sGlobalFrameCounter;
        sGlobalFrameCounter = 1 + j;
        return j;
    }

    public FirstFrameAnimatorHelper(ValueAnimator animator, View target) {
        this.mTarget = target;
        animator.addUpdateListener(this);
    }

    public FirstFrameAnimatorHelper(ViewPropertyAnimator vpa, View target) {
        this.mTarget = target;
        vpa.setListener(this);
    }

    public void onAnimationStart(Animator animation) {
        ValueAnimator va = (ValueAnimator) animation;
        va.addUpdateListener(this);
        onAnimationUpdate(va);
    }

    public static void setIsVisible(boolean visible) {
        sVisible = visible;
    }

    public static void initializeDrawListener(View view) {
        if (sGlobalDrawListener != null) {
            view.getViewTreeObserver().removeOnDrawListener(sGlobalDrawListener);
        }
        sGlobalDrawListener = new ViewTreeObserver.OnDrawListener() {
            private long mTime = System.currentTimeMillis();

            public void onDraw() {
                FirstFrameAnimatorHelper.access$008();
            }
        };
        view.getViewTreeObserver().addOnDrawListener(sGlobalDrawListener);
        sVisible = true;
    }

    public void onAnimationUpdate(final ValueAnimator animation) {
        long currentTime = System.currentTimeMillis();
        if (this.mStartTime == -1) {
            this.mStartFrame = sGlobalFrameCounter;
            this.mStartTime = currentTime;
        }
        if (!this.mHandlingOnAnimationUpdate && sVisible && animation.getCurrentPlayTime() < animation.getDuration()) {
            this.mHandlingOnAnimationUpdate = true;
            long frameNum = sGlobalFrameCounter - this.mStartFrame;
            if (frameNum == 0 && currentTime < this.mStartTime + 1000) {
                this.mTarget.getRootView().invalidate();
                animation.setCurrentPlayTime(0);
            } else if (frameNum == 1 && currentTime < this.mStartTime + 1000 && !this.mAdjustedSecondFrameTime && currentTime > this.mStartTime + 16) {
                animation.setCurrentPlayTime(16);
                this.mAdjustedSecondFrameTime = true;
            } else if (frameNum > 1) {
                this.mTarget.post(new Runnable() {
                    public void run() {
                        animation.removeUpdateListener(FirstFrameAnimatorHelper.this);
                    }
                });
            }
            this.mHandlingOnAnimationUpdate = false;
        }
    }

    public void print(ValueAnimator animation) {
        Log.d("FirstFrameAnimatorHelper", sGlobalFrameCounter + "(" + (sGlobalFrameCounter - this.mStartFrame) + ") " + this.mTarget + " dirty? " + this.mTarget.isDirty() + " " + (((float) animation.getCurrentPlayTime()) / ((float) animation.getDuration())) + " " + this + " " + animation);
    }
}
