package com.android.launcher3.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class ConfigMonitor extends BroadcastReceiver implements DisplayManager.DisplayListener {
    private static final String TAG = "ConfigMonitor";
    private boolean mAlreadyKill;
    private final Context mContext;
    private final int mDensity;
    private final int mDisplayId;
    private final float mFontScale;
    private final Point mLargestSize;
    private final Point mRealSize;
    private final Point mSmallestSize;
    private final Point mTmpPoint1 = new Point();
    private final Point mTmpPoint2 = new Point();

    public ConfigMonitor(Context context) {
        this.mContext = context;
        Configuration config = context.getResources().getConfiguration();
        this.mFontScale = config.fontScale;
        this.mDensity = config.densityDpi;
        Display display = getDefaultDisplay(context);
        this.mDisplayId = display.getDisplayId();
        this.mRealSize = new Point();
        display.getRealSize(this.mRealSize);
        this.mSmallestSize = new Point();
        this.mLargestSize = new Point();
        display.getCurrentSizeRange(this.mSmallestSize, this.mLargestSize);
        Log.d(TAG, "ConfigMonitor init");
        this.mAlreadyKill = false;
    }

    public void onReceive(Context context, Intent intent) {
        Configuration config = context.getResources().getConfiguration();
        if (this.mFontScale != config.fontScale || this.mDensity != config.densityDpi) {
            Log.d(TAG, "Configuration changed");
            killProcess();
        }
    }

    public void register() {
        this.mContext.registerReceiver(this, new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"));
        ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).registerDisplayListener(this, new Handler(UiThreadHelper.getBackgroundLooper()));
    }

    public void onDisplayAdded(int displayId) {
    }

    public void onDisplayRemoved(int displayId) {
    }

    public void onDisplayChanged(int displayId) {
        if (displayId == this.mDisplayId) {
            Display display = getDefaultDisplay(this.mContext);
            display.getRealSize(this.mTmpPoint1);
            if (this.mRealSize.equals(this.mTmpPoint1) || this.mRealSize.equals(this.mTmpPoint1.y, this.mTmpPoint1.x)) {
                display.getCurrentSizeRange(this.mTmpPoint1, this.mTmpPoint2);
                if (!this.mSmallestSize.equals(this.mTmpPoint1) || !this.mLargestSize.equals(this.mTmpPoint2)) {
                    Log.d(TAG, String.format("Available size changed from [%s, %s] to [%s, %s]", new Object[]{this.mSmallestSize, this.mLargestSize, this.mTmpPoint1, this.mTmpPoint2}));
                    killProcess();
                    return;
                }
                return;
            }
            Log.d(TAG, String.format("Display size changed from %s to %s", new Object[]{this.mRealSize, this.mTmpPoint1}));
            killProcess();
        }
    }

    private void killProcess() {
        Log.d(TAG, "restarting launcher");
        if (this.mAlreadyKill) {
            Log.d(TAG, "already kill launcher process");
            return;
        }
        this.mAlreadyKill = true;
        this.mContext.unregisterReceiver(this);
        ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).unregisterDisplayListener(this);
        Process.killProcess(Process.myPid());
    }

    private Display getDefaultDisplay(Context context) {
        return ((WindowManager) context.getSystemService(WindowManager.class)).getDefaultDisplay();
    }
}
