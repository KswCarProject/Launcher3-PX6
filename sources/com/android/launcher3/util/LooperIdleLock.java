package com.android.launcher3.util;

import android.os.Looper;
import android.os.MessageQueue;
import com.android.launcher3.Utilities;

public class LooperIdleLock implements MessageQueue.IdleHandler, Runnable {
    private boolean mIsLocked = true;
    private final Object mLock;

    public LooperIdleLock(Object lock, Looper looper) {
        this.mLock = lock;
        if (Utilities.ATLEAST_MARSHMALLOW) {
            looper.getQueue().addIdleHandler(this);
        } else {
            new LooperExecutor(looper).execute(this);
        }
    }

    public void run() {
        Looper.myQueue().addIdleHandler(this);
    }

    public boolean queueIdle() {
        synchronized (this.mLock) {
            this.mIsLocked = false;
            this.mLock.notify();
        }
        return false;
    }

    public boolean awaitLocked(long ms) {
        if (this.mIsLocked) {
            try {
                this.mLock.wait(ms);
            } catch (InterruptedException e) {
            }
        }
        return this.mIsLocked;
    }
}
