package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.DbDowngradeHelper;
import com.android.launcher3.provider.LauncherDbUtils;
import com.android.launcher3.provider.RestoreDbTask;
import com.android.launcher3.util.NoLocaleSQLiteHelper;
import com.android.launcher3.util.Preconditions;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class LauncherProvider extends ContentProvider {
    public static final String AUTHORITY = FeatureFlags.AUTHORITY;
    private static final String DOWNGRADE_SCHEMA_FILE = "downgrade_schema.json";
    static final String EMPTY_DATABASE_CREATED = "EMPTY_DATABASE_CREATED";
    private static final boolean LOGD = false;
    private static final String RESTRICTION_PACKAGE_NAME = "workspace.configuration.package.name";
    public static final int SCHEMA_VERSION = 27;
    private static final String TAG = "LauncherProvider";
    private Handler mListenerHandler;
    private final ChangeListenerWrapper mListenerWrapper = new ChangeListenerWrapper();
    protected DatabaseHelper mOpenHelper;

    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        LauncherAppState appState = LauncherAppState.getInstanceNoCreate();
        if (appState != null && appState.getModel().isModelLoaded()) {
            appState.getModel().dumpState("", fd, writer, args);
        }
    }

    public boolean onCreate() {
        this.mListenerHandler = new Handler(this.mListenerWrapper);
        MainProcessInitializer.initialize(getContext().getApplicationContext());
        return true;
    }

    public void setLauncherProviderChangeListener(LauncherProviderChangeListener listener) {
        Preconditions.assertUIThread();
        LauncherProviderChangeListener unused = this.mListenerWrapper.mListener = listener;
    }

    public String getType(Uri uri) {
        SqlArguments args = new SqlArguments(uri, (String) null, (String[]) null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        }
        return "vnd.android.cursor.item/" + args.table;
    }

    /* access modifiers changed from: protected */
    public synchronized void createDbIfNotExists() {
        if (this.mOpenHelper == null) {
            this.mOpenHelper = new DatabaseHelper(getContext(), this.mListenerHandler);
            if (RestoreDbTask.isPending(getContext())) {
                if (!RestoreDbTask.performRestore(this.mOpenHelper)) {
                    this.mOpenHelper.createEmptyDB(this.mOpenHelper.getWritableDatabase());
                }
                RestoreDbTask.setPending(getContext(), false);
            }
        }
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Uri uri2 = uri;
        createDbIfNotExists();
        SqlArguments args = new SqlArguments(uri2, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);
        Cursor result = qb.query(this.mOpenHelper.getWritableDatabase(), projection, args.where, args.args, (String) null, (String) null, sortOrder);
        result.setNotificationUri(getContext().getContentResolver(), uri2);
        return result;
    }

    static long dbInsertAndCheck(DatabaseHelper helper, SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
        if (values == null) {
            throw new RuntimeException("Error: attempting to insert null values");
        } else if (values.containsKey("_id")) {
            helper.checkId(table, values);
            return db.insert(table, nullColumnHack, values);
        } else {
            throw new RuntimeException("Error: attempting to add item without specifying an id");
        }
    }

    private void reloadLauncherIfExternal() {
        LauncherAppState app;
        if (Utilities.ATLEAST_MARSHMALLOW && Binder.getCallingPid() != Process.myPid() && (app = LauncherAppState.getInstanceNoCreate()) != null) {
            app.getModel().forceReload();
        }
    }

    public Uri insert(Uri uri, ContentValues initialValues) {
        createDbIfNotExists();
        SqlArguments args = new SqlArguments(uri);
        if (Binder.getCallingPid() != Process.myPid() && !initializeExternalAdd(initialValues)) {
            return null;
        }
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        addModifiedTime(initialValues);
        long rowId = dbInsertAndCheck(this.mOpenHelper, db, args.table, (String) null, initialValues);
        if (rowId < 0) {
            return null;
        }
        Uri uri2 = ContentUris.withAppendedId(uri, rowId);
        notifyListeners();
        if (Utilities.ATLEAST_MARSHMALLOW) {
            reloadLauncherIfExternal();
        } else {
            LauncherAppState app = LauncherAppState.getInstanceNoCreate();
            if (app != null && "true".equals(uri2.getQueryParameter("isExternalAdd"))) {
                app.getModel().forceReload();
            }
            String notify = uri2.getQueryParameter("notify");
            if (notify == null || "true".equals(notify)) {
                getContext().getContentResolver().notifyChange(uri2, (ContentObserver) null);
            }
        }
        return uri2;
    }

    private boolean initializeExternalAdd(ContentValues values) {
        values.put("_id", Long.valueOf(this.mOpenHelper.generateNewItemId()));
        Integer itemType = values.getAsInteger(LauncherSettings.BaseLauncherColumns.ITEM_TYPE);
        if (itemType != null && itemType.intValue() == 4 && !values.containsKey(LauncherSettings.Favorites.APPWIDGET_ID)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
            ComponentName cn = ComponentName.unflattenFromString(values.getAsString(LauncherSettings.Favorites.APPWIDGET_PROVIDER));
            if (cn == null) {
                return false;
            }
            try {
                AppWidgetHost widgetHost = this.mOpenHelper.newLauncherWidgetHost();
                int appWidgetId = widgetHost.allocateAppWidgetId();
                values.put(LauncherSettings.Favorites.APPWIDGET_ID, Integer.valueOf(appWidgetId));
                if (!appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, cn)) {
                    widgetHost.deleteAppWidgetId(appWidgetId);
                    return false;
                }
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to initialize external widget", e);
                return false;
            }
        }
        long screenId = values.getAsLong(LauncherSettings.Favorites.SCREEN).longValue();
        SQLiteStatement stmp = null;
        try {
            stmp = this.mOpenHelper.getWritableDatabase().compileStatement("INSERT OR IGNORE INTO workspaceScreens (_id, screenRank) select ?, (ifnull(MAX(screenRank), -1)+1) from workspaceScreens");
            stmp.bindLong(1, screenId);
            ContentValues valuesInserted = new ContentValues();
            valuesInserted.put("_id", Long.valueOf(stmp.executeInsert()));
            this.mOpenHelper.checkId(LauncherSettings.WorkspaceScreens.TABLE_NAME, valuesInserted);
            return true;
        } catch (Exception e2) {
            return false;
        } finally {
            Utilities.closeSilently(stmp);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0044, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0048, code lost:
        if (r3 != null) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x004e, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x004f, code lost:
        r3.addSuppressed(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0053, code lost:
        r2.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int bulkInsert(android.net.Uri r12, android.content.ContentValues[] r13) {
        /*
            r11 = this;
            r11.createDbIfNotExists()
            com.android.launcher3.LauncherProvider$SqlArguments r0 = new com.android.launcher3.LauncherProvider$SqlArguments
            r0.<init>(r12)
            com.android.launcher3.LauncherProvider$DatabaseHelper r1 = r11.mOpenHelper
            android.database.sqlite.SQLiteDatabase r1 = r1.getWritableDatabase()
            com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r2 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction
            r2.<init>(r1)
            r3 = 0
            int r4 = r13.length     // Catch:{ Throwable -> 0x0046 }
            r5 = 0
            r6 = 0
        L_0x0017:
            if (r6 >= r4) goto L_0x0036
            r7 = r13[r6]     // Catch:{ Throwable -> 0x0046 }
            addModifiedTime(r7)     // Catch:{ Throwable -> 0x0046 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r7 = r11.mOpenHelper     // Catch:{ Throwable -> 0x0046 }
            java.lang.String r8 = r0.table     // Catch:{ Throwable -> 0x0046 }
            r9 = r13[r6]     // Catch:{ Throwable -> 0x0046 }
            long r7 = dbInsertAndCheck(r7, r1, r8, r3, r9)     // Catch:{ Throwable -> 0x0046 }
            r9 = 0
            int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r7 >= 0) goto L_0x0033
            r2.close()
            return r5
        L_0x0033:
            int r6 = r6 + 1
            goto L_0x0017
        L_0x0036:
            r2.commit()     // Catch:{ Throwable -> 0x0046 }
            r2.close()
            r11.notifyListeners()
            r11.reloadLauncherIfExternal()
            int r2 = r13.length
            return r2
        L_0x0044:
            r4 = move-exception
            goto L_0x0048
        L_0x0046:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0044 }
        L_0x0048:
            if (r3 == 0) goto L_0x0053
            r2.close()     // Catch:{ Throwable -> 0x004e }
            goto L_0x0056
        L_0x004e:
            r5 = move-exception
            r3.addSuppressed(r5)
            goto L_0x0056
        L_0x0053:
            r2.close()
        L_0x0056:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.bulkInsert(android.net.Uri, android.content.ContentValues[]):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0028, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0029, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002d, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x001e, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0022, code lost:
        if (r1 != null) goto L_0x0024;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.ContentProviderResult[] applyBatch(java.util.ArrayList<android.content.ContentProviderOperation> r5) throws android.content.OperationApplicationException {
        /*
            r4 = this;
            r4.createDbIfNotExists()
            com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r0 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction
            com.android.launcher3.LauncherProvider$DatabaseHelper r1 = r4.mOpenHelper
            android.database.sqlite.SQLiteDatabase r1 = r1.getWritableDatabase()
            r0.<init>(r1)
            r1 = 0
            android.content.ContentProviderResult[] r2 = super.applyBatch(r5)     // Catch:{ Throwable -> 0x0020 }
            r0.commit()     // Catch:{ Throwable -> 0x0020 }
            r4.reloadLauncherIfExternal()     // Catch:{ Throwable -> 0x0020 }
            r0.close()
            return r2
        L_0x001e:
            r2 = move-exception
            goto L_0x0022
        L_0x0020:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001e }
        L_0x0022:
            if (r1 == 0) goto L_0x002d
            r0.close()     // Catch:{ Throwable -> 0x0028 }
            goto L_0x0030
        L_0x0028:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x0030
        L_0x002d:
            r0.close()
        L_0x0030:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.applyBatch(java.util.ArrayList):android.content.ContentProviderResult[]");
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        createDbIfNotExists();
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        if (Binder.getCallingPid() != Process.myPid() && LauncherSettings.Favorites.TABLE_NAME.equalsIgnoreCase(args.table)) {
            this.mOpenHelper.removeGhostWidgets(this.mOpenHelper.getWritableDatabase());
        }
        int count = db.delete(args.table, args.where, args.args);
        if (count > 0) {
            notifyListeners();
            reloadLauncherIfExternal();
        }
        return count;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        createDbIfNotExists();
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        addModifiedTime(values);
        int count = this.mOpenHelper.getWritableDatabase().update(args.table, values, args.where, args.args);
        if (count > 0) {
            notifyListeners();
        }
        reloadLauncherIfExternal();
        return count;
    }

    public Bundle call(String method, String arg, Bundle extras) {
        if (Binder.getCallingUid() != Process.myUid()) {
            return null;
        }
        createDbIfNotExists();
        char c = 65535;
        switch (method.hashCode()) {
            case -1999597249:
                if (method.equals(LauncherSettings.Settings.METHOD_DELETE_EMPTY_FOLDERS)) {
                    c = 2;
                    break;
                }
                break;
            case -1565944700:
                if (method.equals(LauncherSettings.Settings.METHOD_REMOVE_GHOST_WIDGETS)) {
                    c = 7;
                    break;
                }
                break;
            case -1107339682:
                if (method.equals(LauncherSettings.Settings.METHOD_NEW_ITEM_ID)) {
                    c = 3;
                    break;
                }
                break;
            case -1029923675:
                if (method.equals(LauncherSettings.Settings.METHOD_NEW_SCREEN_ID)) {
                    c = 4;
                    break;
                }
                break;
            case -1008511191:
                if (method.equals(LauncherSettings.Settings.METHOD_CLEAR_EMPTY_DB_FLAG)) {
                    c = 0;
                    break;
                }
                break;
            case 476749504:
                if (method.equals(LauncherSettings.Settings.METHOD_LOAD_DEFAULT_FAVORITES)) {
                    c = 6;
                    break;
                }
                break;
            case 684076146:
                if (method.equals(LauncherSettings.Settings.METHOD_WAS_EMPTY_DB_CREATED)) {
                    c = 1;
                    break;
                }
                break;
            case 2117515411:
                if (method.equals(LauncherSettings.Settings.METHOD_CREATE_EMPTY_DB)) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                clearFlagEmptyDbCreated();
                return null;
            case 1:
                Bundle result = new Bundle();
                result.putBoolean(LauncherSettings.Settings.EXTRA_VALUE, Utilities.getPrefs(getContext()).getBoolean(EMPTY_DATABASE_CREATED, false));
                return result;
            case 2:
                Bundle result2 = new Bundle();
                result2.putSerializable(LauncherSettings.Settings.EXTRA_VALUE, deleteEmptyFolders());
                return result2;
            case 3:
                Bundle result3 = new Bundle();
                result3.putLong(LauncherSettings.Settings.EXTRA_VALUE, this.mOpenHelper.generateNewItemId());
                return result3;
            case 4:
                Bundle result4 = new Bundle();
                result4.putLong(LauncherSettings.Settings.EXTRA_VALUE, this.mOpenHelper.generateNewScreenId());
                return result4;
            case 5:
                this.mOpenHelper.createEmptyDB(this.mOpenHelper.getWritableDatabase());
                return null;
            case 6:
                loadDefaultFavoritesIfNecessary();
                return null;
            case 7:
                this.mOpenHelper.removeGhostWidgets(this.mOpenHelper.getWritableDatabase());
                return null;
            default:
                return null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0046, code lost:
        r3 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0047, code lost:
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004b, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004c, code lost:
        r12 = r4;
        r4 = r3;
        r3 = r12;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.ArrayList<java.lang.Long> deleteEmptyFolders() {
        /*
            r13 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            com.android.launcher3.LauncherProvider$DatabaseHelper r1 = r13.mOpenHelper
            android.database.sqlite.SQLiteDatabase r1 = r1.getWritableDatabase()
            com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r2 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction     // Catch:{ SQLException -> 0x0074 }
            r2.<init>(r1)     // Catch:{ SQLException -> 0x0074 }
            r10 = r2
            r11 = 0
            java.lang.String r5 = "itemType = 2 AND _id NOT IN (SELECT container FROM favorites)"
            java.lang.String r3 = "favorites"
            java.lang.String r2 = "_id"
            java.lang.String[] r4 = new java.lang.String[]{r2}     // Catch:{ Throwable -> 0x0062 }
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r2 = r1
            android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Throwable -> 0x0062 }
            r3 = 0
            com.android.launcher3.provider.LauncherDbUtils.iterateCursor(r2, r3, r0)     // Catch:{ Throwable -> 0x0049, all -> 0x0046 }
            if (r2 == 0) goto L_0x002e
            r2.close()     // Catch:{ Throwable -> 0x0062 }
        L_0x002e:
            boolean r2 = r0.isEmpty()     // Catch:{ Throwable -> 0x0062 }
            if (r2 != 0) goto L_0x003f
            java.lang.String r2 = "favorites"
            java.lang.String r3 = "_id"
            java.lang.String r3 = com.android.launcher3.Utilities.createDbSelectionQuery(r3, r0)     // Catch:{ Throwable -> 0x0062 }
            r1.delete(r2, r3, r11)     // Catch:{ Throwable -> 0x0062 }
        L_0x003f:
            r10.commit()     // Catch:{ Throwable -> 0x0062 }
            r10.close()     // Catch:{ SQLException -> 0x0074 }
            goto L_0x0081
        L_0x0046:
            r3 = move-exception
            r4 = r11
            goto L_0x004f
        L_0x0049:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x004b }
        L_0x004b:
            r4 = move-exception
            r12 = r4
            r4 = r3
            r3 = r12
        L_0x004f:
            if (r2 == 0) goto L_0x005f
            if (r4 == 0) goto L_0x005c
            r2.close()     // Catch:{ Throwable -> 0x0057 }
            goto L_0x005f
        L_0x0057:
            r6 = move-exception
            r4.addSuppressed(r6)     // Catch:{ Throwable -> 0x0062 }
            goto L_0x005f
        L_0x005c:
            r2.close()     // Catch:{ Throwable -> 0x0062 }
        L_0x005f:
            throw r3     // Catch:{ Throwable -> 0x0062 }
        L_0x0060:
            r2 = move-exception
            goto L_0x0065
        L_0x0062:
            r2 = move-exception
            r11 = r2
            throw r11     // Catch:{ all -> 0x0060 }
        L_0x0065:
            if (r11 == 0) goto L_0x0070
            r10.close()     // Catch:{ Throwable -> 0x006b }
            goto L_0x0073
        L_0x006b:
            r3 = move-exception
            r11.addSuppressed(r3)     // Catch:{ SQLException -> 0x0074 }
            goto L_0x0073
        L_0x0070:
            r10.close()     // Catch:{ SQLException -> 0x0074 }
        L_0x0073:
            throw r2     // Catch:{ SQLException -> 0x0074 }
        L_0x0074:
            r2 = move-exception
            java.lang.String r3 = "LauncherProvider"
            java.lang.String r4 = r2.getMessage()
            android.util.Log.e(r3, r4, r2)
            r0.clear()
        L_0x0081:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.deleteEmptyFolders():java.util.ArrayList");
    }

    /* access modifiers changed from: protected */
    public void notifyListeners() {
        this.mListenerHandler.sendEmptyMessage(1);
    }

    static void addModifiedTime(ContentValues values) {
        values.put(LauncherSettings.ChangeLogColumns.MODIFIED, Long.valueOf(System.currentTimeMillis()));
    }

    private void clearFlagEmptyDbCreated() {
        Utilities.getPrefs(getContext()).edit().remove(EMPTY_DATABASE_CREATED).commit();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0048, code lost:
        r11 = r10.getResources();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void loadDefaultFavoritesIfNecessary() {
        /*
            r14 = this;
            monitor-enter(r14)
            android.content.Context r0 = r14.getContext()     // Catch:{ all -> 0x00b1 }
            android.content.SharedPreferences r0 = com.android.launcher3.Utilities.getPrefs(r0)     // Catch:{ all -> 0x00b1 }
            java.lang.String r1 = "EMPTY_DATABASE_CREATED"
            r2 = 0
            boolean r1 = r0.getBoolean(r1, r2)     // Catch:{ all -> 0x00b1 }
            if (r1 == 0) goto L_0x00af
            java.lang.String r1 = "LauncherProvider"
            java.lang.String r3 = "loading default workspace"
            android.util.Log.d(r1, r3)     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r1 = r14.mOpenHelper     // Catch:{ all -> 0x00b1 }
            android.appwidget.AppWidgetHost r1 = r1.newLauncherWidgetHost()     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.AutoInstallsLayout r3 = r14.createWorkspaceLoaderFromAppRestriction(r1)     // Catch:{ all -> 0x00b1 }
            if (r3 != 0) goto L_0x0030
            android.content.Context r4 = r14.getContext()     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r5 = r14.mOpenHelper     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.AutoInstallsLayout r4 = com.android.launcher3.AutoInstallsLayout.get(r4, r1, r5)     // Catch:{ all -> 0x00b1 }
            r3 = r4
        L_0x0030:
            r9 = r3
            if (r9 != 0) goto L_0x006c
            android.content.Context r3 = r14.getContext()     // Catch:{ all -> 0x00b1 }
            android.content.pm.PackageManager r3 = r3.getPackageManager()     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.Partner r3 = com.android.launcher3.Partner.get(r3)     // Catch:{ all -> 0x00b1 }
            r10 = r3
            if (r10 == 0) goto L_0x006c
            boolean r3 = r10.hasDefaultLayout()     // Catch:{ all -> 0x00b1 }
            if (r3 == 0) goto L_0x006c
            android.content.res.Resources r3 = r10.getResources()     // Catch:{ all -> 0x00b1 }
            r11 = r3
            java.lang.String r3 = "partner_default_layout"
            java.lang.String r4 = "xml"
            java.lang.String r5 = r10.getPackageName()     // Catch:{ all -> 0x00b1 }
            int r3 = r11.getIdentifier(r3, r4, r5)     // Catch:{ all -> 0x00b1 }
            r12 = r3
            if (r12 == 0) goto L_0x006c
            com.android.launcher3.DefaultLayoutParser r13 = new com.android.launcher3.DefaultLayoutParser     // Catch:{ all -> 0x00b1 }
            android.content.Context r4 = r14.getContext()     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r6 = r14.mOpenHelper     // Catch:{ all -> 0x00b1 }
            r3 = r13
            r5 = r1
            r7 = r11
            r8 = r12
            r3.<init>(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x00b1 }
            r9 = r13
        L_0x006c:
            if (r9 == 0) goto L_0x0070
            r2 = 1
        L_0x0070:
            if (r9 != 0) goto L_0x0077
            com.android.launcher3.DefaultLayoutParser r3 = r14.getDefaultLayoutParser(r1)     // Catch:{ all -> 0x00b1 }
            r9 = r3
        L_0x0077:
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r14.mOpenHelper     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r4 = r14.mOpenHelper     // Catch:{ all -> 0x00b1 }
            android.database.sqlite.SQLiteDatabase r4 = r4.getWritableDatabase()     // Catch:{ all -> 0x00b1 }
            r3.createEmptyDB(r4)     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r14.mOpenHelper     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r4 = r14.mOpenHelper     // Catch:{ all -> 0x00b1 }
            android.database.sqlite.SQLiteDatabase r4 = r4.getWritableDatabase()     // Catch:{ all -> 0x00b1 }
            int r3 = r3.loadFavorites(r4, r9)     // Catch:{ all -> 0x00b1 }
            if (r3 > 0) goto L_0x00ac
            if (r2 == 0) goto L_0x00ac
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r14.mOpenHelper     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r4 = r14.mOpenHelper     // Catch:{ all -> 0x00b1 }
            android.database.sqlite.SQLiteDatabase r4 = r4.getWritableDatabase()     // Catch:{ all -> 0x00b1 }
            r3.createEmptyDB(r4)     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r3 = r14.mOpenHelper     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.LauncherProvider$DatabaseHelper r4 = r14.mOpenHelper     // Catch:{ all -> 0x00b1 }
            android.database.sqlite.SQLiteDatabase r4 = r4.getWritableDatabase()     // Catch:{ all -> 0x00b1 }
            com.android.launcher3.DefaultLayoutParser r5 = r14.getDefaultLayoutParser(r1)     // Catch:{ all -> 0x00b1 }
            r3.loadFavorites(r4, r5)     // Catch:{ all -> 0x00b1 }
        L_0x00ac:
            r14.clearFlagEmptyDbCreated()     // Catch:{ all -> 0x00b1 }
        L_0x00af:
            monitor-exit(r14)
            return
        L_0x00b1:
            r0 = move-exception
            monitor-exit(r14)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.loadDefaultFavoritesIfNecessary():void");
    }

    private AutoInstallsLayout createWorkspaceLoaderFromAppRestriction(AppWidgetHost widgetHost) {
        String packageName;
        Context ctx = getContext();
        Bundle bundle = ((UserManager) ctx.getSystemService("user")).getApplicationRestrictions(ctx.getPackageName());
        if (bundle == null || (packageName = bundle.getString(RESTRICTION_PACKAGE_NAME)) == null) {
            return null;
        }
        try {
            return AutoInstallsLayout.get(ctx, packageName, ctx.getPackageManager().getResourcesForApplication(packageName), widgetHost, this.mOpenHelper);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Target package for restricted profile not found", e);
            return null;
        }
    }

    private DefaultLayoutParser getDefaultLayoutParser(AppWidgetHost widgetHost) {
        InvariantDeviceProfile idp = LauncherAppState.getIDP(getContext());
        int defaultLayout = idp.defaultLayoutId;
        if (UserManagerCompat.getInstance(getContext()).isDemoUser() && idp.demoModeLayoutId != 0) {
            defaultLayout = idp.demoModeLayoutId;
        }
        return new DefaultLayoutParser(getContext(), widgetHost, this.mOpenHelper, getContext().getResources(), defaultLayout);
    }

    public static class DatabaseHelper extends NoLocaleSQLiteHelper implements AutoInstallsLayout.LayoutParserCallback {
        private final Context mContext;
        private long mMaxItemId;
        private long mMaxScreenId;
        private final Handler mWidgetHostResetHandler;

        DatabaseHelper(Context context, Handler widgetHostResetHandler) {
            this(context, widgetHostResetHandler, LauncherFiles.LAUNCHER_DB);
            if (!tableExists(LauncherSettings.Favorites.TABLE_NAME) || !tableExists(LauncherSettings.WorkspaceScreens.TABLE_NAME)) {
                Log.e(LauncherProvider.TAG, "Tables are missing after onCreate has been called. Trying to recreate");
                addFavoritesTable(getWritableDatabase(), true);
                addWorkspacesTable(getWritableDatabase(), true);
            }
            initIds();
        }

        public DatabaseHelper(Context context, Handler widgetHostResetHandler, String tableName) {
            super(context, tableName, 27);
            this.mMaxItemId = -1;
            this.mMaxScreenId = -1;
            this.mContext = context;
            this.mWidgetHostResetHandler = widgetHostResetHandler;
        }

        /* access modifiers changed from: protected */
        public void initIds() {
            if (this.mMaxItemId == -1) {
                this.mMaxItemId = initializeMaxItemId(getWritableDatabase());
            }
            if (this.mMaxScreenId == -1) {
                this.mMaxScreenId = initializeMaxScreenId(getWritableDatabase());
            }
        }

        private boolean tableExists(String tableName) {
            boolean z = true;
            Cursor c = getReadableDatabase().query(true, "sqlite_master", new String[]{"tbl_name"}, "tbl_name = ?", new String[]{tableName}, (String) null, (String) null, (String) null, (String) null, (CancellationSignal) null);
            try {
                if (c.getCount() <= 0) {
                    z = false;
                }
                return z;
            } finally {
                c.close();
            }
        }

        public void onCreate(SQLiteDatabase db) {
            this.mMaxItemId = 1;
            this.mMaxScreenId = 0;
            addFavoritesTable(db, false);
            addWorkspacesTable(db, false);
            this.mMaxItemId = initializeMaxItemId(db);
            onEmptyDbCreated();
        }

        /* access modifiers changed from: protected */
        public void onEmptyDbCreated() {
            if (this.mWidgetHostResetHandler != null) {
                newLauncherWidgetHost().deleteHost();
                this.mWidgetHostResetHandler.sendEmptyMessage(2);
            }
            Utilities.getPrefs(this.mContext).edit().putBoolean(LauncherProvider.EMPTY_DATABASE_CREATED, true).commit();
        }

        public long getDefaultUserSerial() {
            return UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(Process.myUserHandle());
        }

        private void addFavoritesTable(SQLiteDatabase db, boolean optional) {
            LauncherSettings.Favorites.addTableToDb(db, getDefaultUserSerial(), optional);
        }

        private void addWorkspacesTable(SQLiteDatabase db, boolean optional) {
            String ifNotExists = optional ? " IF NOT EXISTS " : "";
            db.execSQL("CREATE TABLE " + ifNotExists + LauncherSettings.WorkspaceScreens.TABLE_NAME + " (" + "_id" + " INTEGER PRIMARY KEY," + LauncherSettings.WorkspaceScreens.SCREEN_RANK + " INTEGER," + LauncherSettings.ChangeLogColumns.MODIFIED + " INTEGER NOT NULL DEFAULT 0);");
        }

        private void removeOrphanedItems(SQLiteDatabase db) {
            db.execSQL("DELETE FROM favorites WHERE screen NOT IN (SELECT _id FROM workspaceScreens) AND container = -100");
            db.execSQL("DELETE FROM favorites WHERE container <> -100 AND container <> -101 AND container NOT IN (SELECT _id FROM favorites WHERE itemType = 2)");
        }

        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            File schemaFile = this.mContext.getFileStreamPath(LauncherProvider.DOWNGRADE_SCHEMA_FILE);
            if (!schemaFile.exists()) {
                handleOneTimeDataUpgrade(db);
            }
            DbDowngradeHelper.updateSchemaFile(schemaFile, 27, this.mContext, R.raw.downgrade_schema);
        }

        /* access modifiers changed from: protected */
        public void handleOneTimeDataUpgrade(SQLiteDatabase db) {
            UserManagerCompat um = UserManagerCompat.getInstance(this.mContext);
            for (UserHandle user : um.getUserProfiles()) {
                long serial = um.getSerialNumberForUser(user);
                db.execSQL("update favorites set intent = replace(intent, ';l.profile=" + serial + ";', ';') where itemType = 0;");
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:18:0x003b, code lost:
            if (addIntegerColumn(r6, com.android.launcher3.LauncherSettings.Favorites.RESTORED, 0) == false) goto L_0x00ab;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x003f, code lost:
            removeOrphanedItems(r6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0046, code lost:
            if (addProfileColumn(r6) != false) goto L_0x004a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x004f, code lost:
            if (updateFolderItemsRank(r6, true) == false) goto L_0x00ab;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0056, code lost:
            if (recreateWorkspaceTable(r6) == false) goto L_0x00ab;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x005f, code lost:
            if (addIntegerColumn(r6, com.android.launcher3.LauncherSettings.Favorites.OPTIONS, 0) == false) goto L_0x00ab;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0062, code lost:
            convertShortcutsToLauncherActivities(r6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x006b, code lost:
            if (com.android.launcher3.provider.LauncherDbUtils.prepareScreenZeroToHostQsb(r5.mContext, r6) != false) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:45:0x008d, code lost:
            r1 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x0091, code lost:
            if (r0 != null) goto L_0x0093;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:4:?, code lost:
            r3 = new com.android.launcher3.provider.LauncherDbUtils.SQLiteTransaction(r6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:51:?, code lost:
            r3.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:52:0x0097, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:?, code lost:
            r0.addSuppressed(r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x009c, code lost:
            r3.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:0x00a0, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x00a1, code lost:
            android.util.Log.e(com.android.launcher3.LauncherProvider.TAG, r0.getMessage(), r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            r6.execSQL("ALTER TABLE favorites ADD COLUMN appWidgetProvider TEXT;");
            r3.commit();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:?, code lost:
            r3.close();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onUpgrade(android.database.sqlite.SQLiteDatabase r6, int r7, int r8) {
            /*
                r5 = this;
                r0 = 0
                r1 = 0
                switch(r7) {
                    case 12: goto L_0x0008;
                    case 13: goto L_0x000e;
                    case 14: goto L_0x001f;
                    case 15: goto L_0x0035;
                    case 16: goto L_0x003f;
                    case 17: goto L_0x003f;
                    case 18: goto L_0x003f;
                    case 19: goto L_0x0042;
                    case 20: goto L_0x004a;
                    case 21: goto L_0x0052;
                    case 22: goto L_0x0059;
                    case 23: goto L_0x0062;
                    case 24: goto L_0x0062;
                    case 25: goto L_0x0062;
                    case 26: goto L_0x0065;
                    case 27: goto L_0x006e;
                    default: goto L_0x0006;
                }
            L_0x0006:
                goto L_0x00ab
            L_0x0008:
                r5.mMaxScreenId = r1
                r3 = 0
                r5.addWorkspacesTable(r6, r3)
            L_0x000e:
                com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r3 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction     // Catch:{ SQLException -> 0x00a0 }
                r3.<init>(r6)     // Catch:{ SQLException -> 0x00a0 }
                java.lang.String r4 = "ALTER TABLE favorites ADD COLUMN appWidgetProvider TEXT;"
                r6.execSQL(r4)     // Catch:{ Throwable -> 0x008f }
                r3.commit()     // Catch:{ Throwable -> 0x008f }
                r3.close()     // Catch:{ SQLException -> 0x00a0 }
            L_0x001f:
                com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r3 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction     // Catch:{ SQLException -> 0x0082 }
                r3.<init>(r6)     // Catch:{ SQLException -> 0x0082 }
                java.lang.String r4 = "ALTER TABLE favorites ADD COLUMN modified INTEGER NOT NULL DEFAULT 0;"
                r6.execSQL(r4)     // Catch:{ Throwable -> 0x0071 }
                java.lang.String r4 = "ALTER TABLE workspaceScreens ADD COLUMN modified INTEGER NOT NULL DEFAULT 0;"
                r6.execSQL(r4)     // Catch:{ Throwable -> 0x0071 }
                r3.commit()     // Catch:{ Throwable -> 0x0071 }
                r3.close()     // Catch:{ SQLException -> 0x0082 }
            L_0x0035:
                java.lang.String r0 = "restored"
                boolean r0 = r5.addIntegerColumn(r6, r0, r1)
                if (r0 != 0) goto L_0x003f
                goto L_0x00ab
            L_0x003f:
                r5.removeOrphanedItems(r6)
            L_0x0042:
                boolean r0 = r5.addProfileColumn(r6)
                if (r0 != 0) goto L_0x004a
                goto L_0x00ab
            L_0x004a:
                r0 = 1
                boolean r0 = r5.updateFolderItemsRank(r6, r0)
                if (r0 != 0) goto L_0x0052
                goto L_0x00ab
            L_0x0052:
                boolean r0 = r5.recreateWorkspaceTable(r6)
                if (r0 != 0) goto L_0x0059
                goto L_0x00ab
            L_0x0059:
                java.lang.String r0 = "options"
                boolean r0 = r5.addIntegerColumn(r6, r0, r1)
                if (r0 != 0) goto L_0x0062
                goto L_0x00ab
            L_0x0062:
                r5.convertShortcutsToLauncherActivities(r6)
            L_0x0065:
                android.content.Context r0 = r5.mContext
                boolean r0 = com.android.launcher3.provider.LauncherDbUtils.prepareScreenZeroToHostQsb(r0, r6)
                if (r0 != 0) goto L_0x006e
                goto L_0x00ab
            L_0x006e:
                return
            L_0x006f:
                r1 = move-exception
                goto L_0x0073
            L_0x0071:
                r0 = move-exception
                throw r0     // Catch:{ all -> 0x006f }
            L_0x0073:
                if (r0 == 0) goto L_0x007e
                r3.close()     // Catch:{ Throwable -> 0x0079 }
                goto L_0x0081
            L_0x0079:
                r2 = move-exception
                r0.addSuppressed(r2)     // Catch:{ SQLException -> 0x0082 }
                goto L_0x0081
            L_0x007e:
                r3.close()     // Catch:{ SQLException -> 0x0082 }
            L_0x0081:
                throw r1     // Catch:{ SQLException -> 0x0082 }
            L_0x0082:
                r0 = move-exception
                java.lang.String r1 = "LauncherProvider"
                java.lang.String r2 = r0.getMessage()
                android.util.Log.e(r1, r2, r0)
                goto L_0x00ab
            L_0x008d:
                r1 = move-exception
                goto L_0x0091
            L_0x008f:
                r0 = move-exception
                throw r0     // Catch:{ all -> 0x008d }
            L_0x0091:
                if (r0 == 0) goto L_0x009c
                r3.close()     // Catch:{ Throwable -> 0x0097 }
                goto L_0x009f
            L_0x0097:
                r2 = move-exception
                r0.addSuppressed(r2)     // Catch:{ SQLException -> 0x00a0 }
                goto L_0x009f
            L_0x009c:
                r3.close()     // Catch:{ SQLException -> 0x00a0 }
            L_0x009f:
                throw r1     // Catch:{ SQLException -> 0x00a0 }
            L_0x00a0:
                r0 = move-exception
                java.lang.String r1 = "LauncherProvider"
                java.lang.String r2 = r0.getMessage()
                android.util.Log.e(r1, r2, r0)
            L_0x00ab:
                java.lang.String r0 = "LauncherProvider"
                java.lang.String r1 = "Destroying all old data."
                android.util.Log.w(r0, r1)
                r5.createEmptyDB(r6)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.DatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase, int, int):void");
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                DbDowngradeHelper.parse(this.mContext.getFileStreamPath(LauncherProvider.DOWNGRADE_SCHEMA_FILE)).onDowngrade(db, oldVersion, newVersion);
            } catch (Exception e) {
                Log.d(LauncherProvider.TAG, "Unable to downgrade from: " + oldVersion + " to " + newVersion + ". Wiping databse.", e);
                createEmptyDB(db);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
            r0.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0024, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0025, code lost:
            r1.addSuppressed(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0029, code lost:
            r0.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:5:0x001a, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x001e, code lost:
            if (r1 != null) goto L_0x0020;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void createEmptyDB(android.database.sqlite.SQLiteDatabase r5) {
            /*
                r4 = this;
                com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r0 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction
                r0.<init>(r5)
                r1 = 0
                java.lang.String r2 = "DROP TABLE IF EXISTS favorites"
                r5.execSQL(r2)     // Catch:{ Throwable -> 0x001c }
                java.lang.String r2 = "DROP TABLE IF EXISTS workspaceScreens"
                r5.execSQL(r2)     // Catch:{ Throwable -> 0x001c }
                r4.onCreate(r5)     // Catch:{ Throwable -> 0x001c }
                r0.commit()     // Catch:{ Throwable -> 0x001c }
                r0.close()
                return
            L_0x001a:
                r2 = move-exception
                goto L_0x001e
            L_0x001c:
                r1 = move-exception
                throw r1     // Catch:{ all -> 0x001a }
            L_0x001e:
                if (r1 == 0) goto L_0x0029
                r0.close()     // Catch:{ Throwable -> 0x0024 }
                goto L_0x002c
            L_0x0024:
                r3 = move-exception
                r1.addSuppressed(r3)
                goto L_0x002c
            L_0x0029:
                r0.close()
            L_0x002c:
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.DatabaseHelper.createEmptyDB(android.database.sqlite.SQLiteDatabase):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0036, code lost:
            if (r3 == null) goto L_0x003b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
            r3.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x003b, code lost:
            r3 = r1.length;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x003d, code lost:
            if (r6 >= r3) goto L_0x0069;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x003f, code lost:
            r4 = r1[r6];
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0049, code lost:
            if (r2.contains(java.lang.Integer.valueOf(r4)) != false) goto L_0x0066;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
            com.android.launcher3.logging.FileLog.d(com.android.launcher3.LauncherProvider.TAG, "Deleting invalid widget " + r4);
            r0.deleteAppWidgetId(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0069, code lost:
            return;
         */
        @android.annotation.TargetApi(26)
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void removeGhostWidgets(android.database.sqlite.SQLiteDatabase r12) {
            /*
                r11 = this;
                android.appwidget.AppWidgetHost r0 = r11.newLauncherWidgetHost()
                int[] r1 = r0.getAppWidgetIds()     // Catch:{ IncompatibleClassChangeError -> 0x0088 }
                java.util.HashSet r2 = new java.util.HashSet
                r2.<init>()
                java.lang.String r4 = "favorites"
                java.lang.String r3 = "appWidgetId"
                java.lang.String[] r5 = new java.lang.String[]{r3}     // Catch:{ SQLException -> 0x007f }
                java.lang.String r6 = "itemType=4"
                r7 = 0
                r8 = 0
                r9 = 0
                r10 = 0
                r3 = r12
                android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ SQLException -> 0x007f }
                r4 = 0
            L_0x0023:
                boolean r5 = r3.moveToNext()     // Catch:{ Throwable -> 0x006c }
                r6 = 0
                if (r5 == 0) goto L_0x0036
                int r5 = r3.getInt(r6)     // Catch:{ Throwable -> 0x006c }
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Throwable -> 0x006c }
                r2.add(r5)     // Catch:{ Throwable -> 0x006c }
                goto L_0x0023
            L_0x0036:
                if (r3 == 0) goto L_0x003b
                r3.close()     // Catch:{ SQLException -> 0x007f }
            L_0x003b:
                int r3 = r1.length
            L_0x003d:
                if (r6 >= r3) goto L_0x0069
                r4 = r1[r6]
                java.lang.Integer r5 = java.lang.Integer.valueOf(r4)
                boolean r5 = r2.contains(r5)
                if (r5 != 0) goto L_0x0066
                java.lang.String r5 = "LauncherProvider"
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x0065 }
                r7.<init>()     // Catch:{ RuntimeException -> 0x0065 }
                java.lang.String r8 = "Deleting invalid widget "
                r7.append(r8)     // Catch:{ RuntimeException -> 0x0065 }
                r7.append(r4)     // Catch:{ RuntimeException -> 0x0065 }
                java.lang.String r7 = r7.toString()     // Catch:{ RuntimeException -> 0x0065 }
                com.android.launcher3.logging.FileLog.d(r5, r7)     // Catch:{ RuntimeException -> 0x0065 }
                r0.deleteAppWidgetId(r4)     // Catch:{ RuntimeException -> 0x0065 }
                goto L_0x0066
            L_0x0065:
                r5 = move-exception
            L_0x0066:
                int r6 = r6 + 1
                goto L_0x003d
            L_0x0069:
                return
            L_0x006a:
                r5 = move-exception
                goto L_0x006e
            L_0x006c:
                r4 = move-exception
                throw r4     // Catch:{ all -> 0x006a }
            L_0x006e:
                if (r3 == 0) goto L_0x007e
                if (r4 == 0) goto L_0x007b
                r3.close()     // Catch:{ Throwable -> 0x0076 }
                goto L_0x007e
            L_0x0076:
                r6 = move-exception
                r4.addSuppressed(r6)     // Catch:{ SQLException -> 0x007f }
                goto L_0x007e
            L_0x007b:
                r3.close()     // Catch:{ SQLException -> 0x007f }
            L_0x007e:
                throw r5     // Catch:{ SQLException -> 0x007f }
            L_0x007f:
                r3 = move-exception
                java.lang.String r4 = "LauncherProvider"
                java.lang.String r5 = "Error getting widgets list"
                android.util.Log.w(r4, r5, r3)
                return
            L_0x0088:
                r1 = move-exception
                java.lang.String r2 = "LauncherProvider"
                java.lang.String r3 = "getAppWidgetIds not supported"
                android.util.Log.e(r2, r3, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.DatabaseHelper.removeGhostWidgets(android.database.sqlite.SQLiteDatabase):void");
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x0080, code lost:
            r4 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x0081, code lost:
            r5 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x0085, code lost:
            r5 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x0086, code lost:
            r11 = r5;
            r5 = r4;
            r4 = r11;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x009a, code lost:
            r3 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:50:0x009b, code lost:
            r4 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x009f, code lost:
            r4 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x00a0, code lost:
            r11 = r4;
            r4 = r3;
            r3 = r11;
         */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x009a A[ExcHandler: all (th java.lang.Throwable)] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void convertShortcutsToLauncherActivities(android.database.sqlite.SQLiteDatabase r13) {
            /*
                r12 = this;
                com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r0 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction     // Catch:{ SQLException -> 0x00c7 }
                r0.<init>(r13)     // Catch:{ SQLException -> 0x00c7 }
                r1 = 0
                java.lang.String r3 = "favorites"
                java.lang.String r2 = "_id"
                java.lang.String r4 = "intent"
                java.lang.String[] r4 = new java.lang.String[]{r2, r4}     // Catch:{ Throwable -> 0x00b6 }
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x00b6 }
                r2.<init>()     // Catch:{ Throwable -> 0x00b6 }
                java.lang.String r5 = "itemType=1 AND profileId="
                r2.append(r5)     // Catch:{ Throwable -> 0x00b6 }
                long r5 = r12.getDefaultUserSerial()     // Catch:{ Throwable -> 0x00b6 }
                r2.append(r5)     // Catch:{ Throwable -> 0x00b6 }
                java.lang.String r5 = r2.toString()     // Catch:{ Throwable -> 0x00b6 }
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 0
                r2 = r13
                android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Throwable -> 0x00b6 }
                java.lang.String r3 = "UPDATE favorites SET itemType=0 WHERE _id=?"
                android.database.sqlite.SQLiteStatement r3 = r13.compileStatement(r3)     // Catch:{ Throwable -> 0x009d, all -> 0x009a }
                java.lang.String r4 = "_id"
                int r4 = r2.getColumnIndexOrThrow(r4)     // Catch:{ Throwable -> 0x0083, all -> 0x0080 }
                java.lang.String r5 = "intent"
                int r5 = r2.getColumnIndexOrThrow(r5)     // Catch:{ Throwable -> 0x0083, all -> 0x0080 }
            L_0x0042:
                boolean r6 = r2.moveToNext()     // Catch:{ Throwable -> 0x0083, all -> 0x0080 }
                if (r6 == 0) goto L_0x006f
                java.lang.String r6 = r2.getString(r5)     // Catch:{ Throwable -> 0x0083, all -> 0x0080 }
                r7 = 0
                android.content.Intent r7 = android.content.Intent.parseUri(r6, r7)     // Catch:{ URISyntaxException -> 0x0066 }
                boolean r8 = com.android.launcher3.Utilities.isLauncherAppTarget(r7)     // Catch:{ Throwable -> 0x0083, all -> 0x0080 }
                if (r8 != 0) goto L_0x005a
                goto L_0x0042
            L_0x005a:
                long r8 = r2.getLong(r4)     // Catch:{ Throwable -> 0x0083, all -> 0x0080 }
                r10 = 1
                r3.bindLong(r10, r8)     // Catch:{ Throwable -> 0x0083, all -> 0x0080 }
                r3.executeUpdateDelete()     // Catch:{ Throwable -> 0x0083, all -> 0x0080 }
                goto L_0x0042
            L_0x0066:
                r7 = move-exception
                java.lang.String r8 = "LauncherProvider"
                java.lang.String r9 = "Unable to parse intent"
                android.util.Log.e(r8, r9, r7)     // Catch:{ Throwable -> 0x0083, all -> 0x0080 }
                goto L_0x0042
            L_0x006f:
                r0.commit()     // Catch:{ Throwable -> 0x0083, all -> 0x0080 }
                if (r3 == 0) goto L_0x0077
                r3.close()     // Catch:{ Throwable -> 0x009d, all -> 0x009a }
            L_0x0077:
                if (r2 == 0) goto L_0x007c
                r2.close()     // Catch:{ Throwable -> 0x00b6 }
            L_0x007c:
                r0.close()     // Catch:{ SQLException -> 0x00c7 }
                goto L_0x00cf
            L_0x0080:
                r4 = move-exception
                r5 = r1
                goto L_0x0089
            L_0x0083:
                r4 = move-exception
                throw r4     // Catch:{ all -> 0x0085 }
            L_0x0085:
                r5 = move-exception
                r11 = r5
                r5 = r4
                r4 = r11
            L_0x0089:
                if (r3 == 0) goto L_0x0099
                if (r5 == 0) goto L_0x0096
                r3.close()     // Catch:{ Throwable -> 0x0091, all -> 0x009a }
                goto L_0x0099
            L_0x0091:
                r6 = move-exception
                r5.addSuppressed(r6)     // Catch:{ Throwable -> 0x009d, all -> 0x009a }
                goto L_0x0099
            L_0x0096:
                r3.close()     // Catch:{ Throwable -> 0x009d, all -> 0x009a }
            L_0x0099:
                throw r4     // Catch:{ Throwable -> 0x009d, all -> 0x009a }
            L_0x009a:
                r3 = move-exception
                r4 = r1
                goto L_0x00a3
            L_0x009d:
                r3 = move-exception
                throw r3     // Catch:{ all -> 0x009f }
            L_0x009f:
                r4 = move-exception
                r11 = r4
                r4 = r3
                r3 = r11
            L_0x00a3:
                if (r2 == 0) goto L_0x00b3
                if (r4 == 0) goto L_0x00b0
                r2.close()     // Catch:{ Throwable -> 0x00ab }
                goto L_0x00b3
            L_0x00ab:
                r5 = move-exception
                r4.addSuppressed(r5)     // Catch:{ Throwable -> 0x00b6 }
                goto L_0x00b3
            L_0x00b0:
                r2.close()     // Catch:{ Throwable -> 0x00b6 }
            L_0x00b3:
                throw r3     // Catch:{ Throwable -> 0x00b6 }
            L_0x00b4:
                r2 = move-exception
                goto L_0x00b8
            L_0x00b6:
                r1 = move-exception
                throw r1     // Catch:{ all -> 0x00b4 }
            L_0x00b8:
                if (r1 == 0) goto L_0x00c3
                r0.close()     // Catch:{ Throwable -> 0x00be }
                goto L_0x00c6
            L_0x00be:
                r3 = move-exception
                r1.addSuppressed(r3)     // Catch:{ SQLException -> 0x00c7 }
                goto L_0x00c6
            L_0x00c3:
                r0.close()     // Catch:{ SQLException -> 0x00c7 }
            L_0x00c6:
                throw r2     // Catch:{ SQLException -> 0x00c7 }
            L_0x00c7:
                r0 = move-exception
                java.lang.String r1 = "LauncherProvider"
                java.lang.String r2 = "Error deduping shortcuts"
                android.util.Log.w(r1, r2, r0)
            L_0x00cf:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.DatabaseHelper.convertShortcutsToLauncherActivities(android.database.sqlite.SQLiteDatabase):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:22:0x007f, code lost:
            r4 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0080, code lost:
            r5 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0084, code lost:
            r5 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0085, code lost:
            r11 = r5;
            r5 = r4;
            r4 = r11;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean recreateWorkspaceTable(android.database.sqlite.SQLiteDatabase r13) {
            /*
                r12 = this;
                r0 = 0
                com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r1 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction     // Catch:{ SQLException -> 0x00ac }
                r1.<init>(r13)     // Catch:{ SQLException -> 0x00ac }
                r2 = 0
                java.lang.String r4 = "workspaceScreens"
                java.lang.String r3 = "_id"
                java.lang.String[] r5 = new java.lang.String[]{r3}     // Catch:{ Throwable -> 0x009b }
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = 0
                java.lang.String r10 = "screenRank"
                r3 = r13
                android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Throwable -> 0x009b }
                java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Throwable -> 0x0082, all -> 0x007f }
                java.util.LinkedHashSet r5 = new java.util.LinkedHashSet     // Catch:{ Throwable -> 0x0082, all -> 0x007f }
                r5.<init>()     // Catch:{ Throwable -> 0x0082, all -> 0x007f }
                java.util.Collection r5 = com.android.launcher3.provider.LauncherDbUtils.iterateCursor(r3, r0, r5)     // Catch:{ Throwable -> 0x0082, all -> 0x007f }
                r4.<init>(r5)     // Catch:{ Throwable -> 0x0082, all -> 0x007f }
                if (r3 == 0) goto L_0x002d
                r3.close()     // Catch:{ Throwable -> 0x009b }
            L_0x002d:
                r3 = r4
                java.lang.String r4 = "DROP TABLE IF EXISTS workspaceScreens"
                r13.execSQL(r4)     // Catch:{ Throwable -> 0x009b }
                r12.addWorkspacesTable(r13, r0)     // Catch:{ Throwable -> 0x009b }
                int r4 = r3.size()     // Catch:{ Throwable -> 0x009b }
                r5 = 0
            L_0x003b:
                if (r5 >= r4) goto L_0x0061
                android.content.ContentValues r6 = new android.content.ContentValues     // Catch:{ Throwable -> 0x009b }
                r6.<init>()     // Catch:{ Throwable -> 0x009b }
                java.lang.String r7 = "_id"
                java.lang.Object r8 = r3.get(r5)     // Catch:{ Throwable -> 0x009b }
                java.lang.Long r8 = (java.lang.Long) r8     // Catch:{ Throwable -> 0x009b }
                r6.put(r7, r8)     // Catch:{ Throwable -> 0x009b }
                java.lang.String r7 = "screenRank"
                java.lang.Integer r8 = java.lang.Integer.valueOf(r5)     // Catch:{ Throwable -> 0x009b }
                r6.put(r7, r8)     // Catch:{ Throwable -> 0x009b }
                com.android.launcher3.LauncherProvider.addModifiedTime(r6)     // Catch:{ Throwable -> 0x009b }
                java.lang.String r7 = "workspaceScreens"
                r13.insertOrThrow(r7, r2, r6)     // Catch:{ Throwable -> 0x009b }
                int r5 = r5 + 1
                goto L_0x003b
            L_0x0061:
                r1.commit()     // Catch:{ Throwable -> 0x009b }
                boolean r5 = r3.isEmpty()     // Catch:{ Throwable -> 0x009b }
                if (r5 == 0) goto L_0x006d
                r5 = 0
                goto L_0x0077
            L_0x006d:
                java.lang.Object r5 = java.util.Collections.max(r3)     // Catch:{ Throwable -> 0x009b }
                java.lang.Long r5 = (java.lang.Long) r5     // Catch:{ Throwable -> 0x009b }
                long r5 = r5.longValue()     // Catch:{ Throwable -> 0x009b }
            L_0x0077:
                r12.mMaxScreenId = r5     // Catch:{ Throwable -> 0x009b }
                r1.close()     // Catch:{ SQLException -> 0x00ac }
                r0 = 1
                return r0
            L_0x007f:
                r4 = move-exception
                r5 = r2
                goto L_0x0088
            L_0x0082:
                r4 = move-exception
                throw r4     // Catch:{ all -> 0x0084 }
            L_0x0084:
                r5 = move-exception
                r11 = r5
                r5 = r4
                r4 = r11
            L_0x0088:
                if (r3 == 0) goto L_0x0098
                if (r5 == 0) goto L_0x0095
                r3.close()     // Catch:{ Throwable -> 0x0090 }
                goto L_0x0098
            L_0x0090:
                r6 = move-exception
                r5.addSuppressed(r6)     // Catch:{ Throwable -> 0x009b }
                goto L_0x0098
            L_0x0095:
                r3.close()     // Catch:{ Throwable -> 0x009b }
            L_0x0098:
                throw r4     // Catch:{ Throwable -> 0x009b }
            L_0x0099:
                r3 = move-exception
                goto L_0x009d
            L_0x009b:
                r2 = move-exception
                throw r2     // Catch:{ all -> 0x0099 }
            L_0x009d:
                if (r2 == 0) goto L_0x00a8
                r1.close()     // Catch:{ Throwable -> 0x00a3 }
                goto L_0x00ab
            L_0x00a3:
                r4 = move-exception
                r2.addSuppressed(r4)     // Catch:{ SQLException -> 0x00ac }
                goto L_0x00ab
            L_0x00a8:
                r1.close()     // Catch:{ SQLException -> 0x00ac }
            L_0x00ab:
                throw r3     // Catch:{ SQLException -> 0x00ac }
            L_0x00ac:
                r1 = move-exception
                java.lang.String r2 = "LauncherProvider"
                java.lang.String r3 = r1.getMessage()
                android.util.Log.e(r2, r3, r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherProvider.DatabaseHelper.recreateWorkspaceTable(android.database.sqlite.SQLiteDatabase):boolean");
        }

        /* access modifiers changed from: package-private */
        public boolean updateFolderItemsRank(SQLiteDatabase db, boolean addRankColumn) {
            LauncherDbUtils.SQLiteTransaction t;
            try {
                t = new LauncherDbUtils.SQLiteTransaction(db);
                if (addRankColumn) {
                    db.execSQL("ALTER TABLE favorites ADD COLUMN rank INTEGER NOT NULL DEFAULT 0;");
                }
                Cursor c = db.rawQuery("SELECT container, MAX(cellX) FROM favorites WHERE container IN (SELECT _id FROM favorites WHERE itemType = ?) GROUP BY container;", new String[]{Integer.toString(2)});
                while (c.moveToNext()) {
                    db.execSQL("UPDATE favorites SET rank=cellX+(cellY*?) WHERE container=? AND cellX IS NOT NULL AND cellY IS NOT NULL;", new Object[]{Long.valueOf(c.getLong(1) + 1), Long.valueOf(c.getLong(0))});
                }
                c.close();
                t.commit();
                t.close();
                return true;
            } catch (SQLException ex) {
                Log.e(LauncherProvider.TAG, ex.getMessage(), ex);
                return false;
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
            throw th;
        }

        private boolean addProfileColumn(SQLiteDatabase db) {
            return addIntegerColumn(db, LauncherSettings.Favorites.PROFILE_ID, getDefaultUserSerial());
        }

        private boolean addIntegerColumn(SQLiteDatabase db, String columnName, long defaultValue) {
            LauncherDbUtils.SQLiteTransaction t;
            try {
                t = new LauncherDbUtils.SQLiteTransaction(db);
                db.execSQL("ALTER TABLE favorites ADD COLUMN " + columnName + " INTEGER NOT NULL DEFAULT " + defaultValue + ";");
                t.commit();
                t.close();
                return true;
            } catch (SQLException ex) {
                Log.e(LauncherProvider.TAG, ex.getMessage(), ex);
                return false;
            } catch (Throwable th) {
                r1.addSuppressed(th);
            }
            throw th;
        }

        public long generateNewItemId() {
            if (this.mMaxItemId >= 0) {
                this.mMaxItemId++;
                return this.mMaxItemId;
            }
            throw new RuntimeException("Error: max item id was not initialized");
        }

        public AppWidgetHost newLauncherWidgetHost() {
            return new LauncherAppWidgetHost(this.mContext);
        }

        public long insertAndCheck(SQLiteDatabase db, ContentValues values) {
            return LauncherProvider.dbInsertAndCheck(this, db, LauncherSettings.Favorites.TABLE_NAME, (String) null, values);
        }

        public void checkId(String table, ContentValues values) {
            long id = values.getAsLong("_id").longValue();
            if (LauncherSettings.WorkspaceScreens.TABLE_NAME.equals(table)) {
                this.mMaxScreenId = Math.max(id, this.mMaxScreenId);
            } else {
                this.mMaxItemId = Math.max(id, this.mMaxItemId);
            }
        }

        private long initializeMaxItemId(SQLiteDatabase db) {
            return LauncherProvider.getMaxId(db, LauncherSettings.Favorites.TABLE_NAME);
        }

        public long generateNewScreenId() {
            if (this.mMaxScreenId >= 0) {
                this.mMaxScreenId++;
                return this.mMaxScreenId;
            }
            throw new RuntimeException("Error: max screen id was not initialized");
        }

        private long initializeMaxScreenId(SQLiteDatabase db) {
            return LauncherProvider.getMaxId(db, LauncherSettings.WorkspaceScreens.TABLE_NAME);
        }

        /* access modifiers changed from: package-private */
        public int loadFavorites(SQLiteDatabase db, AutoInstallsLayout loader) {
            ArrayList<Long> screenIds = new ArrayList<>();
            int count = loader.loadLayout(db, screenIds);
            Collections.sort(screenIds);
            int rank = 0;
            ContentValues values = new ContentValues();
            Iterator<Long> it = screenIds.iterator();
            while (it.hasNext()) {
                values.clear();
                values.put("_id", it.next());
                values.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, Integer.valueOf(rank));
                if (LauncherProvider.dbInsertAndCheck(this, db, LauncherSettings.WorkspaceScreens.TABLE_NAME, (String) null, values) >= 0) {
                    rank++;
                } else {
                    throw new RuntimeException("Failed initialize screen tablefrom default layout");
                }
            }
            this.mMaxItemId = initializeMaxItemId(db);
            this.mMaxScreenId = initializeMaxScreenId(db);
            return count;
        }
    }

    static long getMaxId(SQLiteDatabase db, String table) {
        Cursor c = db.rawQuery("SELECT MAX(_id) FROM " + table, (String[]) null);
        long id = -1;
        if (c != null && c.moveToNext()) {
            id = c.getLong(0);
        }
        if (c != null) {
            c.close();
        }
        if (id != -1) {
            return id;
        }
        throw new RuntimeException("Error: could not query max id in " + table);
    }

    static class SqlArguments {
        public final String[] args;
        public final String table;
        public final String where;

        SqlArguments(Uri url, String where2, String[] args2) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = where2;
                this.args = args2;
            } else if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            } else if (TextUtils.isEmpty(where2)) {
                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);
                this.args = null;
            } else {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            }
        }

        SqlArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = null;
                this.args = null;
                return;
            }
            throw new IllegalArgumentException("Invalid URI: " + url);
        }
    }

    private static class ChangeListenerWrapper implements Handler.Callback {
        private static final int MSG_APP_WIDGET_HOST_RESET = 2;
        private static final int MSG_LAUNCHER_PROVIDER_CHANGED = 1;
        /* access modifiers changed from: private */
        public LauncherProviderChangeListener mListener;

        private ChangeListenerWrapper() {
        }

        public boolean handleMessage(Message msg) {
            if (this.mListener == null) {
                return true;
            }
            switch (msg.what) {
                case 1:
                    this.mListener.onLauncherProviderChanged();
                    return true;
                case 2:
                    this.mListener.onAppWidgetHostReset();
                    return true;
                default:
                    return true;
            }
        }
    }
}
