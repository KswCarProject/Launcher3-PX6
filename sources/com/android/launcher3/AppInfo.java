package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageManagerHelper;

public class AppInfo extends ItemInfoWithIcon {
    public ComponentName componentName;
    public Intent intent;

    public AppInfo() {
        this.itemType = 0;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public AppInfo(Context context, LauncherActivityInfo info, UserHandle user) {
        this(info, user, UserManagerCompat.getInstance(context).isQuietModeEnabled(user));
    }

    public AppInfo(LauncherActivityInfo info, UserHandle user, boolean quietModeEnabled) {
        this.componentName = info.getComponentName();
        this.container = -1;
        this.user = user;
        this.intent = makeLaunchIntent(info);
        if (quietModeEnabled) {
            this.runtimeStatusFlags |= 8;
        }
        updateRuntimeFlagsForActivityTarget(this, info);
    }

    public AppInfo(AppInfo info) {
        super(info);
        this.componentName = info.componentName;
        this.title = Utilities.trim(info.title);
        this.intent = new Intent(info.intent);
    }

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        return super.dumpProperties() + " componentName=" + this.componentName;
    }

    public ShortcutInfo makeShortcut() {
        return new ShortcutInfo(this);
    }

    public ComponentKey toComponentKey() {
        return new ComponentKey(this.componentName, this.user);
    }

    public static Intent makeLaunchIntent(LauncherActivityInfo info) {
        return makeLaunchIntent(info.getComponentName());
    }

    public static Intent makeLaunchIntent(ComponentName cn) {
        return new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(cn).setFlags(270532608);
    }

    public static void updateRuntimeFlagsForActivityTarget(ItemInfoWithIcon info, LauncherActivityInfo lai) {
        ApplicationInfo appInfo = lai.getApplicationInfo();
        if (PackageManagerHelper.isAppSuspended(appInfo)) {
            info.runtimeStatusFlags |= 4;
        }
        info.runtimeStatusFlags |= (appInfo.flags & 1) == 0 ? 128 : 64;
        if (Utilities.ATLEAST_OREO && appInfo.targetSdkVersion >= 26 && Process.myUserHandle().equals(lai.getUser())) {
            info.runtimeStatusFlags |= 256;
        }
    }
}
