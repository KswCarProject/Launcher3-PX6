package com.android.launcher3;

import android.view.View;

public class CheckLongPressHelper {
    public static final int DEFAULT_LONG_PRESS_TIMEOUT = 300;
    boolean mHasPerformedLongPress;
    View.OnLongClickListener mListener;
    private int mLongPressTimeout = DEFAULT_LONG_PRESS_TIMEOUT;
    private CheckForLongPress mPendingCheckForLongPress;
    View mView;

    class CheckForLongPress implements Runnable {
        CheckForLongPress() {
        }

        public void run() {
            boolean handled;
            if (CheckLongPressHelper.this.mView.getParent() != null && CheckLongPressHelper.this.mView.hasWindowFocus() && !CheckLongPressHelper.this.mHasPerformedLongPress) {
                if (CheckLongPressHelper.this.mListener != null) {
                    handled = CheckLongPressHelper.this.mListener.onLongClick(CheckLongPressHelper.this.mView);
                } else {
                    handled = CheckLongPressHelper.this.mView.performLongClick();
                }
                if (handled) {
                    CheckLongPressHelper.this.mView.setPressed(false);
                    CheckLongPressHelper.this.mHasPerformedLongPress = true;
                }
            }
        }
    }

    public CheckLongPressHelper(View v) {
        this.mView = v;
    }

    public CheckLongPressHelper(View v, View.OnLongClickListener listener) {
        this.mView = v;
        this.mListener = listener;
    }

    public void setLongPressTimeout(int longPressTimeout) {
        this.mLongPressTimeout = longPressTimeout;
    }

    public void postCheckForLongPress() {
        this.mHasPerformedLongPress = false;
        if (this.mPendingCheckForLongPress == null) {
            this.mPendingCheckForLongPress = new CheckForLongPress();
        }
        this.mView.postDelayed(this.mPendingCheckForLongPress, (long) this.mLongPressTimeout);
    }

    public void cancelLongPress() {
        this.mHasPerformedLongPress = false;
        if (this.mPendingCheckForLongPress != null) {
            this.mView.removeCallbacks(this.mPendingCheckForLongPress);
            this.mPendingCheckForLongPress = null;
        }
    }

    public boolean hasPerformedLongPress() {
        return this.mHasPerformedLongPress;
    }
}
