package com.android.launcher3.compat;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.support.annotation.Nullable;
import com.android.launcher3.util.PackageUserKey;
import java.util.List;

class AppWidgetManagerCompatVO extends AppWidgetManagerCompatVL {
    AppWidgetManagerCompatVO(Context context) {
        super(context);
    }

    public List<AppWidgetProviderInfo> getAllProviders(@Nullable PackageUserKey packageUser) {
        if (packageUser == null) {
            return super.getAllProviders((PackageUserKey) null);
        }
        return this.mAppWidgetManager.getInstalledProvidersForPackage(packageUser.mPackageName, packageUser.mUser);
    }
}
