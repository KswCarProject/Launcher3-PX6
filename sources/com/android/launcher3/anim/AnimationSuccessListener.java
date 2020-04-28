package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

public abstract class AnimationSuccessListener extends AnimatorListenerAdapter {
    protected boolean mCancelled = false;

    public abstract void onAnimationSuccess(Animator animator);

    public void onAnimationCancel(Animator animation) {
        this.mCancelled = true;
    }

    public void onAnimationEnd(Animator animation) {
        if (!this.mCancelled) {
            onAnimationSuccess(animation);
        }
    }
}
