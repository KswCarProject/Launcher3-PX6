package com.android.launcher3.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageManagerHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class SdCardAvailableReceiver extends BroadcastReceiver {
    private final Context mContext;
    private final LauncherModel mModel;
    private final MultiHashMap<UserHandle, String> mPackages;

    public SdCardAvailableReceiver(LauncherAppState app, MultiHashMap<UserHandle, String> packages) {
        this.mModel = app.getModel();
        this.mContext = app.getContext();
        this.mPackages = packages;
    }

    public void onReceive(Context context, Intent intent) {
        LauncherAppsCompat launcherApps = LauncherAppsCompat.getInstance(context);
        PackageManagerHelper pmHelper = new PackageManagerHelper(context);
        for (Map.Entry<UserHandle, ArrayList<String>> entry : this.mPackages.entrySet()) {
            UserHandle user = entry.getKey();
            ArrayList<String> packagesRemoved = new ArrayList<>();
            ArrayList<String> packagesUnavailable = new ArrayList<>();
            Iterator it = new HashSet(entry.getValue()).iterator();
            while (it.hasNext()) {
                String pkg = (String) it.next();
                if (!launcherApps.isPackageEnabledForProfile(pkg, user)) {
                    if (pmHelper.isAppOnSdcard(pkg, user)) {
                        packagesUnavailable.add(pkg);
                    } else {
                        packagesRemoved.add(pkg);
                    }
                }
            }
            if (!packagesRemoved.isEmpty()) {
                this.mModel.onPackagesRemoved(user, (String[]) packagesRemoved.toArray(new String[packagesRemoved.size()]));
            }
            if (!packagesUnavailable.isEmpty()) {
                this.mModel.onPackagesUnavailable((String[]) packagesUnavailable.toArray(new String[packagesUnavailable.size()]), user, false);
            }
        }
        this.mContext.unregisterReceiver(this);
    }
}
