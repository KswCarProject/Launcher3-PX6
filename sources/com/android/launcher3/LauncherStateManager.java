package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Handler;
import android.os.Looper;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.AnimatorPlaybackController;
import com.android.launcher3.anim.AnimatorSetBuilder;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PropertySetter;
import com.android.launcher3.uioverrides.UiFactory;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class LauncherStateManager {
    public static final int ANIM_ALL = 3;
    public static final int ATOMIC_COMPONENT = 2;
    public static final int NON_ATOMIC_COMPONENT = 1;
    public static final String TAG = "StateManager";
    /* access modifiers changed from: private */
    public final AnimationConfig mConfig = new AnimationConfig();
    private LauncherState mCurrentStableState = LauncherState.NORMAL;
    private LauncherState mLastStableState = LauncherState.NORMAL;
    private final Launcher mLauncher;
    /* access modifiers changed from: private */
    public final ArrayList<StateListener> mListeners = new ArrayList<>();
    private LauncherState mRestState;
    private LauncherState mState = LauncherState.NORMAL;
    private StateHandler[] mStateHandlers;
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());

    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationComponents {
    }

    public interface StateHandler {
        void setState(LauncherState launcherState);

        void setStateWithAnimation(LauncherState launcherState, AnimatorSetBuilder animatorSetBuilder, AnimationConfig animationConfig);
    }

    public interface StateListener {
        void onStateSetImmediately(LauncherState launcherState);

        void onStateTransitionComplete(LauncherState launcherState);

        void onStateTransitionStart(LauncherState launcherState);
    }

    public LauncherStateManager(Launcher l) {
        this.mLauncher = l;
    }

    public LauncherState getState() {
        return this.mState;
    }

    public StateHandler[] getStateHandlers() {
        if (this.mStateHandlers == null) {
            this.mStateHandlers = UiFactory.getStateHandler(this.mLauncher);
        }
        return this.mStateHandlers;
    }

    public void addStateListener(StateListener listener) {
        this.mListeners.add(listener);
    }

    public void removeStateListener(StateListener listener) {
        this.mListeners.remove(listener);
    }

    public void goToState(LauncherState state) {
        goToState(state, !this.mLauncher.isForceInvisible() && this.mLauncher.isStarted());
    }

    public void goToState(LauncherState state, boolean animated) {
        goToState(state, animated, 0, (Runnable) null);
    }

    public void goToState(LauncherState state, boolean animated, Runnable onCompleteRunnable) {
        goToState(state, animated, 0, onCompleteRunnable);
    }

    public void goToState(LauncherState state, long delay, Runnable onCompleteRunnable) {
        goToState(state, true, delay, onCompleteRunnable);
    }

    public void goToState(LauncherState state, long delay) {
        goToState(state, true, delay, (Runnable) null);
    }

    public void reapplyState() {
        reapplyState(false);
    }

    public void reapplyState(boolean cancelCurrentAnimation) {
        if (cancelCurrentAnimation) {
            cancelAnimation();
        }
        if (this.mConfig.mCurrentAnimation == null) {
            for (StateHandler handler : getStateHandlers()) {
                handler.setState(this.mState);
            }
        }
    }

    private void goToState(LauncherState state, boolean animated, long delay, final Runnable onCompleteRunnable) {
        if (this.mLauncher.isInState(state)) {
            if (this.mConfig.mCurrentAnimation == null) {
                if (onCompleteRunnable != null) {
                    onCompleteRunnable.run();
                    return;
                }
                return;
            } else if (!this.mConfig.userControlled && animated && this.mConfig.mTargetState == state) {
                if (onCompleteRunnable != null) {
                    this.mConfig.mCurrentAnimation.addListener(new AnimationSuccessListener() {
                        public void onAnimationSuccess(Animator animator) {
                            onCompleteRunnable.run();
                        }
                    });
                    return;
                }
                return;
            }
        }
        LauncherState fromState = this.mState;
        this.mConfig.reset();
        if (!animated) {
            onStateTransitionStart(state);
            for (StateHandler handler : getStateHandlers()) {
                handler.setState(state);
            }
            for (int i = this.mListeners.size() - 1; i >= 0; i--) {
                this.mListeners.get(i).onStateSetImmediately(state);
            }
            onStateTransitionEnd(state);
            if (onCompleteRunnable != null) {
                onCompleteRunnable.run();
                return;
            }
            return;
        }
        this.mConfig.duration = (long) (state == LauncherState.NORMAL ? fromState.transitionDuration : state.transitionDuration);
        AnimatorSetBuilder builder = new AnimatorSetBuilder();
        prepareForAtomicAnimation(fromState, state, builder);
        Runnable runnable = new StartAnimRunnable(createAnimationToNewWorkspaceInternal(state, builder, onCompleteRunnable));
        if (delay > 0) {
            this.mUiHandler.postDelayed(runnable, delay);
        } else {
            this.mUiHandler.post(runnable);
        }
    }

    public void prepareForAtomicAnimation(LauncherState fromState, LauncherState toState, AnimatorSetBuilder builder) {
        if (fromState == LauncherState.NORMAL && toState.overviewUi) {
            builder.setInterpolator(1, Interpolators.OVERSHOOT_1_2);
            builder.setInterpolator(2, Interpolators.OVERSHOOT_1_2);
            builder.setInterpolator(3, Interpolators.OVERSHOOT_1_2);
            builder.setInterpolator(4, Interpolators.OVERSHOOT_1_2);
            UiFactory.prepareToShowOverview(this.mLauncher);
        } else if (fromState.overviewUi && toState == LauncherState.NORMAL) {
            builder.setInterpolator(1, Interpolators.DEACCEL);
            builder.setInterpolator(2, Interpolators.ACCEL);
            builder.setInterpolator(3, Interpolators.clampToProgress(Interpolators.ACCEL, 0.0f, 0.9f));
            builder.setInterpolator(4, Interpolators.DEACCEL_1_7);
            Workspace workspace = this.mLauncher.getWorkspace();
            boolean z = false;
            boolean isWorkspaceVisible = workspace.getVisibility() == 0;
            if (isWorkspaceVisible) {
                CellLayout currentChild = (CellLayout) workspace.getChildAt(workspace.getCurrentPage());
                if (currentChild.getVisibility() == 0 && currentChild.getShortcutsAndWidgets().getAlpha() > 0.0f) {
                    z = true;
                }
                isWorkspaceVisible = z;
            }
            if (!isWorkspaceVisible) {
                workspace.setScaleX(0.92f);
                workspace.setScaleY(0.92f);
            }
        }
    }

    public AnimatorPlaybackController createAnimationToNewWorkspace(LauncherState fromState, LauncherState state, long duration) {
        this.mConfig.reset();
        for (StateHandler handler : getStateHandlers()) {
            handler.setState(fromState);
        }
        return createAnimationToNewWorkspace(state, duration);
    }

    public AnimatorPlaybackController createAnimationToNewWorkspace(LauncherState state, long duration) {
        return createAnimationToNewWorkspace(state, duration, 3);
    }

    public AnimatorPlaybackController createAnimationToNewWorkspace(LauncherState state, long duration, int animComponents) {
        return createAnimationToNewWorkspace(state, new AnimatorSetBuilder(), duration, (Runnable) null, animComponents);
    }

    public AnimatorPlaybackController createAnimationToNewWorkspace(LauncherState state, AnimatorSetBuilder builder, long duration, Runnable onCancelRunnable, int animComponents) {
        this.mConfig.reset();
        this.mConfig.userControlled = true;
        this.mConfig.animComponents = animComponents;
        this.mConfig.duration = duration;
        this.mConfig.playbackController = AnimatorPlaybackController.wrap(createAnimationToNewWorkspaceInternal(state, builder, (Runnable) null), duration, onCancelRunnable);
        return this.mConfig.playbackController;
    }

    /* access modifiers changed from: protected */
    public AnimatorSet createAnimationToNewWorkspaceInternal(final LauncherState state, AnimatorSetBuilder builder, final Runnable onCompleteRunnable) {
        for (StateHandler handler : getStateHandlers()) {
            builder.startTag(handler);
            handler.setStateWithAnimation(state, builder, this.mConfig);
        }
        AnimatorSet animation = builder.build();
        animation.addListener(new AnimationSuccessListener() {
            public void onAnimationStart(Animator animation) {
                LauncherStateManager.this.onStateTransitionStart(state);
                for (int i = LauncherStateManager.this.mListeners.size() - 1; i >= 0; i--) {
                    ((StateListener) LauncherStateManager.this.mListeners.get(i)).onStateTransitionStart(state);
                }
            }

            public void onAnimationSuccess(Animator animator) {
                if (onCompleteRunnable != null) {
                    onCompleteRunnable.run();
                }
                LauncherStateManager.this.onStateTransitionEnd(state);
                for (int i = LauncherStateManager.this.mListeners.size() - 1; i >= 0; i--) {
                    ((StateListener) LauncherStateManager.this.mListeners.get(i)).onStateTransitionComplete(state);
                }
            }
        });
        this.mConfig.setAnimation(animation, state);
        return this.mConfig.mCurrentAnimation;
    }

    /* access modifiers changed from: private */
    public void onStateTransitionStart(LauncherState state) {
        if (this.mState != state) {
            this.mState.onStateDisabled(this.mLauncher);
        }
        this.mState = state;
        this.mState.onStateEnabled(this.mLauncher);
        this.mLauncher.getAppWidgetHost().setResumed(state == LauncherState.NORMAL);
        if (state.disablePageClipping) {
            this.mLauncher.getWorkspace().setClipChildren(false);
        }
        UiFactory.onLauncherStateOrResumeChanged(this.mLauncher);
    }

    /* access modifiers changed from: private */
    public void onStateTransitionEnd(LauncherState state) {
        if (state != this.mCurrentStableState) {
            this.mLastStableState = state.getHistoryForState(this.mCurrentStableState);
            this.mCurrentStableState = state;
        }
        state.onStateTransitionEnd(this.mLauncher);
        this.mLauncher.getWorkspace().setClipChildren(!state.disablePageClipping);
        this.mLauncher.finishAutoCancelActionMode();
        if (state == LauncherState.NORMAL) {
            setRestState((LauncherState) null);
        }
        UiFactory.onLauncherStateOrResumeChanged(this.mLauncher);
        this.mLauncher.getDragLayer().requestFocus();
    }

    public void onWindowFocusChanged() {
        UiFactory.onLauncherStateOrFocusChanged(this.mLauncher);
    }

    public LauncherState getLastState() {
        return this.mLastStableState;
    }

    public void moveToRestState() {
        if ((this.mConfig.mCurrentAnimation == null || !this.mConfig.userControlled) && this.mState.disableRestore) {
            goToState(getRestState());
            this.mLastStableState = LauncherState.NORMAL;
        }
    }

    public LauncherState getRestState() {
        return this.mRestState == null ? LauncherState.NORMAL : this.mRestState;
    }

    public void setRestState(LauncherState restState) {
        this.mRestState = restState;
    }

    public void cancelAnimation() {
        this.mConfig.reset();
    }

    public void setCurrentUserControlledAnimation(AnimatorPlaybackController controller) {
        clearCurrentAnimation();
        setCurrentAnimation(controller.getTarget(), new Animator[0]);
        this.mConfig.userControlled = true;
        this.mConfig.playbackController = controller;
    }

    public void setCurrentAnimation(AnimatorSet anim, Animator... childAnimations) {
        int length = childAnimations.length;
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Animator childAnim = childAnimations[i];
            if (childAnim != null) {
                if (this.mConfig.playbackController != null && this.mConfig.playbackController.getTarget() == childAnim) {
                    clearCurrentAnimation();
                    break;
                } else if (this.mConfig.mCurrentAnimation == childAnim) {
                    clearCurrentAnimation();
                    break;
                }
            }
            i++;
        }
        if (this.mConfig.mCurrentAnimation != null) {
            z = true;
        }
        boolean reapplyNeeded = z;
        cancelAnimation();
        if (reapplyNeeded) {
            reapplyState();
        }
        this.mConfig.setAnimation(anim, (LauncherState) null);
    }

    private void clearCurrentAnimation() {
        if (this.mConfig.mCurrentAnimation != null) {
            this.mConfig.mCurrentAnimation.removeListener(this.mConfig);
            AnimatorSet unused = this.mConfig.mCurrentAnimation = null;
        }
        this.mConfig.playbackController = null;
    }

    private class StartAnimRunnable implements Runnable {
        private final AnimatorSet mAnim;

        public StartAnimRunnable(AnimatorSet anim) {
            this.mAnim = anim;
        }

        public void run() {
            if (LauncherStateManager.this.mConfig.mCurrentAnimation == this.mAnim) {
                this.mAnim.start();
            }
        }
    }

    public static class AnimationConfig extends AnimatorListenerAdapter {
        public int animComponents = 3;
        public long duration;
        /* access modifiers changed from: private */
        public AnimatorSet mCurrentAnimation;
        private PropertySetter mPropertySetter;
        /* access modifiers changed from: private */
        public LauncherState mTargetState;
        public AnimatorPlaybackController playbackController;
        public boolean userControlled;

        public void reset() {
            this.duration = 0;
            this.userControlled = false;
            this.animComponents = 3;
            this.mPropertySetter = null;
            this.mTargetState = null;
            if (this.playbackController != null) {
                this.playbackController.getAnimationPlayer().cancel();
                this.playbackController.dispatchOnCancel();
            } else if (this.mCurrentAnimation != null) {
                this.mCurrentAnimation.setDuration(0);
                this.mCurrentAnimation.cancel();
            }
            this.mCurrentAnimation = null;
            this.playbackController = null;
        }

        public PropertySetter getPropertySetter(AnimatorSetBuilder builder) {
            if (this.mPropertySetter == null) {
                this.mPropertySetter = this.duration == 0 ? PropertySetter.NO_ANIM_PROPERTY_SETTER : new PropertySetter.AnimatedPropertySetter(this.duration, builder);
            }
            return this.mPropertySetter;
        }

        public void onAnimationEnd(Animator animation) {
            if (this.playbackController != null && this.playbackController.getTarget() == animation) {
                this.playbackController = null;
            }
            if (this.mCurrentAnimation == animation) {
                this.mCurrentAnimation = null;
            }
        }

        public void setAnimation(AnimatorSet animation, LauncherState targetState) {
            this.mCurrentAnimation = animation;
            this.mTargetState = targetState;
            this.mCurrentAnimation.addListener(this);
        }

        public boolean playAtomicComponent() {
            return (this.animComponents & 2) != 0;
        }

        public boolean playNonAtomicComponent() {
            return (this.animComponents & 1) != 0;
        }
    }
}
