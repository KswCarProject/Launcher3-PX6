package com.android.launcher3.touch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.LauncherStateManager;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.AnimatorSetBuilder;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.touch.SwipeDetector;
import com.android.launcher3.util.FlingBlockCheck;
import com.android.launcher3.util.PendingAnimation;
import com.android.launcher3.util.TouchController;
import java.util.Iterator;

public abstract class AbstractStateChangeTouchController implements TouchController, SwipeDetector.Listener {
    protected static final long ATOMIC_DURATION = 200;
    public static final float ATOMIC_OVERVIEW_ANIM_THRESHOLD = 0.5f;
    public static final float SUCCESS_TRANSITION_PROGRESS = 0.5f;
    private static final String TAG = "ASCTouchController";
    protected AnimatorSet mAtomicAnim;
    private AutoPlayAtomicAnimationInfo mAtomicAnimAutoPlayInfo;
    /* access modifiers changed from: private */
    public AnimatorPlaybackController mAtomicComponentsController;
    /* access modifiers changed from: private */
    public float mAtomicComponentsStartProgress;
    /* access modifiers changed from: private */
    public LauncherState mAtomicComponentsTargetState = LauncherState.NORMAL;
    private boolean mCanBlockFling;
    protected AnimatorPlaybackController mCurrentAnimation;
    protected final SwipeDetector mDetector;
    private float mDisplacementShift;
    private FlingBlockCheck mFlingBlockCheck = new FlingBlockCheck();
    protected LauncherState mFromState;
    protected final Launcher mLauncher;
    private boolean mNoIntercept;
    private boolean mPassedOverviewAtomicThreshold;
    protected PendingAnimation mPendingAnimation;
    private float mProgressMultiplier;
    /* access modifiers changed from: private */
    public boolean mScheduleResumeAtomicComponent;
    protected int mStartContainerType;
    private float mStartProgress;
    protected LauncherState mStartState;
    protected LauncherState mToState;

    /* access modifiers changed from: protected */
    public abstract boolean canInterceptTouch(MotionEvent motionEvent);

    /* access modifiers changed from: protected */
    public abstract int getLogContainerTypeForNormalState();

    /* access modifiers changed from: protected */
    public abstract LauncherState getTargetState(LauncherState launcherState, boolean z);

    /* access modifiers changed from: protected */
    public abstract float initCurrentAnimation(int i);

    public AbstractStateChangeTouchController(Launcher l, SwipeDetector.Direction dir) {
        this.mLauncher = l;
        this.mDetector = new SwipeDetector((Context) l, (SwipeDetector.Listener) this, dir);
    }

    public final boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        int directionsToDetectScroll;
        if (ev.getAction() == 0) {
            this.mNoIntercept = !canInterceptTouch(ev);
            if (this.mNoIntercept) {
                return false;
            }
            boolean ignoreSlopWhenSettling = false;
            if (this.mCurrentAnimation != null) {
                directionsToDetectScroll = 3;
                ignoreSlopWhenSettling = true;
            } else {
                int directionsToDetectScroll2 = getSwipeDirection();
                if (directionsToDetectScroll2 == 0) {
                    this.mNoIntercept = true;
                    return false;
                }
                directionsToDetectScroll = directionsToDetectScroll2;
            }
            this.mDetector.setDetectableScrollConditions(directionsToDetectScroll, ignoreSlopWhenSettling);
        }
        if (this.mNoIntercept) {
            return false;
        }
        onControllerTouchEvent(ev);
        return this.mDetector.isDraggingOrSettling();
    }

    private int getSwipeDirection() {
        LauncherState fromState = this.mLauncher.getStateManager().getState();
        int swipeDirection = 0;
        if (getTargetState(fromState, true) != fromState) {
            swipeDirection = 0 | 1;
        }
        if (getTargetState(fromState, false) != fromState) {
            return swipeDirection | 2;
        }
        return swipeDirection;
    }

    public final boolean onControllerTouchEvent(MotionEvent ev) {
        return this.mDetector.onTouchEvent(ev);
    }

    /* access modifiers changed from: protected */
    public float getShiftRange() {
        return this.mLauncher.getAllAppsController().getShiftRange();
    }

    private boolean reinitCurrentAnimation(boolean reachedToState, boolean isDragTowardPositive) {
        LauncherState newFromState = this.mFromState == null ? this.mLauncher.getStateManager().getState() : reachedToState ? this.mToState : this.mFromState;
        LauncherState newToState = getTargetState(newFromState, isDragTowardPositive);
        if ((newFromState == this.mFromState && newToState == this.mToState) || newFromState == newToState) {
            return false;
        }
        this.mFromState = newFromState;
        this.mToState = newToState;
        this.mStartProgress = 0.0f;
        this.mPassedOverviewAtomicThreshold = false;
        if (this.mCurrentAnimation != null) {
            this.mCurrentAnimation.setOnCancelRunnable((Runnable) null);
        }
        int animComponents = goingBetweenNormalAndOverview(this.mFromState, this.mToState) ? 1 : 3;
        this.mScheduleResumeAtomicComponent = false;
        if (this.mAtomicAnim != null) {
            animComponents = 1;
            this.mScheduleResumeAtomicComponent = true;
        }
        if (goingBetweenNormalAndOverview(this.mFromState, this.mToState) || this.mAtomicComponentsTargetState != this.mToState) {
            cancelAtomicComponentsController();
        }
        if (this.mAtomicComponentsController != null) {
            animComponents &= -3;
        }
        this.mProgressMultiplier = initCurrentAnimation(animComponents);
        this.mCurrentAnimation.dispatchOnStart();
        return true;
    }

    private boolean goingBetweenNormalAndOverview(LauncherState fromState, LauncherState toState) {
        return (fromState == LauncherState.NORMAL || fromState == LauncherState.OVERVIEW) && (toState == LauncherState.NORMAL || toState == LauncherState.OVERVIEW) && this.mPendingAnimation == null;
    }

    public void onDragStart(boolean start) {
        this.mStartState = this.mLauncher.getStateManager().getState();
        if (this.mStartState == LauncherState.ALL_APPS) {
            this.mStartContainerType = 4;
        } else if (this.mStartState == LauncherState.NORMAL) {
            this.mStartContainerType = getLogContainerTypeForNormalState();
        } else if (this.mStartState == LauncherState.OVERVIEW) {
            this.mStartContainerType = 12;
        }
        boolean z = false;
        if (this.mCurrentAnimation == null) {
            this.mFromState = this.mStartState;
            this.mToState = null;
            cancelAnimationControllers();
            reinitCurrentAnimation(false, this.mDetector.wasInitialTouchPositive());
            this.mDisplacementShift = 0.0f;
        } else {
            this.mCurrentAnimation.pause();
            this.mStartProgress = this.mCurrentAnimation.getProgressFraction();
            this.mAtomicAnimAutoPlayInfo = null;
            if (this.mAtomicComponentsController != null) {
                this.mAtomicComponentsController.pause();
            }
        }
        if (this.mFromState == LauncherState.NORMAL) {
            z = true;
        }
        this.mCanBlockFling = z;
        this.mFlingBlockCheck.unblockFling();
    }

    public boolean onDrag(float displacement, float velocity) {
        float progress = this.mStartProgress + (this.mProgressMultiplier * (displacement - this.mDisplacementShift));
        updateProgress(progress);
        boolean isDragTowardPositive = displacement - this.mDisplacementShift < 0.0f;
        if (progress <= 0.0f) {
            if (reinitCurrentAnimation(false, isDragTowardPositive)) {
                this.mDisplacementShift = displacement;
                if (this.mCanBlockFling) {
                    this.mFlingBlockCheck.blockFling();
                }
            }
        } else if (progress < 1.0f) {
            this.mFlingBlockCheck.onEvent();
        } else if (reinitCurrentAnimation(true, isDragTowardPositive)) {
            this.mDisplacementShift = displacement;
            if (this.mCanBlockFling) {
                this.mFlingBlockCheck.blockFling();
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void updateProgress(float fraction) {
        this.mCurrentAnimation.setPlayFraction(fraction);
        if (this.mAtomicComponentsController != null) {
            float start = Math.min(this.mAtomicComponentsStartProgress, 0.9f);
            this.mAtomicComponentsController.setPlayFraction((fraction - start) / (1.0f - start));
        }
        maybeUpdateAtomicAnim(this.mFromState, this.mToState, fraction);
    }

    private void maybeUpdateAtomicAnim(LauncherState fromState, LauncherState toState, float progress) {
        if (goingBetweenNormalAndOverview(fromState, toState)) {
            LauncherState launcherState = LauncherState.OVERVIEW;
            boolean passedThreshold = progress >= 0.5f;
            if (passedThreshold != this.mPassedOverviewAtomicThreshold) {
                LauncherState atomicFromState = passedThreshold ? fromState : toState;
                LauncherState atomicToState = passedThreshold ? toState : fromState;
                this.mPassedOverviewAtomicThreshold = passedThreshold;
                if (this.mAtomicAnim != null) {
                    this.mAtomicAnim.cancel();
                }
                this.mAtomicAnim = createAtomicAnimForState(atomicFromState, atomicToState, ATOMIC_DURATION);
                this.mAtomicAnim.addListener(new AnimationSuccessListener() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        AbstractStateChangeTouchController.this.mAtomicAnim = null;
                        boolean unused = AbstractStateChangeTouchController.this.mScheduleResumeAtomicComponent = false;
                    }

                    public void onAnimationSuccess(Animator animator) {
                        if (AbstractStateChangeTouchController.this.mScheduleResumeAtomicComponent) {
                            AbstractStateChangeTouchController.this.cancelAtomicComponentsController();
                            if (AbstractStateChangeTouchController.this.mCurrentAnimation != null) {
                                float unused = AbstractStateChangeTouchController.this.mAtomicComponentsStartProgress = AbstractStateChangeTouchController.this.mCurrentAnimation.getProgressFraction();
                                long duration = (long) (AbstractStateChangeTouchController.this.getShiftRange() * 2.0f);
                                AnimatorPlaybackController unused2 = AbstractStateChangeTouchController.this.mAtomicComponentsController = AnimatorPlaybackController.wrap(AbstractStateChangeTouchController.this.createAtomicAnimForState(AbstractStateChangeTouchController.this.mFromState, AbstractStateChangeTouchController.this.mToState, duration), duration);
                                AbstractStateChangeTouchController.this.mAtomicComponentsController.dispatchOnStart();
                                LauncherState unused3 = AbstractStateChangeTouchController.this.mAtomicComponentsTargetState = AbstractStateChangeTouchController.this.mToState;
                                AbstractStateChangeTouchController.this.maybeAutoPlayAtomicComponentsAnim();
                            }
                        }
                    }
                });
                this.mAtomicAnim.start();
                this.mLauncher.getDragLayer().performHapticFeedback(1);
            }
        }
    }

    /* access modifiers changed from: private */
    public AnimatorSet createAtomicAnimForState(LauncherState fromState, LauncherState targetState, long duration) {
        AnimatorSetBuilder builder = getAnimatorSetBuilderForStates(fromState, targetState);
        this.mLauncher.getStateManager().prepareForAtomicAnimation(fromState, targetState, builder);
        LauncherStateManager.AnimationConfig config = new LauncherStateManager.AnimationConfig();
        config.animComponents = 2;
        config.duration = duration;
        for (LauncherStateManager.StateHandler handler : this.mLauncher.getStateManager().getStateHandlers()) {
            handler.setStateWithAnimation(targetState, builder, config);
        }
        return builder.build();
    }

    /* access modifiers changed from: protected */
    public AnimatorSetBuilder getAnimatorSetBuilderForStates(LauncherState fromState, LauncherState toState) {
        return new AnimatorSetBuilder();
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00fa  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDragEnd(float r22, boolean r23) {
        /*
            r21 = this;
            r7 = r21
            r8 = r22
            if (r23 == 0) goto L_0x0008
            r1 = 4
            goto L_0x0009
        L_0x0008:
            r1 = 3
        L_0x0009:
            r9 = r1
            r1 = 0
            if (r23 == 0) goto L_0x0017
            com.android.launcher3.util.FlingBlockCheck r3 = r7.mFlingBlockCheck
            boolean r3 = r3.isBlocked()
            if (r3 == 0) goto L_0x0017
            r3 = 1
            goto L_0x0018
        L_0x0017:
            r3 = 0
        L_0x0018:
            r10 = r3
            if (r10 == 0) goto L_0x001e
            r0 = 0
            r11 = r0
            goto L_0x0020
        L_0x001e:
            r11 = r23
        L_0x0020:
            com.android.launcher3.anim.AnimatorPlaybackController r0 = r7.mCurrentAnimation
            float r12 = r0.getProgressFraction()
            com.android.launcher3.anim.AnimatorPlaybackController r0 = r7.mCurrentAnimation
            android.animation.TimeInterpolator r0 = r0.getInterpolator()
            float r13 = r0.getInterpolation(r12)
            if (r11 == 0) goto L_0x0049
            float r0 = java.lang.Math.signum(r22)
            float r3 = r7.mProgressMultiplier
            float r3 = java.lang.Math.signum(r3)
            int r0 = java.lang.Float.compare(r0, r3)
            if (r0 != 0) goto L_0x0046
            com.android.launcher3.LauncherState r0 = r7.mToState
            goto L_0x0048
        L_0x0046:
            com.android.launcher3.LauncherState r0 = r7.mFromState
        L_0x0048:
            goto L_0x0059
        L_0x0049:
            com.android.launcher3.LauncherState r0 = r7.mToState
            com.android.launcher3.LauncherState r3 = com.android.launcher3.LauncherState.ALL_APPS
            r0 = 1056964608(0x3f000000, float:0.5)
            int r3 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1))
            if (r3 <= 0) goto L_0x0056
            com.android.launcher3.LauncherState r3 = r7.mToState
            goto L_0x0058
        L_0x0056:
            com.android.launcher3.LauncherState r3 = r7.mFromState
        L_0x0058:
            r0 = r3
        L_0x0059:
            r14 = r0
            if (r10 == 0) goto L_0x0065
            com.android.launcher3.LauncherState r0 = r7.mFromState
            if (r14 != r0) goto L_0x0065
            int r0 = com.android.launcher3.LauncherAnimUtils.blockedFlingDurationFactor(r22)
            goto L_0x0066
        L_0x0065:
            r0 = 1
        L_0x0066:
            r15 = r0
            com.android.launcher3.LauncherState r0 = r7.mToState
            r3 = 1098907648(0x41800000, float:16.0)
            r4 = 0
            r5 = 1065353216(0x3f800000, float:1.0)
            if (r14 != r0) goto L_0x009b
            r0 = 1065353216(0x3f800000, float:1.0)
            int r6 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r6 < 0) goto L_0x007e
            r16 = 0
            r3 = 1065353216(0x3f800000, float:1.0)
            r6 = r0
        L_0x007b:
            r18 = r3
            goto L_0x00d4
        L_0x007e:
            float r3 = r3 * r8
            float r6 = r7.mProgressMultiplier
            float r3 = r3 * r6
            float r3 = r3 + r12
            float r3 = com.android.launcher3.Utilities.boundToRange((float) r3, (float) r4, (float) r5)
            float r6 = java.lang.Math.max(r12, r4)
            float r6 = r0 - r6
            long r16 = com.android.launcher3.touch.SwipeDetector.calculateDuration(r8, r6)
            r18 = r3
            long r2 = (long) r15
            long r16 = r16 * r2
            r6 = r0
            goto L_0x00d4
        L_0x009b:
            com.android.launcher3.anim.AnimatorPlaybackController r0 = r7.mCurrentAnimation
            java.lang.Runnable r0 = r0.getOnCancelRunnable()
            com.android.launcher3.anim.AnimatorPlaybackController r2 = r7.mCurrentAnimation
            r6 = 0
            r2.setOnCancelRunnable(r6)
            com.android.launcher3.anim.AnimatorPlaybackController r2 = r7.mCurrentAnimation
            r2.dispatchOnCancel()
            com.android.launcher3.anim.AnimatorPlaybackController r2 = r7.mCurrentAnimation
            r2.setOnCancelRunnable(r0)
            r2 = 0
            int r6 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r6 > 0) goto L_0x00bb
            r16 = 0
            r3 = 0
        L_0x00b9:
            r6 = r2
            goto L_0x007b
        L_0x00bb:
            float r3 = r3 * r8
            float r6 = r7.mProgressMultiplier
            float r3 = r3 * r6
            float r3 = r3 + r12
            float r3 = com.android.launcher3.Utilities.boundToRange((float) r3, (float) r4, (float) r5)
            float r6 = java.lang.Math.min(r12, r5)
            float r6 = r6 - r2
            long r16 = com.android.launcher3.touch.SwipeDetector.calculateDuration(r8, r6)
            long r4 = (long) r15
            long r16 = r16 * r4
            goto L_0x00b9
        L_0x00d4:
            r4 = r16
            com.android.launcher3.anim.AnimatorPlaybackController r0 = r7.mCurrentAnimation
            com.android.launcher3.touch.-$$Lambda$AbstractStateChangeTouchController$RwEISxsMUlr5U_4sLbHR6ktFaa4 r2 = new com.android.launcher3.touch.-$$Lambda$AbstractStateChangeTouchController$RwEISxsMUlr5U_4sLbHR6ktFaa4
            r2.<init>(r14, r9)
            r0.setEndAction(r2)
            com.android.launcher3.anim.AnimatorPlaybackController r0 = r7.mCurrentAnimation
            android.animation.ValueAnimator r2 = r0.getAnimationPlayer()
            r0 = 2
            float[] r0 = new float[r0]
            r0[r1] = r18
            r1 = 1
            r0[r1] = r6
            r2.setFloatValues(r0)
            com.android.launcher3.LauncherState r0 = r7.mFromState
            com.android.launcher3.LauncherState r1 = r7.mToState
            if (r14 != r1) goto L_0x00fa
            r1 = 1065353216(0x3f800000, float:1.0)
            goto L_0x00fb
        L_0x00fa:
            r1 = 0
        L_0x00fb:
            r7.maybeUpdateAtomicAnim(r0, r14, r1)
            long r0 = r21.getRemainingAtomicDuration()
            long r16 = java.lang.Math.max(r4, r0)
            r0 = r21
            r1 = r2
            r19 = r9
            r9 = r2
            r2 = r16
            r16 = r4
            r4 = r14
            r5 = r22
            r20 = r10
            r10 = r6
            r6 = r11
            r0.updateSwipeCompleteAnimation(r1, r2, r4, r5, r6)
            com.android.launcher3.anim.AnimatorPlaybackController r0 = r7.mCurrentAnimation
            r0.dispatchOnStart()
            if (r11 == 0) goto L_0x012e
            com.android.launcher3.LauncherState r0 = com.android.launcher3.LauncherState.ALL_APPS
            if (r14 != r0) goto L_0x012e
            com.android.launcher3.Launcher r0 = r7.mLauncher
            com.android.launcher3.allapps.AllAppsContainerView r0 = r0.getAppsView()
            r0.addSpringFromFlingUpdateListener(r9, r8)
        L_0x012e:
            r9.start()
            com.android.launcher3.touch.AbstractStateChangeTouchController$AutoPlayAtomicAnimationInfo r0 = new com.android.launcher3.touch.AbstractStateChangeTouchController$AutoPlayAtomicAnimationInfo
            long r1 = r9.getDuration()
            r0.<init>(r10, r1)
            r7.mAtomicAnimAutoPlayInfo = r0
            r21.maybeAutoPlayAtomicComponentsAnim()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.touch.AbstractStateChangeTouchController.onDragEnd(float, boolean):void");
    }

    /* access modifiers changed from: private */
    public void maybeAutoPlayAtomicComponentsAnim() {
        if (this.mAtomicComponentsController != null && this.mAtomicAnimAutoPlayInfo != null) {
            final AnimatorPlaybackController controller = this.mAtomicComponentsController;
            ValueAnimator atomicAnim = controller.getAnimationPlayer();
            atomicAnim.setFloatValues(new float[]{controller.getProgressFraction(), this.mAtomicAnimAutoPlayInfo.toProgress});
            long duration = this.mAtomicAnimAutoPlayInfo.endTime - SystemClock.elapsedRealtime();
            this.mAtomicAnimAutoPlayInfo = null;
            if (duration <= 0) {
                atomicAnim.start();
                atomicAnim.end();
                this.mAtomicComponentsController = null;
                return;
            }
            atomicAnim.setDuration(duration);
            atomicAnim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (AbstractStateChangeTouchController.this.mAtomicComponentsController == controller) {
                        AnimatorPlaybackController unused = AbstractStateChangeTouchController.this.mAtomicComponentsController = null;
                    }
                }
            });
            atomicAnim.start();
        }
    }

    private long getRemainingAtomicDuration() {
        if (this.mAtomicAnim == null) {
            return 0;
        }
        if (Utilities.ATLEAST_OREO) {
            return this.mAtomicAnim.getTotalDuration() - this.mAtomicAnim.getCurrentPlayTime();
        }
        long remainingDuration = 0;
        Iterator<Animator> it = this.mAtomicAnim.getChildAnimations().iterator();
        while (it.hasNext()) {
            remainingDuration = Math.max(remainingDuration, it.next().getDuration());
        }
        return remainingDuration;
    }

    /* access modifiers changed from: protected */
    public void updateSwipeCompleteAnimation(ValueAnimator animator, long expectedDuration, LauncherState targetState, float velocity, boolean isFling) {
        animator.setDuration(expectedDuration).setInterpolator(Interpolators.scrollInterpolatorForVelocity(velocity));
    }

    /* access modifiers changed from: protected */
    public int getDirectionForLog() {
        return this.mToState.ordinal > this.mFromState.ordinal ? 1 : 2;
    }

    /* access modifiers changed from: protected */
    public void onSwipeInteractionCompleted(LauncherState targetState, int logAction) {
        if (this.mAtomicComponentsController != null) {
            this.mAtomicComponentsController.getAnimationPlayer().end();
            this.mAtomicComponentsController = null;
        }
        cancelAnimationControllers();
        boolean shouldGoToTargetState = true;
        if (this.mPendingAnimation != null) {
            boolean z = true;
            boolean reachedTarget = this.mToState == targetState;
            this.mPendingAnimation.finish(reachedTarget, logAction);
            this.mPendingAnimation = null;
            if (reachedTarget) {
                z = false;
            }
            shouldGoToTargetState = z;
        }
        if (shouldGoToTargetState) {
            if (targetState != this.mStartState) {
                logReachedState(logAction, targetState);
            }
            this.mLauncher.getStateManager().goToState(targetState, false);
        }
    }

    private void logReachedState(int logAction, LauncherState targetState) {
        this.mLauncher.getUserEventDispatcher().logStateChangeAction(logAction, getDirectionForLog(), this.mStartContainerType, this.mStartState.containerType, targetState.containerType, this.mLauncher.getWorkspace().getCurrentPage());
    }

    /* access modifiers changed from: protected */
    public void clearState() {
        cancelAnimationControllers();
        if (this.mAtomicAnim != null) {
            this.mAtomicAnim.cancel();
            this.mAtomicAnim = null;
        }
        this.mScheduleResumeAtomicComponent = false;
    }

    private void cancelAnimationControllers() {
        this.mCurrentAnimation = null;
        cancelAtomicComponentsController();
        this.mDetector.finishedScrolling();
        this.mDetector.setDetectableScrollConditions(0, false);
    }

    /* access modifiers changed from: private */
    public void cancelAtomicComponentsController() {
        if (this.mAtomicComponentsController != null) {
            this.mAtomicComponentsController.getAnimationPlayer().cancel();
            this.mAtomicComponentsController = null;
        }
        this.mAtomicAnimAutoPlayInfo = null;
    }

    private static class AutoPlayAtomicAnimationInfo {
        public final long endTime;
        public final float toProgress;

        AutoPlayAtomicAnimationInfo(float toProgress2, long duration) {
            this.toProgress = toProgress2;
            this.endTime = SystemClock.elapsedRealtime() + duration;
        }
    }
}
