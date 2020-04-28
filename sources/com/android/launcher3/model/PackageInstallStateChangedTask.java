package com.android.launcher3.model;

import com.android.launcher3.compat.PackageInstallerCompat;

public class PackageInstallStateChangedTask extends BaseModelUpdateTask {
    private final PackageInstallerCompat.PackageInstallInfo mInstallInfo;

    public PackageInstallStateChangedTask(PackageInstallerCompat.PackageInstallInfo installInfo) {
        this.mInstallInfo = installInfo;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: com.android.launcher3.AppInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v13, resolved type: com.android.launcher3.PromiseAppInfo} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void execute(com.android.launcher3.LauncherAppState r10, com.android.launcher3.model.BgDataModel r11, com.android.launcher3.AllAppsList r12) {
        /*
            r9 = this;
            com.android.launcher3.compat.PackageInstallerCompat$PackageInstallInfo r0 = r9.mInstallInfo
            int r0 = r0.state
            r1 = 0
            if (r0 != 0) goto L_0x0035
            android.content.Context r0 = r10.getContext()     // Catch:{ NameNotFoundException -> 0x0033 }
            android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ NameNotFoundException -> 0x0033 }
            com.android.launcher3.compat.PackageInstallerCompat$PackageInstallInfo r2 = r9.mInstallInfo     // Catch:{ NameNotFoundException -> 0x0033 }
            java.lang.String r2 = r2.packageName     // Catch:{ NameNotFoundException -> 0x0033 }
            android.content.pm.ApplicationInfo r0 = r0.getApplicationInfo(r2, r1)     // Catch:{ NameNotFoundException -> 0x0033 }
            android.content.Context r1 = r10.getContext()     // Catch:{ NameNotFoundException -> 0x0033 }
            com.android.launcher3.util.InstantAppResolver r1 = com.android.launcher3.util.InstantAppResolver.newInstance(r1)     // Catch:{ NameNotFoundException -> 0x0033 }
            boolean r1 = r1.isInstantApp((android.content.pm.ApplicationInfo) r0)     // Catch:{ NameNotFoundException -> 0x0033 }
            if (r1 == 0) goto L_0x0032
            com.android.launcher3.LauncherModel r1 = r10.getModel()     // Catch:{ NameNotFoundException -> 0x0033 }
            java.lang.String r2 = r0.packageName     // Catch:{ NameNotFoundException -> 0x0033 }
            android.os.UserHandle r3 = android.os.Process.myUserHandle()     // Catch:{ NameNotFoundException -> 0x0033 }
            r1.onPackageAdded(r2, r3)     // Catch:{ NameNotFoundException -> 0x0033 }
        L_0x0032:
            goto L_0x0034
        L_0x0033:
            r0 = move-exception
        L_0x0034:
            return
        L_0x0035:
            monitor-enter(r12)
            r0 = 0
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x012a }
            r2.<init>()     // Catch:{ all -> 0x012a }
        L_0x003d:
            int r3 = r12.size()     // Catch:{ all -> 0x012a }
            r4 = 2
            if (r1 >= r3) goto L_0x0081
            com.android.launcher3.AppInfo r3 = r12.get(r1)     // Catch:{ all -> 0x012a }
            android.content.ComponentName r5 = r3.getTargetComponent()     // Catch:{ all -> 0x012a }
            if (r5 == 0) goto L_0x007e
            java.lang.String r6 = r5.getPackageName()     // Catch:{ all -> 0x012a }
            com.android.launcher3.compat.PackageInstallerCompat$PackageInstallInfo r7 = r9.mInstallInfo     // Catch:{ all -> 0x012a }
            java.lang.String r7 = r7.packageName     // Catch:{ all -> 0x012a }
            boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x012a }
            if (r6 == 0) goto L_0x007e
            boolean r6 = r3 instanceof com.android.launcher3.PromiseAppInfo     // Catch:{ all -> 0x012a }
            if (r6 == 0) goto L_0x007e
            r6 = r3
            com.android.launcher3.PromiseAppInfo r6 = (com.android.launcher3.PromiseAppInfo) r6     // Catch:{ all -> 0x012a }
            com.android.launcher3.compat.PackageInstallerCompat$PackageInstallInfo r7 = r9.mInstallInfo     // Catch:{ all -> 0x012a }
            int r7 = r7.state     // Catch:{ all -> 0x012a }
            r8 = 1
            if (r7 != r8) goto L_0x0072
            com.android.launcher3.compat.PackageInstallerCompat$PackageInstallInfo r4 = r9.mInstallInfo     // Catch:{ all -> 0x012a }
            int r4 = r4.progress     // Catch:{ all -> 0x012a }
            r6.level = r4     // Catch:{ all -> 0x012a }
            r0 = r6
            goto L_0x007e
        L_0x0072:
            com.android.launcher3.compat.PackageInstallerCompat$PackageInstallInfo r7 = r9.mInstallInfo     // Catch:{ all -> 0x012a }
            int r7 = r7.state     // Catch:{ all -> 0x012a }
            if (r7 != r4) goto L_0x007e
            r12.removePromiseApp(r3)     // Catch:{ all -> 0x012a }
            r2.add(r3)     // Catch:{ all -> 0x012a }
        L_0x007e:
            int r1 = r1 + 1
            goto L_0x003d
        L_0x0081:
            if (r0 == 0) goto L_0x008c
            r1 = r0
            com.android.launcher3.model.PackageInstallStateChangedTask$1 r3 = new com.android.launcher3.model.PackageInstallStateChangedTask$1     // Catch:{ all -> 0x012a }
            r3.<init>(r1)     // Catch:{ all -> 0x012a }
            r9.scheduleCallbackTask(r3)     // Catch:{ all -> 0x012a }
        L_0x008c:
            boolean r1 = r2.isEmpty()     // Catch:{ all -> 0x012a }
            if (r1 != 0) goto L_0x009a
            com.android.launcher3.model.PackageInstallStateChangedTask$2 r1 = new com.android.launcher3.model.PackageInstallStateChangedTask$2     // Catch:{ all -> 0x012a }
            r1.<init>(r2)     // Catch:{ all -> 0x012a }
            r9.scheduleCallbackTask(r1)     // Catch:{ all -> 0x012a }
        L_0x009a:
            monitor-exit(r12)     // Catch:{ all -> 0x012a }
            monitor-enter(r11)
            java.util.HashSet r0 = new java.util.HashSet     // Catch:{ all -> 0x0127 }
            r0.<init>()     // Catch:{ all -> 0x0127 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r1 = r11.itemsIdMap     // Catch:{ all -> 0x0127 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0127 }
        L_0x00a7:
            boolean r2 = r1.hasNext()     // Catch:{ all -> 0x0127 }
            if (r2 == 0) goto L_0x00eb
            java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x0127 }
            com.android.launcher3.ItemInfo r2 = (com.android.launcher3.ItemInfo) r2     // Catch:{ all -> 0x0127 }
            boolean r3 = r2 instanceof com.android.launcher3.ShortcutInfo     // Catch:{ all -> 0x0127 }
            if (r3 == 0) goto L_0x00ea
            r3 = r2
            com.android.launcher3.ShortcutInfo r3 = (com.android.launcher3.ShortcutInfo) r3     // Catch:{ all -> 0x0127 }
            android.content.ComponentName r5 = r3.getTargetComponent()     // Catch:{ all -> 0x0127 }
            boolean r6 = r3.hasPromiseIconUi()     // Catch:{ all -> 0x0127 }
            if (r6 == 0) goto L_0x00ea
            if (r5 == 0) goto L_0x00ea
            com.android.launcher3.compat.PackageInstallerCompat$PackageInstallInfo r6 = r9.mInstallInfo     // Catch:{ all -> 0x0127 }
            java.lang.String r6 = r6.packageName     // Catch:{ all -> 0x0127 }
            java.lang.String r7 = r5.getPackageName()     // Catch:{ all -> 0x0127 }
            boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x0127 }
            if (r6 == 0) goto L_0x00ea
            com.android.launcher3.compat.PackageInstallerCompat$PackageInstallInfo r6 = r9.mInstallInfo     // Catch:{ all -> 0x0127 }
            int r6 = r6.progress     // Catch:{ all -> 0x0127 }
            r3.setInstallProgress(r6)     // Catch:{ all -> 0x0127 }
            com.android.launcher3.compat.PackageInstallerCompat$PackageInstallInfo r6 = r9.mInstallInfo     // Catch:{ all -> 0x0127 }
            int r6 = r6.state     // Catch:{ all -> 0x0127 }
            if (r6 != r4) goto L_0x00e7
            int r6 = r3.status     // Catch:{ all -> 0x0127 }
            r6 = r6 & -5
            r3.status = r6     // Catch:{ all -> 0x0127 }
        L_0x00e7:
            r0.add(r3)     // Catch:{ all -> 0x0127 }
        L_0x00ea:
            goto L_0x00a7
        L_0x00eb:
            java.util.ArrayList<com.android.launcher3.LauncherAppWidgetInfo> r1 = r11.appWidgets     // Catch:{ all -> 0x0127 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0127 }
        L_0x00f1:
            boolean r2 = r1.hasNext()     // Catch:{ all -> 0x0127 }
            if (r2 == 0) goto L_0x0117
            java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x0127 }
            com.android.launcher3.LauncherAppWidgetInfo r2 = (com.android.launcher3.LauncherAppWidgetInfo) r2     // Catch:{ all -> 0x0127 }
            android.content.ComponentName r3 = r2.providerName     // Catch:{ all -> 0x0127 }
            java.lang.String r3 = r3.getPackageName()     // Catch:{ all -> 0x0127 }
            com.android.launcher3.compat.PackageInstallerCompat$PackageInstallInfo r4 = r9.mInstallInfo     // Catch:{ all -> 0x0127 }
            java.lang.String r4 = r4.packageName     // Catch:{ all -> 0x0127 }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x0127 }
            if (r3 == 0) goto L_0x0116
            com.android.launcher3.compat.PackageInstallerCompat$PackageInstallInfo r3 = r9.mInstallInfo     // Catch:{ all -> 0x0127 }
            int r3 = r3.progress     // Catch:{ all -> 0x0127 }
            r2.installProgress = r3     // Catch:{ all -> 0x0127 }
            r0.add(r2)     // Catch:{ all -> 0x0127 }
        L_0x0116:
            goto L_0x00f1
        L_0x0117:
            boolean r1 = r0.isEmpty()     // Catch:{ all -> 0x0127 }
            if (r1 != 0) goto L_0x0125
            com.android.launcher3.model.PackageInstallStateChangedTask$3 r1 = new com.android.launcher3.model.PackageInstallStateChangedTask$3     // Catch:{ all -> 0x0127 }
            r1.<init>(r0)     // Catch:{ all -> 0x0127 }
            r9.scheduleCallbackTask(r1)     // Catch:{ all -> 0x0127 }
        L_0x0125:
            monitor-exit(r11)     // Catch:{ all -> 0x0127 }
            return
        L_0x0127:
            r0 = move-exception
            monitor-exit(r11)     // Catch:{ all -> 0x0127 }
            throw r0
        L_0x012a:
            r0 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x012a }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.PackageInstallStateChangedTask.execute(com.android.launcher3.LauncherAppState, com.android.launcher3.model.BgDataModel, com.android.launcher3.AllAppsList):void");
    }
}
