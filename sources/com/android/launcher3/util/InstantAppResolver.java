package com.android.launcher3.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.android.launcher3.AppInfo;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import java.util.Collections;
import java.util.List;

public class InstantAppResolver {
    public static InstantAppResolver newInstance(Context context) {
        return (InstantAppResolver) Utilities.getOverrideObject(InstantAppResolver.class, context, R.string.instant_app_resolver_class);
    }

    public boolean isInstantApp(ApplicationInfo info) {
        return false;
    }

    public boolean isInstantApp(AppInfo info) {
        return false;
    }

    public boolean isInstantApp(Context context, String packageName) {
        try {
            return isInstantApp(context.getPackageManager().getPackageInfo(packageName, 0).applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("InstantAppResolver", "Failed to determine whether package is instant app " + packageName, e);
            return false;
        }
    }

    public List<ApplicationInfo> getInstantApps() {
        return Collections.emptyList();
    }
}
