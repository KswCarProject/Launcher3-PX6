package com.android.launcher3;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.util.PackageManagerHelper;

public class PromiseAppInfo extends AppInfo {
    public int level = 0;

    public PromiseAppInfo(@NonNull PackageInstallerCompat.PackageInstallInfo installInfo) {
        this.componentName = installInfo.componentName;
        this.intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(this.componentName).setFlags(270532608);
    }

    public ShortcutInfo makeShortcut() {
        ShortcutInfo shortcut = new ShortcutInfo((AppInfo) this);
        shortcut.setInstallProgress(this.level);
        shortcut.status |= 2;
        shortcut.status |= 8;
        return shortcut;
    }

    public Intent getMarketIntent(Context context) {
        return new PackageManagerHelper(context).getMarketIntent(this.componentName.getPackageName());
    }
}
