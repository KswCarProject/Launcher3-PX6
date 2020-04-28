package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.util.FlagOp;
import com.android.launcher3.util.ItemInfoMatcher;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class AllAppsList {
    public static final int DEFAULT_APPLICATIONS_NUMBER = 42;
    private static final String TAG = "AllAppsList";
    public ArrayList<AppInfo> added = new ArrayList<>(42);
    public final ArrayList<AppInfo> data = new ArrayList<>(42);
    private AppFilter mAppFilter;
    private IconCache mIconCache;
    public ArrayList<AppInfo> modified = new ArrayList<>();
    public ArrayList<AppInfo> removed = new ArrayList<>();

    public AllAppsList(IconCache iconCache, AppFilter appFilter) {
        this.mIconCache = iconCache;
        this.mAppFilter = appFilter;
    }

    public void add(AppInfo info, LauncherActivityInfo activityInfo) {
        if (this.mAppFilter.shouldShowApp(info.componentName) && findAppInfo(info.componentName, info.user) == null) {
            this.mIconCache.getTitleAndIcon(info, activityInfo, true);
            this.data.add(info);
            this.added.add(info);
        }
    }

    public void addPromiseApp(Context context, PackageInstallerCompat.PackageInstallInfo installInfo) {
        if (LauncherAppsCompat.getInstance(context).getApplicationInfo(installInfo.packageName, 0, Process.myUserHandle()) == null) {
            PromiseAppInfo info = new PromiseAppInfo(installInfo);
            this.mIconCache.getTitleAndIcon(info, info.usingLowResIcon);
            this.data.add(info);
            this.added.add(info);
        }
    }

    public void removePromiseApp(AppInfo appInfo) {
        this.data.remove(appInfo);
    }

    public void clear() {
        this.data.clear();
        this.added.clear();
        this.removed.clear();
        this.modified.clear();
    }

    public int size() {
        return this.data.size();
    }

    public AppInfo get(int index) {
        return this.data.get(index);
    }

    public void addPackage(Context context, String packageName, UserHandle user) {
        for (LauncherActivityInfo info : LauncherAppsCompat.getInstance(context).getActivityList(packageName, user)) {
            add(new AppInfo(context, info, user), info);
        }
    }

    public void removePackage(String packageName, UserHandle user) {
        List<AppInfo> data2 = this.data;
        for (int i = data2.size() - 1; i >= 0; i--) {
            AppInfo info = data2.get(i);
            if (info.user.equals(user) && packageName.equals(info.componentName.getPackageName())) {
                this.removed.add(info);
                data2.remove(i);
            }
        }
    }

    public void updateDisabledFlags(ItemInfoMatcher matcher, FlagOp op) {
        List<AppInfo> data2 = this.data;
        for (int i = data2.size() - 1; i >= 0; i--) {
            AppInfo info = data2.get(i);
            if (matcher.matches(info, info.componentName)) {
                info.runtimeStatusFlags = op.apply(info.runtimeStatusFlags);
                this.modified.add(info);
            }
        }
    }

    public void updateIconsAndLabels(HashSet<String> packages, UserHandle user, ArrayList<AppInfo> outUpdates) {
        Iterator<AppInfo> it = this.data.iterator();
        while (it.hasNext()) {
            AppInfo info = it.next();
            if (info.user.equals(user) && packages.contains(info.componentName.getPackageName())) {
                this.mIconCache.updateTitleAndIcon(info);
                outUpdates.add(info);
            }
        }
    }

    public void updatePackage(Context context, String packageName, UserHandle user) {
        List<LauncherActivityInfo> matches = LauncherAppsCompat.getInstance(context).getActivityList(packageName, user);
        if (matches.size() > 0) {
            for (int i = this.data.size() - 1; i >= 0; i--) {
                AppInfo applicationInfo = this.data.get(i);
                if (user.equals(applicationInfo.user) && packageName.equals(applicationInfo.componentName.getPackageName()) && !findActivity(matches, applicationInfo.componentName)) {
                    Log.w(TAG, "Shortcut will be removed due to app component name change.");
                    this.removed.add(applicationInfo);
                    this.data.remove(i);
                }
            }
            for (LauncherActivityInfo info : matches) {
                AppInfo applicationInfo2 = findAppInfo(info.getComponentName(), user);
                if (applicationInfo2 == null) {
                    add(new AppInfo(context, info, user), info);
                } else {
                    this.mIconCache.getTitleAndIcon(applicationInfo2, info, true);
                    this.modified.add(applicationInfo2);
                }
            }
            return;
        }
        for (int i2 = this.data.size() - 1; i2 >= 0; i2--) {
            AppInfo applicationInfo3 = this.data.get(i2);
            if (user.equals(applicationInfo3.user) && packageName.equals(applicationInfo3.componentName.getPackageName())) {
                this.removed.add(applicationInfo3);
                this.mIconCache.remove(applicationInfo3.componentName, user);
                this.data.remove(i2);
            }
        }
    }

    private static boolean findActivity(List<LauncherActivityInfo> apps, ComponentName component) {
        for (LauncherActivityInfo info : apps) {
            if (info.getComponentName().equals(component)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private AppInfo findAppInfo(@NonNull ComponentName componentName, @NonNull UserHandle user) {
        Iterator<AppInfo> it = this.data.iterator();
        while (it.hasNext()) {
            AppInfo info = it.next();
            if (componentName.equals(info.componentName) && user.equals(info.user)) {
                return info;
            }
        }
        return null;
    }
}
