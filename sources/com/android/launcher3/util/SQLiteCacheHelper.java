package com.android.launcher3.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.util.Log;

public abstract class SQLiteCacheHelper {
    private static final boolean NO_ICON_CACHE = false;
    private static final String TAG = "SQLiteCacheHelper";
    private boolean mIgnoreWrites;
    private final MySQLiteOpenHelper mOpenHelper;
    /* access modifiers changed from: private */
    public final String mTableName;

    /* access modifiers changed from: protected */
    public abstract void onCreateTable(SQLiteDatabase sQLiteDatabase);

    public SQLiteCacheHelper(Context context, String name, int version, String tableName) {
        name = NO_ICON_CACHE ? null : name;
        this.mTableName = tableName;
        this.mOpenHelper = new MySQLiteOpenHelper(context, name, version);
        this.mIgnoreWrites = false;
    }

    public void delete(String whereClause, String[] whereArgs) {
        if (!this.mIgnoreWrites) {
            try {
                this.mOpenHelper.getWritableDatabase().delete(this.mTableName, whereClause, whereArgs);
            } catch (SQLiteFullException e) {
                onDiskFull(e);
            } catch (SQLiteException e2) {
                Log.d(TAG, "Ignoring sqlite exception", e2);
            }
        }
    }

    public void insertOrReplace(ContentValues values) {
        if (!this.mIgnoreWrites) {
            try {
                this.mOpenHelper.getWritableDatabase().insertWithOnConflict(this.mTableName, (String) null, values, 5);
            } catch (SQLiteFullException e) {
                onDiskFull(e);
            } catch (SQLiteException e2) {
                Log.d(TAG, "Ignoring sqlite exception", e2);
            }
        }
    }

    private void onDiskFull(SQLiteFullException e) {
        Log.e(TAG, "Disk full, all write operations will be ignored", e);
        this.mIgnoreWrites = true;
    }

    public Cursor query(String[] columns, String selection, String[] selectionArgs) {
        return this.mOpenHelper.getReadableDatabase().query(this.mTableName, columns, selection, selectionArgs, (String) null, (String) null, (String) null);
    }

    public void clear() {
        this.mOpenHelper.clearDB(this.mOpenHelper.getWritableDatabase());
    }

    private class MySQLiteOpenHelper extends NoLocaleSQLiteHelper {
        public MySQLiteOpenHelper(Context context, String name, int version) {
            super(context, name, version);
        }

        public void onCreate(SQLiteDatabase db) {
            SQLiteCacheHelper.this.onCreateTable(db);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion != newVersion) {
                clearDB(db);
            }
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion != newVersion) {
                clearDB(db);
            }
        }

        /* access modifiers changed from: private */
        public void clearDB(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + SQLiteCacheHelper.this.mTableName);
            onCreate(db);
        }
    }
}
