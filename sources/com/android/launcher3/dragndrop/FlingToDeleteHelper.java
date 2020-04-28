package com.android.launcher3.dragndrop;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.util.FlingAnimation;

public class FlingToDeleteHelper {
    private static final float MAX_FLING_DEGREES = 35.0f;
    private ButtonDropTarget mDropTarget;
    private final int mFlingToDeleteThresholdVelocity;
    private final Launcher mLauncher;
    private VelocityTracker mVelocityTracker;

    public FlingToDeleteHelper(Launcher launcher) {
        this.mLauncher = launcher;
        this.mFlingToDeleteThresholdVelocity = launcher.getResources().getDimensionPixelSize(R.dimen.drag_flingToDeleteMinVelocity);
    }

    public void recordMotionEvent(MotionEvent ev) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(ev);
    }

    public void recordDragEvent(long dragStartTime, DragEvent event) {
        int motionAction;
        int action = event.getAction();
        if (action != 4) {
            switch (action) {
                case 1:
                    motionAction = 0;
                    break;
                case 2:
                    motionAction = 2;
                    break;
                default:
                    return;
            }
        } else {
            motionAction = 1;
        }
        MotionEvent emulatedEvent = MotionEvent.obtain(dragStartTime, SystemClock.uptimeMillis(), motionAction, event.getX(), event.getY(), 0);
        recordMotionEvent(emulatedEvent);
        emulatedEvent.recycle();
    }

    public void releaseVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public DropTarget getDropTarget() {
        return this.mDropTarget;
    }

    public Runnable getFlingAnimation(DropTarget.DragObject dragObject) {
        PointF vel = isFlingingToDelete();
        if (vel == null) {
            return null;
        }
        return new FlingAnimation(dragObject, vel, this.mDropTarget, this.mLauncher);
    }

    private PointF isFlingingToDelete() {
        if (this.mDropTarget == null) {
            this.mDropTarget = (ButtonDropTarget) this.mLauncher.findViewById(R.id.delete_target_text);
        }
        if (this.mDropTarget == null || !this.mDropTarget.isDropEnabled()) {
            return null;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, (float) ViewConfiguration.get(this.mLauncher).getScaledMaximumFlingVelocity());
        PointF vel = new PointF(this.mVelocityTracker.getXVelocity(), this.mVelocityTracker.getYVelocity());
        float theta = 36.0f;
        if (this.mVelocityTracker.getYVelocity() < ((float) this.mFlingToDeleteThresholdVelocity)) {
            theta = getAngleBetweenVectors(vel, new PointF(0.0f, -1.0f));
        } else if (this.mLauncher.getDeviceProfile().isVerticalBarLayout() && this.mVelocityTracker.getXVelocity() < ((float) this.mFlingToDeleteThresholdVelocity)) {
            theta = getAngleBetweenVectors(vel, new PointF(-1.0f, 0.0f));
        }
        if (((double) theta) <= Math.toRadians(35.0d)) {
            return vel;
        }
        return null;
    }

    private float getAngleBetweenVectors(PointF vec1, PointF vec2) {
        return (float) Math.acos((double) (((vec1.x * vec2.x) + (vec1.y * vec2.y)) / (vec1.length() * vec2.length())));
    }
}
