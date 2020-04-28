package com.android.launcher3.compat;

import android.content.Context;
import android.content.pm.PackageInstaller;
import android.os.Handler;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.SparseArray;
import com.android.launcher3.IconCache;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.compat.PackageInstallerCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PackageInstallerCompatVL extends PackageInstallerCompat {
    private static final boolean DEBUG = false;
    final SparseArray<String> mActiveSessions = new SparseArray<>();
    private final Context mAppContext;
    private final IconCache mCache;
    private final PackageInstaller.SessionCallback mCallback = new PackageInstaller.SessionCallback() {
        public void onCreated(int sessionId) {
            PackageInstaller.SessionInfo pushSessionDisplayToLauncher = pushSessionDisplayToLauncher(sessionId);
        }

        public void onFinished(int sessionId, boolean success) {
            String packageName = PackageInstallerCompatVL.this.mActiveSessions.get(sessionId);
            PackageInstallerCompatVL.this.mActiveSessions.remove(sessionId);
            if (packageName != null) {
                PackageInstallerCompatVL.this.sendUpdate(PackageInstallerCompat.PackageInstallInfo.fromState(success ? 0 : 2, packageName));
            }
        }

        public void onProgressChanged(int sessionId, float progress) {
            PackageInstaller.SessionInfo session = PackageInstallerCompatVL.this.verify(PackageInstallerCompatVL.this.mInstaller.getSessionInfo(sessionId));
            if (session != null && session.getAppPackageName() != null) {
                PackageInstallerCompatVL.this.sendUpdate(PackageInstallerCompat.PackageInstallInfo.fromInstallingState(session));
            }
        }

        public void onActiveChanged(int sessionId, boolean active) {
        }

        public void onBadgingChanged(int sessionId) {
            pushSessionDisplayToLauncher(sessionId);
        }

        private PackageInstaller.SessionInfo pushSessionDisplayToLauncher(int sessionId) {
            PackageInstaller.SessionInfo session = PackageInstallerCompatVL.this.verify(PackageInstallerCompatVL.this.mInstaller.getSessionInfo(sessionId));
            if (session == null || session.getAppPackageName() == null) {
                return null;
            }
            PackageInstallerCompatVL.this.mActiveSessions.put(sessionId, session.getAppPackageName());
            PackageInstallerCompatVL.this.addSessionInfoToCache(session, Process.myUserHandle());
            LauncherAppState app = LauncherAppState.getInstanceNoCreate();
            if (app != null) {
                app.getModel().updateSessionDisplayInfo(session.getAppPackageName());
            }
            return session;
        }
    };
    final PackageInstaller mInstaller;
    private final HashMap<String, Boolean> mSessionVerifiedMap = new HashMap<>();
    private final Handler mWorker;

    PackageInstallerCompatVL(Context context) {
        this.mAppContext = context.getApplicationContext();
        this.mInstaller = context.getPackageManager().getPackageInstaller();
        this.mCache = LauncherAppState.getInstance(context).getIconCache();
        this.mWorker = new Handler(LauncherModel.getWorkerLooper());
        this.mInstaller.registerSessionCallback(this.mCallback, this.mWorker);
    }

    public HashMap<String, PackageInstaller.SessionInfo> updateAndGetActiveSessionCache() {
        HashMap<String, PackageInstaller.SessionInfo> activePackages = new HashMap<>();
        UserHandle user = Process.myUserHandle();
        for (PackageInstaller.SessionInfo info : getAllVerifiedSessions()) {
            addSessionInfoToCache(info, user);
            if (info.getAppPackageName() != null) {
                activePackages.put(info.getAppPackageName(), info);
                this.mActiveSessions.put(info.getSessionId(), info.getAppPackageName());
            }
        }
        return activePackages;
    }

    /* access modifiers changed from: package-private */
    public void addSessionInfoToCache(PackageInstaller.SessionInfo info, UserHandle user) {
        String packageName = info.getAppPackageName();
        if (packageName != null) {
            this.mCache.cachePackageInstallInfo(packageName, user, info.getAppIcon(), info.getAppLabel());
        }
    }

    public void onStop() {
        this.mInstaller.unregisterSessionCallback(this.mCallback);
    }

    /* access modifiers changed from: package-private */
    public void sendUpdate(PackageInstallerCompat.PackageInstallInfo info) {
        LauncherAppState app = LauncherAppState.getInstanceNoCreate();
        if (app != null) {
            app.getModel().setPackageState(info);
        }
    }

    /* access modifiers changed from: private */
    public PackageInstaller.SessionInfo verify(PackageInstaller.SessionInfo sessionInfo) {
        if (sessionInfo == null || sessionInfo.getInstallerPackageName() == null || TextUtils.isEmpty(sessionInfo.getAppPackageName())) {
            return null;
        }
        String pkg = sessionInfo.getInstallerPackageName();
        synchronized (this.mSessionVerifiedMap) {
            if (!this.mSessionVerifiedMap.containsKey(pkg)) {
                boolean hasSystemFlag = true;
                if (LauncherAppsCompat.getInstance(this.mAppContext).getApplicationInfo(pkg, 1, Process.myUserHandle()) == null) {
                    hasSystemFlag = false;
                }
                this.mSessionVerifiedMap.put(pkg, Boolean.valueOf(hasSystemFlag));
            }
        }
        if (this.mSessionVerifiedMap.get(pkg).booleanValue()) {
            return sessionInfo;
        }
        return null;
    }

    public List<PackageInstaller.SessionInfo> getAllVerifiedSessions() {
        List<PackageInstaller.SessionInfo> list = new ArrayList<>(this.mInstaller.getAllSessions());
        Iterator<PackageInstaller.SessionInfo> it = list.iterator();
        while (it.hasNext()) {
            if (verify(it.next()) == null) {
                it.remove();
            }
        }
        return list;
    }
}
