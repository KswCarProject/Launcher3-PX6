package com.android.launcher3.dragndrop;

import android.content.Context;
import android.view.DragEvent;
import android.view.MotionEvent;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Utilities;

public abstract class DragDriver {
    protected final EventListener mEventListener;

    public interface EventListener {
        void onDriverDragCancel();

        void onDriverDragEnd(float f, float f2);

        void onDriverDragExitWindow();

        void onDriverDragMove(float f, float f2);
    }

    public abstract boolean onDragEvent(DragEvent dragEvent);

    public DragDriver(EventListener eventListener) {
        this.mEventListener = eventListener;
    }

    public void onDragViewAnimationEnd() {
    }

    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 1:
                this.mEventListener.onDriverDragMove(ev.getX(), ev.getY());
                this.mEventListener.onDriverDragEnd(ev.getX(), ev.getY());
                return true;
            case 2:
                this.mEventListener.onDriverDragMove(ev.getX(), ev.getY());
                return true;
            case 3:
                this.mEventListener.onDriverDragCancel();
                return true;
            default:
                return true;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == 1) {
            this.mEventListener.onDriverDragEnd(ev.getX(), ev.getY());
        } else if (action == 3) {
            this.mEventListener.onDriverDragCancel();
        }
        return true;
    }

    public static DragDriver create(Context context, DragController dragController, DropTarget.DragObject dragObject, DragOptions options) {
        if (!Utilities.ATLEAST_NOUGAT || options.systemDndStartPoint == null) {
            return new InternalDragDriver(dragController);
        }
        return new SystemDragDriver(dragController, context, dragObject);
    }
}
