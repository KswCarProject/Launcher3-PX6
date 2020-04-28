package com.android.launcher3.states;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;

public class RotationHelper implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String ALLOW_ROTATION_PREFERENCE_KEY = "pref_allowRotation";
    public static final int REQUEST_LOCK = 2;
    public static final int REQUEST_NONE = 0;
    public static final int REQUEST_ROTATE = 1;
    private final Activity mActivity;
    private boolean mAutoRotateEnabled;
    private int mCurrentStateRequest = 0;
    public boolean mDestroyed;
    private final boolean mIgnoreAutoRotateSettings;
    private boolean mInitialized;
    private int mLastActivityFlags = -1;
    private final SharedPreferences mPrefs;
    private int mStateHandlerRequest = 0;

    public static boolean getAllowRotationDefaultValue() {
        if (!Utilities.ATLEAST_NOUGAT) {
            return false;
        }
        Resources res = Resources.getSystem();
        if ((res.getConfiguration().smallestScreenWidthDp * res.getDisplayMetrics().densityDpi) / DisplayMetrics.DENSITY_DEVICE_STABLE >= 600) {
            return true;
        }
        return false;
    }

    public RotationHelper(Activity activity) {
        this.mActivity = activity;
        this.mIgnoreAutoRotateSettings = this.mActivity.getResources().getBoolean(R.bool.allow_rotation);
        if (!this.mIgnoreAutoRotateSettings) {
            this.mPrefs = Utilities.getPrefs(this.mActivity);
            this.mPrefs.registerOnSharedPreferenceChangeListener(this);
            this.mAutoRotateEnabled = this.mPrefs.getBoolean(ALLOW_ROTATION_PREFERENCE_KEY, getAllowRotationDefaultValue());
            return;
        }
        this.mPrefs = null;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        this.mAutoRotateEnabled = this.mPrefs.getBoolean(ALLOW_ROTATION_PREFERENCE_KEY, getAllowRotationDefaultValue());
        notifyChange();
    }

    public void setStateHandlerRequest(int request) {
        if (this.mStateHandlerRequest != request) {
            this.mStateHandlerRequest = request;
            notifyChange();
        }
    }

    public void setCurrentStateRequest(int request) {
        if (this.mCurrentStateRequest != request) {
            this.mCurrentStateRequest = request;
            notifyChange();
        }
    }

    public void initialize() {
        if (!this.mInitialized) {
            this.mInitialized = true;
            notifyChange();
        }
    }

    public void destroy() {
        if (!this.mDestroyed) {
            this.mDestroyed = true;
            if (this.mPrefs != null) {
                this.mPrefs.unregisterOnSharedPreferenceChangeListener(this);
            }
        }
    }

    private void notifyChange() {
        int activityFlags;
        if (this.mInitialized && !this.mDestroyed) {
            if (this.mStateHandlerRequest != 0) {
                activityFlags = this.mStateHandlerRequest == 2 ? 14 : -1;
            } else if (this.mCurrentStateRequest == 2) {
                activityFlags = 14;
            } else if (this.mIgnoreAutoRotateSettings || this.mCurrentStateRequest == 1 || this.mAutoRotateEnabled) {
                activityFlags = -1;
            } else {
                activityFlags = 5;
            }
            if (activityFlags != this.mLastActivityFlags) {
                this.mLastActivityFlags = activityFlags;
                this.mActivity.setRequestedOrientation(activityFlags);
            }
        }
    }

    public String toString() {
        return String.format("[mStateHandlerRequest=%d, mCurrentStateRequest=%d, mLastActivityFlags=%d, mIgnoreAutoRotateSettings=%b, mAutoRotateEnabled=%b]", new Object[]{Integer.valueOf(this.mStateHandlerRequest), Integer.valueOf(this.mCurrentStateRequest), Integer.valueOf(this.mLastActivityFlags), Boolean.valueOf(this.mIgnoreAutoRotateSettings), Boolean.valueOf(this.mAutoRotateEnabled)});
    }
}
