package com.android.launcher3;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class StylusEventHelper {
    private boolean mIsButtonPressed;
    private StylusButtonListener mListener;
    private final float mSlop;
    private View mView;

    public interface StylusButtonListener {
        boolean onPressed(MotionEvent motionEvent);

        boolean onReleased(MotionEvent motionEvent);
    }

    public StylusEventHelper(StylusButtonListener listener, View view) {
        this.mListener = listener;
        this.mView = view;
        if (this.mView != null) {
            this.mSlop = (float) ViewConfiguration.get(this.mView.getContext()).getScaledTouchSlop();
        } else {
            this.mSlop = (float) ViewConfiguration.getTouchSlop();
        }
    }

    public boolean onMotionEvent(MotionEvent event) {
        boolean stylusButtonPressed = isStylusButtonPressed(event);
        switch (event.getAction()) {
            case 0:
                this.mIsButtonPressed = stylusButtonPressed;
                if (this.mIsButtonPressed) {
                    return this.mListener.onPressed(event);
                }
                break;
            case 1:
            case 3:
                if (this.mIsButtonPressed) {
                    this.mIsButtonPressed = false;
                    return this.mListener.onReleased(event);
                }
                break;
            case 2:
                if (!Utilities.pointInView(this.mView, event.getX(), event.getY(), this.mSlop)) {
                    return false;
                }
                if (!this.mIsButtonPressed && stylusButtonPressed) {
                    this.mIsButtonPressed = true;
                    return this.mListener.onPressed(event);
                } else if (this.mIsButtonPressed && !stylusButtonPressed) {
                    this.mIsButtonPressed = false;
                    return this.mListener.onReleased(event);
                }
        }
        return false;
    }

    public boolean inStylusButtonPressed() {
        return this.mIsButtonPressed;
    }

    private static boolean isStylusButtonPressed(MotionEvent event) {
        if (event.getToolType(0) == 2 && (event.getButtonState() & 2) == 2) {
            return true;
        }
        return false;
    }
}
