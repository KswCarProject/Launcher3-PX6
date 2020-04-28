package com.android.launcher3;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.uioverrides.UiFactory;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.SystemUiController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public abstract class BaseActivity extends Activity implements UserEventDispatcher.UserEventDelegate {
    private static final int ACTIVITY_STATE_RESUMED = 2;
    private static final int ACTIVITY_STATE_STARTED = 1;
    private static final int ACTIVITY_STATE_USER_ACTIVE = 4;
    public static final int INVISIBLE_ALL = 15;
    public static final int INVISIBLE_BY_APP_TRANSITIONS = 2;
    public static final int INVISIBLE_BY_PENDING_FLAGS = 4;
    public static final int INVISIBLE_BY_STATE_HANDLER = 1;
    private static final int INVISIBLE_FLAGS = 7;
    public static final int PENDING_INVISIBLE_BY_WALLPAPER_ANIMATION = 8;
    public static final int STATE_HANDLER_INVISIBILITY_FLAGS = 9;
    private int mActivityFlags;
    private final ArrayList<DeviceProfile.OnDeviceProfileChangeListener> mDPChangeListeners = new ArrayList<>();
    protected DeviceProfile mDeviceProfile;
    private int mForceInvisible;
    private final ArrayList<MultiWindowModeChangedListener> mMultiWindowModeChangedListeners = new ArrayList<>();
    protected SystemUiController mSystemUiController;
    protected UserEventDispatcher mUserEventDispatcher;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ActivityFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface InvisibilityFlags {
    }

    public interface MultiWindowModeChangedListener {
        void onMultiWindowModeChanged(boolean z);
    }

    public DeviceProfile getDeviceProfile() {
        return this.mDeviceProfile;
    }

    public View.AccessibilityDelegate getAccessibilityDelegate() {
        return null;
    }

    public void modifyUserEvent(LauncherLogProto.LauncherEvent event) {
    }

    public final UserEventDispatcher getUserEventDispatcher() {
        if (this.mUserEventDispatcher == null) {
            this.mUserEventDispatcher = UserEventDispatcher.newInstance(this, this.mDeviceProfile, this);
        }
        return this.mUserEventDispatcher;
    }

    public boolean isInMultiWindowModeCompat() {
        return Utilities.ATLEAST_NOUGAT && isInMultiWindowMode();
    }

    public static BaseActivity fromContext(Context context) {
        if (context instanceof BaseActivity) {
            return (BaseActivity) context;
        }
        return (BaseActivity) ((ContextWrapper) context).getBaseContext();
    }

    public SystemUiController getSystemUiController() {
        if (this.mSystemUiController == null) {
            this.mSystemUiController = new SystemUiController(getWindow());
        }
        return this.mSystemUiController;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        this.mActivityFlags |= 1;
        super.onStart();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        this.mActivityFlags |= 6;
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onUserLeaveHint() {
        this.mActivityFlags &= -5;
        super.onUserLeaveHint();
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig);
        for (int i = this.mMultiWindowModeChangedListeners.size() - 1; i >= 0; i--) {
            this.mMultiWindowModeChangedListeners.get(i).onMultiWindowModeChanged(isInMultiWindowMode);
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        this.mActivityFlags &= -6;
        this.mForceInvisible = 0;
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        this.mActivityFlags &= -3;
        super.onPause();
        getSystemUiController().updateUiState(4, 0);
    }

    public boolean isStarted() {
        return (this.mActivityFlags & 1) != 0;
    }

    public boolean hasBeenResumed() {
        return (this.mActivityFlags & 2) != 0;
    }

    public boolean isUserActive() {
        return (this.mActivityFlags & 4) != 0;
    }

    public void addOnDeviceProfileChangeListener(DeviceProfile.OnDeviceProfileChangeListener listener) {
        this.mDPChangeListeners.add(listener);
    }

    public void removeOnDeviceProfileChangeListener(DeviceProfile.OnDeviceProfileChangeListener listener) {
        this.mDPChangeListeners.remove(listener);
    }

    /* access modifiers changed from: protected */
    public void dispatchDeviceProfileChanged() {
        for (int i = this.mDPChangeListeners.size() - 1; i >= 0; i--) {
            this.mDPChangeListeners.get(i).onDeviceProfileChanged(this.mDeviceProfile);
        }
    }

    public void addMultiWindowModeChangedListener(MultiWindowModeChangedListener listener) {
        this.mMultiWindowModeChangedListeners.add(listener);
    }

    public void removeMultiWindowModeChangedListener(MultiWindowModeChangedListener listener) {
        this.mMultiWindowModeChangedListeners.remove(listener);
    }

    public void addForceInvisibleFlag(int flag) {
        this.mForceInvisible |= flag;
    }

    public void clearForceInvisibleFlag(int flag) {
        this.mForceInvisible &= ~flag;
    }

    public boolean isForceInvisible() {
        return hasSomeInvisibleFlag(7);
    }

    public boolean hasSomeInvisibleFlag(int mask) {
        return (this.mForceInvisible & mask) != 0;
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        if (!UiFactory.dumpActivity(this, writer)) {
            super.dump(prefix, fd, writer, args);
        }
    }

    /* access modifiers changed from: protected */
    public void dumpMisc(PrintWriter writer) {
        writer.println(" deviceProfile isTransposed=" + getDeviceProfile().isVerticalBarLayout());
        writer.println(" orientation=" + getResources().getConfiguration().orientation);
        writer.println(" mSystemUiController: " + this.mSystemUiController);
        writer.println(" mActivityFlags: " + this.mActivityFlags);
        writer.println(" mForceInvisible: " + this.mForceInvisible);
    }
}
