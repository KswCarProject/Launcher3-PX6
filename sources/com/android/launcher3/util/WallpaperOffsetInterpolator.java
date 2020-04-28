package com.android.launcher3.util;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Interpolator;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.anim.Interpolators;

public class WallpaperOffsetInterpolator extends BroadcastReceiver {
    private static final int ANIMATION_DURATION = 250;
    private static final int MIN_PARALLAX_PAGE_SPAN = 4;
    private static final int MSG_APPLY_OFFSET = 3;
    private static final int MSG_JUMP_TO_FINAL = 5;
    private static final int MSG_SET_NUM_PARALLAX = 4;
    private static final int MSG_START_ANIMATION = 1;
    private static final int MSG_UPDATE_OFFSET = 2;
    private static final String TAG = "WPOffsetInterpolator";
    private static final int[] sTempInt = new int[2];
    private final Handler mHandler;
    private final boolean mIsRtl;
    private boolean mLockedToDefaultPage;
    private int mNumScreens;
    private boolean mRegistered = false;
    private boolean mWallpaperIsLiveWallpaper;
    private IBinder mWindowToken;
    private final Workspace mWorkspace;

    public WallpaperOffsetInterpolator(Workspace workspace) {
        this.mWorkspace = workspace;
        this.mIsRtl = Utilities.isRtl(workspace.getResources());
        this.mHandler = new OffsetHandler(workspace.getContext());
    }

    public void setLockToDefaultPage(boolean lockToDefaultPage) {
        this.mLockedToDefaultPage = lockToDefaultPage;
    }

    public boolean isLockedToDefaultPage() {
        return this.mLockedToDefaultPage;
    }

    private void wallpaperOffsetForScroll(int scroll, int numScrollingPages, int[] out) {
        int leftPageIndex;
        int rightPageIndex;
        out[1] = 1;
        if (this.mLockedToDefaultPage || numScrollingPages <= 1) {
            out[0] = this.mIsRtl;
            return;
        }
        int numPagesForWallpaperParallax = this.mWallpaperIsLiveWallpaper ? numScrollingPages : Math.max(4, numScrollingPages);
        if (this.mIsRtl) {
            rightPageIndex = 0;
            leftPageIndex = (0 + numScrollingPages) - 1;
        } else {
            leftPageIndex = 0;
            rightPageIndex = (0 + numScrollingPages) - 1;
        }
        int leftPageScrollX = this.mWorkspace.getScrollForPage(leftPageIndex);
        int scrollRange = this.mWorkspace.getScrollForPage(rightPageIndex) - leftPageScrollX;
        if (scrollRange <= 0) {
            out[0] = 0;
            return;
        }
        int adjustedScroll = Utilities.boundToRange((scroll - leftPageScrollX) - this.mWorkspace.getLayoutTransitionOffsetForPage(0), 0, scrollRange);
        out[1] = (numPagesForWallpaperParallax - 1) * scrollRange;
        int rtlOffset = 0;
        if (this.mIsRtl) {
            rtlOffset = out[1] - ((numScrollingPages - 1) * scrollRange);
        }
        out[0] = ((numScrollingPages - 1) * adjustedScroll) + rtlOffset;
    }

    public float wallpaperOffsetForScroll(int scroll) {
        wallpaperOffsetForScroll(scroll, getNumScreensExcludingEmpty(), sTempInt);
        return ((float) sTempInt[0]) / ((float) sTempInt[1]);
    }

    private int getNumScreensExcludingEmpty() {
        int numScrollingPages = this.mWorkspace.getChildCount();
        if (numScrollingPages < 4 || !this.mWorkspace.hasExtraEmptyScreen()) {
            return numScrollingPages;
        }
        return numScrollingPages - 1;
    }

    public void syncWithScroll() {
        int numScreens = getNumScreensExcludingEmpty();
        wallpaperOffsetForScroll(this.mWorkspace.getScrollX(), numScreens, sTempInt);
        Message msg = Message.obtain(this.mHandler, 2, sTempInt[0], sTempInt[1], this.mWindowToken);
        if (numScreens != this.mNumScreens) {
            if (this.mNumScreens > 0) {
                msg.what = 1;
            }
            this.mNumScreens = numScreens;
            updateOffset();
        }
        msg.sendToTarget();
    }

    private void updateOffset() {
        int numPagesForWallpaperParallax;
        if (this.mWallpaperIsLiveWallpaper) {
            numPagesForWallpaperParallax = this.mNumScreens;
        } else {
            numPagesForWallpaperParallax = Math.max(4, this.mNumScreens);
        }
        Message.obtain(this.mHandler, 4, numPagesForWallpaperParallax, 0, this.mWindowToken).sendToTarget();
    }

    public void jumpToFinal() {
        Message.obtain(this.mHandler, 5, this.mWindowToken).sendToTarget();
    }

    public void setWindowToken(IBinder token) {
        this.mWindowToken = token;
        if (this.mWindowToken == null && this.mRegistered) {
            this.mWorkspace.getContext().unregisterReceiver(this);
            this.mRegistered = false;
        } else if (this.mWindowToken != null && !this.mRegistered) {
            this.mWorkspace.getContext().registerReceiver(this, new IntentFilter("android.intent.action.WALLPAPER_CHANGED"));
            onReceive(this.mWorkspace.getContext(), (Intent) null);
            this.mRegistered = true;
        }
    }

    public void onReceive(Context context, Intent intent) {
        this.mWallpaperIsLiveWallpaper = WallpaperManager.getInstance(this.mWorkspace.getContext()).getWallpaperInfo() != null;
        updateOffset();
    }

    private static class OffsetHandler extends Handler {
        private boolean mAnimating;
        private float mAnimationStartOffset;
        private long mAnimationStartTime;
        private float mCurrentOffset = 0.5f;
        private float mFinalOffset;
        private final Interpolator mInterpolator = Interpolators.DEACCEL_1_5;
        private float mOffsetX;
        private final WallpaperManager mWM;

        public OffsetHandler(Context context) {
            super(UiThreadHelper.getBackgroundLooper());
            this.mWM = WallpaperManager.getInstance(context);
        }

        public void handleMessage(Message msg) {
            IBinder token = (IBinder) msg.obj;
            if (token != null) {
                boolean z = false;
                switch (msg.what) {
                    case 1:
                        this.mAnimating = true;
                        this.mAnimationStartOffset = this.mCurrentOffset;
                        this.mAnimationStartTime = msg.getWhen();
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        this.mOffsetX = 1.0f / ((float) (msg.arg1 - 1));
                        this.mWM.setWallpaperOffsetSteps(this.mOffsetX, 1.0f);
                        return;
                    case 5:
                        if (Float.compare(this.mCurrentOffset, this.mFinalOffset) != 0) {
                            this.mCurrentOffset = this.mFinalOffset;
                            setOffsetSafely(token);
                        }
                        this.mAnimating = false;
                        return;
                    default:
                        return;
                }
                this.mFinalOffset = ((float) msg.arg1) / ((float) msg.arg2);
                float oldOffset = this.mCurrentOffset;
                if (this.mAnimating) {
                    long durationSinceAnimation = SystemClock.uptimeMillis() - this.mAnimationStartTime;
                    this.mCurrentOffset = this.mAnimationStartOffset + ((this.mFinalOffset - this.mAnimationStartOffset) * this.mInterpolator.getInterpolation(((float) durationSinceAnimation) / 250.0f));
                    if (durationSinceAnimation < 250) {
                        z = true;
                    }
                    this.mAnimating = z;
                } else {
                    this.mCurrentOffset = this.mFinalOffset;
                }
                if (Float.compare(this.mCurrentOffset, oldOffset) != 0) {
                    setOffsetSafely(token);
                    this.mWM.setWallpaperOffsetSteps(this.mOffsetX, 1.0f);
                }
                if (this.mAnimating) {
                    Message.obtain(this, 3, token).sendToTarget();
                }
            }
        }

        private void setOffsetSafely(IBinder token) {
            try {
                this.mWM.setWallpaperOffsets(token, this.mCurrentOffset, 0.5f);
            } catch (IllegalArgumentException e) {
                Log.e(WallpaperOffsetInterpolator.TAG, "Error updating wallpaper offset: " + e);
            }
        }
    }
}
