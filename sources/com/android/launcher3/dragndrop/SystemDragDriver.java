package com.android.launcher3.dragndrop;

import android.content.Context;
import android.view.DragEvent;
import android.view.MotionEvent;
import com.android.launcher3.DropTarget;

/* compiled from: DragDriver */
class SystemDragDriver extends DragDriver {
    float mLastX = 0.0f;
    float mLastY = 0.0f;

    SystemDragDriver(DragController dragController, Context context, DropTarget.DragObject dragObject) {
        super(dragController);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    public boolean onDragEvent(DragEvent event) {
        switch (event.getAction()) {
            case 1:
                this.mLastX = event.getX();
                this.mLastY = event.getY();
                return true;
            case 2:
                this.mLastX = event.getX();
                this.mLastY = event.getY();
                this.mEventListener.onDriverDragMove(event.getX(), event.getY());
                return true;
            case 3:
                this.mLastX = event.getX();
                this.mLastY = event.getY();
                this.mEventListener.onDriverDragMove(event.getX(), event.getY());
                this.mEventListener.onDriverDragEnd(this.mLastX, this.mLastY);
                return true;
            case 4:
                this.mEventListener.onDriverDragCancel();
                return true;
            case 5:
                return true;
            case 6:
                this.mEventListener.onDriverDragExitWindow();
                return true;
            default:
                return false;
        }
    }
}
