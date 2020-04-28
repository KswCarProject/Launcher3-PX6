package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class AnimatorPlaybackController implements ValueAnimator.AnimatorUpdateListener {
    protected final AnimatorSet mAnim;
    private final ValueAnimator mAnimationPlayer;
    protected float mCurrentFraction;
    private final long mDuration;
    /* access modifiers changed from: private */
    public Runnable mEndAction;
    protected Runnable mOnCancelRunnable;
    protected boolean mTargetCancelled = false;

    public abstract void setPlayFraction(float f);

    public static AnimatorPlaybackController wrap(AnimatorSet anim, long duration) {
        return wrap(anim, duration, (Runnable) null);
    }

    public static AnimatorPlaybackController wrap(AnimatorSet anim, long duration, Runnable onCancelRunnable) {
        return new AnimatorPlaybackControllerVL(anim, duration, onCancelRunnable);
    }

    protected AnimatorPlaybackController(AnimatorSet anim, long duration, Runnable onCancelRunnable) {
        this.mAnim = anim;
        this.mDuration = duration;
        this.mOnCancelRunnable = onCancelRunnable;
        this.mAnimationPlayer = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.mAnimationPlayer.setInterpolator(Interpolators.LINEAR);
        this.mAnimationPlayer.addListener(new OnAnimationEndDispatcher());
        this.mAnimationPlayer.addUpdateListener(this);
        this.mAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationCancel(Animator animation) {
                AnimatorPlaybackController.this.mTargetCancelled = true;
                if (AnimatorPlaybackController.this.mOnCancelRunnable != null) {
                    AnimatorPlaybackController.this.mOnCancelRunnable.run();
                    AnimatorPlaybackController.this.mOnCancelRunnable = null;
                }
            }

            public void onAnimationEnd(Animator animation) {
                AnimatorPlaybackController.this.mTargetCancelled = false;
                AnimatorPlaybackController.this.mOnCancelRunnable = null;
            }

            public void onAnimationStart(Animator animation) {
                AnimatorPlaybackController.this.mTargetCancelled = false;
            }
        });
    }

    public AnimatorSet getTarget() {
        return this.mAnim;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public TimeInterpolator getInterpolator() {
        return this.mAnim.getInterpolator() != null ? this.mAnim.getInterpolator() : Interpolators.LINEAR;
    }

    public void start() {
        this.mAnimationPlayer.setFloatValues(new float[]{this.mCurrentFraction, 1.0f});
        this.mAnimationPlayer.setDuration(clampDuration(1.0f - this.mCurrentFraction));
        this.mAnimationPlayer.start();
    }

    public void reverse() {
        this.mAnimationPlayer.setFloatValues(new float[]{this.mCurrentFraction, 0.0f});
        this.mAnimationPlayer.setDuration(clampDuration(this.mCurrentFraction));
        this.mAnimationPlayer.start();
    }

    public void pause() {
        this.mAnimationPlayer.cancel();
    }

    public ValueAnimator getAnimationPlayer() {
        return this.mAnimationPlayer;
    }

    public float getProgressFraction() {
        return this.mCurrentFraction;
    }

    public void setEndAction(Runnable runnable) {
        this.mEndAction = runnable;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        setPlayFraction(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: protected */
    public long clampDuration(float fraction) {
        float playPos = ((float) this.mDuration) * fraction;
        if (playPos <= 0.0f) {
            return 0;
        }
        return Math.min((long) playPos, this.mDuration);
    }

    public void dispatchOnStart() {
        dispatchOnStartRecursively(this.mAnim);
    }

    private void dispatchOnStartRecursively(Animator animator) {
        for (T l : nonNullList(animator.getListeners())) {
            l.onAnimationStart(animator);
        }
        if (animator instanceof AnimatorSet) {
            for (T anim : nonNullList(((AnimatorSet) animator).getChildAnimations())) {
                dispatchOnStartRecursively(anim);
            }
        }
    }

    public void dispatchOnCancel() {
        dispatchOnCancelRecursively(this.mAnim);
    }

    private void dispatchOnCancelRecursively(Animator animator) {
        for (T l : nonNullList(animator.getListeners())) {
            l.onAnimationCancel(animator);
        }
        if (animator instanceof AnimatorSet) {
            for (T anim : nonNullList(((AnimatorSet) animator).getChildAnimations())) {
                dispatchOnCancelRecursively(anim);
            }
        }
    }

    public void dispatchSetInterpolator(TimeInterpolator interpolator) {
        dispatchSetInterpolatorRecursively(this.mAnim, interpolator);
    }

    private void dispatchSetInterpolatorRecursively(Animator anim, TimeInterpolator interpolator) {
        anim.setInterpolator(interpolator);
        if (anim instanceof AnimatorSet) {
            for (T child : nonNullList(((AnimatorSet) anim).getChildAnimations())) {
                dispatchSetInterpolatorRecursively(child, interpolator);
            }
        }
    }

    public void setOnCancelRunnable(Runnable runnable) {
        this.mOnCancelRunnable = runnable;
    }

    public Runnable getOnCancelRunnable() {
        return this.mOnCancelRunnable;
    }

    public static class AnimatorPlaybackControllerVL extends AnimatorPlaybackController {
        private final ValueAnimator[] mChildAnimations;

        private AnimatorPlaybackControllerVL(AnimatorSet anim, long duration, Runnable onCancelRunnable) {
            super(anim, duration, onCancelRunnable);
            ArrayList<ValueAnimator> childAnims = new ArrayList<>();
            getAnimationsRecur(this.mAnim, childAnims);
            this.mChildAnimations = (ValueAnimator[]) childAnims.toArray(new ValueAnimator[childAnims.size()]);
        }

        private void getAnimationsRecur(AnimatorSet anim, ArrayList<ValueAnimator> out) {
            long forceDuration = anim.getDuration();
            TimeInterpolator forceInterpolator = anim.getInterpolator();
            Iterator<Animator> it = anim.getChildAnimations().iterator();
            while (it.hasNext()) {
                Animator child = it.next();
                if (forceDuration > 0) {
                    child.setDuration(forceDuration);
                }
                if (forceInterpolator != null) {
                    child.setInterpolator(forceInterpolator);
                }
                if (child instanceof ValueAnimator) {
                    out.add((ValueAnimator) child);
                } else if (child instanceof AnimatorSet) {
                    getAnimationsRecur((AnimatorSet) child, out);
                } else {
                    throw new RuntimeException("Unknown animation type " + child);
                }
            }
        }

        public void setPlayFraction(float fraction) {
            this.mCurrentFraction = fraction;
            if (!this.mTargetCancelled) {
                long playPos = clampDuration(fraction);
                for (ValueAnimator anim : this.mChildAnimations) {
                    anim.setCurrentPlayTime(Math.min(playPos, anim.getDuration()));
                }
            }
        }
    }

    private class OnAnimationEndDispatcher extends AnimationSuccessListener {
        private OnAnimationEndDispatcher() {
        }

        public void onAnimationStart(Animator animation) {
            this.mCancelled = false;
        }

        public void onAnimationSuccess(Animator animator) {
            dispatchOnEndRecursively(AnimatorPlaybackController.this.mAnim);
            if (AnimatorPlaybackController.this.mEndAction != null) {
                AnimatorPlaybackController.this.mEndAction.run();
            }
        }

        private void dispatchOnEndRecursively(Animator animator) {
            for (Animator.AnimatorListener l : AnimatorPlaybackController.nonNullList(animator.getListeners())) {
                l.onAnimationEnd(animator);
            }
            if (animator instanceof AnimatorSet) {
                for (Animator anim : AnimatorPlaybackController.nonNullList(((AnimatorSet) animator).getChildAnimations())) {
                    dispatchOnEndRecursively(anim);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static <T> List<T> nonNullList(ArrayList<T> list) {
        return list == null ? Collections.emptyList() : list;
    }
}
