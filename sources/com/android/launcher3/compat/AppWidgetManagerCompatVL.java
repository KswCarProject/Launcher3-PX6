package com.android.launcher3.compat;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.annotation.Nullable;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class AppWidgetManagerCompatVL extends AppWidgetManagerCompat {
    private final UserManager mUserManager;

    AppWidgetManagerCompatVL(Context context) {
        super(context);
        this.mUserManager = (UserManager) context.getSystemService("user");
    }

    public List<AppWidgetProviderInfo> getAllProviders(@Nullable PackageUserKey packageUser) {
        if (packageUser == null) {
            ArrayList<AppWidgetProviderInfo> providers = new ArrayList<>();
            for (UserHandle user : this.mUserManager.getUserProfiles()) {
                providers.addAll(this.mAppWidgetManager.getInstalledProvidersForProfile(user));
            }
            return providers;
        }
        List<AppWidgetProviderInfo> providers2 = new ArrayList<>(this.mAppWidgetManager.getInstalledProvidersForProfile(packageUser.mUser));
        Iterator<AppWidgetProviderInfo> iterator = providers2.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().provider.getPackageName().equals(packageUser.mPackageName)) {
                iterator.remove();
            }
        }
        return providers2;
    }

    public boolean bindAppWidgetIdIfAllowed(int appWidgetId, AppWidgetProviderInfo info, Bundle options) {
        return this.mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, info.getProfile(), info.provider, options);
    }

    public LauncherAppWidgetProviderInfo findProvider(ComponentName provider, UserHandle user) {
        for (AppWidgetProviderInfo info : getAllProviders(new PackageUserKey(provider.getPackageName(), user))) {
            if (info.provider.equals(provider)) {
                return LauncherAppWidgetProviderInfo.fromProviderInfo(this.mContext, info);
            }
        }
        return null;
    }

    public HashMap<ComponentKey, AppWidgetProviderInfo> getAllProvidersMap() {
        HashMap<ComponentKey, AppWidgetProviderInfo> result = new HashMap<>();
        for (UserHandle user : this.mUserManager.getUserProfiles()) {
            for (AppWidgetProviderInfo info : this.mAppWidgetManager.getInstalledProvidersForProfile(user)) {
                result.put(new ComponentKey(info.provider, user), info);
            }
        }
        return result;
    }
}
