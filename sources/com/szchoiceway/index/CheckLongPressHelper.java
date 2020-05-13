package com.szchoiceway.index;

import android.view.View;

public class CheckLongPressHelper {
    /* access modifiers changed from: private */
    public boolean mHasPerformedLongPress;
    private CheckForLongPress mPendingCheckForLongPress;
    /* access modifiers changed from: private */
    public View mView;

    class CheckForLongPress implements Runnable {
        CheckForLongPress() {
        }

        public void run() {
            if (CheckLongPressHelper.this.mView.getParent() != null && CheckLongPressHelper.this.mView.hasWindowFocus() && !CheckLongPressHelper.this.mHasPerformedLongPress && CheckLongPressHelper.this.mView.performLongClick()) {
                CheckLongPressHelper.this.mView.setPressed(false);
                boolean unused = CheckLongPressHelper.this.mHasPerformedLongPress = true;
            }
        }
    }

    public CheckLongPressHelper(View v) {
        this.mView = v;
    }

    public void postCheckForLongPress() {
        this.mHasPerformedLongPress = false;
        if (this.mPendingCheckForLongPress == null) {
            this.mPendingCheckForLongPress = new CheckForLongPress();
        }
        this.mView.postDelayed(this.mPendingCheckForLongPress, (long) LauncherApplication.getLongPressTimeout());
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
