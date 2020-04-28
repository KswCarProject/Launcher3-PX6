package com.android.launcher3.model;

import android.appwidget.AppWidgetProviderInfo;
import android.content.pm.LauncherActivityInfo;
import android.os.UserHandle;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.AppInfo;
import com.android.launcher3.IconCache;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;

public class LoaderTask implements Runnable {
    private static final String TAG = "LoaderTask";
    private final LauncherAppState mApp;
    private final AppWidgetManagerCompat mAppWidgetManager = AppWidgetManagerCompat.getInstance(this.mApp.getContext());
    private final AllAppsList mBgAllAppsList;
    private final BgDataModel mBgDataModel;
    private FirstScreenBroadcast mFirstScreenBroadcast;
    private final IconCache mIconCache = this.mApp.getIconCache();
    private final LauncherAppsCompat mLauncherApps = LauncherAppsCompat.getInstance(this.mApp.getContext());
    private final PackageInstallerCompat mPackageInstaller = PackageInstallerCompat.getInstance(this.mApp.getContext());
    private final LoaderResults mResults;
    private final DeepShortcutManager mShortcutManager = DeepShortcutManager.getInstance(this.mApp.getContext());
    private boolean mStopped;
    private final UserManagerCompat mUserManager = UserManagerCompat.getInstance(this.mApp.getContext());

    public LoaderTask(LauncherAppState app, AllAppsList bgAllAppsList, BgDataModel dataModel, LoaderResults results) {
        this.mApp = app;
        this.mBgAllAppsList = bgAllAppsList;
        this.mBgDataModel = dataModel;
        this.mResults = results;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:3:0x0007 A[LOOP:0: B:3:0x0007->B:6:0x0011, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void waitForIdle() {
        /*
            r3 = this;
            monitor-enter(r3)
            com.android.launcher3.model.LoaderResults r0 = r3.mResults     // Catch:{ all -> 0x0016 }
            com.android.launcher3.util.LooperIdleLock r0 = r0.newIdleLock(r3)     // Catch:{ all -> 0x0016 }
        L_0x0007:
            boolean r1 = r3.mStopped     // Catch:{ all -> 0x0016 }
            if (r1 != 0) goto L_0x0014
            r1 = 1000(0x3e8, double:4.94E-321)
            boolean r1 = r0.awaitLocked(r1)     // Catch:{ all -> 0x0016 }
            if (r1 == 0) goto L_0x0014
            goto L_0x0007
        L_0x0014:
            monitor-exit(r3)
            return
        L_0x0016:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.LoaderTask.waitForIdle():void");
    }

    private synchronized void verifyNotStopped() throws CancellationException {
        if (this.mStopped) {
            throw new CancellationException("Loader stopped");
        }
    }

    private void sendFirstScreenActiveInstallsBroadcast() {
        long firstScreen;
        ArrayList<ItemInfo> firstScreenItems = new ArrayList<>();
        ArrayList<ItemInfo> allItems = new ArrayList<>();
        synchronized (this.mBgDataModel) {
            allItems.addAll(this.mBgDataModel.workspaceItems);
            allItems.addAll(this.mBgDataModel.appWidgets);
        }
        if (this.mBgDataModel.workspaceScreens.isEmpty()) {
            firstScreen = -1;
        } else {
            firstScreen = this.mBgDataModel.workspaceScreens.get(0).longValue();
        }
        LoaderResults.filterCurrentWorkspaceItems(firstScreen, allItems, firstScreenItems, new ArrayList());
        this.mFirstScreenBroadcast.sendBroadcasts(this.mApp.getContext(), firstScreenItems);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 1.1: loading workspace");
        loadWorkspace();
        verifyNotStopped();
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 1.2: bind workspace workspace");
        r4.mResults.bindWorkspace();
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 1.3: send first screen broadcast");
        sendFirstScreenActiveInstallsBroadcast();
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 1 completed, wait for idle");
        waitForIdle();
        verifyNotStopped();
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 2.1: loading all apps");
        loadAllApps();
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 2.2: Binding all apps");
        verifyNotStopped();
        r4.mResults.bindAllApps();
        verifyNotStopped();
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 2.3: Update icon cache");
        updateIconCache();
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 2 completed, wait for idle");
        waitForIdle();
        verifyNotStopped();
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 3.1: loading deep shortcuts");
        loadDeepShortcuts();
        verifyNotStopped();
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 3.2: bind deep shortcuts");
        r4.mResults.bindDeepShortcuts();
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 3 completed, wait for idle");
        waitForIdle();
        verifyNotStopped();
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 4.1: loading widgets");
        r4.mBgDataModel.widgetsModel.update(r4.mApp, (com.android.launcher3.util.PackageUserKey) null);
        verifyNotStopped();
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "step 4.2: Binding widgets");
        r4.mResults.bindWidgets();
        r0.commit();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x00c3, code lost:
        if (r0 == null) goto L_0x00e6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x00c9, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00cd, code lost:
        if (r0 != null) goto L_0x00cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00cf, code lost:
        if (r1 != null) goto L_0x00d1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00d5, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00da, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00df, code lost:
        com.android.launcher3.util.TraceHelper.partitionSection(TAG, "Cancelled");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0008, code lost:
        com.android.launcher3.util.TraceHelper.beginSection(TAG);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:?, code lost:
        r0 = r4.mApp.getModel().beginLoader(r4);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = r4.mStopped     // Catch:{ all -> 0x00ec }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r4)     // Catch:{ all -> 0x00ec }
            return
        L_0x0007:
            monitor-exit(r4)     // Catch:{ all -> 0x00ec }
            java.lang.String r0 = "LoaderTask"
            com.android.launcher3.util.TraceHelper.beginSection(r0)
            com.android.launcher3.LauncherAppState r0 = r4.mApp     // Catch:{ CancellationException -> 0x00de }
            com.android.launcher3.LauncherModel r0 = r0.getModel()     // Catch:{ CancellationException -> 0x00de }
            com.android.launcher3.LauncherModel$LoaderTransaction r0 = r0.beginLoader(r4)     // Catch:{ CancellationException -> 0x00de }
            r1 = 0
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 1.1: loading workspace"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            r4.loadWorkspace()     // Catch:{ Throwable -> 0x00cb }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x00cb }
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 1.2: bind workspace workspace"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            com.android.launcher3.model.LoaderResults r2 = r4.mResults     // Catch:{ Throwable -> 0x00cb }
            r2.bindWorkspace()     // Catch:{ Throwable -> 0x00cb }
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 1.3: send first screen broadcast"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            r4.sendFirstScreenActiveInstallsBroadcast()     // Catch:{ Throwable -> 0x00cb }
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 1 completed, wait for idle"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            r4.waitForIdle()     // Catch:{ Throwable -> 0x00cb }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x00cb }
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 2.1: loading all apps"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            r4.loadAllApps()     // Catch:{ Throwable -> 0x00cb }
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 2.2: Binding all apps"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x00cb }
            com.android.launcher3.model.LoaderResults r2 = r4.mResults     // Catch:{ Throwable -> 0x00cb }
            r2.bindAllApps()     // Catch:{ Throwable -> 0x00cb }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x00cb }
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 2.3: Update icon cache"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            r4.updateIconCache()     // Catch:{ Throwable -> 0x00cb }
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 2 completed, wait for idle"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            r4.waitForIdle()     // Catch:{ Throwable -> 0x00cb }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x00cb }
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 3.1: loading deep shortcuts"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            r4.loadDeepShortcuts()     // Catch:{ Throwable -> 0x00cb }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x00cb }
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 3.2: bind deep shortcuts"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            com.android.launcher3.model.LoaderResults r2 = r4.mResults     // Catch:{ Throwable -> 0x00cb }
            r2.bindDeepShortcuts()     // Catch:{ Throwable -> 0x00cb }
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 3 completed, wait for idle"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            r4.waitForIdle()     // Catch:{ Throwable -> 0x00cb }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x00cb }
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 4.1: loading widgets"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            com.android.launcher3.model.BgDataModel r2 = r4.mBgDataModel     // Catch:{ Throwable -> 0x00cb }
            com.android.launcher3.model.WidgetsModel r2 = r2.widgetsModel     // Catch:{ Throwable -> 0x00cb }
            com.android.launcher3.LauncherAppState r3 = r4.mApp     // Catch:{ Throwable -> 0x00cb }
            r2.update(r3, r1)     // Catch:{ Throwable -> 0x00cb }
            r4.verifyNotStopped()     // Catch:{ Throwable -> 0x00cb }
            java.lang.String r2 = "LoaderTask"
            java.lang.String r3 = "step 4.2: Binding widgets"
            com.android.launcher3.util.TraceHelper.partitionSection(r2, r3)     // Catch:{ Throwable -> 0x00cb }
            com.android.launcher3.model.LoaderResults r2 = r4.mResults     // Catch:{ Throwable -> 0x00cb }
            r2.bindWidgets()     // Catch:{ Throwable -> 0x00cb }
            r0.commit()     // Catch:{ Throwable -> 0x00cb }
            if (r0 == 0) goto L_0x00c8
            r0.close()     // Catch:{ CancellationException -> 0x00de }
        L_0x00c8:
            goto L_0x00e6
        L_0x00c9:
            r2 = move-exception
            goto L_0x00cd
        L_0x00cb:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x00c9 }
        L_0x00cd:
            if (r0 == 0) goto L_0x00dd
            if (r1 == 0) goto L_0x00da
            r0.close()     // Catch:{ Throwable -> 0x00d5 }
            goto L_0x00dd
        L_0x00d5:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ CancellationException -> 0x00de }
            goto L_0x00dd
        L_0x00da:
            r0.close()     // Catch:{ CancellationException -> 0x00de }
        L_0x00dd:
            throw r2     // Catch:{ CancellationException -> 0x00de }
        L_0x00de:
            r0 = move-exception
            java.lang.String r1 = "LoaderTask"
            java.lang.String r2 = "Cancelled"
            com.android.launcher3.util.TraceHelper.partitionSection(r1, r2)
        L_0x00e6:
            java.lang.String r0 = "LoaderTask"
            com.android.launcher3.util.TraceHelper.endSection(r0)
            return
        L_0x00ec:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x00ec }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.LoaderTask.run():void");
    }

    public synchronized void stopLocked() {
        this.mStopped = true;
        notify();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v39, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v116, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v117, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v123, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v128, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v129, resolved type: android.util.LongSparseArray} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v131, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v133, resolved type: android.content.Context} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v135, resolved type: android.content.Context} */
    /* JADX WARNING: type inference failed for: r4v39 */
    /* JADX WARNING: type inference failed for: r4v57 */
    /* JADX WARNING: type inference failed for: r4v175 */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x04f6, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x04f7, code lost:
        r4 = r1;
        r76 = r21;
        r2 = r26;
        r7 = r29;
        r9 = r33;
        r74 = r46;
        r22 = r49;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x0507, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0508, code lost:
        r51 = r5;
        r5 = r21;
        r4 = r1;
        r60 = r8;
        r61 = r14;
        r65 = r15;
        r7 = r29;
        r71 = r33;
        r13 = r36;
        r74 = r46;
        r68 = r47;
        r63 = r48;
        r22 = r49;
        r59 = r51;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0526, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x0527, code lost:
        r51 = r5;
        r49 = r7;
        r5 = r21;
        r4 = r1;
        r60 = r8;
        r61 = r14;
        r65 = r15;
        r7 = r29;
        r71 = r33;
        r13 = r36;
        r74 = r46;
        r68 = r47;
        r63 = r48;
        r22 = r49;
        r59 = r51;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x0547, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x0548, code lost:
        r51 = r5;
        r49 = r7;
        r5 = r21;
        r4 = r1;
        r63 = r2;
        r60 = r8;
        r61 = r14;
        r65 = r15;
        r7 = r29;
        r71 = r33;
        r13 = r36;
        r74 = r46;
        r68 = r47;
        r22 = r49;
        r59 = r51;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0568, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0569, code lost:
        r49 = r7;
        r4 = r1;
        r76 = r21;
        r2 = r26;
        r7 = r29;
        r9 = r33;
        r74 = r46;
        r22 = r49;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x059c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x059d, code lost:
        r51 = r5;
        r45 = r7;
        r5 = r21;
        r63 = r2;
        r74 = r4;
        r60 = r8;
        r68 = r9;
        r61 = r14;
        r65 = r15;
        r7 = r29;
        r71 = r33;
        r13 = r36;
        r59 = r51;
        r4 = r1;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x05bb, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x05bc, code lost:
        r74 = r4;
        r76 = r21;
        r2 = r26;
        r7 = r29;
        r9 = r33;
        r4 = r1;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x0639, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x063a, code lost:
        r4 = r1;
        r76 = r5;
        r2 = r26;
        r7 = r29;
        r9 = r33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x0643, code lost:
        r74 = r46;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x068d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x068e, code lost:
        r59 = r4;
        r60 = r8;
        r61 = r14;
        r65 = r15;
        r7 = r29;
        r71 = r33;
        r13 = r36;
        r74 = r46;
        r68 = r47;
        r63 = r48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0797, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x0798, code lost:
        r59 = r4;
        r60 = r8;
        r61 = r14;
        r4 = r1;
        r65 = r15;
        r7 = r29;
        r71 = r33;
        r13 = r36;
        r74 = r46;
        r68 = r47;
        r63 = r48;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x0813, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x0814, code lost:
        r4 = r1;
        r65 = r15;
        r7 = r29;
        r71 = r33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x081b, code lost:
        r13 = r36;
        r74 = r46;
        r68 = r47;
        r63 = r48;
        r7 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x08a3, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x08a4, code lost:
        r4 = r1;
        r76 = r5;
        r9 = r8;
        r2 = r26;
        r7 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x08ae, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x08af, code lost:
        r4 = r1;
        r71 = r8;
        r65 = r15;
        r7 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x08d0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x08d1, code lost:
        r4 = r1;
        r63 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x08d4, code lost:
        r71 = r8;
        r65 = r15;
        r7 = r29;
        r74 = r46;
        r68 = r47;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x0915, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x0916, code lost:
        r4 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x0a40, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x0a41, code lost:
        r71 = r8;
        r7 = r29;
        r74 = r46;
        r68 = r47;
        r1 = r0;
        r4 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x0a4e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x0a4f, code lost:
        r7 = r29;
        r74 = r46;
        r1 = r0;
        r76 = r5;
        r9 = r8;
        r2 = r26;
        r4 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x0a5d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x0a5e, code lost:
        r71 = r8;
        r65 = r15;
        r7 = r29;
        r74 = r46;
        r68 = r47;
        r1 = r0;
        r4 = r78;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0b33, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0b34, code lost:
        r4 = r1;
        r71 = r8;
        r65 = r15;
        r7 = r29;
        r74 = r46;
        r68 = r47;
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0b42, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x0b43, code lost:
        r4 = r1;
        r7 = r29;
        r74 = r46;
        r1 = r0;
        r76 = r5;
        r9 = r8;
        r2 = r26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0b76, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x0b77, code lost:
        r4 = r1;
        r7 = r29;
        r74 = r46;
        r1 = r0;
        r76 = r5;
        r2 = r26;
        r9 = r33;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x03f8 A[Catch:{ Exception -> 0x04bf, all -> 0x04b9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x03fa A[Catch:{ Exception -> 0x04bf, all -> 0x04b9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x04f6 A[ExcHandler: all (r0v64 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:146:0x036d] */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0568 A[ExcHandler: all (r0v59 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:111:0x02a8] */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x05bb A[ExcHandler: all (r0v56 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:93:0x0264] */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0639 A[ExcHandler: all (th java.lang.Throwable), PHI: r5 r46 
      PHI: (r5v29 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>) = (r5v28 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v28 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v28 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v28 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v28 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v28 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v28 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v28 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v28 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v28 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v28 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v28 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v30 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v30 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v30 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>), (r5v30 'installingPkgs' java.util.HashMap<java.lang.String, android.content.pm.PackageInstaller$SessionInfo>) binds: [B:305:0x07c8, B:275:0x070f, B:282:0x0727, B:289:0x0745, B:278:0x071b, B:279:?, B:268:0x06fd, B:261:0x06ec, B:251:0x06d1, B:245:0x06bc, B:231:0x0675, B:232:?, B:215:0x05f9, B:216:?, B:218:0x0613, B:219:?] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r46v3 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>) = (r46v1 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v1 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v1 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v1 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v1 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v1 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v1 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v1 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v1 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v1 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v1 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v1 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v4 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v4 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v4 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>), (r46v4 'shortcutKeyToPinnedShortcuts' java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, com.android.launcher3.shortcuts.ShortcutInfoCompat>) binds: [B:305:0x07c8, B:275:0x070f, B:282:0x0727, B:289:0x0745, B:278:0x071b, B:279:?, B:268:0x06fd, B:261:0x06ec, B:251:0x06d1, B:245:0x06bc, B:231:0x0675, B:232:?, B:215:0x05f9, B:216:?, B:218:0x0613, B:219:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:215:0x05f9] */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x07c0 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x089c  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x089f A[SYNTHETIC, Splitter:B:339:0x089f] */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x08a3 A[ExcHandler: all (th java.lang.Throwable), PHI: r8 
      PHI: (r8v29 'pendingPackages' com.android.launcher3.util.MultiHashMap<android.os.UserHandle, java.lang.String>) = (r8v30 'pendingPackages' com.android.launcher3.util.MultiHashMap<android.os.UserHandle, java.lang.String>), (r8v30 'pendingPackages' com.android.launcher3.util.MultiHashMap<android.os.UserHandle, java.lang.String>), (r8v30 'pendingPackages' com.android.launcher3.util.MultiHashMap<android.os.UserHandle, java.lang.String>), (r8v30 'pendingPackages' com.android.launcher3.util.MultiHashMap<android.os.UserHandle, java.lang.String>), (r8v30 'pendingPackages' com.android.launcher3.util.MultiHashMap<android.os.UserHandle, java.lang.String>), (r8v30 'pendingPackages' com.android.launcher3.util.MultiHashMap<android.os.UserHandle, java.lang.String>), (r8v49 'pendingPackages' com.android.launcher3.util.MultiHashMap<android.os.UserHandle, java.lang.String>) binds: [B:375:0x091c, B:367:0x08fe, B:349:0x08c0, B:352:0x08c8, B:339:0x089f, B:340:?, B:323:0x0850] A[DONT_GENERATE, DONT_INLINE], Splitter:B:323:0x0850] */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x08be  */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x08f1  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x08fe A[SYNTHETIC, Splitter:B:367:0x08fe] */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x0918 A[SYNTHETIC, Splitter:B:372:0x0918] */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0a4e A[ExcHandler: all (r0v34 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:381:0x0928] */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x0acd A[SYNTHETIC, Splitter:B:462:0x0acd] */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0b42 A[ExcHandler: all (r0v26 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:333:0x0896] */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0b76 A[ExcHandler: all (r0v19 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:258:0x06e6] */
    /* JADX WARNING: Removed duplicated region for block: B:515:0x0c0a A[SYNTHETIC, Splitter:B:515:0x0c0a] */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0c19 A[SYNTHETIC, Splitter:B:522:0x0c19] */
    /* JADX WARNING: Removed duplicated region for block: B:624:0x0b26 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadWorkspace() {
        /*
            r78 = this;
            r1 = r78
            com.android.launcher3.LauncherAppState r2 = r1.mApp
            android.content.Context r2 = r2.getContext()
            android.content.ContentResolver r9 = r2.getContentResolver()
            com.android.launcher3.util.PackageManagerHelper r3 = new com.android.launcher3.util.PackageManagerHelper
            r3.<init>(r2)
            r10 = r3
            boolean r11 = r10.isSafeMode()
            boolean r12 = com.android.launcher3.Utilities.isBootCompleted()
            com.android.launcher3.util.MultiHashMap r3 = new com.android.launcher3.util.MultiHashMap
            r3.<init>()
            r13 = r3
            r14 = 0
            r3 = r14
            com.android.launcher3.provider.ImportDataTask.performImportIfPossible(r2)     // Catch:{ Exception -> 0x0026 }
            goto L_0x0029
        L_0x0026:
            r0 = move-exception
            r4 = r0
            r3 = 1
        L_0x0029:
            if (r3 != 0) goto L_0x0036
            boolean r4 = com.android.launcher3.model.GridSizeMigrationTask.ENABLED
            if (r4 == 0) goto L_0x0036
            boolean r4 = com.android.launcher3.model.GridSizeMigrationTask.migrateGridIfNeeded(r2)
            if (r4 != 0) goto L_0x0036
            r3 = 1
        L_0x0036:
            r15 = r3
            if (r15 == 0) goto L_0x0045
            java.lang.String r3 = "LoaderTask"
            java.lang.String r4 = "loadWorkspace: resetting launcher database"
            android.util.Log.d(r3, r4)
            java.lang.String r3 = "create_empty_db"
            com.android.launcher3.LauncherSettings.Settings.call(r9, r3)
        L_0x0045:
            java.lang.String r3 = "LoaderTask"
            java.lang.String r4 = "loadWorkspace: loading default favorites"
            android.util.Log.d(r3, r4)
            java.lang.String r3 = "load_default_favorites"
            com.android.launcher3.LauncherSettings.Settings.call(r9, r3)
            com.android.launcher3.model.BgDataModel r8 = r1.mBgDataModel
            monitor-enter(r8)
            com.android.launcher3.model.BgDataModel r3 = r1.mBgDataModel     // Catch:{ all -> 0x0df8 }
            r3.clear()     // Catch:{ all -> 0x0df8 }
            com.android.launcher3.compat.PackageInstallerCompat r3 = r1.mPackageInstaller     // Catch:{ all -> 0x0df8 }
            java.util.HashMap r3 = r3.updateAndGetActiveSessionCache()     // Catch:{ all -> 0x0df8 }
            r7 = r3
            com.android.launcher3.model.FirstScreenBroadcast r3 = new com.android.launcher3.model.FirstScreenBroadcast     // Catch:{ all -> 0x0df8 }
            r3.<init>(r7)     // Catch:{ all -> 0x0df8 }
            r1.mFirstScreenBroadcast = r3     // Catch:{ all -> 0x0df8 }
            com.android.launcher3.model.BgDataModel r3 = r1.mBgDataModel     // Catch:{ all -> 0x0df8 }
            java.util.ArrayList<java.lang.Long> r3 = r3.workspaceScreens     // Catch:{ all -> 0x0df8 }
            java.util.ArrayList r4 = com.android.launcher3.LauncherModel.loadWorkspaceScreensDb(r2)     // Catch:{ all -> 0x0df8 }
            r3.addAll(r4)     // Catch:{ all -> 0x0df8 }
            java.util.HashMap r3 = new java.util.HashMap     // Catch:{ all -> 0x0df8 }
            r3.<init>()     // Catch:{ all -> 0x0df8 }
            r6 = r3
            com.android.launcher3.model.LoaderCursor r5 = new com.android.launcher3.model.LoaderCursor     // Catch:{ all -> 0x0df8 }
            android.net.Uri r4 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI     // Catch:{ all -> 0x0df8 }
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r3 = r9
            r14 = r5
            r5 = r16
            r20 = r6
            r6 = r17
            r21 = r7
            r7 = r18
            r16 = r8
            r8 = r19
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x0df4 }
            com.android.launcher3.LauncherAppState r4 = r1.mApp     // Catch:{ all -> 0x0df4 }
            r14.<init>(r3, r4)     // Catch:{ all -> 0x0df4 }
            r3 = r14
            r4 = 0
            r5 = r4
            java.lang.String r6 = "appWidgetId"
            int r6 = r3.getColumnIndexOrThrow(r6)     // Catch:{ all -> 0x0de0 }
            java.lang.String r7 = "appWidgetProvider"
            int r7 = r3.getColumnIndexOrThrow(r7)     // Catch:{ all -> 0x0de0 }
            java.lang.String r8 = "spanX"
            int r8 = r3.getColumnIndexOrThrow(r8)     // Catch:{ all -> 0x0de0 }
            java.lang.String r14 = "spanY"
            int r14 = r3.getColumnIndexOrThrow(r14)     // Catch:{ all -> 0x0de0 }
            java.lang.String r4 = "rank"
            int r4 = r3.getColumnIndexOrThrow(r4)     // Catch:{ all -> 0x0de0 }
            r22 = r5
            java.lang.String r5 = "options"
            int r5 = r3.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0dd1 }
            r23 = r15
            android.util.LongSparseArray<android.os.UserHandle> r15 = r3.allUsers     // Catch:{ all -> 0x0dc4 }
            android.util.LongSparseArray r17 = new android.util.LongSparseArray     // Catch:{ all -> 0x0dc4 }
            r17.<init>()     // Catch:{ all -> 0x0dc4 }
            r24 = r17
            android.util.LongSparseArray r17 = new android.util.LongSparseArray     // Catch:{ all -> 0x0dc4 }
            r17.<init>()     // Catch:{ all -> 0x0dc4 }
            r25 = r17
            r26 = r9
            com.android.launcher3.compat.UserManagerCompat r9 = r1.mUserManager     // Catch:{ all -> 0x0db6 }
            java.util.List r9 = r9.getUserProfiles()     // Catch:{ all -> 0x0db6 }
            java.util.Iterator r9 = r9.iterator()     // Catch:{ all -> 0x0db6 }
        L_0x00e4:
            boolean r17 = r9.hasNext()     // Catch:{ all -> 0x0db6 }
            if (r17 == 0) goto L_0x01c8
            java.lang.Object r17 = r9.next()     // Catch:{ all -> 0x01b9 }
            android.os.UserHandle r17 = (android.os.UserHandle) r17     // Catch:{ all -> 0x01b9 }
            r27 = r17
            r28 = r9
            com.android.launcher3.compat.UserManagerCompat r9 = r1.mUserManager     // Catch:{ all -> 0x01b9 }
            r29 = r2
            r2 = r27
            long r17 = r9.getSerialNumberForUser(r2)     // Catch:{ all -> 0x01a9 }
            r30 = r17
            r32 = r12
            r33 = r13
            r12 = r30
            r15.put(r12, r2)     // Catch:{ all -> 0x019a }
            com.android.launcher3.compat.UserManagerCompat r9 = r1.mUserManager     // Catch:{ all -> 0x019a }
            boolean r9 = r9.isQuietModeEnabled(r2)     // Catch:{ all -> 0x019a }
            java.lang.Boolean r9 = java.lang.Boolean.valueOf(r9)     // Catch:{ all -> 0x019a }
            r34 = r15
            r15 = r24
            r15.put(r12, r9)     // Catch:{ all -> 0x019a }
            com.android.launcher3.compat.UserManagerCompat r9 = r1.mUserManager     // Catch:{ all -> 0x019a }
            boolean r9 = r9.isUserUnlocked(r2)     // Catch:{ all -> 0x019a }
            if (r9 == 0) goto L_0x0174
            r35 = r9
            com.android.launcher3.shortcuts.DeepShortcutManager r9 = r1.mShortcutManager     // Catch:{ all -> 0x019a }
            r36 = r4
            r4 = 0
            java.util.List r9 = r9.queryForPinnedShortcuts(r4, r2)     // Catch:{ all -> 0x019a }
            r4 = r9
            com.android.launcher3.shortcuts.DeepShortcutManager r9 = r1.mShortcutManager     // Catch:{ all -> 0x019a }
            boolean r9 = r9.wasLastCallSuccess()     // Catch:{ all -> 0x019a }
            if (r9 == 0) goto L_0x016a
            java.util.Iterator r9 = r4.iterator()     // Catch:{ all -> 0x019a }
        L_0x013a:
            boolean r17 = r9.hasNext()     // Catch:{ all -> 0x019a }
            if (r17 == 0) goto L_0x0163
            java.lang.Object r17 = r9.next()     // Catch:{ all -> 0x019a }
            com.android.launcher3.shortcuts.ShortcutInfoCompat r17 = (com.android.launcher3.shortcuts.ShortcutInfoCompat) r17     // Catch:{ all -> 0x019a }
            r37 = r17
            r38 = r2
            com.android.launcher3.shortcuts.ShortcutKey r2 = com.android.launcher3.shortcuts.ShortcutKey.fromInfo(r37)     // Catch:{ all -> 0x019a }
            r39 = r4
            r40 = r9
            r4 = r20
            r9 = r37
            r4.put(r2, r9)     // Catch:{ all -> 0x020f }
            r20 = r4
            r2 = r38
            r4 = r39
            r9 = r40
            goto L_0x013a
        L_0x0163:
            r38 = r2
            r39 = r4
            r4 = r20
            goto L_0x017c
        L_0x016a:
            r38 = r2
            r39 = r4
            r4 = r20
            r9 = 0
            r35 = r9
            goto L_0x017c
        L_0x0174:
            r38 = r2
            r36 = r4
            r35 = r9
            r4 = r20
        L_0x017c:
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r35)     // Catch:{ all -> 0x020f }
            r9 = r25
            r9.put(r12, r2)     // Catch:{ all -> 0x020f }
            r20 = r4
            r25 = r9
            r24 = r15
            r9 = r28
            r2 = r29
            r12 = r32
            r13 = r33
            r15 = r34
            r4 = r36
            goto L_0x00e4
        L_0x019a:
            r0 = move-exception
            r4 = r1
            r74 = r20
            r76 = r21
            r2 = r26
            r7 = r29
            r9 = r33
            r1 = r0
            goto L_0x0df0
        L_0x01a9:
            r0 = move-exception
            r32 = r12
            r4 = r1
            r9 = r13
            r74 = r20
            r76 = r21
            r2 = r26
            r7 = r29
            r1 = r0
            goto L_0x0df0
        L_0x01b9:
            r0 = move-exception
            r32 = r12
            r4 = r1
            r7 = r2
            r9 = r13
            r74 = r20
            r76 = r21
            r2 = r26
            r1 = r0
            goto L_0x0df0
        L_0x01c8:
            r29 = r2
            r36 = r4
            r32 = r12
            r33 = r13
            r34 = r15
            r4 = r20
            r15 = r24
            r9 = r25
            com.android.launcher3.folder.FolderIconPreviewVerifier r2 = new com.android.launcher3.folder.FolderIconPreviewVerifier     // Catch:{ all -> 0x0da8 }
            com.android.launcher3.LauncherAppState r12 = r1.mApp     // Catch:{ all -> 0x0da8 }
            com.android.launcher3.InvariantDeviceProfile r12 = r12.getInvariantDeviceProfile()     // Catch:{ all -> 0x0da8 }
            r2.<init>(r12)     // Catch:{ all -> 0x0da8 }
        L_0x01e3:
            boolean r12 = r1.mStopped     // Catch:{ all -> 0x0da8 }
            if (r12 != 0) goto L_0x0bf9
            boolean r12 = r3.moveToNext()     // Catch:{ all -> 0x0bea }
            if (r12 == 0) goto L_0x0bf9
            android.os.UserHandle r12 = r3.user     // Catch:{ Exception -> 0x0bbb }
            if (r12 != 0) goto L_0x023d
            java.lang.String r12 = "User has been deleted"
            r3.markDeleted(r12)     // Catch:{ Exception -> 0x021e }
            r63 = r2
            r59 = r5
            r44 = r6
            r45 = r7
            r60 = r8
            r7 = r9
            r61 = r14
            r65 = r15
            r5 = r21
            r8 = r33
            r13 = r36
            r14 = r4
            goto L_0x0955
        L_0x020f:
            r0 = move-exception
            r74 = r4
            r76 = r21
            r2 = r26
            r7 = r29
            r9 = r33
            r4 = r1
        L_0x021b:
            r1 = r0
            goto L_0x0df0
        L_0x021e:
            r0 = move-exception
            r63 = r2
            r74 = r4
            r59 = r5
            r44 = r6
            r45 = r7
        L_0x0229:
            r60 = r8
            r68 = r9
            r61 = r14
            r65 = r15
            r5 = r21
            r7 = r29
            r71 = r33
            r13 = r36
        L_0x0239:
            r4 = r1
        L_0x023a:
            r1 = r0
            goto L_0x0bd8
        L_0x023d:
            r12 = 0
            int r13 = r3.itemType     // Catch:{ Exception -> 0x0bbb }
            r17 = 1120403456(0x42c80000, float:100.0)
            r41 = r12
            switch(r13) {
                case 0: goto L_0x0660;
                case 1: goto L_0x0660;
                case 2: goto L_0x05eb;
                case 3: goto L_0x0247;
                case 4: goto L_0x0264;
                case 5: goto L_0x0264;
                case 6: goto L_0x0660;
                default: goto L_0x0247;
            }
        L_0x0247:
            r63 = r2
            r74 = r4
            r59 = r5
            r44 = r6
            r45 = r7
            r60 = r8
            r68 = r9
            r61 = r14
            r65 = r15
            r5 = r21
            r7 = r29
            r71 = r33
            r13 = r36
            r4 = r1
            goto L_0x0b9d
        L_0x0264:
            int r13 = r3.itemType     // Catch:{ Exception -> 0x05ca, all -> 0x05bb }
            r12 = 5
            if (r13 != r12) goto L_0x026b
            r12 = 1
            goto L_0x026c
        L_0x026b:
            r12 = 0
        L_0x026c:
            int r13 = r3.getInt(r6)     // Catch:{ Exception -> 0x05ca, all -> 0x05bb }
            java.lang.String r18 = r3.getString(r7)     // Catch:{ Exception -> 0x05ca, all -> 0x05bb }
            r42 = r18
            android.content.ComponentName r18 = android.content.ComponentName.unflattenFromString(r42)     // Catch:{ Exception -> 0x05ca, all -> 0x05bb }
            r43 = r18
            r44 = r6
            r6 = 1
            boolean r18 = r3.hasRestoreFlag(r6)     // Catch:{ Exception -> 0x059c, all -> 0x05bb }
            r18 = r18 ^ 1
            r6 = 2
            boolean r6 = r3.hasRestoreFlag(r6)     // Catch:{ Exception -> 0x059c, all -> 0x05bb }
            r19 = 1
            r6 = r6 ^ 1
            if (r22 != 0) goto L_0x02a2
            r45 = r7
            com.android.launcher3.compat.AppWidgetManagerCompat r7 = r1.mAppWidgetManager     // Catch:{ Exception -> 0x029a }
            java.util.HashMap r7 = r7.getAllProvidersMap()     // Catch:{ Exception -> 0x029a }
            goto L_0x02a6
        L_0x029a:
            r0 = move-exception
            r63 = r2
            r74 = r4
            r59 = r5
            goto L_0x0229
        L_0x02a2:
            r45 = r7
            r7 = r22
        L_0x02a6:
            r46 = r4
            com.android.launcher3.util.ComponentKey r4 = new com.android.launcher3.util.ComponentKey     // Catch:{ Exception -> 0x057b, all -> 0x0568 }
            r47 = r9
            android.content.ComponentName r9 = android.content.ComponentName.unflattenFromString(r42)     // Catch:{ Exception -> 0x0547, all -> 0x0568 }
            r48 = r2
            android.os.UserHandle r2 = r3.user     // Catch:{ Exception -> 0x0526, all -> 0x0568 }
            r4.<init>(r9, r2)     // Catch:{ Exception -> 0x0526, all -> 0x0568 }
            java.lang.Object r2 = r7.get(r4)     // Catch:{ Exception -> 0x0526, all -> 0x0568 }
            android.appwidget.AppWidgetProviderInfo r2 = (android.appwidget.AppWidgetProviderInfo) r2     // Catch:{ Exception -> 0x0526, all -> 0x0568 }
            boolean r4 = isValidProvider(r2)     // Catch:{ Exception -> 0x0526, all -> 0x0568 }
            if (r11 != 0) goto L_0x0317
            if (r12 != 0) goto L_0x0317
            if (r6 == 0) goto L_0x0317
            if (r4 != 0) goto L_0x0317
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02f8, all -> 0x02e5 }
            r9.<init>()     // Catch:{ Exception -> 0x02f8, all -> 0x02e5 }
            r49 = r7
            java.lang.String r7 = "Deleting widget that isn't installed anymore: "
            r9.append(r7)     // Catch:{ Exception -> 0x034f, all -> 0x033f }
            r9.append(r2)     // Catch:{ Exception -> 0x034f, all -> 0x033f }
            java.lang.String r7 = r9.toString()     // Catch:{ Exception -> 0x034f, all -> 0x033f }
            r3.markDeleted(r7)     // Catch:{ Exception -> 0x034f, all -> 0x033f }
            r51 = r5
            r5 = r21
            goto L_0x04a0
        L_0x02e5:
            r0 = move-exception
            r49 = r7
            r4 = r1
            r76 = r21
            r2 = r26
            r7 = r29
            r9 = r33
            r74 = r46
            r22 = r49
            r1 = r0
            goto L_0x0df0
        L_0x02f8:
            r0 = move-exception
            r49 = r7
            r4 = r1
            r59 = r5
            r60 = r8
            r61 = r14
            r65 = r15
            r5 = r21
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r22 = r49
            r1 = r0
            goto L_0x0bd8
        L_0x0317:
            r49 = r7
            if (r4 == 0) goto L_0x036b
            com.android.launcher3.LauncherAppWidgetInfo r7 = new com.android.launcher3.LauncherAppWidgetInfo     // Catch:{ Exception -> 0x034f, all -> 0x033f }
            android.content.ComponentName r9 = r2.provider     // Catch:{ Exception -> 0x034f, all -> 0x033f }
            r7.<init>(r13, r9)     // Catch:{ Exception -> 0x034f, all -> 0x033f }
            int r9 = r3.restoreFlag     // Catch:{ Exception -> 0x034f, all -> 0x033f }
            r9 = r9 & -9
            r9 = r9 & -3
            if (r6 != 0) goto L_0x032e
            if (r18 == 0) goto L_0x032e
            r9 = r9 | 4
        L_0x032e:
            r7.restoreStatus = r9     // Catch:{ Exception -> 0x034f, all -> 0x033f }
            r50 = r2
            r52 = r4
            r51 = r5
            r54 = r6
            r5 = r21
            r4 = r43
            goto L_0x0400
        L_0x033f:
            r0 = move-exception
            r4 = r1
            r76 = r21
        L_0x0343:
            r2 = r26
            r7 = r29
            r9 = r33
            r74 = r46
            r22 = r49
            goto L_0x021b
        L_0x034f:
            r0 = move-exception
            r4 = r1
            r59 = r5
            r60 = r8
            r61 = r14
            r65 = r15
            r5 = r21
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r22 = r49
            goto L_0x023a
        L_0x036b:
            java.lang.String r7 = "LoaderTask"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0507, all -> 0x04f6 }
            r9.<init>()     // Catch:{ Exception -> 0x0507, all -> 0x04f6 }
            r50 = r2
            java.lang.String r2 = "Widget restore pending id="
            r9.append(r2)     // Catch:{ Exception -> 0x0507, all -> 0x04f6 }
            r52 = r4
            r51 = r5
            long r4 = r3.id     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            r9.append(r4)     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            java.lang.String r2 = " appWidgetId="
            r9.append(r2)     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            r9.append(r13)     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            java.lang.String r2 = " status ="
            r9.append(r2)     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            int r2 = r3.restoreFlag     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            r9.append(r2)     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            java.lang.String r2 = r9.toString()     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            android.util.Log.v(r7, r2)     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            com.android.launcher3.LauncherAppWidgetInfo r2 = new com.android.launcher3.LauncherAppWidgetInfo     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            r4 = r43
            r2.<init>(r13, r4)     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            r7 = r2
            int r2 = r3.restoreFlag     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            r7.restoreStatus = r2     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            java.lang.String r2 = r4.getPackageName()     // Catch:{ Exception -> 0x04d9, all -> 0x04f6 }
            r5 = r21
            java.lang.Object r2 = r5.get(r2)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            android.content.pm.PackageInstaller$SessionInfo r2 = (android.content.pm.PackageInstaller.SessionInfo) r2     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            if (r2 != 0) goto L_0x03b8
            r9 = 0
            goto L_0x03c3
        L_0x03b8:
            float r9 = r2.getProgress()     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            float r9 = r9 * r17
            int r9 = (int) r9     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
        L_0x03c3:
            r53 = r2
            r2 = 8
            boolean r17 = r3.hasRestoreFlag(r2)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            if (r17 == 0) goto L_0x03d0
        L_0x03cd:
            r54 = r6
            goto L_0x03f6
        L_0x03d0:
            if (r9 == 0) goto L_0x03db
            int r2 = r7.restoreStatus     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r17 = 8
            r2 = r2 | 8
            r7.restoreStatus = r2     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            goto L_0x03cd
        L_0x03db:
            if (r11 != 0) goto L_0x03f4
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r2.<init>()     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r54 = r6
            java.lang.String r6 = "Unrestored widget removed: "
            r2.append(r6)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r2.append(r4)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r3.markDeleted(r2)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            goto L_0x042d
        L_0x03f4:
            r54 = r6
        L_0x03f6:
            if (r9 != 0) goto L_0x03fa
            r2 = 0
            goto L_0x03fe
        L_0x03fa:
            int r2 = r9.intValue()     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
        L_0x03fe:
            r7.installProgress = r2     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
        L_0x0400:
            r2 = 32
            boolean r2 = r7.hasRestoreFlag(r2)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            if (r2 == 0) goto L_0x040e
            android.content.Intent r2 = r3.parseIntent()     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r7.bindOptions = r2     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
        L_0x040e:
            r3.applyCommonProperties(r7)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            int r2 = r3.getInt(r8)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r7.spanX = r2     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            int r2 = r3.getInt(r14)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r7.spanY = r2     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            android.os.UserHandle r2 = r3.user     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r7.user = r2     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            boolean r2 = r3.isOnWorkspaceOrHotseat()     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            if (r2 != 0) goto L_0x043f
            java.lang.String r2 = "Widget found where container != CONTAINER_DESKTOP nor CONTAINER_HOTSEAT - ignoring!"
            r3.markDeleted(r2)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
        L_0x042d:
            r21 = r5
            r6 = r44
            r7 = r45
            r4 = r46
            r9 = r47
            r2 = r48
            r22 = r49
            r5 = r51
            goto L_0x01e3
        L_0x043f:
            if (r12 != 0) goto L_0x0476
            android.content.ComponentName r2 = r7.providerName     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            java.lang.String r2 = r2.flattenToString()     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r6 = r42
            boolean r9 = r2.equals(r6)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            if (r9 == 0) goto L_0x0458
            int r9 = r7.restoreStatus     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r55 = r4
            int r4 = r3.restoreFlag     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            if (r9 == r4) goto L_0x047a
            goto L_0x045a
        L_0x0458:
            r55 = r4
        L_0x045a:
            com.android.launcher3.util.ContentWriter r4 = r3.updater()     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            java.lang.String r9 = "appWidgetProvider"
            com.android.launcher3.util.ContentWriter r4 = r4.put((java.lang.String) r9, (java.lang.String) r2)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            java.lang.String r9 = "restored"
            r56 = r2
            int r2 = r7.restoreStatus     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            com.android.launcher3.util.ContentWriter r2 = r4.put((java.lang.String) r9, (java.lang.Integer) r2)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r2.commit()     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            goto L_0x047a
        L_0x0476:
            r55 = r4
            r6 = r42
        L_0x047a:
            int r2 = r7.restoreStatus     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            if (r2 == 0) goto L_0x049b
            android.content.ComponentName r2 = r7.providerName     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            java.lang.String r2 = r2.getPackageName()     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            com.android.launcher3.model.PackageItemInfo r4 = new com.android.launcher3.model.PackageItemInfo     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r4.<init>(r2)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r7.pendingItemInfo = r4     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            com.android.launcher3.model.PackageItemInfo r4 = r7.pendingItemInfo     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            android.os.UserHandle r9 = r7.user     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r4.user = r9     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            com.android.launcher3.IconCache r4 = r1.mIconCache     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            com.android.launcher3.model.PackageItemInfo r9 = r7.pendingItemInfo     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r57 = r2
            r2 = 0
            r4.getTitleAndIconForApp(r9, r2)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
        L_0x049b:
            com.android.launcher3.model.BgDataModel r2 = r1.mBgDataModel     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
            r3.checkAndAddItem(r7, r2)     // Catch:{ Exception -> 0x04bf, all -> 0x04b9 }
        L_0x04a0:
            r4 = r1
            r60 = r8
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r22 = r49
            r59 = r51
            goto L_0x0b9d
        L_0x04b9:
            r0 = move-exception
            r4 = r1
            r76 = r5
            goto L_0x0343
        L_0x04bf:
            r0 = move-exception
            r4 = r1
            r60 = r8
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r22 = r49
            r59 = r51
            goto L_0x023a
        L_0x04d9:
            r0 = move-exception
            r5 = r21
            r4 = r1
            r60 = r8
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r22 = r49
            r59 = r51
            r1 = r0
            goto L_0x0bd8
        L_0x04f6:
            r0 = move-exception
            r4 = r1
            r76 = r21
            r2 = r26
            r7 = r29
            r9 = r33
            r74 = r46
            r22 = r49
            r1 = r0
            goto L_0x0df0
        L_0x0507:
            r0 = move-exception
            r51 = r5
            r5 = r21
            r4 = r1
            r60 = r8
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r22 = r49
            r59 = r51
            r1 = r0
            goto L_0x0bd8
        L_0x0526:
            r0 = move-exception
            r51 = r5
            r49 = r7
            r5 = r21
            r4 = r1
            r60 = r8
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r22 = r49
            r59 = r51
            r1 = r0
            goto L_0x0bd8
        L_0x0547:
            r0 = move-exception
            r51 = r5
            r49 = r7
            r5 = r21
            r4 = r1
            r63 = r2
            r60 = r8
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r22 = r49
            r59 = r51
            r1 = r0
            goto L_0x0bd8
        L_0x0568:
            r0 = move-exception
            r49 = r7
            r4 = r1
            r76 = r21
            r2 = r26
            r7 = r29
            r9 = r33
            r74 = r46
            r22 = r49
            r1 = r0
            goto L_0x0df0
        L_0x057b:
            r0 = move-exception
            r51 = r5
            r49 = r7
            r5 = r21
            r4 = r1
            r63 = r2
            r60 = r8
            r68 = r9
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r22 = r49
            r59 = r51
            r1 = r0
            goto L_0x0bd8
        L_0x059c:
            r0 = move-exception
            r51 = r5
            r45 = r7
            r5 = r21
            r63 = r2
            r74 = r4
            r60 = r8
            r68 = r9
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r59 = r51
            r4 = r1
            r1 = r0
            goto L_0x0bd8
        L_0x05bb:
            r0 = move-exception
            r74 = r4
            r76 = r21
            r2 = r26
            r7 = r29
            r9 = r33
            r4 = r1
            r1 = r0
            goto L_0x0df0
        L_0x05ca:
            r0 = move-exception
            r51 = r5
            r44 = r6
            r45 = r7
            r5 = r21
            r63 = r2
            r74 = r4
            r60 = r8
            r68 = r9
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r59 = r51
            r4 = r1
            r1 = r0
            goto L_0x0bd8
        L_0x05eb:
            r48 = r2
            r46 = r4
            r51 = r5
            r44 = r6
            r45 = r7
            r47 = r9
            r5 = r21
            com.android.launcher3.model.BgDataModel r2 = r1.mBgDataModel     // Catch:{ Exception -> 0x0647, all -> 0x0639 }
            long r6 = r3.id     // Catch:{ Exception -> 0x0647, all -> 0x0639 }
            com.android.launcher3.FolderInfo r2 = r2.findOrMakeFolder(r6)     // Catch:{ Exception -> 0x0647, all -> 0x0639 }
            r3.applyCommonProperties(r2)     // Catch:{ Exception -> 0x0647, all -> 0x0639 }
            int r4 = r3.titleIndex     // Catch:{ Exception -> 0x0647, all -> 0x0639 }
            java.lang.String r4 = r3.getString(r4)     // Catch:{ Exception -> 0x0647, all -> 0x0639 }
            r2.title = r4     // Catch:{ Exception -> 0x0647, all -> 0x0639 }
            r4 = 1
            r2.spanX = r4     // Catch:{ Exception -> 0x0647, all -> 0x0639 }
            r2.spanY = r4     // Catch:{ Exception -> 0x0647, all -> 0x0639 }
            r4 = r51
            int r6 = r3.getInt(r4)     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            r2.options = r6     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            r3.markRestored()     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            com.android.launcher3.model.BgDataModel r6 = r1.mBgDataModel     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            r3.checkAndAddItem(r2, r6)     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            r59 = r4
            r60 = r8
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r4 = r1
            goto L_0x0b9d
        L_0x0639:
            r0 = move-exception
            r4 = r1
            r76 = r5
            r2 = r26
            r7 = r29
            r9 = r33
        L_0x0643:
            r74 = r46
            goto L_0x021b
        L_0x0647:
            r0 = move-exception
            r4 = r1
            r60 = r8
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r59 = r51
            r1 = r0
            goto L_0x0bd8
        L_0x0660:
            r48 = r2
            r46 = r4
            r4 = r5
            r44 = r6
            r45 = r7
            r47 = r9
            r5 = r21
            android.content.Intent r2 = r3.parseIntent()     // Catch:{ Exception -> 0x0b85, all -> 0x0b76 }
            if (r2 != 0) goto L_0x06a4
            java.lang.String r6 = "Invalid or null intent"
            r3.markDeleted(r6)     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
        L_0x0679:
            r59 = r4
            r60 = r8
            r61 = r14
        L_0x067f:
            r65 = r15
            r8 = r33
        L_0x0683:
            r13 = r36
            r14 = r46
            r7 = r47
            r63 = r48
            goto L_0x0955
        L_0x068d:
            r0 = move-exception
            r59 = r4
            r60 = r8
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            goto L_0x0239
        L_0x06a4:
            long r6 = r3.serialNumber     // Catch:{ Exception -> 0x0b85, all -> 0x0b76 }
            java.lang.Object r6 = r15.get(r6)     // Catch:{ Exception -> 0x0b85, all -> 0x0b76 }
            java.lang.Boolean r6 = (java.lang.Boolean) r6     // Catch:{ Exception -> 0x0b85, all -> 0x0b76 }
            boolean r6 = r6.booleanValue()     // Catch:{ Exception -> 0x0b85, all -> 0x0b76 }
            if (r6 == 0) goto L_0x06b5
            r6 = 8
            goto L_0x06b6
        L_0x06b5:
            r6 = 0
        L_0x06b6:
            android.content.ComponentName r7 = r2.getComponent()     // Catch:{ Exception -> 0x0b85, all -> 0x0b76 }
            if (r7 != 0) goto L_0x06c1
            java.lang.String r9 = r2.getPackage()     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            goto L_0x06c5
        L_0x06c1:
            java.lang.String r9 = r7.getPackageName()     // Catch:{ Exception -> 0x0b85, all -> 0x0b76 }
        L_0x06c5:
            android.os.UserHandle r12 = android.os.Process.myUserHandle()     // Catch:{ Exception -> 0x0b85, all -> 0x0b76 }
            android.os.UserHandle r13 = r3.user     // Catch:{ Exception -> 0x0b85, all -> 0x0b76 }
            boolean r12 = r12.equals(r13)     // Catch:{ Exception -> 0x0b85, all -> 0x0b76 }
            if (r12 != 0) goto L_0x06e6
            int r12 = r3.itemType     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            r13 = 1
            if (r12 != r13) goto L_0x06dc
            java.lang.String r12 = "Legacy shortcuts are only allowed for default user"
            r3.markDeleted(r12)     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            goto L_0x0679
        L_0x06dc:
            int r12 = r3.restoreFlag     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            if (r12 == 0) goto L_0x06e6
            java.lang.String r12 = "Restore from managed profile not supported"
            r3.markDeleted(r12)     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            goto L_0x0679
        L_0x06e6:
            boolean r12 = android.text.TextUtils.isEmpty(r9)     // Catch:{ Exception -> 0x0b85, all -> 0x0b76 }
            if (r12 == 0) goto L_0x06f7
            int r12 = r3.itemType     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            r13 = 1
            if (r12 == r13) goto L_0x06f7
            java.lang.String r12 = "Only legacy shortcuts can have null package"
            r3.markDeleted(r12)     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            goto L_0x0679
        L_0x06f7:
            boolean r12 = android.text.TextUtils.isEmpty(r9)     // Catch:{ Exception -> 0x0b85, all -> 0x0b76 }
            if (r12 != 0) goto L_0x070a
            com.android.launcher3.compat.LauncherAppsCompat r12 = r1.mLauncherApps     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            android.os.UserHandle r13 = r3.user     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            boolean r12 = r12.isPackageEnabledForProfile(r9, r13)     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            if (r12 == 0) goto L_0x0708
            goto L_0x070a
        L_0x0708:
            r12 = 0
            goto L_0x070b
        L_0x070a:
            r12 = 1
        L_0x070b:
            if (r7 == 0) goto L_0x07b0
            if (r12 == 0) goto L_0x07b0
            com.android.launcher3.compat.LauncherAppsCompat r13 = r1.mLauncherApps     // Catch:{ Exception -> 0x0797, all -> 0x0639 }
            r58 = r2
            android.os.UserHandle r2 = r3.user     // Catch:{ Exception -> 0x0797, all -> 0x0639 }
            boolean r2 = r13.isActivityEnabledForProfile(r7, r2)     // Catch:{ Exception -> 0x0797, all -> 0x0639 }
            if (r2 == 0) goto L_0x0726
            r3.markRestored()     // Catch:{ Exception -> 0x068d, all -> 0x0639 }
            r59 = r4
            r60 = r8
            r61 = r14
            goto L_0x07b8
        L_0x0726:
            r2 = 2
            boolean r2 = r3.hasRestoreFlag(r2)     // Catch:{ Exception -> 0x0797, all -> 0x0639 }
            if (r2 == 0) goto L_0x077b
            android.os.UserHandle r2 = r3.user     // Catch:{ Exception -> 0x0797, all -> 0x0639 }
            android.content.Intent r2 = r10.getAppLaunchIntent(r9, r2)     // Catch:{ Exception -> 0x0797, all -> 0x0639 }
            if (r2 == 0) goto L_0x076e
            r13 = 0
            r3.restoreFlag = r13     // Catch:{ Exception -> 0x0797, all -> 0x0639 }
            com.android.launcher3.util.ContentWriter r13 = r3.updater()     // Catch:{ Exception -> 0x0797, all -> 0x0639 }
            r59 = r4
            java.lang.String r4 = "intent"
            r60 = r8
            r61 = r14
            r8 = 0
            java.lang.String r14 = r2.toUri(r8)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            com.android.launcher3.util.ContentWriter r4 = r13.put((java.lang.String) r4, (java.lang.String) r14)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r4.commit()     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            android.content.ComponentName r4 = r2.getComponent()     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r7 = r4
            goto L_0x07ba
        L_0x0757:
            r0 = move-exception
            r60 = r8
            r61 = r14
            r4 = r1
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r1 = r0
            goto L_0x0bd8
        L_0x076e:
            r59 = r4
            r60 = r8
            r61 = r14
            java.lang.String r4 = "Unable to find a launch target"
            r3.markDeleted(r4)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            goto L_0x067f
        L_0x077b:
            r59 = r4
            r60 = r8
            r61 = r14
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r2.<init>()     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            java.lang.String r4 = "Invalid component removed: "
            r2.append(r4)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r2.append(r7)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r3.markDeleted(r2)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            goto L_0x067f
        L_0x0797:
            r0 = move-exception
            r59 = r4
            r60 = r8
            r61 = r14
            r4 = r1
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r1 = r0
            goto L_0x0bd8
        L_0x07b0:
            r58 = r2
            r59 = r4
            r60 = r8
            r61 = r14
        L_0x07b8:
            r2 = r58
        L_0x07ba:
            boolean r4 = android.text.TextUtils.isEmpty(r9)     // Catch:{ Exception -> 0x0b63, all -> 0x0b76 }
            if (r4 != 0) goto L_0x0892
            if (r12 != 0) goto L_0x0892
            int r4 = r3.restoreFlag     // Catch:{ Exception -> 0x087f, all -> 0x0870 }
            if (r4 == 0) goto L_0x0825
            java.lang.String r4 = "LoaderTask"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r8.<init>()     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            java.lang.String r13 = "package not yet restored: "
            r8.append(r13)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r8.append(r9)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            com.android.launcher3.logging.FileLog.d(r4, r8)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r4 = 8
            boolean r8 = r3.hasRestoreFlag(r4)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            if (r8 == 0) goto L_0x07e8
        L_0x07e4:
            r8 = r33
            goto L_0x0894
        L_0x07e8:
            boolean r4 = r5.containsKey(r9)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            if (r4 == 0) goto L_0x07fd
            int r4 = r3.restoreFlag     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r8 = 8
            r4 = r4 | r8
            r3.restoreFlag = r4     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            com.android.launcher3.util.ContentWriter r4 = r3.updater()     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r4.commit()     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            goto L_0x07e4
        L_0x07fd:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r4.<init>()     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            java.lang.String r8 = "Unrestored app removed: "
            r4.append(r8)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r4.append(r9)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            r3.markDeleted(r4)     // Catch:{ Exception -> 0x0813, all -> 0x0639 }
            goto L_0x067f
        L_0x0813:
            r0 = move-exception
            r4 = r1
            r65 = r15
            r7 = r29
            r71 = r33
        L_0x081b:
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            goto L_0x023a
        L_0x0825:
            android.os.UserHandle r4 = r3.user     // Catch:{ Exception -> 0x087f, all -> 0x0870 }
            boolean r4 = r10.isAppOnSdcard(r9, r4)     // Catch:{ Exception -> 0x087f, all -> 0x0870 }
            if (r4 == 0) goto L_0x0834
            r6 = r6 | 2
            r4 = 1
            r8 = r33
            goto L_0x0896
        L_0x0834:
            if (r32 != 0) goto L_0x0855
            java.lang.String r4 = "LoaderTask"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x087f, all -> 0x0870 }
            r8.<init>()     // Catch:{ Exception -> 0x087f, all -> 0x0870 }
            java.lang.String r13 = "Missing pkg, will check later: "
            r8.append(r13)     // Catch:{ Exception -> 0x087f, all -> 0x0870 }
            r8.append(r9)     // Catch:{ Exception -> 0x087f, all -> 0x0870 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x087f, all -> 0x0870 }
            android.util.Log.d(r4, r8)     // Catch:{ Exception -> 0x087f, all -> 0x0870 }
            android.os.UserHandle r4 = r3.user     // Catch:{ Exception -> 0x087f, all -> 0x0870 }
            r8 = r33
            r8.addToList(r4, r9)     // Catch:{ Exception -> 0x08ae, all -> 0x08a3 }
            r4 = 1
            goto L_0x0896
        L_0x0855:
            r8 = r33
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x08ae, all -> 0x08a3 }
            r4.<init>()     // Catch:{ Exception -> 0x08ae, all -> 0x08a3 }
            java.lang.String r13 = "Invalid package removed: "
            r4.append(r13)     // Catch:{ Exception -> 0x08ae, all -> 0x08a3 }
            r4.append(r9)     // Catch:{ Exception -> 0x08ae, all -> 0x08a3 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x08ae, all -> 0x08a3 }
            r3.markDeleted(r4)     // Catch:{ Exception -> 0x08ae, all -> 0x08a3 }
            r65 = r15
            goto L_0x0683
        L_0x0870:
            r0 = move-exception
            r4 = r1
            r76 = r5
            r2 = r26
            r7 = r29
            r9 = r33
            r74 = r46
            r1 = r0
            goto L_0x0df0
        L_0x087f:
            r0 = move-exception
            r4 = r1
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r1 = r0
            goto L_0x0bd8
        L_0x0892:
            r8 = r33
        L_0x0894:
            r4 = r41
        L_0x0896:
            int r13 = r3.restoreFlag     // Catch:{ Exception -> 0x0b50, all -> 0x0b42 }
            r13 = r13 & 16
            if (r13 == 0) goto L_0x089d
            r12 = 0
        L_0x089d:
            if (r12 == 0) goto L_0x08b8
            r3.markRestored()     // Catch:{ Exception -> 0x08ae, all -> 0x08a3 }
            goto L_0x08b8
        L_0x08a3:
            r0 = move-exception
            r4 = r1
            r76 = r5
            r9 = r8
            r2 = r26
            r7 = r29
            goto L_0x0643
        L_0x08ae:
            r0 = move-exception
            r4 = r1
            r71 = r8
            r65 = r15
            r7 = r29
            goto L_0x081b
        L_0x08b8:
            boolean r13 = r3.isOnWorkspaceOrHotseat()     // Catch:{ Exception -> 0x0b50, all -> 0x0b42 }
            if (r13 != 0) goto L_0x08f1
            r13 = r36
            int r14 = r3.getInt(r13)     // Catch:{ Exception -> 0x08e0, all -> 0x08a3 }
            r62 = r7
            r7 = r48
            boolean r14 = r7.isItemInPreview(r14)     // Catch:{ Exception -> 0x08d0, all -> 0x08a3 }
            if (r14 != 0) goto L_0x08f7
            r14 = 1
            goto L_0x08f8
        L_0x08d0:
            r0 = move-exception
            r4 = r1
            r63 = r7
        L_0x08d4:
            r71 = r8
            r65 = r15
            r7 = r29
            r74 = r46
            r68 = r47
            goto L_0x023a
        L_0x08e0:
            r0 = move-exception
            r4 = r1
            r71 = r8
            r65 = r15
            r7 = r29
            r74 = r46
            r68 = r47
            r63 = r48
            r1 = r0
            goto L_0x0bd8
        L_0x08f1:
            r62 = r7
            r13 = r36
            r7 = r48
        L_0x08f7:
            r14 = 0
        L_0x08f8:
            r63 = r7
            int r7 = r3.restoreFlag     // Catch:{ Exception -> 0x0b33, all -> 0x0b42 }
            if (r7 == 0) goto L_0x0918
            com.android.launcher3.ShortcutInfo r7 = r3.getRestoredItemInfo(r2)     // Catch:{ Exception -> 0x0915, all -> 0x08a3 }
        L_0x0902:
            r64 = r4
            r1 = r7
            r71 = r8
            r70 = r12
            r66 = r14
            r65 = r15
            r7 = r29
            r74 = r46
            r68 = r47
            goto L_0x0acb
        L_0x0915:
            r0 = move-exception
            r4 = r1
            goto L_0x08d4
        L_0x0918:
            int r7 = r3.itemType     // Catch:{ Exception -> 0x0b33, all -> 0x0b42 }
            if (r7 != 0) goto L_0x0921
            com.android.launcher3.ShortcutInfo r7 = r3.getAppShortcutInfo(r2, r4, r14)     // Catch:{ Exception -> 0x0915, all -> 0x08a3 }
            goto L_0x0902
        L_0x0921:
            int r7 = r3.itemType     // Catch:{ Exception -> 0x0b33, all -> 0x0b42 }
            r64 = r4
            r4 = 6
            if (r7 != r4) goto L_0x0a6d
            android.os.UserHandle r4 = r3.user     // Catch:{ Exception -> 0x0a5d, all -> 0x0a4e }
            com.android.launcher3.shortcuts.ShortcutKey r4 = com.android.launcher3.shortcuts.ShortcutKey.fromIntent(r2, r4)     // Catch:{ Exception -> 0x0a5d, all -> 0x0a4e }
            r66 = r14
            r65 = r15
            long r14 = r3.serialNumber     // Catch:{ Exception -> 0x0a40, all -> 0x0a4e }
            r7 = r47
            java.lang.Object r14 = r7.get(r14)     // Catch:{ Exception -> 0x0a32, all -> 0x0a4e }
            java.lang.Boolean r14 = (java.lang.Boolean) r14     // Catch:{ Exception -> 0x0a32, all -> 0x0a4e }
            boolean r14 = r14.booleanValue()     // Catch:{ Exception -> 0x0a32, all -> 0x0a4e }
            if (r14 == 0) goto L_0x0a19
            r14 = r46
            java.lang.Object r15 = r14.get(r4)     // Catch:{ Exception -> 0x0a0b, all -> 0x09fc }
            com.android.launcher3.shortcuts.ShortcutInfoCompat r15 = (com.android.launcher3.shortcuts.ShortcutInfoCompat) r15     // Catch:{ Exception -> 0x0a0b, all -> 0x09fc }
            if (r15 != 0) goto L_0x0986
            r67 = r4
            java.lang.String r4 = "Pinned shortcut not found"
            r3.markDeleted(r4)     // Catch:{ Exception -> 0x097a, all -> 0x096d }
        L_0x0955:
            r21 = r5
            r9 = r7
            r33 = r8
            r36 = r13
            r4 = r14
            r6 = r44
            r7 = r45
            r5 = r59
            r8 = r60
            r14 = r61
            r2 = r63
            r15 = r65
            goto L_0x01e3
        L_0x096d:
            r0 = move-exception
            r4 = r1
            r76 = r5
            r9 = r8
            r74 = r14
            r2 = r26
            r7 = r29
            goto L_0x021b
        L_0x097a:
            r0 = move-exception
            r4 = r1
            r68 = r7
            r71 = r8
            r74 = r14
            r7 = r29
            goto L_0x023a
        L_0x0986:
            r67 = r4
            com.android.launcher3.ShortcutInfo r4 = new com.android.launcher3.ShortcutInfo     // Catch:{ Exception -> 0x0a0b, all -> 0x09fc }
            r68 = r7
            r7 = r29
            r4.<init>(r15, r7)     // Catch:{ Exception -> 0x09f2, all -> 0x09e5 }
            r69 = r4
            r70 = r12
            com.android.launcher3.model.LoaderTask$1 r12 = new com.android.launcher3.model.LoaderTask$1     // Catch:{ Exception -> 0x09f2, all -> 0x09e5 }
            r71 = r8
            r8 = r69
            r12.<init>(r3, r8)     // Catch:{ Exception -> 0x09dd, all -> 0x09cf }
            com.android.launcher3.graphics.LauncherIcons r18 = com.android.launcher3.graphics.LauncherIcons.obtain(r7)     // Catch:{ Exception -> 0x09dd, all -> 0x09cf }
            r72 = r18
            r73 = r8
            r74 = r14
            r8 = r72
            r14 = 1
            com.android.launcher3.graphics.BitmapInfo r1 = r8.createShortcutIcon(r15, r14, r12)     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            r1.applyTo((com.android.launcher3.ItemInfoWithIcon) r4)     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            r8.recycle()     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            java.lang.String r1 = r15.getPackage()     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            android.os.UserHandle r14 = r4.user     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            boolean r1 = r10.isAppSuspended(r1, r14)     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            if (r1 == 0) goto L_0x09c8
            int r1 = r4.runtimeStatusFlags     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            r14 = 4
            r1 = r1 | r14
            r4.runtimeStatusFlags = r1     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
        L_0x09c8:
            android.content.Intent r1 = r4.intent     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            r2 = r1
            r1 = r4
            goto L_0x0a30
        L_0x09cf:
            r0 = move-exception
            r74 = r14
            r1 = r0
            r76 = r5
            r2 = r26
            r9 = r71
            r4 = r78
            goto L_0x0df0
        L_0x09dd:
            r0 = move-exception
            r74 = r14
            r1 = r0
            r4 = r78
            goto L_0x0bd8
        L_0x09e5:
            r0 = move-exception
            r74 = r14
            r1 = r0
            r76 = r5
            r9 = r8
            r2 = r26
            r4 = r78
            goto L_0x0df0
        L_0x09f2:
            r0 = move-exception
            r71 = r8
            r74 = r14
            r1 = r0
            r4 = r78
            goto L_0x0bd8
        L_0x09fc:
            r0 = move-exception
            r74 = r14
            r7 = r29
            r1 = r0
            r76 = r5
            r9 = r8
            r2 = r26
            r4 = r78
            goto L_0x0df0
        L_0x0a0b:
            r0 = move-exception
            r68 = r7
            r71 = r8
            r74 = r14
            r7 = r29
            r1 = r0
            r4 = r78
            goto L_0x0bd8
        L_0x0a19:
            r67 = r4
            r68 = r7
            r71 = r8
            r70 = r12
            r7 = r29
            r74 = r46
            com.android.launcher3.ShortcutInfo r1 = r3.loadSimpleShortcut()     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            int r4 = r1.runtimeStatusFlags     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            r8 = 32
            r4 = r4 | r8
            r1.runtimeStatusFlags = r4     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
        L_0x0a30:
            goto L_0x0acb
        L_0x0a32:
            r0 = move-exception
            r68 = r7
            r71 = r8
            r7 = r29
            r74 = r46
            r1 = r0
            r4 = r78
            goto L_0x0bd8
        L_0x0a40:
            r0 = move-exception
            r71 = r8
            r7 = r29
            r74 = r46
            r68 = r47
            r1 = r0
            r4 = r78
            goto L_0x0bd8
        L_0x0a4e:
            r0 = move-exception
            r7 = r29
            r74 = r46
            r1 = r0
            r76 = r5
            r9 = r8
            r2 = r26
            r4 = r78
            goto L_0x0b4e
        L_0x0a5d:
            r0 = move-exception
            r71 = r8
            r65 = r15
            r7 = r29
            r74 = r46
            r68 = r47
            r1 = r0
            r4 = r78
            goto L_0x0b40
        L_0x0a6d:
            r71 = r8
            r70 = r12
            r66 = r14
            r65 = r15
            r7 = r29
            r74 = r46
            r68 = r47
            com.android.launcher3.ShortcutInfo r1 = r3.loadSimpleShortcut()     // Catch:{ Exception -> 0x0b21, all -> 0x0b1c }
            boolean r4 = android.text.TextUtils.isEmpty(r9)     // Catch:{ Exception -> 0x0b21, all -> 0x0b1c }
            if (r4 != 0) goto L_0x0aa2
            android.os.UserHandle r4 = r3.user     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            boolean r4 = r10.isAppSuspended(r9, r4)     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            if (r4 == 0) goto L_0x0aa2
            r6 = r6 | 4
            goto L_0x0aa2
        L_0x0a90:
            r0 = move-exception
            r1 = r0
            r76 = r5
            r2 = r26
            r9 = r71
            r4 = r78
            goto L_0x0df0
        L_0x0a9c:
            r0 = move-exception
            r1 = r0
            r4 = r78
            goto L_0x0bd8
        L_0x0aa2:
            java.lang.String r4 = r2.getAction()     // Catch:{ Exception -> 0x0b21, all -> 0x0b1c }
            if (r4 == 0) goto L_0x0acb
            java.util.Set r4 = r2.getCategories()     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            if (r4 == 0) goto L_0x0acb
            java.lang.String r4 = r2.getAction()     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            java.lang.String r8 = "android.intent.action.MAIN"
            boolean r4 = r4.equals(r8)     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            if (r4 == 0) goto L_0x0acb
            java.util.Set r4 = r2.getCategories()     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            java.lang.String r8 = "android.intent.category.LAUNCHER"
            boolean r4 = r4.contains(r8)     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            if (r4 == 0) goto L_0x0acb
            r4 = 270532608(0x10200000, float:3.1554436E-29)
            r2.addFlags(r4)     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
        L_0x0acb:
            if (r1 == 0) goto L_0x0b26
            r3.applyCommonProperties(r1)     // Catch:{ Exception -> 0x0b21, all -> 0x0b1c }
            r1.intent = r2     // Catch:{ Exception -> 0x0b21, all -> 0x0b1c }
            int r4 = r3.getInt(r13)     // Catch:{ Exception -> 0x0b21, all -> 0x0b1c }
            r1.rank = r4     // Catch:{ Exception -> 0x0b21, all -> 0x0b1c }
            r4 = 1
            r1.spanX = r4     // Catch:{ Exception -> 0x0b21, all -> 0x0b1c }
            r1.spanY = r4     // Catch:{ Exception -> 0x0b21, all -> 0x0b1c }
            int r4 = r1.runtimeStatusFlags     // Catch:{ Exception -> 0x0b21, all -> 0x0b1c }
            r4 = r4 | r6
            r1.runtimeStatusFlags = r4     // Catch:{ Exception -> 0x0b21, all -> 0x0b1c }
            if (r11 == 0) goto L_0x0af0
            boolean r4 = com.android.launcher3.Utilities.isSystemApp(r7, r2)     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            if (r4 != 0) goto L_0x0af0
            int r4 = r1.runtimeStatusFlags     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            r8 = 1
            r4 = r4 | r8
            r1.runtimeStatusFlags = r4     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
        L_0x0af0:
            int r4 = r3.restoreFlag     // Catch:{ Exception -> 0x0b21, all -> 0x0b1c }
            if (r4 == 0) goto L_0x0b13
            boolean r4 = android.text.TextUtils.isEmpty(r9)     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            if (r4 != 0) goto L_0x0b13
            java.lang.Object r4 = r5.get(r9)     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            android.content.pm.PackageInstaller$SessionInfo r4 = (android.content.pm.PackageInstaller.SessionInfo) r4     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            if (r4 != 0) goto L_0x0b09
            int r8 = r1.status     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            r8 = r8 & -5
            r1.status = r8     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            goto L_0x0b13
        L_0x0b09:
            float r8 = r4.getProgress()     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            float r8 = r8 * r17
            int r8 = (int) r8     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
            r1.setInstallProgress(r8)     // Catch:{ Exception -> 0x0a9c, all -> 0x0a90 }
        L_0x0b13:
            r4 = r78
            com.android.launcher3.model.BgDataModel r8 = r4.mBgDataModel     // Catch:{ Exception -> 0x0b30 }
            r3.checkAndAddItem(r1, r8)     // Catch:{ Exception -> 0x0b30 }
            goto L_0x0b9d
        L_0x0b1c:
            r0 = move-exception
            r4 = r78
            goto L_0x0be1
        L_0x0b21:
            r0 = move-exception
            r4 = r78
            goto L_0x023a
        L_0x0b26:
            r4 = r78
            java.lang.RuntimeException r8 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0b30 }
            java.lang.String r12 = "Unexpected null ShortcutInfo"
            r8.<init>(r12)     // Catch:{ Exception -> 0x0b30 }
            throw r8     // Catch:{ Exception -> 0x0b30 }
        L_0x0b30:
            r0 = move-exception
            goto L_0x023a
        L_0x0b33:
            r0 = move-exception
            r4 = r1
            r71 = r8
            r65 = r15
            r7 = r29
            r74 = r46
            r68 = r47
            r1 = r0
        L_0x0b40:
            goto L_0x0bd8
        L_0x0b42:
            r0 = move-exception
            r4 = r1
            r7 = r29
            r74 = r46
            r1 = r0
            r76 = r5
            r9 = r8
            r2 = r26
        L_0x0b4e:
            goto L_0x0df0
        L_0x0b50:
            r0 = move-exception
            r4 = r1
            r71 = r8
            r65 = r15
            r7 = r29
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r1 = r0
            goto L_0x0bd8
        L_0x0b63:
            r0 = move-exception
            r4 = r1
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r1 = r0
            goto L_0x0bd8
        L_0x0b76:
            r0 = move-exception
            r4 = r1
            r7 = r29
            r74 = r46
            r1 = r0
            r76 = r5
            r2 = r26
            r9 = r33
            goto L_0x0df0
        L_0x0b85:
            r0 = move-exception
            r59 = r4
            r60 = r8
            r61 = r14
            r65 = r15
            r7 = r29
            r71 = r33
            r13 = r36
            r74 = r46
            r68 = r47
            r63 = r48
            r4 = r1
            r1 = r0
            goto L_0x0bd8
        L_0x0b9d:
            r1 = r4
            r21 = r5
            r29 = r7
            r36 = r13
            r6 = r44
            r7 = r45
            r5 = r59
            r8 = r60
            r14 = r61
            r2 = r63
            r15 = r65
            r9 = r68
            r33 = r71
            r4 = r74
            goto L_0x01e3
        L_0x0bbb:
            r0 = move-exception
            r63 = r2
            r74 = r4
            r59 = r5
            r44 = r6
            r45 = r7
            r60 = r8
            r68 = r9
            r61 = r14
            r65 = r15
            r5 = r21
            r7 = r29
            r71 = r33
            r13 = r36
            r4 = r1
            r1 = r0
        L_0x0bd8:
            java.lang.String r2 = "LoaderTask"
            java.lang.String r6 = "Desktop items loading interrupted"
            android.util.Log.e(r2, r6, r1)     // Catch:{ all -> 0x0be0 }
            goto L_0x0b9d
        L_0x0be0:
            r0 = move-exception
        L_0x0be1:
            r1 = r0
            r76 = r5
            r2 = r26
            r9 = r71
            goto L_0x0df0
        L_0x0bea:
            r0 = move-exception
            r74 = r4
            r7 = r29
            r4 = r1
            r1 = r0
            r76 = r21
            r2 = r26
            r9 = r33
            goto L_0x0df0
        L_0x0bf9:
            r74 = r4
            r5 = r21
            r7 = r29
            r71 = r33
            r4 = r1
            com.android.launcher3.Utilities.closeSilently(r3)     // Catch:{ all -> 0x0da0 }
            boolean r1 = r4.mStopped     // Catch:{ all -> 0x0da0 }
            if (r1 == 0) goto L_0x0c19
            com.android.launcher3.model.BgDataModel r1 = r4.mBgDataModel     // Catch:{ all -> 0x0c11 }
            r1.clear()     // Catch:{ all -> 0x0c11 }
            monitor-exit(r16)     // Catch:{ all -> 0x0c11 }
            return
        L_0x0c11:
            r0 = move-exception
            r1 = r0
            r2 = r26
        L_0x0c15:
            r9 = r71
            goto L_0x0e04
        L_0x0c19:
            boolean r1 = r3.commitDeleted()     // Catch:{ all -> 0x0da0 }
            if (r1 == 0) goto L_0x0c72
            java.lang.String r1 = "delete_empty_folders"
            r2 = r26
            android.os.Bundle r1 = com.android.launcher3.LauncherSettings.Settings.call(r2, r1)     // Catch:{ all -> 0x0c67 }
            java.lang.String r6 = "value"
            java.io.Serializable r1 = r1.getSerializable(r6)     // Catch:{ all -> 0x0c67 }
            java.util.ArrayList r1 = (java.util.ArrayList) r1     // Catch:{ all -> 0x0c67 }
            java.util.Iterator r6 = r1.iterator()     // Catch:{ all -> 0x0c67 }
        L_0x0c33:
            boolean r8 = r6.hasNext()     // Catch:{ all -> 0x0c67 }
            if (r8 == 0) goto L_0x0c61
            java.lang.Object r8 = r6.next()     // Catch:{ all -> 0x0c67 }
            java.lang.Long r8 = (java.lang.Long) r8     // Catch:{ all -> 0x0c67 }
            long r8 = r8.longValue()     // Catch:{ all -> 0x0c67 }
            com.android.launcher3.model.BgDataModel r12 = r4.mBgDataModel     // Catch:{ all -> 0x0c67 }
            java.util.ArrayList<com.android.launcher3.ItemInfo> r12 = r12.workspaceItems     // Catch:{ all -> 0x0c67 }
            com.android.launcher3.model.BgDataModel r13 = r4.mBgDataModel     // Catch:{ all -> 0x0c67 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.FolderInfo> r13 = r13.folders     // Catch:{ all -> 0x0c67 }
            java.lang.Object r13 = r13.get(r8)     // Catch:{ all -> 0x0c67 }
            r12.remove(r13)     // Catch:{ all -> 0x0c67 }
            com.android.launcher3.model.BgDataModel r12 = r4.mBgDataModel     // Catch:{ all -> 0x0c67 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.FolderInfo> r12 = r12.folders     // Catch:{ all -> 0x0c67 }
            r12.remove(r8)     // Catch:{ all -> 0x0c67 }
            com.android.launcher3.model.BgDataModel r12 = r4.mBgDataModel     // Catch:{ all -> 0x0c67 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r12 = r12.itemsIdMap     // Catch:{ all -> 0x0c67 }
            r12.remove(r8)     // Catch:{ all -> 0x0c67 }
            goto L_0x0c33
        L_0x0c61:
            java.lang.String r6 = "remove_ghost_widgets"
            com.android.launcher3.LauncherSettings.Settings.call(r2, r6)     // Catch:{ all -> 0x0c67 }
            goto L_0x0c74
        L_0x0c67:
            r0 = move-exception
            r1 = r0
            goto L_0x0c15
        L_0x0c6a:
            r0 = move-exception
            r2 = r26
            r1 = r0
            r9 = r71
            goto L_0x0e04
        L_0x0c72:
            r2 = r26
        L_0x0c74:
            java.util.HashSet r1 = com.android.launcher3.InstallShortcutReceiver.getPendingShortcuts(r7)     // Catch:{ all -> 0x0d9a }
            java.util.Set r6 = r74.keySet()     // Catch:{ all -> 0x0d9a }
            java.util.Iterator r6 = r6.iterator()     // Catch:{ all -> 0x0d9a }
        L_0x0c80:
            boolean r8 = r6.hasNext()     // Catch:{ all -> 0x0d9a }
            if (r8 == 0) goto L_0x0ca8
            java.lang.Object r8 = r6.next()     // Catch:{ all -> 0x0c67 }
            com.android.launcher3.shortcuts.ShortcutKey r8 = (com.android.launcher3.shortcuts.ShortcutKey) r8     // Catch:{ all -> 0x0c67 }
            com.android.launcher3.model.BgDataModel r9 = r4.mBgDataModel     // Catch:{ all -> 0x0c67 }
            java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, android.util.MutableInt> r9 = r9.pinnedShortcutCounts     // Catch:{ all -> 0x0c67 }
            java.lang.Object r9 = r9.get(r8)     // Catch:{ all -> 0x0c67 }
            android.util.MutableInt r9 = (android.util.MutableInt) r9     // Catch:{ all -> 0x0c67 }
            if (r9 == 0) goto L_0x0c9c
            int r12 = r9.value     // Catch:{ all -> 0x0c67 }
            if (r12 != 0) goto L_0x0ca7
        L_0x0c9c:
            boolean r12 = r1.contains(r8)     // Catch:{ all -> 0x0c67 }
            if (r12 != 0) goto L_0x0ca7
            com.android.launcher3.shortcuts.DeepShortcutManager r12 = r4.mShortcutManager     // Catch:{ all -> 0x0c67 }
            r12.unpinShortcut(r8)     // Catch:{ all -> 0x0c67 }
        L_0x0ca7:
            goto L_0x0c80
        L_0x0ca8:
            com.android.launcher3.folder.FolderIconPreviewVerifier r6 = new com.android.launcher3.folder.FolderIconPreviewVerifier     // Catch:{ all -> 0x0d9a }
            com.android.launcher3.LauncherAppState r8 = r4.mApp     // Catch:{ all -> 0x0d9a }
            com.android.launcher3.InvariantDeviceProfile r8 = r8.getInvariantDeviceProfile()     // Catch:{ all -> 0x0d9a }
            r6.<init>(r8)     // Catch:{ all -> 0x0d9a }
            com.android.launcher3.model.BgDataModel r8 = r4.mBgDataModel     // Catch:{ all -> 0x0d9a }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.FolderInfo> r8 = r8.folders     // Catch:{ all -> 0x0d9a }
            java.util.Iterator r8 = r8.iterator()     // Catch:{ all -> 0x0d9a }
        L_0x0cbb:
            boolean r9 = r8.hasNext()     // Catch:{ all -> 0x0d9a }
            if (r9 == 0) goto L_0x0d11
            java.lang.Object r9 = r8.next()     // Catch:{ all -> 0x0c67 }
            com.android.launcher3.FolderInfo r9 = (com.android.launcher3.FolderInfo) r9     // Catch:{ all -> 0x0c67 }
            java.util.ArrayList<com.android.launcher3.ShortcutInfo> r12 = r9.contents     // Catch:{ all -> 0x0c67 }
            java.util.Comparator<com.android.launcher3.ItemInfo> r13 = com.android.launcher3.folder.Folder.ITEM_POS_COMPARATOR     // Catch:{ all -> 0x0c67 }
            java.util.Collections.sort(r12, r13)     // Catch:{ all -> 0x0c67 }
            r6.setFolderInfo(r9)     // Catch:{ all -> 0x0c67 }
            r12 = 0
            java.util.ArrayList<com.android.launcher3.ShortcutInfo> r13 = r9.contents     // Catch:{ all -> 0x0c67 }
            java.util.Iterator r13 = r13.iterator()     // Catch:{ all -> 0x0c67 }
        L_0x0cd8:
            boolean r14 = r13.hasNext()     // Catch:{ all -> 0x0c67 }
            if (r14 == 0) goto L_0x0d0a
            java.lang.Object r14 = r13.next()     // Catch:{ all -> 0x0c67 }
            com.android.launcher3.ShortcutInfo r14 = (com.android.launcher3.ShortcutInfo) r14     // Catch:{ all -> 0x0c67 }
            boolean r15 = r14.usingLowResIcon     // Catch:{ all -> 0x0c67 }
            if (r15 == 0) goto L_0x0cff
            int r15 = r14.itemType     // Catch:{ all -> 0x0c67 }
            if (r15 != 0) goto L_0x0cff
            int r15 = r14.rank     // Catch:{ all -> 0x0c67 }
            boolean r15 = r6.isItemInPreview(r15)     // Catch:{ all -> 0x0c67 }
            if (r15 == 0) goto L_0x0cff
            com.android.launcher3.IconCache r15 = r4.mIconCache     // Catch:{ all -> 0x0c67 }
            r75 = r1
            r1 = 0
            r15.getTitleAndIcon(r14, r1)     // Catch:{ all -> 0x0c67 }
            int r12 = r12 + 1
            goto L_0x0d02
        L_0x0cff:
            r75 = r1
            r1 = 0
        L_0x0d02:
            r15 = 4
            if (r12 < r15) goto L_0x0d06
            goto L_0x0d0e
        L_0x0d06:
            r1 = r75
            goto L_0x0cd8
        L_0x0d0a:
            r75 = r1
            r1 = 0
            r15 = 4
        L_0x0d0e:
            r1 = r75
            goto L_0x0cbb
        L_0x0d11:
            r75 = r1
            r3.commitRestoredItems()     // Catch:{ all -> 0x0d9a }
            if (r32 != 0) goto L_0x0d3c
            boolean r1 = r71.isEmpty()     // Catch:{ all -> 0x0d9a }
            if (r1 != 0) goto L_0x0d3c
            com.android.launcher3.model.SdCardAvailableReceiver r1 = new com.android.launcher3.model.SdCardAvailableReceiver     // Catch:{ all -> 0x0d9a }
            com.android.launcher3.LauncherAppState r8 = r4.mApp     // Catch:{ all -> 0x0d9a }
            r9 = r71
            r1.<init>(r8, r9)     // Catch:{ all -> 0x0e06 }
            android.content.IntentFilter r8 = new android.content.IntentFilter     // Catch:{ all -> 0x0e06 }
            java.lang.String r12 = "android.intent.action.BOOT_COMPLETED"
            r8.<init>(r12)     // Catch:{ all -> 0x0e06 }
            android.os.Handler r12 = new android.os.Handler     // Catch:{ all -> 0x0e06 }
            android.os.Looper r13 = com.android.launcher3.LauncherModel.getWorkerLooper()     // Catch:{ all -> 0x0e06 }
            r12.<init>(r13)     // Catch:{ all -> 0x0e06 }
            r13 = 0
            r7.registerReceiver(r1, r8, r13, r12)     // Catch:{ all -> 0x0e06 }
            goto L_0x0d3e
        L_0x0d3c:
            r9 = r71
        L_0x0d3e:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x0e06 }
            com.android.launcher3.model.BgDataModel r8 = r4.mBgDataModel     // Catch:{ all -> 0x0e06 }
            java.util.ArrayList<java.lang.Long> r8 = r8.workspaceScreens     // Catch:{ all -> 0x0e06 }
            r1.<init>(r8)     // Catch:{ all -> 0x0e06 }
            com.android.launcher3.model.BgDataModel r8 = r4.mBgDataModel     // Catch:{ all -> 0x0e06 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r8 = r8.itemsIdMap     // Catch:{ all -> 0x0e06 }
            java.util.Iterator r8 = r8.iterator()     // Catch:{ all -> 0x0e06 }
        L_0x0d4f:
            boolean r12 = r8.hasNext()     // Catch:{ all -> 0x0e06 }
            if (r12 == 0) goto L_0x0d80
            java.lang.Object r12 = r8.next()     // Catch:{ all -> 0x0e06 }
            com.android.launcher3.ItemInfo r12 = (com.android.launcher3.ItemInfo) r12     // Catch:{ all -> 0x0e06 }
            long r13 = r12.screenId     // Catch:{ all -> 0x0e06 }
            r76 = r5
            r77 = r6
            long r5 = r12.container     // Catch:{ all -> 0x0e06 }
            r17 = -100
            int r5 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
            if (r5 != 0) goto L_0x0d7a
            java.lang.Long r5 = java.lang.Long.valueOf(r13)     // Catch:{ all -> 0x0e06 }
            boolean r5 = r1.contains(r5)     // Catch:{ all -> 0x0e06 }
            if (r5 == 0) goto L_0x0d7a
            java.lang.Long r5 = java.lang.Long.valueOf(r13)     // Catch:{ all -> 0x0e06 }
            r1.remove(r5)     // Catch:{ all -> 0x0e06 }
        L_0x0d7a:
            r5 = r76
            r6 = r77
            goto L_0x0d4f
        L_0x0d80:
            r76 = r5
            r77 = r6
            int r5 = r1.size()     // Catch:{ all -> 0x0e06 }
            if (r5 == 0) goto L_0x0d98
            com.android.launcher3.model.BgDataModel r5 = r4.mBgDataModel     // Catch:{ all -> 0x0e06 }
            java.util.ArrayList<java.lang.Long> r5 = r5.workspaceScreens     // Catch:{ all -> 0x0e06 }
            r5.removeAll(r1)     // Catch:{ all -> 0x0e06 }
            com.android.launcher3.model.BgDataModel r5 = r4.mBgDataModel     // Catch:{ all -> 0x0e06 }
            java.util.ArrayList<java.lang.Long> r5 = r5.workspaceScreens     // Catch:{ all -> 0x0e06 }
            com.android.launcher3.LauncherModel.updateWorkspaceScreenOrder(r7, r5)     // Catch:{ all -> 0x0e06 }
        L_0x0d98:
            monitor-exit(r16)     // Catch:{ all -> 0x0e06 }
            return
        L_0x0d9a:
            r0 = move-exception
            r9 = r71
            r1 = r0
            goto L_0x0e04
        L_0x0da0:
            r0 = move-exception
            r2 = r26
            r9 = r71
            r1 = r0
            goto L_0x0e04
        L_0x0da8:
            r0 = move-exception
            r74 = r4
            r76 = r21
            r2 = r26
            r7 = r29
            r9 = r33
            r4 = r1
            r1 = r0
            goto L_0x0df0
        L_0x0db6:
            r0 = move-exception
            r4 = r1
            r7 = r2
            r32 = r12
            r9 = r13
            r74 = r20
            r76 = r21
            r2 = r26
            r1 = r0
            goto L_0x0df0
        L_0x0dc4:
            r0 = move-exception
            r4 = r1
            r7 = r2
            r2 = r9
            r32 = r12
            r9 = r13
            r74 = r20
            r76 = r21
            r1 = r0
            goto L_0x0df0
        L_0x0dd1:
            r0 = move-exception
            r4 = r1
            r7 = r2
            r2 = r9
            r32 = r12
            r9 = r13
            r23 = r15
            r74 = r20
            r76 = r21
            r1 = r0
            goto L_0x0df0
        L_0x0de0:
            r0 = move-exception
            r4 = r1
            r7 = r2
            r22 = r5
            r2 = r9
            r32 = r12
            r9 = r13
            r23 = r15
            r74 = r20
            r76 = r21
            r1 = r0
        L_0x0df0:
            com.android.launcher3.Utilities.closeSilently(r3)     // Catch:{ all -> 0x0e06 }
            throw r1     // Catch:{ all -> 0x0e06 }
        L_0x0df4:
            r0 = move-exception
            r4 = r1
            r7 = r2
            goto L_0x0dfd
        L_0x0df8:
            r0 = move-exception
            r4 = r1
            r7 = r2
            r16 = r8
        L_0x0dfd:
            r2 = r9
            r32 = r12
            r9 = r13
            r23 = r15
            r1 = r0
        L_0x0e04:
            monitor-exit(r16)     // Catch:{ all -> 0x0e06 }
            throw r1
        L_0x0e06:
            r0 = move-exception
            r1 = r0
            goto L_0x0e04
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.LoaderTask.loadWorkspace():void");
    }

    private void updateIconCache() {
        HashSet<String> packagesToIgnore = new HashSet<>();
        synchronized (this.mBgDataModel) {
            Iterator<ItemInfo> it = this.mBgDataModel.itemsIdMap.iterator();
            while (it.hasNext()) {
                ItemInfo info = it.next();
                if (info instanceof ShortcutInfo) {
                    ShortcutInfo si = (ShortcutInfo) info;
                    if (si.isPromise() && si.getTargetComponent() != null) {
                        packagesToIgnore.add(si.getTargetComponent().getPackageName());
                    }
                } else if (info instanceof LauncherAppWidgetInfo) {
                    LauncherAppWidgetInfo lawi = (LauncherAppWidgetInfo) info;
                    if (lawi.hasRestoreFlag(2)) {
                        packagesToIgnore.add(lawi.providerName.getPackageName());
                    }
                }
            }
        }
        this.mIconCache.updateDbIcons(packagesToIgnore);
    }

    private void loadAllApps() {
        List<UserHandle> profiles = this.mUserManager.getUserProfiles();
        this.mBgAllAppsList.clear();
        for (UserHandle user : profiles) {
            List<LauncherActivityInfo> apps = this.mLauncherApps.getActivityList((String) null, user);
            if (apps != null && !apps.isEmpty()) {
                boolean quietMode = this.mUserManager.isQuietModeEnabled(user);
                for (int i = 0; i < apps.size(); i++) {
                    LauncherActivityInfo app = apps.get(i);
                    this.mBgAllAppsList.add(new AppInfo(app, user, quietMode), app);
                }
            } else {
                return;
            }
        }
        this.mBgAllAppsList.added = new ArrayList<>();
    }

    private void loadDeepShortcuts() {
        this.mBgDataModel.deepShortcutMap.clear();
        this.mBgDataModel.hasShortcutHostPermission = this.mShortcutManager.hasHostPermission();
        if (this.mBgDataModel.hasShortcutHostPermission) {
            for (UserHandle user : this.mUserManager.getUserProfiles()) {
                if (this.mUserManager.isUserUnlocked(user)) {
                    this.mBgDataModel.updateDeepShortcutMap((String) null, user, this.mShortcutManager.queryForAllShortcuts(user));
                }
            }
        }
    }

    public static boolean isValidProvider(AppWidgetProviderInfo provider) {
        return (provider == null || provider.provider == null || provider.provider.getPackageName() == null) ? false : true;
    }
}
