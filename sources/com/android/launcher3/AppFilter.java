package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

public class AppFilter {
    private static boolean mShouldHideFiles = false;

    public static AppFilter newInstance(Context context) {
        mShouldHideFiles = !TextUtils.isEmpty(Utilities.getSystemProperty("ro.com.google.gmsversion", ""));
        return (AppFilter) Utilities.getOverrideObject(AppFilter.class, context, R.string.app_filter_class);
    }

    public boolean shouldShowApp(ComponentName app) {
        if (app == null || !mShouldHideFiles) {
            return true;
        }
        String packageName = app.getPackageName();
        String className = app.getClassName();
        if (!"com.android.documentsui".equals(packageName) || !"com.android.documentsui.LauncherActivity".equals(className)) {
            return true;
        }
        return false;
    }
}
