package com.android.launcher3.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.List;

public class LauncherAppsCompatVL extends LauncherAppsCompat {
    private final ArrayMap<LauncherAppsCompat.OnAppsChangedCallbackCompat, WrappedCallback> mCallbacks = new ArrayMap<>();
    protected final Context mContext;
    protected final LauncherApps mLauncherApps;

    LauncherAppsCompatVL(Context context) {
        this.mContext = context;
        this.mLauncherApps = (LauncherApps) context.getSystemService("launcherapps");
    }

    public List<LauncherActivityInfo> getActivityList(String packageName, UserHandle user) {
        return this.mLauncherApps.getActivityList(packageName, user);
    }

    public LauncherActivityInfo resolveActivity(Intent intent, UserHandle user) {
        return this.mLauncherApps.resolveActivity(intent, user);
    }

    public void startActivityForProfile(ComponentName component, UserHandle user, Rect sourceBounds, Bundle opts) {
        this.mLauncherApps.startMainActivity(component, user, sourceBounds, opts);
    }

    public ApplicationInfo getApplicationInfo(String packageName, int flags, UserHandle user) {
        boolean isPrimaryUser = Process.myUserHandle().equals(user);
        if (isPrimaryUser || flags != 0) {
            try {
                ApplicationInfo info = this.mContext.getPackageManager().getApplicationInfo(packageName, flags);
                if ((!isPrimaryUser || (info.flags & 8388608) != 0) && info.enabled) {
                    return info;
                }
                return null;
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        } else {
            List<LauncherActivityInfo> activityList = this.mLauncherApps.getActivityList(packageName, user);
            if (activityList.size() > 0) {
                return activityList.get(0).getApplicationInfo();
            }
            return null;
        }
    }

    public void showAppDetailsForProfile(ComponentName component, UserHandle user, Rect sourceBounds, Bundle opts) {
        this.mLauncherApps.startAppDetailsActivity(component, user, sourceBounds, opts);
    }

    public void addOnAppsChangedCallback(LauncherAppsCompat.OnAppsChangedCallbackCompat callback) {
        WrappedCallback wrappedCallback = new WrappedCallback(callback);
        synchronized (this.mCallbacks) {
            this.mCallbacks.put(callback, wrappedCallback);
        }
        this.mLauncherApps.registerCallback(wrappedCallback);
    }

    public void removeOnAppsChangedCallback(LauncherAppsCompat.OnAppsChangedCallbackCompat callback) {
        WrappedCallback wrappedCallback;
        synchronized (this.mCallbacks) {
            wrappedCallback = this.mCallbacks.remove(callback);
        }
        if (wrappedCallback != null) {
            this.mLauncherApps.unregisterCallback(wrappedCallback);
        }
    }

    public boolean isPackageEnabledForProfile(String packageName, UserHandle user) {
        return this.mLauncherApps.isPackageEnabled(packageName, user);
    }

    public boolean isActivityEnabledForProfile(ComponentName component, UserHandle user) {
        return this.mLauncherApps.isActivityEnabled(component, user);
    }

    private static class WrappedCallback extends LauncherApps.Callback {
        private final LauncherAppsCompat.OnAppsChangedCallbackCompat mCallback;

        public WrappedCallback(LauncherAppsCompat.OnAppsChangedCallbackCompat callback) {
            this.mCallback = callback;
        }

        public void onPackageRemoved(String packageName, UserHandle user) {
            this.mCallback.onPackageRemoved(packageName, user);
        }

        public void onPackageAdded(String packageName, UserHandle user) {
            this.mCallback.onPackageAdded(packageName, user);
        }

        public void onPackageChanged(String packageName, UserHandle user) {
            this.mCallback.onPackageChanged(packageName, user);
        }

        public void onPackagesAvailable(String[] packageNames, UserHandle user, boolean replacing) {
            this.mCallback.onPackagesAvailable(packageNames, user, replacing);
        }

        public void onPackagesUnavailable(String[] packageNames, UserHandle user, boolean replacing) {
            this.mCallback.onPackagesUnavailable(packageNames, user, replacing);
        }

        public void onPackagesSuspended(String[] packageNames, UserHandle user) {
            this.mCallback.onPackagesSuspended(packageNames, user);
        }

        public void onPackagesUnsuspended(String[] packageNames, UserHandle user) {
            this.mCallback.onPackagesUnsuspended(packageNames, user);
        }

        public void onShortcutsChanged(@NonNull String packageName, @NonNull List<ShortcutInfo> shortcuts, @NonNull UserHandle user) {
            List<ShortcutInfoCompat> shortcutInfoCompats = new ArrayList<>(shortcuts.size());
            for (ShortcutInfo shortcutInfo : shortcuts) {
                shortcutInfoCompats.add(new ShortcutInfoCompat(shortcutInfo));
            }
            this.mCallback.onShortcutsChanged(packageName, shortcutInfoCompats, user);
        }
    }

    public List<ShortcutConfigActivityInfo> getCustomShortcutActivityList(@Nullable PackageUserKey packageUser) {
        List<ShortcutConfigActivityInfo> result = new ArrayList<>();
        if (packageUser != null && !packageUser.mUser.equals(Process.myUserHandle())) {
            return result;
        }
        PackageManager pm = this.mContext.getPackageManager();
        for (ResolveInfo info : pm.queryIntentActivities(new Intent("android.intent.action.CREATE_SHORTCUT"), 0)) {
            if (packageUser == null || packageUser.mPackageName.equals(info.activityInfo.packageName)) {
                result.add(new ShortcutConfigActivityInfo.ShortcutConfigActivityInfoVL(info.activityInfo, pm));
            }
        }
        return result;
    }
}
