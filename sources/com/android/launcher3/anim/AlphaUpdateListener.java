package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;

public class AlphaUpdateListener extends AnimationSuccessListener implements ValueAnimator.AnimatorUpdateListener {
    private static final float ALPHA_CUTOFF_THRESHOLD = 0.01f;
    private View mView;

    public AlphaUpdateListener(View v) {
        this.mView = v;
    }

    public void onAnimationUpdate(ValueAnimator arg0) {
        updateVisibility(this.mView);
    }

    public void onAnimationSuccess(Animator animator) {
        updateVisibility(this.mView);
    }

    public void onAnimationStart(Animator arg0) {
        this.mView.setVisibility(0);
    }

    public static void updateVisibility(View view) {
        if (view.getAlpha() < ALPHA_CUTOFF_THRESHOLD && view.getVisibility() != 4) {
            view.setVisibility(4);
        } else if (view.getAlpha() > ALPHA_CUTOFF_THRESHOLD && view.getVisibility() != 0) {
            view.setVisibility(0);
        }
    }
}
