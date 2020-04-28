package com.android.launcher3.touch;

import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Workspace;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.views.OptionsPopupView;

public class WorkspaceTouchListener implements View.OnTouchListener, Runnable {
    private static final int STATE_CANCELLED = 0;
    private static final int STATE_COMPLETED = 3;
    private static final int STATE_PENDING_PARENT_INFORM = 2;
    private static final int STATE_REQUESTED = 1;
    private final Launcher mLauncher;
    private int mLongPressState = 0;
    private final Rect mTempRect = new Rect();
    private final PointF mTouchDownPoint = new PointF();
    private final Workspace mWorkspace;

    public WorkspaceTouchListener(Launcher launcher, Workspace workspace) {
        this.mLauncher = launcher;
        this.mWorkspace = workspace;
    }

    public boolean onTouch(View view, MotionEvent ev) {
        boolean result;
        int action = ev.getActionMasked();
        if (action == 0) {
            boolean handleLongPress = canHandleLongPress();
            if (handleLongPress) {
                DeviceProfile dp = this.mLauncher.getDeviceProfile();
                DragLayer dl = this.mLauncher.getDragLayer();
                Rect insets = dp.getInsets();
                this.mTempRect.set(insets.left, insets.top, dl.getWidth() - insets.right, dl.getHeight() - insets.bottom);
                this.mTempRect.inset(dp.edgeMarginPx, dp.edgeMarginPx);
                handleLongPress = this.mTempRect.contains((int) ev.getX(), (int) ev.getY());
            }
            cancelLongPress();
            if (handleLongPress) {
                this.mLongPressState = 1;
                this.mTouchDownPoint.set(ev.getX(), ev.getY());
                this.mWorkspace.postDelayed(this, (long) ViewConfiguration.getLongPressTimeout());
            }
            this.mWorkspace.onTouchEvent(ev);
            return true;
        }
        if (this.mLongPressState == 2) {
            ev.setAction(3);
            this.mWorkspace.onTouchEvent(ev);
            ev.setAction(action);
            this.mLongPressState = 3;
        }
        if (this.mLongPressState == 3) {
            result = true;
        } else if (this.mLongPressState == 1) {
            this.mWorkspace.onTouchEvent(ev);
            if (this.mWorkspace.isHandlingTouch()) {
                cancelLongPress();
            }
            result = true;
        } else {
            result = false;
        }
        if ((action == 1 || action == 6) && !this.mWorkspace.isTouchActive() && ((CellLayout) this.mWorkspace.getChildAt(this.mWorkspace.getCurrentPage())) != null) {
            this.mWorkspace.onWallpaperTap(ev);
        }
        if (action == 1 || action == 3) {
            cancelLongPress();
        }
        return result;
    }

    private boolean canHandleLongPress() {
        return AbstractFloatingView.getTopOpenView(this.mLauncher) == null && this.mLauncher.isInState(LauncherState.NORMAL);
    }

    private void cancelLongPress() {
        this.mWorkspace.removeCallbacks(this);
        this.mLongPressState = 0;
    }

    public void run() {
        if (this.mLongPressState != 1) {
            return;
        }
        if (canHandleLongPress()) {
            this.mLongPressState = 2;
            this.mWorkspace.getParent().requestDisallowInterceptTouchEvent(true);
            this.mWorkspace.performHapticFeedback(0, 1);
            this.mLauncher.getUserEventDispatcher().logActionOnContainer(1, 0, 1, this.mWorkspace.getCurrentPage());
            OptionsPopupView.showDefaultOptions(this.mLauncher, this.mTouchDownPoint.x, this.mTouchDownPoint.y);
            return;
        }
        cancelLongPress();
    }
}
