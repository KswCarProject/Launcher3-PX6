package com.android.launcher3;

import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.model.AddWorkspaceItemsTask;
import com.android.launcher3.model.BaseModelUpdateTask;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.CacheDataUpdatedTask;
import com.android.launcher3.model.LoaderResults;
import com.android.launcher3.model.LoaderTask;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.model.PackageInstallStateChangedTask;
import com.android.launcher3.model.PackageUpdatedTask;
import com.android.launcher3.model.ShortcutsChangedTask;
import com.android.launcher3.model.UserLockStateChangedTask;
import com.android.launcher3.provider.LauncherDbUtils;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.Provider;
import com.android.launcher3.util.ViewOnDrawExecutor;
import com.android.launcher3.widget.WidgetListRowEntry;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;

public class LauncherModel extends BroadcastReceiver implements LauncherAppsCompat.OnAppsChangedCallbackCompat {
    private static final boolean DEBUG_RECEIVER = false;
    static final String TAG = "Launcher.Model";
    static final BgDataModel sBgDataModel = new BgDataModel();
    static final Handler sWorker = new Handler(sWorkerThread.getLooper());
    static final HandlerThread sWorkerThread = new HandlerThread("launcher-loader");
    final LauncherAppState mApp;
    private final AllAppsList mBgAllAppsList;
    WeakReference<Callbacks> mCallbacks;
    boolean mIsLoaderTaskRunning;
    LoaderTask mLoaderTask;
    final Object mLock = new Object();
    /* access modifiers changed from: private */
    public boolean mModelLoaded;
    private final Runnable mShortcutPermissionCheckRunnable = new Runnable() {
        public void run() {
            if (LauncherModel.this.mModelLoaded && DeepShortcutManager.getInstance(LauncherModel.this.mApp.getContext()).hasHostPermission() != LauncherModel.sBgDataModel.hasShortcutHostPermission) {
                LauncherModel.this.forceReload();
            }
        }
    };
    private final MainThreadExecutor mUiExecutor = new MainThreadExecutor();

    public interface CallbackTask {
        void execute(Callbacks callbacks);
    }

    public interface Callbacks {
        void bindAllApplications(ArrayList<AppInfo> arrayList);

        void bindAllWidgets(ArrayList<WidgetListRowEntry> arrayList);

        void bindAppInfosRemoved(ArrayList<AppInfo> arrayList);

        void bindAppsAdded(ArrayList<Long> arrayList, ArrayList<ItemInfo> arrayList2, ArrayList<ItemInfo> arrayList3);

        void bindAppsAddedOrUpdated(ArrayList<AppInfo> arrayList);

        void bindDeepShortcutMap(MultiHashMap<ComponentKey, String> multiHashMap);

        void bindItems(List<ItemInfo> list, boolean z);

        void bindPromiseAppProgressUpdated(PromiseAppInfo promiseAppInfo);

        void bindRestoreItemsChange(HashSet<ItemInfo> hashSet);

        void bindScreens(ArrayList<Long> arrayList);

        void bindShortcutsChanged(ArrayList<ShortcutInfo> arrayList, UserHandle userHandle);

        void bindWidgetsRestored(ArrayList<LauncherAppWidgetInfo> arrayList);

        void bindWorkspaceComponentsRemoved(ItemInfoMatcher itemInfoMatcher);

        void clearPendingBinds();

        void executeOnNextDraw(ViewOnDrawExecutor viewOnDrawExecutor);

        void finishBindingItems();

        void finishFirstPageBind(ViewOnDrawExecutor viewOnDrawExecutor);

        int getCurrentWorkspaceScreen();

        void onPageBoundSynchronously(int i);

        void rebindModel();

        void startBinding();
    }

    public interface ModelUpdateTask extends Runnable {
        void init(LauncherAppState launcherAppState, LauncherModel launcherModel, BgDataModel bgDataModel, AllAppsList allAppsList, Executor executor);
    }

    static {
        sWorkerThread.start();
    }

    public boolean isModelLoaded() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mModelLoaded && this.mLoaderTask == null;
        }
        return z;
    }

    LauncherModel(LauncherAppState app, IconCache iconCache, AppFilter appFilter) {
        this.mApp = app;
        this.mBgAllAppsList = new AllAppsList(iconCache, appFilter);
    }

    private static void runOnWorkerThread(Runnable r) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            sWorker.post(r);
        }
    }

    public void setPackageState(PackageInstallerCompat.PackageInstallInfo installInfo) {
        enqueueModelUpdateTask(new PackageInstallStateChangedTask(installInfo));
    }

    public void updateSessionDisplayInfo(String packageName) {
        HashSet<String> packages = new HashSet<>();
        packages.add(packageName);
        enqueueModelUpdateTask(new CacheDataUpdatedTask(2, Process.myUserHandle(), packages));
    }

    public void addAndBindAddedWorkspaceItems(List<Pair<ItemInfo, Object>> itemList) {
        enqueueModelUpdateTask(new AddWorkspaceItemsTask(itemList));
    }

    public ModelWriter getWriter(boolean hasVerticalHotseat, boolean verifyChanges) {
        return new ModelWriter(this.mApp.getContext(), this, sBgDataModel, hasVerticalHotseat, verifyChanges);
    }

    static void checkItemInfoLocked(long itemId, ItemInfo item, StackTraceElement[] stackTrace) {
        ItemInfo modelItem = (ItemInfo) sBgDataModel.itemsIdMap.get(itemId);
        if (modelItem != null && item != modelItem) {
            if ((modelItem instanceof ShortcutInfo) && (item instanceof ShortcutInfo)) {
                ShortcutInfo modelShortcut = (ShortcutInfo) modelItem;
                ShortcutInfo shortcut = (ShortcutInfo) item;
                if (modelShortcut.title.toString().equals(shortcut.title.toString()) && modelShortcut.intent.filterEquals(shortcut.intent) && modelShortcut.id == shortcut.id && modelShortcut.itemType == shortcut.itemType && modelShortcut.container == shortcut.container && modelShortcut.screenId == shortcut.screenId && modelShortcut.cellX == shortcut.cellX && modelShortcut.cellY == shortcut.cellY && modelShortcut.spanX == shortcut.spanX && modelShortcut.spanY == shortcut.spanY) {
                    return;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("item: ");
            sb.append(item != null ? item.toString() : "null");
            sb.append("modelItem: ");
            sb.append(modelItem != null ? modelItem.toString() : "null");
            sb.append("Error: ItemInfo passed to checkItemInfo doesn't match original");
            RuntimeException e = new RuntimeException(sb.toString());
            if (stackTrace != null) {
                e.setStackTrace(stackTrace);
            }
            throw e;
        }
    }

    static void checkItemInfo(final ItemInfo item) {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        final long itemId = item.id;
        runOnWorkerThread(new Runnable() {
            public void run() {
                synchronized (LauncherModel.sBgDataModel) {
                    LauncherModel.checkItemInfoLocked(itemId, item, stackTrace);
                }
            }
        });
    }

    public static void updateWorkspaceScreenOrder(Context context, ArrayList<Long> screens) {
        final ArrayList<Long> screensCopy = new ArrayList<>(screens);
        final ContentResolver cr = context.getContentResolver();
        final Uri uri = LauncherSettings.WorkspaceScreens.CONTENT_URI;
        Iterator<Long> iter = screensCopy.iterator();
        while (iter.hasNext()) {
            if (iter.next().longValue() < 0) {
                iter.remove();
            }
        }
        runOnWorkerThread(new Runnable() {
            public void run() {
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                ops.add(ContentProviderOperation.newDelete(uri).build());
                int count = screensCopy.size();
                for (int i = 0; i < count; i++) {
                    ContentValues v = new ContentValues();
                    v.put("_id", Long.valueOf(((Long) screensCopy.get(i)).longValue()));
                    v.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i));
                    ops.add(ContentProviderOperation.newInsert(uri).withValues(v).build());
                }
                try {
                    cr.applyBatch(LauncherProvider.AUTHORITY, ops);
                    synchronized (LauncherModel.sBgDataModel) {
                        LauncherModel.sBgDataModel.workspaceScreens.clear();
                        LauncherModel.sBgDataModel.workspaceScreens.addAll(screensCopy);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void initialize(Callbacks callbacks) {
        synchronized (this.mLock) {
            Preconditions.assertUIThread();
            this.mCallbacks = new WeakReference<>(callbacks);
        }
    }

    public void onPackageChanged(String packageName, UserHandle user) {
        enqueueModelUpdateTask(new PackageUpdatedTask(2, user, packageName));
    }

    public void onPackageRemoved(String packageName, UserHandle user) {
        onPackagesRemoved(user, packageName);
    }

    public void onPackagesRemoved(UserHandle user, String... packages) {
        enqueueModelUpdateTask(new PackageUpdatedTask(3, user, packages));
    }

    public void onPackageAdded(String packageName, UserHandle user) {
        enqueueModelUpdateTask(new PackageUpdatedTask(1, user, packageName));
    }

    public void onPackagesAvailable(String[] packageNames, UserHandle user, boolean replacing) {
        enqueueModelUpdateTask(new PackageUpdatedTask(2, user, packageNames));
    }

    public void onPackagesUnavailable(String[] packageNames, UserHandle user, boolean replacing) {
        if (!replacing) {
            enqueueModelUpdateTask(new PackageUpdatedTask(4, user, packageNames));
        }
    }

    public void onPackagesSuspended(String[] packageNames, UserHandle user) {
        enqueueModelUpdateTask(new PackageUpdatedTask(5, user, packageNames));
    }

    public void onPackagesUnsuspended(String[] packageNames, UserHandle user) {
        enqueueModelUpdateTask(new PackageUpdatedTask(6, user, packageNames));
    }

    public void onShortcutsChanged(String packageName, List<ShortcutInfoCompat> shortcuts, UserHandle user) {
        enqueueModelUpdateTask(new ShortcutsChangedTask(packageName, shortcuts, user, true));
    }

    public void updatePinnedShortcuts(String packageName, List<ShortcutInfoCompat> shortcuts, UserHandle user) {
        enqueueModelUpdateTask(new ShortcutsChangedTask(packageName, shortcuts, user, false));
    }

    public void onReceive(Context context, Intent intent) {
        UserHandle user;
        String action = intent.getAction();
        if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
            forceReload();
        } else if ("android.intent.action.MANAGED_PROFILE_ADDED".equals(action) || "android.intent.action.MANAGED_PROFILE_REMOVED".equals(action)) {
            UserManagerCompat.getInstance(context).enableAndResetCache();
            forceReload();
        } else if (("android.intent.action.MANAGED_PROFILE_AVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNLOCKED".equals(action)) && (user = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER")) != null) {
            if ("android.intent.action.MANAGED_PROFILE_AVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(action)) {
                enqueueModelUpdateTask(new PackageUpdatedTask(7, user, new String[0]));
            }
            if ("android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNLOCKED".equals(action)) {
                enqueueModelUpdateTask(new UserLockStateChangedTask(user));
            }
        }
    }

    public void forceReload() {
        synchronized (this.mLock) {
            stopLoader();
            this.mModelLoaded = false;
        }
        Callbacks callbacks = getCallback();
        if (callbacks != null) {
            startLoader(callbacks.getCurrentWorkspaceScreen());
        }
    }

    public boolean isCurrentCallbacks(Callbacks callbacks) {
        return this.mCallbacks != null && this.mCallbacks.get() == callbacks;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0055, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean startLoader(int r11) {
        /*
            r10 = this;
            r0 = 2
            com.android.launcher3.InstallShortcutReceiver.enableInstallQueue(r0)
            java.lang.Object r0 = r10.mLock
            monitor-enter(r0)
            java.lang.ref.WeakReference<com.android.launcher3.LauncherModel$Callbacks> r1 = r10.mCallbacks     // Catch:{ all -> 0x0057 }
            if (r1 == 0) goto L_0x0054
            java.lang.ref.WeakReference<com.android.launcher3.LauncherModel$Callbacks> r1 = r10.mCallbacks     // Catch:{ all -> 0x0057 }
            java.lang.Object r1 = r1.get()     // Catch:{ all -> 0x0057 }
            if (r1 == 0) goto L_0x0054
            java.lang.ref.WeakReference<com.android.launcher3.LauncherModel$Callbacks> r1 = r10.mCallbacks     // Catch:{ all -> 0x0057 }
            java.lang.Object r1 = r1.get()     // Catch:{ all -> 0x0057 }
            com.android.launcher3.LauncherModel$Callbacks r1 = (com.android.launcher3.LauncherModel.Callbacks) r1     // Catch:{ all -> 0x0057 }
            com.android.launcher3.MainThreadExecutor r2 = r10.mUiExecutor     // Catch:{ all -> 0x0057 }
            r1.getClass()     // Catch:{ all -> 0x0057 }
            com.android.launcher3.-$$Lambda$rGy4HMHlfF5mKCkPMTuda4Xd94I r3 = new com.android.launcher3.-$$Lambda$rGy4HMHlfF5mKCkPMTuda4Xd94I     // Catch:{ all -> 0x0057 }
            r3.<init>()     // Catch:{ all -> 0x0057 }
            r2.execute(r3)     // Catch:{ all -> 0x0057 }
            r10.stopLoader()     // Catch:{ all -> 0x0057 }
            com.android.launcher3.model.LoaderResults r2 = new com.android.launcher3.model.LoaderResults     // Catch:{ all -> 0x0057 }
            com.android.launcher3.LauncherAppState r5 = r10.mApp     // Catch:{ all -> 0x0057 }
            com.android.launcher3.model.BgDataModel r6 = sBgDataModel     // Catch:{ all -> 0x0057 }
            com.android.launcher3.AllAppsList r7 = r10.mBgAllAppsList     // Catch:{ all -> 0x0057 }
            java.lang.ref.WeakReference<com.android.launcher3.LauncherModel$Callbacks> r9 = r10.mCallbacks     // Catch:{ all -> 0x0057 }
            r4 = r2
            r8 = r11
            r4.<init>(r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0057 }
            boolean r3 = r10.mModelLoaded     // Catch:{ all -> 0x0057 }
            if (r3 == 0) goto L_0x0051
            boolean r3 = r10.mIsLoaderTaskRunning     // Catch:{ all -> 0x0057 }
            if (r3 != 0) goto L_0x0051
            r2.bindWorkspace()     // Catch:{ all -> 0x0057 }
            r2.bindAllApps()     // Catch:{ all -> 0x0057 }
            r2.bindDeepShortcuts()     // Catch:{ all -> 0x0057 }
            r2.bindWidgets()     // Catch:{ all -> 0x0057 }
            r3 = 1
            monitor-exit(r0)     // Catch:{ all -> 0x0057 }
            return r3
        L_0x0051:
            r10.startLoaderForResults(r2)     // Catch:{ all -> 0x0057 }
        L_0x0054:
            monitor-exit(r0)     // Catch:{ all -> 0x0057 }
            r0 = 0
            return r0
        L_0x0057:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0057 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherModel.startLoader(int):boolean");
    }

    public void stopLoader() {
        synchronized (this.mLock) {
            LoaderTask oldTask = this.mLoaderTask;
            this.mLoaderTask = null;
            if (oldTask != null) {
                oldTask.stopLocked();
            }
        }
    }

    public void startLoaderForResults(LoaderResults results) {
        synchronized (this.mLock) {
            stopLoader();
            this.mLoaderTask = new LoaderTask(this.mApp, this.mBgAllAppsList, sBgDataModel, results);
            runOnWorkerThread(this.mLoaderTask);
        }
    }

    public void startLoaderForResultsIfNotLoaded(LoaderResults results) {
        synchronized (this.mLock) {
            if (!isModelLoaded()) {
                Log.d(TAG, "Workspace not loaded, loading now");
                startLoaderForResults(results);
            }
        }
    }

    public static ArrayList<Long> loadWorkspaceScreensDb(Context context) {
        return LauncherDbUtils.getScreenIdsFromCursor(context.getContentResolver().query(LauncherSettings.WorkspaceScreens.CONTENT_URI, (String[]) null, (String) null, (String[]) null, LauncherSettings.WorkspaceScreens.SCREEN_RANK));
    }

    public void onInstallSessionCreated(final PackageInstallerCompat.PackageInstallInfo sessionInfo) {
        enqueueModelUpdateTask(new BaseModelUpdateTask() {
            public void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps) {
                apps.addPromiseApp(app.getContext(), sessionInfo);
                if (!apps.added.isEmpty()) {
                    final ArrayList<AppInfo> arrayList = new ArrayList<>(apps.added);
                    apps.added.clear();
                    scheduleCallbackTask(new CallbackTask() {
                        public void execute(Callbacks callbacks) {
                            callbacks.bindAppsAddedOrUpdated(arrayList);
                        }
                    });
                }
            }
        });
    }

    public class LoaderTransaction implements AutoCloseable {
        private final LoaderTask mTask;

        private LoaderTransaction(LoaderTask task) throws CancellationException {
            synchronized (LauncherModel.this.mLock) {
                if (LauncherModel.this.mLoaderTask == task) {
                    this.mTask = task;
                    LauncherModel.this.mIsLoaderTaskRunning = true;
                    boolean unused = LauncherModel.this.mModelLoaded = false;
                } else {
                    throw new CancellationException("Loader already stopped");
                }
            }
        }

        public void commit() {
            synchronized (LauncherModel.this.mLock) {
                boolean unused = LauncherModel.this.mModelLoaded = true;
            }
        }

        public void close() {
            synchronized (LauncherModel.this.mLock) {
                if (LauncherModel.this.mLoaderTask == this.mTask) {
                    LauncherModel.this.mLoaderTask = null;
                }
                LauncherModel.this.mIsLoaderTaskRunning = false;
            }
        }
    }

    public LoaderTransaction beginLoader(LoaderTask task) throws CancellationException {
        return new LoaderTransaction(task);
    }

    public void refreshShortcutsIfRequired() {
        if (Utilities.ATLEAST_NOUGAT_MR1) {
            sWorker.removeCallbacks(this.mShortcutPermissionCheckRunnable);
            sWorker.post(this.mShortcutPermissionCheckRunnable);
        }
    }

    public void onPackageIconsUpdated(HashSet<String> updatedPackages, UserHandle user) {
        enqueueModelUpdateTask(new CacheDataUpdatedTask(1, user, updatedPackages));
    }

    public void enqueueModelUpdateTask(ModelUpdateTask task) {
        task.init(this.mApp, this, sBgDataModel, this.mBgAllAppsList, this.mUiExecutor);
        runOnWorkerThread(task);
    }

    public void updateAndBindShortcutInfo(final ShortcutInfo si, final ShortcutInfoCompat info) {
        updateAndBindShortcutInfo(new Provider<ShortcutInfo>() {
            public ShortcutInfo get() {
                si.updateFromDeepShortcutInfo(info, LauncherModel.this.mApp.getContext());
                LauncherIcons li = LauncherIcons.obtain(LauncherModel.this.mApp.getContext());
                li.createShortcutIcon(info).applyTo((ItemInfoWithIcon) si);
                li.recycle();
                return si;
            }
        });
    }

    public void updateAndBindShortcutInfo(final Provider<ShortcutInfo> shortcutProvider) {
        enqueueModelUpdateTask(new BaseModelUpdateTask() {
            public void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps) {
                ShortcutInfo info = (ShortcutInfo) shortcutProvider.get();
                getModelWriter().updateItemInDatabase(info);
                ArrayList<ShortcutInfo> update = new ArrayList<>();
                update.add(info);
                bindUpdatedShortcuts(update, info.user);
            }
        });
    }

    public void refreshAndBindWidgetsAndShortcuts(@Nullable final PackageUserKey packageUser) {
        enqueueModelUpdateTask(new BaseModelUpdateTask() {
            public void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps) {
                dataModel.widgetsModel.update(app, packageUser);
                bindUpdatedWidgets(dataModel);
            }
        });
    }

    public void dumpState(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        if (args.length > 0 && TextUtils.equals(args[0], "--all")) {
            writer.println(prefix + "All apps list: size=" + this.mBgAllAppsList.data.size());
            Iterator<AppInfo> it = this.mBgAllAppsList.data.iterator();
            while (it.hasNext()) {
                AppInfo info = it.next();
                writer.println(prefix + "   title=\"" + info.title + "\" iconBitmap=" + info.iconBitmap + " componentName=" + info.componentName.getPackageName());
            }
        }
        sBgDataModel.dump(prefix, fd, writer, args);
    }

    public Callbacks getCallback() {
        if (this.mCallbacks != null) {
            return (Callbacks) this.mCallbacks.get();
        }
        return null;
    }

    public static Looper getWorkerLooper() {
        return sWorkerThread.getLooper();
    }

    public static void setWorkerPriority(int priority) {
        Process.setThreadPriority(sWorkerThread.getThreadId(), priority);
    }
}
