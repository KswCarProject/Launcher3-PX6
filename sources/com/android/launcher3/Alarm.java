package com.android.launcher3;

import android.os.Handler;
import android.os.SystemClock;

public class Alarm implements Runnable {
    private OnAlarmListener mAlarmListener;
    private boolean mAlarmPending = false;
    private long mAlarmTriggerTime;
    private Handler mHandler = new Handler();
    private boolean mWaitingForCallback;

    public void setOnAlarmListener(OnAlarmListener alarmListener) {
        this.mAlarmListener = alarmListener;
    }

    public void setAlarm(long millisecondsInFuture) {
        long currentTime = SystemClock.uptimeMillis();
        this.mAlarmPending = true;
        long oldTriggerTime = this.mAlarmTriggerTime;
        this.mAlarmTriggerTime = currentTime + millisecondsInFuture;
        if (this.mWaitingForCallback && oldTriggerTime > this.mAlarmTriggerTime) {
            this.mHandler.removeCallbacks(this);
            this.mWaitingForCallback = false;
        }
        if (!this.mWaitingForCallback) {
            this.mHandler.postDelayed(this, this.mAlarmTriggerTime - currentTime);
            this.mWaitingForCallback = true;
        }
    }

    public void cancelAlarm() {
        this.mAlarmPending = false;
    }

    public void run() {
        this.mWaitingForCallback = false;
        if (this.mAlarmPending) {
            long currentTime = SystemClock.uptimeMillis();
            if (this.mAlarmTriggerTime > currentTime) {
                this.mHandler.postDelayed(this, Math.max(0, this.mAlarmTriggerTime - currentTime));
                this.mWaitingForCallback = true;
                return;
            }
            this.mAlarmPending = false;
            if (this.mAlarmListener != null) {
                this.mAlarmListener.onAlarm(this);
            }
        }
    }

    public boolean alarmPending() {
        return this.mAlarmPending;
    }
}
