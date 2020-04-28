package com.android.launcher3.allapps;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.states.InternalStateHandler;

public class DiscoveryBounce extends AbstractFloatingView {
    private static final long DELAY_MS = 450;
    public static final String HOME_BOUNCE_SEEN = "launcher.apps_view_shown";
    public static final String SHELF_BOUNCE_SEEN = "launcher.shelf_bounce_seen";
    private final Animator mDiscoBounceAnimation;
    private final Launcher mLauncher;

    public DiscoveryBounce(Launcher launcher, float delta) {
        super(launcher, (AttributeSet) null);
        this.mLauncher = launcher;
        AllAppsTransitionController controller = this.mLauncher.getAllAppsController();
        this.mDiscoBounceAnimation = AnimatorInflater.loadAnimator(launcher, R.animator.discovery_bounce);
        this.mDiscoBounceAnimation.setTarget(new VerticalProgressWrapper(controller, delta));
        this.mDiscoBounceAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                DiscoveryBounce.this.handleClose(false);
            }
        });
        this.mDiscoBounceAnimation.addListener(controller.getProgressAnimatorListener());
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mDiscoBounceAnimation.start();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mDiscoBounceAnimation.isRunning()) {
            this.mDiscoBounceAnimation.end();
        }
    }

    public boolean onBackPressed() {
        super.onBackPressed();
        return false;
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        handleClose(false);
        return false;
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean animate) {
        if (this.mIsOpen) {
            this.mIsOpen = false;
            this.mLauncher.getDragLayer().removeView(this);
            this.mLauncher.getAllAppsController().setProgress(this.mLauncher.getStateManager().getState().getVerticalProgress(this.mLauncher));
        }
    }

    public void logActionCommand(int command) {
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int type) {
        return (type & 64) != 0;
    }

    private void show(int containerType) {
        this.mIsOpen = true;
        this.mLauncher.getDragLayer().addView(this);
        this.mLauncher.getUserEventDispatcher().logActionBounceTip(containerType);
    }

    public static void showForHomeIfNeeded(Launcher launcher) {
        showForHomeIfNeeded(launcher, true);
    }

    /* access modifiers changed from: private */
    public static void showForHomeIfNeeded(Launcher launcher, boolean withDelay) {
        if (launcher.isInState(LauncherState.NORMAL)) {
            String platformName = Utilities.getSystemProperty("ro.board.platform", "");
            if (!launcher.getSharedPrefs().getBoolean(HOME_BOUNCE_SEEN, "rk3126c".equals(platformName) || "rk3326".equals(platformName)) && AbstractFloatingView.getTopOpenView(launcher) == null && !UserManagerCompat.getInstance(launcher).isDemoUser() && !ActivityManager.isRunningInTestHarness()) {
                if (withDelay) {
                    new Handler().postDelayed(new Runnable() {
                        public final void run() {
                            DiscoveryBounce.showForHomeIfNeeded(Launcher.this, false);
                        }
                    }, DELAY_MS);
                } else {
                    new DiscoveryBounce(launcher, 0.0f).show(2);
                }
            }
        }
    }

    public static void showForOverviewIfNeeded(Launcher launcher) {
        showForOverviewIfNeeded(launcher, true);
    }

    /* access modifiers changed from: private */
    public static void showForOverviewIfNeeded(Launcher launcher, boolean withDelay) {
        if (launcher.isInState(LauncherState.OVERVIEW) && launcher.hasBeenResumed() && !launcher.isForceInvisible() && !launcher.getDeviceProfile().isVerticalBarLayout() && !launcher.getSharedPrefs().getBoolean(SHELF_BOUNCE_SEEN, false) && !UserManagerCompat.getInstance(launcher).isDemoUser() && !ActivityManager.isRunningInTestHarness()) {
            if (withDelay) {
                new Handler().postDelayed(new Runnable() {
                    public final void run() {
                        DiscoveryBounce.showForOverviewIfNeeded(Launcher.this, false);
                    }
                }, DELAY_MS);
            } else if (!InternalStateHandler.hasPending() && AbstractFloatingView.getTopOpenView(launcher) == null) {
                new DiscoveryBounce(launcher, 1.0f - LauncherState.OVERVIEW.getVerticalProgress(launcher)).show(7);
            }
        }
    }

    public static class VerticalProgressWrapper {
        private final AllAppsTransitionController mController;
        private final float mDelta;

        private VerticalProgressWrapper(AllAppsTransitionController controller, float delta) {
            this.mController = controller;
            this.mDelta = delta;
        }

        public float getProgress() {
            return this.mController.getProgress() + this.mDelta;
        }

        public void setProgress(float progress) {
            this.mController.setProgress(progress - this.mDelta);
        }
    }
}
