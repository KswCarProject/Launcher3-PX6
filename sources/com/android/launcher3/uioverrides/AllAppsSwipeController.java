package com.android.launcher3.uioverrides;

import android.view.MotionEvent;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.touch.AbstractStateChangeTouchController;
import com.android.launcher3.touch.SwipeDetector;

public class AllAppsSwipeController extends AbstractStateChangeTouchController {
    private MotionEvent mTouchDownEvent;

    public AllAppsSwipeController(Launcher l) {
        super(l, SwipeDetector.VERTICAL);
    }

    /* access modifiers changed from: protected */
    public boolean canInterceptTouch(MotionEvent ev) {
        if (ev.getAction() == 0) {
            this.mTouchDownEvent = ev;
        }
        if (this.mCurrentAnimation != null) {
            return true;
        }
        if (AbstractFloatingView.getTopOpenView(this.mLauncher) != null) {
            return false;
        }
        if (!this.mLauncher.isInState(LauncherState.NORMAL) && !this.mLauncher.isInState(LauncherState.ALL_APPS)) {
            return false;
        }
        if (!this.mLauncher.isInState(LauncherState.ALL_APPS) || this.mLauncher.getAppsView().shouldContainerScroll(ev)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public LauncherState getTargetState(LauncherState fromState, boolean isDragTowardPositive) {
        if (fromState == LauncherState.NORMAL && isDragTowardPositive) {
            return LauncherState.ALL_APPS;
        }
        if (fromState != LauncherState.ALL_APPS || isDragTowardPositive) {
            return fromState;
        }
        return LauncherState.NORMAL;
    }

    /* access modifiers changed from: protected */
    public int getLogContainerTypeForNormalState() {
        return this.mLauncher.getDragLayer().isEventOverView(this.mLauncher.getHotseat(), this.mTouchDownEvent) ? 2 : 1;
    }

    /* access modifiers changed from: protected */
    public float initCurrentAnimation(int animComponents) {
        float range = getShiftRange();
        this.mCurrentAnimation = this.mLauncher.getStateManager().createAnimationToNewWorkspace(this.mToState, (long) (2.0f * range), animComponents);
        return 1.0f / ((this.mToState.getVerticalProgress(this.mLauncher) * range) - (this.mFromState.getVerticalProgress(this.mLauncher) * range));
    }
}
