package com.android.launcher3.util;

import android.os.SystemClock;

public class FlingBlockCheck {
    private static final long UNBLOCK_FLING_PAUSE_DURATION = 200;
    private boolean mBlockFling;
    private long mBlockFlingTime;

    public void blockFling() {
        this.mBlockFling = true;
        this.mBlockFlingTime = SystemClock.uptimeMillis();
    }

    public void unblockFling() {
        this.mBlockFling = false;
        this.mBlockFlingTime = 0;
    }

    public void onEvent() {
        if (SystemClock.uptimeMillis() - this.mBlockFlingTime >= UNBLOCK_FLING_PAUSE_DURATION) {
            this.mBlockFling = false;
        }
    }

    public boolean isBlocked() {
        return this.mBlockFling;
    }
}
