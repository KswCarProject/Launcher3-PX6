package com.android.launcher3.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import com.android.launcher3.Utilities;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.PackageUserKey;
import java.util.List;

public abstract class LauncherAppsCompat {
    private static LauncherAppsCompat sInstance;
    private static final Object sInstanceLock = new Object();

    public interface OnAppsChangedCallbackCompat {
        void onPackageAdded(String str, UserHandle userHandle);

        void onPackageChanged(String str, UserHandle userHandle);

        void onPackageRemoved(String str, UserHandle userHandle);

        void onPackagesAvailable(String[] strArr, UserHandle userHandle, boolean z);

        void onPackagesSuspended(String[] strArr, UserHandle userHandle);

        void onPackagesUnavailable(String[] strArr, UserHandle userHandle, boolean z);

        void onPackagesUnsuspended(String[] strArr, UserHandle userHandle);

        void onShortcutsChanged(String str, List<ShortcutInfoCompat> list, UserHandle userHandle);
    }

    public abstract void addOnAppsChangedCallback(OnAppsChangedCallbackCompat onAppsChangedCallbackCompat);

    public abstract List<LauncherActivityInfo> getActivityList(String str, UserHandle userHandle);

    public abstract ApplicationInfo getApplicationInfo(String str, int i, UserHandle userHandle);

    public abstract List<ShortcutConfigActivityInfo> getCustomShortcutActivityList(@Nullable PackageUserKey packageUserKey);

    public abstract boolean isActivityEnabledForProfile(ComponentName componentName, UserHandle userHandle);

    public abstract boolean isPackageEnabledForProfile(String str, UserHandle userHandle);

    public abstract void removeOnAppsChangedCallback(OnAppsChangedCallbackCompat onAppsChangedCallbackCompat);

    public abstract LauncherActivityInfo resolveActivity(Intent intent, UserHandle userHandle);

    public abstract void showAppDetailsForProfile(ComponentName componentName, UserHandle userHandle, Rect rect, Bundle bundle);

    public abstract void startActivityForProfile(ComponentName componentName, UserHandle userHandle, Rect rect, Bundle bundle);

    protected LauncherAppsCompat() {
    }

    public static LauncherAppsCompat getInstance(Context context) {
        LauncherAppsCompat launcherAppsCompat;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                if (Utilities.ATLEAST_OREO) {
                    sInstance = new LauncherAppsCompatVO(context.getApplicationContext());
                } else {
                    sInstance = new LauncherAppsCompatVL(context.getApplicationContext());
                }
            }
            launcherAppsCompat = sInstance;
        }
        return launcherAppsCompat;
    }

    public void showAppDetailsForProfile(ComponentName component, UserHandle user) {
        showAppDetailsForProfile(component, user, (Rect) null, (Bundle) null);
    }
}
