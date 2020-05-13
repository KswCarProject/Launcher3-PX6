package com.szchoiceway.index;

import android.app.SearchManager;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import com.szchoiceway.index.LauncherSettings;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParserException;

public class LauncherProvider extends ContentProvider {
    private static final String ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE = "com.szchoiceway.index.action.APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE";
    static final String AUTHORITY = "com.szchoiceway.index.settings";
    static final Uri CONTENT_APPWIDGET_RESET_URI = Uri.parse("content://com.szchoiceway.index.settings/appWidgetReset");
    private static final String DATABASE_NAME = "index.db";
    private static final int DATABASE_VERSION = 12;
    static final String DB_CREATED_BUT_DEFAULT_WORKSPACE_NOT_LOADED = "DB_CREATED_BUT_DEFAULT_WORKSPACE_NOT_LOADED";
    static final String DEFAULT_WORKSPACE_RESOURCE_ID = "DEFAULT_WORKSPACE_RESOURCE_ID";
    private static final boolean LOGD = false;
    static final String PARAMETER_NOTIFY = "notify";
    static final String StrSaveDBPath = "/data/local/zxwDBUIconfig.ini";
    static final String StrSavePath = "/data/local/zxwUIconfig.ini";
    static final String TABLE_FAVORITES = "favorites";
    private static final String TAG = "index.LauncherProvider";
    public static String UItype = "1";
    private DatabaseHelper mOpenHelper;

    public boolean onCreate() {
        Log.i(TAG, "onCreate() ****** ");
        IniFile file = new IniFile(new File(StrSaveDBPath));
        if (file != null) {
            if (file.get("zxwDBUIconfig", "DBUIType") == null) {
                IniFile Configfile = new IniFile();
                if (Configfile == null) {
                    Log.e(TAG, " New zxwDBUIconfig Error");
                } else {
                    Log.e(TAG, " New zxwDBUIconfig ok");
                }
                Configfile.set("zxwDBUIconfig", "DBUIType", UItype);
                File mfile = new File(StrSaveDBPath);
                if (mfile != null) {
                    Log.e(TAG, " New mfile ok");
                    Configfile.save(mfile);
                    try {
                        Runtime.getRuntime().exec("chmod 777 /data/local/zxwDBUIconfig.ini");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                UItype = file.get("zxwDBUIconfig", "DBUIType").toString();
            }
        }
        Log.i(TAG, "UItype ****** = " + UItype);
        this.mOpenHelper = new DatabaseHelper(getContext());
        ((LauncherApplication) getContext()).setLauncherProvider(this);
        return true;
    }

    public String getType(Uri uri) {
        SqlArguments args = new SqlArguments(uri, (String) null, (String[]) null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        }
        return "vnd.android.cursor.item/" + args.table;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);
        Cursor result = qb.query(this.mOpenHelper.getWritableDatabase(), projection, args.where, args.args, (String) null, (String) null, sortOrder);
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    /* access modifiers changed from: private */
    public static long dbInsertAndCheck(DatabaseHelper helper, SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
        if (values.containsKey("_id")) {
            return db.insert(table, nullColumnHack, values);
        }
        throw new RuntimeException("Error: attempting to add item without specifying an id");
    }

    /* access modifiers changed from: private */
    public static void deleteId(SQLiteDatabase db, long id) {
        SqlArguments args = new SqlArguments(LauncherSettings.Favorites.getContentUri(id, false), (String) null, (String[]) null);
        db.delete(args.table, args.where, args.args);
    }

    public Uri insert(Uri uri, ContentValues initialValues) {
        SqlArguments args = new SqlArguments(uri);
        long rowId = dbInsertAndCheck(this.mOpenHelper, this.mOpenHelper.getWritableDatabase(), args.table, (String) null, initialValues);
        if (rowId <= 0) {
            return null;
        }
        Uri uri2 = ContentUris.withAppendedId(uri, rowId);
        sendNotify(uri2);
        return uri2;
    }

    public int bulkInsert(Uri uri, ContentValues[] values) {
        SqlArguments args = new SqlArguments(uri);
        SQLiteDatabase db = this.mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContentValues dbInsertAndCheck : values) {
                if (dbInsertAndCheck(this.mOpenHelper, db, args.table, (String) null, dbInsertAndCheck) < 0) {
                    return 0;
                }
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            sendNotify(uri);
            return values.length;
        } finally {
            db.endTransaction();
        }
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        int count = this.mOpenHelper.getWritableDatabase().delete(args.table, args.where, args.args);
        if (count > 0) {
            sendNotify(uri);
        }
        return count;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        int count = this.mOpenHelper.getWritableDatabase().update(args.table, values, args.where, args.args);
        if (count > 0) {
            sendNotify(uri);
        }
        return count;
    }

    private void sendNotify(Uri uri) {
        String notify = uri.getQueryParameter(PARAMETER_NOTIFY);
        if (notify == null || "true".equals(notify)) {
            getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
        }
    }

    public long generateNewId() {
        return this.mOpenHelper.generateNewId();
    }

    public synchronized void loadDefaultFavoritesIfNecessary(int origWorkspaceResId) {
        SharedPreferences sp = getContext().getSharedPreferences(LauncherApplication.getSharedPreferencesKey(), 0);
        if (sp.getBoolean(DB_CREATED_BUT_DEFAULT_WORKSPACE_NOT_LOADED, false)) {
            int workspaceResId = origWorkspaceResId;
            if (workspaceResId == 0) {
                if (LauncherApplication.SetUITypeVer() == 4) {
                    workspaceResId = sp.getInt(DEFAULT_WORKSPACE_RESOURCE_ID, R.xml.default_workspace_hangrunui1);
                } else if (LauncherApplication.SetUIType() == 5) {
                    workspaceResId = sp.getInt(DEFAULT_WORKSPACE_RESOURCE_ID, R.xml.default_workspace_keshangui4);
                } else {
                    workspaceResId = sp.getInt(DEFAULT_WORKSPACE_RESOURCE_ID, R.xml.default_workspace);
                }
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(DB_CREATED_BUT_DEFAULT_WORKSPACE_NOT_LOADED);
            if (origWorkspaceResId != 0) {
                editor.putInt(DEFAULT_WORKSPACE_RESOURCE_ID, origWorkspaceResId);
            }
            int unused = this.mOpenHelper.loadFavorites(this.mOpenHelper.getWritableDatabase(), workspaceResId);
            editor.commit();
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String TAG_APPWIDGET = "appwidget";
        private static final String TAG_CLOCK = "clock";
        private static final String TAG_EXTRA = "extra";
        private static final String TAG_FAVORITE = "favorite";
        private static final String TAG_FAVORITES = "favorites";
        private static final String TAG_FOLDER = "folder";
        private static final String TAG_SEARCH = "search";
        private static final String TAG_SHORTCUT = "shortcut";
        private final AppWidgetHost mAppWidgetHost;
        private final Context mContext;
        private long mMaxId = -1;

        DatabaseHelper(Context context) {
            super(context, LauncherProvider.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 12);
            Log.i(LauncherProvider.TAG, "DatabaseHelper ******* ");
            IniFile file = new IniFile(new File(LauncherProvider.StrSavePath));
            if (file != null) {
                if (file.get("zxwUIconfig", "UIType") == null) {
                    IniFile Configfile = new IniFile();
                    if (Configfile == null) {
                        Log.e(LauncherProvider.TAG, " New zxwUIconfig Error");
                    } else {
                        Log.e(LauncherProvider.TAG, " New zxwUIconfig ok");
                    }
                    Configfile.set("zxwUIconfig", "UIType", LauncherProvider.UItype);
                    File mfile = new File(LauncherProvider.StrSavePath);
                    if (mfile != null) {
                        Log.e(LauncherProvider.TAG, " New mfile ok");
                        Configfile.save(mfile);
                    } else {
                        Log.e(LauncherProvider.TAG, " New mfile error");
                    }
                } else if (file.get("zxwUIconfig", "UIType").equals(LauncherProvider.UItype)) {
                    Log.e(LauncherProvider.TAG, "UIType = " + LauncherProvider.UItype);
                } else {
                    Log.e(LauncherProvider.TAG, "UIType 0");
                    IniFile Configfile2 = new IniFile();
                    Configfile2.set("zxwUIconfig", "UIType", LauncherProvider.UItype);
                    File mfile2 = new File(LauncherProvider.StrSavePath);
                    if (mfile2 != null) {
                        Log.e(LauncherProvider.TAG, " New mfile ok");
                        Configfile2.save(mfile2);
                    } else {
                        Log.e(LauncherProvider.TAG, " New mfile error");
                    }
                    deleteDatabase(context);
                }
            }
            this.mContext = context;
            this.mAppWidgetHost = new AppWidgetHost(context, 1024);
            if (this.mMaxId == -1) {
                this.mMaxId = initializeMaxId(getWritableDatabase());
            }
        }

        private void sendAppWidgetResetNotify() {
            this.mContext.getContentResolver().notifyChange(LauncherProvider.CONTENT_APPWIDGET_RESET_URI, (ContentObserver) null);
        }

        public void onCreate(SQLiteDatabase db) {
            this.mMaxId = 1;
            db.execSQL("CREATE TABLE favorites (_id INTEGER PRIMARY KEY,title TEXT,intent TEXT,container INTEGER,screen INTEGER,cellX INTEGER,cellY INTEGER,spanX INTEGER,spanY INTEGER,itemType INTEGER,appWidgetId INTEGER NOT NULL DEFAULT -1,isShortcut INTEGER,iconType INTEGER,iconPackage TEXT,iconResource TEXT,icon BLOB,uri TEXT,displayMode INTEGER);");
            if (this.mAppWidgetHost != null) {
                this.mAppWidgetHost.deleteHost();
                sendAppWidgetResetNotify();
            }
            if (!convertDatabase(db)) {
                setFlagToLoadDefaultWorkspaceLater();
            }
        }

        public boolean deleteDatabase(Context context) {
            return context.deleteDatabase(LauncherProvider.DATABASE_NAME);
        }

        private void setFlagToLoadDefaultWorkspaceLater() {
            SharedPreferences.Editor editor = this.mContext.getSharedPreferences(LauncherApplication.getSharedPreferencesKey(), 0).edit();
            editor.putBoolean(LauncherProvider.DB_CREATED_BUT_DEFAULT_WORKSPACE_NOT_LOADED, true);
            editor.commit();
        }

        private boolean convertDatabase(SQLiteDatabase db) {
            boolean converted = false;
            Uri uri = Uri.parse("content://settings/old_favorites?notify=true");
            ContentResolver resolver = this.mContext.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, (String[]) null, (String) null, (String[]) null, (String) null);
            } catch (Exception e) {
            }
            if (cursor != null && cursor.getCount() > 0) {
                try {
                    converted = copyFromCursor(db, cursor) > 0;
                    if (converted) {
                        resolver.delete(uri, (String) null, (String[]) null);
                    }
                } finally {
                    cursor.close();
                }
            }
            if (converted) {
                convertWidgets(db);
            }
            return converted;
        }

        private int copyFromCursor(SQLiteDatabase db, Cursor c) {
            int idIndex = c.getColumnIndexOrThrow("_id");
            int intentIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.INTENT);
            int titleIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.TITLE);
            int iconTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON_TYPE);
            int iconIndex = c.getColumnIndexOrThrow("icon");
            int iconPackageIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE);
            int iconResourceIndex = c.getColumnIndexOrThrow("iconResource");
            int containerIndex = c.getColumnIndexOrThrow("container");
            int itemTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ITEM_TYPE);
            int screenIndex = c.getColumnIndexOrThrow("screen");
            int cellXIndex = c.getColumnIndexOrThrow("cellX");
            int cellYIndex = c.getColumnIndexOrThrow("cellY");
            int uriIndex = c.getColumnIndexOrThrow("uri");
            int displayModeIndex = c.getColumnIndexOrThrow("displayMode");
            ContentValues[] rows = new ContentValues[c.getCount()];
            int i = 0;
            while (c.moveToNext()) {
                ContentValues values = new ContentValues(c.getColumnCount());
                values.put("_id", Long.valueOf(c.getLong(idIndex)));
                values.put(LauncherSettings.BaseLauncherColumns.INTENT, c.getString(intentIndex));
                values.put(LauncherSettings.BaseLauncherColumns.TITLE, c.getString(titleIndex));
                values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, Integer.valueOf(c.getInt(iconTypeIndex)));
                values.put("icon", c.getBlob(iconIndex));
                values.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, c.getString(iconPackageIndex));
                values.put("iconResource", c.getString(iconResourceIndex));
                values.put("container", Integer.valueOf(c.getInt(containerIndex)));
                values.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(c.getInt(itemTypeIndex)));
                values.put("appWidgetId", -1);
                values.put("screen", Integer.valueOf(c.getInt(screenIndex)));
                values.put("cellX", Integer.valueOf(c.getInt(cellXIndex)));
                values.put("cellY", Integer.valueOf(c.getInt(cellYIndex)));
                values.put("uri", c.getString(uriIndex));
                values.put("displayMode", Integer.valueOf(c.getInt(displayModeIndex)));
                rows[i] = values;
                i++;
            }
            db.beginTransaction();
            int total = 0;
            try {
                for (ContentValues access$100 : rows) {
                    if (LauncherProvider.dbInsertAndCheck(this, db, TAG_FAVORITES, (String) null, access$100) < 0) {
                        return 0;
                    }
                    total++;
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                return total;
            } finally {
                db.endTransaction();
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            int version = oldVersion;
            if (version < 3) {
                db.beginTransaction();
                try {
                    db.execSQL("ALTER TABLE favorites ADD COLUMN appWidgetId INTEGER NOT NULL DEFAULT -1;");
                    db.setTransactionSuccessful();
                    version = 3;
                } catch (SQLException ex) {
                    Log.e(LauncherProvider.TAG, ex.getMessage(), ex);
                } finally {
                    db.endTransaction();
                }
                if (version == 3) {
                    convertWidgets(db);
                }
            }
            if (version < 4) {
                version = 4;
            }
            if (version < 6) {
                db.beginTransaction();
                try {
                    db.execSQL("UPDATE favorites SET screen=(screen + 1);");
                    db.setTransactionSuccessful();
                } catch (SQLException ex2) {
                    Log.e(LauncherProvider.TAG, ex2.getMessage(), ex2);
                } finally {
                    db.endTransaction();
                }
                if (updateContactsShortcuts(db)) {
                    version = 6;
                }
            }
            if (version < 7) {
                convertWidgets(db);
                version = 7;
            }
            if (version < 8) {
                normalizeIcons(db);
                version = 8;
            }
            if (version < 9) {
                if (this.mMaxId == -1) {
                    this.mMaxId = initializeMaxId(db);
                }
                loadFavorites(db, R.xml.update_workspace);
                version = 9;
            }
            if (version < 12) {
                updateContactsShortcuts(db);
                version = 12;
            }
            if (version != 12) {
                Log.w(LauncherProvider.TAG, "Destroying all old data.");
                db.execSQL("DROP TABLE IF EXISTS favorites");
                onCreate(db);
            }
        }

        private boolean updateContactsShortcuts(SQLiteDatabase db) {
            String selectWhere = LauncherProvider.buildOrWhereString(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, new int[]{1});
            Cursor c = null;
            db.beginTransaction();
            try {
                Cursor c2 = db.query(TAG_FAVORITES, new String[]{"_id", LauncherSettings.BaseLauncherColumns.INTENT}, selectWhere, (String[]) null, (String) null, (String) null, (String) null);
                if (c2 == null) {
                    db.endTransaction();
                    if (c2 == null) {
                        return false;
                    }
                    c2.close();
                    return false;
                }
                int idIndex = c2.getColumnIndex("_id");
                int intentIndex = c2.getColumnIndex(LauncherSettings.BaseLauncherColumns.INTENT);
                while (c2.moveToNext()) {
                    long favoriteId = c2.getLong(idIndex);
                    String intentUri = c2.getString(intentIndex);
                    if (intentUri != null) {
                        try {
                            Intent intent = Intent.parseUri(intentUri, 0);
                            Log.d("Home", intent.toString());
                            Uri uri = intent.getData();
                            if (uri != null) {
                                String data = uri.toString();
                                if (("android.intent.action.VIEW".equals(intent.getAction()) || "com.android.contacts.action.QUICK_CONTACT".equals(intent.getAction())) && (data.startsWith("content://contacts/people/") || data.startsWith("content://com.android.contacts/contacts/lookup/"))) {
                                    Intent intent2 = new Intent("com.android.contacts.action.QUICK_CONTACT");
                                    intent2.addFlags(268468224);
                                    intent2.putExtra("com.szchoiceway.index.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION", true);
                                    intent2.setData(uri);
                                    intent2.setDataAndType(uri, intent2.resolveType(this.mContext));
                                    ContentValues values = new ContentValues();
                                    values.put(LauncherSettings.BaseLauncherColumns.INTENT, intent2.toUri(0));
                                    db.update(TAG_FAVORITES, values, "_id=" + favoriteId, (String[]) null);
                                }
                            }
                        } catch (RuntimeException ex) {
                            Log.e(LauncherProvider.TAG, "Problem upgrading shortcut", ex);
                        } catch (URISyntaxException e) {
                            Log.e(LauncherProvider.TAG, "Problem upgrading shortcut", e);
                        }
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                if (c2 != null) {
                    c2.close();
                }
                return true;
            } catch (SQLException ex2) {
                Log.w(LauncherProvider.TAG, "Problem while upgrading contacts", ex2);
                db.endTransaction();
                if (c == null) {
                    return false;
                }
                c.close();
                return false;
            } catch (Throwable th) {
                db.endTransaction();
                if (c != null) {
                    c.close();
                }
                throw th;
            }
        }

        private void normalizeIcons(SQLiteDatabase db) {
            Log.d(LauncherProvider.TAG, "normalizing icons");
            db.beginTransaction();
            Cursor c = null;
            SQLiteStatement update = null;
            boolean logged = false;
            try {
                update = db.compileStatement("UPDATE favorites SET icon=? WHERE _id=?");
                c = db.rawQuery("SELECT _id, icon FROM favorites WHERE iconType=1", (String[]) null);
                int idIndex = c.getColumnIndexOrThrow("_id");
                int iconIndex = c.getColumnIndexOrThrow("icon");
                while (c.moveToNext()) {
                    long id = c.getLong(idIndex);
                    byte[] data = c.getBlob(iconIndex);
                    try {
                        Bitmap bitmap = Utilities.resampleIconBitmap(BitmapFactory.decodeByteArray(data, 0, data.length), this.mContext);
                        if (bitmap != null) {
                            update.bindLong(1, id);
                            byte[] data2 = ItemInfo.flattenBitmap(bitmap);
                            if (data2 != null) {
                                update.bindBlob(2, data2);
                                update.execute();
                            }
                            bitmap.recycle();
                        }
                    } catch (Exception e) {
                        if (!logged) {
                            Log.e(LauncherProvider.TAG, "Failed normalizing icon " + id, e);
                        } else {
                            Log.e(LauncherProvider.TAG, "Also failed normalizing icon " + id);
                        }
                        logged = true;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                if (update != null) {
                    update.close();
                }
                if (c != null) {
                    c.close();
                }
            } catch (SQLException ex) {
                Log.w(LauncherProvider.TAG, "Problem while allocating appWidgetIds for existing widgets", ex);
                db.endTransaction();
                if (update != null) {
                    update.close();
                }
                if (c != null) {
                    c.close();
                }
            } catch (Throwable th) {
                db.endTransaction();
                if (update != null) {
                    update.close();
                }
                if (c != null) {
                    c.close();
                }
                throw th;
            }
        }

        public long generateNewId() {
            if (this.mMaxId < 0) {
                throw new RuntimeException("Error: max id was not initialized");
            }
            this.mMaxId++;
            return this.mMaxId;
        }

        private long initializeMaxId(SQLiteDatabase db) {
            Cursor c = db.rawQuery("SELECT MAX(_id) FROM favorites", (String[]) null);
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
            throw new RuntimeException("Error: could not query max id");
        }

        private void convertWidgets(SQLiteDatabase db) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.mContext);
            String selectWhere = LauncherProvider.buildOrWhereString(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, new int[]{1000, PointerIconCompat.TYPE_HAND, PointerIconCompat.TYPE_CONTEXT_MENU});
            Cursor c = null;
            db.beginTransaction();
            try {
                c = db.query(TAG_FAVORITES, new String[]{"_id", LauncherSettings.BaseLauncherColumns.ITEM_TYPE}, selectWhere, (String[]) null, (String) null, (String) null, (String) null);
                ContentValues values = new ContentValues();
                while (c != null && c.moveToNext()) {
                    long favoriteId = c.getLong(0);
                    int favoriteType = c.getInt(1);
                    try {
                        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
                        values.clear();
                        values.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, 4);
                        values.put("appWidgetId", Integer.valueOf(appWidgetId));
                        if (favoriteType == 1001) {
                            values.put("spanX", 4);
                            values.put("spanY", 1);
                        } else {
                            values.put("spanX", 2);
                            values.put("spanY", 2);
                        }
                        db.update(TAG_FAVORITES, values, "_id=" + favoriteId, (String[]) null);
                        if (favoriteType == 1000) {
                            appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, new ComponentName("com.android.alarmclock", "com.android.alarmclock.AnalogAppWidgetProvider"));
                        } else if (favoriteType == 1002) {
                            appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, new ComponentName("com.android.camera", "com.android.camera.PhotoAppWidgetProvider"));
                        } else if (favoriteType == 1001) {
                            appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, getSearchWidgetProvider());
                        }
                    } catch (RuntimeException ex) {
                        Log.e(LauncherProvider.TAG, "Problem allocating appWidgetId", ex);
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                if (c != null) {
                    c.close();
                }
            } catch (SQLException ex2) {
                Log.w(LauncherProvider.TAG, "Problem while allocating appWidgetIds for existing widgets", ex2);
                db.endTransaction();
                if (c != null) {
                    c.close();
                }
            } catch (Throwable th) {
                db.endTransaction();
                if (c != null) {
                    c.close();
                }
                throw th;
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:6:0x000c  */
        /* JADX WARNING: Removed duplicated region for block: B:8:0x0014  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static final void beginDocument(org.xmlpull.v1.XmlPullParser r4, java.lang.String r5) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
            /*
                r2 = 2
            L_0x0001:
                int r0 = r4.next()
                if (r0 == r2) goto L_0x000a
                r1 = 1
                if (r0 != r1) goto L_0x0001
            L_0x000a:
                if (r0 == r2) goto L_0x0014
                org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
                java.lang.String r2 = "No start tag found"
                r1.<init>(r2)
                throw r1
            L_0x0014:
                java.lang.String r1 = r4.getName()
                boolean r1 = r1.equals(r5)
                if (r1 != 0) goto L_0x0045
                org.xmlpull.v1.XmlPullParserException r1 = new org.xmlpull.v1.XmlPullParserException
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "Unexpected start tag: found "
                java.lang.StringBuilder r2 = r2.append(r3)
                java.lang.String r3 = r4.getName()
                java.lang.StringBuilder r2 = r2.append(r3)
                java.lang.String r3 = ", expected "
                java.lang.StringBuilder r2 = r2.append(r3)
                java.lang.StringBuilder r2 = r2.append(r5)
                java.lang.String r2 = r2.toString()
                r1.<init>(r2)
                throw r1
            L_0x0045:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.LauncherProvider.DatabaseHelper.beginDocument(org.xmlpull.v1.XmlPullParser, java.lang.String):void");
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:90:0x0256, code lost:
            throw new java.lang.RuntimeException("Folders can contain only shortcuts");
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int loadFavorites(android.database.sqlite.SQLiteDatabase r41, int r42) {
            /*
                r40 = this;
                android.content.Intent r9 = new android.content.Intent
                java.lang.String r4 = "android.intent.action.MAIN"
                r5 = 0
                r9.<init>(r4, r5)
                java.lang.String r4 = "android.intent.category.LAUNCHER"
                r9.addCategory(r4)
                android.content.ContentValues r6 = new android.content.ContentValues
                r6.<init>()
                r0 = r40
                android.content.Context r4 = r0.mContext
                android.content.pm.PackageManager r8 = r4.getPackageManager()
                r0 = r40
                android.content.Context r4 = r0.mContext
                android.content.res.Resources r4 = r4.getResources()
                r5 = 2131296267(0x7f09000b, float:1.8210446E38)
                int r21 = r4.getInteger(r5)
                r31 = 0
                r0 = r40
                android.content.Context r4 = r0.mContext     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                android.content.res.Resources r4 = r4.getResources()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r0 = r42
                android.content.res.XmlResourceParser r11 = r4.getXml(r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                android.util.AttributeSet r12 = android.util.Xml.asAttributeSet(r11)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                java.lang.String r4 = "favorites"
                beginDocument(r11, r4)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                int r24 = r11.getDepth()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
            L_0x0046:
                int r13 = r11.next()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r4 = 3
                if (r13 != r4) goto L_0x0055
                int r4 = r11.getDepth()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r0 = r24
                if (r4 <= r0) goto L_0x00b4
            L_0x0055:
                r4 = 1
                if (r13 == r4) goto L_0x00b4
                r4 = 2
                if (r13 != r4) goto L_0x0046
                r20 = 0
                java.lang.String r34 = r11.getName()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r0 = r40
                android.content.Context r4 = r0.mContext     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                int[] r5 = com.szchoiceway.index.R.styleable.Favorite     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                android.content.res.TypedArray r7 = r4.obtainStyledAttributes(r12, r5)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r22 = -100
                r4 = 2
                boolean r4 = r7.hasValue(r4)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                if (r4 == 0) goto L_0x0081
                r4 = 2
                java.lang.String r4 = r7.getString(r4)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                java.lang.Long r4 = java.lang.Long.valueOf(r4)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                long r22 = r4.longValue()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
            L_0x0081:
                r4 = 3
                java.lang.String r35 = r7.getString(r4)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r4 = 4
                java.lang.String r38 = r7.getString(r4)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r4 = 5
                java.lang.String r39 = r7.getString(r4)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r4 = -101(0xffffffffffffff9b, double:NaN)
                int r4 = (r22 > r4 ? 1 : (r22 == r4 ? 0 : -1))
                if (r4 != 0) goto L_0x00b5
                java.lang.Integer r4 = java.lang.Integer.valueOf(r35)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                int r4 = r4.intValue()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r0 = r21
                if (r4 != r0) goto L_0x00b5
                java.lang.RuntimeException r4 = new java.lang.RuntimeException     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                java.lang.String r5 = "Invalid screen position for hotseat item"
                r4.<init>(r5)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                throw r4     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
            L_0x00aa:
                r25 = move-exception
                java.lang.String r4 = "index.LauncherProvider"
                java.lang.String r5 = "Got exception parsing favorites."
                r0 = r25
                android.util.Log.w(r4, r5, r0)
            L_0x00b4:
                return r31
            L_0x00b5:
                r6.clear()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                java.lang.String r4 = "container"
                java.lang.Long r5 = java.lang.Long.valueOf(r22)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r6.put(r4, r5)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                java.lang.String r4 = "screen"
                r0 = r35
                r6.put(r4, r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                java.lang.String r4 = "cellX"
                r0 = r38
                r6.put(r4, r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                java.lang.String r4 = "cellY"
                r0 = r39
                r6.put(r4, r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                java.lang.String r4 = "favorite"
                r0 = r34
                boolean r4 = r4.equals(r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                if (r4 == 0) goto L_0x0107
                r4 = r40
                r5 = r41
                long r32 = r4.addAppShortcut(r5, r6, r7, r8, r9)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r4 = 0
                int r4 = (r32 > r4 ? 1 : (r32 == r4 ? 0 : -1))
                if (r4 < 0) goto L_0x0104
                r20 = 1
            L_0x00f0:
                if (r20 == 0) goto L_0x00f4
                int r31 = r31 + 1
            L_0x00f4:
                r7.recycle()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                goto L_0x0046
            L_0x00f9:
                r25 = move-exception
                java.lang.String r4 = "index.LauncherProvider"
                java.lang.String r5 = "Got exception parsing favorites."
                r0 = r25
                android.util.Log.w(r4, r5, r0)
                goto L_0x00b4
            L_0x0104:
                r20 = 0
                goto L_0x00f0
            L_0x0107:
                java.lang.String r4 = "search"
                r0 = r34
                boolean r4 = r4.equals(r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                if (r4 == 0) goto L_0x011a
                r0 = r40
                r1 = r41
                boolean r20 = r0.addSearchWidget(r1, r6)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                goto L_0x00f0
            L_0x011a:
                java.lang.String r4 = "clock"
                r0 = r34
                boolean r4 = r4.equals(r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                if (r4 == 0) goto L_0x012d
                r0 = r40
                r1 = r41
                boolean r20 = r0.addClockWidget(r1, r6)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                goto L_0x00f0
            L_0x012d:
                java.lang.String r4 = "appwidget"
                r0 = r34
                boolean r4 = r4.equals(r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                if (r4 == 0) goto L_0x0145
                r10 = r40
                r14 = r41
                r15 = r6
                r16 = r7
                r17 = r8
                boolean r20 = r10.addAppWidget(r11, r12, r13, r14, r15, r16, r17)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                goto L_0x00f0
            L_0x0145:
                java.lang.String r4 = "shortcut"
                r0 = r34
                boolean r4 = r4.equals(r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                if (r4 == 0) goto L_0x0163
                r0 = r40
                r1 = r41
                long r32 = r0.addUriShortcut(r1, r6, r7)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r4 = 0
                int r4 = (r32 > r4 ? 1 : (r32 == r4 ? 0 : -1))
                if (r4 < 0) goto L_0x0160
                r20 = 1
            L_0x015f:
                goto L_0x00f0
            L_0x0160:
                r20 = 0
                goto L_0x015f
            L_0x0163:
                java.lang.String r4 = "folder"
                r0 = r34
                boolean r4 = r4.equals(r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                if (r4 == 0) goto L_0x00f0
                r4 = 9
                r5 = -1
                int r37 = r7.getResourceId(r4, r5)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r4 = -1
                r0 = r37
                if (r0 == r4) goto L_0x0210
                r0 = r40
                android.content.Context r4 = r0.mContext     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                android.content.res.Resources r4 = r4.getResources()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r0 = r37
                java.lang.String r36 = r4.getString(r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
            L_0x0187:
                java.lang.String r4 = "title"
                r0 = r36
                r6.put(r4, r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r0 = r40
                r1 = r41
                long r28 = r0.addFolder(r1, r6)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r4 = 0
                int r4 = (r28 > r4 ? 1 : (r28 == r4 ? 0 : -1))
                if (r4 < 0) goto L_0x0221
                r20 = 1
            L_0x019e:
                java.util.ArrayList r27 = new java.util.ArrayList     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r27.<init>()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                int r26 = r11.getDepth()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
            L_0x01a7:
                int r13 = r11.next()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r4 = 3
                if (r13 != r4) goto L_0x01b6
                int r4 = r11.getDepth()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r0 = r26
                if (r4 <= r0) goto L_0x0257
            L_0x01b6:
                r4 = 2
                if (r13 != r4) goto L_0x01a7
                java.lang.String r30 = r11.getName()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r0 = r40
                android.content.Context r4 = r0.mContext     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                int[] r5 = com.szchoiceway.index.R.styleable.Favorite     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                android.content.res.TypedArray r17 = r4.obtainStyledAttributes(r12, r5)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r6.clear()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                java.lang.String r4 = "container"
                java.lang.Long r5 = java.lang.Long.valueOf(r28)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r6.put(r4, r5)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                java.lang.String r4 = "favorite"
                r0 = r30
                boolean r4 = r4.equals(r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                if (r4 == 0) goto L_0x0225
                r4 = 0
                int r4 = (r28 > r4 ? 1 : (r28 == r4 ? 0 : -1))
                if (r4 < 0) goto L_0x0225
                r14 = r40
                r15 = r41
                r16 = r6
                r18 = r8
                r19 = r9
                long r32 = r14.addAppShortcut(r15, r16, r17, r18, r19)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r4 = 0
                int r4 = (r32 > r4 ? 1 : (r32 == r4 ? 0 : -1))
                if (r4 < 0) goto L_0x0200
                java.lang.Long r4 = java.lang.Long.valueOf(r32)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r0 = r27
                r0.add(r4)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
            L_0x0200:
                r17.recycle()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                goto L_0x01a7
            L_0x0204:
                r25 = move-exception
                java.lang.String r4 = "index.LauncherProvider"
                java.lang.String r5 = "Got exception parsing favorites."
                r0 = r25
                android.util.Log.w(r4, r5, r0)
                goto L_0x00b4
            L_0x0210:
                r0 = r40
                android.content.Context r4 = r0.mContext     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                android.content.res.Resources r4 = r4.getResources()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r5 = 2131099667(0x7f060013, float:1.7811694E38)
                java.lang.String r36 = r4.getString(r5)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                goto L_0x0187
            L_0x0221:
                r20 = 0
                goto L_0x019e
            L_0x0225:
                java.lang.String r4 = "shortcut"
                r0 = r30
                boolean r4 = r4.equals(r0)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                if (r4 == 0) goto L_0x024f
                r4 = 0
                int r4 = (r28 > r4 ? 1 : (r28 == r4 ? 0 : -1))
                if (r4 < 0) goto L_0x024f
                r0 = r40
                r1 = r41
                r2 = r17
                long r32 = r0.addUriShortcut(r1, r6, r2)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r4 = 0
                int r4 = (r32 > r4 ? 1 : (r32 == r4 ? 0 : -1))
                if (r4 < 0) goto L_0x0200
                java.lang.Long r4 = java.lang.Long.valueOf(r32)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r0 = r27
                r0.add(r4)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                goto L_0x0200
            L_0x024f:
                java.lang.RuntimeException r4 = new java.lang.RuntimeException     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                java.lang.String r5 = "Folders can contain only shortcuts"
                r4.<init>(r5)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                throw r4     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
            L_0x0257:
                int r4 = r27.size()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r5 = 2
                if (r4 >= r5) goto L_0x00f0
                r4 = 0
                int r4 = (r28 > r4 ? 1 : (r28 == r4 ? 0 : -1))
                if (r4 < 0) goto L_0x00f0
                r0 = r41
                r1 = r28
                com.szchoiceway.index.LauncherProvider.deleteId(r0, r1)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                int r4 = r27.size()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                if (r4 <= 0) goto L_0x0283
                r4 = 0
                r0 = r27
                java.lang.Object r4 = r0.get(r4)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                java.lang.Long r4 = (java.lang.Long) r4     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                long r4 = r4.longValue()     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
                r0 = r41
                com.szchoiceway.index.LauncherProvider.deleteId(r0, r4)     // Catch:{ XmlPullParserException -> 0x00aa, IOException -> 0x00f9, RuntimeException -> 0x0204 }
            L_0x0283:
                r20 = 0
                goto L_0x00f0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.LauncherProvider.DatabaseHelper.loadFavorites(android.database.sqlite.SQLiteDatabase, int):int");
        }

        private long addAppShortcut(SQLiteDatabase db, ContentValues values, TypedArray a, PackageManager packageManager, Intent intent) {
            ComponentName cn;
            ActivityInfo info;
            long id = -1;
            String packageName = a.getString(1);
            String className = a.getString(0);
            try {
                cn = new ComponentName(packageName, className);
                info = packageManager.getActivityInfo(cn, 0);
            } catch (PackageManager.NameNotFoundException e) {
                cn = new ComponentName(packageManager.currentToCanonicalPackageNames(new String[]{packageName})[0], className);
                info = packageManager.getActivityInfo(cn, 0);
            }
            try {
                id = generateNewId();
                intent.setComponent(cn);
                intent.setFlags(270532608);
                values.put(LauncherSettings.BaseLauncherColumns.INTENT, intent.toUri(0));
                values.put(LauncherSettings.BaseLauncherColumns.TITLE, info.loadLabel(packageManager).toString());
                values.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, 0);
                values.put("spanX", 1);
                values.put("spanY", 1);
                values.put("_id", Long.valueOf(generateNewId()));
                if (LauncherProvider.dbInsertAndCheck(this, db, TAG_FAVORITES, (String) null, values) < 0) {
                    return -1;
                }
            } catch (PackageManager.NameNotFoundException e2) {
                Log.w(LauncherProvider.TAG, "Unable to add favorite: " + packageName + "/" + className, e2);
            }
            return id;
        }

        private long addFolder(SQLiteDatabase db, ContentValues values) {
            values.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, 2);
            values.put("spanX", 1);
            values.put("spanY", 1);
            long id = generateNewId();
            values.put("_id", Long.valueOf(id));
            if (LauncherProvider.dbInsertAndCheck(this, db, TAG_FAVORITES, (String) null, values) <= 0) {
                return -1;
            }
            return id;
        }

        private ComponentName getSearchWidgetProvider() {
            ComponentName searchComponent = ((SearchManager) this.mContext.getSystemService(TAG_SEARCH)).getGlobalSearchActivity();
            if (searchComponent == null) {
                return null;
            }
            return getProviderInPackage(searchComponent.getPackageName());
        }

        private ComponentName getProviderInPackage(String packageName) {
            List<AppWidgetProviderInfo> providers = AppWidgetManager.getInstance(this.mContext).getInstalledProviders();
            if (providers == null) {
                return null;
            }
            int providerCount = providers.size();
            for (int i = 0; i < providerCount; i++) {
                ComponentName provider = providers.get(i).provider;
                if (provider != null && provider.getPackageName().equals(packageName)) {
                    return provider;
                }
            }
            return null;
        }

        private boolean addSearchWidget(SQLiteDatabase db, ContentValues values) {
            return addAppWidget(db, values, getSearchWidgetProvider(), 4, 1, (Bundle) null);
        }

        private boolean addClockWidget(SQLiteDatabase db, ContentValues values) {
            return addAppWidget(db, values, new ComponentName("com.android.alarmclock", "com.android.alarmclock.AnalogAppWidgetProvider"), 2, 2, (Bundle) null);
        }

        private boolean addAppWidget(XmlResourceParser parser, AttributeSet attrs, int type, SQLiteDatabase db, ContentValues values, TypedArray a, PackageManager packageManager) throws XmlPullParserException, IOException {
            String packageName = a.getString(1);
            String className = a.getString(0);
            if (packageName == null || className == null) {
                return false;
            }
            boolean hasPackage = true;
            ComponentName cn = new ComponentName(packageName, className);
            try {
                packageManager.getReceiverInfo(cn, 0);
            } catch (Exception e) {
                cn = new ComponentName(packageManager.currentToCanonicalPackageNames(new String[]{packageName})[0], className);
                try {
                    packageManager.getReceiverInfo(cn, 0);
                } catch (Exception e2) {
                    hasPackage = false;
                }
            }
            if (!hasPackage) {
                return false;
            }
            int spanX = a.getInt(6, 0);
            int spanY = a.getInt(7, 0);
            Bundle extras = new Bundle();
            int widgetDepth = parser.getDepth();
            while (true) {
                int type2 = parser.next();
                if (type2 == 3 && parser.getDepth() <= widgetDepth) {
                    return addAppWidget(db, values, cn, spanX, spanY, extras);
                }
                if (type2 == 2) {
                    TypedArray ar = this.mContext.obtainStyledAttributes(attrs, R.styleable.Extra);
                    if (TAG_EXTRA.equals(parser.getName())) {
                        String key = ar.getString(0);
                        String value = ar.getString(1);
                        if (key != null && value != null) {
                            extras.putString(key, value);
                            ar.recycle();
                        }
                    } else {
                        throw new RuntimeException("Widgets can contain only extras");
                    }
                }
            }
            throw new RuntimeException("Widget extras must have a key and value");
        }

        private boolean addAppWidget(SQLiteDatabase db, ContentValues values, ComponentName cn, int spanX, int spanY, Bundle extras) {
            boolean allocatedAppWidgets = false;
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.mContext);
            try {
                int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
                values.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, 4);
                values.put("spanX", Integer.valueOf(spanX));
                values.put("spanY", Integer.valueOf(spanY));
                values.put("appWidgetId", Integer.valueOf(appWidgetId));
                values.put("_id", Long.valueOf(generateNewId()));
                long unused = LauncherProvider.dbInsertAndCheck(this, db, TAG_FAVORITES, (String) null, values);
                allocatedAppWidgets = true;
                appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, cn);
                if (extras != null && !extras.isEmpty()) {
                    Intent intent = new Intent(LauncherProvider.ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE);
                    intent.setComponent(cn);
                    intent.putExtras(extras);
                    intent.putExtra("appWidgetId", appWidgetId);
                    this.mContext.sendBroadcast(intent);
                }
            } catch (RuntimeException ex) {
                Log.e(LauncherProvider.TAG, "Problem allocating appWidgetId", ex);
            }
            return allocatedAppWidgets;
        }

        private long addUriShortcut(SQLiteDatabase db, ContentValues values, TypedArray a) {
            Resources r = this.mContext.getResources();
            int iconResId = a.getResourceId(8, 0);
            int titleResId = a.getResourceId(9, 0);
            String uri = null;
            try {
                uri = a.getString(10);
                Intent intent = Intent.parseUri(uri, 0);
                if (iconResId == 0 || titleResId == 0) {
                    Log.w(LauncherProvider.TAG, "Shortcut is missing title or icon resource ID");
                    return -1;
                }
                long id = generateNewId();
                intent.setFlags(268435456);
                values.put(LauncherSettings.BaseLauncherColumns.INTENT, intent.toUri(0));
                values.put(LauncherSettings.BaseLauncherColumns.TITLE, r.getString(titleResId));
                values.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, 1);
                values.put("spanX", 1);
                values.put("spanY", 1);
                values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, 0);
                values.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, this.mContext.getPackageName());
                values.put("iconResource", r.getResourceName(iconResId));
                values.put("_id", Long.valueOf(id));
                if (LauncherProvider.dbInsertAndCheck(this, db, TAG_FAVORITES, (String) null, values) < 0) {
                    return -1;
                }
                return id;
            } catch (URISyntaxException e) {
                Log.w(LauncherProvider.TAG, "Shortcut has malformed uri: " + uri);
                return -1;
            }
        }
    }

    static String buildOrWhereString(String column, int[] values) {
        StringBuilder selectWhere = new StringBuilder();
        for (int i = values.length - 1; i >= 0; i--) {
            selectWhere.append(column).append("=").append(values[i]);
            if (i > 0) {
                selectWhere.append(" OR ");
            }
        }
        return selectWhere.toString();
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
            } else if (!TextUtils.isEmpty(where2)) {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            } else {
                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);
                this.args = null;
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

    public static class IniFile {
        private String charSet = HTTP.UTF_8;
        private File file = null;
        private String line_separator = null;
        private Map<String, Section> sections = new LinkedHashMap();

        public class Section {
            /* access modifiers changed from: private */
            public String name;
            private Map<String, Object> values = new LinkedHashMap();

            public Section() {
            }

            public String getName() {
                return this.name;
            }

            public void setName(String name2) {
                this.name = name2;
            }

            public void set(String key, Object value) {
                this.values.put(key, value);
            }

            public Object get(String key) {
                return this.values.get(key);
            }

            public Map<String, Object> getValues() {
                return this.values;
            }
        }

        public void setLineSeparator(String line_separator2) {
            this.line_separator = line_separator2;
        }

        public void setCharSet(String charSet2) {
            this.charSet = charSet2;
        }

        public void set(String section, String key, Object value) {
            Section sectionObject = this.sections.get(section);
            if (sectionObject == null) {
                sectionObject = new Section();
            }
            String unused = sectionObject.name = section;
            sectionObject.set(key, value);
            this.sections.put(section, sectionObject);
        }

        public Section get(String section) {
            return this.sections.get(section);
        }

        public Object get(String section, String key) {
            return get(section, key, (String) null);
        }

        public Object get(String section, String key, String defaultValue) {
            Section sectionObject = this.sections.get(section);
            if (sectionObject == null) {
                return null;
            }
            Object value = sectionObject.get(key);
            if (value == null || value.toString().trim().equals("")) {
                return defaultValue;
            }
            return value;
        }

        public void remove(String section) {
            this.sections.remove(section);
        }

        public void remove(String section, String key) {
            Section sectionObject = this.sections.get(section);
            if (sectionObject != null) {
                sectionObject.getValues().remove(key);
            }
        }

        public IniFile() {
        }

        public IniFile(File file2) {
            this.file = file2;
            initFromFile(file2);
        }

        public IniFile(InputStream inputStream) {
            initFromInputStream(inputStream);
        }

        public void load(File file2) {
            this.file = file2;
            initFromFile(file2);
        }

        public void load(InputStream inputStream) {
            initFromInputStream(inputStream);
        }

        public void save(OutputStream outputStream) {
            try {
                saveConfig(new BufferedWriter(new OutputStreamWriter(outputStream, this.charSet)));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        public void save(File file2) {
            try {
                saveConfig(new BufferedWriter(new FileWriter(file2)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void save() {
            save(this.file);
        }

        private void initFromInputStream(InputStream inputStream) {
            try {
                toIniFile(new BufferedReader(new InputStreamReader(inputStream, this.charSet)));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        private void initFromFile(File file2) {
            try {
                toIniFile(new BufferedReader(new FileReader(file2)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void toIniFile(BufferedReader bufferedReader) {
            Pattern p = Pattern.compile("^\\[.*\\]$");
            Section section = null;
            while (true) {
                try {
                    String strLine = bufferedReader.readLine();
                    if (strLine == null) {
                        bufferedReader.close();
                        Section section2 = section;
                        return;
                    } else if (p.matcher(strLine).matches()) {
                        String strLine2 = strLine.trim();
                        Section section3 = new Section();
                        try {
                            String unused = section3.name = strLine2.substring(1, strLine2.length() - 1);
                            this.sections.put(section3.name, section3);
                            section = section3;
                        } catch (IOException e) {
                            e = e;
                            e.printStackTrace();
                            return;
                        }
                    } else {
                        String[] keyValue = strLine.split("=");
                        if (keyValue.length == 2) {
                            section.set(keyValue[0], keyValue[1]);
                        }
                    }
                } catch (IOException e2) {
                    e = e2;
                    Section section4 = section;
                    e.printStackTrace();
                    return;
                }
            }
        }

        private void saveConfig(BufferedWriter bufferedWriter) {
            boolean line_spe = false;
            try {
                if (this.line_separator == null || this.line_separator.trim().equals("")) {
                    line_spe = true;
                }
                for (Section section : this.sections.values()) {
                    bufferedWriter.write("[" + section.getName() + "]");
                    if (line_spe) {
                        if (this.line_separator != null) {
                            bufferedWriter.write(this.line_separator);
                        } else {
                            bufferedWriter.newLine();
                        }
                    }
                    for (Map.Entry<String, Object> entry : section.getValues().entrySet()) {
                        bufferedWriter.write(entry.getKey());
                        bufferedWriter.write("=");
                        bufferedWriter.write(entry.getValue().toString());
                        if (line_spe) {
                            if (this.line_separator != null) {
                                bufferedWriter.write(this.line_separator);
                            } else {
                                bufferedWriter.newLine();
                            }
                        }
                    }
                }
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
