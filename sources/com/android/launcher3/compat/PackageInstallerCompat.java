package com.android.launcher3.compat;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInstaller;
import android.support.annotation.NonNull;
import java.util.HashMap;
import java.util.List;

public abstract class PackageInstallerCompat {
    public static final int STATUS_FAILED = 2;
    public static final int STATUS_INSTALLED = 0;
    public static final int STATUS_INSTALLING = 1;
    private static PackageInstallerCompat sInstance;
    private static final Object sInstanceLock = new Object();

    public abstract List<PackageInstaller.SessionInfo> getAllVerifiedSessions();

    public abstract void onStop();

    public abstract HashMap<String, PackageInstaller.SessionInfo> updateAndGetActiveSessionCache();

    public static PackageInstallerCompat getInstance(Context context) {
        PackageInstallerCompat packageInstallerCompat;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new PackageInstallerCompatVL(context);
            }
            packageInstallerCompat = sInstance;
        }
        return packageInstallerCompat;
    }

    public static final class PackageInstallInfo {
        public final ComponentName componentName;
        public final String packageName;
        public final int progress;
        public final int state;

        private PackageInstallInfo(@NonNull PackageInstaller.SessionInfo info) {
            this.state = 1;
            this.packageName = info.getAppPackageName();
            this.componentName = new ComponentName(this.packageName, "");
            this.progress = (int) (info.getProgress() * 100.0f);
        }

        public PackageInstallInfo(String packageName2, int state2, int progress2) {
            this.state = state2;
            this.packageName = packageName2;
            this.componentName = new ComponentName(packageName2, "");
            this.progress = progress2;
        }

        public static PackageInstallInfo fromInstallingState(PackageInstaller.SessionInfo info) {
            return new PackageInstallInfo(info);
        }

        public static PackageInstallInfo fromState(int state2, String packageName2) {
            return new PackageInstallInfo(packageName2, state2, 0);
        }
    }
}
