package com.android.launcher3.util;

import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.launcher3.AppInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.PromiseAppInfo;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.LauncherAppsCompat;
import java.net.URISyntaxException;
import java.util.List;

public class PackageManagerHelper {
    private static final String TAG = "PackageManagerHelper";
    private final Context mContext;
    private final LauncherAppsCompat mLauncherApps;
    private final PackageManager mPm;

    public PackageManagerHelper(Context context) {
        this.mContext = context;
        this.mPm = context.getPackageManager();
        this.mLauncherApps = LauncherAppsCompat.getInstance(context);
    }

    public boolean isAppOnSdcard(String packageName, UserHandle user) {
        ApplicationInfo info = this.mLauncherApps.getApplicationInfo(packageName, 8192, user);
        return (info == null || (info.flags & 262144) == 0) ? false : true;
    }

    public boolean isAppSuspended(String packageName, UserHandle user) {
        ApplicationInfo info = this.mLauncherApps.getApplicationInfo(packageName, 0, user);
        if (info == null || !isAppSuspended(info)) {
            return false;
        }
        return true;
    }

    public boolean isSafeMode() {
        return this.mContext.getPackageManager().isSafeMode();
    }

    public Intent getAppLaunchIntent(String pkg, UserHandle user) {
        List<LauncherActivityInfo> activities = this.mLauncherApps.getActivityList(pkg, user);
        if (activities.isEmpty()) {
            return null;
        }
        return AppInfo.makeLaunchIntent(activities.get(0));
    }

    public static boolean isAppSuspended(ApplicationInfo info) {
        if (!Utilities.ATLEAST_NOUGAT || (info.flags & 1073741824) == 0) {
            return false;
        }
        return true;
    }

    public boolean hasPermissionForActivity(Intent intent, String srcPackage) {
        ResolveInfo target = this.mPm.resolveActivity(intent, 0);
        if (target == null) {
            return false;
        }
        if (TextUtils.isEmpty(target.activityInfo.permission)) {
            return true;
        }
        if (TextUtils.isEmpty(srcPackage) || this.mPm.checkPermission(target.activityInfo.permission, srcPackage) != 0) {
            return false;
        }
        if (!Utilities.ATLEAST_MARSHMALLOW || TextUtils.isEmpty(AppOpsManager.permissionToOp(target.activityInfo.permission))) {
            return true;
        }
        try {
            if (this.mPm.getApplicationInfo(srcPackage, 0).targetSdkVersion >= 23) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public Intent getMarketIntent(String packageName) {
        return new Intent("android.intent.action.VIEW").setData(new Uri.Builder().scheme("market").authority("details").appendQueryParameter("id", packageName).build()).putExtra("android.intent.extra.REFERRER", new Uri.Builder().scheme("android-app").authority(this.mContext.getPackageName()).build());
    }

    public static Intent getMarketSearchIntent(Context context, String query) {
        try {
            Intent intent = Intent.parseUri(context.getString(R.string.market_search_intent), 0);
            if (!TextUtils.isEmpty(query)) {
                intent.setData(intent.getData().buildUpon().appendQueryParameter("q", query).build());
            }
            return intent;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void startDetailsActivityForInfo(ItemInfo info, Rect sourceBounds, Bundle opts) {
        if (info instanceof PromiseAppInfo) {
            this.mContext.startActivity(((PromiseAppInfo) info).getMarketIntent(this.mContext));
            return;
        }
        ComponentName componentName = null;
        if (info instanceof AppInfo) {
            componentName = ((AppInfo) info).componentName;
        } else if (info instanceof ShortcutInfo) {
            componentName = info.getTargetComponent();
        } else if (info instanceof PendingAddItemInfo) {
            componentName = ((PendingAddItemInfo) info).componentName;
        } else if (info instanceof LauncherAppWidgetInfo) {
            componentName = ((LauncherAppWidgetInfo) info).providerName;
        }
        if (componentName != null) {
            try {
                this.mLauncherApps.showAppDetailsForProfile(componentName, info.user, sourceBounds, opts);
            } catch (ActivityNotFoundException | SecurityException e) {
                Toast.makeText(this.mContext, R.string.activity_not_found, 0).show();
                Log.e(TAG, "Unable to launch settings", e);
            }
        }
    }
}
