package com.szchoiceway.index;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcelable;
import android.os.Process;
import android.os.SystemClock;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import com.szchoiceway.index.InstallWidgetReceiver;
import com.szchoiceway.index.LauncherSettings;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LauncherModel extends BroadcastReceiver {
    public static final Comparator<ApplicationInfo> APP_INSTALL_TIME_COMPARATOR = new Comparator<ApplicationInfo>() {
        public final int compare(ApplicationInfo a, ApplicationInfo b) {
            if (a.firstInstallTime < b.firstInstallTime) {
                return 1;
            }
            if (a.firstInstallTime > b.firstInstallTime) {
                return -1;
            }
            return 0;
        }
    };
    static final boolean DEBUG_LOADERS = false;
    private static final int ITEMS_CHUNK = 6;
    private static final int MAIN_THREAD_BINDING_RUNNABLE = 1;
    private static final int MAIN_THREAD_NORMAL_RUNNABLE = 0;
    static final String TAG = "Launcher.Model";
    /* access modifiers changed from: private */
    public static LauncherApplication mApp = null;
    /* access modifiers changed from: private */
    public static int mCellCountX;
    /* access modifiers changed from: private */
    public static int mCellCountY;
    static final ArrayList<Runnable> mDeferredBindRunnables = new ArrayList<>();
    static final ArrayList<LauncherAppWidgetInfo> sBgAppWidgets = new ArrayList<>();
    static final HashMap<Object, byte[]> sBgDbIconCache = new HashMap<>();
    static final HashMap<Long, FolderInfo> sBgFolders = new HashMap<>();
    static final HashMap<Long, ItemInfo> sBgItemsIdMap = new HashMap<>();
    static final Object sBgLock = new Object();
    static final ArrayList<ItemInfo> sBgWorkspaceItems = new ArrayList<>();
    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());
    /* access modifiers changed from: private */
    public static final HandlerThread sWorkerThread = new HandlerThread("launcher-loader");
    /* access modifiers changed from: private */
    public int mAllAppsLoadDelay;
    /* access modifiers changed from: private */
    public boolean mAllAppsLoaded;
    private final boolean mAppsCanBeOnExternalStorage;
    /* access modifiers changed from: private */
    public int mBatchSize;
    /* access modifiers changed from: private */
    public AllAppsList mBgAllAppsList;
    /* access modifiers changed from: private */
    public WeakReference<Callbacks> mCallbacks;
    private Bitmap mDefaultIcon;
    /* access modifiers changed from: private */
    public volatile boolean mFlushingWorkerThread;
    /* access modifiers changed from: private */
    public DeferredHandler mHandler = new DeferredHandler();
    /* access modifiers changed from: private */
    public IconCache mIconCache;
    /* access modifiers changed from: private */
    public boolean mIsLoaderTaskRunning;
    /* access modifiers changed from: private */
    public LoaderTask mLoaderTask;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    protected int mPreviousConfigMcc;
    /* access modifiers changed from: private */
    public boolean mWorkspaceLoaded;

    public interface Callbacks {
        void bindAllApplications(ArrayList<ApplicationInfo> arrayList);

        void bindAppWidget(LauncherAppWidgetInfo launcherAppWidgetInfo);

        void bindAppsAdded(ArrayList<ApplicationInfo> arrayList);

        void bindAppsUpdated(ArrayList<ApplicationInfo> arrayList);

        void bindComponentsRemoved(ArrayList<String> arrayList, ArrayList<ApplicationInfo> arrayList2, boolean z);

        void bindFolders(HashMap<Long, FolderInfo> hashMap);

        void bindItems(ArrayList<ItemInfo> arrayList, int i, int i2);

        void bindPackagesUpdated(ArrayList<Object> arrayList);

        void bindSearchablesChanged();

        void finishBindingItems();

        int getCurrentWorkspaceScreen();

        boolean isAllAppsButtonRank(int i);

        boolean isAllAppsVisible();

        void onPageBoundSynchronously(int i);

        boolean setLoadOnResume();

        void startBinding();
    }

    static {
        sWorkerThread.start();
    }

    LauncherModel(LauncherApplication app, IconCache iconCache) {
        this.mAppsCanBeOnExternalStorage = !Environment.isExternalStorageEmulated();
        mApp = app;
        this.mBgAllAppsList = new AllAppsList(iconCache, mApp);
        this.mIconCache = iconCache;
        this.mDefaultIcon = Utilities.createIconBitmap(this.mIconCache.getFullResDefaultActivityIcon(), (Context) app);
        Resources res = app.getResources();
        this.mAllAppsLoadDelay = res.getInteger(R.integer.config_allAppsBatchLoadDelay);
        this.mBatchSize = res.getInteger(R.integer.config_allAppsBatchSize);
        this.mPreviousConfigMcc = res.getConfiguration().mcc;
    }

    private void runOnMainThread(Runnable r) {
        runOnMainThread(r, 0);
    }

    /* access modifiers changed from: private */
    public void runOnMainThread(Runnable r, int type) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            this.mHandler.post(r);
        } else {
            r.run();
        }
    }

    private static void runOnWorkerThread(Runnable r) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            r.run();
        } else {
            sWorker.post(r);
        }
    }

    public Bitmap getFallbackIcon() {
        return Bitmap.createBitmap(this.mDefaultIcon);
    }

    public void unbindItemInfosAndClearQueuedBindRunnables() {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            throw new RuntimeException("Expected unbindLauncherItemInfos() to be called from the main thread");
        }
        mDeferredBindRunnables.clear();
        this.mHandler.cancelAllRunnablesOfType(1);
        unbindWorkspaceItemsOnMainThread();
    }

    /* access modifiers changed from: package-private */
    public void unbindWorkspaceItemsOnMainThread() {
        final ArrayList<ItemInfo> tmpWorkspaceItems = new ArrayList<>();
        final ArrayList<ItemInfo> tmpAppWidgets = new ArrayList<>();
        synchronized (sBgLock) {
            tmpWorkspaceItems.addAll(sBgWorkspaceItems);
            tmpAppWidgets.addAll(sBgAppWidgets);
        }
        runOnMainThread(new Runnable() {
            public void run() {
                Iterator it = tmpWorkspaceItems.iterator();
                while (it.hasNext()) {
                    ((ItemInfo) it.next()).unbind();
                }
                Iterator it2 = tmpAppWidgets.iterator();
                while (it2.hasNext()) {
                    ((ItemInfo) it2.next()).unbind();
                }
            }
        });
    }

    static void addOrMoveItemInDatabase(Context context, ItemInfo item, long container, int screen, int cellX, int cellY) {
        if (item.container == -1) {
            addItemToDatabase(context, item, container, screen, cellX, cellY, false);
        } else {
            moveItemInDatabase(context, item, container, screen, cellX, cellY);
        }
    }

    static void checkItemInfoLocked(long itemId, ItemInfo item, StackTraceElement[] stackTrace) {
        ItemInfo modelItem = sBgItemsIdMap.get(Long.valueOf(itemId));
        if (modelItem != null && item != modelItem) {
            if ((modelItem instanceof ShortcutInfo) && (item instanceof ShortcutInfo)) {
                ShortcutInfo modelShortcut = (ShortcutInfo) modelItem;
                ShortcutInfo shortcut = (ShortcutInfo) item;
                if (modelShortcut.title.toString().equals(shortcut.title.toString()) && modelShortcut.intent.filterEquals(shortcut.intent) && modelShortcut.id == shortcut.id && modelShortcut.itemType == shortcut.itemType && modelShortcut.container == shortcut.container && modelShortcut.screen == shortcut.screen && modelShortcut.cellX == shortcut.cellX && modelShortcut.cellY == shortcut.cellY && modelShortcut.spanX == shortcut.spanX && modelShortcut.spanY == shortcut.spanY) {
                    if (modelShortcut.dropPos != null || shortcut.dropPos != null) {
                        if (modelShortcut.dropPos != null && shortcut.dropPos != null && modelShortcut.dropPos[0] == shortcut.dropPos[0] && modelShortcut.dropPos[1] == shortcut.dropPos[1]) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
            RuntimeException e = new RuntimeException("item: " + (item != null ? item.toString() : "null") + "modelItem: " + (modelItem != null ? modelItem.toString() : "null") + "Error: ItemInfo passed to checkItemInfo doesn't match original");
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
                synchronized (LauncherModel.sBgLock) {
                    LauncherModel.checkItemInfoLocked(itemId, item, stackTrace);
                }
            }
        });
    }

    static void updateItemInDatabaseHelper(Context context, ContentValues values, ItemInfo item, String callingFunction) {
        final long itemId = item.id;
        final Uri uri = LauncherSettings.Favorites.getContentUri(itemId, false);
        final ContentResolver cr = context.getContentResolver();
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        final ContentValues contentValues = values;
        final ItemInfo itemInfo = item;
        runOnWorkerThread(new Runnable() {
            public void run() {
                cr.update(uri, contentValues, (String) null, (String[]) null);
                synchronized (LauncherModel.sBgLock) {
                    LauncherModel.checkItemInfoLocked(itemId, itemInfo, stackTrace);
                    if (!(itemInfo.container == -100 || itemInfo.container == -101 || LauncherModel.sBgFolders.containsKey(Long.valueOf(itemInfo.container)))) {
                        Log.e(LauncherModel.TAG, "item: " + itemInfo + " container being set to: " + itemInfo.container + ", not in the list of folders");
                        Launcher.dumpDebugLogsToConsole();
                    }
                    ItemInfo modelItem = LauncherModel.sBgItemsIdMap.get(Long.valueOf(itemId));
                    if (modelItem.container == -100 || modelItem.container == -101) {
                        switch (modelItem.itemType) {
                            case 0:
                            case 1:
                            case 2:
                                if (!LauncherModel.sBgWorkspaceItems.contains(modelItem)) {
                                    LauncherModel.sBgWorkspaceItems.add(modelItem);
                                    break;
                                }
                                break;
                        }
                    } else {
                        LauncherModel.sBgWorkspaceItems.remove(modelItem);
                    }
                }
            }
        });
    }

    public void flushWorkerThread() {
        this.mFlushingWorkerThread = true;
        Runnable waiter = new Runnable() {
            public void run() {
                synchronized (this) {
                    notifyAll();
                    boolean unused = LauncherModel.this.mFlushingWorkerThread = false;
                }
            }
        };
        synchronized (waiter) {
            runOnWorkerThread(waiter);
            if (this.mLoaderTask != null) {
                synchronized (this.mLoaderTask) {
                    this.mLoaderTask.notify();
                }
            }
            boolean success = false;
            while (!success) {
                try {
                    waiter.wait();
                    success = true;
                } catch (InterruptedException e) {
                }
            }
        }
    }

    static void moveItemInDatabase(Context context, ItemInfo item, long container, int screen, int cellX, int cellY) {
        String transaction = "DbDebug    Modify item (" + item.title + ") in db, id: " + item.id + " (" + item.container + ", " + item.screen + ", " + item.cellX + ", " + item.cellY + ") --> " + "(" + container + ", " + screen + ", " + cellX + ", " + cellY + ")";
        Launcher.sDumpLogs.add(transaction);
        Log.d(TAG, transaction);
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        if (!(context instanceof Launcher) || screen >= 0 || container != -101) {
            item.screen = screen;
        } else {
            item.screen = ((Launcher) context).getHotseat().getOrderInHotseat(cellX, cellY);
        }
        ContentValues values = new ContentValues();
        values.put("container", Long.valueOf(item.container));
        values.put("cellX", Integer.valueOf(item.cellX));
        values.put("cellY", Integer.valueOf(item.cellY));
        values.put("screen", Integer.valueOf(item.screen));
        updateItemInDatabaseHelper(context, values, item, "moveItemInDatabase");
    }

    static void modifyItemInDatabase(Context context, ItemInfo item, long container, int screen, int cellX, int cellY, int spanX, int spanY) {
        String transaction = "DbDebug    Modify item (" + item.title + ") in db, id: " + item.id + " (" + item.container + ", " + item.screen + ", " + item.cellX + ", " + item.cellY + ") --> " + "(" + container + ", " + screen + ", " + cellX + ", " + cellY + ")";
        Launcher.sDumpLogs.add(transaction);
        Log.d(TAG, transaction);
        item.cellX = cellX;
        item.cellY = cellY;
        item.spanX = spanX;
        item.spanY = spanY;
        if (!(context instanceof Launcher) || screen >= 0 || container != -101) {
            item.screen = screen;
        } else {
            item.screen = ((Launcher) context).getHotseat().getOrderInHotseat(cellX, cellY);
        }
        ContentValues values = new ContentValues();
        values.put("container", Long.valueOf(item.container));
        values.put("cellX", Integer.valueOf(item.cellX));
        values.put("cellY", Integer.valueOf(item.cellY));
        values.put("spanX", Integer.valueOf(item.spanX));
        values.put("spanY", Integer.valueOf(item.spanY));
        values.put("screen", Integer.valueOf(item.screen));
        updateItemInDatabaseHelper(context, values, item, "modifyItemInDatabase");
    }

    static void updateItemInDatabase(Context context, ItemInfo item) {
        ContentValues values = new ContentValues();
        item.onAddToDatabase(values);
        item.updateValuesWithCoordinates(values, item.cellX, item.cellY);
        updateItemInDatabaseHelper(context, values, item, "updateItemInDatabase");
    }

    static boolean shortcutExists(Context context, String title, Intent intent) {
        Cursor c = context.getContentResolver().query(LauncherSettings.Favorites.CONTENT_URI, new String[]{LauncherSettings.BaseLauncherColumns.TITLE, LauncherSettings.BaseLauncherColumns.INTENT}, "title=? and intent=?", new String[]{title, intent.toUri(0)}, (String) null);
        try {
            return c.moveToFirst();
        } finally {
            c.close();
        }
    }

    static ArrayList<ItemInfo> getItemsInLocalCoordinates(Context context) {
        ArrayList<ItemInfo> items = new ArrayList<>();
        Cursor c = context.getContentResolver().query(LauncherSettings.Favorites.CONTENT_URI, new String[]{LauncherSettings.BaseLauncherColumns.ITEM_TYPE, "container", "screen", "cellX", "cellY", "spanX", "spanY"}, (String) null, (String[]) null, (String) null);
        int itemTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ITEM_TYPE);
        int containerIndex = c.getColumnIndexOrThrow("container");
        int screenIndex = c.getColumnIndexOrThrow("screen");
        int cellXIndex = c.getColumnIndexOrThrow("cellX");
        int cellYIndex = c.getColumnIndexOrThrow("cellY");
        int spanXIndex = c.getColumnIndexOrThrow("spanX");
        int spanYIndex = c.getColumnIndexOrThrow("spanY");
        while (c.moveToNext()) {
            try {
                ItemInfo item = new ItemInfo();
                item.cellX = c.getInt(cellXIndex);
                item.cellY = c.getInt(cellYIndex);
                item.spanX = c.getInt(spanXIndex);
                item.spanY = c.getInt(spanYIndex);
                item.container = (long) c.getInt(containerIndex);
                item.itemType = c.getInt(itemTypeIndex);
                item.screen = c.getInt(screenIndex);
                items.add(item);
            } catch (Exception e) {
                items.clear();
            } finally {
                c.close();
            }
        }
        return items;
    }

    /* access modifiers changed from: package-private */
    public FolderInfo getFolderById(Context context, HashMap<Long, FolderInfo> folderList, long id) {
        Cursor c = context.getContentResolver().query(LauncherSettings.Favorites.CONTENT_URI, (String[]) null, "_id=? and (itemType=? or itemType=?)", new String[]{String.valueOf(id), String.valueOf(2)}, (String) null);
        try {
            if (c.moveToFirst()) {
                int itemTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ITEM_TYPE);
                int titleIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.TITLE);
                int containerIndex = c.getColumnIndexOrThrow("container");
                int screenIndex = c.getColumnIndexOrThrow("screen");
                int cellXIndex = c.getColumnIndexOrThrow("cellX");
                int cellYIndex = c.getColumnIndexOrThrow("cellY");
                FolderInfo folderInfo = null;
                switch (c.getInt(itemTypeIndex)) {
                    case 2:
                        folderInfo = findOrMakeFolder(folderList, id);
                        break;
                }
                folderInfo.title = c.getString(titleIndex);
                folderInfo.id = id;
                folderInfo.container = (long) c.getInt(containerIndex);
                folderInfo.screen = c.getInt(screenIndex);
                folderInfo.cellX = c.getInt(cellXIndex);
                folderInfo.cellY = c.getInt(cellYIndex);
                return folderInfo;
            }
            c.close();
            return null;
        } finally {
            c.close();
        }
    }

    static void addItemToDatabase(Context context, ItemInfo item, long container, int screen, int cellX, int cellY, boolean notify) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        if (!(context instanceof Launcher) || screen >= 0 || container != -101) {
            item.screen = screen;
        } else {
            item.screen = ((Launcher) context).getHotseat().getOrderInHotseat(cellX, cellY);
        }
        final ContentValues values = new ContentValues();
        final ContentResolver cr = context.getContentResolver();
        item.onAddToDatabase(values);
        item.id = ((LauncherApplication) context.getApplicationContext()).getLauncherProvider().generateNewId();
        values.put("_id", Long.valueOf(item.id));
        item.updateValuesWithCoordinates(values, item.cellX, item.cellY);
        final ItemInfo itemInfo = item;
        final long j = container;
        final int i = screen;
        final int i2 = cellX;
        final int i3 = cellY;
        final boolean z = notify;
        runOnWorkerThread(new Runnable() {
            public void run() {
                String transaction = "DbDebug    Add item (" + itemInfo.title + ") to db, id: " + itemInfo.id + " (" + j + ", " + i + ", " + i2 + ", " + i3 + ")";
                Launcher.sDumpLogs.add(transaction);
                Log.d(LauncherModel.TAG, transaction);
                cr.insert(z ? LauncherSettings.Favorites.CONTENT_URI : LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION, values);
                synchronized (LauncherModel.sBgLock) {
                    LauncherModel.checkItemInfoLocked(itemInfo.id, itemInfo, (StackTraceElement[]) null);
                    LauncherModel.sBgItemsIdMap.put(Long.valueOf(itemInfo.id), itemInfo);
                    switch (itemInfo.itemType) {
                        case 2:
                            LauncherModel.sBgFolders.put(Long.valueOf(itemInfo.id), (FolderInfo) itemInfo);
                        case 0:
                        case 1:
                            if (itemInfo.container != -100 && itemInfo.container != -101) {
                                if (!LauncherModel.sBgFolders.containsKey(Long.valueOf(itemInfo.container))) {
                                    Log.e(LauncherModel.TAG, "adding item: " + itemInfo + " to a folder that " + " doesn't exist");
                                    Launcher.dumpDebugLogsToConsole();
                                    break;
                                }
                            } else {
                                LauncherModel.sBgWorkspaceItems.add(itemInfo);
                                break;
                            }
                            break;
                        case 4:
                            LauncherModel.sBgAppWidgets.add((LauncherAppWidgetInfo) itemInfo);
                            break;
                    }
                }
            }
        });
    }

    static int getCellLayoutChildId(long container, int screen, int localCellX, int localCellY, int spanX, int spanY) {
        return ((((int) container) & 255) << 24) | ((screen & 255) << 16) | ((localCellX & 255) << 8) | (localCellY & 255);
    }

    static int getCellCountX() {
        return mCellCountX;
    }

    static int getCellCountY() {
        return mCellCountY;
    }

    static void updateWorkspaceLayoutCells(int shortAxisCellCount, int longAxisCellCount) {
        mCellCountX = shortAxisCellCount;
        mCellCountY = longAxisCellCount;
    }

    static void deleteItemFromDatabase(Context context, final ItemInfo item) {
        final ContentResolver cr = context.getContentResolver();
        final Uri uriToDelete = LauncherSettings.Favorites.getContentUri(item.id, false);
        runOnWorkerThread(new Runnable() {
            public void run() {
                String transaction = "DbDebug    Delete item (" + item.title + ") from db, id: " + item.id + " (" + item.container + ", " + item.screen + ", " + item.cellX + ", " + item.cellY + ")";
                Launcher.sDumpLogs.add(transaction);
                Log.d(LauncherModel.TAG, transaction);
                cr.delete(uriToDelete, (String) null, (String[]) null);
                synchronized (LauncherModel.sBgLock) {
                    switch (item.itemType) {
                        case 0:
                        case 1:
                            LauncherModel.sBgWorkspaceItems.remove(item);
                            break;
                        case 2:
                            LauncherModel.sBgFolders.remove(Long.valueOf(item.id));
                            for (ItemInfo info : LauncherModel.sBgItemsIdMap.values()) {
                                if (info.container == item.id) {
                                    Log.e(LauncherModel.TAG, "deleting a folder (" + item + ") which still " + "contains items (" + info + ")");
                                    Launcher.dumpDebugLogsToConsole();
                                }
                            }
                            LauncherModel.sBgWorkspaceItems.remove(item);
                            break;
                        case 4:
                            LauncherModel.sBgAppWidgets.remove((LauncherAppWidgetInfo) item);
                            break;
                    }
                    LauncherModel.sBgItemsIdMap.remove(Long.valueOf(item.id));
                    LauncherModel.sBgDbIconCache.remove(item);
                }
            }
        });
    }

    static void deleteFolderContentsFromDatabase(Context context, final FolderInfo info) {
        final ContentResolver cr = context.getContentResolver();
        runOnWorkerThread(new Runnable() {
            public void run() {
                cr.delete(LauncherSettings.Favorites.getContentUri(info.id, false), (String) null, (String[]) null);
                synchronized (LauncherModel.sBgLock) {
                    LauncherModel.sBgItemsIdMap.remove(Long.valueOf(info.id));
                    LauncherModel.sBgFolders.remove(Long.valueOf(info.id));
                    LauncherModel.sBgDbIconCache.remove(info);
                    LauncherModel.sBgWorkspaceItems.remove(info);
                }
                cr.delete(LauncherSettings.Favorites.CONTENT_URI_NO_NOTIFICATION, "container=" + info.id, (String[]) null);
                synchronized (LauncherModel.sBgLock) {
                    Iterator<ShortcutInfo> it = info.contents.iterator();
                    while (it.hasNext()) {
                        ItemInfo childInfo = it.next();
                        LauncherModel.sBgItemsIdMap.remove(Long.valueOf(childInfo.id));
                        LauncherModel.sBgDbIconCache.remove(childInfo);
                    }
                }
            }
        });
    }

    public void initialize(Callbacks callbacks) {
        synchronized (this.mLock) {
            this.mCallbacks = new WeakReference<>(callbacks);
        }
    }

    public void onReceive(Context context, Intent intent) {
        Callbacks callbacks;
        String action = intent.getAction();
        if ("android.intent.action.PACKAGE_CHANGED".equals(action) || "android.intent.action.PACKAGE_REMOVED".equals(action) || "android.intent.action.PACKAGE_ADDED".equals(action)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            boolean replacing = intent.getBooleanExtra("android.intent.extra.REPLACING", false);
            int op = 0;
            if (packageName != null && packageName.length() != 0) {
                if ("android.intent.action.PACKAGE_CHANGED".equals(action)) {
                    op = 2;
                } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                    if (!replacing) {
                        op = 3;
                    }
                } else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                    op = !replacing ? 1 : 2;
                }
                if (op != 0) {
                    enqueuePackageUpdated(new PackageUpdatedTask(op, new String[]{packageName}));
                }
            }
        } else if (IntentCompat.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action)) {
            enqueuePackageUpdated(new PackageUpdatedTask(1, intent.getStringArrayExtra(IntentCompat.EXTRA_CHANGED_PACKAGE_LIST)));
            startLoaderFromBackground();
        } else if (IntentCompat.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
            enqueuePackageUpdated(new PackageUpdatedTask(4, intent.getStringArrayExtra(IntentCompat.EXTRA_CHANGED_PACKAGE_LIST)));
        } else if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
            forceReload();
        } else if ("android.intent.action.CONFIGURATION_CHANGED".equals(action)) {
            Configuration currentConfig = context.getResources().getConfiguration();
            if (this.mPreviousConfigMcc != currentConfig.mcc) {
                Log.d(TAG, "Reload apps on config change. curr_mcc:" + currentConfig.mcc + " prevmcc:" + this.mPreviousConfigMcc);
                forceReload();
            }
            this.mPreviousConfigMcc = currentConfig.mcc;
        } else if (("android.search.action.GLOBAL_SEARCH_ACTIVITY_CHANGED".equals(action) || "android.search.action.SEARCHABLES_CHANGED".equals(action)) && this.mCallbacks != null && (callbacks = (Callbacks) this.mCallbacks.get()) != null) {
            callbacks.bindSearchablesChanged();
        }
    }

    private void forceReload() {
        resetLoadedState(true, true);
        startLoaderFromBackground();
    }

    public void resetLoadedState(boolean resetAllAppsLoaded, boolean resetWorkspaceLoaded) {
        synchronized (this.mLock) {
            stopLoaderLocked();
            if (resetAllAppsLoaded) {
                this.mAllAppsLoaded = false;
            }
            if (resetWorkspaceLoaded) {
                this.mWorkspaceLoaded = false;
            }
        }
    }

    public void startLoaderFromBackground() {
        Callbacks callbacks;
        boolean runLoader = false;
        if (!(this.mCallbacks == null || (callbacks = (Callbacks) this.mCallbacks.get()) == null || callbacks.setLoadOnResume())) {
            runLoader = true;
        }
        if (runLoader) {
            startLoader(false, -1);
        }
    }

    private boolean stopLoaderLocked() {
        boolean isLaunching = false;
        LoaderTask oldTask = this.mLoaderTask;
        if (oldTask != null) {
            if (oldTask.isLaunching()) {
                isLaunching = true;
            }
            oldTask.stopLocked();
        }
        return isLaunching;
    }

    public void startLoader(boolean isLaunching, int synchronousBindPage) {
        synchronized (this.mLock) {
            mDeferredBindRunnables.clear();
            if (!(this.mCallbacks == null || this.mCallbacks.get() == null)) {
                this.mLoaderTask = new LoaderTask(mApp, isLaunching || stopLoaderLocked());
                if (synchronousBindPage <= -1 || !this.mAllAppsLoaded || !this.mWorkspaceLoaded) {
                    sWorkerThread.setPriority(5);
                    sWorker.post(this.mLoaderTask);
                } else {
                    this.mLoaderTask.runBindSynchronousPage(synchronousBindPage);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void bindRemainingSynchronousPages() {
        if (!mDeferredBindRunnables.isEmpty()) {
            Iterator<Runnable> it = mDeferredBindRunnables.iterator();
            while (it.hasNext()) {
                this.mHandler.post(it.next(), 1);
            }
            mDeferredBindRunnables.clear();
        }
    }

    public void stopLoader() {
        synchronized (this.mLock) {
            if (this.mLoaderTask != null) {
                this.mLoaderTask.stopLocked();
            }
        }
    }

    public boolean isAllAppsLoaded() {
        return this.mAllAppsLoaded;
    }

    /* access modifiers changed from: package-private */
    public boolean isLoadingWorkspace() {
        synchronized (this.mLock) {
            if (this.mLoaderTask == null) {
                return false;
            }
            boolean isLoadingWorkspace = this.mLoaderTask.isLoadingWorkspace();
            return isLoadingWorkspace;
        }
    }

    private class LoaderTask implements Runnable {
        private Context mContext;
        private boolean mIsLaunching;
        /* access modifiers changed from: private */
        public boolean mIsLoadingAndBindingWorkspace;
        private HashMap<Object, CharSequence> mLabelCache = new HashMap<>();
        /* access modifiers changed from: private */
        public boolean mLoadAndBindStepFinished;
        private boolean mStopped;

        LoaderTask(Context context, boolean isLaunching) {
            this.mContext = context;
            this.mIsLaunching = isLaunching;
        }

        /* access modifiers changed from: package-private */
        public boolean isLaunching() {
            return this.mIsLaunching;
        }

        /* access modifiers changed from: package-private */
        public boolean isLoadingWorkspace() {
            return this.mIsLoadingAndBindingWorkspace;
        }

        private void loadAndBindWorkspace() {
            this.mIsLoadingAndBindingWorkspace = true;
            if (!LauncherModel.this.mWorkspaceLoaded) {
                loadWorkspace();
                synchronized (this) {
                    if (!this.mStopped) {
                        boolean unused = LauncherModel.this.mWorkspaceLoaded = true;
                    } else {
                        return;
                    }
                }
            }
            bindWorkspace(-1);
        }

        private void waitForIdle() {
            synchronized (this) {
                LauncherModel.this.mHandler.postIdle(new Runnable() {
                    public void run() {
                        synchronized (LoaderTask.this) {
                            boolean unused = LoaderTask.this.mLoadAndBindStepFinished = true;
                            LoaderTask.this.notify();
                        }
                    }
                });
                while (!this.mStopped && !this.mLoadAndBindStepFinished && !LauncherModel.this.mFlushingWorkerThread) {
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void runBindSynchronousPage(int synchronousBindPage) {
            if (synchronousBindPage < 0) {
                throw new RuntimeException("Should not call runBindSynchronousPage() without valid page index");
            } else if (!LauncherModel.this.mAllAppsLoaded || !LauncherModel.this.mWorkspaceLoaded) {
                throw new RuntimeException("Expecting AllApps and Workspace to be loaded");
            } else {
                synchronized (LauncherModel.this.mLock) {
                    if (LauncherModel.this.mIsLoaderTaskRunning) {
                        throw new RuntimeException("Error! Background loading is already running");
                    }
                }
                LauncherModel.this.mHandler.flush();
                bindWorkspace(synchronousBindPage);
                onlyBindAllApps();
            }
        }

        public void run() {
            boolean loadWorkspaceFirst = true;
            int i = 0;
            synchronized (LauncherModel.this.mLock) {
                boolean unused = LauncherModel.this.mIsLoaderTaskRunning = true;
            }
            Callbacks cbk = (Callbacks) LauncherModel.this.mCallbacks.get();
            if (cbk != null && cbk.isAllAppsVisible()) {
                loadWorkspaceFirst = false;
            }
            synchronized (LauncherModel.this.mLock) {
                if (!this.mIsLaunching) {
                    i = 10;
                }
                Process.setThreadPriority(i);
            }
            if (loadWorkspaceFirst) {
                loadAndBindWorkspace();
            } else {
                loadAndBindAllApps();
            }
            if (!this.mStopped) {
                synchronized (LauncherModel.this.mLock) {
                    if (this.mIsLaunching) {
                        Process.setThreadPriority(10);
                    }
                }
                waitForIdle();
                if (loadWorkspaceFirst) {
                    loadAndBindAllApps();
                } else {
                    loadAndBindWorkspace();
                }
                synchronized (LauncherModel.this.mLock) {
                    Process.setThreadPriority(0);
                }
            }
            synchronized (LauncherModel.sBgLock) {
                for (Object key : LauncherModel.sBgDbIconCache.keySet()) {
                    LauncherModel.this.updateSavedIcon(this.mContext, (ShortcutInfo) key, LauncherModel.sBgDbIconCache.get(key));
                }
                LauncherModel.sBgDbIconCache.clear();
            }
            this.mContext = null;
            synchronized (LauncherModel.this.mLock) {
                if (LauncherModel.this.mLoaderTask == this) {
                    LoaderTask unused2 = LauncherModel.this.mLoaderTask = null;
                }
                boolean unused3 = LauncherModel.this.mIsLoaderTaskRunning = false;
            }
        }

        public void stopLocked() {
            synchronized (this) {
                this.mStopped = true;
                notify();
            }
        }

        /* access modifiers changed from: package-private */
        public Callbacks tryGetCallbacks(Callbacks oldCallbacks) {
            synchronized (LauncherModel.this.mLock) {
                if (this.mStopped) {
                    return null;
                }
                if (LauncherModel.this.mCallbacks == null) {
                    return null;
                }
                Callbacks callbacks = (Callbacks) LauncherModel.this.mCallbacks.get();
                if (callbacks != oldCallbacks) {
                    return null;
                }
                if (callbacks != null) {
                    return callbacks;
                }
                Log.w(LauncherModel.TAG, "no mCallbacks");
                return null;
            }
        }

        private boolean checkItemPlacement(ItemInfo[][][] occupied, ItemInfo item) {
            int containerIndex = item.screen;
            if (item.container == -101) {
                if (LauncherModel.this.mCallbacks == null || ((Callbacks) LauncherModel.this.mCallbacks.get()).isAllAppsButtonRank(item.screen)) {
                    return false;
                }
                if (occupied[5][item.screen][0] != null) {
                    Log.e(LauncherModel.TAG, "Error loading shortcut into hotseat " + item + " into position (" + item.screen + ":" + item.cellX + "," + item.cellY + ") occupied by " + occupied[5][item.screen][0]);
                    return false;
                }
                occupied[5][item.screen][0] = item;
                return true;
            } else if (item.container != -100) {
                return true;
            } else {
                for (int x = item.cellX; x < item.cellX + item.spanX; x++) {
                    for (int y = item.cellY; y < item.cellY + item.spanY; y++) {
                        if (occupied[containerIndex][x][y] != null) {
                            Log.e(LauncherModel.TAG, "Error loading shortcut " + item + " into cell (" + containerIndex + "-" + item.screen + ":" + x + "," + y + ") occupied by " + occupied[containerIndex][x][y]);
                            return false;
                        }
                    }
                }
                for (int x2 = item.cellX; x2 < item.cellX + item.spanX; x2++) {
                    for (int y2 = item.cellY; y2 < item.cellY + item.spanY; y2++) {
                        occupied[containerIndex][x2][y2] = item;
                    }
                }
                return true;
            }
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void loadWorkspace() {
            /*
                r52 = this;
                r48 = 0
                r0 = r52
                android.content.Context r14 = r0.mContext
                android.content.ContentResolver r4 = r14.getContentResolver()
                android.content.pm.PackageManager r41 = r14.getPackageManager()
                android.appwidget.AppWidgetManager r50 = android.appwidget.AppWidgetManager.getInstance(r14)
                boolean r36 = r41.isSafeMode()
                com.szchoiceway.index.LauncherApplication r5 = com.szchoiceway.index.LauncherModel.mApp
                com.szchoiceway.index.LauncherProvider r5 = r5.getLauncherProvider()
                r6 = 0
                r5.loadDefaultFavoritesIfNecessary(r6)
                java.lang.Object r51 = com.szchoiceway.index.LauncherModel.sBgLock
                monitor-enter(r51)
                java.util.ArrayList<com.szchoiceway.index.ItemInfo> r5 = com.szchoiceway.index.LauncherModel.sBgWorkspaceItems     // Catch:{ all -> 0x017b }
                r5.clear()     // Catch:{ all -> 0x017b }
                java.util.ArrayList<com.szchoiceway.index.LauncherAppWidgetInfo> r5 = com.szchoiceway.index.LauncherModel.sBgAppWidgets     // Catch:{ all -> 0x017b }
                r5.clear()     // Catch:{ all -> 0x017b }
                java.util.HashMap<java.lang.Long, com.szchoiceway.index.FolderInfo> r5 = com.szchoiceway.index.LauncherModel.sBgFolders     // Catch:{ all -> 0x017b }
                r5.clear()     // Catch:{ all -> 0x017b }
                java.util.HashMap<java.lang.Long, com.szchoiceway.index.ItemInfo> r5 = com.szchoiceway.index.LauncherModel.sBgItemsIdMap     // Catch:{ all -> 0x017b }
                r5.clear()     // Catch:{ all -> 0x017b }
                java.util.HashMap<java.lang.Object, byte[]> r5 = com.szchoiceway.index.LauncherModel.sBgDbIconCache     // Catch:{ all -> 0x017b }
                r5.clear()     // Catch:{ all -> 0x017b }
                java.util.ArrayList r39 = new java.util.ArrayList     // Catch:{ all -> 0x017b }
                r39.<init>()     // Catch:{ all -> 0x017b }
                android.net.Uri r5 = com.szchoiceway.index.LauncherSettings.Favorites.CONTENT_URI     // Catch:{ all -> 0x017b }
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 0
                android.database.Cursor r9 = r4.query(r5, r6, r7, r8, r9)     // Catch:{ all -> 0x017b }
                r5 = 6
                int r6 = com.szchoiceway.index.LauncherModel.mCellCountX     // Catch:{ all -> 0x017b }
                int r6 = r6 + 1
                int r8 = com.szchoiceway.index.LauncherModel.mCellCountY     // Catch:{ all -> 0x017b }
                int r8 = r8 + 1
                int[] r5 = new int[]{r5, r6, r8}     // Catch:{ all -> 0x017b }
                java.lang.Class<com.szchoiceway.index.ItemInfo> r6 = com.szchoiceway.index.ItemInfo.class
                java.lang.Object r43 = java.lang.reflect.Array.newInstance(r6, r5)     // Catch:{ all -> 0x017b }
                com.szchoiceway.index.ItemInfo[][][] r43 = (com.szchoiceway.index.ItemInfo[][][]) r43     // Catch:{ all -> 0x017b }
                java.lang.String r5 = "_id"
                int r32 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "intent"
                int r35 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "title"
                int r11 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "iconType"
                int r15 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "icon"
                int r10 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "iconPackage"
                int r16 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "iconResource"
                int r17 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "container"
                int r27 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "itemType"
                int r38 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "appWidgetId"
                int r21 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "screen"
                int r45 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "cellX"
                int r23 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "cellY"
                int r24 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "spanX"
                int r46 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
                java.lang.String r5 = "spanY"
                int r47 = r9.getColumnIndexOrThrow(r5)     // Catch:{ all -> 0x0176 }
            L_0x00c0:
                r0 = r52
                boolean r5 = r0.mStopped     // Catch:{ all -> 0x0176 }
                if (r5 != 0) goto L_0x0378
                boolean r5 = r9.moveToNext()     // Catch:{ all -> 0x0176 }
                if (r5 == 0) goto L_0x0378
                r0 = r38
                int r37 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                switch(r37) {
                    case 0: goto L_0x00d6;
                    case 1: goto L_0x00d6;
                    case 2: goto L_0x01f5;
                    case 3: goto L_0x00d5;
                    case 4: goto L_0x0273;
                    default: goto L_0x00d5;
                }     // Catch:{ Exception -> 0x016a }
            L_0x00d5:
                goto L_0x00c0
            L_0x00d6:
                r0 = r35
                java.lang.String r34 = r9.getString(r0)     // Catch:{ Exception -> 0x016a }
                r5 = 0
                r0 = r34
                android.content.Intent r7 = android.content.Intent.parseUri(r0, r5)     // Catch:{ URISyntaxException -> 0x017e }
                if (r37 != 0) goto L_0x0181
                r0 = r52
                com.szchoiceway.index.LauncherModel r5 = com.szchoiceway.index.LauncherModel.this     // Catch:{ Exception -> 0x016a }
                r0 = r52
                java.util.HashMap<java.lang.Object, java.lang.CharSequence> r12 = r0.mLabelCache     // Catch:{ Exception -> 0x016a }
                r6 = r41
                r8 = r14
                com.szchoiceway.index.ShortcutInfo r33 = r5.getShortcutInfo((android.content.pm.PackageManager) r6, (android.content.Intent) r7, (android.content.Context) r8, (android.database.Cursor) r9, (int) r10, (int) r11, (java.util.HashMap<java.lang.Object, java.lang.CharSequence>) r12)     // Catch:{ Exception -> 0x016a }
            L_0x00f4:
                if (r33 == 0) goto L_0x01c1
                r0 = r33
                r0.intent = r7     // Catch:{ Exception -> 0x016a }
                r0 = r32
                long r12 = r9.getLong(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r33
                r0.id = r12     // Catch:{ Exception -> 0x016a }
                r0 = r27
                int r26 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r26
                long r12 = (long) r0     // Catch:{ Exception -> 0x016a }
                r0 = r33
                r0.container = r12     // Catch:{ Exception -> 0x016a }
                r0 = r45
                int r5 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r33
                r0.screen = r5     // Catch:{ Exception -> 0x016a }
                r0 = r23
                int r5 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r33
                r0.cellX = r5     // Catch:{ Exception -> 0x016a }
                r0 = r24
                int r5 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r33
                r0.cellY = r5     // Catch:{ Exception -> 0x016a }
                r0 = r52
                r1 = r43
                r2 = r33
                boolean r5 = r0.checkItemPlacement(r1, r2)     // Catch:{ Exception -> 0x016a }
                if (r5 == 0) goto L_0x00c0
                switch(r26) {
                    case -101: goto L_0x01b9;
                    case -100: goto L_0x01b9;
                    default: goto L_0x013e;
                }     // Catch:{ Exception -> 0x016a }
            L_0x013e:
                java.util.HashMap<java.lang.Long, com.szchoiceway.index.FolderInfo> r5 = com.szchoiceway.index.LauncherModel.sBgFolders     // Catch:{ Exception -> 0x016a }
                r0 = r26
                long r12 = (long) r0     // Catch:{ Exception -> 0x016a }
                com.szchoiceway.index.FolderInfo r29 = com.szchoiceway.index.LauncherModel.findOrMakeFolder(r5, r12)     // Catch:{ Exception -> 0x016a }
                r0 = r29
                r1 = r33
                r0.add(r1)     // Catch:{ Exception -> 0x016a }
            L_0x014e:
                java.util.HashMap<java.lang.Long, com.szchoiceway.index.ItemInfo> r5 = com.szchoiceway.index.LauncherModel.sBgItemsIdMap     // Catch:{ Exception -> 0x016a }
                r0 = r33
                long r12 = r0.id     // Catch:{ Exception -> 0x016a }
                java.lang.Long r6 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x016a }
                r0 = r33
                r5.put(r6, r0)     // Catch:{ Exception -> 0x016a }
                r0 = r52
                com.szchoiceway.index.LauncherModel r5 = com.szchoiceway.index.LauncherModel.this     // Catch:{ Exception -> 0x016a }
                java.util.HashMap<java.lang.Object, byte[]> r6 = com.szchoiceway.index.LauncherModel.sBgDbIconCache     // Catch:{ Exception -> 0x016a }
                r0 = r33
                r5.queueIconToBeChecked(r6, r0, r9, r10)     // Catch:{ Exception -> 0x016a }
                goto L_0x00c0
            L_0x016a:
                r28 = move-exception
                java.lang.String r5 = "Launcher.Model"
                java.lang.String r6 = "Desktop items loading interrupted:"
                r0 = r28
                android.util.Log.w(r5, r6, r0)     // Catch:{ all -> 0x0176 }
                goto L_0x00c0
            L_0x0176:
                r5 = move-exception
                r9.close()     // Catch:{ all -> 0x017b }
                throw r5     // Catch:{ all -> 0x017b }
            L_0x017b:
                r5 = move-exception
                monitor-exit(r51)     // Catch:{ all -> 0x017b }
                throw r5
            L_0x017e:
                r28 = move-exception
                goto L_0x00c0
            L_0x0181:
                r0 = r52
                com.szchoiceway.index.LauncherModel r12 = com.szchoiceway.index.LauncherModel.this     // Catch:{ Exception -> 0x016a }
                r13 = r9
                r18 = r10
                r19 = r11
                com.szchoiceway.index.ShortcutInfo r33 = r12.getShortcutInfo((android.database.Cursor) r13, (android.content.Context) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)     // Catch:{ Exception -> 0x016a }
                java.lang.String r5 = r7.getAction()     // Catch:{ Exception -> 0x016a }
                if (r5 == 0) goto L_0x00f4
                java.util.Set r5 = r7.getCategories()     // Catch:{ Exception -> 0x016a }
                if (r5 == 0) goto L_0x00f4
                java.lang.String r5 = r7.getAction()     // Catch:{ Exception -> 0x016a }
                java.lang.String r6 = "android.intent.action.MAIN"
                boolean r5 = r5.equals(r6)     // Catch:{ Exception -> 0x016a }
                if (r5 == 0) goto L_0x00f4
                java.util.Set r5 = r7.getCategories()     // Catch:{ Exception -> 0x016a }
                java.lang.String r6 = "android.intent.category.LAUNCHER"
                boolean r5 = r5.contains(r6)     // Catch:{ Exception -> 0x016a }
                if (r5 == 0) goto L_0x00f4
                r5 = 270532608(0x10200000, float:3.1554436E-29)
                r7.addFlags(r5)     // Catch:{ Exception -> 0x016a }
                goto L_0x00f4
            L_0x01b9:
                java.util.ArrayList<com.szchoiceway.index.ItemInfo> r5 = com.szchoiceway.index.LauncherModel.sBgWorkspaceItems     // Catch:{ Exception -> 0x016a }
                r0 = r33
                r5.add(r0)     // Catch:{ Exception -> 0x016a }
                goto L_0x014e
            L_0x01c1:
                r0 = r32
                long r30 = r9.getLong(r0)     // Catch:{ Exception -> 0x016a }
                java.lang.String r5 = "Launcher.Model"
                java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x016a }
                r6.<init>()     // Catch:{ Exception -> 0x016a }
                java.lang.String r8 = "Error loading shortcut "
                java.lang.StringBuilder r6 = r6.append(r8)     // Catch:{ Exception -> 0x016a }
                r0 = r30
                java.lang.StringBuilder r6 = r6.append(r0)     // Catch:{ Exception -> 0x016a }
                java.lang.String r8 = ", removing it"
                java.lang.StringBuilder r6 = r6.append(r8)     // Catch:{ Exception -> 0x016a }
                java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x016a }
                android.util.Log.e(r5, r6)     // Catch:{ Exception -> 0x016a }
                r5 = 0
                r0 = r30
                android.net.Uri r5 = com.szchoiceway.index.LauncherSettings.Favorites.getContentUri(r0, r5)     // Catch:{ Exception -> 0x016a }
                r6 = 0
                r8 = 0
                r4.delete(r5, r6, r8)     // Catch:{ Exception -> 0x016a }
                goto L_0x00c0
            L_0x01f5:
                r0 = r32
                long r30 = r9.getLong(r0)     // Catch:{ Exception -> 0x016a }
                java.util.HashMap<java.lang.Long, com.szchoiceway.index.FolderInfo> r5 = com.szchoiceway.index.LauncherModel.sBgFolders     // Catch:{ Exception -> 0x016a }
                r0 = r30
                com.szchoiceway.index.FolderInfo r29 = com.szchoiceway.index.LauncherModel.findOrMakeFolder(r5, r0)     // Catch:{ Exception -> 0x016a }
                java.lang.String r5 = r9.getString(r11)     // Catch:{ Exception -> 0x016a }
                r0 = r29
                r0.title = r5     // Catch:{ Exception -> 0x016a }
                r0 = r30
                r2 = r29
                r2.id = r0     // Catch:{ Exception -> 0x016a }
                r0 = r27
                int r26 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r26
                long r12 = (long) r0     // Catch:{ Exception -> 0x016a }
                r0 = r29
                r0.container = r12     // Catch:{ Exception -> 0x016a }
                r0 = r45
                int r5 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r29
                r0.screen = r5     // Catch:{ Exception -> 0x016a }
                r0 = r23
                int r5 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r29
                r0.cellX = r5     // Catch:{ Exception -> 0x016a }
                r0 = r24
                int r5 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r29
                r0.cellY = r5     // Catch:{ Exception -> 0x016a }
                r0 = r52
                r1 = r43
                r2 = r29
                boolean r5 = r0.checkItemPlacement(r1, r2)     // Catch:{ Exception -> 0x016a }
                if (r5 == 0) goto L_0x00c0
                switch(r26) {
                    case -101: goto L_0x026b;
                    case -100: goto L_0x026b;
                    default: goto L_0x024b;
                }     // Catch:{ Exception -> 0x016a }
            L_0x024b:
                java.util.HashMap<java.lang.Long, com.szchoiceway.index.ItemInfo> r5 = com.szchoiceway.index.LauncherModel.sBgItemsIdMap     // Catch:{ Exception -> 0x016a }
                r0 = r29
                long r12 = r0.id     // Catch:{ Exception -> 0x016a }
                java.lang.Long r6 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x016a }
                r0 = r29
                r5.put(r6, r0)     // Catch:{ Exception -> 0x016a }
                java.util.HashMap<java.lang.Long, com.szchoiceway.index.FolderInfo> r5 = com.szchoiceway.index.LauncherModel.sBgFolders     // Catch:{ Exception -> 0x016a }
                r0 = r29
                long r12 = r0.id     // Catch:{ Exception -> 0x016a }
                java.lang.Long r6 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x016a }
                r0 = r29
                r5.put(r6, r0)     // Catch:{ Exception -> 0x016a }
                goto L_0x00c0
            L_0x026b:
                java.util.ArrayList<com.szchoiceway.index.ItemInfo> r5 = com.szchoiceway.index.LauncherModel.sBgWorkspaceItems     // Catch:{ Exception -> 0x016a }
                r0 = r29
                r5.add(r0)     // Catch:{ Exception -> 0x016a }
                goto L_0x024b
            L_0x0273:
                r0 = r21
                int r20 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r32
                long r30 = r9.getLong(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r50
                r1 = r20
                android.appwidget.AppWidgetProviderInfo r44 = r0.getAppWidgetInfo(r1)     // Catch:{ Exception -> 0x016a }
                if (r36 != 0) goto L_0x02d5
                if (r44 == 0) goto L_0x029b
                r0 = r44
                android.content.ComponentName r5 = r0.provider     // Catch:{ Exception -> 0x016a }
                if (r5 == 0) goto L_0x029b
                r0 = r44
                android.content.ComponentName r5 = r0.provider     // Catch:{ Exception -> 0x016a }
                java.lang.String r5 = r5.getPackageName()     // Catch:{ Exception -> 0x016a }
                if (r5 != 0) goto L_0x02d5
            L_0x029b:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x016a }
                r5.<init>()     // Catch:{ Exception -> 0x016a }
                java.lang.String r6 = "Deleting widget that isn't installed anymore: id="
                java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ Exception -> 0x016a }
                r0 = r30
                java.lang.StringBuilder r5 = r5.append(r0)     // Catch:{ Exception -> 0x016a }
                java.lang.String r6 = " appWidgetId="
                java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ Exception -> 0x016a }
                r0 = r20
                java.lang.StringBuilder r5 = r5.append(r0)     // Catch:{ Exception -> 0x016a }
                java.lang.String r40 = r5.toString()     // Catch:{ Exception -> 0x016a }
                java.lang.String r5 = "Launcher.Model"
                r0 = r40
                android.util.Log.e(r5, r0)     // Catch:{ Exception -> 0x016a }
                java.util.ArrayList<java.lang.String> r5 = com.szchoiceway.index.Launcher.sDumpLogs     // Catch:{ Exception -> 0x016a }
                r0 = r40
                r5.add(r0)     // Catch:{ Exception -> 0x016a }
                java.lang.Long r5 = java.lang.Long.valueOf(r30)     // Catch:{ Exception -> 0x016a }
                r0 = r39
                r0.add(r5)     // Catch:{ Exception -> 0x016a }
                goto L_0x00c0
            L_0x02d5:
                com.szchoiceway.index.LauncherAppWidgetInfo r22 = new com.szchoiceway.index.LauncherAppWidgetInfo     // Catch:{ Exception -> 0x016a }
                r0 = r44
                android.content.ComponentName r5 = r0.provider     // Catch:{ Exception -> 0x016a }
                r0 = r22
                r1 = r20
                r0.<init>(r1, r5)     // Catch:{ Exception -> 0x016a }
                r0 = r30
                r2 = r22
                r2.id = r0     // Catch:{ Exception -> 0x016a }
                r0 = r45
                int r5 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r22
                r0.screen = r5     // Catch:{ Exception -> 0x016a }
                r0 = r23
                int r5 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r22
                r0.cellX = r5     // Catch:{ Exception -> 0x016a }
                r0 = r24
                int r5 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r22
                r0.cellY = r5     // Catch:{ Exception -> 0x016a }
                r0 = r46
                int r5 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r22
                r0.spanX = r5     // Catch:{ Exception -> 0x016a }
                r0 = r47
                int r5 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r0 = r22
                r0.spanY = r5     // Catch:{ Exception -> 0x016a }
                r0 = r44
                int[] r42 = com.szchoiceway.index.Launcher.getMinSpanForWidget((android.content.Context) r14, (android.appwidget.AppWidgetProviderInfo) r0)     // Catch:{ Exception -> 0x016a }
                r5 = 0
                r5 = r42[r5]     // Catch:{ Exception -> 0x016a }
                r0 = r22
                r0.minSpanX = r5     // Catch:{ Exception -> 0x016a }
                r5 = 1
                r5 = r42[r5]     // Catch:{ Exception -> 0x016a }
                r0 = r22
                r0.minSpanY = r5     // Catch:{ Exception -> 0x016a }
                r0 = r27
                int r26 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                r5 = -100
                r0 = r26
                if (r0 == r5) goto L_0x0349
                r5 = -101(0xffffffffffffff9b, float:NaN)
                r0 = r26
                if (r0 == r5) goto L_0x0349
                java.lang.String r5 = "Launcher.Model"
                java.lang.String r6 = "Widget found where container != CONTAINER_DESKTOP nor CONTAINER_HOTSEAT - ignoring!"
                android.util.Log.e(r5, r6)     // Catch:{ Exception -> 0x016a }
                goto L_0x00c0
            L_0x0349:
                r0 = r27
                int r5 = r9.getInt(r0)     // Catch:{ Exception -> 0x016a }
                long r12 = (long) r5     // Catch:{ Exception -> 0x016a }
                r0 = r22
                r0.container = r12     // Catch:{ Exception -> 0x016a }
                r0 = r52
                r1 = r43
                r2 = r22
                boolean r5 = r0.checkItemPlacement(r1, r2)     // Catch:{ Exception -> 0x016a }
                if (r5 == 0) goto L_0x00c0
                java.util.HashMap<java.lang.Long, com.szchoiceway.index.ItemInfo> r5 = com.szchoiceway.index.LauncherModel.sBgItemsIdMap     // Catch:{ Exception -> 0x016a }
                r0 = r22
                long r12 = r0.id     // Catch:{ Exception -> 0x016a }
                java.lang.Long r6 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x016a }
                r0 = r22
                r5.put(r6, r0)     // Catch:{ Exception -> 0x016a }
                java.util.ArrayList<com.szchoiceway.index.LauncherAppWidgetInfo> r5 = com.szchoiceway.index.LauncherModel.sBgAppWidgets     // Catch:{ Exception -> 0x016a }
                r0 = r22
                r5.add(r0)     // Catch:{ Exception -> 0x016a }
                goto L_0x00c0
            L_0x0378:
                r9.close()     // Catch:{ all -> 0x017b }
                int r5 = r39.size()     // Catch:{ all -> 0x017b }
                if (r5 <= 0) goto L_0x03c6
                android.net.Uri r5 = com.szchoiceway.index.LauncherSettings.Favorites.CONTENT_URI     // Catch:{ all -> 0x017b }
                android.content.ContentProviderClient r25 = r4.acquireContentProviderClient(r5)     // Catch:{ all -> 0x017b }
                java.util.Iterator r6 = r39.iterator()     // Catch:{ all -> 0x017b }
            L_0x038b:
                boolean r5 = r6.hasNext()     // Catch:{ all -> 0x017b }
                if (r5 == 0) goto L_0x03c6
                java.lang.Object r5 = r6.next()     // Catch:{ all -> 0x017b }
                java.lang.Long r5 = (java.lang.Long) r5     // Catch:{ all -> 0x017b }
                long r30 = r5.longValue()     // Catch:{ all -> 0x017b }
                r5 = 0
                r0 = r30
                android.net.Uri r5 = com.szchoiceway.index.LauncherSettings.Favorites.getContentUri(r0, r5)     // Catch:{ RemoteException -> 0x03aa }
                r8 = 0
                r12 = 0
                r0 = r25
                r0.delete(r5, r8, r12)     // Catch:{ RemoteException -> 0x03aa }
                goto L_0x038b
            L_0x03aa:
                r28 = move-exception
                java.lang.String r5 = "Launcher.Model"
                java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x017b }
                r8.<init>()     // Catch:{ all -> 0x017b }
                java.lang.String r12 = "Could not remove id = "
                java.lang.StringBuilder r8 = r8.append(r12)     // Catch:{ all -> 0x017b }
                r0 = r30
                java.lang.StringBuilder r8 = r8.append(r0)     // Catch:{ all -> 0x017b }
                java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x017b }
                android.util.Log.w(r5, r8)     // Catch:{ all -> 0x017b }
                goto L_0x038b
            L_0x03c6:
                monitor-exit(r51)     // Catch:{ all -> 0x017b }
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.LauncherModel.LoaderTask.loadWorkspace():void");
        }

        private void filterCurrentWorkspaceItems(int currentScreen, ArrayList<ItemInfo> allWorkspaceItems, ArrayList<ItemInfo> currentScreenItems, ArrayList<ItemInfo> otherScreenItems) {
            Iterator<ItemInfo> iter = allWorkspaceItems.iterator();
            while (iter.hasNext()) {
                if (iter.next() == null) {
                    iter.remove();
                }
            }
            if (currentScreen < 0) {
                currentScreenItems.addAll(allWorkspaceItems);
            }
            Set<Long> itemsOnScreen = new HashSet<>();
            Collections.sort(allWorkspaceItems, new Comparator<ItemInfo>() {
                public int compare(ItemInfo lhs, ItemInfo rhs) {
                    return (int) (lhs.container - rhs.container);
                }
            });
            Iterator<ItemInfo> it = allWorkspaceItems.iterator();
            while (it.hasNext()) {
                ItemInfo info = it.next();
                if (info.container == -100) {
                    if (info.screen == currentScreen) {
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

        private void filterCurrentAppWidgets(int currentScreen, ArrayList<LauncherAppWidgetInfo> appWidgets, ArrayList<LauncherAppWidgetInfo> currentScreenWidgets, ArrayList<LauncherAppWidgetInfo> otherScreenWidgets) {
            if (currentScreen < 0) {
                currentScreenWidgets.addAll(appWidgets);
            }
            Iterator<LauncherAppWidgetInfo> it = appWidgets.iterator();
            while (it.hasNext()) {
                LauncherAppWidgetInfo widget = it.next();
                if (widget != null) {
                    if (widget.container == -100 && widget.screen == currentScreen) {
                        currentScreenWidgets.add(widget);
                    } else {
                        otherScreenWidgets.add(widget);
                    }
                }
            }
        }

        private void filterCurrentFolders(int currentScreen, HashMap<Long, ItemInfo> itemsIdMap, HashMap<Long, FolderInfo> folders, HashMap<Long, FolderInfo> currentScreenFolders, HashMap<Long, FolderInfo> otherScreenFolders) {
            if (currentScreen < 0) {
                currentScreenFolders.putAll(folders);
            }
            for (Long longValue : folders.keySet()) {
                long id = longValue.longValue();
                ItemInfo info = itemsIdMap.get(Long.valueOf(id));
                FolderInfo folder = folders.get(Long.valueOf(id));
                if (!(info == null || folder == null)) {
                    if (info.container == -100 && info.screen == currentScreen) {
                        currentScreenFolders.put(Long.valueOf(id), folder);
                    } else {
                        otherScreenFolders.put(Long.valueOf(id), folder);
                    }
                }
            }
        }

        private void sortWorkspaceItemsSpatially(ArrayList<ItemInfo> workspaceItems) {
            Collections.sort(workspaceItems, new Comparator<ItemInfo>() {
                public int compare(ItemInfo lhs, ItemInfo rhs) {
                    int cellCountX = LauncherModel.getCellCountX();
                    int screenOffset = cellCountX * LauncherModel.getCellCountY();
                    int containerOffset = screenOffset * 6;
                    return (int) (((((lhs.container * ((long) containerOffset)) + ((long) (lhs.screen * screenOffset))) + ((long) (lhs.cellY * cellCountX))) + ((long) lhs.cellX)) - ((((rhs.container * ((long) containerOffset)) + ((long) (rhs.screen * screenOffset))) + ((long) (rhs.cellY * cellCountX))) + ((long) rhs.cellX)));
                }
            });
        }

        private void bindWorkspaceItems(final Callbacks oldCallbacks, ArrayList<ItemInfo> workspaceItems, ArrayList<LauncherAppWidgetInfo> appWidgets, final HashMap<Long, FolderInfo> folders, ArrayList<Runnable> deferredBindRunnables) {
            boolean postOnMainThread = deferredBindRunnables != null;
            int N = workspaceItems.size();
            for (int i = 0; i < N; i += 6) {
                final int start = i;
                final int chunkSize = i + 6 <= N ? 6 : N - i;
                final Callbacks callbacks = oldCallbacks;
                final ArrayList<ItemInfo> arrayList = workspaceItems;
                Runnable r = new Runnable() {
                    public void run() {
                        Callbacks callbacks = LoaderTask.this.tryGetCallbacks(callbacks);
                        if (callbacks != null) {
                            callbacks.bindItems(arrayList, start, start + chunkSize);
                        }
                    }
                };
                if (postOnMainThread) {
                    deferredBindRunnables.add(r);
                } else {
                    LauncherModel.this.runOnMainThread(r, 1);
                }
            }
            if (!folders.isEmpty()) {
                Runnable r2 = new Runnable() {
                    public void run() {
                        Callbacks callbacks = LoaderTask.this.tryGetCallbacks(oldCallbacks);
                        if (callbacks != null) {
                            callbacks.bindFolders(folders);
                        }
                    }
                };
                if (postOnMainThread) {
                    deferredBindRunnables.add(r2);
                } else {
                    LauncherModel.this.runOnMainThread(r2, 1);
                }
            }
            int N2 = appWidgets.size();
            for (int i2 = 0; i2 < N2; i2++) {
                final LauncherAppWidgetInfo widget = appWidgets.get(i2);
                Runnable r3 = new Runnable() {
                    public void run() {
                        Callbacks callbacks = LoaderTask.this.tryGetCallbacks(oldCallbacks);
                        if (callbacks != null) {
                            callbacks.bindAppWidget(widget);
                        }
                    }
                };
                if (postOnMainThread) {
                    deferredBindRunnables.add(r3);
                } else {
                    LauncherModel.this.runOnMainThread(r3, 1);
                }
            }
        }

        private void bindWorkspace(int synchronizeBindPage) {
            final int currentScreen;
            long t = SystemClock.uptimeMillis();
            final Callbacks oldCallbacks = (Callbacks) LauncherModel.this.mCallbacks.get();
            if (oldCallbacks == null) {
                Log.w(LauncherModel.TAG, "LoaderTask running with no launcher");
                return;
            }
            boolean isLoadingSynchronously = synchronizeBindPage > -1;
            if (isLoadingSynchronously) {
                currentScreen = synchronizeBindPage;
            } else {
                currentScreen = oldCallbacks.getCurrentWorkspaceScreen();
            }
            LauncherModel.this.unbindWorkspaceItemsOnMainThread();
            ArrayList<ItemInfo> workspaceItems = new ArrayList<>();
            ArrayList<LauncherAppWidgetInfo> appWidgets = new ArrayList<>();
            HashMap<Long, FolderInfo> folders = new HashMap<>();
            HashMap<Long, ItemInfo> itemsIdMap = new HashMap<>();
            synchronized (LauncherModel.sBgLock) {
                workspaceItems.addAll(LauncherModel.sBgWorkspaceItems);
                appWidgets.addAll(LauncherModel.sBgAppWidgets);
                folders.putAll(LauncherModel.sBgFolders);
                itemsIdMap.putAll(LauncherModel.sBgItemsIdMap);
            }
            ArrayList<ItemInfo> currentWorkspaceItems = new ArrayList<>();
            ArrayList<ItemInfo> otherWorkspaceItems = new ArrayList<>();
            ArrayList<LauncherAppWidgetInfo> currentAppWidgets = new ArrayList<>();
            ArrayList<LauncherAppWidgetInfo> otherAppWidgets = new ArrayList<>();
            HashMap<Long, FolderInfo> currentFolders = new HashMap<>();
            HashMap<Long, FolderInfo> otherFolders = new HashMap<>();
            filterCurrentWorkspaceItems(currentScreen, workspaceItems, currentWorkspaceItems, otherWorkspaceItems);
            filterCurrentAppWidgets(currentScreen, appWidgets, currentAppWidgets, otherAppWidgets);
            filterCurrentFolders(currentScreen, itemsIdMap, folders, currentFolders, otherFolders);
            sortWorkspaceItemsSpatially(currentWorkspaceItems);
            sortWorkspaceItemsSpatially(otherWorkspaceItems);
            LauncherModel.this.runOnMainThread(new Runnable() {
                public void run() {
                    Callbacks callbacks = LoaderTask.this.tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.startBinding();
                    }
                }
            }, 1);
            bindWorkspaceItems(oldCallbacks, currentWorkspaceItems, currentAppWidgets, currentFolders, (ArrayList<Runnable>) null);
            if (isLoadingSynchronously) {
                LauncherModel.this.runOnMainThread(new Runnable() {
                    public void run() {
                        Callbacks callbacks = LoaderTask.this.tryGetCallbacks(oldCallbacks);
                        if (callbacks != null) {
                            callbacks.onPageBoundSynchronously(currentScreen);
                        }
                    }
                }, 1);
            }
            LauncherModel.mDeferredBindRunnables.clear();
            bindWorkspaceItems(oldCallbacks, otherWorkspaceItems, otherAppWidgets, otherFolders, isLoadingSynchronously ? LauncherModel.mDeferredBindRunnables : null);
            final long j = t;
            AnonymousClass9 r0 = new Runnable() {
                public void run() {
                    Callbacks callbacks = LoaderTask.this.tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.finishBindingItems();
                    }
                    boolean unused = LoaderTask.this.mIsLoadingAndBindingWorkspace = false;
                }
            };
            if (isLoadingSynchronously) {
                LauncherModel.mDeferredBindRunnables.add(r0);
            } else {
                LauncherModel.this.runOnMainThread(r0, 1);
            }
        }

        private void loadAndBindAllApps() {
            if (!LauncherModel.this.mAllAppsLoaded) {
                loadAllAppsByBatch();
                synchronized (this) {
                    if (!this.mStopped) {
                        boolean unused = LauncherModel.this.mAllAppsLoaded = true;
                        return;
                    }
                    return;
                }
            }
            onlyBindAllApps();
        }

        private void onlyBindAllApps() {
            final Callbacks oldCallbacks = (Callbacks) LauncherModel.this.mCallbacks.get();
            if (oldCallbacks == null) {
                Log.w(LauncherModel.TAG, "LoaderTask running with no launcher (onlyBindAllApps)");
                return;
            }
            final ArrayList<ApplicationInfo> list = (ArrayList) LauncherModel.this.mBgAllAppsList.data.clone();
            Runnable r = new Runnable() {
                public void run() {
                    long uptimeMillis = SystemClock.uptimeMillis();
                    Callbacks callbacks = LoaderTask.this.tryGetCallbacks(oldCallbacks);
                    if (callbacks != null) {
                        callbacks.bindAllApplications(list);
                    }
                }
            };
            boolean isRunningOnMainThread = LauncherModel.sWorkerThread.getThreadId() != Process.myTid();
            if (!oldCallbacks.isAllAppsVisible() || !isRunningOnMainThread) {
                LauncherModel.this.mHandler.post(r);
            } else {
                r.run();
            }
        }

        private void loadAllAppsByBatch() {
            Callbacks oldCallbacks = (Callbacks) LauncherModel.this.mCallbacks.get();
            if (oldCallbacks == null) {
                Log.w(LauncherModel.TAG, "LoaderTask running with no launcher (loadAllAppsByBatch)");
                return;
            }
            Intent mainIntent = new Intent("android.intent.action.MAIN", (Uri) null);
            mainIntent.addCategory("android.intent.category.LAUNCHER");
            PackageManager packageManager = this.mContext.getPackageManager();
            List<ResolveInfo> apps = null;
            int N = Integer.MAX_VALUE;
            int i = 0;
            int batchSize = -1;
            while (i < N && !this.mStopped) {
                if (i == 0) {
                    LauncherModel.this.mBgAllAppsList.clear();
                    apps = packageManager.queryIntentActivities(mainIntent, 0);
                    if (apps != null && (N = apps.size()) != 0) {
                        if (LauncherModel.this.mBatchSize == 0) {
                            batchSize = N;
                        } else {
                            batchSize = LauncherModel.this.mBatchSize;
                        }
                        Collections.sort(apps, new ShortcutNameComparator(packageManager, this.mLabelCache));
                    } else {
                        return;
                    }
                }
                int i2 = i;
                int j = 0;
                while (i < N && j < batchSize) {
                    LauncherModel.this.mBgAllAppsList.add(new ApplicationInfo(packageManager, apps.get(i), LauncherModel.this.mIconCache, this.mLabelCache));
                    i++;
                    j++;
                }
                final boolean first = i <= batchSize;
                final Callbacks callbacks = tryGetCallbacks(oldCallbacks);
                final ArrayList<ApplicationInfo> added = LauncherModel.this.mBgAllAppsList.added;
                LauncherModel.this.mBgAllAppsList.added = new ArrayList<>();
                LauncherModel.this.mHandler.post(new Runnable() {
                    public void run() {
                        long uptimeMillis = SystemClock.uptimeMillis();
                        if (callbacks == null) {
                            Log.i(LauncherModel.TAG, "not binding apps: no Launcher activity");
                        } else if (first) {
                            callbacks.bindAllApplications(added);
                        } else {
                            callbacks.bindAppsAdded(added);
                        }
                    }
                });
                if (LauncherModel.this.mAllAppsLoadDelay > 0 && i < N) {
                    try {
                        Thread.sleep((long) LauncherModel.this.mAllAppsLoadDelay);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

        public void dumpState() {
            synchronized (LauncherModel.sBgLock) {
                Log.d(LauncherModel.TAG, "mLoaderTask.mContext=" + this.mContext);
                Log.d(LauncherModel.TAG, "mLoaderTask.mIsLaunching=" + this.mIsLaunching);
                Log.d(LauncherModel.TAG, "mLoaderTask.mStopped=" + this.mStopped);
                Log.d(LauncherModel.TAG, "mLoaderTask.mLoadAndBindStepFinished=" + this.mLoadAndBindStepFinished);
                Log.d(LauncherModel.TAG, "mItems size=" + LauncherModel.sBgWorkspaceItems.size());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void enqueuePackageUpdated(PackageUpdatedTask task) {
        sWorker.post(task);
    }

    private class PackageUpdatedTask implements Runnable {
        public static final int OP_ADD = 1;
        public static final int OP_NONE = 0;
        public static final int OP_REMOVE = 3;
        public static final int OP_UNAVAILABLE = 4;
        public static final int OP_UPDATE = 2;
        int mOp;
        String[] mPackages;

        public PackageUpdatedTask(int op, String[] packages) {
            this.mOp = op;
            this.mPackages = packages;
        }

        public void run() {
            Context context = LauncherModel.mApp;
            String[] packages = this.mPackages;
            switch (this.mOp) {
                case 1:
                    for (String addPackage : packages) {
                        LauncherModel.this.mBgAllAppsList.addPackage(context, addPackage);
                    }
                    break;
                case 2:
                    for (int i = 0; i < N; i++) {
                        LauncherModel.this.mBgAllAppsList.updatePackage(context, packages[i]);
                        WidgetPreviewLoader.removeFromDb(((LauncherApplication) context.getApplicationContext()).getWidgetPreviewCacheDb(), packages[i]);
                    }
                    break;
                case 3:
                case 4:
                    for (int i2 = 0; i2 < N; i2++) {
                        LauncherModel.this.mBgAllAppsList.removePackage(packages[i2]);
                        WidgetPreviewLoader.removeFromDb(((LauncherApplication) context.getApplicationContext()).getWidgetPreviewCacheDb(), packages[i2]);
                    }
                    break;
            }
            ArrayList<ApplicationInfo> added = null;
            ArrayList<ApplicationInfo> modified = null;
            final ArrayList<ApplicationInfo> removedApps = new ArrayList<>();
            if (LauncherModel.this.mBgAllAppsList.added.size() > 0) {
                added = new ArrayList<>(LauncherModel.this.mBgAllAppsList.added);
                LauncherModel.this.mBgAllAppsList.added.clear();
            }
            if (LauncherModel.this.mBgAllAppsList.modified.size() > 0) {
                modified = new ArrayList<>(LauncherModel.this.mBgAllAppsList.modified);
                LauncherModel.this.mBgAllAppsList.modified.clear();
            }
            if (LauncherModel.this.mBgAllAppsList.removed.size() > 0) {
                removedApps.addAll(LauncherModel.this.mBgAllAppsList.removed);
                LauncherModel.this.mBgAllAppsList.removed.clear();
            }
            final Callbacks callbacks = LauncherModel.this.mCallbacks != null ? (Callbacks) LauncherModel.this.mCallbacks.get() : null;
            if (callbacks == null) {
                Log.w(LauncherModel.TAG, "Nobody to tell about the new app.  Launcher is probably loading.");
                return;
            }
            if (added != null) {
                final ArrayList<ApplicationInfo> addedFinal = added;
                LauncherModel.this.mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks cb = LauncherModel.this.mCallbacks != null ? (Callbacks) LauncherModel.this.mCallbacks.get() : null;
                        if (callbacks == cb && cb != null) {
                            callbacks.bindAppsAdded(addedFinal);
                        }
                    }
                });
            }
            if (modified != null) {
                final ArrayList<ApplicationInfo> modifiedFinal = modified;
                LauncherModel.this.mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks cb = LauncherModel.this.mCallbacks != null ? (Callbacks) LauncherModel.this.mCallbacks.get() : null;
                        if (callbacks == cb && cb != null) {
                            callbacks.bindAppsUpdated(modifiedFinal);
                        }
                    }
                });
            }
            if (this.mOp == 3 || !removedApps.isEmpty()) {
                final boolean permanent = this.mOp == 3;
                final ArrayList<String> removedPackageNames = new ArrayList<>(Arrays.asList(packages));
                LauncherModel.this.mHandler.post(new Runnable() {
                    public void run() {
                        Callbacks cb = LauncherModel.this.mCallbacks != null ? (Callbacks) LauncherModel.this.mCallbacks.get() : null;
                        if (callbacks == cb && cb != null) {
                            callbacks.bindComponentsRemoved(removedPackageNames, removedApps, permanent);
                        }
                    }
                });
            }
            final ArrayList<Object> sortedWidgetsAndShortcuts = LauncherModel.getSortedWidgetsAndShortcuts(context);
            LauncherModel.this.mHandler.post(new Runnable() {
                public void run() {
                    Callbacks cb = LauncherModel.this.mCallbacks != null ? (Callbacks) LauncherModel.this.mCallbacks.get() : null;
                    if (callbacks == cb && cb != null) {
                        callbacks.bindPackagesUpdated(sortedWidgetsAndShortcuts);
                    }
                }
            });
        }
    }

    public static ArrayList<Object> getSortedWidgetsAndShortcuts(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ArrayList<Object> widgetsAndShortcuts = new ArrayList<>();
        for (AppWidgetProviderInfo info : AppWidgetManager.getInstance(context).getInstalledProviders()) {
            Log.i(TAG, "" + info.provider.getPackageName());
            if (info.provider.getPackageName().startsWith("com.szchoiceway")) {
                widgetsAndShortcuts.add(info);
            }
        }
        if (widgetsAndShortcuts.size() == 0) {
            widgetsAndShortcuts.addAll(packageManager.queryIntentActivities(new Intent("android.intent.action.CREATE_SHORTCUT"), 0));
        }
        Collections.sort(widgetsAndShortcuts, new WidgetAndShortcutNameComparator(packageManager));
        return widgetsAndShortcuts;
    }

    public ShortcutInfo getShortcutInfo(PackageManager manager, Intent intent, Context context) {
        return getShortcutInfo(manager, intent, context, (Cursor) null, -1, -1, (HashMap<Object, CharSequence>) null);
    }

    public ShortcutInfo getShortcutInfo(PackageManager manager, Intent intent, Context context, Cursor c, int iconIndex, int titleIndex, HashMap<Object, CharSequence> labelCache) {
        Bitmap icon = null;
        ShortcutInfo info = new ShortcutInfo();
        ComponentName componentName = intent.getComponent();
        if (componentName == null) {
            return null;
        }
        try {
            if (!manager.getPackageInfo(componentName.getPackageName(), 0).applicationInfo.enabled) {
                return null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "getPackInfo failed for package " + componentName.getPackageName());
        }
        ResolveInfo resolveInfo = null;
        ComponentName oldComponent = intent.getComponent();
        Intent newIntent = new Intent(intent.getAction(), (Uri) null);
        newIntent.addCategory("android.intent.category.LAUNCHER");
        newIntent.setPackage(oldComponent.getPackageName());
        for (ResolveInfo i : manager.queryIntentActivities(newIntent, 0)) {
            if (new ComponentName(i.activityInfo.packageName, i.activityInfo.name).equals(oldComponent)) {
                resolveInfo = i;
            }
        }
        if (resolveInfo == null) {
            resolveInfo = manager.resolveActivity(intent, 0);
        }
        if (resolveInfo != null) {
            icon = this.mIconCache.getIcon(componentName, resolveInfo, labelCache);
        }
        if (icon == null && c != null) {
            icon = getIconFromCursor(c, iconIndex, context);
        }
        if (icon == null) {
            icon = getFallbackIcon();
            info.usingFallbackIcon = true;
        }
        info.setIcon(icon);
        if (resolveInfo != null) {
            ComponentName key = getComponentNameFromResolveInfo(resolveInfo);
            if (labelCache == null || !labelCache.containsKey(key)) {
                info.title = resolveInfo.activityInfo.loadLabel(manager);
                if (labelCache != null) {
                    labelCache.put(key, info.title);
                }
            } else {
                info.title = labelCache.get(key);
            }
        }
        if (info.title == null && c != null) {
            info.title = c.getString(titleIndex);
        }
        if (info.title == null) {
            info.title = componentName.getClassName();
        }
        info.itemType = 0;
        return info;
    }

    static ArrayList<ItemInfo> getWorkspaceShortcutItemInfosWithIntent(Intent intent) {
        ArrayList<ItemInfo> items = new ArrayList<>();
        synchronized (sBgLock) {
            Iterator<ItemInfo> it = sBgWorkspaceItems.iterator();
            while (it.hasNext()) {
                ItemInfo info = it.next();
                if (info instanceof ShortcutInfo) {
                    ShortcutInfo shortcut = (ShortcutInfo) info;
                    if (shortcut.intent.toUri(0).equals(intent.toUri(0))) {
                        items.add(shortcut);
                    }
                }
            }
        }
        return items;
    }

    /* access modifiers changed from: private */
    public ShortcutInfo getShortcutInfo(Cursor c, Context context, int iconTypeIndex, int iconPackageIndex, int iconResourceIndex, int iconIndex, int titleIndex) {
        Bitmap icon = null;
        ShortcutInfo info = new ShortcutInfo();
        info.itemType = 1;
        info.title = c.getString(titleIndex);
        switch (c.getInt(iconTypeIndex)) {
            case 0:
                String packageName = c.getString(iconPackageIndex);
                String resourceName = c.getString(iconResourceIndex);
                PackageManager packageManager = context.getPackageManager();
                info.customIcon = false;
                try {
                    Resources resources = packageManager.getResourcesForApplication(packageName);
                    if (resources != null) {
                        icon = Utilities.createIconBitmap(this.mIconCache.getFullResIcon(resources, resources.getIdentifier(resourceName, (String) null, (String) null)), context);
                    }
                } catch (Exception e) {
                }
                if (icon == null) {
                    icon = getIconFromCursor(c, iconIndex, context);
                }
                if (icon == null) {
                    icon = getFallbackIcon();
                    info.usingFallbackIcon = true;
                    break;
                }
                break;
            case 1:
                icon = getIconFromCursor(c, iconIndex, context);
                if (icon != null) {
                    info.customIcon = true;
                    break;
                } else {
                    icon = getFallbackIcon();
                    info.customIcon = false;
                    info.usingFallbackIcon = true;
                    break;
                }
            default:
                icon = getFallbackIcon();
                info.usingFallbackIcon = true;
                info.customIcon = false;
                break;
        }
        info.setIcon(icon);
        return info;
    }

    /* access modifiers changed from: package-private */
    public Bitmap getIconFromCursor(Cursor c, int iconIndex, Context context) {
        byte[] data = c.getBlob(iconIndex);
        try {
            return Utilities.createIconBitmap(BitmapFactory.decodeByteArray(data, 0, data.length), context);
        } catch (Exception e) {
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public ShortcutInfo addShortcut(Context context, Intent data, long container, int screen, int cellX, int cellY, boolean notify) {
        ShortcutInfo info = infoFromShortcutIntent(context, data, (Bitmap) null);
        if (info == null) {
            return null;
        }
        addItemToDatabase(context, info, container, screen, cellX, cellY, notify);
        return info;
    }

    /* access modifiers changed from: package-private */
    public AppWidgetProviderInfo findAppWidgetProviderInfoWithComponent(Context context, ComponentName component) {
        for (AppWidgetProviderInfo info : AppWidgetManager.getInstance(context).getInstalledProviders()) {
            if (info.provider.equals(component)) {
                return info;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public List<InstallWidgetReceiver.WidgetMimeTypeHandlerData> resolveWidgetsForMimeType(Context context, String mimeType) {
        PackageManager packageManager = context.getPackageManager();
        List<InstallWidgetReceiver.WidgetMimeTypeHandlerData> supportedConfigurationActivities = new ArrayList<>();
        Intent supportsIntent = new Intent(InstallWidgetReceiver.ACTION_SUPPORTS_CLIPDATA_MIMETYPE);
        supportsIntent.setType(mimeType);
        List<AppWidgetProviderInfo> widgets = AppWidgetManager.getInstance(context).getInstalledProviders();
        HashMap<ComponentName, AppWidgetProviderInfo> configurationComponentToWidget = new HashMap<>();
        for (AppWidgetProviderInfo info : widgets) {
            configurationComponentToWidget.put(info.configure, info);
        }
        for (ResolveInfo info2 : packageManager.queryIntentActivities(supportsIntent, 65536)) {
            ActivityInfo activityInfo = info2.activityInfo;
            ComponentName infoComponent = new ComponentName(activityInfo.packageName, activityInfo.name);
            if (configurationComponentToWidget.containsKey(infoComponent)) {
                supportedConfigurationActivities.add(new InstallWidgetReceiver.WidgetMimeTypeHandlerData(info2, configurationComponentToWidget.get(infoComponent)));
            }
        }
        return supportedConfigurationActivities;
    }

    /* access modifiers changed from: package-private */
    public ShortcutInfo infoFromShortcutIntent(Context context, Intent data, Bitmap fallbackIcon) {
        Intent intent = (Intent) data.getParcelableExtra("android.intent.extra.shortcut.INTENT");
        String name = data.getStringExtra("android.intent.extra.shortcut.NAME");
        Parcelable bitmap = data.getParcelableExtra("android.intent.extra.shortcut.ICON");
        if (intent == null) {
            Log.e(TAG, "Can't construct ShorcutInfo with null intent");
            return null;
        }
        Bitmap icon = null;
        boolean customIcon = false;
        Intent.ShortcutIconResource iconResource = null;
        if (bitmap == null || !(bitmap instanceof Bitmap)) {
            Parcelable extra = data.getParcelableExtra("android.intent.extra.shortcut.ICON_RESOURCE");
            if (extra != null && (extra instanceof Intent.ShortcutIconResource)) {
                try {
                    iconResource = (Intent.ShortcutIconResource) extra;
                    Resources resources = context.getPackageManager().getResourcesForApplication(iconResource.packageName);
                    icon = Utilities.createIconBitmap(this.mIconCache.getFullResIcon(resources, resources.getIdentifier(iconResource.resourceName, (String) null, (String) null)), context);
                } catch (Exception e) {
                    Log.w(TAG, "Could not load shortcut icon: " + extra);
                }
            }
        } else {
            icon = Utilities.createIconBitmap((Drawable) new FastBitmapDrawable((Bitmap) bitmap), context);
            customIcon = true;
        }
        ShortcutInfo info = new ShortcutInfo();
        if (icon == null) {
            if (fallbackIcon != null) {
                icon = fallbackIcon;
            } else {
                icon = getFallbackIcon();
                info.usingFallbackIcon = true;
            }
        }
        info.setIcon(icon);
        info.title = name;
        info.intent = intent;
        info.customIcon = customIcon;
        info.iconResource = iconResource;
        return info;
    }

    /* access modifiers changed from: package-private */
    public boolean queueIconToBeChecked(HashMap<Object, byte[]> cache, ShortcutInfo info, Cursor c, int iconIndex) {
        if (!this.mAppsCanBeOnExternalStorage || info.customIcon || info.usingFallbackIcon) {
            return false;
        }
        cache.put(info, c.getBlob(iconIndex));
        return true;
    }

    /* access modifiers changed from: package-private */
    public void updateSavedIcon(Context context, ShortcutInfo info, byte[] data) {
        boolean needSave;
        if (data != null) {
            try {
                if (!BitmapFactory.decodeByteArray(data, 0, data.length).sameAs(info.getIcon(this.mIconCache))) {
                    needSave = true;
                } else {
                    needSave = false;
                }
            } catch (Exception e) {
                needSave = true;
            }
        } else {
            needSave = true;
        }
        if (needSave) {
            Log.d(TAG, "going to save icon bitmap for info=" + info);
            updateItemInDatabase(context, info);
        }
    }

    /* access modifiers changed from: private */
    public static FolderInfo findOrMakeFolder(HashMap<Long, FolderInfo> folders, long id) {
        FolderInfo folderInfo = folders.get(Long.valueOf(id));
        if (folderInfo != null) {
            return folderInfo;
        }
        FolderInfo folderInfo2 = new FolderInfo();
        folders.put(Long.valueOf(id), folderInfo2);
        return folderInfo2;
    }

    public static final Comparator<ApplicationInfo> getAppNameComparator() {
        final Collator collator = Collator.getInstance();
        return new Comparator<ApplicationInfo>() {
            public final int compare(ApplicationInfo a, ApplicationInfo b) {
                int aIndex = -1;
                int bIndex = -1;
                LauncherApplication unused = LauncherModel.mApp;
                if (LauncherApplication.m_iUITypeVer == 36) {
                    String[] appClassName = {EventUtils.EXPLORER_MODE_CLASS_NAME, "com.szchoiceway.navigation", EventUtils.BT_MODE_CLASS_NAME, EventUtils.BTMUSIC_MODE_CLASS_NAME, EventUtils.MUSIC_MODE_CLASS_NAME, EventUtils.SET_MODE_CLASS_NAME, EventUtils.MOVIE_MODE_CLASS_NAME};
                    for (int appIndex = 0; appIndex < appClassName.length; appIndex++) {
                        if (a.componentName.getClassName().equals(appClassName[appIndex])) {
                            aIndex = appIndex;
                        }
                        if (b.componentName.getClassName().equals(appClassName[appIndex])) {
                            bIndex = appIndex;
                        }
                    }
                    if (aIndex >= 0 && bIndex < 0) {
                        return -1;
                    }
                    if (aIndex < 0 && bIndex >= 0) {
                        return 1;
                    }
                    if (aIndex >= 0 && bIndex >= 0) {
                        if (aIndex >= bIndex) {
                            return 1;
                        }
                        return -1;
                    }
                } else {
                    boolean aIsSysApp = false;
                    boolean bIsSysApp = false;
                    if (a.componentName.getPackageName().startsWith("com.szchoiceway")) {
                        aIsSysApp = true;
                    }
                    if (b.componentName.getPackageName().startsWith("com.szchoiceway")) {
                        bIsSysApp = true;
                    }
                    if (aIsSysApp && !bIsSysApp) {
                        return -1;
                    }
                    if (!aIsSysApp && bIsSysApp) {
                        return 1;
                    }
                }
                int result = collator.compare(a.title.toString(), b.title.toString());
                if (result == 0) {
                    return a.componentName.compareTo(b.componentName);
                }
                return result;
            }
        };
    }

    public static final Comparator<AppWidgetProviderInfo> getWidgetNameComparator() {
        final Collator collator = Collator.getInstance();
        return new Comparator<AppWidgetProviderInfo>() {
            public final int compare(AppWidgetProviderInfo a, AppWidgetProviderInfo b) {
                return collator.compare(a.label.toString(), b.label.toString());
            }
        };
    }

    static ComponentName getComponentNameFromResolveInfo(ResolveInfo info) {
        if (info.activityInfo != null) {
            return new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
        }
        return new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name);
    }

    public static class ShortcutNameComparator implements Comparator<ResolveInfo> {
        private Collator mCollator;
        private HashMap<Object, CharSequence> mLabelCache;
        private PackageManager mPackageManager;

        ShortcutNameComparator(PackageManager pm) {
            this.mPackageManager = pm;
            this.mLabelCache = new HashMap<>();
            this.mCollator = Collator.getInstance();
        }

        ShortcutNameComparator(PackageManager pm, HashMap<Object, CharSequence> labelCache) {
            this.mPackageManager = pm;
            this.mLabelCache = labelCache;
            this.mCollator = Collator.getInstance();
        }

        public final int compare(ResolveInfo a, ResolveInfo b) {
            CharSequence labelA;
            CharSequence labelB;
            ComponentName keyA = LauncherModel.getComponentNameFromResolveInfo(a);
            ComponentName keyB = LauncherModel.getComponentNameFromResolveInfo(b);
            if (this.mLabelCache.containsKey(keyA)) {
                labelA = this.mLabelCache.get(keyA);
            } else {
                labelA = a.loadLabel(this.mPackageManager).toString();
                this.mLabelCache.put(keyA, labelA);
            }
            if (this.mLabelCache.containsKey(keyB)) {
                labelB = this.mLabelCache.get(keyB);
            } else {
                labelB = b.loadLabel(this.mPackageManager).toString();
                this.mLabelCache.put(keyB, labelB);
            }
            return this.mCollator.compare(labelA, labelB);
        }
    }

    public static class WidgetAndShortcutNameComparator implements Comparator<Object> {
        private Collator mCollator = Collator.getInstance();
        private HashMap<Object, String> mLabelCache = new HashMap<>();
        private PackageManager mPackageManager;

        WidgetAndShortcutNameComparator(PackageManager pm) {
            this.mPackageManager = pm;
        }

        public final int compare(Object a, Object b) {
            String labelA;
            String labelB;
            if (this.mLabelCache.containsKey(a)) {
                labelA = this.mLabelCache.get(a);
            } else {
                if (a instanceof AppWidgetProviderInfo) {
                    labelA = ((AppWidgetProviderInfo) a).label;
                } else {
                    labelA = ((ResolveInfo) a).loadLabel(this.mPackageManager).toString();
                }
                this.mLabelCache.put(a, labelA);
            }
            if (this.mLabelCache.containsKey(b)) {
                labelB = this.mLabelCache.get(b);
            } else {
                if (b instanceof AppWidgetProviderInfo) {
                    labelB = ((AppWidgetProviderInfo) b).label;
                } else {
                    labelB = ((ResolveInfo) b).loadLabel(this.mPackageManager).toString();
                }
                this.mLabelCache.put(b, labelB);
            }
            return this.mCollator.compare(labelA, labelB);
        }
    }

    public void dumpState() {
        Log.d(TAG, "mCallbacks=" + this.mCallbacks);
        ApplicationInfo.dumpApplicationInfoList(TAG, "mAllAppsList.data", this.mBgAllAppsList.data);
        ApplicationInfo.dumpApplicationInfoList(TAG, "mAllAppsList.added", this.mBgAllAppsList.added);
        ApplicationInfo.dumpApplicationInfoList(TAG, "mAllAppsList.removed", this.mBgAllAppsList.removed);
        ApplicationInfo.dumpApplicationInfoList(TAG, "mAllAppsList.modified", this.mBgAllAppsList.modified);
        if (this.mLoaderTask != null) {
            this.mLoaderTask.dumpState();
        } else {
            Log.d(TAG, "mLoaderTask=null");
        }
    }
}
