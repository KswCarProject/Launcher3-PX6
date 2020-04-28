package com.android.launcher3.model;

import android.os.Looper;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.AppInfo;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.MainThreadExecutor;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.LooperIdleLock;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.widget.WidgetListRowEntry;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

public class LoaderResults {
    private static final long INVALID_SCREEN_ID = -1;
    private static final int ITEMS_CHUNK = 6;
    private static final String TAG = "LoaderResults";
    private final LauncherAppState mApp;
    private final AllAppsList mBgAllAppsList;
    private final BgDataModel mBgDataModel;
    /* access modifiers changed from: private */
    public final WeakReference<LauncherModel.Callbacks> mCallbacks;
    private final int mPageToBindFirst;
    private final Executor mUiExecutor = new MainThreadExecutor();

    public LoaderResults(LauncherAppState app, BgDataModel dataModel, AllAppsList allAppsList, int pageToBindFirst, WeakReference<LauncherModel.Callbacks> callbacks) {
        this.mApp = app;
        this.mBgDataModel = dataModel;
        this.mBgAllAppsList = allAppsList;
        this.mPageToBindFirst = pageToBindFirst;
        this.mCallbacks = callbacks == null ? new WeakReference<>((Object) null) : callbacks;
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00dc, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void bindWorkspace() {
        /*
            r17 = this;
            r1 = r17
            java.lang.ref.WeakReference<com.android.launcher3.LauncherModel$Callbacks> r0 = r1.mCallbacks
            java.lang.Object r0 = r0.get()
            r2 = r0
            com.android.launcher3.LauncherModel$Callbacks r2 = (com.android.launcher3.LauncherModel.Callbacks) r2
            if (r2 != 0) goto L_0x0015
            java.lang.String r0 = "LoaderResults"
            java.lang.String r3 = "LoaderTask running with no launcher"
            android.util.Log.w(r0, r3)
            return
        L_0x0015:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r3 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r4 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r5 = r0
            com.android.launcher3.model.BgDataModel r6 = r1.mBgDataModel
            monitor-enter(r6)
            com.android.launcher3.model.BgDataModel r0 = r1.mBgDataModel     // Catch:{ all -> 0x00d7 }
            java.util.ArrayList<com.android.launcher3.ItemInfo> r0 = r0.workspaceItems     // Catch:{ all -> 0x00d7 }
            r3.addAll(r0)     // Catch:{ all -> 0x00d7 }
            com.android.launcher3.model.BgDataModel r0 = r1.mBgDataModel     // Catch:{ all -> 0x00d7 }
            java.util.ArrayList<com.android.launcher3.LauncherAppWidgetInfo> r0 = r0.appWidgets     // Catch:{ all -> 0x00d7 }
            r4.addAll(r0)     // Catch:{ all -> 0x00d7 }
            com.android.launcher3.model.BgDataModel r0 = r1.mBgDataModel     // Catch:{ all -> 0x00d7 }
            java.util.ArrayList<java.lang.Long> r0 = r0.workspaceScreens     // Catch:{ all -> 0x00d7 }
            r5.addAll(r0)     // Catch:{ all -> 0x00d7 }
            com.android.launcher3.model.BgDataModel r0 = r1.mBgDataModel     // Catch:{ all -> 0x00d7 }
            int r7 = r0.lastBindId     // Catch:{ all -> 0x00d7 }
            r8 = 1
            int r7 = r7 + r8
            r0.lastBindId = r7     // Catch:{ all -> 0x00d7 }
            monitor-exit(r6)     // Catch:{ all -> 0x00d7 }
            int r0 = r1.mPageToBindFirst
            r6 = -1001(0xfffffffffffffc17, float:NaN)
            if (r0 == r6) goto L_0x0051
            int r0 = r1.mPageToBindFirst
            goto L_0x0055
        L_0x0051:
            int r0 = r2.getCurrentWorkspaceScreen()
        L_0x0055:
            int r6 = r5.size()
            if (r0 < r6) goto L_0x005d
            r0 = -1001(0xfffffffffffffc17, float:NaN)
        L_0x005d:
            if (r0 < 0) goto L_0x0061
            goto L_0x0062
        L_0x0061:
            r8 = 0
        L_0x0062:
            r6 = r8
            if (r6 == 0) goto L_0x0070
            java.lang.Object r7 = r5.get(r0)
            java.lang.Long r7 = (java.lang.Long) r7
            long r7 = r7.longValue()
            goto L_0x0072
        L_0x0070:
            r7 = -1
        L_0x0072:
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            filterCurrentWorkspaceItems(r7, r3, r9, r10)
            filterCurrentWorkspaceItems(r7, r4, r11, r12)
            r1.sortWorkspaceItemsSpatially(r9)
            r1.sortWorkspaceItemsSpatially(r10)
            com.android.launcher3.model.LoaderResults$1 r13 = new com.android.launcher3.model.LoaderResults$1
            r13.<init>()
            java.util.concurrent.Executor r14 = r1.mUiExecutor
            r14.execute(r13)
            java.util.concurrent.Executor r14 = r1.mUiExecutor
            com.android.launcher3.model.LoaderResults$2 r15 = new com.android.launcher3.model.LoaderResults$2
            r15.<init>(r5)
            r14.execute(r15)
            java.util.concurrent.Executor r14 = r1.mUiExecutor
            r1.bindWorkspaceItems(r9, r11, r14)
            if (r6 == 0) goto L_0x00b3
            com.android.launcher3.util.ViewOnDrawExecutor r15 = new com.android.launcher3.util.ViewOnDrawExecutor
            r15.<init>()
            goto L_0x00b4
        L_0x00b3:
            r15 = r14
        L_0x00b4:
            r16 = r2
            com.android.launcher3.model.LoaderResults$3 r2 = new com.android.launcher3.model.LoaderResults$3
            r2.<init>(r6, r15)
            r14.execute(r2)
            r1.bindWorkspaceItems(r10, r12, r15)
            com.android.launcher3.model.LoaderResults$4 r2 = new com.android.launcher3.model.LoaderResults$4
            r2.<init>()
            r15.execute(r2)
            if (r6 == 0) goto L_0x00d6
            com.android.launcher3.model.LoaderResults$5 r13 = new com.android.launcher3.model.LoaderResults$5
            r13.<init>(r0, r15)
            r2 = r13
            java.util.concurrent.Executor r13 = r1.mUiExecutor
            r13.execute(r2)
        L_0x00d6:
            return
        L_0x00d7:
            r0 = move-exception
            r16 = r2
        L_0x00da:
            monitor-exit(r6)     // Catch:{ all -> 0x00dc }
            throw r0
        L_0x00dc:
            r0 = move-exception
            goto L_0x00da
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.LoaderResults.bindWorkspace():void");
    }

    public static <T extends ItemInfo> void filterCurrentWorkspaceItems(long currentScreenId, ArrayList<T> allWorkspaceItems, ArrayList<T> currentScreenItems, ArrayList<T> otherScreenItems) {
        Iterator<T> iter = allWorkspaceItems.iterator();
        while (iter.hasNext()) {
            if (((ItemInfo) iter.next()) == null) {
                iter.remove();
            }
        }
        Set<Long> itemsOnScreen = new HashSet<>();
        Collections.sort(allWorkspaceItems, new Comparator<ItemInfo>() {
            public int compare(ItemInfo lhs, ItemInfo rhs) {
                return Utilities.longCompare(lhs.container, rhs.container);
            }
        });
        Iterator<T> it = allWorkspaceItems.iterator();
        while (it.hasNext()) {
            T info = (ItemInfo) it.next();
            if (info.container == -100) {
                if (info.screenId == currentScreenId) {
                    currentScreenItems.add(info);
                    itemsOnScreen.add(Long.valueOf(info.id));
                } else {
                    otherScreenItems.add(info);
                }
            } else if (info.container == -101) {
                currentScreenItems.add(info);
                itemsOnScreen.add(Long.valueOf(info.id));
            } else if (itemsOnScreen.contains(Long.valueOf(info.container))) {
                currentScreenItems.add(info);
                itemsOnScreen.add(Long.valueOf(info.id));
            } else {
                otherScreenItems.add(info);
            }
        }
    }

    private void sortWorkspaceItemsSpatially(ArrayList<ItemInfo> workspaceItems) {
        InvariantDeviceProfile profile = this.mApp.getInvariantDeviceProfile();
        final int screenCols = profile.numColumns;
        final int screenCellCount = profile.numColumns * profile.numRows;
        Collections.sort(workspaceItems, new Comparator<ItemInfo>() {
            public int compare(ItemInfo lhs, ItemInfo rhs) {
                if (lhs.container != rhs.container) {
                    return Utilities.longCompare(lhs.container, rhs.container);
                }
                switch ((int) lhs.container) {
                    case LauncherSettings.Favorites.CONTAINER_HOTSEAT:
                        return Utilities.longCompare(lhs.screenId, rhs.screenId);
                    case -100:
                        return Utilities.longCompare((lhs.screenId * ((long) screenCellCount)) + ((long) (lhs.cellY * screenCols)) + ((long) lhs.cellX), (rhs.screenId * ((long) screenCellCount)) + ((long) (rhs.cellY * screenCols)) + ((long) rhs.cellX));
                    default:
                        return 0;
                }
            }
        });
    }

    private void bindWorkspaceItems(final ArrayList<ItemInfo> workspaceItems, ArrayList<LauncherAppWidgetInfo> appWidgets, Executor executor) {
        int N = workspaceItems.size();
        for (int i = 0; i < N; i += 6) {
            final int start = i;
            final int chunkSize = i + 6 <= N ? 6 : N - i;
            executor.execute(new Runnable() {
                public void run() {
                    LauncherModel.Callbacks callbacks = (LauncherModel.Callbacks) LoaderResults.this.mCallbacks.get();
                    if (callbacks != null) {
                        callbacks.bindItems(workspaceItems.subList(start, start + chunkSize), false);
                    }
                }
            });
        }
        int N2 = appWidgets.size();
        for (int i2 = 0; i2 < N2; i2++) {
            final ItemInfo widget = appWidgets.get(i2);
            executor.execute(new Runnable() {
                public void run() {
                    LauncherModel.Callbacks callbacks = (LauncherModel.Callbacks) LoaderResults.this.mCallbacks.get();
                    if (callbacks != null) {
                        callbacks.bindItems(Collections.singletonList(widget), false);
                    }
                }
            });
        }
    }

    public void bindDeepShortcuts() {
        final MultiHashMap<ComponentKey, String> shortcutMapCopy;
        synchronized (this.mBgDataModel) {
            shortcutMapCopy = this.mBgDataModel.deepShortcutMap.clone();
        }
        this.mUiExecutor.execute(new Runnable() {
            public void run() {
                LauncherModel.Callbacks callbacks = (LauncherModel.Callbacks) LoaderResults.this.mCallbacks.get();
                if (callbacks != null) {
                    callbacks.bindDeepShortcutMap(shortcutMapCopy);
                }
            }
        });
    }

    public void bindAllApps() {
        final ArrayList<AppInfo> list = (ArrayList) this.mBgAllAppsList.data.clone();
        this.mUiExecutor.execute(new Runnable() {
            public void run() {
                LauncherModel.Callbacks callbacks = (LauncherModel.Callbacks) LoaderResults.this.mCallbacks.get();
                if (callbacks != null) {
                    callbacks.bindAllApplications(list);
                }
            }
        });
    }

    public void bindWidgets() {
        final ArrayList<WidgetListRowEntry> widgets = this.mBgDataModel.widgetsModel.getWidgetsList(this.mApp.getContext());
        this.mUiExecutor.execute(new Runnable() {
            public void run() {
                LauncherModel.Callbacks callbacks = (LauncherModel.Callbacks) LoaderResults.this.mCallbacks.get();
                if (callbacks != null) {
                    callbacks.bindAllWidgets(widgets);
                }
            }
        });
    }

    public LooperIdleLock newIdleLock(Object lock) {
        LooperIdleLock idleLock = new LooperIdleLock(lock, Looper.getMainLooper());
        if (this.mCallbacks.get() == null) {
            idleLock.queueIdle();
        }
        return idleLock;
    }
}
